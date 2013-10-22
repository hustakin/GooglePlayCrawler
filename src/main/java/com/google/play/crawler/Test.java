package com.google.play.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.Span;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.HtmlPage;

public class Test {
	public static StringBuffer readFileByLines(String fileName, String charsetName) {
		StringBuffer sb = new StringBuffer();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				sb.append(tempString);
			}
			reader.close();
			return sb;
		} catch (IOException e) {
			e.printStackTrace();
			return sb;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void appendStrInFile(String fileName, String content, String charsetName) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName, true), charsetName);
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StringBuffer getContent(URL url, String id, String savePath, int retryTimes) {
		System.out.println("Requesting.. : " + url.toString());
		StringBuffer contentBuffer = new StringBuffer();
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		GetMethod getMethod = new GetMethod(url.toString());
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		boolean finish = false;
		while (!finish) {
			try {
				if (retryTimes-- == 0)
					finish = true;
				int statusCode = httpClient.executeMethod(getMethod);
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: " + getMethod.getStatusLine());
					finish = true;
				} else {
					byte[] responseBody = getMethod.getResponseBody();
					String content = new String(responseBody, "UTF-8");
					contentBuffer.append(content);
					File apkPageFile = new File(savePath + File.separator + id + ".htm");
					if (!apkPageFile.exists()) {
						apkPageFile.createNewFile();
						System.err.println("Saved file:" + apkPageFile.getAbsolutePath());
						appendStrInFile(apkPageFile.getAbsolutePath(), content, "UTF-8");
					}
					finish = true;
				}
			} catch (HttpException e) {
				System.err.println("Please check your provided http address!");
			} catch (Exception e) {
				System.err.println("Some error happened when get content");
			} finally {
				getMethod.releaseConnection();
			}
		}
		return contentBuffer;
	}

	public static void getMatchedUrls(StringBuffer contentBuffer, String prevString, String patternString, String postString, List<String> crawledUrls, String savePath,
			String crawledUrlsLog, int retryTimes) {
		Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(contentBuffer);

		while (matcher.find()) {
			String matchedUrl = matcher.group().trim();
			String matchedId = matcher.group(1).trim();
			if (crawledUrls.contains(matchedUrl))
				continue;
			System.out.println("******* Found matching: " + matchedUrl);
			crawledUrls.add(matchedUrl);
			try {
				URL url = new URL(prevString + matchedUrl + postString);
				StringBuffer sb = null;
				File contentFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "output" + System.getProperty("file.separator") + matchedId
						+ ".htm");
				if (contentFile.exists()) {
					System.out.println("Reading.. : " + contentFile.getAbsolutePath());
					sb = readFileByLines(contentFile.getAbsolutePath(), "UTF-8");
				} else {
					sb = getContent(url, matchedId, savePath, retryTimes);
					appendStrInFile(crawledUrlsLog, matchedUrl + "\r\n", "UTF-8");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				getMatchedUrls(sb, prevString, patternString, postString, crawledUrls, savePath, crawledUrlsLog, retryTimes);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parsePage(String url) {
		Parser parser;
		try {
			parser = new Parser(url);
			parser.setEncoding("UTF-8");

			MyTagFilter bodyFilter = new MyTagFilter("div");
			bodyFilter.addAttributeFilter("id", "body-content");
			NodeList bodyList = parser.parse(bodyFilter);
			if (bodyList.size() > 0) {
				Div bodyDiv = (Div) bodyList.elements().nextNode();
				String name = null;
				String genre = null;
				String downloadTimes = null;
				url = "https://play.google.com/store/apps/details?id=" + url.substring(url.lastIndexOf('\\') + 1, url.length() - 4);

				MyTagFilter nameFilter = new MyTagFilter("div");
				nameFilter.addAttributeFilter("class", "document-title");
				NodeList nameList = bodyDiv.getChildren().extractAllNodesThatMatch(nameFilter, true);
				if (nameList.size() > 0) {
					Div nameDiv = (Div) nameList.elements().nextNode();
					nameDiv = (Div) nameDiv.getChild(1);
					TextNode nameTextNode = (TextNode) nameDiv.getChild(0);
					name = nameTextNode.getText();
				}

				MyTagFilter genreFilter = new MyTagFilter("span");
				genreFilter.addAttributeFilter("itemprop", "genre");
				NodeList genreList = bodyDiv.getChildren().extractAllNodesThatMatch(genreFilter, true);
				if (genreList.size() > 0) {
					Span genreSpan = (Span) genreList.elements().nextNode();
					TextNode genreTextNode = (TextNode) genreSpan.getChild(0);
					genre = genreTextNode.getText();
				}

				MyTagFilter downloadTimesFilter = new MyTagFilter("div");
				downloadTimesFilter.addAttributeFilter("itemprop", "numDownloads");
				NodeList downloadList = bodyDiv.getChildren().extractAllNodesThatMatch(downloadTimesFilter, true);
				if (downloadList.size() > 0) {
					Div downloadDiv = (Div) downloadList.elements().nextNode();
					TextNode downloadTextNode = (TextNode) downloadDiv.getChild(0);
					downloadTimes = downloadTextNode.getText();
				}

				if (downloadTimes.contains("1,000,000 -") && genre.contains("¹¤¾ß")) {
					System.out.println(name + " / " + genre + " / " + downloadTimes + " / " + url);
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws MalformedURLException {
		String url = "https://play.google.com/store?hl=en";
		String outputPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "output";
		String crawledUrlsLog = System.getProperty("user.dir") + System.getProperty("file.separator") + "log" + System.getProperty("file.separator") + "crawledUrl.log";

		StringBuffer sb = getContent(new URL(url), "main", outputPath, 100);
		List<String> crawledUrls = new ArrayList<String>();
		getMatchedUrls(sb, "https://play.google.com", "/store/apps/details\\?id=([^\"&<]+)", "", crawledUrls, outputPath, crawledUrlsLog, 100);

		// File outputPathFile = new File(outputPath);
		// File[] pageFiles = outputPathFile.listFiles(new FileFilter() {
		// public boolean accept(File pathname) {
		// if (pathname.getName().endsWith("htm") && pathname.canRead() &&
		// pathname.canWrite())
		// return true;
		// return false;
		// }
		// });
		// for (File file : pageFiles) {
		// parsePage(file.getAbsolutePath());
		// }
	}
}
