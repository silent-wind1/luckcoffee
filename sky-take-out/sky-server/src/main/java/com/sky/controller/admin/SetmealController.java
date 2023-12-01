package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "套餐管理")
@RestController
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    SetmealService setmealService;

    @CacheEvict(cacheNames = "setmealcache",key = "setmealDTO.categoryId")
    @ApiOperation(value = "套餐新增")
    @PostMapping
    public Result insert(@RequestBody SetmealDTO setmealDTO){

        setmealService.insert(setmealDTO);

        return Result.success();
    }


    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result page(SetmealPageQueryDTO setmealPageQueryDTO){

        PageResult page = setmealService.page(setmealPageQueryDTO);

        return Result.success(page);
    }

    @ApiOperation(value = "根据id查询套餐")
    @GetMapping("/{id}")
    public Result getByID(@PathVariable Long id){

        SetmealVO byID = setmealService.getByID(id);

        return Result.success(byID);
    }

    @CacheEvict(cacheNames = "setmealcache",allEntries = true)
    @ApiOperation(value = "修改套餐")
    @PutMapping
    public Result update(@RequestBody SetmealDTO setmealDTO){

        setmealService.update(setmealDTO);

        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealcache",allEntries = true)
    @ApiOperation(value = "批量删除")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }

    @CacheEvict(cacheNames = "setmealcache",allEntries = true)
    @ApiOperation(value = "修改状态")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status, Long id){

        setmealService.status(status,id);


        return Result.success();
    }
}
