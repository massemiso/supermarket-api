package com.massemiso.supermarket_api.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
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

  @Column(unique = true)
  private String email;

  private Boolean isAccountExpired;
  private Boolean isAccountLocked;
  private Boolean isCredentialsExpired;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "sec_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<RoleEntity> roles;

  @Builder
  public UserEntity(
      String username,
      String password,
      String email,
      Boolean isAccountExpired,
      Boolean isAccountLocked,
      Boolean isCredentialsExpired,
      Set<RoleEntity> roles
  ) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.isAccountExpired = isAccountExpired;
    this.isAccountLocked = isAccountLocked;
    this.isCredentialsExpired = isCredentialsExpired;
    this.roles = roles;
  }

  public Collection<? extends SimpleGrantedAuthority> getAuthorities(){
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    roles.forEach(role -> authorities.add(
            new SimpleGrantedAuthority(
                "ROLE_".concat(role.getRoleEnum().toString()))
        )
    );
    return authorities;
  }

}
