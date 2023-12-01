package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

import java.util.List;

public interface ShoppingCartService {
    void add(ShoppingCartDTO shoppingCartDTO);

    List select();

    void deleteAll();

    void deleteById(ShoppingCartDTO shoppingCartDTO);
}
