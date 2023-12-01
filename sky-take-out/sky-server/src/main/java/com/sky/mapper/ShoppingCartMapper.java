package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> selectById(ShoppingCart shoppingCart);


    void update(ShoppingCart shoppingCart);

    @Insert("insert into sky_take_out.shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void add(ShoppingCart shoppingCart);

    @Select("select * from sky_take_out.shopping_cart where user_id=#{currentId}")
    List<ShoppingCart> selectAll(Long currentId);

    @Delete("delete from sky_take_out.shopping_cart where user_id=#{currentId}")
    void deleteAll(Long currentId);


    void deleteById(ShoppingCart shoppingCart);
}
