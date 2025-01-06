package com.example.usermodule.service;

import com.example.usermodule.entity.LoanUser;
import com.example.usermodule.entity.MyUserDetails;
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
        LoanUser loanUser = userRepository.findUserByEmail(email);

        if (ObjectUtils.isEmpty(loanUser)) {
            throw new UsernameNotFoundException("Loan User not found : " + email);
        }

        System.out.println(loanUser);
        System.out.println(loanUser.getRole());

        return new MyUserDetails(loanUser, getAuthoritiesForUser(loanUser.getRole()));
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesForUser(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

}
