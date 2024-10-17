package com.example.onlinefoodordering.service;


import com.example.onlinefoodordering.model.IngredientCategory;
import com.example.onlinefoodordering.model.IngredientsItem;
import com.example.onlinefoodordering.model.Restaurant;
import com.example.onlinefoodordering.repository.IngredientCategoryRepository;
import com.example.onlinefoodordering.repository.IngredientItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientItemServiceImpl implements IngredientItemService {

    @Autowired
    private IngredientItemRepository ingredientRepository;

    @Autowired
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private IngredientItemRepository ingredientItemRepository;

    @Override
    public IngredientCategory createIngredientCategory(String name, Long restaurantId) throws Exception {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setRestaurant(restaurant);
        ingredientCategory.setName(name);
        return ingredientCategoryRepository.save(ingredientCategory);
    }

    @Override
    public IngredientCategory findIngredientCategoryById(Long id) throws Exception {
        Optional<IngredientCategory> optional = ingredientCategoryRepository.findById(id);
        if (optional.isEmpty()){
            throw new Exception("Ingredient category not found");
        }
        return optional.get();
    }

    @Override
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) throws Exception {
        restaurantService.findRestaurantById(id);
        return ingredientCategoryRepository.findByRestaurantId(id);
    }

    @Override
    public IngredientsItem createIngredientItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = findIngredientCategoryById(categoryId);
        IngredientsItem item = new IngredientsItem();
        item.setName(ingredientName);
        item.setRestaurant(restaurant);
        item.setCategory(category);

        IngredientsItem savedItem = ingredientRepository.save(item);
        category.getIngredientsItems().add(savedItem);
        return savedItem;
    }

    @Override
    public List<IngredientsItem> findRestaurantIngredients(Long restaurantId) {
        return ingredientRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientsItem updateStock(Long id) throws Exception {
        Optional<IngredientsItem> optional = ingredientItemRepository.findById(id);
        if (optional.isEmpty()){
            throw new Exception("Ingredient item not found");
        }
        IngredientsItem item = optional.get();
        item.setInStoke(!item.isInStoke());
        return ingredientItemRepository.save(item);
    }
}
