package com.yd.listener;

import com.yd.commend.CommendManager;
import com.yd.device.DeviceEntry;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuhailin on 2017/4/11.
 */
public class CrashLog extends UploadListener {

	private static ExecutorService service = Executors.newFixedThreadPool(10);

	public CrashLog(CommendManager commendManager) {
		super(commendManager);
	}

	@Override
	public boolean addListener(DeviceEntry device, String appName) {

		return false;
	}

	@Override
	public boolean removeListenr(DeviceEntry device) {
		return false;
	}
}
