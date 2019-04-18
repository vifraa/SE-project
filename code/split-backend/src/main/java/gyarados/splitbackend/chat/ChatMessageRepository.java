package gyarados.splitbackend.chat;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * ChatMessageRepository is used to abstract the implementation of the database operations
 * for working with ChatMessages. MongRepository contains alot of basic functions for
 * querying and working with the database.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
