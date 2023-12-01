package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

//    购物车新增
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
//    查询当前用户的购物车,判断当前购物车中是否存在当前相同的商品(包括口味也需要相同)
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectById(shoppingCart);
//    有相同的商品,直接将数量+1
        if (shoppingCartList != null && shoppingCartList.size() == 1) {
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
        } else {//    没有相同的商品,判断是套餐还是菜品
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
//                菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
//                套餐
                Setmeal setmeal = setmealMapper.selectByIds(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.add(shoppingCart);
        }
    }
//查询购物车
    @Override
    public List select() {

        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectAll(BaseContext.getCurrentId());

        return shoppingCarts;
    }

//    清空购物车
    @Override
    public void deleteAll() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(currentId);

    }

//    某个商品减少一个
    @Override
    public void deleteById(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
//        判断当前商品购物车中是否存在
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectById(shoppingCart);
        System.out.println("购物车"+shoppingCartList);
//        当前商品存在
        if (shoppingCartList != null && shoppingCartList.size() == 1) {
            shoppingCart = shoppingCartList.get(0);
//            判断是否是最后一个
            if (shoppingCart.getNumber() >= 2) {
//                当前商品数量为1,将当前商品从购物车中删除
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.update(shoppingCart);
            } else {
//                将当前商品数量减一
                shoppingCartMapper.deleteById(shoppingCart);
            }
        }
    }
}
