package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void add(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dto);

    void statusChange(Integer status, Long id);

    List selecyBycId(Integer categoryId, String name);

    DishVO selectById(Long id);

    void deleteById(List<Long> ids);

    void update(DishVO dishVO);

    List getBycateId(Long categoryId);

}
