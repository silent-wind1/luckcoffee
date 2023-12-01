package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/setmeal")
@Api(tags = "C端-菜品接口")
public class UserSetmealController {

    @Autowired
    SetmealService setmealService;

    @Cacheable(cacheNames = "setmealcache",key = "#categoryId")
    @ApiOperation("根据分类id查询套餐")
    @GetMapping("/list")
    public Result getBycateId( Long categoryId){

        List dishVos = setmealService.getBycateId(categoryId);

        return Result.success(dishVos);
    }

    @ApiOperation("根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result getBydishId(@PathVariable Long id){

        List<DishItemVO> dishVOS = setmealService.getBydishId(id);

        return Result.success(dishVOS);
    }
}
