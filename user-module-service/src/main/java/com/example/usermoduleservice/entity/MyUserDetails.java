package com.example.usermoduleservice.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class MyUserDetails implements UserDetails {

        private final LoanUser loanUser;
        private final Collection<? extends GrantedAuthority> authorities;

        public MyUserDetails(LoanUser loanUser, Collection<? extends GrantedAuthority> authorities) {
                this.loanUser = loanUser;
                this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
                return authorities;
        }

        @Override
        public String getPassword() {
                return loanUser.getPassword();
        }

        @Override
        public String getUsername() {
                return loanUser.getEmail();
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
                return true;
        }
}
