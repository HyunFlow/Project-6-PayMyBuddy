package com.openclassrooms.pay_my_buddy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_relations")
@Getter
@Setter
@NoArgsConstructor
public class UserRelation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "relation_id")
  private Integer relationId;

  @ManyToOne
  @JoinColumn(name = "defining_user_id")
  private User definingUser;

  @ManyToOne
  @JoinColumn(name = "related_user_id")
  private User relatedUser;

  @Column(name = "relation_name")
  private String relationName;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
