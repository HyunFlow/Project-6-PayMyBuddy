package com.openclassrooms.pay_my_buddy.domain.model;

import com.openclassrooms.pay_my_buddy.domain.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @EqualsAndHashCode.Include
  private Integer id;

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<Account> accounts = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private List<ExternalAccount> externalAccounts = new ArrayList<>();

  @OneToMany(mappedBy = "definingUser", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private List<UserRelation> definedRelations = new ArrayList<>();

  @OneToMany(mappedBy = "relatedUser", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private List<UserRelation> relatedRelations = new ArrayList<>();

  @Column(name = "username", nullable = false, length = 255)
  private String username;

  @Column(name = "email", nullable = false, unique = true, updatable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private Role role;

  public void addAccount(Account account) {
    this.accounts.add(account);
    account.setUser(this);
  }

  public void addExternalAccount(ExternalAccount externalAccount) {
    this.externalAccounts.add(externalAccount);
    externalAccount.setUser(this);
  }

  public void addDefinedRelation(UserRelation relation) {
    this.definedRelations.add(relation);
    relation.setDefiningUser(this);
  }

  public void addRelatedRelation(UserRelation relation) {
    this.relatedRelations.add(relation);
    relation.setRelatedUser(this);
  }

  public static User create(String username, String email, String password, Role role) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    user.setRole(role);
    return user;
  }
}
