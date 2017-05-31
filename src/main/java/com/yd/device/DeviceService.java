package com.yd.device;

import com.yd.commend.CommendManager;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhailin on 2017/4/7.
 */
@Slf4j
public class DeviceService implements IDevice {

	private CommendManager commendManager;

	public DeviceService(CommendManager cm) {
		this.commendManager = cm;
	}

	public static File getCmdStrInfo(String cmd, String fileName) {
		File file = new File(fileName);
		String line = null;
		try {
			FileWriter fileWriter = new FileWriter(file);
			InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = bufferedReader.readLine()) != null) {
				fileWriter.write(line);
				fileWriter.write("\n");
				System.out.println(line);
			}
			fileWriter.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	@Override
	public List<DeviceEntry> getAllDevice() {
		List<DeviceEntry> deviceInfoList = new ArrayList<>();
		List<String> devicesList = getDeviceName();

		for (int j = 0; j < devicesList.size(); j++) {
			DeviceEntry testDevices = new DeviceEntry();
			String version = null;
			String model = null;
			String brand = null;
			String device_id = devicesList.get(j);
			String cmd = "adb -s " + device_id + " shell cat /system/build.prop";
			String deviceInfo = commendManager.runCmd(cmd);
			log.info(cmd);
			String[] dInfo = deviceInfo.split("\n");
			for (int i = 0; i < dInfo.length; i++) {
				if (dInfo[i].split("=")[0].equals("ro.build.version.release")) {
					version = dInfo[i].split("=")[1];
				} else if (dInfo[i].split("=")[0].equals("ro.product.model")) {
					model = dInfo[i].split("=")[1];
				} else if (dInfo[i].split("=")[0].equals("ro.product.brand")) {
					brand = dInfo[i].split("=")[1];
				} else {

				}
			}
			testDevices.setDeviceId(device_id);
			testDevices.setBrand(brand);
			testDevices.setModel(model);
			testDevices.setVersion(version);
			deviceInfoList.add(testDevices);
		}
		return deviceInfoList;
	}

	@Override
	public boolean installApp(DeviceEntry deviceEntry, String Path) {
		boolean flag = false;

		if (!(Path.isEmpty()) && Path.endsWith(".apk")) {

			String cmd = "adb -s " + deviceEntry.getDeviceId() + " install " + Path;
			log.info(cmd);
			String response = commendManager.runCmd(cmd);

			if (response.contains("Success")) {
				flag = true;
			} else {
				flag = false;
			}
		} else {
			log.info("请检查apk路径参数是否正确");
			flag = false;
		}
		return flag;

	}

	@Override
	public boolean uninstall(DeviceEntry deviceEntry, String appPackageName) {
		boolean flag = false;
		if (!(appPackageName.isEmpty())) {
			String cmd = "adb -s " + deviceEntry.getDeviceId() + " uninstall " + appPackageName;
			log.info(cmd);
			String response = commendManager.runCmd(cmd);
			log.info(response);
			if (response.contains("Success")) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;

	}

	@Override
	public boolean checkIsInstall(DeviceEntry deviceEntry, String appPackageName) {
		boolean flag = false;
		if (!(appPackageName.isEmpty())) {
			String cmd = "adb -s " + deviceEntry.getDeviceId() + " shell pm list packages";
			log.info("checkInstall:" + cmd);
			String response = commendManager.runCmd(cmd);
			log.info("checkInstall-response:" + response);
			String[] str = response.split("\n");
			for (int i = 0; i < str.length; i++) {
				if (str[i].equals("package:" + appPackageName)) {
					flag = true;
					break;
				} else {
					flag = false;
				}
			}
		}
		log.info("checkIsInstall" + flag);
		return flag;
	}

	@Override
	public String screenShot(DeviceEntry deviceEntry, String appName, String screenPath) {
		String screenName = System.currentTimeMillis() + "_" + appName + ".png";
		String cmd = "adb -s " + deviceEntry.getDeviceId() + " shell screencap -p /sdcard/" + screenName;
		log.info(cmd);
		commendManager.runCmd(cmd);
		String cmd2 = "adb pull /sdcard/" + screenName + " " + screenPath;
		log.info(cmd2);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		commendManager.runCmd(cmd2);
		return screenPath + screenName;
	}

	@Override
	public double getUsedCpu(DeviceEntry deviceEntry, String appName) {
		String cmd = "adb -s " + deviceEntry.getDeviceId() + " shell top -n 1 |grep '" + appName + "'";
		log.info(cmd);
		String response = commendManager.runCmd(cmd);
		double cpuinfo = 0;
		String[] str = response.split("\n");

		for (int i = 0; i < str.length; i++) {
			double cp = 0;
			if (str[i].contains("com.yongche")) {
				String cpu = str[i].trim().replaceAll(" +", " ").split(" ")[2].replace("%", "");
				cp = Double.parseDouble(cpu);
			}
			cpuinfo = cp + cpuinfo;
		}
		return cpuinfo;
	}

	@Override
	public double getUsedMem(DeviceEntry deviceEntry, String appName) {
		double meminfo = 0;
		String cmd = "adb -s " + deviceEntry.getDeviceId() + " shell dumpsys meminfo " + appName;
		log.info(cmd);
		String response = commendManager.runCmd(cmd);
		String[] str = response.split("\n");
		for (int i = 0; i < str.length; i++) {
			double mem = 0;
			if (str[i].contains("TOTAL SWAP")) {
				String me = str[i].trim().replaceAll(" +", " ").split(" ")[1];
				mem = Double.parseDouble(me);
			}
			meminfo = mem + meminfo;
		}
		return meminfo;

	}

	@Override
	public void runCmd(DeviceEntry deviceEntry, String cmd, File file) {
		commendManager.runCmdAndSaveToFile(deviceEntry, cmd, file);
	}

	@Override
	public void runCmd(String cmd, File file) {
		commendManager.runCmdAndSaveToFile(cmd, file);
	}

	@Override
	public void pushFile(DeviceEntry deviceEntry, String from, String to) {
	}

	@Override
	public void pullFile(DeviceEntry deviceEntry, String from, String to) {

	}

	public ArrayList<String> getDeviceName() {
		ArrayList<String> devicesList = new ArrayList<>();
		commendManager.runCmd("adb kill-server");
		String devices = commendManager.runCmd("adb devices");
		String[] str = devices.split("\n");
		for (int j = 0; j < str.length; j++) {
			if (str[j].contains("device") && !str[j].contains("devices")) {
				devicesList.add(str[j].split("\t")[0].trim());
			}
		}
		return devicesList;
	}

	public static void main(String[] args) {

		/*
		 * deviceService方法测试
		 */

		CommendManager commendManager = new CommendManager();
		IDevice d = new DeviceService(commendManager);
		DeviceEntry deviceEntry = d.getAllDevice().get(0);
		System.out.println(deviceEntry.getDeviceId());
		String Path = "E:/Git/Monkey_Crash_Test/src/main/resources/app/V6.4.6-release.apk";
		String appPackageName = "com.yongche";
		System.out.println("cpuinfo:" + d.getUsedCpu(deviceEntry, appPackageName));
		System.out.println("meminfo:" + d.getUsedMem(deviceEntry, appPackageName));
		System.out.println("checkIsInstall:" + d.checkIsInstall(deviceEntry, appPackageName));
		System.out.println("uninstall:" + d.uninstall(deviceEntry, appPackageName));
		System.out.println("checkIsInstall:" + d.checkIsInstall(deviceEntry, appPackageName));
		System.out.println("install:" + d.installApp(deviceEntry, Path));
		System.out.println("checkIsInstall:" + d.checkIsInstall(deviceEntry, appPackageName));
		commendManager.close();

		/*
		 * demo
		 */
		// double meminfo=0;
		// String cmd="adb shell dumpsys meminfo com.yongche";
		// System.out.println(cmd);
		// String response=commendManager.runCmd(cmd);
		// String[] str=response.split("\n");
		// for(int i=0;i<str.length;i++){
		// double mem=0;
		// if(str[i].contains("TOTAL SWAP")){
		// String me=str[i].trim().replaceAll(" +"," ").split(" ")[1];
		// mem=Double.parseDouble(me);
		// }
		// meminfo=mem+meminfo;
		// }
		// System.out.println("meminfo:"+meminfo);

		/*
		 * date test
		 */

		// SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
		// Date d1=new Date(System.currentTimeMillis());
		// String d2=sdf.format(d1);
		// System.out.println(d2);
		//
		// try {
		// String
		// blacklistpath="E:/Git/Monkey_Crash_Test/src/main/resources/config/"+d2+"_blacklist.txt";
		// System.out.println(blacklistpath);
		// File file=new File(blacklistpath);
		// if(!file.exists()){
		// file.createNewFile();
		// FileOutputStream out=new FileOutputStream(file,true);
		// String cmd="adb";
		// String response="hello";
		// StringBuffer sb=new StringBuffer(response);
		// out.write(sb.toString().getBytes("utf-8"));
		// out.close();
		// System.out.println("write file successful");
		// }
		//
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
