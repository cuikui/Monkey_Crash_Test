package com.yd.commend;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Created by liuhailin on 2017/4/7.
 */
@Slf4j
public class Commend implements Callable<String> {
	private String	cmd;

	private File	file;

	private Runtime	runtime	= Runtime.getRuntime();

	public Commend(String cmd) {
		this.cmd = cmd;
	}

	public Commend(String cmd, File file) {
		this.cmd = cmd;
		this.file = file;
	}

	@Override
	public String call() throws Exception {
		if (cmd == null) {
			return null;
		}

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
			} else {
				StringBuilder sb = new StringBuilder();
				bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line = "";
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				return sb.toString();
			}
			int exitCode = process.waitFor();
			log.info("Process exitValue: {}", exitCode);
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (fw != null) {
				fw.close();
			}
		}

		return null;
	}
}
