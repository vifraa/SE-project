package gyarados.splitbackend.Group;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String>, FindGroupsByCoordinates {

}
