package com.inn.weatherApp.serviceImpl;

import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.dao.UserDao;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.utils.WeatherUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("inside group {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmail(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserObject(requestMap));
                    return WeatherUtility.getResponse("Register success", HttpStatus.ACCEPTED);


                } else {
                    return WeatherUtility.getResponse("email is already registered", HttpStatus.BAD_REQUEST);
                }

            } else {
                return (WeatherUtility.getResponse("WRONG Credentials", HttpStatus.BAD_REQUEST));
            }
        }catch(Exception ex){
            ex.printStackTrace();

        }
        return WeatherUtility.getResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    private boolean validateSignUpMap(Map<String,String> requestMap){
        return requestMap.containsKey("first_name") && requestMap.containsKey("last_name") && requestMap.containsKey("email")
                && requestMap.containsKey("user_password");

    }
    private User getUserObject(Map<String, String> requestMap){
        User user = new User();
        user.setFirst_name(requestMap.get("first_name"));
        user.setLast_name(requestMap.get("last_name"));
        user.setEmail(requestMap.get("email"));
        user.setUser_password(requestMap.get("user_password"));
        user.setSubscription(1==0);
        return user;



    }
}
