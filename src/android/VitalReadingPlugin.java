package org.medsolis.vitalreading;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class VitalReadingPlugin extends CordovaPlugin {
    public static final String ACTION_VIEW_SP_READING = "viewSPReading";
    
  
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
    	try {
	        if (action.equals(ACTION_VIEW_SP_READING)) {
	
	            String name = data.getString(0);
	            String message = "Hello, " + name;
	            callbackContext.success(message);
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
