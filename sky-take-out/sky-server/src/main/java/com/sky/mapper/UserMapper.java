package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from sky_take_out.user where openid=#{openid}")
    User getByOpenid(String openid);

    @Insert("insert into sky_take_out.user(openid, name, phone, sex, id_number, avatar, create_time) values " +
            "(#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    void insert(User user);

    @Select("select * from sky_take_out.user where id=#{userId}")
    User getById(Long userId);

    @Select("select * from user")
    List<User> selectAll();

    @Select("select * from sky_take_out.user where create_time between #{start} and #{last} ")
    List<User> selectUserByTime(LocalDateTime start, LocalDateTime last);
}
