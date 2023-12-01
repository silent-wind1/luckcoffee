package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shop")
@Api(tags = "用户端店铺相关接口")
public class ShopController {

    @Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/status")
    public Result getStatus(){
        Integer showStatus = (Integer)redisTemplate.opsForValue().get("showStatus");
        return Result.success(showStatus);
    }



}
