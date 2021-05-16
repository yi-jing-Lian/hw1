package com.example.hwtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceAdapter extends RecyclerView.Adapter <DeviceAdapter.ViewHolder>{
    private ArrayList<BLEDevice> list;
    private HashMap<String,BLEDevice> hashMap;

    public DeviceAdapter(){
        list=new ArrayList<BLEDevice>();
        hashMap=new HashMap<String, BLEDevice>();
    }
    public void addDevice(String mac,String rssi,String content){
        if(hashMap.containsKey(mac)) return;
        BLEDevice device=new BLEDevice();
        device.deviceName=mac;
        device.RSSI=rssi;
        device.content=content;
        Log.d("BLE",device.deviceName);
        list.add(0,device);
        hashMap.put(mac,device);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("BLED", "bind" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView rssi;
        private TextView content;
        private Button detail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.tv_name);
            rssi=(TextView)itemView.findViewById(R.id.tv_rssi);
            content=(TextView)itemView.findViewById(R.id.tv_content);
            content.setVisibility(View.GONE);
            detail=(Button)itemView.findViewById(R.id.btn_detail);
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstDirections.ActionFirstToSecond action = firstDirections.actionFirstToSecond();
                    action.setDetail(content.getText().toString());
                    Navigation.findNavController(view).navigate(action);
                }
            });
        }

        void bind(int listIndex){
            BLEDevice device=list.get(listIndex);
            name.setText(device.deviceName);
            rssi.setText(device.RSSI);
            content.setText(device.content);


        }


    }
}



