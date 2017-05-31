package com.yd.monkey;

import com.yd.device.DeviceEntry;
import com.yd.listener.IListener;

/**
 * Created by liuhailin on 2017/4/10.
 */
public interface IWorkerMan {

	boolean start(DeviceEntry deviceEntry, MKConfig config, IListener listener);

}
