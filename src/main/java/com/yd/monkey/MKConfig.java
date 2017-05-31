package com.yd.monkey;

import lombok.Builder;
import lombok.Data;

/**
 * Created by liuhailin on 2017/4/10.
 */
@Builder
@Data
public class MKConfig {
	/**
	 * 定义monkey命令中需要参数化部分
	 */

	private String	packageName;
	/**
	 * apk路径
	 */
	private String	apkPath;

	/**
	 * catLog保存路径
	 */
	private String	catLogPath;

	/**
	 * 保持monkey日志
	 */
	private String	monkeyLogPath;

	/**
	 * 跑Monkey命令,不需要写adb，只需要写adb命令后的部分
	 */
	private String	monkeyCMD;

	/**
	 * 保持日志命令
	 */
	private String	catLogCMD;

	/**
	 * 上传信息的接口(日志，截图，cpu，内存)
	 */
	private String	uploadURL;

	/**
	 * monkey跑的时间，单位s
	 */
	private int		runTimes;

	/**
	 * 间隔时间，单位s
	 */
	private int		interval;

}
