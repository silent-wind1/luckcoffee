package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface  SetmealMapper {

    @Select("select count(*) from sky_take_out.setmeal where category_id = #{id}")
    Integer countByCategoryId(Long id);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    @Select("select s.*,c.name as categoryName " +
            "from sky_take_out.setmeal as s,sky_take_out.category as c " +
            "where s.id=#{id} and c.id = s.category_id")
    SetmealVO getByID(Long id);

    void delete(List<Long> ids);

    @Select("select * from sky_take_out.setmeal where id=#{id}")
    Setmeal selectByIds(Long id);

    @Update("update sky_take_out.setmeal set status=#{status} where id = #{id}")
    void status(Integer status, Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Select("select * from sky_take_out.setmeal where category_id=#{categoryId} and status=#{status}")
    List<Setmeal> getBycateId(Long categoryId,Integer status);

    @Select("select d.image, d.name,d.description,sd.copies from sky_take_out.dish as d,sky_take_out.setmeal_dish as sd " +
            "where setmeal_id=#{id} and dish_id=d.id")
    List<DishItemVO> getBydishId(Long id);

    @Select("select count(*) from sky_take_out.setmeal where status=#{status}")
    Integer selectBystatus(Integer status);
}
