package com.app.billmanager.service;

import com.app.billmanager.model.User;
import com.app.billmanager.repository.UserRepository;
import com.app.billmanager.utilities.PasswordUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
@Slf4j
public class CustomOidcUserService extends OidcUserService {

    private UserRepository userRepo;
    private PasswordEncoder encoder;

    public CustomOidcUserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        try {

            OidcUser oidcUser = super.loadUser(userRequest);
            return processOidcUser(oidcUser);

        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    OidcUser processOidcUser(OidcUser oidcUser) throws Exception {
        if(StringUtils.isEmpty(oidcUser.getEmail())) throw new Exception("No email");

        User user = userRepo.findUserByEmail(oidcUser.getEmail());
        if(Objects.isNull(user)){
            user = new User(oidcUser.getName(), oidcUser.getClaims(), oidcUser.getIdToken(), oidcUser.getUserInfo(), oidcUser.getAttributes());
            user.setPassword(encoder.encode(PasswordUtilities.generatePassword()));
            user.setEnabled(true);
            userRepo.save(user);
        }

        return user;
    }

}
