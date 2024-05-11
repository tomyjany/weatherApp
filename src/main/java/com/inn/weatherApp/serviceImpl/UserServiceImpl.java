package com.inn.weatherApp.serviceImpl;

import com.inn.weatherApp.JWT.CustomerDetailsService;
import com.inn.weatherApp.JWT.JWTUtil;
import com.inn.weatherApp.POJO.FavoriteCity;
import com.inn.weatherApp.POJO.User;
import com.inn.weatherApp.dao.FavoriteCityDao;
import com.inn.weatherApp.dao.UserDao;
import com.inn.weatherApp.service.UserService;
import com.inn.weatherApp.utils.WeatherUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    FavoriteCityDao favoriteCityDao;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomerDetailsService customerDetailsService;
    @Autowired
    JWTUtil jwtUtil;
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
        String encoded_password = passwordEncoder.encode(requestMap.get("user_password"));

        //user.setUser_password(requestMap.get("user_password"));
        user.setUser_password(encoded_password);
        user.setSubscription(1==0);
        return user;



    }
    @Override
    public ResponseEntity<Map<String,String>> signIn(Map<String,String> requestMap) {
        try {
            UserDetails userDetails = customerDetailsService.loadUserByUsername(requestMap.get("email"));
            if (passwordEncoder.matches(requestMap.get("user_password"), userDetails.getPassword())) {
                String role = userDetails.getAuthorities().iterator().next().getAuthority();
                String token = jwtUtil.generateToken(userDetails.getUsername(),role);
                User user = userDao.findByEmail(requestMap.get("email"));
                String apiKey = user.getApi_key();
                Map<String,String> response = new HashMap<>();
                response.put("token",token);
                response.put("apiKey",apiKey);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User not found"));
        } catch (DataAccessException e) {
            log.error("Database access issue: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Service unavailable"));
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @Override
    public ResponseEntity<String> pay(String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userDao.findByEmail(email);
            if (user != null) {
                user.setSubscription(true);
                String apiKey = UUID.randomUUID().toString();
                user.setApi_key(apiKey);
                userDao.save(user);
                return WeatherUtility.getResponse("Payment successful, subscription activated", HttpStatus.OK);
            } else {
                return WeatherUtility.getResponse("User not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return WeatherUtility.getResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public boolean validateApiKey(String apiKey) {
        User user = userDao.findByApiKey(apiKey);
        return user != null;
    }

    @Override
    public Integer findUserByEmailAddress(String email) {
        User user = userDao.findByEmail(email);
        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }

    @Override
    public ResponseEntity<String> addFavoriteCity(String email,String city) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            return WeatherUtility.getResponse("User not found", HttpStatus.BAD_REQUEST);
        }

        List<FavoriteCity> favoriteCities = user.getFavoriteCities();
        for (FavoriteCity favoriteCity : favoriteCities) {
            if (favoriteCity.getCityName().equals(city)) {
                //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City is already in the list of favorite cities");
                return WeatherUtility.getResponse("City is already in the list of favorite cities", HttpStatus.BAD_REQUEST);
            }
        }

        FavoriteCity newFavoriteCity = new FavoriteCity();
        newFavoriteCity.setCityName(city);
        newFavoriteCity.setUser(user);
        favoriteCityDao.save(newFavoriteCity);

        //return ResponseEntity.ok("City added to favorite cities");
        return WeatherUtility.getResponse("City added to favorite cities", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getFavoriteCities(String email) {
        User user = userDao.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<FavoriteCity> favoriteCities = user.getFavoriteCities();
        List<String> cityNames = new ArrayList<>();
        for (FavoriteCity favoriteCity : favoriteCities) {
            cityNames.add(favoriteCity.getCityName());
        }

        return ResponseEntity.ok(cityNames);    }


}
