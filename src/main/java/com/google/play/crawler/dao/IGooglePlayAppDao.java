package com.google.play.crawler.dao;

import java.util.List;

import com.google.play.crawler.bean.GooglePlayApp;

/**
 * @author fanjie
 * @date 2013-10-22
 */
public interface IGooglePlayAppDao {
	public void insertGooglePlayApp(GooglePlayApp app);

	public GooglePlayApp findGooglePlayAppById(String id);

	public List<GooglePlayApp> findGooglePlayApps(String genre, String downloadTimesFrom, String downloadTimesTo);

	public boolean existGooglePlayAppById(String id);

	public void removeGooglePlayAppById(String id);
}
