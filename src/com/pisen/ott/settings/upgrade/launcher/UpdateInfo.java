package com.pisen.ott.settings.upgrade.launcher;

import java.util.List;
import android.izy.util.parse.GsonUtils;


/**
 * 应用程序更新实体类
 * @author Liuhc
 * @version 1.0 2014年12月17日 下午4:42:38
 */
public class UpdateInfo{
	
	public System System;
	public List<Content> Content;

	static public class System {
		//apk更新url
		public String Apk;
		//MD5校验码
		public String MD5;
		//系统版本号（硬件版本号）
		public String SystemVersion;
		//内部版本号（软件版本号）
		public int InternalVersion;
		//内容版本号
		public String UiVersion;
		//版本描述
		public String Description;
		//发布时间
		public String ReleaseDate;
	}

	static public class Content {
		public String UiVersion; // "1.0.1",
		public String Config; // "http://file.pisen.com.cn/ott/SB0001/Content/1.0.1/content.20150108093624.json",
		public String ConentVersion; // "20150108093624",
		public String MD5; // "621364c73adb4fb4b4d1482d9a86f41c",
		public String Description; // null
	}
	
	public static UpdateInfo json2bean(String json){
		return GsonUtils.jsonDeserializer(json, UpdateInfo.class);
	}
}
