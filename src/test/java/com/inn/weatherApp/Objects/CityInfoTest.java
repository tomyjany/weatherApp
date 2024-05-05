package com.inn.weatherApp.Objects;

import Objects.CityInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
public class CityInfoTest {
    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String expectedName = "TestCity";
        double expectedLongitude = 123.456;
        double expectedLatitude = 78.910;

        // Act
        CityInfo cityInfo = new CityInfo(expectedName, expectedLongitude, expectedLatitude);

        // Assert
        assertEquals(expectedName, cityInfo.getName());
        assertEquals(expectedLongitude, cityInfo.getLongitude());
        assertEquals(expectedLatitude, cityInfo.getLatitude());
    }
}
