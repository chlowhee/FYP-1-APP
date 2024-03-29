package com.example.jasiribrain.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

import com.example.jasiribrain.R;
import com.example.jasiribrain.data.Constants;
import com.example.jasiribrain.common.OnRecyclerViewInteractedListener;
import com.example.jasiribrain.databinding.FragmentBtDeviceBinding;

public class BTDeviceFragment extends Fragment implements OnRecyclerViewInteractedListener {
    private static final String TAG = "BT Fragment";
    private static final int DURATION_ONE_SEC = 1000;
    private BluetoothAdapter btAdapter;
    private BTDeviceAdapter btDeviceAdapter;

    private FragmentBtDeviceBinding binding;

    // progress count down
    Handler handler = new Handler();
    int curTimerSec;
    Runnable runnableScanCountdown = new Runnable() {
        @Override
        public void run() {
            if (++curTimerSec > Constants.SCAN_DURATION_SEC){
                doEndScanning();
            } else {
                updateSubtitle(getString(R.string.discovering, (Constants.SCAN_DURATION_SEC-curTimerSec)));
                handler.postDelayed(this, DURATION_ONE_SEC);
            }
        }
    };

    private void updateSubtitle(String subtitle){
        if (binding.btDiscoveringSubtitle.getVisibility() == View.INVISIBLE) {
            binding.btDiscoveringSubtitle.setVisibility(View.VISIBLE);
        }
        binding.btDiscoveringSubtitle.setText(subtitle);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentBtDeviceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        btDeviceAdapter = new BTDeviceAdapter();
        btDeviceAdapter.setOnRecyclerViewInteractListener(this);

        binding.rvBtDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBtDevices.setAdapter(btDeviceAdapter);

        refreshPairDevices();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRefreshBtn();
        initExitBtn();
    }

    @Override
    public void onViewInteracted(View itemView, RecyclerView.ViewHolder holder, int action) {
        BluetoothDevice device = (BluetoothDevice) itemView.getTag();
        Log.d(TAG, String.format("click on %s(%s)", device.getAddress(), device.getName()!=null? device.getName():"unknown"));
        pickDevice(device);
    }

    private void pickDevice(BluetoothDevice device){
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_DEVICE_ADDRESS, device.getAddress());

        // Set result and finish this Activity
        requireActivity().setResult(Activity.RESULT_OK, intent);
        requireActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        doEndScanning();
    }

    private void initRefreshBtn() {
        binding.refreshBtn.setOnClickListener(v -> doRefresh());
    }

    private void initExitBtn() {
        binding.btDeviceExitBtn.setOnClickListener(v ->
                requireActivity().finish()
            );
    }

    private void doRefresh(){
        if (btAdapter == null){
            return;
        }
        refreshPairDevices();
        doScanningDevices();
    }

    private void refreshPairDevices() {
        if (btAdapter ==null){
            return;
        }
        btDeviceAdapter.clearPaired();
        Set<BluetoothDevice> set  = btAdapter.getBondedDevices();
        if (!set.isEmpty()) {
            btDeviceAdapter.setDevicesPaired(set);
        }
    }

    private BtDeviceFoundReceiver deviceFoundReceiver;
    private void doEndScanning(){
        updateSubtitle(null);
        handler.removeCallbacks(runnableScanCountdown);
        if (btAdapter!= null && btAdapter.isDiscovering()){
            Log.d(TAG, "End bluetooth scanning");
            btAdapter.cancelDiscovery();

            if (deviceFoundReceiver != null){
                requireActivity().unregisterReceiver(deviceFoundReceiver);
                deviceFoundReceiver = null;
            }
        }
    }
    private void doScanningDevices(){
        doEndScanning();

        // start timer
        handler.postDelayed(runnableScanCountdown, DURATION_ONE_SEC);
        curTimerSec = 0;
        updateSubtitle(getString(R.string.discovering, Constants.SCAN_DURATION_SEC));

        btDeviceAdapter.clearNearby();
        if (deviceFoundReceiver == null){
            deviceFoundReceiver = new BtDeviceFoundReceiver();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            requireActivity().registerReceiver(deviceFoundReceiver, filter);
        }
        btAdapter.startDiscovery();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                if (btAdapter.isEnabled()) {
                    refreshPairDevices();
                }
            }
        }
    }

//    private void checkPermissionForScanDevice(){
//        if (Utility.checkPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)){
//            doScanningDevices();
//        } else {
//            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)){
//                DialogUtil.promptPermissionForScanDevice(getContext(), (dialog, which) -> {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION);
//                });
//            } else {
//                requestPermissions( new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_LOCATION_PERMISSION );
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> granted = new ArrayList<>();
        ArrayList<String> denied  = new ArrayList<>();
        String permission;
        for (int i=0; i<permissions.length; i++){
            permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                granted.add(permission);
            } else {
                denied.add(permission);
            }
        }

        // check permission result for scanning
        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length>0 && denied.size() == 0 ) {
                doScanningDevices();
            }
        }

        if (requestCode == Constants.REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                doScanningDevices();
            }
        }
    }

    Toast toast;
    private void showToast(String message){
        if (toast!=null){
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    private class BtDeviceFoundReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "[BT device] addr: " + device.getAddress() + "name: " + device.getName());
                btDeviceAdapter.addNearby(device);
            }
        }
    }
}