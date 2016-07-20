package kr.co.wikibook.my_bluetooth_app;

import java.util.ArrayList;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchBluetoothDeviceActivity extends ListActivity {
	
	private ArrayAdapter<String> mArrayAdapter;
	private ArrayList<BluetoothDevice> mBluetoothDeviceList;
	
	BluetoothAdapter mBluetoothAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search_bluetooth_device_activity);
	    
	    ArrayList<String> arrayList = new ArrayList<String>();
	    
	    mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
	    setListAdapter(mArrayAdapter);
	    
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    mBluetoothDeviceList = new ArrayList<BluetoothDevice>();
	    
	    if (mBluetoothAdapter == null) {
	    	finish();
	    }
	    
	    if (!mBluetoothAdapter.isEnabled()) {
	    	Intent it = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    	startActivityForResult(it, MyBluetoothAppActivity.REQUEST_ENABLE_BT);
	    }
	    
	    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    if (pairedDevices.size() > 0) {
	    	for (BluetoothDevice device : pairedDevices) {
		    	mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		    	mBluetoothDeviceList.add(device);
	    	}
	    }
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		super.onListItemClick(parent, v, position, id);
		
		BluetoothDevice device = mBluetoothDeviceList.get(position);
		
		Intent it = new Intent();
		it.putExtra("device", device);
		setResult(MyBluetoothAppActivity.SEARCH_BT_DEVICE, it);
		
		finish();
	}
}