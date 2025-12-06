package com.greenlime.notification_reminder_flutter.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.greenlime.notification_reminder_flutter.devices.Asus;
import com.greenlime.notification_reminder_flutter.devices.DeviceAbstract;
import com.greenlime.notification_reminder_flutter.devices.DeviceBase;
import com.greenlime.notification_reminder_flutter.devices.HTC;
import com.greenlime.notification_reminder_flutter.devices.Huawei;
import com.greenlime.notification_reminder_flutter.devices.Letv;
import com.greenlime.notification_reminder_flutter.devices.Meizu;
import com.greenlime.notification_reminder_flutter.devices.OnePlus;
import com.greenlime.notification_reminder_flutter.devices.Oppo;
import com.greenlime.notification_reminder_flutter.devices.Samsung;
import com.greenlime.notification_reminder_flutter.devices.Vivo;
import com.greenlime.notification_reminder_flutter.devices.Xiaomi;
import com.greenlime.notification_reminder_flutter.devices.ZTE;
import com.greenlime.notification_reminder_flutter.utils.LogUtils;
import com.greenlime.notification_reminder_flutter.utils.SystemUtils;

public class DevicesManager {

    private static List<DeviceAbstract> deviceBaseList = new ArrayList<>(Arrays.asList(
            new Asus(),
            new Huawei(),
            new Letv(),
            new Meizu(),
            new OnePlus(),
            new Oppo(),
            new Vivo(),
            new HTC(),
            new Samsung(),
            new Xiaomi(),
            new ZTE()));

    public static DeviceBase getDevice(){
        List<DeviceBase> currentDeviceBase =new ArrayList<>();
        for (DeviceBase deviceBase : deviceBaseList) {
            if(deviceBase.isThatRom()){
                currentDeviceBase.add(deviceBase);
            }
        }
        if(currentDeviceBase.size()>1){
            StringBuilder logDevices= new StringBuilder();
            for (DeviceBase deviceBase : currentDeviceBase) {
                logDevices.append(deviceBase.getDeviceManufacturer());
            }
            LogUtils.e(DevicesManager.class.getName(),"MORE THAN ONE CORRESPONDING:"+logDevices+"|"+
                    SystemUtils.getDefaultDebugInformation());
        }

        if (currentDeviceBase.size()>0) {
            return currentDeviceBase.get(0);
        }else {
            return null;
        }
    }
}
