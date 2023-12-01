package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
public class UserCategoryController {
    @Autowired
    CategoryService categoryService;

    @ApiOperation("查询分类")
    @GetMapping("/list")
    public Result getCategory(){

        List<Category> category = categoryService.getCategory();


        return Result.success(category);
    }

}
