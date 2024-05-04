package com.inn.weatherApp.restimpl;
import com.inn.weatherApp.rest.UserRest;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.utils.WeatherUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
@RestController
@Slf4j
public class UserRestImpl implements UserRest {

    @Autowired
    UserService userService;

    /*
    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        try{
            return userService.signUp(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return WeatherUtility.getResponse("something went wrong", HttpStatus.BAD_REQUEST);
    }
*/

    // Utility method to validate the request map
    private boolean validateSignUpMap(Map<String, String> requestMap) {
        for (String key : requestMap.keySet()) {
            String value = requestMap.get(key);
            // Check if any field is empty or too short, e.g., less than 3 characters
            if (value == null || value.trim().length() < 3) {
                log.info(key);
                return false;
            }
        }
        return true;
    }


    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        log.info(requestMap.toString());
            // Validate input map
            if (!validateSignUpMap(requestMap)) {
                return WeatherUtility.getResponse("Wrong Credentials", HttpStatus.BAD_REQUEST);
            }
        try {
            return userService.signUp(requestMap);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return WeatherUtility.getResponse("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Map<String,String>> signIn(Map<String, String> requestMap) {
        if (!requestMap.containsKey("email") || !requestMap.containsKey("user_password")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email and password are required"));
        }
        return userService.signIn(requestMap);
    }

    @Override
    public ResponseEntity<String> testUser() {
        return WeatherUtility.getResponse("Hello this is USER endpoint!", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> pay(@RequestBody String token) {
        return userService.pay(token);
    }

    @Override
    public ResponseEntity<String> addFavoriteCity(String city) {
        String email = getEmailFromSecurityContext();
        return userService.addFavoriteCity(email, city);
    }

    @Override
    public ResponseEntity<List<String>> getFavoriteCities() {
        String email = getEmailFromSecurityContext();
        return userService.getFavoriteCities(email);
    }

    private String getEmailFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }


}
