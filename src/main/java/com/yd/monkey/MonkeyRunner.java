package com.yd.monkey;

import com.yd.device.DeviceEntry;
import com.yd.device.IDevice;
import com.yd.listener.IListener;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by liuhailin on 2017/4/10.
 */
public class MonkeyRunner extends Monkey {

	private IDevice device;

	public MonkeyRunner(IDevice device, DeviceEntry deviceEntry, MKConfig config, IListener listener) {
		super(deviceEntry, config, listener);
		this.device = device;
	}

	@Override
	public void runMK(DeviceEntry deviceEntry, MKConfig config) {

		boolean isInstall = device.checkIsInstall(deviceEntry, config.getPackageName());

		if (!isInstall) {
			device.installApp(deviceEntry, config.getApkPath());
		}

		String mkLog = config.getMonkeyLogPath();

		File mkdir = new File(mkLog);
		if (!mkdir.exists()) {
			mkdir.mkdirs();
		}
		long timestemp = System.currentTimeMillis();
		if (StringUtils.isNotBlank(mkLog)) {
			if (mkLog.endsWith("/")) {
				mkLog = mkLog + deviceEntry.getDeviceId() + "_" + timestemp + ".log";
			} else {
				mkLog = mkLog + "/" + deviceEntry.getDeviceId() + "_" + timestemp + ".log";
			}
		}

		File mkLogFile = new File(mkLog);

		String catLog = config.getCatLogPath();

		File catLogDir = new File(catLog);
		if (!catLogDir.exists()) {
			catLogDir.mkdirs();
		}

		if (StringUtils.isNotBlank(catLog)) {
			if (catLog.endsWith("/")) {
				catLog = catLog + deviceEntry.getDeviceId() + "_" + timestemp + ".log";
			} else {
				catLog = catLog + "/" + deviceEntry.getDeviceId() + "_" + timestemp + ".log";
			}
		}

		File catLogFile = new File(catLog);
		// 跑monkey
		String mk_cmd = "adb -s " + deviceEntry.getDeviceId() + " " + config.getMonkeyCMD();
		String logcat_cmd = "adb -s " + deviceEntry.getDeviceId() + " " + config.getCatLogCMD();

		device.runCmd(logcat_cmd, catLogFile);

		device.runCmd(deviceEntry, mk_cmd, mkLogFile);

	}
}
