package com.yd.zkReg;

import java.util.List;

import com.google.common.base.Joiner;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * Created by liuhailin on 2017/1/10.
 */
@Data
@Builder
public class ZKConifg {

	private List<String>	servers;

	private String			nameSpace;

	private int				sessionTimeOut;

	private int				connectionTimeOut;

	public String getServers() {
		return Joiner.on(",").join(servers);
	}

}
