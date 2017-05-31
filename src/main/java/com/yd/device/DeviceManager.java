package com.yd.device;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yd.commend.CommendManager;
import com.yd.listener.CrashLog;
import com.yd.listener.IListener;
import com.yd.listener.Performance;
import com.yd.monkey.MKConfig;
import com.yd.monkey.MonkeyRunner;
import com.yd.zkReg.ZKCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by liuhailin on 2017/4/7.
 */
@Slf4j
public class DeviceManager {

	private final Map<String, DeviceEntry>	cachedDevices	= Maps.newConcurrentMap();

	private final Map<String, DeviceEntry>	online			= Maps.newConcurrentMap();

	private final Map<String, Integer>		runInfo			= Maps.newConcurrentMap();

	public Map<String, DeviceEntry> getOnlineDevice() {
		return cachedDevices;
	}

	private IDevice							deviceOp;

	private Performance						performance;

	private CrashLog						crashLog;

	private MKConfig						config;

	private ZKCenter						zkCenter;

	private static ScheduledExecutorService	service			= Executors.newSingleThreadScheduledExecutor();

	private static ExecutorService			MonkeyService	= Executors.newFixedThreadPool(20);

	public DeviceManager(CommendManager commendManager, ZKCenter zk) {
		deviceOp = new DeviceService(commendManager);
		performance = new Performance(commendManager);
		crashLog = new CrashLog(commendManager);
		zkCenter = zk;
	}

	public void init(MKConfig config) {
		this.config = config;
		service.scheduleAtFixedRate(new ScanDevice(), 0, 30, TimeUnit.SECONDS);
	}

	public void destroy() {
		service.shutdown();
	}

	public void onLineDevice(DeviceEntry entry, String appName) {
		performance.addListener(entry, appName);
		crashLog.addListener(entry, appName);
		online.put(entry.getDeviceId(), entry);
	}

	public void offLineDevice(DeviceEntry entry, boolean live) {

		String id = entry.getDeviceId();
		Integer t = runInfo.get(id);
		int it = t == null ? 1 : t.intValue();
		int times = config.getRunTimes();

		log.info("TaskNum:{}**************Stop**Monkey Task Device:{}", it, JSON.toJSONString(entry));
		// 注册zk
		// 关闭监听
		performance.removeListenr(entry);

		crashLog.removeListenr(entry);

		if (times > 0 && times > it) {
			startMonkeyOnDevice(entry, it + 1);
			runInfo.put(id, it + 1);
			return;
		} else {
			if (!live) {
				online.remove(entry.getDeviceId());
			}
		}
	}

	class ScanDevice implements Runnable {
		@Override
		public void run() {
			log.info("Start Scaning Device...");

			List<DeviceEntry> deviceEntries = deviceOp.getAllDevice();
			if (CollectionUtils.isNotEmpty(deviceEntries)) {
				for (DeviceEntry d : deviceEntries) {
					if (!cachedDevices.containsKey(d.getDeviceId())) {
						log.info("[*] New Device onLine:{}", JSON.toJSONString(d));
						cachedDevices.put(d.getDeviceId(), d);
						// 注册ZK
						// 执行流程
						// 添加监听
						startMonkeyOnDevice(d, 1);
					}
				}
			}
		}
	}

	private void startMonkeyOnDevice(DeviceEntry d, int times) {
		log.info("TaskNum:{}**************Start**Monkey Task Device:{}", times, JSON.toJSONString(d));
		MonkeyService.execute(new MonkeyRunner(deviceOp, d, config, new IListener() {
			@Override
			public boolean addListener(DeviceEntry device, String appName) {
				onLineDevice(device, appName);
				return true;
			}

			@Override
			public boolean removeListenr(DeviceEntry device) {
				offLineDevice(device, false);
				return true;
			}
		}));
	}
}
