package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "工作台")
@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    WorkspaceService workspaceService;

    @ApiOperation("查看今日运营数据")
    @GetMapping("/businessData")
    public Result businessData(){

        BusinessDataVO businessDataVO =workspaceService.businessData();

       return Result.success(businessDataVO);
    }

    @ApiOperation("查看套餐情况")
    @GetMapping("/overviewSetmeals")
    public Result overviewSetmeals(){

        SetmealOverViewVO setmeal =workspaceService.overviewSetmeals();

       return Result.success(setmeal);
    }

    @ApiOperation("查看菜品情况")
    @GetMapping("/overviewDishes")
    public Result overviewDishes(){

        DishOverViewVO dish =workspaceService.overviewDishes();

       return Result.success(dish);
    }

    @ApiOperation("查看订单管理数据")
        @GetMapping("/overviewOrders")
        public Result overviewOrders(){

        OrderOverViewVO orders =workspaceService.overviewOrders();

           return Result.success(orders);
        }

}
