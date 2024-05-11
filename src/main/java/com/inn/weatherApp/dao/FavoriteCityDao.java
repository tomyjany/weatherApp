package com.inn.weatherApp.dao;

import com.inn.weatherApp.POJO.FavoriteCity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteCityDao extends JpaRepository<FavoriteCity, Integer> {
    List<FavoriteCity> findByUser_Id(Integer userId);
}