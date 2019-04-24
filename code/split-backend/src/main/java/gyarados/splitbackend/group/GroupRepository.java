package gyarados.splitbackend.group;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * GroupRepository is used to abstract the implementation of the database operations
 * for working with Groups. MongoRepository contains alot of basic functions for
 * querying and working with the database.
 */
public interface GroupRepository extends MongoRepository<Group, String> {

}
