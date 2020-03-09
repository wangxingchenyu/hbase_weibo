package com.zhileiedu.weibo;

import com.zhileiedu.weibo.controller.WeiboController;

import java.io.IOException;
import java.util.List;

/**
 * @Author: wzl
 * @Date: 2020/3/7 22:59
 */
public class WeiboApp {
	private static WeiboController weiboController = new WeiboController();

	public static void main(String[] args) throws IOException {
		//weiboController.init();
		// 发布微博
		//weiboController.publish("1003", "test1");
		//weiboController.publish("1003", "test3");
		//weiboController.publish("1001", "test34");
		//weiboController.publish("1001", "testwt3");
		//weiboController.publish("1001", "testt6");
		//weiboController.publish("1001", "testt36");

		//weiboController.publish("1004", "testwt3");
		//weiboController.publish("1005", "testt6");

		// 关注明星
		//weiboController.follow("1002", "1001");
		//weiboController.follow("1002", "1003");
		//weiboController.follow("1002", "1004");
		//
		//List<String> recentWeiboByFans = weiboController.getRecentWeiboByFans("1002");
		//System.out.println("总条数=" + recentWeiboByFans.size());
		//for (String recentWeiboByFan : recentWeiboByFans) {
		//	System.out.println(recentWeiboByFan);
		//}

		// 取关
		//weiboController.unfollowed("1002", "1001");


		List<String> allWeiboByStar = weiboController.getAllWeiboByStar("1001");

		for (String s : allWeiboByStar) {
			System.out.println(s);
		}

	}
}
