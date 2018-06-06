package com.example.shubham.imfundo;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.ParcelUuid;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BluetoothActivity extends AppCompatActivity {


    BluetoothAdapter bluetoothAdapter;
    ListView deviceListviewUnpaired;
    ArrayList<String> devicesListUnpaired;
    ListView deviceListviewPaired;
    ArrayList<String> devicesListPaired;
    ArrayList<BluetoothDevice> BTlistUnpaired;
    ArrayList<BluetoothDevice> BTlistPaired;
    ArrayAdapter<String> listAdapterUnpaired;
    ArrayAdapter<String> listAdapterPaired;
    FloatingActionButton fabBT;

    OutputStream outputStream;
    int pairRegisterIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);


        AlertDialog.Builder dialogbuilder=new AlertDialog.Builder(this);
        dialogbuilder.setView(R.layout.dialog_bt_instruct);
        dialogbuilder.create().show();

        deviceListviewUnpaired = findViewById(R.id.bt_devices_list_unpaired);
        deviceListviewPaired = findViewById(R.id.bt_devices_list_paired);
        devicesListUnpaired = new ArrayList<>();
        devicesListPaired = new ArrayList<>();
        BTlistUnpaired = new ArrayList<>();
        BTlistPaired = new ArrayList<>();
        pairRegisterIndex=-1;
        fabBT=findViewById(R.id.fab_sendbt);
        listAdapterUnpaired = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesListUnpaired);
        deviceListviewUnpaired.setAdapter(listAdapterUnpaired);

        deviceListviewUnpaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pairRegisterIndex=i;
                if(BTlistPaired.contains(BTlistUnpaired.get(i))){
                    Toast.makeText(BluetoothActivity.this, "Already Paired ", Toast.LENGTH_SHORT).show();
                }
                else
                    pairDevice(BTlistUnpaired.get(i));
            }
        });

        listAdapterPaired = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesListPaired);
        deviceListviewPaired.setAdapter(listAdapterPaired);
        deviceListviewPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(BluetoothActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {

            Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTintent, Constants.BT_ENABLE_REQUEST);
        }
        else {
            checkBondedDevices();
            GetAvailableDevices();


        }


        fabBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean goNOgo=Init();
                if (goNOgo==true) {
                    if(outputStream==null){
                        Toast.makeText(BluetoothActivity.this, "Make sure MJBT device is turned on.", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
                    View v = LayoutInflater.from(BluetoothActivity.this).inflate(R.layout.bt_dialog_data_send, null);
                    builder.setView(v);
                    final EditText textview = v.findViewById(R.id.send_bt_text);
                    builder.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String str = textview.getText().toString();
                            SendString(str);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    //dialog.show();
                        }
                }
            }
        });


    }



    private boolean Init() {
        BluetoothDevice bluetoothDevice =null ;
        BluetoothSocket socket;

        for(BluetoothDevice bt : bluetoothAdapter.getBondedDevices()){
            if(bt.getName().equals("MJBT"))
                bluetoothDevice=bt;
        }
        if(bluetoothDevice==null){
            Toast.makeText(this, "Arduino not paired.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else

        {
            ParcelUuid[] PUids= bluetoothDevice.getUuids();
            try {
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(PUids[0].getUuid());
                socket.connect();
                outputStream = socket.getOutputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
        return true;
        }
    }




    private void SendString(String text)  {


        try {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    private  void checkBondedDevices(){
        for(BluetoothDevice bt : bluetoothAdapter.getBondedDevices()){
            if(bt.getName().equals("MJBT")) {
                devicesListPaired.add(bt.getName() + "  ( " + bt.getAddress() + " )");
                BTlistPaired.add(bt);
            }
        }
        listAdapterPaired.notifyDataSetChanged();

    }


    private void GetAvailableDevices() {

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btReciever, filter);
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
    }


    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        IntentFilter pairFilter= new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver,pairFilter);
    }

    private BroadcastReceiver btReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equalsIgnoreCase(action)) {
                BluetoothDevice bt = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if ((!BTlistUnpaired.contains(bt))&& (!BTlistPaired.contains(bt))&& bt!=null && bt.getName().equals("MJBT")) {
                    BTlistUnpaired.add(bt);
                    devicesListUnpaired.add(bt.getName()+"  ( "+bt.getAddress()+" )");
                    listAdapterUnpaired.notifyDataSetChanged();
                }
            }
        }
    };

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Paired", Toast.LENGTH_SHORT).show();
                    BTlistPaired.add(BTlistUnpaired.remove(pairRegisterIndex));
                    devicesListPaired.add(devicesListUnpaired.remove(pairRegisterIndex));
                    listAdapterUnpaired.notifyDataSetChanged();
                    listAdapterPaired.notifyDataSetChanged();
                    unregisterReceiver(mPairReceiver);

                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(context, "Unpaired", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.BT_ENABLE_REQUEST) {
            if (resultCode == RESULT_OK)
                Toast.makeText(this, RESULT_OK + " ", Toast.LENGTH_SHORT).show();
            checkBondedDevices();
            GetAvailableDevices();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(btReciever);
        super.onDestroy();
    }
}


