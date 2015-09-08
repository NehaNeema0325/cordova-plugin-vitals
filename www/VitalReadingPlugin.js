  cordova.define("cordova-plugin-vitalreading.VitalReadingPlugin", function(require, exports, module)
  {
	  var exec = require("cordova/exec");
      var vitalReadingCall = {
		  initiateDevice: function(text, successCallback, errorCallback)  { 
			
			 exec(
	            successCallback, // success callback function
	            errorCallback, // error callback function
	            "vitalreading", // mapped to our native Java class called "Calendar"
	            "initialize",
	            [{                  // and this array of custom arguments to create our entry
	                "text": text
	            }] );
			  },
      
       /* Function to Scan Device all object */
		  startReading: function(type,mac, successCallback, errorCallback)  { 
 	      exec(
          successCallback, // success callback function
          errorCallback, // error callback function
          "vitalreading", // mapped to our native Java class called "Calendar"
          "viewReading",
          [{                  // and this array of custom arguments to create our entry
              "mac": mac,
              "type": type
              
          }] );
 	    }
     };
     module.exports = vitalReadingCall;
  }); 