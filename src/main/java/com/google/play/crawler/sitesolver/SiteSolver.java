package com.google.play.crawler.sitesolver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.play.crawler.URLQueue;

/**
 * �����࣬��������ҳ�������Ļ������� �����ڵ������඼Ӧ�̳��Դ���
 */
public class SiteSolver {

	protected URL url;
	protected StringBuffer content;
	protected URLQueue pageUrlQueue;
	protected URLQueue fileQueue;

	public SiteSolver(URL url, URLQueue pageQueue, URLQueue fileQueue) {
		this.url = url;
		this.pageUrlQueue = pageQueue;
		this.fileQueue = fileQueue;
		this.content = new StringBuffer();
	}

	/**
	 * �̳���Ӧ����д�÷�����������ҳ���ݣ��õ���URL�����Ӧ���� Ҫ��Ϊʱ�������ģ��Ա�ִ֤����Ϻ����ȷ�Ϸ�������
	 */
	public void analyze() {

	}

	/**
	 * ��̬������������URL�͸���������ʽ����ý���������ҳ
	 */
	public static Vector<URL> getMatchedUrls(URL url, String prevString, String patternString, String postString) {
		return getMatchedUrls(getContent(url), prevString, patternString, postString);
	}

	/**
	 * ��̬���������ݸ�����URL�õ���ҳ����
	 */
	public static StringBuffer getContent(URL url) {
		StringBuffer contentBuffer = new StringBuffer();
		try {
			InputStreamReader istreamReader = new InputStreamReader(url.openStream());
			int ch = 0;
			while ((ch = istreamReader.read()) != -1)
				contentBuffer.append((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuffer;
	}

	/**
	 * ��̬�������Ӹ������ı������У����ݸ�����������ʽ�����ƥ���URL
	 */
	public static Vector<URL> getMatchedUrls(StringBuffer contentBuffer, String prevString, String patternString, String postString) {
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentBuffer);

		Vector<URL> urls = new Vector<URL>();
		while (matcher.find()) {
			try {
				urls.add(new URL(prevString + matcher.group().trim() + postString));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		return urls;
	}

	/**
	 * ��̬�������жϸ�����URL�Ƿ����������������ʽ
	 */
	public static boolean checkUrl(URL url, String patternString) {
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url.toString());
		return matcher.find();
	}
}
