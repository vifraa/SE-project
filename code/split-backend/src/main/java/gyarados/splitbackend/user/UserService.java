package gyarados.splitbackend.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    public User add(User user){
        return repository.save(user);
    }

    public User findById(String id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
