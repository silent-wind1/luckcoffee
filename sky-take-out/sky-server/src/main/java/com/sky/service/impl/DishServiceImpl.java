package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
//import com.sky.exception.DishErroyException;
import com.sky.exception.DishErroyException;
import com.sky.exception.DishException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(1);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        dishMapper.add(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insert(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dto) {
        log.info("用户id = {}", BaseContext.getCurrentId());
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        if (dto.getName() != null) {
            dto.setName(dto.getName().trim());
        }
        Page<DishVO> page = dishMapper.page(dto);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    public void statusChange(Integer status, Long id) {
        Dish dish = Dish.builder().id(id).status(status).build();
        dishMapper.update(dish);
        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder().id(setmealId).status(StatusConstant.DISABLE).build();
                    setmealMapper.update(setmeal);
                }
            }
        }
        dishMapper.statusChange(status, id);
    }

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    @Transactional
    public void startOrStop(Integer status, Long id) {

    }


    @Override
    public List selecyBycId(Integer categoryId, String name) {
        List<Dish> list = dishMapper.selecyBycId(categoryId, name);

        return list;
    }

    @Override
    public DishVO selectById(Long id) {
        Dish dish = dishMapper.selectById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(List<Long> ids) {
        //判断当前菜品是否能够删除---是否存在起售中的菜品？？
        for (Long id : ids) {
            Dish dish = dishMapper.selectByIds(id);
            if (dish.getStatus() == 1) {
                throw new DishErroyException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断当前菜品是否能够删除---是否被套餐关联了？？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            //当前菜品被套餐关联了，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品表中的菜品数据
        for (Long id : ids) {
            dishMapper.deleteById(ids);
            dishFlavorMapper.deleteById(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DishVO dishVO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishVO, dish);

        dishMapper.update(dish);

        dishFlavorMapper.deleteById(dish.getId());

        List<DishFlavor> flavors = dishVO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            for (DishFlavor flavor : flavors) {

                flavor.setDishId(dish.getId());

            }

            dishFlavorMapper.insert(flavors);
        }

    }

    @Override
    public List getBycateId(Long categoryId) {

        List<DishVO> list = dishMapper.getBycateId(categoryId, StatusConstant.ENABLE);

        for (DishVO dishVO : list) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectById(dishVO.getId());
            dishVO.setFlavors(dishFlavors);
        }
        return list;
    }

}
