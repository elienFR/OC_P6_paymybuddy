package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.model.UserBeneficiary;
import com.openclassrooms.paymybuddy.repository.UserBeneficiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserBeneficiaryService {

  @Autowired
  private UserBeneficiaryRepository userBeneficiaryRepository;

  public List<UserBeneficiary> getAllByUser(User user) {
    return userBeneficiaryRepository.findAllByUser(user);
  }

  public List<UserBeneficiary> getAllByBeneficiary(User user) {
    return userBeneficiaryRepository.findAllByBeneficiary(user);
  }

  public UserBeneficiary save(UserBeneficiary userBeneficiary) {
    return userBeneficiaryRepository.save(userBeneficiary);
  }

  public void delete(UserBeneficiary userBeneficiary) {
    userBeneficiaryRepository.delete(userBeneficiary);
  }

  public Iterable<UserBeneficiary> getAll() {
    return userBeneficiaryRepository.findAll();
  }
}
