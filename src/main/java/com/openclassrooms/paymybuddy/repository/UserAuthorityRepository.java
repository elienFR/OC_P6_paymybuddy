package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.UserAuthority;
import org.springframework.data.repository.CrudRepository;

public interface UserAuthorityRepository extends CrudRepository<UserAuthority, Integer> {
}
