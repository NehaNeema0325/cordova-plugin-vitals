package com.medsolis.plugin.iHealthDeviceReading;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.medsolis.plugin.iHealthDeviceReading.DatabaseHandler;
import com.medsolis.plugin.iHealthDeviceReading.SPConnectApplication;
import com.medsolis.plugin.iHealthDeviceReading.SPReadingApplication;

public class VitalReadingPlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_CONNECT = "connect";
    public static final String ACTION_VIEW_READING = "viewReading";
    public static final String ACTION_EXIST = "existing";
    SPConnectApplication spConnectApp;
    SPReadingApplication spReadingApp;
    String type,mac;
    //create database
    public static DatabaseHandler db;
    public static String flag;
    
    @Override
    public boolean execute(final String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException {
        //create local Database
        db = new DatabaseHandler(cordova.getActivity());
      try {
          if(action.equals(ACTION_INITIALIZE) ) {

              
              // Get the application instance
                spConnectApp = new SPConnectApplication(cordova.getActivity(),callbackContext);
              spConnectApp.scanDevice();
                return true;
          } 
          else if(action.equals(ACTION_EXIST)) {
              flag = "availableConnect";
              // Get the application instance
              spConnectApp = new SPConnectApplication(cordova.getActivity(),callbackContext);
              spConnectApp.getScannedDevice();
                return true;
          } 
          else  if(action.equals(ACTION_VIEW_READING) || action.equals(ACTION_CONNECT)) {
            for(int i=0;i<data.length();i++){
                   try {
            JSONObject json_obj = data.getJSONObject(i);
            type = json_obj.getString("type");
                    mac = json_obj.getString("mac"); 
           }catch (JSONException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
           }
                }
              if(action.equals(ACTION_VIEW_READING)){
                 spReadingApp = new SPReadingApplication(cordova.getActivity(),callbackContext,type,mac);
                     spReadingApp.startReading();
              } 
               else if(action.equals(ACTION_CONNECT)){
                 flag = "connect";    //spReadingApp.startConnect();
                 spReadingApp = new SPReadingApplication(cordova.getActivity(),callbackContext,type,mac);

               }
                    
                 // }
            // });
             return true;
          }
          else {
            callbackContext.error("Invalid action");
              return false;
         }
      }
      catch(Exception e) {
          System.err.println("Exception: " + e.getMessage());
          callbackContext.error(e.getMessage());
          return false;
      } 
    }
}
