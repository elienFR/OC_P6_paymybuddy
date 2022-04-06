package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  Optional<User> findByGithubId(String githubId);

  Optional<User> findByGoogleId(String googleId);
}
