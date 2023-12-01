package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void insert(SetmealDTO setmealDTO);

    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getByID(Long id);

    void delete(List<Long> ids);

    void status(Integer status, Long id);

    void update(SetmealDTO setmealDTO);

    List getBycateId(Long categoryId);

    List<DishItemVO> getBydishId(Long id);
}
