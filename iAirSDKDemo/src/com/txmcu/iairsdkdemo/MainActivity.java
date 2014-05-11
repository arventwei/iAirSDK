package com.txmcu.iairsdkdemo;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.txmcu.iairsdk.config.XinStateManager;
import com.txmcu.iairsdk.config.XinStateManager.ConfigType;
import com.txmcu.iairsdk.config.XinStateManager.XinOperations;
public class MainActivity extends Activity implements XinOperations,OnClickListener {

	
	private static final String TAG = "DeviceAddActivity";

	XinStateManager xinMgr;
	EditText editSSIDEditText;
	EditText editPwdEditText;
	EditText editSnEditText;
	EditText editUserIdEditText;
	int cooldown;

	CountDownTimer timer;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//if (savedInstanceState == null) {
		//	getSupportFragmentManager().beginTransaction()
		//			.add(R.id.container, new PlaceholderFragment()).commit();
		//}
		
		((Button) findViewById(R.id.nextstep)).setOnClickListener(this);
		editSSIDEditText = (EditText) findViewById(R.id.input_ssid);
		
		editSSIDEditText.setOnClickListener(this);
		editPwdEditText = (EditText) findViewById(R.id.input_pwd);
		editSnEditText = (EditText) findViewById(R.id.input_sn);
		editUserIdEditText= (EditText) findViewById(R.id.input_uid);
		showProgressDialog(this);
		xinMgr = XinStateManager.getInstance(this);
		xinMgr.Init();
	}

	@Override
	protected void onDestroy() {
		xinMgr.Destroy();
		xinMgr = null;
		
		if(timer!=null)
			timer.cancel();
		
		super.onDestroy();
		// Log.v(TAG, "onDestroy");

	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.nextstep) 
		{
			//TODO...
			xinMgr.Config(editSSIDEditText.getText().toString(),
					editPwdEditText.getText().toString(),
					editSnEditText.getText().toString(),
					editUserIdEditText.getText().toString()
					,"","");
			
			showProgressDialog(this, getString(R.string.setting)
					, getString(R.string.add_device_cooldown)+120+getString(R.string.second)
					,new DialogInterface.OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							// TODO Auto-generated method stub
							finish();
						}
					});


			
			//timer.schedule(task, 1000,1000);
			if(timer!=null)
			{
				timer.cancel();
			}
			
			timer = new CountDownTimer(30000*4, 1000) {

			     public void onTick(long millisUntilFinished) {
			    	 setProgressText(getString(R.string.add_device_cooldown)+(millisUntilFinished / 1000)+getString(R.string.second));
			         //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
			     }

			     public void onFinish() {
			    	 dismissDialog();
			    	// iAirUtil.toastMessage(DeviceAddActivity.this, getString(R.string.add_device_failed));
			         //mTextField.setText("done!");
			     }
			  }.start();
			Log.i(TAG, "start config");
		}
		else if(view.getId() == R.id.input_ssid)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.please_select_your_wifi);
			
			if (scannedlist==null) {
				return;
			}

			
//			String [] listStrings= new String[scannedlist.size()];
//			for (int i = 0; i < scannedlist.size(); i++) {
//				listStrings[i]=scannedlist.get(i);
//			}
//			Map<String,String> item = new HashMap<String, String>();
//			List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
//			
//			for (ScanResult sResult : scannedlist) {
//				item.put(sResult.SSID, String.valueOf(sResult.level)+"%");
//			}
//			dataList.add(item);
			ListView modeList = new ListView(this);
//			for (String string : listStrings) {
//				
//			}
		//	String[] stringArray =listStrings;//new String[] { "Bright Mode", "Normal Mode" };
			final WifiAdapter modeAdapter = new WifiAdapter(this,scannedlist);
			
			modeList.setAdapter(modeAdapter);


			builder.setView(modeList);
			final Dialog dialog = builder.create();
			modeList.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		     {
				@Override
				public void onItemClick(AdapterView parent, View view,int position, long id){
					
					ScanResult ssid =(ScanResult) modeAdapter.getItem(position);
					String ssidString = ssid.SSID.replace("\"", "");
					
					editSSIDEditText.setText(ssidString);
					//List<String> detailList = xinMgr.getWifiDetailInfo(ssidString);
					dialog.dismiss();
				}
		     });
			dialog.show();


			
		}
		
	}

	List<ScanResult> scannedlist = new ArrayList<ScanResult>();
	@Override
	public void initResult(boolean result, String SSID,List<ScanResult> scanList) {
		// TODO Auto-generated method stub
		editSSIDEditText.setText(SSID);
		dismissDialog();
		scannedlist = scanList;
		if (scannedlist==null) {
			return;
		}
	}

	@Override
	public void configResult(ConfigType type) {
		// TODO Auto-generated method stub
		dismissDialog();
		if (type == ConfigType.Succeed) {
			Toast.makeText(this, "Add Ok", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(this, "Add Failed", Toast.LENGTH_LONG).show();
		}
		
	}

	@Override
	public void log(String msg) {
		// TODO Auto-generated method stub
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	
	
	private static ProgressDialog mProgressDialog;
	
	public static final void showProgressDialog(Context context)
	{
		showProgressDialog(context,"","");
	}
	public static final void showProgressDialogCancelable(Context context)
	{
		showProgressDialog(context,"","",new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public static final void showProgressDialog(Context context, String title,
			String message) 
	{
		dismissDialog();
		if (TextUtils.isEmpty(title)) {
			title = context.getString(R.string.wait_moment);
		}
		if (TextUtils.isEmpty(message)) {
			message = context.getString(R.string.now_loading);
		}
		mProgressDialog = ProgressDialog.show(context, title, message);
	}
	public static final void showProgressDialog(Context context, String title,
			String message, DialogInterface.OnCancelListener cancelListener)
	{
		dismissDialog();
		if (TextUtils.isEmpty(title)) {
			title = context.getString(R.string.wait_moment);
		}
		if (TextUtils.isEmpty(message)) {
			message = context.getString(R.string.now_loading);
		}
		mProgressDialog = ProgressDialog.show(context, title, message,false,true,cancelListener);
		
	}
	
	public static final void dismissDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	public static final void setProgressText(String text) {
		if (mProgressDialog != null) {
			mProgressDialog.setMessage(text);
			
		}
	}
	

}
