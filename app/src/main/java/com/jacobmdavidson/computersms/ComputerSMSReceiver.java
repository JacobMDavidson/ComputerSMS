package com.jacobmdavidson.computersms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by jacobdavidson on 8/6/15
 */
public class ComputerSMSReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // SMS message is received
        if(action.equals(Constants.MESSAGE.SMS_RECEIVED)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {

                // Build the incoming sms intent and start the service
                Intent incomingSmsIntent = new Intent(context, ComputerSMSService.class);
                incomingSmsIntent.setAction(Constants.ACTION.INCOMING_SMS_ACTION);

                // Get the pdus for the intent
                Object[] pdus = (Object[]) extras.get("pdus");

                if (pdus != null) {
                    // Get message body and sender for each message, add to the intent, and start service
                    for (Object message : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) message);
                        String sender = sms.getOriginatingAddress();
                        incomingSmsIntent.putExtra("sender", sender);
                        String body = sms.getMessageBody();
                        incomingSmsIntent.putExtra("body", body);
                        context.startService(incomingSmsIntent);

                    }
                }
            }



            // Phone call is received
        } else if (action.equals(Constants.MESSAGE.CALL)) {

            Bundle extras = intent.getExtras();
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                // Build the  incoming call intent and send it to the service
                Intent incomingCallIntent = new Intent(context, ComputerSMSService.class);
                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCallIntent.setAction(Constants.ACTION.INCOMING_CALL_ACTION);
                incomingCallIntent.putExtra("number", phoneNumber);
                context.startService(incomingCallIntent);
            }

        }
    }
}
