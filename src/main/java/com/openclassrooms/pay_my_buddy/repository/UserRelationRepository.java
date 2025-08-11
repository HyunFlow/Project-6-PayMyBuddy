package com.openclassrooms.pay_my_buddy.repository;

import com.openclassrooms.pay_my_buddy.model.User;
import com.openclassrooms.pay_my_buddy.model.UserRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Integer> {
  List<UserRelation> findByDefiningUserId(Integer id);
  List<UserRelation> findByDefiningUser(User user);

  List<UserRelation> findByRelatedUserId(Integer id);
  List<UserRelation> findByRelatedUser(User user);
}
