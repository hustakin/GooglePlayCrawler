package com.google.play.crawler.sitesolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.play.crawler.URLQueue;

/**
 * 基础类，定义了网页分析器的基本操作 本包内的所有类都应继承自此类
 */
public class SiteSolver {

	protected URL m_url;
	protected StringBuffer m_content;
	protected URLQueue m_page_que;
	protected URLQueue m_file_que;

	public SiteSolver(URL url, URLQueue pageQueue, URLQueue fileQueue) {
		m_url = url;
		m_page_que = pageQueue;
		m_file_que = fileQueue;
		m_content = new StringBuffer();
	}

	/**
	 * 继承类应该重写该方法：分析网页内容，得到的URL放入对应向量 要求为时间阻塞的，以保证执行完毕后可以确认分析结束
	 */
	public void analyze() {

	}

	/**
	 * 静态方法：按给定URL和给定正则表达式，获得解析出的网页
	 */
	public static Vector<URL> getMatchedUrls(URL url, String prevString,
			String patternString, String postString) {
		return getMatchedUrls(getContent(url), prevString, patternString,
				postString);
	}

	/**
	 * 静态方法：根据给定的URL得到网页内容
	 */
	public static StringBuffer getContent(URL url) {
		StringBuffer contentBuffer = new StringBuffer();
		;
		try {
			InputStreamReader istreamReader = new InputStreamReader(
					url.openStream());
			int ch = 0;
			while ((ch = istreamReader.read()) != -1)
				contentBuffer.append((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuffer;
	}

	/**
	 * 静态方法：从给定的文本内容中，根据给定的正则表达式，获得匹配的URL
	 */
	public static Vector<URL> getMatchedUrls(StringBuffer contentBuffer,
			String prevString, String patternString, String postString) {
		Pattern pattern = Pattern.compile(patternString,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentBuffer);

		Vector<URL> urls = new Vector<URL>();
		while (matcher.find()) {
			try {
				urls.add(new URL(prevString + matcher.group().trim()
						+ postString));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return urls;
	}

	/**
	 * 静态方法：判断给定的URL是否满足给定的正则表达式
	 */
	public static boolean checkUrl(URL url, String patternString) {
		Pattern pattern = Pattern.compile(patternString,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url.toString());
		return matcher.find();
	}
}
