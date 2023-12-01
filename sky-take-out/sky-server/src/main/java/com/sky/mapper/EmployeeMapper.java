package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from sky_take_out.employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("insert into sky_take_out.employee(name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    void save(Employee employee);

    Page<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);

    //    @Update("update sky_take_out.employee set status=#{status} where id=#{id}")
    @AutoFill(OperationType.UPDATE)
    void update(Employee build);

    @Select("select * from sky_take_out.employee where id = #{id}")
    Employee getByid(Long id);

    @Update("update sky_take_out.employee set password=#{newPassword} where id=#{empId} and password=#{oldPassword}")
    void updateps(PasswordEditDTO passwordEditDTO);
}
