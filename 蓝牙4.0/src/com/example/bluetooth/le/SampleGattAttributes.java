/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.bluetooth.le;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    public static String MANUFACTURER_NAME_STRING = "00002a29-0000-1000-8000-00805f9b34fb";//设备信息
    public static String SENSOR_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";//传感器信息
    public static String SENSOR_CHECK = "00002a38-0000-1000-8000-00805f9b34fb";//传感器检查
    public static String CONTROL_POINT = "00002a52-0000-1000-8000-00805f9b34fb";//控制写命令
    public static String BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";//电量
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    

    static {
        // Sample Services.
    	attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");//设备信息服务
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Sensor Service");//传感器服务
        attributes.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");//电池电量服务
        
        // Sample Characteristics.
        attributes.put(MANUFACTURER_NAME_STRING, "Manufacturer Name String");
        attributes.put(SENSOR_MEASUREMENT, "Sensor Measurement String");
        attributes.put(SENSOR_CHECK, "Sensor Check String");
        attributes.put(CONTROL_POINT, "Control Point String");        
        attributes.put(BATTERY_LEVEL, "Battery Level String");    
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
