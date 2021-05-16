package com.example.hwtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link first#newInstance} factory method to
 * create an instance of this fragment.
 */
public class first extends Fragment {
    private DeviceAdapter mResultAdapter;
    private RecyclerView mRecyclerView;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean scanning=false;
    private Button btn_scan;

//    public void stopCast(){
//        scanning=false;
//        mBluetoothLeScanner.stopScan(startScanCallback);
//        btn_scan.setText("SCAN");
//    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public first() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        scanning = false;
        mBluetoothLeScanner.stopScan(startScanCallback);
        btn_scan.setText("Start scanning");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment first.
     */
    // TODO: Rename and change types and number of parameters
    public static first newInstance(String param1, String param2) {
        first fragment = new first();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mResultAdapter = new DeviceAdapter();
        mRecyclerView.setAdapter(mResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(),DividerItemDecoration.VERTICAL));

        scanning = false;
        setUpPermissions();

        btn_scan = getActivity().findViewById(R.id.btn_scan);

        btn_scan.setOnClickListener((v) -> {
            if (scanning == false) {
                scanning = true;
                mBluetoothLeScanner.startScan(startScanCallback);
                btn_scan.setText("Stop scanning");
            } else {
                scanning = false;
                mBluetoothLeScanner.stopScan(startScanCallback);
                btn_scan.setText("Start scanning");
            }
        });


    }
    private static final int PERMIWWION_REQUEST_CODE=666;
    private final static String[] permisseionsWeNeed=new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private void setUpPermissions(){
        boolean isGranted=true;
        for(String permission:permisseionsWeNeed){
            isGranted&= ActivityCompat.checkSelfPermission(this.getActivity(),permission)== PackageManager.PERMISSION_GRANTED;
        }
        if(!isGranted){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(permisseionsWeNeed,PERMIWWION_REQUEST_CODE);
            }
            else{
                Toast.makeText(this.getActivity(),"no permission",Toast.LENGTH_SHORT).show();
                getActivity().finishAndRemoveTask();
            }
        }else{
            initBluetooth();
        }

    }
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMIWWION_REQUEST_CODE:{
                boolean isGranted=grantResults.length>0;
                for(int grantResult:grantResults){
                    isGranted&=grantResult==PackageManager.PERMISSION_GRANTED;
                }
                if(isGranted){
                    initBluetooth();
                }
                else{
                    Toast.makeText(this.getActivity(),"no permission",Toast.LENGTH_SHORT).show();
                    getActivity().finishAndRemoveTask();
                }
            }
        }
    }

    private void initBluetooth(){
        boolean success=false;
        mBluetoothManager=(BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if(mBluetoothManager!=null){
            mBluetoothAdapter=mBluetoothManager.getAdapter();
            if(mBluetoothAdapter!=null){
                mBluetoothLeScanner=mBluetoothAdapter.getBluetoothLeScanner();
                Toast.makeText(this.getActivity(),"Bluetooth function start",Toast.LENGTH_SHORT);
                success=true;
            }
        }
        if(!success){
            Toast.makeText(this.getActivity(),"Cannot start bluetooth fuction",Toast.LENGTH_SHORT);
            getActivity().finishAndRemoveTask();
        }
    }
    private final ScanCallback startScanCallback=new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device=result.getDevice();
            ScanRecord mScanRecord=result.getScanRecord();
            String address=device.getAddress();
            byte[] content=mScanRecord.getBytes();
            int flag=mScanRecord.getAdvertiseFlags();
            int mRssi=result.getRssi();
            String dataS=byteArrayToHexString(content);
            if(address==null||address.trim().length()==0) return;
            mResultAdapter.addDevice(address,""+mRssi,dataS);
            mResultAdapter.notifyDataSetChanged();
        }
    };
    public static String byteArrayToHexString(byte[] bytes){
//        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
//        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
//        int v;
//        for (int j = 0; j < bytes.length; j++) {
//            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
//            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
//        }
//        return new String(hexChars);
        if (bytes == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte aData : bytes) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }








    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

}