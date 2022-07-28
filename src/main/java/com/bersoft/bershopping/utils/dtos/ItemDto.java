package com.bersoft.bershopping.utils.dtos;

public record ItemDto(
        long productId,
        String productDesc,
        double price,
        double qty,
        double productStock) {
}
