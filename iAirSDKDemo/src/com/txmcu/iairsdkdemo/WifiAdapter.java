package com.txmcu.iairsdkdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;
/**
 * WIFI列表信息
 * @author Administrator
 *
 */
public class WifiAdapter extends BaseAdapter
{

	
	private List<ScanResult> wifilist = new ArrayList<ScanResult>();;
	
	
	Activity activity;
	
	
	public WifiAdapter(Activity activity,List<ScanResult>scanResults)
	{
		this.activity = activity;
		this.wifilist = scanResults;
	}
	
//	public  void addDevice(String sn) {
//		Device book = new Device();
//    	book.setId(index);
//    	book.setSn(sn);
//    	//book.setBitmapId(R.drawable.b001);
//    	devices.add(book);
//	}
	
	
	@Override
	public int getCount() {
		//return 5;
		return wifilist.size();
	}

	@Override
	public Object getItem(int position) {
		return wifilist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return  0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//if (null == convertView) {
		ScanResult sResult = wifilist.get(position);
		TwoLineListItem twoLineListItem;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            twoLineListItem = (TwoLineListItem) inflater.inflate(
                    android.R.layout.simple_list_item_2, null);
        } else {
            twoLineListItem = (TwoLineListItem) convertView;
        }

        TextView text1 = twoLineListItem.getText1();
        TextView text2 = twoLineListItem.getText2();

        text1.setText(sResult.SSID);
        String level = "" + sResult.level + "%";
        level= level.replace("-", "");
        text2.setText(level);
        
       // TextView text = (TextView) view.findViewById(android.R.id.text1);
        text1.setTextColor(Color.WHITE);
        text2.setTextColor(Color.WHITE);

        return twoLineListItem;
	}
	
	
	  
	
}