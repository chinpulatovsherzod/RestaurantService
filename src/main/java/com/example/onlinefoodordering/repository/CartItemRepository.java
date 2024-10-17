package com.example.onlinefoodordering.repository;

import com.example.onlinefoodordering.model.Cart;
import com.example.onlinefoodordering.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {


}
