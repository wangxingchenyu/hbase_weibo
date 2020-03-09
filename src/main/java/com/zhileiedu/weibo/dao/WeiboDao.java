package com.zhileiedu.weibo.dao;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wzl
 * @Date: 2020/3/7 18:56
 */
public class WeiboDao {

	private static Connection connection;

	// 初始化连接
	static {
		try {
			Configuration configuration = HBaseConfiguration.create();
			configuration.set("hbase.zookeeper.quorum", "hadoop01,hadoop02,hadoop03,hadoop04");
			connection = ConnectionFactory.createConnection(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 创建名字空间
	public void creatNamespace(String weiboNamespace) throws IOException {
		Admin admin = connection.getAdmin();
		try {
			admin.getNamespaceDescriptor(weiboNamespace);
		} catch (NamespaceNotFoundException e) { // 有异常的话，直接的创建namespace
			NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(weiboNamespace).build();
			admin.createNamespace(namespaceDescriptor);
		} finally {
			admin.close();
		}

	}

	// 创建普通微博内容表
	public void createTable(String tableName, String... family) throws IOException {
		createTable(tableName, 1, family);
	}


	// 创建有版本的微博内容表
	public void createTable(String tableName, int version, String... family) throws IOException {
		Admin admin = connection.getAdmin();

		// 只有表不存在的时候，才执行创建操作
		if (!admin.tableExists(TableName.valueOf(tableName))) {
			HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
			// 可能有多个列族
			for (String s : family) {
				HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(s));
				// 设置版本信息
				hColumnDescriptor.setMaxVersions(version);
				hTableDescriptor.addFamily(hColumnDescriptor);
			}
			admin.createTable(hTableDescriptor);
		}

		admin.close();
	}


	// 发布微博内容
	public void putCell(String tableName, String rowKey, String family, String column, String content) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(content));
		table.put(put);
		table.close();
	}

	// 获取发布人的粉丝列表
	public List<String> getRowKeysByPrifix(String tableRelation, String prefix) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableRelation));
		Scan scan = new Scan();
		scan.setRowPrefixFilter(Bytes.toBytes(prefix));

		ArrayList<String> rowKeys = new ArrayList<String>();

		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			byte[] row = result.getRow();
			rowKeys.add(new String(row));
		}

		// 切记关闭
		table.close();
		scanner.close();
		return rowKeys;
	}

	public void putCells(String tableInbox, ArrayList<String> fansIds, String family, String column) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableInbox));
		List<Put> puts = new ArrayList<Put>();
		for (String fansId : fansIds) {
			Put put = new Put(Bytes.toBytes(fansId));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(column), Bytes.toBytes(""));
			puts.add(put);
		}
		table.put(puts);
		table.close();
	}

	// 删除行
	public void deleteRow(String tableName, String rowKey) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Delete delete = new Delete(Bytes.toBytes(rowKey));
		table.delete(delete);
		table.close();
	}

	public void deleteRows(String tableInbox, String rowKey, String inboxFamily, String star) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableInbox));
		Delete delete = new Delete(Bytes.toBytes(rowKey));

		// addColumns 给一个列相关的版本也给删除
		delete.addColumns(Bytes.toBytes(inboxFamily), Bytes.toBytes(star));
		table.delete(delete);
	}

	public List<String> getCells(String tableName, String rowKey, String family) throws IOException {

		Table table = connection.getTable(TableName.valueOf(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		get.setMaxVersions(); // 获取所有版本
		get.addFamily(Bytes.toBytes(family));

		List<String> lists = new ArrayList<String>();

		Result result = table.get(get);

		Cell[] cells = result.rawCells();
		// 获取所有列的所有值
		for (Cell cell : cells) {
			byte[] bytesValue = CellUtil.cloneValue(cell);
			String s = Bytes.toString(bytesValue);
			lists.add(s);
		}

		return lists;
	}

	// 所有所有关注的微博的数据
	public List<String> getCellsByRowKeys(String tableWeibo, List<String> ids, String weiboFamily, String weiboColumn) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableWeibo));

		List<Get> gets = new ArrayList<Get>();

		for (String id : ids) {
			Get get = new Get(Bytes.toBytes(id));
			get.addColumn(Bytes.toBytes(weiboFamily), Bytes.toBytes(weiboColumn));

			gets.add(get);
		}

		Result[] results = table.get(gets);

		List<String> values = new ArrayList<String>();

		for (Result result : results) {
			Cell[] cells = result.rawCells();
			byte[] bytes = CellUtil.cloneValue(cells[0]);
			values.add(Bytes.toString(bytes));
		}

		table.close();
		return values;
	}

	public List<String> getValueByPrefix(String tableName, String prefix, String family, String column) throws IOException {
		Table table = connection.getTable(TableName.valueOf(tableName));
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(column));
		scan.setRowPrefixFilter(Bytes.toBytes(prefix));
		ResultScanner scanner = table.getScanner(scan);
		List<String> lists = new ArrayList<String>();

		for (Result result : scanner) {
			Cell[] cells = result.rawCells();
			byte[] bytes = CellUtil.cloneValue(cells[0]);
			lists.add(Bytes.toString(bytes));
		}

		return lists;
	}
}
