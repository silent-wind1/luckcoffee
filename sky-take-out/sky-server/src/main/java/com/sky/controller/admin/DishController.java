package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Api(tags = "菜品管理")
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 清理缓存数据
     * @param pattern
     */
    private void   cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

    @ApiOperation(value = "新增菜品 ")
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){
        dishService.add(dishDTO);
//        String key = "dish_" + dishDTO.getCategoryId();
//        cleanCache(key);
        return Result.success();
    }

    @ApiOperation(value = "分页查询菜品 ")
    @GetMapping("/page")
    public Result page(DishPageQueryDTO dto){
        PageResult page = dishService.page(dto);
        return Result.success(page);
    }

    @ApiOperation(value = "修改状态 ")
    @PostMapping("/status/{status}")
    public Result statusChange(@PathVariable Integer status,Long id){
        dishService.statusChange(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation(value = "根据分类id查询菜品 ")
    @GetMapping("/list")
    public Result selectBycId( Integer categoryId,String name){
        List list = dishService.selecyBycId(categoryId,name);
        return Result.success(list);
    }

    @ApiOperation(value = "根据id查询菜品 ")
    @GetMapping("/{id}")
    public Result selectById(@PathVariable Long id){
        DishVO dishVO = dishService.selectById(id);
        return Result.success(dishVO);
    }

    @ApiOperation(value = "修改菜品 ")
    @PutMapping
    public Result update(@RequestBody DishVO dishVO){
        dishService.update(dishVO);
        cleanCache("dish_*");
        return Result.success();
    }

    @ApiOperation(value = "批量删除菜品 ")
    @DeleteMapping
    public Result deleteById(@RequestParam List<Long> ids){
        dishService.deleteById(ids);
        cleanCache("dish_*");
        return Result.success();
    }
}
