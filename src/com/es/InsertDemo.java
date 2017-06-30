package com.es;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Demo test on insert to Elasticsearch cluster
 * Please contact Philips for the data file
 * Please remove `plr_sg_tweet_test` after you learn about this code. Thanks!
 * @author pprasetyo
 *
 */
public class InsertDemo {
	static final String IDX_TYPE = "fb_posts";
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		final String filename = "D:/result/dataTwo.json";
		final String destIdxName = "facebook_data_post";
		final ObjectMapper mapper = new ObjectMapper();
		final Client starClient = ESConnectionUtils.getConnection(ESSetting.LOCAL);
		BulkProcessor bulkProcessor = BulkProcessor.builder(starClient, new Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest req) { }
			
			@Override
			public void afterBulk(long executionId, BulkRequest req, Throwable failure) {
				System.err.println(failure.getMessage());	
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest req, BulkResponse res) {
				if (res.hasFailures()) {
					res.buildFailureMessage();
				}
			}
		})
		.setBulkActions(8000)
		.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB))
		.setFlushInterval(TimeValue.timeValueSeconds(5))
		.setConcurrentRequests(1)
		.build();
		
		try (Stream<String> stream = Files.lines(Paths.get(filename),StandardCharsets.ISO_8859_1)) {
			stream.forEach(line -> {
				try {
					if(line.toString().contains("likeDislikeCount")){
					ObjectNode tweet = (ObjectNode) mapper.readTree(line);
					//System.out.println(line);
					// For this example, 
					// the format of field "geoLocation" is not in the proper Geo-point format
					// We need to convert it to the correct format
					// geo_point accepts the format shown in this page: 
					// https://www.elastic.co/guide/en/elasticsearch/guide/current/lat-lon-formats.html
					/*tweet = convertTweetGeoLocationFormat(tweet, "geoLocation");
					ObjectNode retweet = (ObjectNode) tweet.get("retweetedStatus");
					if (retweet != null) {
						retweet = convertTweetGeoLocationFormat(retweet, "geoLocation");
						ObjectNode retweetQuoted = (ObjectNode) retweet.get("quotedStatus");
						if (retweetQuoted != null) {
							retweetQuoted = convertTweetGeoLocationFormat(retweetQuoted, "geoLocation");
						}
					}
					ObjectNode quoted = (ObjectNode) tweet.get("quotedStatus");
					if (quoted != null) {
						quoted = convertTweetGeoLocationFormat(quoted, "geoLocation");
					}*/
					bulkProcessor.add(new IndexRequest(destIdxName, IDX_TYPE).source(mapper.writeValueAsString(tweet)));	
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bulkProcessor.awaitClose(10, TimeUnit.SECONDS);
		starClient.close();		
	}
	private static ObjectNode convertTweetGeoLocationFormat(ObjectNode jsonObj, String geoField) {
		JsonNode geoLocation = jsonObj.get(geoField);
		if (geoLocation != null) {
			if ((geoLocation.get("latitude") != null) && (geoLocation.get("longitude") != null)) {
				double lat = geoLocation.get("latitude").asDouble();
				double lng = geoLocation.get("longitude").asDouble();
				jsonObj.put("geoLocation", lat + "," + lng);
			}						
		}
		return jsonObj;
	}
}