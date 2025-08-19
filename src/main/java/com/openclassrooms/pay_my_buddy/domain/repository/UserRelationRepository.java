package com.openclassrooms.pay_my_buddy.domain.repository;

import com.openclassrooms.pay_my_buddy.domain.model.User;
import com.openclassrooms.pay_my_buddy.domain.model.UserRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Integer> {
  List<UserRelation> findAllByDefiningUserId(Integer id);

  List<UserRelation> findByRelatedUserId(Integer id);
  List<UserRelation> findByRelatedUser(User user);

  boolean existsByDefiningUserIdAndRelatedUserId(Integer definingUserId, Integer relatedUserId);

  @Query(value = """
      SELECT a.account_id
      FROM `accounts` a
      WHERE a.user_id = :userId
      AND (a.account_status = 'ACTIVE' OR a.account_status IS NULL)
      ORDER BY a.account_type = 'CHECKING' THEN 0 ELSE 1 END, a.created_at DESC
      LIMIT 1
      """, nativeQuery = true)
  Integer pickReceiverAccountId(@Param("userId") Integer userId);
}
