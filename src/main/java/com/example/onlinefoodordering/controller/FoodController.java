package com.example.onlinefoodordering.controller;


import com.example.onlinefoodordering.model.Food;
import com.example.onlinefoodordering.model.User;
import com.example.onlinefoodordering.service.FoodService;
import com.example.onlinefoodordering.service.RestaurantService;
import com.example.onlinefoodordering.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/search")
    public ResponseEntity<List<Food>> searchFood(@RequestParam String name,
                                                @RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserByJwtToken(jwt);

        List<Food> foods = foodService.searchFood(name);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Food>> getRestaurantFood(@RequestParam boolean vegetarian,
                                                @RequestParam boolean seasonal,
                                                @RequestParam boolean nonveg,
                                                @RequestParam(required = false)String food_category,
                                                @PathVariable Long restaurantId,
                                                @RequestHeader("Authorization") String jwt)throws Exception{
        User user = userService.findUserByJwtToken(jwt);

        List<Food> foods = foodService.getRestaurantsFood(restaurantId, vegetarian, seasonal, nonveg, food_category);
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }
}
