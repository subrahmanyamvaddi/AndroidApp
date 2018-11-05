package com.example.subrahmanyamvaddi.detect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> devices;
    private int mResourceId;

    public DeviceListAdapter(Context context, int resourceId, ArrayList<BluetoothDevice> dev) {
        super(context, resourceId, dev);
        devices = dev;
        mResourceId = resourceId;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int pos, View convertView, ViewGroup parent)
    {
        convertView = layoutInflater.inflate(mResourceId,null);

        BluetoothDevice device = devices.get(pos);

        if(device != null)
        {
            TextView deviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
            TextView deviceAddress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);

            if(deviceName != null)
            {
                deviceName.setText(device.getName());
            }
            if(deviceAddress != null){
                deviceAddress.setText(device.getAddress());
            }
        }

        return convertView;
    }
}
