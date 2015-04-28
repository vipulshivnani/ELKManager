package ELK;

/**
 * Created by vipul on 4/13/2015.
 */

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Spring MongoDB configuration file
 *
 */
@Configuration
public class SpringMongoConfig {

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {


        MongoClient mongoClient = new MongoClient("ds047107.mongolab.com:47107");
        MongoCredential cred = MongoCredential.createCredential("admin","cmpe283", "admin".toCharArray());
       MongoDatabase db = mongoClient.getDatabase("cmpe283");
        //boolean auth = db.("admin", "admin".toCharArray());

        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient,"cmpe283");
        return mongoTemplate;

    }

}
