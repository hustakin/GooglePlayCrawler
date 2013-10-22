package com.google.play.crawler.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.play.crawler.bean.GooglePlayApp;
import com.google.play.crawler.dao.IGooglePlayAppDao;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author FanJie
 */
public class GooglePlayAppDao implements IGooglePlayAppDao {
	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

	private String col = "google_play_app";

	private DBCollection dbCol;

	public void init() {
		dbCol = mongoTemplate.getCollection(col);
		dbCol.ensureIndex(new BasicDBObject().append("genre", 1));
		dbCol.ensureIndex(new BasicDBObject().append("downloadTimesFrom", 1));
		dbCol.ensureIndex(new BasicDBObject().append("downloadTimesTo", 1));
	}

	@Override
	public void insertGooglePlayApp(GooglePlayApp app) {
		mongoTemplate.save(app, col);
	}

	@Override
	public GooglePlayApp findGooglePlayAppById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		GooglePlayApp app = mongoTemplate.findOne(query, GooglePlayApp.class, col);
		return app;
	}

	@Override
	public List<GooglePlayApp> findGooglePlayApps(String genre, String downloadTimesFrom, String downloadTimesTo) {
		Query query = new Query();
		if (genre != null && !genre.trim().equals("")) {
			query.addCriteria(Criteria.where("genre").is(genre));
		}
		if (downloadTimesFrom != null && !downloadTimesFrom.trim().equals("")) {
			query.addCriteria(Criteria.where("downloadTimesFrom").is(downloadTimesFrom));
		}
		if (downloadTimesTo != null && !downloadTimesTo.trim().equals("")) {
			query.addCriteria(Criteria.where("downloadTimesTo").is(downloadTimesTo));
		}
		List<GooglePlayApp> apps = mongoTemplate.find(query, GooglePlayApp.class, col);
		return apps;
	}

	@Override
	public boolean existGooglePlayAppById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		long count = mongoTemplate.count(query, col);
		return count > 0;
	}

	@Override
	public void removeGooglePlayAppById(String id) {
		Query query = new Query(Criteria.where("_id").is(id));
		mongoTemplate.remove(query, col);
	}
}
