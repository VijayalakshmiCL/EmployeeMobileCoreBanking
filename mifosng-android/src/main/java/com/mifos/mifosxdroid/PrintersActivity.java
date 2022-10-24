package com.mifos.mifosxdroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mifos.mifosxdroid.adapters.BluetoothListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.models.BluetoothModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class PrintersActivity extends MifosBaseActivity {

    private static final int PERMISSION_BLUETOOTH = 1;
    BluetoothAdapter btAdapter;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.BLUETOOTH_ADVERTISE
    };
    private static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.BLUETOOTH_ADVERTISE
    };

    ArrayList<BluetoothModel> listBluetooth = new ArrayList<>();
    BluetoothListAdapter adapter = new BluetoothListAdapter(listBluetooth, this);
    //  Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    checkPermissions();
                }
                if (ContextCompat.checkSelfPermission(PrintersActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PrintersActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PrintersActivity.PERMISSION_BLUETOOTH);
                }
                if(isConnected(device)){
                    String deviceName = device.getName() != null ? device.getName() : device.getAddress();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    listBluetooth.add(new BluetoothModel(deviceName));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
        if (ContextCompat.checkSelfPermission(PrintersActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrintersActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PrintersActivity.PERMISSION_BLUETOOTH);
        }
    }

    private boolean  isConnected(BluetoothDevice device) {
        try {
             Method m = device.getClass().getMethod("isConnected");
            return Boolean.parseBoolean(Objects.requireNonNull(m.invoke(device)).toString());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkPermissions();
        }
        setContentView(R.layout.activity_printers);
        setToolbarTitle("Connected Devices");
        showBackButton();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        if (btAdapter == null) {
            Toast.makeText(this, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!btAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        if (ContextCompat.checkSelfPermission(PrintersActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PrintersActivity.this, new String[]{Manifest.permission.BLUETOOTH}, PrintersActivity.PERMISSION_BLUETOOTH);
        }

//        BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
//        if (connection != null) {
//            try {
//                EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
//                final String text;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        final Locale locale = new Locale("id", "ID");
//                        final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", locale);
//                        final NumberFormat nf =  NumberFormat.getCurrencyInstance(locale);
//
//                        text = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
//                                this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.about_icon_github,
//                                        DisplayMetrics.DENSITY_LOW, getTheme())) + "</img>\n" +
//                                "[L]\n" +
//                                "[L] Chetana Fraternity Fund (MH) PVT LTD. \n" +
//                                "[L]" + df.format(new Date()) + "\n" +
//                                "[C]================================\n" +
//                                "[L]<b>Item 1</b>\n" +
//                                "[L]    1 pcs[R]" + nf.format(25000) + "\n" +
//                                "[L]<b>Item 2</b>\n" +
//                                "[L]    1 pcs[R]" + nf.format(45000) + "\n" +
//                                "[L]<b>Item 3</b>\n" +
//                                "[L]    2 pcs[R]" + nf.format(20000) + "\n" +
//                                "[C]--------------------------------\n" +
//                                "[L]TOTAL[R]" + nf.format(90000) + "\n" +
//                                "[L]DISCOUNT 15%[R]" + nf.format(13500) + "\n" +
//                                "[L]TAX 10%[R]" + nf.format(7650) + "\n" +
//                                "[L]<b>GRAND TOTAL[R]" + nf.format(84150) + "</b>\n" +
//                                "[C]--------------------------------\n" +
//                                "[L]\n";
//                        printer.printFormattedText(text);
//                    }
//                }
//
//
//            } catch (EscPosConnectionException e) {
//                e.printStackTrace();
//            } catch (EscPosEncodingException e) {
//                e.printStackTrace();
//            } catch (EscPosBarcodeException e) {
//                e.printStackTrace();
//            } catch (EscPosParserException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            Toast.makeText(this, "No printer was connected!", Toast.LENGTH_SHORT).show();
//        }



        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_bluetooth_device_list);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
        //check if the bluetooth service is available on the device
        if (btAdapter != null) { //check if the device supports bluetooth
            /*
             ** if the bluetooth module is not enabled this block of code pops-up a message to
             * enable it via an Intent , this is done by performing an Intent as shown below
             */
            if (!btAdapter.isEnabled()) {
                Intent bluetoothEnableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothEnableIntent, 0);
            }
            Toast.makeText(getApplicationContext(),"Searching for nearby Devices",Toast.LENGTH_SHORT).show();
            Intent makeDeviceDiscoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(makeDeviceDiscoverable,0);
            btAdapter.startDiscovery();
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    if(isConnected(device)) {
                        listBluetooth.add(new BluetoothModel(deviceName));
                    }
                }
            }

        }





//        CheckBluetoothState();
//        try {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PrintersActivity.PERMISSION_BLUETOOTH);
//            } else {
//                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
//                if (connection != null) {
//                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("APP", "Can't print", e);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkPermissions(){
        int permission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT);
        int permission2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        } else if (permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    1
            );
        }
    }
}
