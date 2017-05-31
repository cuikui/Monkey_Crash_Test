package com.yd.commend;

import com.yd.device.DeviceEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * Created by liuhailin on 2017/4/7.
 */
@Slf4j
public class BackgroundCommend implements Runnable {

	private String		cmd;

	private File		file;

	private DeviceEntry	device;

	private Runtime		runtime	= Runtime.getRuntime();

	public BackgroundCommend(String cmd, File file, DeviceEntry device) {
		this.cmd = cmd;
		this.file = file;
		this.device = device;
	}

	public BackgroundCommend(String cmd, File file) {
		this.cmd = cmd;
		this.file = file;
	}

	@Override
	public void run() {
		if (cmd == null) {
			return;
		}
		if (device != null) {
			syncInvoke();
		} else {
			asynInvoke();
		}

	}

	private void syncInvoke() {
		execute();
		synchronized (device) {
			device.notify();
		}
	}

	private void asynInvoke() {
		execute();
	}

	private void execute() {
		BufferedReader bufferedReader = null;
		FileWriter fw = null;
		try {
			Process process = runtime.exec(cmd);
			if (file != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				fw = new FileWriter(file);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					fw.write(line);
					fw.write("\n");
				}
			}
			int exitCode = process.waitFor();
			log.info("Process exitValue: {}", exitCode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}