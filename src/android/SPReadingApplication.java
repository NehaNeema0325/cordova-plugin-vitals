package com.medsolis.plugin.iHealthDeviceReading;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jiuan.android.sdk.device.DeviceManager;
import com.jiuan.android.sdk.po.bluetooth.lpcbt.JiuanPO3Observer;
import com.jiuan.android.sdk.po.bluetooth.lpcbt.PO3Control;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class SPReadingApplication implements JiuanPO3Observer{
  
  Context context;  
  CallbackContext callbackContext;
  private PO3Control poControl;
    private DeviceManager deviceManager;
  String type,mAddress;
  String userId ;
  String clientID;
  String clientSecret;
  
  
  public SPReadingApplication(Context context, CallbackContext callbackContext,String type,String mac){
    this.context = context;
      this.callbackContext = callbackContext;
      
      this.type = type;
      this.mAddress = mac;
      userId = "shravan@medsolis.com";//"maveriklko9719@gmail.com";
    clientID = "1aee3c0295c94926a432b8d072365d1e";
        //"c7939dd4f608492dbe2718a46d7e239f";
    clientSecret = "fb9697a0f346481a89d829ce5cf35962";
        //"4d83740f3b204290a003804af748a45c";
      
      // get control by device mac
        deviceManager = DeviceManager.getInstance();
    
        if (deviceManager.getAmDevice(mAddress) instanceof PO3Control) {
            poControl = (PO3Control) deviceManager.getPoDevice(mAddress);
        }

        if (poControl != null) {
            poControl.addObserver(this);
        }
        
        try {
      poControl.connect(context, userId, clientID, clientSecret);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /*public void startConnect(){
     poControl.connect(context, userId, clientID, clientSecret);
  }*/
  
  public void startReading(){
    poControl.startRealTime();
    //poControl.syncHistoryDatas();
  }
  
  @SuppressLint("HandlerLeak")
  private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
        // TODO Auto-generated method stub
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                int userStatus = ((Bundle) (msg.obj)).getInt("status");
                if(userStatus == 2)
                {
                  try {
            JSONObject obj = new JSONObject();
            obj.put("flag", VitalReadingPlugin.flag);
            obj.put("mac", mAddress);
            if(type.equalsIgnoreCase("PO3"))
                obj.put("type", "Pulse Ox");
            else
               obj.put("type", type);
              obj.put("count", ++VitalReadingPlugin.connectCount);
            startReading();
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj.toString());
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
          } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
                }
                else if(userStatus != 2)
                   reScanDevice();
                Toast.makeText(context, "PO3 UserStatus=" + userStatus+"---"+mAddress, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                int battery = ((Bundle) (msg.obj)).getInt("battery");
                Toast.makeText(context, "PO3 battery=" + battery, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                String historyData = ((Bundle) (msg.obj)).getString("historyData");
                Toast.makeText(context, "PO3 historyData=" + historyData, Toast.LENGTH_SHORT).show();
                break;
            case 3:
                String result = ((Bundle) (msg.obj)).getString("result");
                Toast.makeText(context, "PO3 result=" + result, Toast.LENGTH_SHORT).show();
                reScanDevice();
                break;
            case 4:
                Toast.makeText(context, "no historyData", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
            }
        }

    };
    
  
     public void reScanDevice()
     {
        ((CordovaActivity)context).runOnUiThread(new Runnable() {
         public void run()
         {
             poControl.disconnect();
             mAddress = "";
             SPConnectApplication spConnectApp = new SPConnectApplication(context,callbackContext);
             spConnectApp.scanDevice();
         }
       });
     }
  /* JiuanPO3Observer interface methods*/
    @Override
    public void msgUserStatus(int status) {
      // TODO Auto-generated method stub
        Log.i("act", "user status"+status);
      Message msg = new Message();
      Bundle bundle = new Bundle();
      bundle.putInt("status", status);
      msg.what = 0;
      msg.obj = bundle;
      handler.sendMessage(msg);
    }
    @Override
    public void msgBattery(int battery) {
      // TODO Auto-generated method stub
      Message msg = new Message();
      Bundle bundle = new Bundle();
      bundle.putInt("battery", battery);
      msg.what = 1;
      msg.obj = bundle;
      handler.sendMessage(msg);
    }

    @Override
    public void msgHistroyData(String historyData) {
      // TODO Auto-generated method stub
      if(historyData == null){
        poControl.startRealTime();
      }else{
        Log.i("history", historyData);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("historyData", historyData);
        msg.what = 2;
        msg.obj = bundle;
        handler.sendMessage(msg);
        sendReading(historyData,"offlineData");
        /*{"offlineData":[{"pulseWave":["4895","4548","5340","4895","4548","5340","4895","4548","5340","4895","4548","5340","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895","4548","4052","4895"],
        "bloodOxygen":"98","time":"2015-8-31 19:49:15","pulseRate":"110"}]}*/
      }
      
    }

    @Override
    public void msgRealtimeData(final String realData) {
      // TODO Auto-generated method stub
      Log.i("realdata", realData);
      sendReading(realData,"Data");
      
    }
    //{"Data":[{"pulseWave":["396","306","268"],"PI":0.03959139809012413,"bloodOxygen":"99","pulseStrength":"1","pulseRate":"73"}]}

    @Override
    public void msgResultData(String result) {
      // TODO Auto-generated method stub
      Log.i("end", result);
      //callbackContext.success(result);
        
      Message msg = new Message();
      Bundle bundle = new Bundle();
      bundle.putString("result", result);
      msg.what = 3;
      msg.obj = bundle;
      handler.sendMessage(msg);
    }


    public void sendReading(String reading,String arrayString)
    {
      try{
        JSONObject result_PO = new JSONObject(reading);
        JSONArray data = result_PO.getJSONArray(arrayString);
        JSONObject Oximeter_Result = data.getJSONObject(0);
        String SPO2 = Oximeter_Result.getString("bloodOxygen");
        String PR = Oximeter_Result.getString("pulseRate");
        JSONObject obj = new JSONObject();
      obj.put("SPO2", SPO2);
      obj.put("PR", PR);
      obj.put("MAC", mAddress);
      
      if(arrayString.equals("offlineData")){
        obj.put("flag", "Pastreading");
        String time = Oximeter_Result.getString("time");
        obj.put("TIME", time);
      }
      else 
        obj.put("flag", "reading");
        
        PluginResult result = new PluginResult(PluginResult.Status.OK, obj.toString());
            result.setKeepCallback(true);
            callbackContext.sendPluginResult(result);
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }
}
