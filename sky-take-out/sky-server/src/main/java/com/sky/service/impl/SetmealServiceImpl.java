package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.exception.SetmealStatusFailedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmeal.setStatus(0);
        setmealMapper.insert(setmeal);
        Long id = setmeal.getId();
        System.out.println(id);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(id);
        }
        setmealDishMapper.insert(setmealDishes);
    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public SetmealVO getByID(Long id) {

        SetmealVO setmealVO = setmealMapper.getByID(id);

        List<SetmealDish> sd = setmealDishMapper.getByID(id);

        setmealVO.setSetmealDishes(sd);

        return setmealVO;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {

        for (Long id : ids) {
            Setmeal dish = setmealMapper.selectByIds(id);
            if (dish.getStatus()==1){
                throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setmealMapper.delete(ids);
        for (Long integer : ids) {
            setmealDishMapper.delete(integer);
        }

    }

    @Override
    public void status(Integer status, Long id) {
        if (status==1) {
            List<Integer> bySdmID = setmealDishMapper.getBySdmID(id);
            for (Integer integer : bySdmID) {
                if (integer == 0) {
                    throw new SetmealStatusFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        setmealMapper.status(status,id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SetmealDTO setmealDTO) {
        setmealDTO.setStatus(0);
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishMapper.deleteById(setmeal.getId());

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insert(setmealDishes);
    }

    @Override
    public List getBycateId(Long categoryId) {

        List<Setmeal> bycateId = setmealMapper.getBycateId(categoryId, StatusConstant.ENABLE);

        return bycateId;
    }

    @Override
    public List<DishItemVO> getBydishId(Long id) {

        List<DishItemVO> list = setmealMapper.getBydishId(id);

        return list;
    }
}
