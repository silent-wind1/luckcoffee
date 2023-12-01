package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "分类管理")
@RestController
@RequestMapping("/admin/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "新增分类 ")
    @PostMapping
    public Result insert(@RequestBody CategoryDTO categoryDTO){

        categoryService.insert(categoryDTO);

        return Result.success();
    }

    @ApiOperation(value = "分类分页查询 ")
    @GetMapping("/page")
    public Result page( CategoryPageQueryDTO  categoryPageQueryDTO){

        PageResult page = categoryService.page(categoryPageQueryDTO);

        return Result.success(page);
    }

    @ApiOperation(value = "根据id删除分类 ")
    @DeleteMapping
    public Result delete(@RequestParam Long id){
        categoryService.deleteByid(id);

        return Result.success();
    }

    @ApiOperation(value = "批量删除分类 ")
    @GetMapping("/{id}")
    public Result getById(Long id){
        Category byId = categoryService.getById(id);

        return Result.success(byId);
    }
    @ApiOperation(value = "修改分类 ")
    @PutMapping()
    public Result update(@RequestBody CategoryDTO categoryDTO){

        categoryService.update(categoryDTO);

        return Result.success();

    }

    @ApiOperation(value = "修改售卖状态 ")
    @PostMapping("/status/{status}")
    public Result upStatus(@PathVariable Integer status,Long id){

        categoryService.updateStatus(status,id);

        return Result.success();
    }

    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
