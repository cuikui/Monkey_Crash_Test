package com.yd.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yd.commend.CommendManager;
import com.yd.device.DeviceEntry;
import com.yd.device.DeviceService;
import com.yd.device.IDevice;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static java.lang.String.valueOf;

/**
 * Created by liuhailin on 2017/4/10.
 */
@Slf4j
public abstract class UploadListener implements IListener {

	private IDevice deviceOp;

	public UploadListener(CommendManager commendManager) {

	    deviceOp = new DeviceService(commendManager);
	}

	public boolean uploadInfo(Map<String, String> params) {
		return true;
	}

	class AddUploading implements Runnable {

		private DeviceEntry	device;

		private String		appName;

		public AddUploading(DeviceEntry device, String appName) {
			this.device = device;
			this.appName = appName;
		}

		@Override
		public void run() {
			double usedCpu = deviceOp.getUsedCpu(device, appName);
			double usedMem = deviceOp.getUsedMem(device, appName);
			Map<String, String> params = Maps.newHashMap();
			params.put("cpu", valueOf(usedCpu));
			params.put("mem", valueOf(usedMem));
			boolean isS = uploadInfo(params);
			log.info("[ThreadID:{}],param is {}", Thread.currentThread().getId(), JSON.toJSONString(params));
			log.info("[ThreadID:{}],uploading Performance is {}", Thread.currentThread().getId(), isS);
		}
	}
}
