package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dish")
public class UserDishController {
    @Autowired
    DishService dishService;

    @Autowired
    RedisTemplate redisTemplate;

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result getBycateId(Long categoryId){
        String key ="dish_"+categoryId;

        List<DishVO> list = (List<DishVO>)redisTemplate.opsForValue().get(key);
        if (list!=null&&list.size()>0){
            return Result.success(list);
        }

        List dishVos = dishService.getBycateId(categoryId);

        redisTemplate.opsForValue().set(key, dishVos);
        return Result.success(dishVos);
    }
}
