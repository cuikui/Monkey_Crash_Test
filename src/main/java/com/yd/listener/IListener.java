package com.yd.listener;

import com.yd.device.DeviceEntry;

/**
 * Created by liuhailin on 2017/4/10.
 */
public interface IListener {

	boolean addListener(DeviceEntry device, String appName);

	boolean removeListenr(DeviceEntry device);
}
