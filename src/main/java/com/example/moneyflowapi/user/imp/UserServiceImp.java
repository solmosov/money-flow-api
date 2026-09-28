package com.example.moneyflowapi.user.imp;

import com.example.moneyflowapi.user.UserRepository;
import com.example.moneyflowapi.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmailWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
