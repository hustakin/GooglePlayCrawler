package com.google.play.crawler.bean;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * @author fanjie
 * @date 2013-10-22
 */
public class GooglePlayApp implements Serializable, Comparable<GooglePlayApp> {
	private static final long serialVersionUID = 4221438053033334003L;

	@Id
	private String id;

	private String name;

	private String genre;

	private String url;

	private String downloadTimes;

	private int downloadTimesFrom;

	private int downloadTimesTo;

	@Override
	public String toString() {
		return id + " / " + name + " / " + genre + " / " + downloadTimesFrom + "-" + downloadTimesTo + " / " + url;
	}

	@Override
	public int compareTo(GooglePlayApp obj) {
		GooglePlayApp other = (GooglePlayApp) obj;
		return downloadTimesFrom - other.getDownloadTimesFrom();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDownloadTimesFrom() {
		return downloadTimesFrom;
	}

	public void setDownloadTimesFrom(int downloadTimesFrom) {
		this.downloadTimesFrom = downloadTimesFrom;
	}

	public int getDownloadTimesTo() {
		return downloadTimesTo;
	}

	public void setDownloadTimesTo(int downloadTimesTo) {
		this.downloadTimesTo = downloadTimesTo;
	}

	public String getDownloadTimes() {
		return downloadTimes;
	}

	public void setDownloadTimes(String downloadTimes) {
		this.downloadTimes = downloadTimes;
	}
}
