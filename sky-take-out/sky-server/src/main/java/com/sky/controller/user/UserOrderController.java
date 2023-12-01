package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "用户端店铺相关接口")
public class UserOrderController {

    @Autowired
    OrderService orderService;

    @ApiOperation("下单")
    @PostMapping("/submit")
    public Result OrdersSubmit(@RequestBody OrdersSubmitDTO submitDTO){

        OrderSubmitVO submitVO = orderService.ordersSubmit(submitDTO);

        return Result.success(submitVO);
    }

    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO){
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    @ApiOperation("历史订单查询")
    @GetMapping("/historyOrders")
    public Result historyOrders(@RequestParam Integer page,Integer pageSize,Integer status){

        PageResult page1 = orderService.page(page, pageSize, status);

        return Result.success(page1);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result selectById(@PathVariable Long id) {
        OrderVO orderVO = orderService.selectById(id);
        return Result.success(orderVO);
    }


    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id){

        orderService.cancel(id);

        return Result.success();
    }

    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("用户催单")
    public Result reminder(@PathVariable Long id) {
        orderService.reminder(id);
        return Result.success();
    }
}
