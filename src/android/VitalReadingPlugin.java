package org.medsolis.vitalreading;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.medsolis.vitalreading.SPConnectApplication;
import org.medsolis.vitalreading.SPReadingApplication;
import org.medsolis.vitalreading.DatabaseHandler;

public class VitalReadingPlugin extends CordovaPlugin {
    public static final String ACTION_INITIALIZE = "initialize";
    public static final String ACTION_VIEW_READING = "viewReading";
    SPConnectApplication spConnectApp;
    SPReadingApplication spReadingApp;
    String type,mac;
    //create database
    public static DatabaseHandler db;
    
    @Override
    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException {
        //create local Database
        db = new DatabaseHandler(cordova.getActivity());
    	try {
	        if(action.equals(ACTION_INITIALIZE)) {

	        	// Get the application instance
	              spConnectApp = new SPConnectApplication(cordova.getActivity(),callbackContext);
	        	  spConnectApp.scanDevice();
	  
				  return true;
	        } 
	        else  if(action.equals(ACTION_VIEW_READING)) {
	        	this.cordova.getActivity().runOnUiThread(new Runnable() {
        	      public void run() {
        	    	  for(int i=0;i<data.length();i++){
                      try {
							JSONObject json_obj = data.getJSONObject(i);
							type = json_obj.getString("type");
		          	        mac = json_obj.getString("mac"); 
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					  }
                         
      	        	}
        	        spReadingApp = new SPReadingApplication(cordova.getActivity(),callbackContext,type,mac);
      	          }
        	   });
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
