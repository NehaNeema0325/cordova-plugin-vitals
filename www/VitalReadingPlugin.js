var vitalReading =  {
    createEvent: function(title,notes, successCallback, errorCallback) {
        cordova.exec(
            successCallback, // success callback function
            errorCallback, // error callback function
            'VitalReadingPlugin', // mapped to our native Java class called "Calendar"
            'viewSPReading', // with this action name
            [{                  // and this array of custom arguments to create our entry
                "title": title,
                "description": notes
            }]
        );
    }
}
module.exports = vitalReading;