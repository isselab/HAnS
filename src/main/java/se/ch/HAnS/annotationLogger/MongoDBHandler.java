package se.ch.HAnS.annotationLogger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBHandler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBHandler() {
        String connectionString = "mongodb+srv://testUser:IsThisWorking@cluster0.81u6phl.mongodb.net/?retryWrites=true&w=majority";
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("sampleDB");
        collection = database.getCollection("test3");
    }

    public void close() {
        mongoClient.close();
    }

    public void insertLogFile(String projectName, String logContents) {
        Document logData = new Document("project_name", projectName)
                .append("log_contents", logContents);

        System.out.println("Inserting log data: " + logData.toJson());

        collection.insertOne(logData);
        System.out.println("Log data inserted.");
    }
}