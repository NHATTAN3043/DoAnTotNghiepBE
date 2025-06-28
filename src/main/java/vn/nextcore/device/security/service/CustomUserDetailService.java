package vn.nextcore.device.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.nextcore.device.entity.User;
import vn.nextcore.device.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Email not found!");
        } else {
            // set role here
            String roleName = user.getRole().getName();
            Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    authorities
            );
        }
    }
}
