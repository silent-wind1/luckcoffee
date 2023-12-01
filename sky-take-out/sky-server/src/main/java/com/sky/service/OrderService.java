package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {
    OrderSubmitVO ordersSubmit(OrdersSubmitDTO submitDTO);


    PageResult page(Integer page, Integer pageSize, Integer status);

    void payment(OrdersPaymentDTO ordersPaymentDTO);

    OrderVO selectById(Long id);

    void cancel(Long id);

    void repetition(Long id);

    PageResult adminPage(OrdersPageQueryDTO ordersPageQueryDTO);

    OrderVO details(Long id);

    OrderStatisticsVO statistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void admincancel(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);

    void reminder(Long id);

}
