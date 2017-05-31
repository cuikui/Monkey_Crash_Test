package com.yd.listener;

import com.google.common.collect.Maps;
import com.yd.commend.CommendManager;
import com.yd.device.DeviceEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by liuhailin on 2017/4/10.
 */
@Slf4j
public class Performance extends UploadListener {

	private Map<String, ScheduledFuture>	runingJob	= Maps.newConcurrentMap();

	private ScheduledExecutorService		service		= Executors.newScheduledThreadPool(10);


	public Performance(CommendManager commendManager) {
		super(commendManager);
	}


	@Override
	public boolean addListener(DeviceEntry device, String appName) {
		ScheduledFuture task = service.scheduleAtFixedRate(new AddUploading(device, appName), 0, 5, TimeUnit.SECONDS);
		log.info("[add listener]:device-id:{}", device.getDeviceId());
		runingJob.put(device.getDeviceId(), task);
		return true;
	}

	@Override
	public boolean removeListenr(DeviceEntry device) {

		String deviceId = device.getDeviceId();

		ScheduledFuture task = runingJob.get(deviceId);

		if (task != null) {
			boolean r = task.cancel(false);

			if (r) {
				log.info("[remove listener]:device-id:{}", deviceId);
				runingJob.remove(deviceId);
			}
			return r;
		}

		return false;
	}

}
