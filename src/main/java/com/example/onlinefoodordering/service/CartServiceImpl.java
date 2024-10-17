package com.example.onlinefoodordering.service;

import com.example.onlinefoodordering.model.Cart;
import com.example.onlinefoodordering.model.CartItem;
import com.example.onlinefoodordering.model.Food;
import com.example.onlinefoodordering.model.User;
import com.example.onlinefoodordering.repository.CartItemRepository;
import com.example.onlinefoodordering.repository.CartRepository;
import com.example.onlinefoodordering.request.AddCartItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FoodService foodService;

    @Override
    public CartItem addItemToCart(AddCartItemRequest request, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Food food = foodService.findFoodById(request.getFoodId());
        Cart cart = cartRepository.findByCustomerId(user.getId());

        for (CartItem cartItem: cart.getItem()){
            if ( cartItem.getFood().equals(food)){
                int newQuantity = cartItem.getQuantity() + request.getQuantity();
                return updateCartItemQuantity(cartItem.getId(), newQuantity);
            }
        }

        CartItem cartItem = new CartItem();
        cartItem.setFood(food);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setCart(cart);
        cartItem.setIngredients(request.getIngredients());
        cartItem.setTotalPrice(food.getPrice() * request.getQuantity());

        CartItem savedCartItem = cartItemRepository.save(new CartItem());
        cart.getItem().add(savedCartItem);

        return savedCartItem;
    }

    @Override
    public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem.isEmpty()){
            throw new Exception("CartItem not found");
        }
        CartItem item = cartItem.get();
        item.setQuantity(quantity);

        item.setTotalPrice(item.getFood().getPrice() * quantity);

        return cartItemRepository.save(item);
    }

    @Override
    public Cart removeItemFromCart(Long cartItemId, String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartRepository.findByCustomerId(user.getId());

        Optional<CartItem> cartItem = cartItemRepository.findById(cartItemId);
        if (cartItem.isEmpty()){
            throw new Exception("CartItem not found");
        }

        CartItem item = cartItem.get();

        cart.getItem().remove(item);
        return cartRepository.save(cart);
    }

    @Override
    public Long calculatorCartTotals(Cart cart) throws Exception {
        Long total = 0L;
        for (CartItem cartItem: cart.getItem()){
            total += cartItem.getFood().getPrice() * cartItem.getQuantity();
        }

        return total;
    }

    @Override
    public Cart findCartById(Long id) throws Exception {
        Optional<Cart> optionalCart = cartRepository.findById(id);
        if (optionalCart.isEmpty()){
            throw new Exception("CartItem not found with id" + id);
        }
        return optionalCart.get();
    }

    @Override
    public Cart findCartByUserId(Long userId) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
       Cart cart = cartRepository.findByCustomerId(userId);
       cart.setTotal(calculatorCartTotals(cart));
       return cart;
    }

    @Override
    public Cart clearCart(Long userId) throws Exception {
//        User user = userService.findUserByJwtToken(jwt);
        Cart cart = findCartByUserId(userId);

        cart.getItem().clear();
        return cartRepository.save(cart);
    }
}
