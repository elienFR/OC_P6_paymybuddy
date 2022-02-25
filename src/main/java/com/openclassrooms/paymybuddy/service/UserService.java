package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

  @Autowired
  private UserRepository userRepository;

 public Optional<User> getUserByEmail(String email){
   return userRepository.getByEmail(email);
 }

  public User save(User user) {
    return userRepository.save(user);
  }

  public void delete(User user) {
   userRepository.delete(user);
  }

  public void deleteByEmail(String email) {
   userRepository.deleteByEmail(email);
  }

  public Iterable<User> getAll() {
    return userRepository.findAll();
  }
}
