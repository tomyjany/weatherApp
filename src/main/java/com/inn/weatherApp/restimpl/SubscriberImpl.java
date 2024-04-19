package com.inn.weatherApp.restimpl;

import com.inn.weatherApp.rest.Subscriber;
import com.inn.weatherApp.utils.WeatherUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SubscriberImpl implements Subscriber {
    @Override
    public ResponseEntity<String> youAreSubscribed() {
        return WeatherUtility.getResponse("Congratulation, you are now subscribed!", HttpStatus.ACCEPTED);
    }
}
