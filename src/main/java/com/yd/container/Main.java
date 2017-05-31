package com.yd.container;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.yd.commend.CommendManager;
import com.yd.device.DeviceManager;
import com.yd.monkey.MKConfig;
import com.yd.zkReg.ZKCenter;
import com.yd.zkReg.ZKConifg;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liuhailin on 2017/4/7.
 */
@Slf4j
public class Main {
	private static volatile boolean running = true;







	/**
	 *
	 * TODO 配置zk中心，注册config配置，通过web改配置后，重启DeviceManager
	 *
	 *
	 * @param args
	 */
	public static void main(String[] args) {


        final Config config = ConfigFactory.load();

		final ZKConifg zk = ZKConifg.builder().servers(config.getStringList("zk.servers")).nameSpace("zk.namespace")
				.connectionTimeOut(10000).sessionTimeOut(5000).build();
        System.out.println("======="+zk);
        System.out.println("apk:====="+config.getString("app.apk"));

//        final ZKConifg zk=null;
        final MKConfig mk = MKConfig.builder().apkPath(config.getString("app.apk"))
				.packageName(config.getString("app.package")).catLogPath(config.getString("app.cat-log"))
				.monkeyLogPath(config.getString("app.monkey-log")).monkeyCMD(config.getString("app.monkey-cmd"))
				.catLogCMD(config.getString("app.catlog-cmd")).runTimes(config.getInt("app.monkey-run-times")).build();

		final ZKCenter zkCenter = new ZKCenter(zk);
		zkCenter.init();

		final CommendManager commendManager = new CommendManager();
		final DeviceManager deviceManager = new DeviceManager(commendManager, zkCenter);

		deviceManager.init(mk);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Ready to close..");
				commendManager.close();
				deviceManager.destroy();
				zkCenter.close();
				synchronized (Main.class) {
					running = false;
					Main.class.notify();
				}

			}
		});

		synchronized (Main.class) {
			while (running) {
				try {
					Main.class.wait();
				} catch (Throwable e) {
				}
			}
		}

	}

}
