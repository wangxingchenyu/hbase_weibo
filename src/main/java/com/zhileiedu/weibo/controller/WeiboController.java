package com.zhileiedu.weibo.controller;

import com.zhileiedu.weibo.service.WeiboService;

import java.io.IOException;
import java.util.List;

/**
 * @Author: wzl
 * @Date: 2020/3/7 18:56
 */
public class WeiboController {

	private WeiboService weiboService = new WeiboService();

	// 初始化名字空间与表
	public void init() throws IOException {
		weiboService.init();
	}

	// 发布微博
	public void publish(String userId, String content) throws IOException {
		weiboService.publish(userId, content);
	}

	// 添加关注
	public void follow(String fans, String star) throws IOException {
		weiboService.follow(fans, star);
	}

	// 取关
	public void unfollowed(String fans, String star) throws IOException {
		weiboService.unfollowed(fans, star);
	}

	// 获取关注人的微博的内容
	public List<String> getRecentWeiboByFans(String fans) throws IOException {
		return weiboService.getRecentWeiboByFans(fans);
	}

	public List<String> getAllWeiboByStar(String star) throws IOException {
		return weiboService.getgetAllWeiboByStar(star);
	}

}
