package com.ecommerce.ecommerce.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ecommerce.ecommerce.pojo.User;
import com.ecommerce.ecommerce.repository.UserRepository;

@Component
public class AuthUtil {
    @Autowired
    UserRepository userRepository;

    public String loggedInEmail()
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                        .orElseThrow(()->new UsernameNotFoundException("User not found exception"+authentication.getPrincipal()));
        
        return user.getEmail();
    }

    public Long loggedInUserId()
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                    .orElseThrow(()->new UsernameNotFoundException("User Id not found exception"+authentication.getPrincipal()));
        return user.getUserId();
    }


    public User loggedInUser()
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User user=userRepository.findByUsername(authentication.getName())
                    .orElseThrow(()->new UsernameNotFoundException("User not found exception"+authentication.getPrincipal()));
        return user;
    }


    
}
