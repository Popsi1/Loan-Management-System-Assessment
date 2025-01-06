package com.example.usermodule.service;

import com.example.usermodule.entity.MyUserDetails;
import com.example.usermodule.entity.User;
import com.example.usermodule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("User not found : " + email);
        }

        return new MyUserDetails(user, getAuthoritiesForUser("user.getRole()"));
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForUser(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
