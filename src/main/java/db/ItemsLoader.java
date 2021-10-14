package db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mongodb.client.model.Filters.eq;

public class ItemsLoader {

    @Autowired
    private MongoDatabase mongoDatabase;

    public MongoCollection<ItemDb> getAll() {
        return mongoDatabase.getCollection("items", ItemDb.class);
    }

    public void write(ItemDb itemDb) {
        try {
            Bson filter = Filters.eq("_id", itemDb.id);
            Bson update = new Document("$set", itemDb);
            UpdateOptions options = new UpdateOptions().upsert(true);
            getAll().updateOne(filter, update, options);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public ItemDb findOne(long id) {
        return getAll().find(eq("items", id)).first();
    }

    public void delete(long id) {
        getAll().deleteOne(eq("items", id));
    }

    public void readDocumentFromCollection() {

    }

    public void deleteDocument() {

    }
}
