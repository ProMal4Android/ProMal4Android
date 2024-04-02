package org.torproject.android.service;

/* loaded from: classes.dex */
public interface TorServiceConstants {
    public static final String BINARY_PRIVOXY_VERSION = "3.0.12";
    public static final String BINARY_TOR_VERSION = "0.2.4.3-alpha";
    public static final String CHMOD_EXE_VALUE = "700";
    public static final int DISABLE_TOR_MSG = 3;
    public static final int ENABLE_TOR_MSG = 2;
    public static final int FILE_WRITE_BUFFER_SIZE = 2048;
    public static final String GEOIP_ASSET_KEY = "geoip";
    public static final String IP_LOCALHOST = "127.0.0.1";
    public static final int LOG_MSG = 4;
    public static final int MESSAGE_TRAFFIC_COUNT = 5;
    public static final String OBFSPROXY_ASSET_KEY = "obfsproxy";
    public static final String ORWEB_APP_USERNAME = "info.guardianproject.browser";
    public static final int PORT_HTTP = 8118;
    public static final int PORT_SOCKS = 9050;
    public static final String PREF_BINARY_PRIVOXY_VERSION_INSTALLED = "BINARY_PRIVOXY_VERSION_INTALLED";
    public static final String PREF_BINARY_TOR_VERSION_INSTALLED = "BINARY_TOR_VERSION_INTALLED";
    public static final String PRIVOXYCONFIG_ASSET_KEY = "privoxy.config";
    public static final String PRIVOXY_ASSET_KEY = "privoxy";
    public static final int PROFILE_OFF = -1;
    public static final int PROFILE_ON = 1;
    public static final String SHELL_CMD_CHMOD = "chmod";
    public static final String SHELL_CMD_KILL = "kill -9";
    public static final String SHELL_CMD_PIDOF = "pidof";
    public static final String SHELL_CMD_PS = "ps";
    public static final String SHELL_CMD_RM = "rm";
    public static final int STANDARD_DNS_PORT = 53;
    public static final int STATUS_CONNECTING = 2;
    public static final int STATUS_MSG = 1;
    public static final int STATUS_OFF = 0;
    public static final int STATUS_ON = 1;
    public static final String TORRC_ASSET_KEY = "torrc";
    public static final String TORRC_TETHER_KEY = "torrctether";
    public static final String TOR_APP_USERNAME = "org.torproject.android";
    public static final String TOR_BINARY_ASSET_KEY = "tor";
    public static final String TOR_CONTROL_COOKIE = "control_auth_cookie";
    public static final int TOR_CONTROL_PORT = 9051;
    public static final String TOR_CONTROL_PORT_MSG_BOOTSTRAP_DONE = "Bootstrapped 100%";
    public static final int TOR_DNS_PORT = 5400;
    public static final int TOR_TRANSPROXY_PORT = 9040;
    public static final int UPDATE_TIMEOUT = 1000;
    public static final String URL_TOR_CHECK = "https://check.torproject.org";
}