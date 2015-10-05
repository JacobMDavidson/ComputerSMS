package com.jacobmdavidson.computersms;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jacobdavidson on 9/29/15. Parses incoming server xml messages
 */
public class XMLPullParserHandler {
    private SMSMessage smsMessage;
    private String text;

    public XMLPullParserHandler() {

    }

    public SMSMessage parse(InputStream is) {
        XmlPullParserFactory factory;
        XmlPullParser parser;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("smsmessage")) {
                            // Create a new smsMessage
                            smsMessage = new SMSMessage();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("number")) {
                            smsMessage.setNumber(text);
                        } else if (tagName.equalsIgnoreCase("body")) {
                            smsMessage.setBody(text);
                        }
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsMessage;
    }
}
