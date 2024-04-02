package ch.boye.httpclientandroidlib.impl.auth;

import ch.boye.httpclientandroidlib.androidextra.Base64;
import ch.boye.httpclientandroidlib.util.EncodingUtils;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class NTLMEngineImpl implements NTLMEngine {
    static final String DEFAULT_CHARSET = "ASCII";
    protected static final int FLAG_DOMAIN_PRESENT = 4096;
    protected static final int FLAG_REQUEST_128BIT_KEY_EXCH = 536870912;
    protected static final int FLAG_REQUEST_56BIT_ENCRYPTION = Integer.MIN_VALUE;
    protected static final int FLAG_REQUEST_ALWAYS_SIGN = 32768;
    protected static final int FLAG_REQUEST_EXPLICIT_KEY_EXCH = 1073741824;
    protected static final int FLAG_REQUEST_LAN_MANAGER_KEY = 128;
    protected static final int FLAG_REQUEST_NTLM2_SESSION = 524288;
    protected static final int FLAG_REQUEST_NTLMv1 = 512;
    protected static final int FLAG_REQUEST_SEAL = 32;
    protected static final int FLAG_REQUEST_SIGN = 16;
    protected static final int FLAG_REQUEST_TARGET = 4;
    protected static final int FLAG_REQUEST_UNICODE_ENCODING = 1;
    protected static final int FLAG_REQUEST_VERSION = 33554432;
    protected static final int FLAG_TARGETINFO_PRESENT = 8388608;
    protected static final int FLAG_WORKSTATION_PRESENT = 8192;
    private static final SecureRandom RND_GEN;
    private static byte[] SIGNATURE;
    private String credentialCharset = "ASCII";

    static /* synthetic */ byte[] access$000() throws NTLMEngineException {
        return makeRandomChallenge();
    }

    static /* synthetic */ byte[] access$100() throws NTLMEngineException {
        return makeSecondaryKey();
    }

    static {
        SecureRandom rnd = null;
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
        }
        RND_GEN = rnd;
        byte[] bytesWithoutNull = EncodingUtils.getBytes("NTLMSSP", "ASCII");
        SIGNATURE = new byte[bytesWithoutNull.length + 1];
        System.arraycopy(bytesWithoutNull, 0, SIGNATURE, 0, bytesWithoutNull.length);
        SIGNATURE[bytesWithoutNull.length] = 0;
    }

    final String getResponseFor(String message, String username, String password, String host, String domain) throws NTLMEngineException {
        if (message == null || message.trim().equals("")) {
            String response = getType1Message(host, domain);
            return response;
        }
        Type2Message t2m = new Type2Message(message);
        String response2 = getType3Message(username, password, host, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
        return response2;
    }

    String getType1Message(String host, String domain) throws NTLMEngineException {
        return new Type1Message(domain, host).getResponse();
    }

    String getType3Message(String user, String password, String host, String domain, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
        return new Type3Message(domain, host, user, password, nonce, type2Flags, target, targetInformation).getResponse();
    }

    String getCredentialCharset() {
        return this.credentialCharset;
    }

    void setCredentialCharset(String credentialCharset) {
        this.credentialCharset = credentialCharset;
    }

    private static String stripDotSuffix(String value) {
        int index = value.indexOf(".");
        if (index != -1) {
            return value.substring(0, index);
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String convertHost(String host) {
        return stripDotSuffix(host);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String convertDomain(String domain) {
        return stripDotSuffix(domain);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int readULong(byte[] src, int index) throws NTLMEngineException {
        if (src.length < index + 4) {
            throw new NTLMEngineException("NTLM authentication - buffer too small for DWORD");
        }
        return (src[index] & 255) | ((src[index + 1] & 255) << 8) | ((src[index + 2] & 255) << 16) | ((src[index + 3] & 255) << 24);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int readUShort(byte[] src, int index) throws NTLMEngineException {
        if (src.length < index + 2) {
            throw new NTLMEngineException("NTLM authentication - buffer too small for WORD");
        }
        return (src[index] & 255) | ((src[index + 1] & 255) << 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] readSecurityBuffer(byte[] src, int index) throws NTLMEngineException {
        int length = readUShort(src, index);
        int offset = readULong(src, index + 4);
        if (src.length < offset + length) {
            throw new NTLMEngineException("NTLM authentication - buffer too small for data item");
        }
        byte[] buffer = new byte[length];
        System.arraycopy(src, offset, buffer, 0, length);
        return buffer;
    }

    private static byte[] makeRandomChallenge() throws NTLMEngineException {
        if (RND_GEN == null) {
            throw new NTLMEngineException("Random generator not available");
        }
        byte[] rval = new byte[8];
        synchronized (RND_GEN) {
            RND_GEN.nextBytes(rval);
        }
        return rval;
    }

    private static byte[] makeSecondaryKey() throws NTLMEngineException {
        if (RND_GEN == null) {
            throw new NTLMEngineException("Random generator not available");
        }
        byte[] rval = new byte[16];
        synchronized (RND_GEN) {
            RND_GEN.nextBytes(rval);
        }
        return rval;
    }

    /* loaded from: classes.dex */
    protected static class CipherGen {
        protected final byte[] challenge;
        protected byte[] clientChallenge;
        protected byte[] lanManagerSessionKey;
        protected byte[] lm2SessionResponse;
        protected byte[] lmHash;
        protected byte[] lmResponse;
        protected byte[] lmUserSessionKey;
        protected byte[] lmv2Response;
        protected byte[] ntlm2SessionResponse;
        protected byte[] ntlm2SessionResponseUserSessionKey;
        protected byte[] ntlmHash;
        protected byte[] ntlmResponse;
        protected byte[] ntlmUserSessionKey;
        protected byte[] ntlmv2Blob;
        protected byte[] ntlmv2Hash;
        protected byte[] ntlmv2Response;
        protected byte[] ntlmv2UserSessionKey;
        protected final String password;
        protected byte[] secondaryKey;
        protected final String target;
        protected final byte[] targetInformation;
        protected byte[] timestamp;
        protected final String user;

        public CipherGen(String target, String user, String password, byte[] challenge, byte[] targetInformation, byte[] clientChallenge, byte[] secondaryKey, byte[] timestamp) {
            this.lmHash = null;
            this.lmResponse = null;
            this.ntlmHash = null;
            this.ntlmResponse = null;
            this.ntlmv2Hash = null;
            this.lmv2Response = null;
            this.ntlmv2Blob = null;
            this.ntlmv2Response = null;
            this.ntlm2SessionResponse = null;
            this.lm2SessionResponse = null;
            this.lmUserSessionKey = null;
            this.ntlmUserSessionKey = null;
            this.ntlmv2UserSessionKey = null;
            this.ntlm2SessionResponseUserSessionKey = null;
            this.lanManagerSessionKey = null;
            this.target = target;
            this.user = user;
            this.password = password;
            this.challenge = challenge;
            this.targetInformation = targetInformation;
            this.clientChallenge = clientChallenge;
            this.secondaryKey = secondaryKey;
            this.timestamp = timestamp;
        }

        public CipherGen(String target, String user, String password, byte[] challenge, byte[] targetInformation) {
            this(target, user, password, challenge, targetInformation, null, null, null);
        }

        public byte[] getClientChallenge() throws NTLMEngineException {
            if (this.clientChallenge == null) {
                this.clientChallenge = NTLMEngineImpl.access$000();
            }
            return this.clientChallenge;
        }

        public byte[] getSecondaryKey() throws NTLMEngineException {
            if (this.secondaryKey == null) {
                this.secondaryKey = NTLMEngineImpl.access$100();
            }
            return this.secondaryKey;
        }

        public byte[] getLMHash() throws NTLMEngineException {
            if (this.lmHash == null) {
                this.lmHash = NTLMEngineImpl.lmHash(this.password);
            }
            return this.lmHash;
        }

        public byte[] getLMResponse() throws NTLMEngineException {
            if (this.lmResponse == null) {
                this.lmResponse = NTLMEngineImpl.lmResponse(getLMHash(), this.challenge);
            }
            return this.lmResponse;
        }

        public byte[] getNTLMHash() throws NTLMEngineException {
            if (this.ntlmHash == null) {
                this.ntlmHash = NTLMEngineImpl.ntlmHash(this.password);
            }
            return this.ntlmHash;
        }

        public byte[] getNTLMResponse() throws NTLMEngineException {
            if (this.ntlmResponse == null) {
                this.ntlmResponse = NTLMEngineImpl.lmResponse(getNTLMHash(), this.challenge);
            }
            return this.ntlmResponse;
        }

        public byte[] getNTLMv2Hash() throws NTLMEngineException {
            if (this.ntlmv2Hash == null) {
                this.ntlmv2Hash = NTLMEngineImpl.ntlmv2Hash(this.target, this.user, this.password);
            }
            return this.ntlmv2Hash;
        }

        public byte[] getTimestamp() {
            if (this.timestamp == null) {
                long time = System.currentTimeMillis();
                long time2 = (time + 11644473600000L) * 10000;
                this.timestamp = new byte[8];
                for (int i = 0; i < 8; i++) {
                    this.timestamp[i] = (byte) time2;
                    time2 >>>= 8;
                }
            }
            return this.timestamp;
        }

        public byte[] getNTLMv2Blob() throws NTLMEngineException {
            if (this.ntlmv2Blob == null) {
                this.ntlmv2Blob = NTLMEngineImpl.createBlob(getClientChallenge(), this.targetInformation, getTimestamp());
            }
            return this.ntlmv2Blob;
        }

        public byte[] getNTLMv2Response() throws NTLMEngineException {
            if (this.ntlmv2Response == null) {
                this.ntlmv2Response = NTLMEngineImpl.lmv2Response(getNTLMv2Hash(), this.challenge, getNTLMv2Blob());
            }
            return this.ntlmv2Response;
        }

        public byte[] getLMv2Response() throws NTLMEngineException {
            if (this.lmv2Response == null) {
                this.lmv2Response = NTLMEngineImpl.lmv2Response(getNTLMv2Hash(), this.challenge, getClientChallenge());
            }
            return this.lmv2Response;
        }

        public byte[] getNTLM2SessionResponse() throws NTLMEngineException {
            if (this.ntlm2SessionResponse == null) {
                this.ntlm2SessionResponse = NTLMEngineImpl.ntlm2SessionResponse(getNTLMHash(), this.challenge, getClientChallenge());
            }
            return this.ntlm2SessionResponse;
        }

        public byte[] getLM2SessionResponse() throws NTLMEngineException {
            if (this.lm2SessionResponse == null) {
                byte[] clientChallenge = getClientChallenge();
                this.lm2SessionResponse = new byte[24];
                System.arraycopy(clientChallenge, 0, this.lm2SessionResponse, 0, clientChallenge.length);
                Arrays.fill(this.lm2SessionResponse, clientChallenge.length, this.lm2SessionResponse.length, (byte) 0);
            }
            return this.lm2SessionResponse;
        }

        public byte[] getLMUserSessionKey() throws NTLMEngineException {
            if (this.lmUserSessionKey == null) {
                byte[] lmHash = getLMHash();
                this.lmUserSessionKey = new byte[16];
                System.arraycopy(lmHash, 0, this.lmUserSessionKey, 0, 8);
                Arrays.fill(this.lmUserSessionKey, 8, 16, (byte) 0);
            }
            return this.lmUserSessionKey;
        }

        public byte[] getNTLMUserSessionKey() throws NTLMEngineException {
            if (this.ntlmUserSessionKey == null) {
                byte[] ntlmHash = getNTLMHash();
                MD4 md4 = new MD4();
                md4.update(ntlmHash);
                this.ntlmUserSessionKey = md4.getOutput();
            }
            return this.ntlmUserSessionKey;
        }

        public byte[] getNTLMv2UserSessionKey() throws NTLMEngineException {
            if (this.ntlmv2UserSessionKey == null) {
                byte[] ntlmv2Hash = getNTLMv2Hash();
                byte[] ntlmv2Blob = getNTLMv2Blob();
                byte[] temp = new byte[ntlmv2Blob.length + this.challenge.length];
                System.arraycopy(this.challenge, 0, temp, 0, this.challenge.length);
                System.arraycopy(ntlmv2Blob, 0, temp, this.challenge.length, ntlmv2Blob.length);
                byte[] partial = NTLMEngineImpl.hmacMD5(temp, ntlmv2Hash);
                this.ntlmv2UserSessionKey = NTLMEngineImpl.hmacMD5(partial, ntlmv2Hash);
            }
            return this.ntlmv2UserSessionKey;
        }

        public byte[] getNTLM2SessionResponseUserSessionKey() throws NTLMEngineException {
            if (this.ntlm2SessionResponseUserSessionKey == null) {
                byte[] ntlmUserSessionKey = getNTLMUserSessionKey();
                byte[] ntlm2SessionResponseNonce = getLM2SessionResponse();
                byte[] sessionNonce = new byte[this.challenge.length + ntlm2SessionResponseNonce.length];
                System.arraycopy(this.challenge, 0, sessionNonce, 0, this.challenge.length);
                System.arraycopy(ntlm2SessionResponseNonce, 0, sessionNonce, this.challenge.length, ntlm2SessionResponseNonce.length);
                this.ntlm2SessionResponseUserSessionKey = NTLMEngineImpl.hmacMD5(sessionNonce, ntlmUserSessionKey);
            }
            return this.ntlm2SessionResponseUserSessionKey;
        }

        public byte[] getLanManagerSessionKey() throws NTLMEngineException {
            if (this.lanManagerSessionKey == null) {
                byte[] lmHash = getLMHash();
                byte[] lmResponse = getLMResponse();
                try {
                    byte[] keyBytes = new byte[14];
                    System.arraycopy(lmHash, 0, keyBytes, 0, 8);
                    Arrays.fill(keyBytes, 8, keyBytes.length, (byte) -67);
                    Key lowKey = NTLMEngineImpl.createDESKey(keyBytes, 0);
                    Key highKey = NTLMEngineImpl.createDESKey(keyBytes, 7);
                    byte[] truncatedResponse = new byte[8];
                    System.arraycopy(lmResponse, 0, truncatedResponse, 0, truncatedResponse.length);
                    Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
                    des.init(1, lowKey);
                    byte[] lowPart = des.doFinal(truncatedResponse);
                    Cipher des2 = Cipher.getInstance("DES/ECB/NoPadding");
                    des2.init(1, highKey);
                    byte[] highPart = des2.doFinal(truncatedResponse);
                    this.lanManagerSessionKey = new byte[16];
                    System.arraycopy(lowPart, 0, this.lanManagerSessionKey, 0, lowPart.length);
                    System.arraycopy(highPart, 0, this.lanManagerSessionKey, lowPart.length, highPart.length);
                } catch (Exception e) {
                    throw new NTLMEngineException(e.getMessage(), e);
                }
            }
            return this.lanManagerSessionKey;
        }
    }

    static byte[] hmacMD5(byte[] value, byte[] key) throws NTLMEngineException {
        HMACMD5 hmacMD5 = new HMACMD5(key);
        hmacMD5.update(value);
        return hmacMD5.getOutput();
    }

    static byte[] RC4(byte[] value, byte[] key) throws NTLMEngineException {
        try {
            Cipher rc4 = Cipher.getInstance("RC4");
            rc4.init(1, new SecretKeySpec(key, "RC4"));
            return rc4.doFinal(value);
        } catch (Exception e) {
            throw new NTLMEngineException(e.getMessage(), e);
        }
    }

    static byte[] ntlm2SessionResponse(byte[] ntlmHash, byte[] challenge, byte[] clientChallenge) throws NTLMEngineException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(challenge);
            md5.update(clientChallenge);
            byte[] digest = md5.digest();
            byte[] sessionHash = new byte[8];
            System.arraycopy(digest, 0, sessionHash, 0, 8);
            return lmResponse(ntlmHash, sessionHash);
        } catch (Exception e) {
            if (e instanceof NTLMEngineException) {
                throw ((NTLMEngineException) e);
            }
            throw new NTLMEngineException(e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] lmHash(String password) throws NTLMEngineException {
        try {
            byte[] oemPassword = password.toUpperCase(Locale.US).getBytes("US-ASCII");
            int length = Math.min(oemPassword.length, 14);
            byte[] keyBytes = new byte[14];
            System.arraycopy(oemPassword, 0, keyBytes, 0, length);
            Key lowKey = createDESKey(keyBytes, 0);
            Key highKey = createDESKey(keyBytes, 7);
            byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
            Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
            des.init(1, lowKey);
            byte[] lowHash = des.doFinal(magicConstant);
            des.init(1, highKey);
            byte[] highHash = des.doFinal(magicConstant);
            byte[] lmHash = new byte[16];
            System.arraycopy(lowHash, 0, lmHash, 0, 8);
            System.arraycopy(highHash, 0, lmHash, 8, 8);
            return lmHash;
        } catch (Exception e) {
            throw new NTLMEngineException(e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] ntlmHash(String password) throws NTLMEngineException {
        try {
            byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
            MD4 md4 = new MD4();
            md4.update(unicodePassword);
            return md4.getOutput();
        } catch (UnsupportedEncodingException e) {
            throw new NTLMEngineException("Unicode not supported: " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] ntlmv2Hash(String target, String user, String password) throws NTLMEngineException {
        try {
            byte[] ntlmHash = ntlmHash(password);
            HMACMD5 hmacMD5 = new HMACMD5(ntlmHash);
            hmacMD5.update(user.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked"));
            hmacMD5.update(target.getBytes("UnicodeLittleUnmarked"));
            return hmacMD5.getOutput();
        } catch (UnsupportedEncodingException e) {
            throw new NTLMEngineException("Unicode not supported! " + e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] lmResponse(byte[] hash, byte[] challenge) throws NTLMEngineException {
        try {
            byte[] keyBytes = new byte[21];
            System.arraycopy(hash, 0, keyBytes, 0, 16);
            Key lowKey = createDESKey(keyBytes, 0);
            Key middleKey = createDESKey(keyBytes, 7);
            Key highKey = createDESKey(keyBytes, 14);
            Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
            des.init(1, lowKey);
            byte[] lowResponse = des.doFinal(challenge);
            des.init(1, middleKey);
            byte[] middleResponse = des.doFinal(challenge);
            des.init(1, highKey);
            byte[] highResponse = des.doFinal(challenge);
            byte[] lmResponse = new byte[24];
            System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
            System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
            System.arraycopy(highResponse, 0, lmResponse, 16, 8);
            return lmResponse;
        } catch (Exception e) {
            throw new NTLMEngineException(e.getMessage(), e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] lmv2Response(byte[] hash, byte[] challenge, byte[] clientData) throws NTLMEngineException {
        HMACMD5 hmacMD5 = new HMACMD5(hash);
        hmacMD5.update(challenge);
        hmacMD5.update(clientData);
        byte[] mac = hmacMD5.getOutput();
        byte[] lmv2Response = new byte[mac.length + clientData.length];
        System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
        System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
        return lmv2Response;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static byte[] createBlob(byte[] clientChallenge, byte[] targetInformation, byte[] timestamp) {
        byte[] blobSignature = {1, 1, 0, 0};
        byte[] reserved = {0, 0, 0, 0};
        byte[] unknown1 = {0, 0, 0, 0};
        byte[] unknown2 = {0, 0, 0, 0};
        byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + 8 + unknown1.length + targetInformation.length + unknown2.length];
        System.arraycopy(blobSignature, 0, blob, 0, blobSignature.length);
        int offset = 0 + blobSignature.length;
        System.arraycopy(reserved, 0, blob, offset, reserved.length);
        int offset2 = offset + reserved.length;
        System.arraycopy(timestamp, 0, blob, offset2, timestamp.length);
        int offset3 = offset2 + timestamp.length;
        System.arraycopy(clientChallenge, 0, blob, offset3, 8);
        int offset4 = offset3 + 8;
        System.arraycopy(unknown1, 0, blob, offset4, unknown1.length);
        int offset5 = offset4 + unknown1.length;
        System.arraycopy(targetInformation, 0, blob, offset5, targetInformation.length);
        int offset6 = offset5 + targetInformation.length;
        System.arraycopy(unknown2, 0, blob, offset6, unknown2.length);
        int length = offset6 + unknown2.length;
        return blob;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Key createDESKey(byte[] bytes, int offset) {
        byte[] keyBytes = new byte[7];
        System.arraycopy(bytes, offset, keyBytes, 0, 7);
        byte[] material = {keyBytes[0], (byte) ((keyBytes[0] << 7) | ((keyBytes[1] & 255) >>> 1)), (byte) ((keyBytes[1] << 6) | ((keyBytes[2] & 255) >>> 2)), (byte) ((keyBytes[2] << 5) | ((keyBytes[3] & 255) >>> 3)), (byte) ((keyBytes[3] << 4) | ((keyBytes[4] & 255) >>> 4)), (byte) ((keyBytes[4] << 3) | ((keyBytes[5] & 255) >>> 5)), (byte) ((keyBytes[5] << 2) | ((keyBytes[6] & 255) >>> 6)), (byte) (keyBytes[6] << 1)};
        oddParity(material);
        return new SecretKeySpec(material, "DES");
    }

    private static void oddParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            boolean needsParity = ((((((((b >>> 7) ^ (b >>> 6)) ^ (b >>> 5)) ^ (b >>> 4)) ^ (b >>> 3)) ^ (b >>> 2)) ^ (b >>> 1)) & 1) == 0;
            if (needsParity) {
                bytes[i] = (byte) (bytes[i] | 1);
            } else {
                bytes[i] = (byte) (bytes[i] & (-2));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class NTLMMessage {
        private int currentOutputPosition;
        private byte[] messageContents;

        NTLMMessage() {
            this.messageContents = null;
            this.currentOutputPosition = 0;
        }

        NTLMMessage(String messageBody, int expectedType) throws NTLMEngineException {
            this.messageContents = null;
            this.currentOutputPosition = 0;
            this.messageContents = Base64.decode(EncodingUtils.getBytes(messageBody, "ASCII"), 2);
            if (this.messageContents.length < NTLMEngineImpl.SIGNATURE.length) {
                throw new NTLMEngineException("NTLM message decoding error - packet too short");
            }
            for (int i = 0; i < NTLMEngineImpl.SIGNATURE.length; i++) {
                if (this.messageContents[i] != NTLMEngineImpl.SIGNATURE[i]) {
                    throw new NTLMEngineException("NTLM message expected - instead got unrecognized bytes");
                }
            }
            int type = readULong(NTLMEngineImpl.SIGNATURE.length);
            if (type != expectedType) {
                throw new NTLMEngineException("NTLM type " + Integer.toString(expectedType) + " message expected - instead got type " + Integer.toString(type));
            }
            this.currentOutputPosition = this.messageContents.length;
        }

        protected int getPreambleLength() {
            return NTLMEngineImpl.SIGNATURE.length + 4;
        }

        protected int getMessageLength() {
            return this.currentOutputPosition;
        }

        protected byte readByte(int position) throws NTLMEngineException {
            if (this.messageContents.length < position + 1) {
                throw new NTLMEngineException("NTLM: Message too short");
            }
            return this.messageContents[position];
        }

        protected void readBytes(byte[] buffer, int position) throws NTLMEngineException {
            if (this.messageContents.length < buffer.length + position) {
                throw new NTLMEngineException("NTLM: Message too short");
            }
            System.arraycopy(this.messageContents, position, buffer, 0, buffer.length);
        }

        protected int readUShort(int position) throws NTLMEngineException {
            return NTLMEngineImpl.readUShort(this.messageContents, position);
        }

        protected int readULong(int position) throws NTLMEngineException {
            return NTLMEngineImpl.readULong(this.messageContents, position);
        }

        protected byte[] readSecurityBuffer(int position) throws NTLMEngineException {
            return NTLMEngineImpl.readSecurityBuffer(this.messageContents, position);
        }

        protected void prepareResponse(int maxlength, int messageType) {
            this.messageContents = new byte[maxlength];
            this.currentOutputPosition = 0;
            addBytes(NTLMEngineImpl.SIGNATURE);
            addULong(messageType);
        }

        protected void addByte(byte b) {
            this.messageContents[this.currentOutputPosition] = b;
            this.currentOutputPosition++;
        }

        protected void addBytes(byte[] bytes) {
            for (byte b : bytes) {
                this.messageContents[this.currentOutputPosition] = b;
                this.currentOutputPosition++;
            }
        }

        protected void addUShort(int value) {
            addByte((byte) (value & 255));
            addByte((byte) ((value >> 8) & 255));
        }

        protected void addULong(int value) {
            addByte((byte) (value & 255));
            addByte((byte) ((value >> 8) & 255));
            addByte((byte) ((value >> 16) & 255));
            addByte((byte) ((value >> 24) & 255));
        }

        String getResponse() {
            byte[] resp;
            if (this.messageContents.length > this.currentOutputPosition) {
                byte[] tmp = new byte[this.currentOutputPosition];
                for (int i = 0; i < this.currentOutputPosition; i++) {
                    tmp[i] = this.messageContents[i];
                }
                resp = tmp;
            } else {
                resp = this.messageContents;
            }
            return EncodingUtils.getAsciiString(Base64.encode(resp, 2));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Type1Message extends NTLMMessage {
        protected byte[] domainBytes;
        protected byte[] hostBytes;

        Type1Message(String domain, String host) throws NTLMEngineException {
            try {
                String host2 = NTLMEngineImpl.convertHost(host);
                String domain2 = NTLMEngineImpl.convertDomain(domain);
                this.hostBytes = host2.getBytes("UnicodeLittleUnmarked");
                this.domainBytes = domain2.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked");
            } catch (UnsupportedEncodingException e) {
                throw new NTLMEngineException("Unicode unsupported: " + e.getMessage(), e);
            }
        }

        @Override // ch.boye.httpclientandroidlib.impl.auth.NTLMEngineImpl.NTLMMessage
        String getResponse() {
            prepareResponse(40, 1);
            addULong(-502758767);
            addUShort(0);
            addUShort(0);
            addULong(40);
            addUShort(0);
            addUShort(0);
            addULong(40);
            addUShort(261);
            addULong(2600);
            addUShort(3840);
            return super.getResponse();
        }
    }

    /* loaded from: classes.dex */
    static class Type2Message extends NTLMMessage {
        protected byte[] challenge;
        protected int flags;
        protected String target;
        protected byte[] targetInfo;

        Type2Message(String message) throws NTLMEngineException {
            super(message, 2);
            this.challenge = new byte[8];
            readBytes(this.challenge, 24);
            this.flags = readULong(20);
            if ((this.flags & 1) == 0) {
                throw new NTLMEngineException("NTLM type 2 message has flags that make no sense: " + Integer.toString(this.flags));
            }
            this.target = null;
            if (getMessageLength() >= 20) {
                byte[] bytes = readSecurityBuffer(12);
                if (bytes.length != 0) {
                    try {
                        this.target = new String(bytes, "UnicodeLittleUnmarked");
                    } catch (UnsupportedEncodingException e) {
                        throw new NTLMEngineException(e.getMessage(), e);
                    }
                }
            }
            this.targetInfo = null;
            if (getMessageLength() >= 48) {
                byte[] bytes2 = readSecurityBuffer(40);
                if (bytes2.length != 0) {
                    this.targetInfo = bytes2;
                }
            }
        }

        byte[] getChallenge() {
            return this.challenge;
        }

        String getTarget() {
            return this.target;
        }

        byte[] getTargetInfo() {
            return this.targetInfo;
        }

        int getFlags() {
            return this.flags;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Type3Message extends NTLMMessage {
        protected byte[] domainBytes;
        protected byte[] hostBytes;
        protected byte[] lmResp;
        protected byte[] ntResp;
        protected byte[] sessionKey;
        protected int type2Flags;
        protected byte[] userBytes;

        Type3Message(String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation) throws NTLMEngineException {
            byte[] userSessionKey;
            this.type2Flags = type2Flags;
            String host2 = NTLMEngineImpl.convertHost(host);
            String domain2 = NTLMEngineImpl.convertDomain(domain);
            CipherGen gen = new CipherGen(target, user, password, nonce, targetInformation);
            try {
                if ((8388608 & type2Flags) != 0 && targetInformation != null && target != null) {
                    this.ntResp = gen.getNTLMv2Response();
                    this.lmResp = gen.getLMv2Response();
                    if ((type2Flags & 128) != 0) {
                        userSessionKey = gen.getLanManagerSessionKey();
                    } else {
                        userSessionKey = gen.getNTLMv2UserSessionKey();
                    }
                } else if ((524288 & type2Flags) != 0) {
                    this.ntResp = gen.getNTLM2SessionResponse();
                    this.lmResp = gen.getLM2SessionResponse();
                    if ((type2Flags & 128) != 0) {
                        userSessionKey = gen.getLanManagerSessionKey();
                    } else {
                        userSessionKey = gen.getNTLM2SessionResponseUserSessionKey();
                    }
                } else {
                    this.ntResp = gen.getNTLMResponse();
                    this.lmResp = gen.getLMResponse();
                    if ((type2Flags & 128) != 0) {
                        userSessionKey = gen.getLanManagerSessionKey();
                    } else {
                        userSessionKey = gen.getNTLMUserSessionKey();
                    }
                }
            } catch (NTLMEngineException e) {
                this.ntResp = new byte[0];
                this.lmResp = gen.getLMResponse();
                if ((type2Flags & 128) != 0) {
                    userSessionKey = gen.getLanManagerSessionKey();
                } else {
                    userSessionKey = gen.getLMUserSessionKey();
                }
            }
            if ((1073741824 & type2Flags) != 0) {
                this.sessionKey = NTLMEngineImpl.RC4(gen.getSecondaryKey(), userSessionKey);
            } else {
                this.sessionKey = null;
            }
            try {
                this.domainBytes = domain2.toUpperCase(Locale.US).getBytes("UnicodeLittleUnmarked");
                this.hostBytes = host2.getBytes("UnicodeLittleUnmarked");
                this.userBytes = user.getBytes("UnicodeLittleUnmarked");
            } catch (UnsupportedEncodingException e2) {
                throw new NTLMEngineException("Unicode not supported: " + e2.getMessage(), e2);
            }
        }

        @Override // ch.boye.httpclientandroidlib.impl.auth.NTLMEngineImpl.NTLMMessage
        String getResponse() {
            int sessionKeyLen;
            int ntRespLen = this.ntResp.length;
            int lmRespLen = this.lmResp.length;
            int domainLen = this.domainBytes.length;
            int hostLen = this.hostBytes.length;
            int userLen = this.userBytes.length;
            if (this.sessionKey != null) {
                sessionKeyLen = this.sessionKey.length;
            } else {
                sessionKeyLen = 0;
            }
            int ntRespOffset = 72 + lmRespLen;
            int domainOffset = ntRespOffset + ntRespLen;
            int userOffset = domainOffset + domainLen;
            int hostOffset = userOffset + userLen;
            int sessionKeyOffset = hostOffset + hostLen;
            int finalLength = sessionKeyOffset + sessionKeyLen;
            prepareResponse(finalLength, 3);
            addUShort(lmRespLen);
            addUShort(lmRespLen);
            addULong(72);
            addUShort(ntRespLen);
            addUShort(ntRespLen);
            addULong(ntRespOffset);
            addUShort(domainLen);
            addUShort(domainLen);
            addULong(domainOffset);
            addUShort(userLen);
            addUShort(userLen);
            addULong(userOffset);
            addUShort(hostLen);
            addUShort(hostLen);
            addULong(hostOffset);
            addUShort(sessionKeyLen);
            addUShort(sessionKeyLen);
            addULong(sessionKeyOffset);
            addULong((this.type2Flags & 128) | 12288 | (this.type2Flags & 512) | (this.type2Flags & 524288) | NTLMEngineImpl.FLAG_REQUEST_VERSION | (this.type2Flags & 32768) | (this.type2Flags & 32) | (this.type2Flags & 16) | (this.type2Flags & 536870912) | (this.type2Flags & Integer.MIN_VALUE) | (this.type2Flags & 1073741824) | (this.type2Flags & 8388608) | (this.type2Flags & 1) | (this.type2Flags & 4));
            addUShort(261);
            addULong(2600);
            addUShort(3840);
            addBytes(this.lmResp);
            addBytes(this.ntResp);
            addBytes(this.domainBytes);
            addBytes(this.userBytes);
            addBytes(this.hostBytes);
            if (this.sessionKey != null) {
                addBytes(this.sessionKey);
            }
            return super.getResponse();
        }
    }

    static void writeULong(byte[] buffer, int value, int offset) {
        buffer[offset] = (byte) (value & 255);
        buffer[offset + 1] = (byte) ((value >> 8) & 255);
        buffer[offset + 2] = (byte) ((value >> 16) & 255);
        buffer[offset + 3] = (byte) ((value >> 24) & 255);
    }

    static int F(int x, int y, int z) {
        return (x & y) | ((x ^ (-1)) & z);
    }

    static int G(int x, int y, int z) {
        return (x & y) | (x & z) | (y & z);
    }

    static int H(int x, int y, int z) {
        return (x ^ y) ^ z;
    }

    static int rotintlft(int val, int numbits) {
        return (val << numbits) | (val >>> (32 - numbits));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class MD4 {
        protected int A = 1732584193;
        protected int B = -271733879;
        protected int C = -1732584194;
        protected int D = 271733878;
        protected long count = 0;
        protected byte[] dataBuffer = new byte[64];

        MD4() {
        }

        void update(byte[] input) {
            int curBufferPos = (int) (this.count & 63);
            int inputIndex = 0;
            while ((input.length - inputIndex) + curBufferPos >= this.dataBuffer.length) {
                int transferAmt = this.dataBuffer.length - curBufferPos;
                System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
                this.count += transferAmt;
                curBufferPos = 0;
                inputIndex += transferAmt;
                processBuffer();
            }
            if (inputIndex < input.length) {
                int transferAmt2 = input.length - inputIndex;
                System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt2);
                this.count += transferAmt2;
                int i = curBufferPos + transferAmt2;
            }
        }

        byte[] getOutput() {
            int bufferIndex = (int) (this.count & 63);
            int padLen = bufferIndex < 56 ? 56 - bufferIndex : 120 - bufferIndex;
            byte[] postBytes = new byte[padLen + 8];
            postBytes[0] = Byte.MIN_VALUE;
            for (int i = 0; i < 8; i++) {
                postBytes[padLen + i] = (byte) ((this.count * 8) >>> (i * 8));
            }
            update(postBytes);
            byte[] result = new byte[16];
            NTLMEngineImpl.writeULong(result, this.A, 0);
            NTLMEngineImpl.writeULong(result, this.B, 4);
            NTLMEngineImpl.writeULong(result, this.C, 8);
            NTLMEngineImpl.writeULong(result, this.D, 12);
            return result;
        }

        protected void processBuffer() {
            int[] d = new int[16];
            for (int i = 0; i < 16; i++) {
                d[i] = (this.dataBuffer[i * 4] & 255) + ((this.dataBuffer[(i * 4) + 1] & 255) << 8) + ((this.dataBuffer[(i * 4) + 2] & 255) << 16) + ((this.dataBuffer[(i * 4) + 3] & 255) << 24);
            }
            int AA = this.A;
            int BB = this.B;
            int CC = this.C;
            int DD = this.D;
            round1(d);
            round2(d);
            round3(d);
            this.A += AA;
            this.B += BB;
            this.C += CC;
            this.D += DD;
        }

        protected void round1(int[] d) {
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[0], 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[1], 7);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[2], 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[3], 19);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[4], 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[5], 7);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[6], 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[7], 19);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[8], 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[9], 7);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[10], 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[11], 19);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[12], 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[13], 7);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[14], 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[15], 19);
        }

        protected void round2(int[] d) {
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[0] + 1518500249, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[4] + 1518500249, 5);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[8] + 1518500249, 9);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[12] + 1518500249, 13);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[1] + 1518500249, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[5] + 1518500249, 5);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[9] + 1518500249, 9);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[13] + 1518500249, 13);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[2] + 1518500249, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[6] + 1518500249, 5);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[10] + 1518500249, 9);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[14] + 1518500249, 13);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[3] + 1518500249, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[7] + 1518500249, 5);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[11] + 1518500249, 9);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[15] + 1518500249, 13);
        }

        protected void round3(int[] d) {
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[0] + 1859775393, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[8] + 1859775393, 9);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[4] + 1859775393, 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[12] + 1859775393, 15);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[2] + 1859775393, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[10] + 1859775393, 9);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[6] + 1859775393, 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[14] + 1859775393, 15);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[1] + 1859775393, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[9] + 1859775393, 9);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[5] + 1859775393, 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[13] + 1859775393, 15);
            this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[3] + 1859775393, 3);
            this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[11] + 1859775393, 9);
            this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[7] + 1859775393, 11);
            this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[15] + 1859775393, 15);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class HMACMD5 {
        protected byte[] ipad;
        protected MessageDigest md5;
        protected byte[] opad;

        HMACMD5(byte[] key) throws NTLMEngineException {
            try {
                this.md5 = MessageDigest.getInstance("MD5");
                this.ipad = new byte[64];
                this.opad = new byte[64];
                int keyLength = key.length;
                if (keyLength > 64) {
                    this.md5.update(key);
                    key = this.md5.digest();
                    keyLength = key.length;
                }
                int i = 0;
                while (i < keyLength) {
                    this.ipad[i] = (byte) (key[i] ^ 54);
                    this.opad[i] = (byte) (key[i] ^ 92);
                    i++;
                }
                while (i < 64) {
                    this.ipad[i] = 54;
                    this.opad[i] = 92;
                    i++;
                }
                this.md5.reset();
                this.md5.update(this.ipad);
            } catch (Exception ex) {
                throw new NTLMEngineException("Error getting md5 message digest implementation: " + ex.getMessage(), ex);
            }
        }

        byte[] getOutput() {
            byte[] digest = this.md5.digest();
            this.md5.update(this.opad);
            return this.md5.digest(digest);
        }

        void update(byte[] input) {
            this.md5.update(input);
        }

        void update(byte[] input, int offset, int length) {
            this.md5.update(input, offset, length);
        }
    }

    @Override // ch.boye.httpclientandroidlib.impl.auth.NTLMEngine
    public String generateType1Msg(String domain, String workstation) throws NTLMEngineException {
        return getType1Message(workstation, domain);
    }

    @Override // ch.boye.httpclientandroidlib.impl.auth.NTLMEngine
    public String generateType3Msg(String username, String password, String domain, String workstation, String challenge) throws NTLMEngineException {
        Type2Message t2m = new Type2Message(challenge);
        return getType3Message(username, password, workstation, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
    }
}