package com.txmcu.iairsdk.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.txmcu.iairsdk.wifi.WifiHotManager.OpretionsType;
import com.txmcu.iairsdk.wifi.WifiHotManager.WifiBroadCastOperations;

public class WifiStateBroadCast extends BroadcastReceiver {

	private WifiBroadCastOperations operations;

	private OpretionsType type;

	private String SSID;
    private String pWDString;
	public WifiStateBroadCast(WifiBroadCastOperations operations, String SSID,String pwd) {

		this.operations = operations;
		this.SSID = SSID;
		this.pWDString = pwd;
	}

	public void setOpType(OpretionsType type) {
		this.type = type;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			// 
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			Log.i("onReceive","wifiState" + wifiState);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_DISABLED:
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				break;
			case WifiManager.WIFI_STATE_ENABLED:
				if (type != null) {
					operations.operationByType(type, SSID,pWDString);
				}
				break;
			}
		}

	}

}
