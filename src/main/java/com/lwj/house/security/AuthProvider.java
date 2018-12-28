package com.lwj.house.security;

import com.lwj.house.entity.User;
import com.lwj.house.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 自定义认证实现
 * @author lwj
 */
@Configuration
@EnableWebSecurity
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private IUserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String inputPassword = (String) authentication.getCredentials();
        User user = userService.findUserByName(userName);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }

        if (this.passwordEncoder.matches(user.getPassword(), inputPassword)) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
