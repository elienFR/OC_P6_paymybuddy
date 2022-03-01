package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBeneficiaryRepository extends CrudRepository<UserBeneficiary, Integer> {

  List<UserBeneficiary> findAllByUser(User user);

  List<UserBeneficiary> findAllByBeneficiary(User user);
}
