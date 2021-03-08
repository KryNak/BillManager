package com.app.billmanager.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class User implements UserDetails, OidcUser, OAuth2User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Email(message = "Correct your email address")
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String role;

    @NonNull
    private String fullName;

    private Boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @PrePersist
    private void defaultUserOptions(){
        enabled = false;
    }

    @Transient
    private Map<String, Object> attributes;

    @Transient
    private Map<String, Object> claims;

    @Transient
    private OidcUserInfo userInfo;

    @Transient
    private OidcIdToken idToken;

    @Transient
    private String name;

    public User(String name, Map<String, Object> claims, OidcIdToken idToken, OidcUserInfo userInfo,
                Map<String, Object> attributes){
        this.name = name;
        this.claims = claims;
        this.idToken = idToken;
        this.userInfo = userInfo;

        this.fullName = (String) attributes.get("name");
        this.email = (String) attributes.get("email");
        this.role = "ROLE_USER";
    }

    public User(String name, Map<String, Object> attributes){
        this.name = name;

        this.fullName = (String) attributes.get("name");
        this.email = (String) attributes.get("email");
        this.role = "ROLE_USER";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> getClaims() {
        return claims;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
