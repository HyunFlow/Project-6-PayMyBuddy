package com.openclassrooms.pay_my_buddy.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Account> accounts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<ExternalAccount> externalAccounts;

  @OneToMany(mappedBy = "definingUser", cascade = CascadeType.ALL)
  private List<UserRelation> definedRelations;

  @OneToMany(mappedBy = "relatedUser", cascade = CascadeType.ALL)
  private List<UserRelation> relatedRelations;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "email",nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

}
