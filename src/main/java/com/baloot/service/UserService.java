package com.baloot.service;

import com.baloot.exception.UserNotFoundException;
import com.baloot.model.User;
import com.baloot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    @Autowired
    public UserService(UserRepository userRepository){
        this.repo = userRepository;
    }

    public User findUserById(String id) throws UserNotFoundException {
        Optional<User> user = repo.findById(id);
        if(user.isPresent()){
            return user.get();
        }
        throw new UserNotFoundException(id);
    }

    public void save(User user) {
        repo.save(user);
    }
    public void delete(User user){
        repo.delete(user);
    }
    public  void saveAll(List<User> users){
        repo.saveAll(users);
    }
}
