package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("insert into sky_take_out.category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            " VALUES" +
            " (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);

    Page<Category> page(CategoryPageQueryDTO categoryPageQueryDTO);

    @Delete("delete from sky_take_out.category where id=#{id}")
    void deleteById(Long id);

    @Select("select * from sky_take_out.category where id=#{id}")
    Category getById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    List<Category> list(Integer type);

    @Select("select * from sky_take_out.category where status=#{status} order by type")
    List<Category> getCategory(Integer status);
}

