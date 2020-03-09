package com.zhileiedu.weibo.name;

/**
 * @Author: wzl
 * @Date: 2020/3/7 21:38
 */
public class Names {
	// 定义名字空是与 微博表名 + 关系表名
	public static final String WEIBO_NAMESPACE = "weibo";
	public static final String TABLE_WEIBO = "weibo:weibo";
	public static final String TABLE_RELATION = "weibo:relation";
	public static final String TABLE_INBOX = "weibo:inbox";

	// 定义列族
	public static final String WEIBO_FAMILY = "data";
	public static final String RELATION_FAMILY = "data";
	public static final String INBOX_FAMILY = "data";

	// 定义列名
	public static final String WEIBO_COLUMN = "content";
	public static final String RELATION_COLUMN = "time";

	public static final int VERSIONS = 3;

}
