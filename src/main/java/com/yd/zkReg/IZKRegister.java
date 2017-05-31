package com.yd.zkReg;

import java.util.List;

import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.zookeeper.data.Stat;

/**
 * Created by liuhailin on 2017/1/10.
 */
public interface IZKRegister {

	/**
	 *
	 * @param path
	 * @param value
	 * @return path
	 */
	String createNode(String path, String value);

	String createNodeWithCallBack(String path, String value, BackgroundCallback callback);

	String getNodeData(String path);

	String getNodeData(String path, Stat stat);

	boolean checkNode(String path);

	List<String> getNodeChildren(String path);

	/**
	 * 默认无视版本 -1;
	 * 
	 * @param path
	 * @return
	 */
	boolean deleteNode(String path);

	boolean deleteNode(String path, int version);

	String updateNode(String path, String value);

	String updateNode(String path, String value, int version);

}
