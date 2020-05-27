package com.mydigitalschool.mongodb;

import java.util.Arrays;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates.*;

public class MongoService {

  private final MongoClient mongoClient;
  private MongoCollection<Document> collection;
  private Map<String, String> fields;

  public MongoClient getMongoClient() {
    return mongoClient;
  }

  public MongoService() {
    this("127.0.0.1", 27017);
  }

  public MongoService(final Integer port) {
    this("127.0.0.1", port);
  }

  public MongoService(final String host) {
    this(host, 27017);
  }

  public MongoService(final String host, final Integer port) {
    mongoClient = MongoClients.create(
        MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Arrays.asList(new ServerAddress(host, port))))
                .build());
  }

  public void deleteAll(FindIterable<Document> result) {
	  result.forEach((doc) -> collection.deleteMany(Filters.eq("_id", doc.get("_id"))));
  }
  
  public void deleteOne(String id) {
	  collection.deleteOne(Filters.eq("id", id));
  }
  
  public void updateDocuments(FindIterable<Document> result, Bson filter) {
	  result.forEach((doc) -> collection.updateOne(Filters.eq("_id", doc.get("_id")), filter));
  }
  
  public void updateOneDocument(String id, Bson filter) {
	  collection.updateOne(Filters.eq("_id", id), filter);
  }
}
