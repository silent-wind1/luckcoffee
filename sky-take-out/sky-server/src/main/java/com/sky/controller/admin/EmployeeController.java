package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工管理")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    @ApiOperation(value = "修改密码")
    @PutMapping("/editPassword")
    public Result updateps(@RequestBody PasswordEditDTO passwordEditDTO) {
        employeeService.updateps(passwordEditDTO);

        return Result.success();
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employee) {

        employeeService.save(employee);
        return Result.success();
    }

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        PageResult page = employeeService.page(employeePageQueryDTO);
        return Result.success(page);
    }

    @ApiOperation(value = "修改状态")
    @PostMapping("/status/{status}")
    public Result status(@RequestParam Long id, @PathVariable Integer status) {
        employeeService.startOrstop(status, id);
        return Result.success();
    }

    @ApiOperation(value = "根据id获取")
    @GetMapping("/{id}")
    public Result getByid(@PathVariable Long id) {
        Employee employee = employeeService.getByid(id);
        return Result.success(employee);
    }

    @ApiOperation(value = "修改员工信息")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employee) {

        employeeService.update(employee);
        return Result.success();
    }
}
