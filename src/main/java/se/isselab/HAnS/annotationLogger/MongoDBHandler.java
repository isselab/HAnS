package se.isselab.HAnS.annotationLogger;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * This class handles interaction with MongoDB.
 * It provides functionalities to establish a connection with a MongoDB instance, and perform operations
 * like inserting log files to the database
 */
public class MongoDBHandler {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    /**
     * Constructs a MongoDBHandler object and establishes a connection with the MongoDB instance.
     * It also initializes the database and collection to be used.
     */
    public MongoDBHandler() {
        String connectionString = "mongodb+srv://testUser:IsThisWorking@cluster0.81u6phl.mongodb.net/?retryWrites=true&w=majority";
        mongoClient = MongoClients.create(connectionString);
        database = mongoClient.getDatabase("sampleDB");
        collection = database.getCollection("test3");
    }

    /**
     * Closes the connection with the MongoDB instance.
     */
    public void close() {
        mongoClient.close();
    }

    /**
     * Inserts a log file into the MongoDB collection.
     *
     * @param projectName Name of the project associated with the log file.
     * @param logContents Contents of the log file.
     */
    public void insertLogFile(String projectName, String logContents) {
        Document logData = new Document("project_name", projectName)
                .append("log_contents", logContents);

        System.out.println("Inserting log data: " + logData.toJson());

        collection.insertOne(logData);
        System.out.println("Log data inserted.");
    }
}