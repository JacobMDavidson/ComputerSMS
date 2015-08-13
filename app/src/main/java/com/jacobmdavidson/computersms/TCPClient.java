package com.jacobmdavidson.computersms;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by jacobdavidson on 8/6/15
 */
public class TCPClient {

    private String serverMessage;
    private String serverIP;
    private int port;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String computerIP, int port) {
        mMessageListener = listener;
        serverIP = computerIP;
        this.port = port;
    }
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient(){
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.i(Constants.DEBUGGING.LOG_TAG, "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, port);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.i(Constants.DEBUGGING.LOG_TAG, "C: Sent.");

                Log.i(Constants.DEBUGGING.LOG_TAG, "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }

                Log.i(Constants.DEBUGGING.LOG_TAG, "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.i(Constants.DEBUGGING.LOG_TAG, "S: Error", e);

            }

        } catch (Exception e) {

            Log.i(Constants.DEBUGGING.LOG_TAG, "C: Error", e);

        }

    }




    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }


}
