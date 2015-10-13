//cordova.define("com.medsolis.plugin.iHealthDeviceReading.VitalReadingPlugin", function(require, exports, module) { 
var exec = require('cordova/exec');

var vitalreading = {

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
     /* Function to Connect Device all object */
     startConnect: function(type,mac, successCallback, errorCallback)  { 
        exec(
        successCallback, // success callback function
        errorCallback, // error callback function
        "vitalreading", // mapped to our native Java class called "Calendar"
        "connect",
        [{                  // and this array of custom arguments to create our entry
            "mac": mac,
            "type": type
            
        }] );
      },
      /* Function to get all available Devices */
       getAvailableDevice: function(text, successCallback, errorCallback)  { 
       exec(
              successCallback, // success callback function
              errorCallback, // error callback function
              "vitalreading", // mapped to our native Java class called "Calendar"
              "existing",
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

module.exports = vitalreading;

//});
