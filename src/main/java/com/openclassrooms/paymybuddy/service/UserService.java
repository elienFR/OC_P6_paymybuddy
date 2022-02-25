package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

 public Optional<User> getUserByEmail(String email){
   return userRepository.getByEmail(email);
 }

  public User save(User user) {
    return userRepository.save(user);
  }
}
