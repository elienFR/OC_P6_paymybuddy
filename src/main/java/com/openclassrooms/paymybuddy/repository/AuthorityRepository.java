package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Authority;
import com.openclassrooms.paymybuddy.model.utils.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends CrudRepository<Authority, Integer> {
  Optional<Authority> findByName(Role role);
}
