package com.inn.weatherApp.JWT;

import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.dao.UserDao;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    UserDao userDao;

    @Getter
    private User userDetail;
    @Override
    //username = email
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("log info for loadUserByUsername");
        userDetail = userDao.findByEmail(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(userDetail.isSubscription()){
            authorities.add(new SimpleGrantedAuthority("ROLE_SUBSCRIBED"));
        }else{
            authorities.add(new SimpleGrantedAuthority("ROLE_UNSUBSCRIBED"));

        }


        if(!Objects.isNull(userDetail)){
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(),userDetail.getUser_password(),authorities);
        }else{
            throw new UsernameNotFoundException("User not Found");
        }
    }
}
