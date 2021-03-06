package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserAuthority;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityRepository extends CrudRepository<UserAuthority, Integer> {
  Iterable<UserAuthority> findAllByUser(User user);
}
