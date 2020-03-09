package com.zhileiedu.weibo.service;

import com.zhileiedu.weibo.dao.WeiboDao;
import com.zhileiedu.weibo.name.Names;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: wzl
 * @Date: 2020/3/7 18:57
 */
public class WeiboService {
	private WeiboDao dao = new WeiboDao();

	public void init() throws IOException {

		//  创建名字空间
		dao.creatNamespace(Names.WEIBO_NAMESPACE);

		// 创建微博表内容表
		dao.createTable(Names.TABLE_WEIBO, Names.WEIBO_FAMILY);

		// 创建用户关系表
		dao.createTable(Names.TABLE_RELATION, Names.RELATION_FAMILY);

		// 创建微博内容表
		dao.createTable(Names.TABLE_INBOX, Names.VERSIONS, Names.INBOX_FAMILY);


	}

	// 发布微博内容
	public void publish(String userId, String content) throws IOException {
		// 向weibo表插入一条数据
		String starId = userId + "-" + System.currentTimeMillis();

		// 指定表，列族，列，内容
		dao.putCell(Names.TABLE_WEIBO, starId, Names.WEIBO_FAMILY, Names.WEIBO_COLUMN, content);

		// 根据userId 获取所有的关注的fansID
		String prefix = userId + ":followedby";
		List<String> rows = dao.getRowKeysByPrifix(Names.TABLE_RELATION, prefix);

		if (rows.size() <= 0) {
			return;
		}

		ArrayList<String> fansIds = new ArrayList<String>();
		for (String row : rows) { // 每行取到的值是: a:followedby:b
			String[] split = row.split(":");
			fansIds.add(split[2]);
		}

		// 向inbox表fansID放微表对应的rowKey
		dao.putCells(Names.TABLE_INBOX, fansIds, Names.INBOX_FAMILY, starId);
	}

	// service 层实现关注
	public void follow(String fans, String star) throws IOException {

		// 1 往relation 里面插入两条数据
		String rowKey1 = fans + ":follow" + star;
		String rowKey2 = star + ":followedby" + fans;
		String time = System.currentTimeMillis() + "";
		dao.putCell(Names.TABLE_RELATION, rowKey1, Names.RELATION_FAMILY, Names.RELATION_COLUMN, time);
		dao.putCell(Names.TABLE_RELATION, rowKey2, Names.RELATION_FAMILY, Names.RELATION_COLUMN, time);

		// 2 在微博表里获取Start 所有的rowkey
		String prefix = star + "-";
		List<String> rowKeys = dao.getRowKeysByPrifix(Names.TABLE_WEIBO, prefix);

		if (rowKeys.size() <= 0) {
			return;
		}

		// 3 找到 2步中最后三条
		int fromIndex = rowKeys.size() > 3 ? rowKeys.size() - Names.VERSIONS : 0;
		List<String> recentWbIds = rowKeys.subList(fromIndex, rowKeys.size());

		// 4 在inbox中插入3的结果
		for (String recentWbId : recentWbIds) {
			dao.putCell(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY, star, recentWbId);
		}


	}

	// 取关
	public void unfollowed(String fans, String star) throws IOException {
		// 删除relation两条数据
		// 1 往relation 里面插入两条数据
		String rowKey1 = fans + ":follow" + star;
		String rowKey2 = star + ":followedby" + fans;
		dao.deleteRow(Names.TABLE_RELATION, rowKey1);
		dao.deleteRow(Names.TABLE_RELATION, rowKey2);

		// 删除inbox表fans行，start所有版本操作
		dao.deleteRows(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY, star);

	}

	// 根据Fansid获取所有的关注的star微博内容
	public List<String> getRecentWeiboByFans(String fans) throws IOException {
		// 从inbox表里获取所有的列
		List<String> ids = dao.getCells(Names.TABLE_INBOX, fans, Names.INBOX_FAMILY);

		if (ids.size() <= 0) {
			return new ArrayList<String>();
		}

		List<String> list = new ArrayList<String>();
		// 查询所有的微博的数据

		List<String> recents = dao.getCellsByRowKeys(Names.TABLE_WEIBO, ids, Names.WEIBO_FAMILY, Names.WEIBO_COLUMN);

		return recents;
	}

	// 获取一个star的所有的微博
	public List<String> getgetAllWeiboByStar(String star) throws IOException {

		String prefix = star + "-";
		List<String> valueByPrefix = dao.getValueByPrefix(Names.TABLE_WEIBO, prefix, Names.WEIBO_FAMILY, Names.WEIBO_COLUMN);
		if (valueByPrefix.size() <= 0) {
			return new ArrayList<String>();
		}
		return valueByPrefix;
	}
}
