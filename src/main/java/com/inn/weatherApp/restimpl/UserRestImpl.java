package com.inn.weatherApp.restimpl;
import com.inn.weatherApp.rest.UserRest;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.utils.WeatherUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;
    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return WeatherUtility.getResponse("something went wrong", HttpStatus.BAD_REQUEST);
    }
}