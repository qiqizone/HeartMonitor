package com.ustc.engineering.lab410.HeartMonitor.ui.main;




import com.ustc.engineering.lab410.HeartMonitor.ui.widget.CustomToast;
import com.ustc.engineering.lab410.HeartMonitor.R;

//import android.support.v4.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class HomeFragment extends Fragment{
//
//	  @Override
//	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	      Bundle savedInstanceState) {
//	    
//	    return inflater.inflate(R.layout.home_fragment,null);		
//	  }	
//}



import com.ustc.engineering.lab410.HeartMonitor.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class HomeFragment extends Fragment implements OnClickListener{

   private Button On,Off,Visible,searchList;
   static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
   private UUID uuid ;
   private BluetoothAdapter mBluetoothAdapterBA;
   private Set<BluetoothDevice>pairedDevices;
   private ListView lv;
   private View mRootView = null;
   private Context mContext;
   private List<BluetoothDevice> deviceList;
   private List<String> lstDevices = new ArrayList<String>();	
   private ArrayAdapter<String> adtDevices;
	private RecordFragment recordFragment;

   private final String lockName = "BOLUTEK";
	

   public void on(View view){
	   mBluetoothAdapterBA = BluetoothAdapter.getDefaultAdapter();
      if (!mBluetoothAdapterBA.isEnabled()) {
         Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(turnOn, 0);
         CustomToast.showToast(mContext, "������", CustomToast.LENGTH_LONG);
      }
      else{
    	  CustomToast.showToast(mContext, "�����Ѿ���", CustomToast.LENGTH_LONG);
         }
   }
   public void searchList(View view){

//      pairedDevices = mBluetoothAdapterBA.getBondedDevices();
//      
//      ArrayList list = new ArrayList();
//      for(BluetoothDevice bt : pairedDevices)
//         list.add(bt.getName());
//
//      CustomToast.showToast(mContext, "��������豸", CustomToast.LENGTH_LONG);
//      
//      final ArrayAdapter adapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1);
//      lv.setAdapter(adapter);
//	   getActivity().setProgressBarIndeterminateVisibility(true);  
//       getActivity().setTitle("����ɨ��....");  
      if (mBluetoothAdapterBA.getState() == BluetoothAdapter.STATE_OFF) {// ���������û����
			CustomToast.showToast(mContext, "���ȴ�����", CustomToast.LENGTH_LONG);
			return;
		}
		getActivity().setTitle("����������ַ��" + mBluetoothAdapterBA.getAddress());
//		lstDevices.clear();
		mBluetoothAdapterBA.startDiscovery();
   }
   
   public void off(View view){
	  mBluetoothAdapterBA.disable();
      CustomToast.showToast(mContext, "�ر�����", CustomToast.LENGTH_LONG);
   }
   
   public void visible(View view){
      Intent getVisible = new Intent(BluetoothAdapter.
      ACTION_REQUEST_DISCOVERABLE);
      getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600Ϊ�����豸�ɼ�ʱ��
      startActivityForResult(getVisible, 0);
   }
	   

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (mRootView == null) {
			mRootView = inflater.inflate(R.layout.record_fragment,
					container, false);
			initView();
		}
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		return mRootView;
	}

	private void initView() {
		// TODO Auto-generated method stub
	
	    On = (Button) mRootView.findViewById(R.id.on);
	    On.setOnClickListener(this);
	    Visible = (Button)mRootView.findViewById(R.id.set_visible);
	    Visible.setOnClickListener(this);
	    searchList = (Button)mRootView.findViewById(R.id.search_list);
	    searchList.setOnClickListener(this);
	    Off = (Button)mRootView.findViewById(R.id.off);
	    Off.setOnClickListener(this);
	    lv = (ListView)mRootView.findViewById(R.id.listView1);
	    mContext = mRootView.getContext();
	    adtDevices = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, lstDevices);
	    lv.setAdapter(adtDevices);
	    lv.setOnItemClickListener(new ItemClickEvent());
	    mBluetoothAdapterBA = BluetoothAdapter.getDefaultAdapter();// ��ʼ��������������
		uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		
		if(mBluetoothAdapterBA == null){
			getActivity().finish();
			return;
		}
		else{
		
			if (mBluetoothAdapterBA.getState() == BluetoothAdapter.STATE_OFF)// ��ȡ����״̬����ʾ
				{
					CustomToast.showToast(mContext, "������δ��,��������ȴ�����", CustomToast.LENGTH_LONG);
				}
			else if (mBluetoothAdapterBA.getState() == BluetoothAdapter.STATE_ON){
			
				//����˼���
//				serverThread=new AcceptThread();
//				serverThread.start();
				
			}
	

		
		}	
	}
	
	
	class ItemClickEvent implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (mBluetoothAdapterBA.getState() != BluetoothAdapter.STATE_ON) {// ���������û����
				CustomToast.showToast(mContext, "�뿪������", CustomToast.LENGTH_LONG);
				return;
			}

			if (mBluetoothAdapterBA.isDiscovering())
				mBluetoothAdapterBA.cancelDiscovery();
			String str = lstDevices.get(arg2);
			if (str == null | str.equals(""))
				return;
			String[] values = str.split("\\|");
			String address = values[1];
			Log.e("onItemClick:value", values[1]);
			
//		
//			if(null == recordFragment){//���Ա����л���ʱ���ظ�����
//				recordFragment = new RecordFragment();
//			}
//			if(!recordFragment.isAdded()){
//				Bundle data = new Bundle();
//				data.putString("NAME", values[0]);
//				data.putString("MAC", values[1]);
//				recordFragment.setArguments(data);
//				MainActivity.mTabHost.setCurrentTab(1);
//				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//				fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
//				fragmentTransaction.add(R.id.tab_content_layout,recordFragment);
//				fragmentTransaction.addToBackStack(null);
//				fragmentTransaction.commitAllowingStateLoss();
//			}
			
//			MainActivity.setTabStyle(false, 0);
//			
			
//			MainActivity.mTxt_home.setTextColor(Color.BLACK);
//			MainActivity.mImg_home.setImageResource(MainActivity.mNorDrawable[0]);
//			
//			MainActivity.mTxt_record.setTextColor(Color.WHITE);
//			MainActivity.mImg_record.setImageResource(MainActivity.mPreDrawable[1]);
//            
//			RecordFragment recordFragment = new RecordFragment();
//			FragmentTransaction transaction =getFragmentManager().beginTransaction();
//			transaction.replace(R.id.tab_content_layout,recordFragment);
//			transaction.commit();
			
//			try {
//				Intent intMain = new Intent(mContext, RecordFragment.class);
//            	Bundle bd = new Bundle();
//            	bd.putString("NAME", values[0]);
//            	bd.putString("MAC", values[1]);
//            	intMain.putExtras(bd);
//				startActivity(intMain);
//			} catch (Exception e) {
//				Log.d("onItemClick:", "Error connected to: " + address);
//				e.printStackTrace();
//			}

		}

	}

	
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Bundle b = intent.getExtras();
			Object[] lstName = b.keySet().toArray();

			// ��ʾ�����յ�����Ϣ����ϸ��
			for (int i = 0; i < lstName.length; i++) {
				String keyName = lstName[i].toString();
				Log.e(keyName, String.valueOf(b.get(keyName)));
			}
			//�����豸ʱ��ȡ���豸��MAC��ַ
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String str= device.getName() + "|" + device.getAddress();
				
				if (lstDevices.indexOf(str) == -1)// ��ֹ�ظ����
					lstDevices.add(str); // ��ȡ�豸���ƺ�mac��ַ
				if (lstDevices.indexOf("null|" + device.getAddress()) != -1)
					lstDevices.set(lstDevices.indexOf("null|" + device.getAddress()), str);
				adtDevices.notifyDataSetChanged();
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				searchList.setText("����ɨ��");
				searchList.setTextColor(Color.RED);
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				searchList.setText("ɨ���豸");
				searchList.setTextColor(Color.BLACK);
				CustomToast.showToast(mContext, "ɨ����ɣ�����б��е��豸����������", CustomToast.LENGTH_LONG);
			}
		}
	};


	
//	  @Override  
//	    public void onAttach(Activity activity) {  
//			
//	        super.onAttach(activity);  
//	    }  
	  
	  @Override  
	  public void onResume() {
		// ע��Receiver����ȡ�����豸��صĽ��
					IntentFilter intent = new IntentFilter();
					intent.addAction(BluetoothDevice.ACTION_FOUND);// ��BroadcastReceiver��ȡ���������
					intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
					intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
					intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
					getActivity().registerReceiver(searchDevices, intent);
					super.onResume();
	  }
	  
	   /**
     *ע���㲥
     * */  
    @Override  
    public void onDestroyView() {  
       getActivity().unregisterReceiver(searchDevices);  
       super.onDestroyView();  
    }  


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.on:
			on(v);
			Log.d("onClick","button1");
			break;
		case R.id.set_visible:
			visible(v);
			Log.d("onClick","button2");
			break;
		case R.id.search_list:
			searchList(v);
			Log.d("onClick","button3");
			break;
		case R.id.off:
			off(v);
			Log.d("onClick","button4");
			break;
		default:
			break;
		}	
	}


 
    
}