package com.example.demo.security;

import com.petpulse.security.AuthTokenFilter;
import com.petpulse.security.UserDetailsServiceImpl;

public class WebSecurityConfig extends com.petpulse.security.WebSecurityConfig {
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthTokenFilter authTokenFilter) {
        super(userDetailsService, authTokenFilter);
    }
}
