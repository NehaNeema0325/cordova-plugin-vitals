package com.medsolis.plugin.iHealthDeviceReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.hs.bluetooth.Hs4sControl;
import com.jiuan.android.sdk.po.observer_comm.Interface_Observer_CommMsg_PO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Paint.Join;
import android.os.Handler;
import android.os.Message;



public class SPConnectApplication implements Interface_Observer_CommMsg_PO
             /*Interface_Observer_BG*/{
	
	private List<HashMap<String, String>> mDeviceList;
	private DeviceManager deviceManager = DeviceManager.getInstance();
	String qr = "02323C64323C14322D1200A03E36BCF9D91446B4DC6E19011EDA01201D39";
	String BottleId = "18882266";
	String userId ;
	String clientID;
	String clientSecret;
	Context context;  
	CallbackContext callbackContext;
	DeviceList deviceList ;
	
	
	public SPConnectApplication(Context context, CallbackContext callbackContext)
	{
	    this.context = context;
	   
	    this.callbackContext = callbackContext;
	    mDeviceList = new ArrayList<HashMap<String, String>>();
	   
	    userId = "shravan@medsolis.com";//"maveriklko9719@gmail.com";
		//clientID = "c7939dd4f608492dbe2718a46d7e239f";
		//clientSecret = "4d83740f3b204290a003804af748a45c";
        
		deviceManager.initDeviceManager(context, userId);
        deviceManager.initReceiver();
        deviceManager.initPoStateCallaback(this);
        initReceiver();
    }
	
	 private void initReceiver() {
	        IntentFilter intentFilter = new IntentFilter();
	        intentFilter.addAction(Hs4sControl.MSG_HS4S_CONNECTED);
	        intentFilter.addAction(Hs4sControl.MSG_HS4S_DISCONNECT);
	        context.registerReceiver(mReceiver, intentFilter);
	    }

	  

	    BroadcastReceiver mReceiver = new BroadcastReceiver() {
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            if (Hs4sControl.MSG_HS4S_CONNECTED.equals(action)) {
	                String mac = intent.getStringExtra(Hs4sControl.MSG_HS4S_MAC);
	                deviceMap.put(mac, "HS4S");
	                refresh();
	            } else if (Hs4sControl.MSG_HS4S_DISCONNECT.equals(action)) {
	                String mac = intent.getStringExtra(Hs4sControl.MSG_HS4S_MAC);
	                deviceMap.remove(mac);
	                refresh();
	            }
	        }
	    };
	
	private List<HashMap<String, String>> map2List() {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        mDeviceList.clear();
        for (Iterator<String> it = deviceMap.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            String mac = (String) key;
            String type =  deviceMap.get(key);
            try {
            	  //check if scanned mac id present in local DB than ignore it
            	  Cursor cursor = VitalReadingPlugin.db.getDevice(mac);
            	  if(cursor.getCount() == 0)
				  {
   
    	            //Save scanned device in local DB
    			    deviceList = new DeviceList();
    				deviceList.setDeviceMAC(mac);
    				deviceList.setDeviceType(deviceMap.get(key));
    				//Save scanned device in local DB
    				VitalReadingPlugin.db.addDevice(deviceList);
    				JSONObject obj = new JSONObject();
    				
            		if(type.equalsIgnoreCase("PO3"))
            		    obj.put("type", "Pulse Ox");
            		else
            			 obj.put("type", type);
    				obj.put("mac", mac);
    				obj.put("flag", "scan");
    				//Return total device found count
              	    Cursor totalCursor = VitalReadingPlugin.db.getAllDevices();
    				obj.put("count", totalCursor.getCount());
    		        returnResult(obj.toString());
				}
            	/*else{
            		SPReadingApplication spReadingApp = null;
            	    spReadingApp = new SPReadingApplication(context,callbackContext,type,mac);
            	    //spReadingApp.startConnect();
            	}*/
             } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        return list;
    }

	public void returnResult(String listJson)
	{
		
	    PluginResult result = new PluginResult(PluginResult.Status.OK, listJson.toString());
	    result.setKeepCallback(true);
	    callbackContext.sendPluginResult(result);
	}
	
	public void getScannedDevice()
	{
		SPConnectApplication spConnectApp = new SPConnectApplication(context,callbackContext);
   	    spConnectApp.scanDevice();
   	  
		Cursor totalCursor = VitalReadingPlugin.db.getAllDevices();
		JSONObject obj = new JSONObject();
	
		try
		{
		  if(totalCursor.getCount() > 0)
		  {
			if (totalCursor.moveToFirst()) {
		        do {
		          String savedMac = totalCursor.getString(1);
		          String savedType = totalCursor.getString(0);
		          SPReadingApplication spReadingApp = null;
            	  spReadingApp = new SPReadingApplication(context,callbackContext,savedType,savedMac);
            	   //spReadingApp.startConnect();
				 } while (totalCursor.moveToNext());
		      }
		  }
		  else
		  {	obj.put("count", totalCursor.getCount());
			obj.put("flag", "available");
			PluginResult result = new PluginResult(PluginResult.Status.OK, obj.toString());
		    result.setKeepCallback(true);
		    callbackContext.sendPluginResult(result);
		 }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
    private void refresh() {
        Message message = new Message();
        message.what = 5;
        handler.sendMessage(message);
    }

    private Map<String, String> deviceMap = new ConcurrentHashMap<String, String>();
    
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                
                case 5:
                    map2List();
                    //initListView();
                    break;
                default:
                    break;
            }
        }

    };

    public void scanDevice()
    {
	   new Thread(new Runnable() {
       @Override
	      public void run() {
	          deviceManager.scanDevice();
	      }
	   }).start(); 
    }
	/*
	 *Interface_Observer_CommMsg_PO Interface Methods
	 */
	@Override
	public void msgDeviceConnect_Po(String deviceMac, String deviceType) {
		deviceMap.put(deviceMac, deviceType);
        refresh();
		
	}
    
	@Override
	public void msgDeviceDisconnect_Po(String deviceMac, String deviceType) {
		deviceMap.remove(deviceMac);
        refresh();
	}
	
	/*
	 *Interface_Observer_BG Interface Methods
	 */
	/*@Override
	public void msgBGError(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBGGetBlood() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBGPowerOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBGResult(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBGStripIn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBGStripOut() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void msgDeviceReady_new() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgUserStatus(int arg0) {
		// TODO Auto-generated method stub
		
	}*/

	

}
