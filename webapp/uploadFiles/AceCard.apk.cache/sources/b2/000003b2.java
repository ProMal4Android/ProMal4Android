package net.freehaven.tor.control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CancellationException;
import org.spongycastle.asn1.eac.EACTags;
import org.torproject.android.TorConstants;

/* loaded from: classes.dex */
public class TorControlConnection implements TorControlCommands {
    protected PrintWriter debugOutput;
    protected EventHandler handler;
    protected BufferedReader input;
    protected Writer output;
    protected ControlParseThread thread;
    protected LinkedList<Waiter> waiters;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Waiter {
        List<ReplyLine> response;

        Waiter() {
        }

        public synchronized List<ReplyLine> getResponse() {
            while (this.response == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new CancellationException("Please don't interrupt library calls.");
                }
            }
            return this.response;
        }

        public synchronized void setResponse(List<ReplyLine> list) {
            this.response = list;
            notifyAll();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ReplyLine {
        public String msg;
        public String rest;
        public String status;

        ReplyLine(String str, String str2, String str3) {
            this.status = str;
            this.msg = str2;
            this.rest = str3;
        }
    }

    public static TorControlConnection getConnection(Socket socket) throws IOException {
        return new TorControlConnection(socket);
    }

    public TorControlConnection(Socket socket) throws IOException {
        this(socket.getInputStream(), socket.getOutputStream());
    }

    public TorControlConnection(InputStream inputStream, OutputStream outputStream) {
        this(new InputStreamReader(inputStream), new OutputStreamWriter(outputStream));
    }

    public TorControlConnection(Reader reader, Writer writer) {
        this.output = writer;
        if (reader instanceof BufferedReader) {
            this.input = (BufferedReader) reader;
        } else {
            this.input = new BufferedReader(reader);
        }
        this.waiters = new LinkedList<>();
    }

    protected final void writeEscaped(String str) throws IOException {
        String str2;
        StringTokenizer stringTokenizer = new StringTokenizer(str, TorConstants.NEWLINE);
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            if (nextToken.startsWith(".")) {
                nextToken = "." + nextToken;
            }
            if (nextToken.endsWith("\r")) {
                str2 = nextToken + TorConstants.NEWLINE;
            } else {
                str2 = nextToken + "\r\n";
            }
            if (this.debugOutput != null) {
                this.debugOutput.print(">> " + str2);
            }
            this.output.write(str2);
        }
        this.output.write(".\r\n");
        if (this.debugOutput != null) {
            this.debugOutput.print(">> .\n");
        }
    }

    protected static final String quote(String str) {
        StringBuffer stringBuffer = new StringBuffer("\"");
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\n':
                case '\r':
                case '\"':
                case EACTags.TAG_LIST /* 92 */:
                    stringBuffer.append('\\');
                    break;
            }
            stringBuffer.append(charAt);
        }
        stringBuffer.append('\"');
        return stringBuffer.toString();
    }

    protected final ArrayList<ReplyLine> readReply() throws IOException {
        char charAt;
        ArrayList<ReplyLine> arrayList = new ArrayList<>();
        do {
            String readLine = this.input.readLine();
            if (readLine == null) {
                if (arrayList.isEmpty()) {
                    return arrayList;
                }
                throw new TorControlSyntaxError("Connection to Tor  broke down while receiving reply!");
            }
            if (this.debugOutput != null) {
                this.debugOutput.println("<< " + readLine);
            }
            if (readLine.length() < 4) {
                throw new TorControlSyntaxError("Line (\"" + readLine + "\") too short");
            }
            String substring = readLine.substring(0, 3);
            charAt = readLine.charAt(3);
            String substring2 = readLine.substring(4);
            String str = null;
            if (charAt == '+') {
                StringBuffer stringBuffer = new StringBuffer();
                while (true) {
                    String readLine2 = this.input.readLine();
                    if (this.debugOutput != null) {
                        this.debugOutput.print("<< " + readLine2);
                    }
                    if (readLine2.equals(".")) {
                        break;
                    }
                    if (readLine2.startsWith(".")) {
                        readLine2 = readLine2.substring(1);
                    }
                    stringBuffer.append(readLine2).append('\n');
                }
                str = stringBuffer.toString();
            }
            arrayList.add(new ReplyLine(substring, substring2, str));
        } while (charAt != ' ');
        return arrayList;
    }

    protected synchronized List<ReplyLine> sendAndWaitForResponse(String str, String str2) throws IOException {
        List<ReplyLine> response;
        checkThread();
        Waiter waiter = new Waiter();
        if (this.debugOutput != null) {
            this.debugOutput.print(">> " + str);
        }
        synchronized (this.waiters) {
            this.output.write(str);
            if (str2 != null) {
                writeEscaped(str2);
            }
            this.output.flush();
            this.waiters.addLast(waiter);
        }
        response = waiter.getResponse();
        for (ReplyLine replyLine : response) {
            if (!replyLine.status.startsWith("2")) {
                throw new TorControlError("Error reply: " + replyLine.msg);
            }
        }
        return response;
    }

    protected void handleEvent(ArrayList<ReplyLine> arrayList) {
        if (this.handler != null) {
            Iterator<ReplyLine> it = arrayList.iterator();
            while (it.hasNext()) {
                ReplyLine next = it.next();
                int indexOf = next.msg.indexOf(32);
                String upperCase = next.msg.substring(0, indexOf).toUpperCase();
                String substring = next.msg.substring(indexOf + 1);
                if (upperCase.equals("CIRC")) {
                    List<String> splitStr = Bytes.splitStr(null, substring);
                    this.handler.circuitStatus(splitStr.get(1), splitStr.get(0), (splitStr.get(1).equals("LAUNCHED") || splitStr.size() < 2) ? "" : splitStr.get(2));
                } else if (upperCase.equals("STREAM")) {
                    List<String> splitStr2 = Bytes.splitStr(null, substring);
                    this.handler.streamStatus(splitStr2.get(1), splitStr2.get(0), splitStr2.get(3));
                } else if (upperCase.equals("ORCONN")) {
                    List<String> splitStr3 = Bytes.splitStr(null, substring);
                    this.handler.orConnStatus(splitStr3.get(1), splitStr3.get(0));
                } else if (upperCase.equals("BW")) {
                    List<String> splitStr4 = Bytes.splitStr(null, substring);
                    this.handler.bandwidthUsed(Integer.parseInt(splitStr4.get(0)), Integer.parseInt(splitStr4.get(1)));
                } else if (upperCase.equals("NEWDESC")) {
                    this.handler.newDescriptors(Bytes.splitStr(null, substring));
                } else if (upperCase.equals("DEBUG") || upperCase.equals("INFO") || upperCase.equals("NOTICE") || upperCase.equals("WARN") || upperCase.equals("ERR")) {
                    this.handler.message(upperCase, substring);
                } else {
                    this.handler.unrecognized(upperCase, substring);
                }
            }
        }
    }

    public void setDebugging(PrintWriter printWriter) {
        this.debugOutput = printWriter;
    }

    public void setDebugging(PrintStream printStream) {
        this.debugOutput = new PrintWriter((OutputStream) printStream, true);
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.handler = eventHandler;
    }

    public Thread launchThread(boolean z) {
        ControlParseThread controlParseThread = new ControlParseThread();
        if (z) {
            controlParseThread.setDaemon(true);
        }
        controlParseThread.start();
        this.thread = controlParseThread;
        return controlParseThread;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class ControlParseThread extends Thread {
        boolean stopped = false;

        protected ControlParseThread() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                TorControlConnection.this.react();
            } catch (SocketException e) {
                if (!this.stopped) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
        }

        public void stopListening() {
            this.stopped = true;
        }
    }

    protected final void checkThread() {
        if (this.thread == null) {
            launchThread(true);
        }
    }

    protected void react() throws IOException {
        Waiter removeFirst;
        while (true) {
            ArrayList<ReplyLine> readReply = readReply();
            if (readReply.isEmpty()) {
                return;
            }
            if (readReply.get(0).status.startsWith("6")) {
                handleEvent(readReply);
            } else {
                synchronized (this.waiters) {
                    removeFirst = this.waiters.removeFirst();
                }
                removeFirst.setResponse(readReply);
            }
        }
    }

    public void setConf(String str, String str2) throws IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str + " " + str2);
        setConf(arrayList);
    }

    public void setConf(Map<String, String> map) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(entry.getKey() + " " + entry.getValue() + TorConstants.NEWLINE);
        }
        setConf(arrayList);
    }

    public void setConf(Collection<String> collection) throws IOException {
        if (collection.size() != 0) {
            StringBuffer stringBuffer = new StringBuffer("SETCONF");
            for (String str : collection) {
                int indexOf = str.indexOf(32);
                if (indexOf == -1) {
                    stringBuffer.append(" ").append(str);
                }
                stringBuffer.append(" ").append(str.substring(0, indexOf)).append("=").append(quote(str.substring(indexOf + 1)));
            }
            stringBuffer.append("\r\n");
            sendAndWaitForResponse(stringBuffer.toString(), null);
        }
    }

    public void resetConf(Collection<String> collection) throws IOException {
        if (collection.size() != 0) {
            StringBuffer stringBuffer = new StringBuffer("RESETCONF");
            for (String str : collection) {
                stringBuffer.append(" ").append(str);
            }
            stringBuffer.append("\r\n");
            sendAndWaitForResponse(stringBuffer.toString(), null);
        }
    }

    public List<ConfigEntry> getConf(String str) throws IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        return getConf(arrayList);
    }

    public List<ConfigEntry> getConf(Collection<String> collection) throws IOException {
        StringBuffer stringBuffer = new StringBuffer("GETCONF");
        for (String str : collection) {
            stringBuffer.append(" ").append(str);
        }
        stringBuffer.append("\r\n");
        List<ReplyLine> sendAndWaitForResponse = sendAndWaitForResponse(stringBuffer.toString(), null);
        ArrayList arrayList = new ArrayList();
        for (ReplyLine replyLine : sendAndWaitForResponse) {
            String str2 = replyLine.msg;
            int indexOf = str2.indexOf(61);
            if (indexOf >= 0) {
                arrayList.add(new ConfigEntry(str2.substring(0, indexOf), str2.substring(indexOf + 1)));
            } else {
                arrayList.add(new ConfigEntry(str2));
            }
        }
        return arrayList;
    }

    public void setEvents(List<String> list) throws IOException {
        StringBuffer stringBuffer = new StringBuffer("SETEVENTS");
        for (String str : list) {
            stringBuffer.append(" ").append(str);
        }
        stringBuffer.append("\r\n");
        sendAndWaitForResponse(stringBuffer.toString(), null);
    }

    public void authenticate(byte[] bArr) throws IOException {
        sendAndWaitForResponse("AUTHENTICATE " + Bytes.hex(bArr) + "\r\n", null);
    }

    public void saveConf() throws IOException {
        sendAndWaitForResponse("SAVECONF\r\n", null);
    }

    public void signal(String str) throws IOException {
        sendAndWaitForResponse("SIGNAL " + str + "\r\n", null);
    }

    public void shutdownTor(String str) throws IOException {
        String str2 = "SIGNAL " + str + "\r\n";
        Waiter waiter = new Waiter();
        if (this.debugOutput != null) {
            this.debugOutput.print(">> " + str2);
        }
        if (this.thread != null) {
            this.thread.stopListening();
        }
        synchronized (this.waiters) {
            this.output.write(str2);
            this.output.flush();
            this.waiters.addLast(waiter);
        }
    }

    public Map<String, String> mapAddresses(Collection<String> collection) throws IOException {
        StringBuffer stringBuffer = new StringBuffer("MAPADDRESS");
        for (String str : collection) {
            int indexOf = str.indexOf(32);
            stringBuffer.append(" ").append(str.substring(0, indexOf)).append("=").append(quote(str.substring(indexOf + 1)));
        }
        stringBuffer.append("\r\n");
        List<ReplyLine> sendAndWaitForResponse = sendAndWaitForResponse(stringBuffer.toString(), null);
        HashMap hashMap = new HashMap();
        for (ReplyLine replyLine : sendAndWaitForResponse) {
            String str2 = replyLine.msg;
            int indexOf2 = str2.indexOf(61);
            hashMap.put(str2.substring(0, indexOf2), str2.substring(indexOf2 + 1));
        }
        return hashMap;
    }

    public Map<String, String> mapAddresses(Map<String, String> map) throws IOException {
        ArrayList arrayList = new ArrayList();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(entry.getKey() + " " + entry.getValue());
        }
        return mapAddresses(arrayList);
    }

    public String mapAddress(String str, String str2) throws IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str + " " + str2 + TorConstants.NEWLINE);
        return mapAddresses(arrayList).get(str);
    }

    public Map<String, String> getInfo(Collection<String> collection) throws IOException {
        String substring;
        StringBuffer stringBuffer = new StringBuffer("GETINFO");
        for (String str : collection) {
            stringBuffer.append(" ").append(str);
        }
        stringBuffer.append("\r\n");
        List<ReplyLine> sendAndWaitForResponse = sendAndWaitForResponse(stringBuffer.toString(), null);
        HashMap hashMap = new HashMap();
        for (ReplyLine replyLine : sendAndWaitForResponse) {
            int indexOf = replyLine.msg.indexOf(61);
            if (indexOf < 0) {
                break;
            }
            String substring2 = replyLine.msg.substring(0, indexOf);
            if (replyLine.rest != null) {
                substring = replyLine.rest;
            } else {
                substring = replyLine.msg.substring(indexOf + 1);
            }
            hashMap.put(substring2, substring);
        }
        return hashMap;
    }

    public String getInfo(String str) throws IOException {
        ArrayList arrayList = new ArrayList();
        arrayList.add(str);
        return getInfo(arrayList).get(str);
    }

    public String extendCircuit(String str, String str2) throws IOException {
        return sendAndWaitForResponse("EXTENDCIRCUIT " + str + " " + str2 + "\r\n", null).get(0).msg;
    }

    public void attachStream(String str, String str2) throws IOException {
        sendAndWaitForResponse("ATTACHSTREAM " + str + " " + str2 + "\r\n", null);
    }

    public String postDescriptor(String str) throws IOException {
        return sendAndWaitForResponse("+POSTDESCRIPTOR\r\n", str).get(0).msg;
    }

    public void redirectStream(String str, String str2) throws IOException {
        sendAndWaitForResponse("REDIRECTSTREAM " + str + " " + str2 + "\r\n", null);
    }

    public void closeStream(String str, byte b) throws IOException {
        sendAndWaitForResponse("CLOSESTREAM " + str + " " + ((int) b) + "\r\n", null);
    }

    public void closeCircuit(String str, boolean z) throws IOException {
        sendAndWaitForResponse("CLOSECIRCUIT " + str + (z ? " IFUNUSED" : "") + "\r\n", null);
    }
}