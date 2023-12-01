package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {


    @AutoFill(OperationType.INSERT)
    void add(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dto);

    @Update("update sky_take_out.dish set status=#{status} where id = #{id}")
    void statusChange(Integer status, Long id);


    List<Dish> selecyBycId(Integer categoryId, String name);

    @Select("select d.*,c.name from sky_take_out.dish as d,sky_take_out.category as c where d.id=#{id} and d.category_id=c.id")
    Dish selectById(Long id);

    void deleteById(List<Long> ids);

    @Select("select * from sky_take_out.dish where id = #{id}")
    Dish selectByIds(Long id);

    @Select("select count(*) from sky_take_out.dish where category_id=#{id}")
    Integer select(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    @Select("select * from sky_take_out.dish where category_id=#{categoryId} and status=#{status}")
    List<DishVO> getBycateId(Long categoryId, Integer status);


    @Select("select d.* from sky_take_out.dish as d " +
            "where id=#{id}")
    Dish getById(Long id);

    @Select("select count(*) from sky_take_out.dish where status=#{status}")
    Integer selectByStatus(int i);
}
