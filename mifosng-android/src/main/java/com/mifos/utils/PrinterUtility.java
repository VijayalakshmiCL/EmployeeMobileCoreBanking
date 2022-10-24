package com.mifos.utils;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.mifos.mifosxdroid.PrintersActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.util.Toaster;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import android.os.Handler;

public class PrinterUtility implements Runnable{

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private final Activity activity;
    private final Context context;
    static BluetoothAdapter mBluetoothAdapter;
    private final UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private static BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    Bundle printData = new Bundle();
    public PrinterUtility(Context context, Activity activity){
        this.activity = activity;
        this.context = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth not working", Toast.LENGTH_SHORT).show();
        }
    }

    public void printReceipt(String text){
            printData.putString("PRINT_TEXT", text);
            run();
    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    @SuppressLint("HandlerLeak")
    private static final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // mBluetoothConnectProgressDialog.dismiss();
            // Toast.makeText(MainActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
            Thread t = new Thread() {
                public void run() {
                    try {
                        OutputStream os = mBluetoothSocket
                                .getOutputStream();
                        Bundle b = msg.getData();
                        String BILL = b.getString("PRINT_TEXT");

//                        BILL = "\n"+
//                                HO+"\n"
//                                +"------------------------\n"
//                                +  "Loan Transaction Reciept \n " +
//                                "\n"+
//                                " Date = "+Tra_date+"\n"+
//                                "\n"+
//                                " Client = " + Clientshort+"\n"+
//                                "\n"+
//                                " Loan ID     = " + loanId+"\n"+
//                                "\n"+
//                                " Transaction ID = "+ ResId+"\n"+
//                                "\n"+
//                                " Paid Amount = " + Tra_amount+"\n" +
//                                "\n"+
//                                " Staff = "+loanOfficerName+
//                                "\n"+
//                                " Branch = "+branch+
//                                "\n";
//                        BILL = BILL
//                                + "-----------------------\n"+
//                                "\n";


                        os.write(BILL.getBytes());
                        //This is printer specific code you can comment ==== > Start

                        // Setting height
                        int gs = 29;
                        os.write(intToByteArray(gs));
                        int h = 104;
                        os.write(intToByteArray(h));
                        int n = 162;
                        os.write(intToByteArray(n));

                        // Setting Width
                        int gs_width = 29;
                        os.write(intToByteArray(gs_width));
                        int w = 119;
                        os.write(intToByteArray(w));
                        int n_width = 2;
                        os.write(intToByteArray(n_width));
                        try {
                            if (mBluetoothSocket != null)
                                mBluetoothSocket.close();
                        } catch (Exception e) {
                            Log.e("Tag", "Exe ", e);
                        }

                    } catch (Exception e) {
                        Log.e("PrinterUtility", "Exe ", e);
                    }
                }
            };
            t.start();
        }
    };

    @Override
    public void run() {
        try {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            final Message msg = new Message();
            msg.setData(printData);
            mHandler.sendMessage(msg);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }
}
