package com.fontolan.spring.securitykeyvault.example.adapters.security.oauth2;

import com.fontolan.spring.securitykeyvault.example.domain.model.*;
import com.fontolan.spring.securitykeyvault.example.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public CustomOAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        AuthProvider provider = AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String providerId = oauth2User.getName();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(oauth2User.getAttribute("email"));
                    u.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    u.setRoles(Set.of(Role.ROLE_USER));
                    u.setProvider(provider);
                    u.setProviderId(providerId);
                    return userRepository.save(u);
                });

        String usernameAttr = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.name())).toList(),
                oauth2User.getAttributes(),
                usernameAttr);
    }
}

