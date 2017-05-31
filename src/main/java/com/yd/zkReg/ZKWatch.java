package com.yd.zkReg;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * Curator提供了三种Watcher(Cache)来监听结点的变化：
 * 
 * Path
 * Cache：监视一个路径下1）孩子结点的创建、2）删除，3）以及结点数据的更新。产生的事件会传递给注册的PathChildrenCacheListener。
 * Node Cache：监视一个结点的创建、更新、删除，并将结点的数据缓存在本地。 Tree Cache：Path Cache和Node
 * Cache的“合体”，监视路径下的创建、更新、删除事件，并缓存路径下所有孩子结点的数据。
 * 
 * Created by liuhailin on 2017/1/11.
 */
@Slf4j
public class ZKWatch extends ZKCenter {
	protected NodeCache			nodeCache;

	protected PathChildrenCache	childrenCache;

	public TreeCache			treeCache;

	public ZKWatch(ZKConifg config) {
		super(config);
	}

	public void addNodeListener(final String path) {
		nodeCache = new NodeCache(client, path);
		try {
			nodeCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		nodeCache.getListenable().addListener(new NodeCacheListener() {// 监听对象

			@Override
			public void nodeChanged() throws Exception {// 重写监听方法
				ChildData data = nodeCache.getCurrentData();
				if (data == null) {
					log.debug("删除当前节点:{}", path);
				} else {
					log.info("[更新，当前节点内容是]：{}", new String(data.getData()));
				}

			}
		});
	}

	public void addNodeChildListener(String path) {
		childrenCache = new PathChildrenCache(client, path, true);
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				switch (event.getType()) {// 子节点的事件类型
					case CHILD_ADDED:
						log.debug("===========node data:{}", event.getData());// 通过pathChildrenCacheEvent，可以获取到节点相关的数据
						break;
					case CHILD_REMOVED:
						log.debug("===========node data path:{}", event.getData().getPath());
						break;
					case CHILD_UPDATED:
						break;
					default:
						break;
				}
			}
		});
		try {
			childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addAllNodeListener(String path) {
		treeCache = new TreeCache(client, path);
		try {
			treeCache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		treeCache.getListenable().addListener(new TreeCacheListener() {

			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				switch (event.getType()) {// 子节点的事件类型
					case INITIALIZED:
						log.debug("=============Init Cache Finished");
						break;
					case NODE_ADDED:
						log.debug("ADD:===========node data:{}", event.getData());
						break;
					case NODE_REMOVED:
						log.debug("Delete:===========node data path:{}", event.getData().getPath());
						break;
					case NODE_UPDATED:
						break;
					default:
						break;
				}
			}
		});
	}

	// public static void main(String[] args) {
	//
	// ZKConifg config = new ZKConifg();
	// config.setServers(Lists.newArrayList("10.0.13.62:2181"));
	// ZKWatch watch = new ZKWatch(config);
	//
	// watch.addNodeListener("/test/123");
	// // watch.addNodeChildListener("/test");
	//// watch.addAllNodeListener("/test");
	//
	// try {
	// Thread.sleep(Integer.MAX_VALUE);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
}
