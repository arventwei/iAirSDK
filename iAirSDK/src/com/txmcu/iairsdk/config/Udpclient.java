package com.txmcu.iairsdk.config;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;


import com.txmcu.iairsdk.config.iAirConstants;
import com.txmcu.iairsdk.wifi.WifiHotManager;


/**
 * APP和设备通讯连接类
 * app通过UDP协议和设备连接
 * 连接成功后，app发送WIFI 的SSID，密码等信息
 * 设备收到后，发送receive给app
 * app收到后，断开和设备的连接。
 * app连接服务器，查询是否设备注册成功，此过程会执行多次，
 * 查询成功后，添加设备，否则提示添加失败
 * @author Administrator
 *
 */
public class Udpclient {
	
	public static interface UdpclientOperations {

		/**
		 * @param init callback ,close wait dialog
		 */
		public void setState(boolean result,String exception);
		
		public void logudp(String msg);

		
	}
	Activity activity;
	XinStateManager xinMgr;
	public UdpclientOperations operations;
	//public Context contentView;
	private static String TAG = "Udpclient";
	public byte[] send_msg ;
	// private AsyncTask<Void, Void, Void> async_cient;
    
    public String recvingMsg;
    DatagramSocket ds = null;
    InetAddress receiverAddress = null;
    int stateCode = 0;
    String sn;
    String userid;
    WifiHotManager wifiHotM;
    
    CountDownTimer connectApTimer;
    CountDownTimer sendDataTimer;
    CountDownTimer querySnTimer;
    public Udpclient(XinStateManager xinstateMgr,UdpclientOperations opertion,Activity activity,WifiHotManager wifiM)
    {
    	this.xinMgr = xinstateMgr;
    	this.operations = opertion;
    	this.activity = activity;
    	this.wifiHotM = wifiM;
    	
    }
    public void destroy() {
		if(connectApTimer!=null)
			connectApTimer.cancel();
		if(sendDataTimer!=null)
			sendDataTimer.cancel();
		if(querySnTimer!=null)
			querySnTimer.cancel();
	}
    public void setSendWifiInfo(String ssid,String pwd,String auth_mode,String encryp_type,
    		String channel,String _sn,String _userid,String vsn,String homeId)
    {
    	//check input paramter
    	//if(_userid.length() > 20 )
    	//	_userid = _userid.substring(0, 20);
    	//sn 10
    	//userid:10
    	//vsn:20
    	//flag 1 ,0-home,1-vsn
    	//home or vsn 19 bytes
    	String  flag = "1";
    	if (homeId.length()>0) {
    		flag = "0";
		}
    	else if (vsn.length()>0){
    		flag = "1";
		}
    	
    	if(ssid.length()>20 ||pwd.length()>20)
    	{
    		operations.setState(false, "input ssid or pwd is wrong");
    		return;
    	}
    	if(_sn.length()>10)
    	{
    		operations.setState(false, "sn is to long");
    		return;
    	}
    	sn = _sn;
    	userid=_userid;
    	send_msg =  new byte[105];
    	int len=0;
    	operations.logudp("setwifi:"+ssid+"-"+pwd+"-"+auth_mode+"-"+encryp_type+"-"+channel);
    	

    	byte[] bytes =ssid.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=20;
    	bytes =pwd.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=20;
    	bytes =auth_mode.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=10;
    	bytes =encryp_type.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=10;
    	bytes =channel.getBytes();


    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=5;
    	
    	bytes =sn.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=10;
    	
    	bytes =userid.getBytes();
    	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=10;

    	//bytes = flag.getBytes();
    	
    	if (flag.equals("1")) {
    		send_msg[len]=1;len+=1;
    		bytes =vsn.getBytes();
    		
        	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=19;
		}
    	else {
    		send_msg[len]=0;len+=1;
    		bytes =homeId.getBytes();
        	System.arraycopy(bytes,0,send_msg,len,bytes.length);len+=19;
		}
    	
    	recvingMsg = "";
    	//stateCode=100;
    	
    	wifiHotM.connectToHotpot(iAirConstants.XIAOXIN_SSID, iAirConstants.XIAOXIN_PWD);
    	
    	
    	leftTime = totalTime;
    	postMessage(initApState);
    	//setStopLoop(0,"");
    	
    	
    	
    }
    static final  int initApState = 100;
    static final int sendState = 50;
    static final int queryState = 30;
    static final int endState = 10;
    
    static final long totalTime = 120*1000;
    long    leftTime = totalTime;

    
    void postMessage(int code)
    {
    	if (stateCode == code) {
			return;
		}
    	 Message tempMsg = msghandler.obtainMessage();
	       tempMsg.what = code;
	      // tempMsg.obj = msg;
	       msghandler.sendMessage(tempMsg);
    }
    
    @SuppressLint("HandlerLeak")
	Handler msghandler = new Handler()
    {   
        @SuppressLint("NewApi")
		public void handleMessage(Message msg)
        {  
            switch (msg.what)
            { 
            case initApState:
            {
            	stateCode=initApState;
            	/*
            	 * 每3秒间隔，检查状态
            	 */
            	connectApTimer = new CountDownTimer(leftTime, 3000) 
            	{

       		     public void onTick(long millisUntilFinished)
       		     {
       		    	 //initscanRetryTimes++;
       		    	 
       		    	 if(stateCode!=initApState)
       		    		 return;
       		    	 /*
       		    	  * 检查当前的WIFI是否是XIAOXIN_AP,如果不是，则继续尝试连接该AP,如果是，则直接转入下一个状态
       		    	  * 
       		    	  */
       		    	 
	       		    	leftTime = millisUntilFinished;
		       		    WifiInfo curWifi =wifiHotM.getConnectWifiInfo();
		       		    String reasonString="null";
		       		    if (curWifi!=null) {
							reasonString = curWifi.getSupplicantState().toString();
		       		    }
		       		    /*
		       		     * 输出日志，开始连接了
		       		     */
	       		    	operations.logudp(iAirConstants.XIAOXIN_SSID+" dis connected " + reasonString);
	       		    	
	       		    	Boolean isOkBoolean = false;
	       		    	/*
	       		    	 * 把当前的WIFI信息名字转换下，去掉引号
	       		    	 */
	       		    	String curSSIDString = "";
	       		        if( curWifi!=null && curWifi.getSSID()!=null)
	       		        {
	       		        	curSSIDString = curWifi.getSSID().replace("\"", "");
	       		        }
	       		        /*
	       		         * 当前连接是XIAOINX_AP,非常好，直接跳转下个状态
	       		         */
	       		        if (curSSIDString.equals(iAirConstants.XIAOXIN_SSID)
							&& curWifi!=null
							&& curWifi.getNetworkId()!=-1
							&& curWifi.getSupplicantState()== SupplicantState.COMPLETED)
	       		        {
	       		        	operations.logudp(iAirConstants.XIAOXIN_SSID+"  connected");
	      		    		 postMessage(sendState);
	      		    		 return;
						}
	       		        /*
	       		         * 当前没有任何WIFI信息，Ooops，继续连接吧。
	       		         */
	       		        if (curWifi==null ||curWifi.getNetworkId()!=-1) 
	       		        {
	       		        	wifiHotM.connectToHotpot(iAirConstants.XIAOXIN_SSID, iAirConstants.XIAOXIN_PWD);
						}
	       		        /*
	       		         * 什么情况，已经连上一个WIFI了，但不是我们想要的，断开吧
	       		         */
	       		        if (!curSSIDString.equals(iAirConstants.XIAOXIN_SSID)
	       		        		&& curWifi!=null
	       		        		&&curWifi.getSupplicantState()== SupplicantState.COMPLETED) 
	       		        {
	       		        	wifiHotM.disconnectWifi();
							//wifiHotM.removeWifiInfo(curWifi.getNetworkId());
						}
	       		    	 
	       		 }
       		     
            
       		     public void onFinish()
       		     {
       		    	 if(stateCode==initApState)
       		    	 {
       		    		operations.setState(false,"connect ap time out");
       		    	 }
       		    	
       		     }
       		  }.start();
       		  break;
            }
            case sendState:
            {
            	stateCode=sendState;
            	/*
            	 * 每隔3秒做一次检查
            	 */
            	sendDataTimer = new CountDownTimer(leftTime, 3000) {

       		     public void onTick(long millisUntilFinished) {
       		    	 //initscanRetryTimes++;
       		    	 
       		    	 if(stateCode!=sendState)
       		    		 return;
       		    	leftTime = millisUntilFinished;
       		    	 
       		    	 operations.logudp(iAirConstants.XIAOXIN_SSID+" send data");
       		    	 /*
       		    	  * 开启线程发送数据，如果不开线程，会阻塞主线程
       		    	  */
       		    	AsyncTask<Void, Void, Void>  async_cient = new AsyncTask<Void, Void, Void>() 
       		             {
       		                 @Override
       		                 protected Void doInBackground(Void... params)
       		                 {  
       		                 	try 
       		     	            {
       		                 		/*
       		                 		 * 设置一个超时为3秒的UDP包，这个和3秒一检查，是对应的。
       		                 		 */
       		                     	receiverAddress = InetAddress.getByName(iAirConstants.XIAOXIN_IP);
       		                         ds = new DatagramSocket();
       		                         ds.setSoTimeout(3000);
       		                         
       		                         /*
       		                          * 构造包的内容，主要是WIFI的SSID和WIFI密码等信息
       		                          */
       		                         DatagramPacket dp;                          
    		                         dp = new DatagramPacket(send_msg, send_msg.length,
    		                         		receiverAddress, iAirConstants.XIAOXIN_PORT);
    		                         ds.setBroadcast(true);
    		                         ds.send(dp);
    		                         /*
    		                          * 正常情况下，会收到一个receive。如果没有收到，则表示，没有正确发给设备。
    		                          */
    		                         byte[] receiveData = new byte[20];
       		     	            	 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
       		     	            	 ds.receive(receivePacket);
       		     	            	 recvingMsg = new String( receivePacket.getData());
       		     	            	 
       		     	            	if(recvingMsg.startsWith("receive"))
       		                 		{
       		                 			//非常好，设备收到了，我们吧状态切换下吧。进入下一个阶段
       		                 			postMessage(queryState);
       		                 			//xinMgr.restoreCurrentWifiState();
       		                 			//setStopLoop(1,"");
       		                 		}
       		     	            	 
       		     	            }
       		                 	catch (SocketException e) 
       		                     {
       		                 	//	setStopLoop(-1,e.toString());
       		                     } catch (UnknownHostException e) {
       		     					// TODO Auto-generated catch block
       		                    // 	setStopLoop(-1,e.toString());
       		     				} catch (IOException e) {
       		     					// TODO Auto-generated catch block
      		                     	 //setStopLoop(-3,e.toString());
      		     				}
       		                 	
       		                 	
       		                 	
       		                 
       		                    return null;
       		                 }
       		               

       		                 protected void onPostExecute(Void result) 
       		                 {
       		                    super.onPostExecute(result);
       		                 }
       		             };

       		             if (Build.VERSION.SDK_INT >= 11) 
       		             	async_cient.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
       		             else 
       		             	async_cient.execute();
       		    	// iAirUtil.setProgressText(getString(R.string.add_device_cooldown)+(millisUntilFinished / 1000)+getString(R.string.second));
       		         //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
       		     }

       		     public void onFinish() {
       		    	 if(stateCode==sendState)
       		    	 {
       		    		operations.setState(false,"send message xiaoxin time out");
       		    		
       		    	 }
       		    	
       		     }
       		  }.start();
       		 break; 
            }
                
            case queryState:
            {
            	/*
            	 * app和服务器开始交互了，
            	 * 到此为止，我们不再和设备连接了，
            	 * 我们发送SN给服务器，验证，这个SN的设备是否存在。
            	 */
            	stateCode=queryState;
            	querySnTimer = new CountDownTimer(leftTime, 4000) {

       		     public void onTick(long millisUntilFinished) {
       		    	 //initscanRetryTimes++;
       		    	 
       		    	 if(stateCode!=queryState)
       		    		 return;
       		    	 
       		    	leftTime = millisUntilFinished;
       		    	 
       		    	xinMgr.restoreCurrentWifiState();
       		    	
       		    	
       		    	
       		    	//
       		    	
       		    	XinServerManager.checkxiaoxin_exist(activity,userid,sn,new XinServerManager.onSuccess() {
       					
       					@Override
       					public void run(JSONObject response) throws JSONException {
       						
       						if(response.get("ret").equals("Ok"))
       						{
           						stateCode = endState;
    							operations.setState(true,"Ok");	
       						}

       						//application.homeList = XinServerManager.getHomeFromJson(MainActivity.this,response.getJSONArray("home"));
       						//application.cityList = XinServerManager.getCityFromJson(response.getJSONArray("area"));
       						// TODO Auto-generated method stub
       						//refreshlist();
       					//	synchomebb();
       						
       					}
       				});

       		    	
       		     }

       		     public void onFinish() {
       		    	 if(stateCode==queryState)
       		    	 {
       		    		operations.setState(false,"query sn time out");
       		    	 }
       		    	
       		     }
       		  }.start();
       		  break;
            }
            }      
            super.handleMessage(msg);  
        }  
          
    };
    
   
}
