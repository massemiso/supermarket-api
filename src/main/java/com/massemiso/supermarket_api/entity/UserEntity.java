package com.massemiso.supermarket_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Entity
@Table(name = "sec_user")
@NoArgsConstructor
@Getter
public class UserEntity extends BaseEntityWithSoftDelete{
  @Column(unique = true)
  private String username;

  private String password;
  private String email;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;

  @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(name = "sec_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles;

  @Builder
  public UserEntity(
      String username,
      String password,
      String email,
      Boolean accountNonExpired,
      Boolean accountNonLocked,
      Boolean credentialsNonExpired,
      Set<RoleEntity> roles
  ) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.roles = roles;
  }

  public Set<SimpleGrantedAuthority> getAuthorities(){
    return roles.stream()
        .map(RoleEntity::toAuthority)
        .collect(Collectors.toSet());
  }

  public void update(String passwordEncoded, String email, Set<RoleEntity> roles) {
    if (passwordEncoded != null && !passwordEncoded.isBlank()) {
      this.password = passwordEncoded;
    }
    if (email != null && !email.isBlank()) {
      this.email = email;
    }
    if (roles != null && !roles.isEmpty()) {
      this.roles = roles;
    }
  }
}
