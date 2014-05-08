package com.txmcu.iairsdk.config;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.CountDownTimer;
import android.util.Log;

import com.txmcu.iairsdk.R;
import com.txmcu.iairsdk.wifi.WifiHotManager;
import com.txmcu.iairsdk.wifi.WifiHotManager.OpretionsType;
import com.txmcu.iairsdk.wifi.WifiHotManager.WifiBroadCastOperations;

/**
 * 设备状态管理器
 * @author Oliver
 *
 */
public class XinStateManager 
implements WifiBroadCastOperations , Udpclient.UdpclientOperations{
	
	private Activity context;
	private XinOperations operations;
	private WifiHotManager wifiHotM;
	private static XinStateManager instance = null;
	private Udpclient udpclient = null;
	
	static String TAG = "XinStateManager";
	
	//private iAirApplication application;
	
	public enum ConfigType {
		Succeed,
		Failed,
		//Failed_Connect_XiaoXin,
	//	Failed_TimeOut,
		//Failed_XiaoXinConfig
	}
	public enum State
	{
		Init,
		//Scaned,
		Config,
	}
	public State mCurState = State.Init;
	public static int TimeOutSecond = 120;
	
	/**
	 * 回调实现的接口函数，
	 * @author Administrator
	 *
	 */
	public static interface XinOperations {

		/**
		 * @param init callback ,close wait dialog
		 */
		public void initResult(boolean result,String curSSID,List<ScanResult> SSID);

		/**
		 * @param invoke callback
		 */
		public void configResult(ConfigType type );
		
		public void log(String msg);
	}
	public static void destroy() {
		instance = null;
	}
	public static XinStateManager getInstance(Activity context) {

		if (instance == null) {
			instance = new XinStateManager(context,(XinOperations) context);

		}
		return instance;
	}


	/**
	 * 构造函数，如果当前的WiFi是设备的XIAOXIN_AP，则删除这个WiFi。避免用户WIFI状态切换错误
	 * @param context
	 * @param operations
	 */
	private XinStateManager(Activity context,XinOperations operations) {
		this.context = context;
		this.operations = operations;
		//this.application = ((iAirApplication)context.getApplication());
		wifiHotM = WifiHotManager.getInstance(this.context, XinStateManager.this);
		WifiInfo curWifiInfo = wifiHotM.getConnectWifiInfo();
		if(curWifiInfo!=null&&curWifiInfo.getSSID()!=null&&curWifiInfo.getSSID().endsWith(iAirConstants.XIAOXIN_SSID))
		{
			wifiHotM.removeWifiInfo(curWifiInfo.getNetworkId());
		}
		if (curWifiInfo!=null&&curWifiInfo.getNetworkId()==-1)
		{
			operations.log("no wifi connection");
			//iAirUtil.toastMessage(context, context.getString(R.string.add_device_no_wifi));
			
		}
		//wifiHotM.scanWifiHot();
	}
	CountDownTimer initCoolTimer;
	int initscanRetryTimes=0;
	public void Init()
	{
		mCurState = State.Init;
		
		udpclient = new Udpclient(this,this,context,wifiHotM);
		//operations.log("Init");
		
		startScan();
		//udpclient.operations= this;
		//backupCurrentWifiState();
	}
	/**
	 * 开始扫描WIFI列表
	 */
	private void startScan() {
		
		
		WifiInfo curWifiInfo = wifiHotM.getConnectWifiInfo();
		if(curWifiInfo!=null&&curWifiInfo.getSSID()!=null&&curWifiInfo.getSSID().endsWith(iAirConstants.XIAOXIN_SSID))
		{
			wifiHotM.removeWifiInfo(curWifiInfo.getNetworkId());
		}
		_scannlist.clear();
		wifiHotM.scanWifiHot();
		initscanRetryTimes=0;
		if (initCoolTimer!=null) {
			initCoolTimer.cancel();
		}
		

		//operations.initResult(true, curSSID, SSID)
		
		initCoolTimer = new CountDownTimer(8000, 3000) {

		     public void onTick(long millisUntilFinished) {
		    	 initscanRetryTimes++;
		    	 
		    	 if(mCurState == State.Init&&_scannlist.size()==0)
		    	 {
		    		 operations.log("times"+initscanRetryTimes+" left:"+millisUntilFinished/1000);
		    		// if()
		    		// {
		    			// operations.log("try max times:"+initscanRetryTimes);
		    			// operations.configResult(ConfigType.Failed);
		    			 //initCoolTimer.cancel();
		    		// }
		    			 
		    		 initscanRetryTimes++;
		    		 operations.log("scan timeout retry"+initscanRetryTimes);
		    		 wifiHotM.unRegisterWifiScanBroadCast();
		    		 wifiHotM.scanWifiHot();
		    		
		    	 }
		    	// iAirUtil.setProgressText(getString(R.string.add_device_cooldown)+(millisUntilFinished / 1000)+getString(R.string.second));
		         //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
		     }

		     public void onFinish() {
		    	 
		    	 
		    	 if(_scannlist.size()==0)
		    	 {
		    			List<ScanResult> apList = wifiHotM.getRecentScanResults();
		    			backupCurrentWifiState(apList);
		    			
		    		 //operations.log("times ok");
		    		// operations.initResult(false,"",null);
		    	 }
		    	 

		    	 //iAirUtil.toastMessage(DeviceAddActivity.this, getString(R.string.add_device_failed));
		         //mTextField.setText("done!");
		     }
		  }.start();
	}

/**
	 * 用户开始配置设备，此过程需要120秒。
 * 
 * @param SSID
 * @param Pwd
 * @param _userid
 * @param _sn
 * @param vsn
 * @param homeId
 */
	public void Config(String SSID,String Pwd,String _sn,String _userid)
	{
		mCurState = State.Config;
		SSID = SSID.replace("\"", "");
		//application.setWifibackupPwd(Pwd);
		//wifibackupPwd = Pwd;
		userid=_userid;
		sn=_sn;
		
		//if(mCurState == State.Config)
		//{
		List<String> detailinfo = getWifiDetailInfo(SSID);
		udpclient.setSendWifiInfo(SSID, Pwd,
				detailinfo.get(0), detailinfo.get(1),
					detailinfo.get(2),sn,userid);
			
			//udpclient.Looper();
		//}
		//
	}
	public void Destroy()
	{
		restoreCurrentWifiState();
		wifiHotM.unRegisterWifiConnectBroadCast();
		wifiHotM.unRegisterWifiScanBroadCast();
		wifiHotM.unRegisterWifiStateBroadCast();
		WifiHotManager.destroy();
		XinStateManager.destroy();
		
		if (initCoolTimer!=null) {
			initCoolTimer.cancel();
		}
		udpclient.destroy();
		
	}

	
	/**
	 * 获取WIFI的详细信息，如加密方式，加密算法和通道信息
	 * @param ssid
	 * @return
	 */
	public List<String> getWifiDetailInfo(String ssid)
	{
		String channel = "6";
		String AuthMode="OPEN";
		String EncrypType="None";
		
		//application.setWifibackupChannel("6");
		//wifibackupChannel = "6";
		for (ScanResult scanRet : _scannlist) 
		{
			String scanSSIDString = scanRet.SSID.replace("\"", "");
			if (scanSSIDString.equals(ssid))
			{
				int  ch =  getChannel(scanRet.frequency);
				channel = String.valueOf(ch);
				logudp(scanRet.capabilities);
				
				if (scanRet.equals("[ESS]"))
				{//OPEN None pwd
					 AuthMode="OPEN";
					 EncrypType="None";
				}
				else
				{
					if(scanRet.capabilities.contains("WPA") && !scanRet.capabilities.contains("WPA2"))  
	                 {  
						 AuthMode ="WPAPSK";
	              
	                 }  
					 else if(scanRet.capabilities.contains("WPA2"))  
	                 {  
						 AuthMode ="WPA2PSK";
	             
	                 }  
					 else if(scanRet.capabilities.contains("WEP"))   {
						 
						 
						 EncrypType="WEP";
						 if(scanRet.capabilities.contains("ESS"))   {
							 AuthMode ="OPEN";
						 }
						 else {
							 AuthMode ="SHARED";
						}
					}
					 
	                 if(scanRet.capabilities.contains("TKIP"))  
	                 {  
	                	 EncrypType="TKIP";
	                 }  
	                 if(scanRet.capabilities.contains("CCMP"))  
	                 {  
	                	 EncrypType="AES"; 
	                 }  
				}
				break;
			}
		}
		
		//List<String> authInfo = wifiHotM.getAuthMode(ssid);
		List<String> ret = new ArrayList<String>();
		
		ret.add(AuthMode);
		ret.add(EncrypType);
		ret.add(channel);
		return ret;
	}
	/**
	 * 检测ssid是否在list中
	 * @param list
	 * @param ssid
	 * @return
	 */
	Boolean isWifiContain(List<ScanResult> list,String ssid)
	{
		for (ScanResult scanResult : list) {
			if (scanResult.SSID.equals(ssid)) {
				return true;
			}
		}
		return false;
	}
	String userid;
	String sn;
	List<ScanResult> _scannlist = new ArrayList<ScanResult>();
	int wifiBackupNetId = -1;

/**
	 * 备份当前已经连接的WIFI，连接完设备后，需要改回当前的配置
 * @param scannlist
 */
	private void backupCurrentWifiState(List<ScanResult> scannlist ) 
	{
		
		WifiInfo info = wifiHotM.getConnectWifiInfo();
		//wifibackupNetId=-1;
		//_scannlist = scannlist;
		for (ScanResult scanResult : scannlist) {
			if (!scanResult.SSID.equals(iAirConstants.XIAOXIN_SSID))
			{
				if(!isWifiContain(_scannlist,scanResult.SSID))
					_scannlist.add(scanResult);
			}
		}
		//application.setWifibackupSSID("");
		String curSSIDString = "";
		wifiBackupNetId = -1;
		//application.setWifibackupNetId(-1);
		if (info!=null&&info.getSSID()!=null&&!info.getSSID().equals(iAirConstants.XIAOXIN_SSID)) {
			wifiBackupNetId = info.getNetworkId();
			//application.setWifibackupNetId(info.getNetworkId());
			curSSIDString = info.getSSID().replace("\"", "");
			
		}
		
		operations.initResult(true,curSSIDString,_scannlist);
		
	}

/**
	 * 还原之前备份的设备信息
 */
	public void restoreCurrentWifiState() {
		//operations.log("restoreCurrentWifiState");
		if(wifiBackupNetId!=-1)
			wifiHotM.enableNetWorkById(wifiBackupNetId);
	}
	// wifi scan callback
	@Override
	public void disPlayWifiScanResult(List<ScanResult> wifiList) {

		wifiHotM.unRegisterWifiScanBroadCast();
		Log.i(TAG, " scan: = " + wifiList);
		operations.log("disPlayWifiScanResult"+wifiList.size());
		if(mCurState == State.Init)
		{
			//mCurState = State.Scaned;
			backupCurrentWifiState(wifiList);
			//if (application.getWifibackupSSID().length()>0) {
			
			//}
		}
		
		
		
		
		//wifiHotM.enableNetwork(SSID, password)
	}
	
	// wifi connect callback
	@Override
	public boolean disPlayWifiConResult(boolean result, WifiInfo wifiInfo) {

		Log.i(TAG, "disPlayWifiConResult");
		//operations.log("disPlayWifiConResult"+result+wifiInfo);
		wifiHotM.unRegisterWifiConnectBroadCast();


		

		return false;
	}

	// wifi connect & scan ,when wifi enable
	@Override
	public void operationByType(OpretionsType type, String SSID,String pwd) {
		Log.i(TAG, "operationByType!type = " + type);

		operations.log("operationByType!type = " + type);
		if (type == OpretionsType.SCAN) {
			//wifiHotM.scanWifiHot();
			startScan();
		}
		else {
			wifiHotM.connectToHotpot(SSID, pwd);
		}
	}
	@Override
	public void setState(boolean result, String exception) {
		// TODO Auto-generated method stub
		restoreCurrentWifiState();
		//operations.log(" setState result:"+exception);
		if (result && exception.startsWith("Ok")) {
			operations.configResult(ConfigType.Succeed);
		}
		else {
			operations.configResult(ConfigType.Failed);
		}
	}
	@Override
	public void logudp(String msg) {
		// TODO Auto-generated method stub
		operations.log("udp:" + msg);
	}
	
	
	
	 public static int getChannel(int frequency)
	    {
	            int channel = 0;
	            switch(frequency){
	            case 2412:
	                    channel = 1;
	                    break;
	            case 2417:
	                    channel = 2;
	                    break;
	            case 2422:
	                    channel = 3;
	                    break;
	            case 2427:
	                    channel = 4;
	                    break;
	            case 2432:
	                    channel = 5;
	                    break;
	            case 2437:
	                    channel = 6;
	                    break;
	            case 2442:
	                    channel = 7;
	                    break;
	            case 2447:
	                    channel = 8;
	                    break;
	            case 2452:
	                    channel = 9;
	                    break;
	            case 2457:
	                    channel = 10;
	                    break;
	            case 2462:
	                    channel = 11;
	                    break;
	            case 2467:
	                    channel = 12;
	                    break;
	            case 2472:
	                    channel = 13;
	                    break;
	            case 2484:
	                    channel = 14;
	                    break;
	            }
	            return channel;
	    }
	    
}
