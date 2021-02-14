package db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mongodb.client.model.Filters.eq;

public class ItemLoader {

    @Autowired
    private MongoDatabase mongoDatabase;

    public MongoCollection<ItemDb> getAll() {
        return mongoDatabase.getCollection("items", ItemDb.class);
    }

    public void getOne() {
        /*FindIterable<Document> findIterable = collection.find(new Document());
        //MongoCollection<Document> collection = mongoDatabase.getCollection("inventory");*/
    }

    public void write(ItemDb itemDb) {
        getAll().insertOne(itemDb);
    }

    public ItemDb findOne(long id) {
        return getAll().find(eq("items", id)).first();
    }

    public void delete(long id) {
        getAll().deleteOne(eq("items", id));
    }

    public void update(long id, ItemDb newItemVersion) {
        /*List<Score> newScores = new ArrayList<ItemDb>(getAll());
        newScores.add(new Score().setType("exam").setScore(42d));
        grade.setScores(newScores);
        Document filterByGradeId = new Document("_id", grade.getId());
        FindOneAndReplaceOptions returnDocAfterReplace = new FindOneAndReplaceOptions()
                .returnDocument(ReturnDocument.AFTER);
        Grade updatedGrade = grades.findOneAndReplace(filterByGradeId, grade, returnDocAfterReplace);
        System.out.println("Grade replaced:\t" + updatedGrade);*/
    }

    public void init() {
        //database.createCollection("customers", null);
        //Now, let's display all existing collections for current database:
        //
        //database.getCollectionNames().forEach(System.out::println);

    }

    /*public void saveInsert() {
        MongoCollection<Document> collection = mongoDatabase.getCollection("customers");
        BasicDBObject document = new BasicDBObject();
        document.put("name", "Shubham");
        document.put("company", "Baeldung");
        collection.insertOne(document);
    }

    public void saveUpdate() {
        BasicDBObject query = new BasicDBObject();
        query.put("name", "Shubham");

        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put("name", "John");

        BasicDBObject updateObject = new BasicDBObject();
        updateObject.put("$set", newDocument);

        collection.update(query, updateObject);
    }*/

    public void readDocumentFromCollection() {

    }

    public void deleteDocument() {

    }
    //boolean auth = database.authenticate("username", "pwd".toCharArray());

}
