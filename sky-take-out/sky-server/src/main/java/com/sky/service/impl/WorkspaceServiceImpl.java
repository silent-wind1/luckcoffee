package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    SetmealMapper setmealMapper;

    @Autowired
    DishMapper  dishMapper;

    @Override
    public BusinessDataVO businessData() {
        BusinessDataVO businessDataVO = new BusinessDataVO();
//        今日新增用户
        LocalDate now = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(now, LocalTime.MAX);
        List<User> users = userMapper.selectUserByTime(begin, end);
        businessDataVO.setNewUsers(users.size());

//        订单完成率
        List<Orders> orders = orderMapper.selectByTime(begin, end, Orders.COMPLETED);
        List<Orders> orders1 = orderMapper.selectByTime(begin, end, null);
        if (orders1.size()>0){
            double size = orders.size();
            double size1 = orders1.size();
            businessDataVO.setOrderCompletionRate(Double.valueOf(size/size1));

//        营业额
            BigDecimal acm = new BigDecimal(0);
            for (Orders order : orders) {
            acm = acm.add(order.getAmount());
        }
        businessDataVO.setTurnover(acm.doubleValue());

//        平均客单价
            Double aDouble = Double.valueOf(acm.doubleValue() / orders.size());
        businessDataVO.setUnitPrice(aDouble);

//        有效订单数
        businessDataVO.setValidOrderCount(orders.size());
        }else{
            businessDataVO.setOrderCompletionRate(0.0);
            businessDataVO.setTurnover(0.0);
            businessDataVO.setUnitPrice(0.0);
            businessDataVO.setValidOrderCount(0);
        }

        return businessDataVO;
    }

    @Override
    public SetmealOverViewVO overviewSetmeals() {
//        起售
       Integer start = setmealMapper.selectBystatus(1);
//       停售
       Integer stop = setmealMapper.selectBystatus(0);
        SetmealOverViewVO setmealOverViewVO = new SetmealOverViewVO(start, stop);
        return setmealOverViewVO;
    }

    @Override
    public DishOverViewVO overviewDishes() {
        //        起售
       Integer start = dishMapper.selectByStatus(1);
        //       停售
       Integer end = dishMapper.selectByStatus(0);
        DishOverViewVO dishOverViewVO = new DishOverViewVO(start, end);
        return dishOverViewVO;
    }

    @Override
    public OrderOverViewVO overviewOrders() {
        LocalDateTime begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        int allOrders = orderMapper.selectByTime(begin, end, null).size();
        int cancelledOrders = orderMapper.selectByTime(begin, end, 6).size();
        int completedOrders = orderMapper.selectByTime(begin, end, 5).size();
        int deliveredOrders = orderMapper.selectByTime(begin, end, 3).size();
        int waitingOrders = orderMapper.selectByTime(begin, end, 2).size();
        OrderOverViewVO build = OrderOverViewVO.builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders).build();
        return build;
    }
}
