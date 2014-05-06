/*
 * Copyright (C) 2010-2013 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.txmcu.iairsdk.config;


/**
 * 
 * @author Oliver
 *
 */
public interface iAirConstants {
	
	public static final String Server_OK = "Ok";
	public static final String Server_Fail = "Fail";
	public static final String XIAOXIN_SSID="xiaoxin_AP";
	public static final String XIAOXIN_PWD ="xiaoxinap";
	
	public static final String XIAOXIN_IP="192.168.3.1";
	public static final Integer XIAOXIN_PORT = 8888;
	
  
    
    public static final String Mobile_AP_SSID="xiaoxin";
    public static final String Mobile_AP_PWD="1234567890";   
    public static final String login  = "http://112.124.58.144/android/login";
    public static final String gethome_structdata  = "http://112.124.58.144/android/gethome_structdata";
    public static final String setuser_nickname  = "http://112.124.58.144/android/setuser_nickname";
    public static final String getarea_structlist  = "http://112.124.58.144/android/getarea_structlist";
   // public static final String binduser_area  = "http://112.124.58.144/android/binduser_area";
    public static final String sethome_baseinfo  = "http://112.124.58.144/android/sethome_baseinfo";
    public static final String setxiaoxin_baseinfo  = "http://112.124.58.144/android/setxiaoxin_baseinfo";
    
    public static final String getfirstpage_briefinfo  = "http://112.124.58.144/android/getfirstpage_briefinfo";
    public static final String gethome_detailweather  = "http://112.124.58.144/android/gethome_detailweather";
    public static final String getxiaoxin_detailweather  = "http://112.124.58.144/android/getxiaoxin_detailweather";
    public static final String gethome_basedata  = "http://112.124.58.144/android/gethome_basedata";
    public static final String binduser_home  = "http://112.124.58.144/android/binduser_home";
    public static final String addhome  = "http://112.124.58.144/android/addhome";
    public static final String unbinduser_home  = "http://112.124.58.144/android/unbinduser_home";
    
    
    public static final String addhomenotice  = "http://112.124.58.144/android/addhomenotice";
    public static final String checkxiaoxin_exist  = "http://112.124.58.144/android/checkxiaoxin_exist";
    
    public static final String unbindhome_xiaoxin  = "http://112.124.58.144/android/unbindhome_xiaoxin";
    public static final String setxiaoxin_switch  = "http://112.124.58.144/android/setxiaoxin_switch";
    
    public static final String setxiaoxin_speed  = "http://112.124.58.144/android/setxiaoxin_speed";
    public static final String binduser_area  = "http://112.124.58.144/android/binduser_area";
    public static final String unbinduser_area  = "http://112.124.58.144/android/unbinduser_area";
    
    public static final String getarealist_briefweather  = "http://112.124.58.144/android/getarealist_briefweather";
    public static final String getarea_detailweather  = "http://112.124.58.144/android/getarea_detailweather";
    public static final String gethomenotice_bypage  = "http://112.124.58.144/android/gethomenotice_bypage";
    public static final String getarealist_briefweather4  = "http://112.124.58.144/android/getarealist_briefweather";
    public static final String getarealist_briefweather5  = "http://112.124.58.144/android/getarealist_briefweather";
    public static final String getarealist_briefweather6  = "http://112.124.58.144/android/getarealist_briefweather";
    public static final String getarealist_briefweather7  = "http://112.124.58.144/android/getarealist_briefweather";
    public static final String getarealist_briefweather8  = "http://112.124.58.144/android/getarealist_briefweather";
    
    
    
    public static final String API_Bind  ="http://112.124.58.144/mobile/bind";
    public static final String API_UnBind = "http://112.124.58.144/mobile/unbind";
    public static final String API_QueryBindlist = "http://112.124.58.144/mobile/query_bindlist";
    public static final String API_GetXiaoxin ="http://112.124.58.144/mobile/getxiaoxin";
    public static final String API_SetXiaoxinSwitch ="http://112.124.58.144/mobile/setxiaoxin_switch";
    public static final String API_SetXiaoxinSeed ="http://112.124.58.144/mobile/setxiaoxin_speed";
    public static final String API_SetXiaoxinName ="http://112.124.58.144/mobile/setxiaoxin_name";
    public static final String API_SetXiaoxinMode ="http://112.124.58.144/mobile/setxiaoxin_mode";
    public static final String API_GetAreaData ="http://112.124.58.144/mobile/getareadata";
}
