package com.example.usermoduleservice.service;

import com.example.usermoduleservice.entity.LoanUser;
import com.example.usermoduleservice.entity.MyUserDetails;
import com.example.usermoduleservice.repository.UserRepository;
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
        LoanUser loanUser = userRepository.findUserByEmail(email);

        if (ObjectUtils.isEmpty(loanUser)) {
            throw new UsernameNotFoundException("Loan User not found : " + email);
        }

        return new MyUserDetails(loanUser, getAuthoritiesForUser(loanUser.getRole()));
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForUser(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
