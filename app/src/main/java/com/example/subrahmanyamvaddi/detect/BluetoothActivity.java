package com.example.subrahmanyamvaddi.detect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "Bluetooth Activity";

    BluetoothAdapter bluetoothAdapter;
    Button btnOn, btnStartCon, btnSend;
    TextView tvStatus,tvBondStatus, tvRecieve;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView lvDevices;
    EditText etReceive;
    StringBuilder messages;

    BluetoothConnctionService bluetoothConnctionService;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice mBTDevice;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver broadCastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,bluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"broadCastReceiver1 : STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"broadCastReceiver1: STATE TURNIG OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"broadCastReceiver1 : STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"broadCastReceiver1 : STATE TURNING ON");
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy(){
        Log.d(TAG,"On Destroy");
        super.onDestroy();
        unregisterReceiver(broadCastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        unregisterReceiver(broadcastReceiver3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        btnOn = findViewById(R.id.buttonBTon);
        tvStatus = findViewById(R.id.textView4);
        lvDevices = findViewById(R.id.listViewDevices);
        tvBondStatus = findViewById(R.id.tvBondStatus);
        btnStartCon = findViewById(R.id.buttonConnect);
        etReceive = findViewById(R.id.editTextReceive);
        btnSend = findViewById(R.id.buttonSend);
        tvRecieve = findViewById(R.id.textViewRecieved);
        messages = new StringBuilder();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReciever,new IntentFilter("IncomingMessage"));

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"On click : Enabling/disabling BT");
                enableDisableBT();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(broadcastReceiver2,filter);

        setBTStatus();

        lvDevices.setOnItemClickListener(BluetoothActivity.this);

        btnStartCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = etReceive.getText().toString().getBytes(Charset.defaultCharset());
                bluetoothConnctionService.write(bytes);
                etReceive.setText("");
            }
        });
    }

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");

            messages.append(text + '\n');
            tvRecieve.setText(messages.toString());
        }
    };

    private void startConnection() {
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG,"Initializing RFConn bluetooth connection!");

        bluetoothConnctionService.startClient(device,uuid);
    }

    private void setBTStatus(){
        if(bluetoothAdapter == null)
        {
            tvStatus.setText("No bluetooth!");
        }
        else if(!bluetoothAdapter.isEnabled()){
            tvStatus.setText("Bluetooth disabled!");
        }
        else if(bluetoothAdapter.isEnabled())
        {
            tvStatus.setText("Bluetooth enabled!");
        }
    }

    private final BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED)
                {
                    Log.d(TAG,"Broadcase Receiver2 : Bond Bonded");
                    tvBondStatus.setText("Paired Succesfully!");

                    mBTDevice = mDevice;
                }

                if(mDevice.getBondState() == BluetoothDevice.BOND_BONDING)
                {
                    Log.d(TAG,"Broadcase Receiver2 : Bond Bonding");
                    tvBondStatus.setText("Pairing...!");

                }
                if(mDevice.getBondState() == BluetoothDevice.BOND_NONE)
                {
                    Log.d(TAG,"Broadcase Receiver2 : Bond None");
                    tvBondStatus.setText("Pairing Failed!");

                }
            }
        }
    };

    public void enableDisableBT() {
        if(bluetoothAdapter == null)
        {
            Log.d(TAG, "EnableDisableBT: does not have BT compatibility");
            tvStatus.setText("No bluetooth!");
        }
        else if(!bluetoothAdapter.isEnabled()){
            Log.d(TAG, "EnableDisableBT: Enabling BT");
            Intent enableBTAdaptor = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTAdaptor);
            tvStatus.setText("Bluetooth Enabled!");

            IntentFilter btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadCastReceiver1,btFilter);
        }
        else if(bluetoothAdapter.isEnabled())
        {
            Log.d(TAG, "EnableDisableBT: Disabling BT");

            bluetoothAdapter.disable();
            tvStatus.setText("Bluetooth Disabled!");

            IntentFilter btFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadCastReceiver1,btFilter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        bluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemCLicked: you clicked on a device!");

        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG,"DeviceNAme: " + deviceName + " and device Address: " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with device: " + deviceName);
            mBTDevices.get(position).createBond();

            mBTDevice = mBTDevices.get(position);
            bluetoothConnctionService = new BluetoothConnctionService(BluetoothActivity.this);
        }
    }

    public void btnDiscover(View view) {
        Log.d(TAG,"looking for unpaired devices");

        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG,"Canceling discovery");

            checkBTPremission();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver3,discoverFilter);
        }
        if(!bluetoothAdapter.isDiscovering())
        {
            checkBTPremission();
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver3,discoverFilter);
        }

    }

    private final BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG,"broadcastReceiver3: Action found");

            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG,"device found: " + device.getName() + ":" + device.getAddress());
                deviceListAdapter = new DeviceListAdapter(context,R.layout.device_adapter_view,mBTDevices);
                lvDevices.setAdapter(deviceListAdapter);
            }
        }
    };

    private void checkBTPremission() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
        }
        else
        {
            Log.d(TAG,"checkBTPermissions: No nedd to check permissions. SDK version is less than Lollipop");
        }
    }
}
