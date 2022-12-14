package com.mifos.mifosxdroid;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;


public class DeviceListActivity extends Activity {
    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        setResult(Activity.RESULT_CANCELED);
        ArrayAdapter<String> mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);

        ListView mPairedListView = findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);
        if (ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeviceListActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        if (mPairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            if (ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DeviceListActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
            }
            mBluetoothAdapter.cancelDiscovery();
        }
    }

    private final OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {

            try {
                if (ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DeviceListActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 1);
                }
                mBluetoothAdapter.cancelDiscovery();
                String mDeviceInfo = ((TextView) mView).getText().toString();
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);
                Log.v(TAG, "Device_Address " + mDeviceAddress);

                Bundle mBundle = new Bundle();
                mBundle.putString("DeviceAddress", mDeviceAddress);
                Intent mBackIntent = new Intent();
                mBackIntent.putExtras(mBundle);
                setResult(Activity.RESULT_OK, mBackIntent);
                finish();
            } catch (Exception ignored) {

            }
        }
    };

}