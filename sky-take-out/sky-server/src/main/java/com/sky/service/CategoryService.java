package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void insert(CategoryDTO categoryDTO);

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void deleteByid(Long id);

    Category getById(Long id);

    void update(CategoryDTO categoryDTO);

    void updateStatus(Integer status, Long id);

    List<Category> list(Integer type);

    List<Category> getCategory();
}
