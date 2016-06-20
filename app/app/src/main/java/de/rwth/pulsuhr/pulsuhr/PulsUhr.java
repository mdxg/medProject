package de.rwth.pulsuhr.pulsuhr;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by steffen on 14.05.16.
 */
public class PulsUhr {

    private BluetoothAdapter mBluetoothAdapter;
    private ConnectThread connectThread;
    private BluetoothDevice bluetoothDevice;
    private ConnectedThread connectedThread = null;
    private Context context;
    private Activity mActivity;
    private final String mac;

    public PulsUhr(Activity activity, String new_mac)
    {
        mActivity = activity;
        mac = new_mac;
        context = activity.getBaseContext();
        //get Bluetooth Module
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(activity.getApplicationContext(), "No Bluetooth Device Connected", Toast.LENGTH_LONG).show();
        }else
        {
            mBluetoothAdapter = adapter;
            Toast.makeText(activity.getApplicationContext(),"Bluetooth Module found", Toast.LENGTH_LONG).show();
            //Bluetoth devices exists and is set in mBluetoothAdapter
            //check if its turned on, otherwise go to bluetooth menu and ask to turn it on
            if (!mBluetoothAdapter.isEnabled()) {
                //set REQUEST_ENABLE_BT ID
                int REQUEST_ENABLE_BT = 50;
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }
    public void connect()
    {
        bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac);
        if(bluetoothDevice != null) {
            connectThread = new ConnectThread(bluetoothDevice);
            connectThread.start();
        }
    }

    public void cancel() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        connectedThread = null;
    }

    public void startBeacon() {
        if(connectedThread != null)
        {
            Log.i("Write:","0x00");
            byte startByte = 0x00;
            connectedThread.write(startByte);
        }
    }

    public void startMeasurement() {
        if(connectedThread!=null)
        {
            Log.i("Measurement", "startMeasurement() called");
            connectedThread.start();
            connectedThread.startMeasurement();
        }else { Log.i("Measurement:", "connectedThread = null"); }
    }

    public void continueBeacon() {
        if(connectedThread != null)
        {
            Log.i("Write:","0x0f");
            byte continueByte = 0x0f;
            connectedThread.write(continueByte);
        }
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            Log.i("Connect", " Connection established");
            connectedThread = new ConnectedThread(mmSocket);
            //connectedThread.run();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean started = false;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
          /*  byte[] buffer = new byte[1024];  // buffer store for the stream
           // int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                   final int  bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //check if Fragment is visible
                            MeasurePulsFragment measurePulsFragment = (MeasurePulsFragment) mActivity.getFragmentManager().findFragmentByTag("MeasurePulse");
                            if(measurePulsFragment != null && measurePulsFragment.isVisible())
                            {
                                //plot live graph data
                                GraphView graphView = (GraphView) mActivity.findViewById(R.id.graph);
                                measurePulsFragment.addDataPoint((int) bytes);
                            }
                        }
                    });
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    String str = new String(buffer);
                    Log.d("Received", str);
                } catch (IOException e) {
                    Log.d("Connection","Read error");
                    break;
                }
            }
            String str = new String(buffer);
            Log.d("Finally Received", str);
            //save buffer in DB
            ContentValues values = new ContentValues();
            Long timestamp = System.currentTimeMillis()/1000;
            values.put("Timestamp", timestamp);
            values.put("Measurements", buffer);
            SqlHelper mDbHelper = new SqlHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insert("Data", null, values); */
        }

        public void listenToData()
        {
            Log.i("Measurement", "Listening:");
            byte[] buffer = new byte[1024];  // buffer store for the stream
            // int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (started) {
                try {
                    // Read from the InputStream
                    final int  bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //check if Fragment is visible
                            MeasurePulsFragment measurePulsFragment = (MeasurePulsFragment) mActivity.getFragmentManager().findFragmentByTag("MeasurePulse");
                            if(measurePulsFragment != null && measurePulsFragment.isVisible())
                            {
                                //plot live graph data
                                GraphView graphView = (GraphView) mActivity.findViewById(R.id.graph);
                                measurePulsFragment.addDataPoint((int) bytes);
                            }
                        }
                    });
                    //mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    String str = new String(buffer);
                    Log.d("Received", str);
                } catch (IOException e) {
                    Log.d("Connection","Read error");
                    break;
                }
            }
            String str = new String(buffer);
            Log.d("Finally Received", str);
            //save buffer in DB
            ContentValues values = new ContentValues();
            Long timestamp = System.currentTimeMillis()/1000;
            values.put("Timestamp", timestamp);
            values.put("Measurements", buffer);
            SqlHelper mDbHelper = new SqlHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            db.insert("Data", null, values);
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void startMeasurement()
        {
            started = true;
            listenToData();
            Log.i("measurement:", "Frames started");
            write((byte)0x00);
            for (int i=0; i <=30; i++)
            {
                try {
                    sleep(1000);
                }catch(InterruptedException e)
                {
                    Log.i("Measurement", "Interrupted Exeption:" + e);
                }
                write((byte)0x0f);
            }
            started = false;
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}


