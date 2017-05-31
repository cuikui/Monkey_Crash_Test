package com.yd.device;

import java.io.File;
import java.util.List;

/**
 * Created by liuhailin on 2017/4/7.
 */
public interface IDevice {

	/**
	 * 获取用户adb命令行刷出来的设备，如果没有设备，返回空List，不要返回NULL
	 * 
	 * @return
	 */
	List<DeviceEntry> getAllDevice();

	/**
	 * 安装指定APP
	 * 
	 * @return
	 */
	boolean installApp(DeviceEntry deviceEntry, String Path);

	/**
	 * 卸载指定APP
	 * 
	 * @return
	 */
	boolean uninstall(DeviceEntry deviceEntry, String appPackageName);

	/**
	 * 判断app是否被安装
	 * 
	 * @return
	 */
	boolean checkIsInstall(DeviceEntry deviceEntry, String appPackageName);

	/**
	 * 返回手机上保持图片路径
	 * 
	 * @return 返回当前view的截图
	 */
	String screenShot(DeviceEntry deviceEntry, String appName, String screenPath);

	/**
	 * 获取当前设备使用cpu
	 * 
	 * @param deviceEntry
	 * @return
	 */
	double getUsedCpu(DeviceEntry deviceEntry, String appName);

	/**
	 * 获取当前设备使用内存
	 * 
	 * @param deviceEntry
	 * @return
	 */
	double getUsedMem(DeviceEntry deviceEntry, String appName);

	void runCmd(DeviceEntry deviceEntry, String cmd, File file);

	void runCmd(String cmd, File file);

	void pushFile(DeviceEntry deviceEntry, String from, String to);

	void pullFile(DeviceEntry deviceEntry, String from, String to);

}
