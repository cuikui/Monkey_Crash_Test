package com.yd.zkReg;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuhailin on 2017/1/10.
 */
@Slf4j
public class ZKCenter extends ZKManager {

	public ZKCenter(ZKConifg config) {
		super(config);
	}

	@Override
	public String createNode(String path, String value) {
		String result = null;
		try {
			if (!checkNode(path)) {
				if (null == value) {
					this.client.create().creatingParentsIfNeeded().forPath(path);
				} else {
					result = this.client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
							.forPath(path, value.getBytes());
				}
			} else {
				log.info("[CreateNode] node is already exists。path:{}", path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("[CreateNode] path:{},value:{},result:{}", path, value, result);
		return result;
	}

	@Override
	public String createNodeWithCallBack(String path, String value, BackgroundCallback callback) {
		ExecutorService service = Executors.newFixedThreadPool(2);
		try {
			client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
					.inBackground(new BackgroundCallback() {

						@Override
						public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
							System.out.println("创建节点返回的状态码:" + event.getResultCode() + ",返回的类型:" + event.getType());
							System.out.println("运行创建该节点的线程为:" + Thread.currentThread().getName());
							/**
							 * CuratorEventType c =
							 * curatorEvent.getType();//事件类型，可在CuratorEventType看到具体种类
							 * int r =
							 * curatorEvent.getResultCode();//0,执行成功，其它，执行失败
							 * Object o =
							 * curatorEvent.getContext();//事件上下文，一般是由调用方法传入，供回调函数使用的参数
							 * String p = curatorEvent.getPath();//节点路径
							 * List<String> li =
							 * curatorEvent.getChildren();//子节点列表 byte[] datas =
							 * curatorEvent.getData();//节点数据
							 */
						}
					}, service).forPath(path, value.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getNodeData(String path) {
		byte[] data = new byte[0];
		try {
			if (checkNode(path)) {
				data = this.client.getData().forPath(path);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(data, Charset.forName("utf8"));
	}

	@Override
	public String getNodeData(String path, Stat stat) {

		byte[] data = new byte[0];
		try {
			if (checkNode(path)) {
				data = this.client.getData().storingStatIn(stat).forPath(path);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(data, Charset.forName("utf8"));
	}

	@Override
	public boolean checkNode(String path) {
		boolean exist = false;
		try {
			Stat stat = this.client.checkExists().forPath(path);

			if (stat != null) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exist;
	}

	public List<String> getNodeChildren(String path) {
		List<String> children = Lists.newArrayList();
		try {
			if (checkNode(path)) {
				children = this.client.getChildren().forPath(path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return children;
	}

	@Override
	public boolean deleteNode(String path) {
		boolean isSuccess = false;
		try {
			this.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(path);
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public boolean deleteNode(String path, int version) {
		boolean isSuccess = false;
		try {
			this.client.delete().deletingChildrenIfNeeded().withVersion(version).forPath(path);
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSuccess;
	}

	@Override
	public String updateNode(String path, String value) {
		String result = null;
		try {
			Stat stat = client.setData().forPath(path, value.getBytes());
			if (stat != null) {
				result = value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String updateNode(String path, String value, int version) {
		String result = null;
		try {
			Stat stat = client.setData().withVersion(version).forPath(path, value.getBytes());
			if (stat != null) {
				result = value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
