package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetmealDishMapper {
    public List<Long> getSetmealIdsByDishIds(List<Long> ids);

    void insert(List<SetmealDish> setmealDishes);

    @Select("select * from sky_take_out.setmeal_dish where setmeal_id =  #{id}")
    List<SetmealDish> getByID(Long id);

    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{id}")
    void delete(Long integer);

    @Delete("delete  from sky_take_out.setmeal_dish where setmeal_id=#{id}")
    void deleteById(Long id);

    @Select("SELECT s.status from sky_take_out.setmeal_dish as sd,sky_take_out.setmeal as s " +
            "where dish_id = #{id} and setmeal_id = s.id")
    List<Integer> getByDishID(Long id);

    @Select("SELECT d.status from sky_take_out.setmeal_dish as sd,sky_take_out.dish as d " +
            "where setmeal_id = #{id} and dish_id = d.id")
    List<Integer> getBySdmID(Long id);
}
