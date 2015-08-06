package com.jacobmdavidson.computersms;

/**
 * Created by jacobdavidson on 8/6/15
 */
public class Constants {
    public interface ACTION {
        String STARTFOREGROUND_ACTION = "com.jacobmdavidson.computersms.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.jacobmdavidson.computersms.action.stopforeground";
    }
    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 5253;
    }
    public interface TOGGLE_BUTTON {
        String STATE = "ToggleButtonState";
    }
    public interface MESSAGE {
        String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
        String CALL = "android.intent.action.PHONE_STATE";
    }
}
