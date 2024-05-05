package com.inn.weatherApp.POJO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FavoriteCityTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        Integer expectedId = 1;
        User expectedUser = new User(); // You may want to create a real User object here
        String expectedCityName = "TestCity";

        // Act
        FavoriteCity favoriteCity = new FavoriteCity();
        favoriteCity.setId(expectedId);
        favoriteCity.setUser(expectedUser);
        favoriteCity.setCityName(expectedCityName);

        // Assert
        assertNotNull(favoriteCity);
        assertEquals(expectedId, favoriteCity.getId());
        assertEquals(expectedUser, favoriteCity.getUser());
        assertEquals(expectedCityName, favoriteCity.getCityName());
    }

    @Test
    public void testConstructorAndGettersAndSetters() {
        // Arrange
        Integer expectedId = 1;
        User expectedUser = new User(); // You may want to create a real User object here
        String expectedCityName = "TestCity";

        // Act
        FavoriteCity favoriteCity = new FavoriteCity();
        favoriteCity.setId(expectedId);
        favoriteCity.setUser(expectedUser);
        favoriteCity.setCityName(expectedCityName);

        // Assert
        assertNotNull(favoriteCity);
        assertEquals(expectedId, favoriteCity.getId());
        assertEquals(expectedUser, favoriteCity.getUser());
        assertEquals(expectedCityName, favoriteCity.getCityName());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FavoriteCity favoriteCity1 = new FavoriteCity();
        favoriteCity1.setId(1);
        favoriteCity1.setCityName("TestCity");

        FavoriteCity favoriteCity2 = new FavoriteCity();
        favoriteCity2.setId(1);
        favoriteCity2.setCityName("TestCity");

        // Assert
        assertEquals(favoriteCity1, favoriteCity2);
        assertEquals(favoriteCity1.hashCode(), favoriteCity2.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        FavoriteCity favoriteCity = new FavoriteCity();
        favoriteCity.setId(1);
        favoriteCity.setCityName("TestCity");

        // Act
        String str = favoriteCity.toString();

        // Assert
        assertTrue(str.contains("id=1"));
        assertTrue(str.contains("cityName=TestCity"));
    }
}