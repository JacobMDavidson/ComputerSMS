package com.jacobmdavidson.computersms;


import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by jacobdavidson on 8/6/15
 */
public class TCPClient {

    private String serverMessage;
    private String serverIP;
    private int port;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    private DiffieHellmanModule diffieHellmanModule;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String computerIP, int port, DiffieHellmanModule diffieHellmanModule) {
        mMessageListener = listener;
        serverIP = computerIP;
        this.port = port;
        this.diffieHellmanModule = diffieHellmanModule;
    }
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){

        if (out != null && !out.checkError()) {

            // If encryption connection has been made
            if (diffieHellmanModule.isConnected()) {
                message = diffieHellmanModule.encryptString(message);
            }
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

            //Log.i(Constants.DEBUGGING.LOG_TAG, "Connecting to Server...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, port);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                // Send the public Diffie Hellman key
                String message = new String(Base64.encode(diffieHellmanModule.getPublicKey().getEncoded(), Base64.NO_WRAP));
                //Log.i(Constants.DEBUGGING.LOG_TAG, "Public Key: " + message);
                sendMessage(message);
                //Log.i(Constants.DEBUGGING.LOG_TAG, "Public Key Sent!");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                //Log.i(Constants.DEBUGGING.LOG_TAG, "Waiting for DH Public Key from Server");
                while (!diffieHellmanModule.isConnected()) {
                    serverMessage = in.readLine();
                    //Log.i(Constants.DEBUGGING.LOG_TAG, serverMessage);
                    if (serverMessage != null) {
                        // @ TODO look for specific xml message with key built in and extract it
                        //attempt to calculate the AES key usig the DH key exchange
                        byte[] data = Base64.decode(serverMessage, Base64.NO_WRAP);
                        //Log.i(Constants.DEBUGGING.LOG_TAG, "Public Key: " + new String(data));
                        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
                        KeyFactory keyFactory = KeyFactory.getInstance("DH");
                        diffieHellmanModule.generateSecretKey(keyFactory.generatePublic(spec), true);
                        //Log.i(Constants.DEBUGGING.LOG_TAG, "AES Key Generated");
                    }
                    serverMessage = null;
                }

                //Log.i(Constants.DEBUGGING.LOG_TAG, "All Communication Now Encrypted!");

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    serverMessage = in.readLine();

                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                    }
                    serverMessage = null;

                }


            } catch (Exception e) {

                Log.i(Constants.DEBUGGING.LOG_TAG, "Communication Error", e);

            }

        } catch (Exception e) {

            Log.i(Constants.DEBUGGING.LOG_TAG, "Server connection error", e);

        }

    }




    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(String message);
    }


}
