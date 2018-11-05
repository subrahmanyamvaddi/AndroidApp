package com.example.subrahmanyamvaddi.detect;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.*;

public class BluetoothConnctionService {
    public static final String TAG = "BLUETOOTHCONNECTIONSERV";
    public static final String APP_NAME = "MY_APP";

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter;
    Context mContext;
    private AcceptThread mInsecureAcceptThread;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread connectedThread;

    public BluetoothConnctionService(Context context){
        mContext = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }

    private class AcceptThread extends Thread{
        private final BluetoothServerSocket mServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try {
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME,MY_UUID_INSECURE);
                Log.d(TAG,"Accept Thread: Setting up server using: " + MY_UUID_INSECURE);
            }
            catch (IOException e){
                Log.d(TAG,"AcceptThread IOException: " + e.getMessage());
            }
            mServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG,"Accept Thread: runing");

            BluetoothSocket bluetoothSocket = null;
            try {
                Log.d(TAG,"AcceptThread RFCOM server socket start..");
                bluetoothSocket = mServerSocket.accept();
                Log.d(TAG,"AcceptThread RFCom server socket accepted connection");
            }
            catch (IOException e){

                Log.d(TAG,"AcceptThread IOException: " + e.getMessage());
            }

            if(bluetoothSocket != null){
                connected(bluetoothSocket,mmDevice);
            }

            Log.i(TAG,"End AcceptThread");
        }

        public void cancel(){
            try {
                mServerSocket.close();
            }
            catch (IOException e){

                Log.d(TAG,"close of AcceptThread failed, IOException: " + e.getMessage());
            }
        }
    }

    private class ConnectThread extends Thread
    {
        private BluetoothSocket mSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid){
            Log.d(TAG,"ConnctThread started");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run()
        {
            BluetoothSocket tmp = null;
            Log.d(TAG,"RUN ConnectThread");

            try {
                Log.d(TAG,"Trying to create InsecureRFConn using UUID: " + MY_UUID_INSECURE);
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            }
            catch (IOException e){
                Log.d(TAG,"could not create insecure rfcomm connection: " + e.getMessage());

            }
            mSocket = tmp;
            bluetoothAdapter.cancelDiscovery();

            try {
                mSocket.connect();
                Log.d(TAG,"ConnectThread connected");
            }
            catch (IOException e){
                try {
                    mSocket.close();
                    Log.d(TAG,"run closed socket");
                } catch (IOException e1) {
                    Log.d(TAG,"ConnectThread unable to close connection in socket");
                }
                Log.d(TAG,"ConnectThread cannot connect to UUID using");
            }

            connected(mSocket,mmDevice);
        }

        public void cancel(){
            try {
                mSocket.close();
            }
            catch (IOException e){

                Log.d(TAG,"close of ConnectThread failed, IOException: " + e.getMessage());
            }
        }
    }

    public synchronized void start(){
        Log.d(TAG, "start");

        if(mConnectThread !=null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG,"start client");

        mProgressDialog = ProgressDialog.show(mContext,"connecting bluetooth","Please wait..",true);

        mConnectThread = new ConnectThread(device,uuid);
        mConnectThread.start();
    }

    public class ConnectedThread extends Thread
    {
        public final BluetoothSocket bluetoothSocket;
        public final java.io.InputStream mminputStream;
        public final java.io.OutputStream mmoutputStream;

        public ConnectedThread(BluetoothSocket bS){
            bluetoothSocket = bS;

            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                mProgressDialog.dismiss();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mminputStream = tempIn;
            mmoutputStream = tempOut;
        }

        public void run()
        {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true){
                try {
                    bytes = mminputStream.read(buffer);
                    String incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG,"Incoming Message: "+ incomingMessage);

                    Intent incomingMessageIntent = new Intent("IncomingMessage");
                    incomingMessageIntent.putExtra("theMessage",incomingMessage);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(incomingMessageIntent);
                } catch (IOException e) {
                    Log.d(TAG,"read error reading inputstream" + e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes,Charset.defaultCharset());
            Log.d(TAG,"Writing to output stream :" + text);

            try {
                mmoutputStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG,"Write error writing to outstream" + e.getMessage());
            }
        }

        public void cancel(){
            try {
                bluetoothSocket.close();
            }
            catch (IOException e){

                Log.d(TAG,"close of ConnectedThread failed, IOException: " + e.getMessage());
            }
        }
    }

    private void connected(BluetoothSocket mSocket, BluetoothDevice mmDevice) {
        Log.d(TAG,"connected starting:");

        connectedThread = new ConnectedThread(mSocket);
        connectedThread.start();
    }

    public void write(byte[] out){
        Log.d(TAG,"connect Write called");
        connectedThread.write(out);
    }

}
