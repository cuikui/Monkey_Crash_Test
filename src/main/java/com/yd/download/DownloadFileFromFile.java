package com.yd.download;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by liuhailin on 2017/4/14.
 */
public class DownloadFileFromFile {

	public static String download(String url, String localPath) throws IOException {

		URL fileUrl = new URL(url);
		String fileName = getFileNameFromUrl(url);
		File saveDir = new File(localPath + fileName);
		// 文件保存位置
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		FileUtils.copyURLToFile(fileUrl, saveDir);

		return localPath;
	}

	public static String getFileNameFromUrl(String url) {
		String name = System.currentTimeMillis() + ".apk";
		int index = url.lastIndexOf("/");
		if (index > 0) {
			name = url.substring(index + 1);
			if (name.trim().length() > 0) {
				return name;
			}
		}
		return name;
	}
}
