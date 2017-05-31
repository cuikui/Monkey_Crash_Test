package com.yd.commend;

import com.yd.device.DeviceEntry;

import java.io.File;
import java.util.concurrent.*;

/**
 * Created by liuhailin on 2017/4/7.
 */
public class CommendManager {

	private static ExecutorService service = Executors.newFixedThreadPool(100);

	public void close() {
		service.shutdown();
	}

	public String runCmd(String cmd) {

		return runCmd(new Commend(cmd));
	}

	public void runCmdAndSaveToFile(DeviceEntry device, String cmd, File file) {
		runCmdAndSaveToFile(new BackgroundCommend(cmd, file, device));
	}

	public void runCmdAndSaveToFile(String cmd, File file) {

	    runCmdAndSaveToFile(new BackgroundCommend(cmd, file));
	}

	private String runCmd(Commend cmd) {
		Future<String> future = service.submit(cmd);
		try {
			String result = future.get();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void runBGCmd(BackgroundCommend cmd) {
		service.execute(cmd);
	}

	/**
	 * if cmd return null, throw Exception;
	 * 
	 * @param cmd
	 */
	private void runCmdAndSaveToFile(BackgroundCommend cmd) {
		runBGCmd(cmd);
	}
}
