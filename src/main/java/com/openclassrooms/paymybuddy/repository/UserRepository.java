package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> getByEmail(String email);

  void deleteByEmail(String email);

  List<User> findAllByEmail(String email);

  boolean existsByEmail(String email);
}
