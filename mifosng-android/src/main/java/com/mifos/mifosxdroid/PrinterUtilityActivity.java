package com.mifos.mifosxdroid;

import static com.mifos.utils.PrinterUtility.intToByteArray;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mifos.utils.ReceiptContent;
import com.mifos.utils.ReceiptPrinterUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class PrinterUtilityActivity extends AppCompatActivity implements Runnable {

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    BluetoothAdapter mBluetoothAdapter;
    private final UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    Bundle printData = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Bundle bundle = getIntent().getExtras();
        printData.putString("PRINT_TEXT", bundle.getString("PRINT_DATA"));
        if (mBluetoothAdapter == null) {
            Toast.makeText(PrinterUtilityActivity.this, "Bluetooth not working", Toast.LENGTH_SHORT).show();

        } else {
            if (ContextCompat.checkSelfPermission(PrinterUtilityActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrinterUtilityActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT);
            } else {
                ListPairedDevices();
                Intent connectIntent = new Intent(PrinterUtilityActivity.this,
                        DeviceListActivity.class);
                startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            //if (mBluetoothSocket != null)
            //mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            //         if (mBluetoothSocket != null)
//                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    if (ContextCompat.checkSelfPermission(PrinterUtilityActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PrinterUtilityActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                    }

                    ReceiptPrinterUtils.printReceipt(ReceiptContent.defaultText());
                    this.finish();
//                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
//                            "Connecting...", mBluetoothDevice.getName() + " : "
//                                    + mBluetoothDevice.getAddress(), true, false);
//

//                    Thread mBlutoothConnectThread = new Thread(this);
//                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(PrinterUtilityActivity.this,
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(PrinterUtilityActivity.this, "Transaction Success, couldn't print receipt", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        if (ContextCompat.checkSelfPermission(PrinterUtilityActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrinterUtilityActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
        }
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(PrinterUtilityActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
            Thread t = new Thread() {
                public void run() {
                    try {
                        OutputStream os = mBluetoothSocket
                                .getOutputStream();
                        String BILL = printData.getString("PRINT_TEXT");

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
                            //if (mBluetoothSocket != null)
                            //mBluetoothSocket.close();
                        } catch (Exception e) {
                            Log.e("Tag", "Exe ", e);
                        }

                    } catch (Exception e) {
                        Log.e("PrinterUtility", "Exe ", e);
                    }
                }
            };
            t.start();
            if (mBluetoothAdapter != null) {
                if (ContextCompat.checkSelfPermission(PrinterUtilityActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrinterUtilityActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                }
                mBluetoothAdapter.disable();
            }
            Toast.makeText(PrinterUtilityActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
            Intent homepage = new Intent(getBaseContext(), HomeActivity.class);
            startActivity(homepage);
        }
    };

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    @Override
    public void run() {
        try {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrinterUtilityActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            final Message msg = new Message();
            msg.setData(printData);
            mHandler.sendMessage(msg);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            // closeSocket(mBluetoothSocket);
            return;
        }
    }
}