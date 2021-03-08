package com.app.billmanager.service;

import com.app.billmanager.model.User;
import com.app.billmanager.repository.UserRepository;
import com.app.billmanager.exceptions.NoEmailAuthenticationException;
import com.app.billmanager.utilities.PasswordUtilities;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepo;
    private PasswordEncoder encoder;

    public CustomOAuth2UserService(UserRepository userRepo, PasswordEncoder encoder){
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
            return processOAuth2User(oAuth2User);
        } catch (Exception e) {
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT), e.getMessage(), e.getCause());
        }
    }

    OAuth2User processOAuth2User(OAuth2User oAuth2User) throws NoEmailAuthenticationException {
        String email = (String) oAuth2User.getAttributes().get("email");

        if (StringUtils.isEmpty(email)) throw new NoEmailAuthenticationException("No email");

        User userByUsername = userRepo.findUserByEmail(email);
        if (Objects.isNull(userByUsername)) {

            userByUsername = new User(oAuth2User.getName(), oAuth2User.getAttributes());
            userByUsername.setPassword(encoder.encode(PasswordUtilities.generatePassword()));
            userByUsername.setEnabled(true);
            userRepo.save(userByUsername);

        }
        return userByUsername;
    }

}
