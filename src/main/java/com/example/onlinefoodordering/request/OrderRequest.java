package com.example.onlinefoodordering.request;

import com.example.onlinefoodordering.model.Address;
import lombok.Data;

@Data
public class OrderRequest {

    private Long restaurantId;
    private Address deliveryAddress;
}
