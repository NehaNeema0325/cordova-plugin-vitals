package com.medsolis.plugin.iHealthDeviceReading;

public class DeviceList {
    
    //private variables
    String device_type;
    String device_mac;
     
  
    // constructor
    public DeviceList(String device_type, String device_mac){
        this.device_type = device_type;
        this.device_mac = device_mac;
    }
     
    // constructor
    public DeviceList(){
        
    }
    // getting name
    public String getDeviceType(){
        return this.device_type;
    }
     
    // setting name
    public void setDeviceType(String type){
        this.device_type = type;
    }
     
    // getting phone number
    public String getDeviceMAC(){
        return this.device_mac;
    }
     
    // setting phone number
    public void setDeviceMAC(String phone){
        this.device_mac = phone;
    }
}
