package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "购物车")
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){

        shoppingCartService.add(shoppingCartDTO);

        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("查看购物车")
    public Result selectAll(){

        List select = shoppingCartService.select();

        return Result.success(select);

    }

    @DeleteMapping("/clean")
    @ApiOperation("清空购物车商品")
    public Result deleteAll(){
        shoppingCartService.deleteAll();
        return Result.success();
    }

    @ApiOperation("删除购物车")
    @PostMapping("/sub")
    public Result deleteById(@RequestBody ShoppingCartDTO shoppingCartDTO){

        shoppingCartService.deleteById(shoppingCartDTO);

        return Result.success();
    }
}
