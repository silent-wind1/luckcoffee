package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.config.WebSocketServer;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.OrderException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    AddressBookMapper addressBookMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;

//    用户端下单
    @Override
    public OrderSubmitVO ordersSubmit(OrdersSubmitDTO submitDTO) {
//        根据地址id获取地址
        AddressBook addressBook = addressBookMapper.getById(submitDTO.getAddressBookId());
//        判断地址是否在地址薄中存在
        if (addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
//        获取当前用户购物车中所有商品
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectAll(BaseContext.getCurrentId());
//        判断购物车是否为空
        if (shoppingCarts==null || shoppingCarts.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
//        创建一个订单
        Orders order = new Orders();
        BeanUtils.copyProperties(submitDTO,order);
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        order.setUserId(BaseContext.getCurrentId());
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setPayStatus(Orders.UN_PAID);
        order.setOrderTime(LocalDateTime.now());
//      将当前订单存入数据库
        orderMapper.insert(order);
//      将当前订单购物车中的物品加入数据库
        for (ShoppingCart shoppingCart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            orderDetail.setOrderId(order.getId());
            orderMapper.insertDetail(orderDetail);
        }
//      返回订单的id，订单号，金额，下单时间
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(order.getId());
        orderSubmitVO.setOrderAmount(order.getAmount());
        orderSubmitVO.setOrderNumber(order.getNumber());
        orderSubmitVO.setOrderTime(order.getOrderTime());
        shoppingCartMapper.deleteAll(BaseContext.getCurrentId());
        return orderSubmitVO;
    }

//    用户端分页查询
    @Override
    public PageResult page(Integer page, Integer pageSize, Integer status) {
        List<OrderVO> orderVOList = new ArrayList<>();
//        分页查询订单
        PageHelper.startPage(page,pageSize);
        Page<Orders> pageSelect = orderMapper.pageSelect(BaseContext.getCurrentId(),status);
        List<Orders> result = pageSelect.getResult();
        for (Orders orders : result) {
//            获取订单关联的菜品或套餐
            List<OrderDetail> list = orderMapper.getByUserId(orders.getId());
//            将订单信息及订单中包含的菜品封装
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDetailList(list);
            orderVOList.add(orderVO);
        }
        return new PageResult(pageSelect.getTotal(),orderVOList);
    }

//    用户端支付
    @Override
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
//    .........调用微信支付接口

//    修改订单状态，支付方式等
        Orders orders = Orders.builder()
                .number(ordersPaymentDTO.getOrderNumber())
                .userId(BaseContext.getCurrentId())
                .payMethod(ordersPaymentDTO.getPayMethod())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();
        orderMapper.updateStatus(orders);

            Map map = new HashMap();
            map.put("type", 1);
            map.put("orderId", orders.getId());
            map.put("content", "订单号：" + ordersPaymentDTO.getOrderNumber());

            //通过WebSocket实现来单提醒，向客户端浏览器推送消息
            webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

//    根据订单id查询订单详情
    @Override
    public OrderVO selectById(Long id) {
//        根据id获取订单详情
        Orders orders = orderMapper.getByOrderId(id);
//        根据订单id获取与订单关联的菜品或套餐信息
        List<OrderDetail> byUserId = orderMapper.getByUserId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(byUserId);
        return orderVO;
    }

//用户端取消订单
    @Override
    public void cancel(Long id) {
//        根据id获取订单详情
        Orders ordersDB  = orderMapper.getByOrderId(id);
//        校验订单是否存在
        if (ordersDB  == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB .getStatus() > 2){
            throw new OrderException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

//          订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
        }

//      用户端再来一单
    @Override
    public void repetition(Long id) {
//        创建购物车对象
        ShoppingCart shoppingCart = new ShoppingCart();
//        根据订单id,找出当前订单中对应的商品
        List<OrderDetail> orderDetailList = orderMapper.getByUserId(id);
//        重新将商品加入购物车
        for (OrderDetail orderDetail : orderDetailList) {
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.add(shoppingCart);
        }
    }

//    管理端分页查询
    @Override
    public PageResult adminPage(OrdersPageQueryDTO ordersPageQueryDTO) {
//        分页查询订单
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.adminPage(ordersPageQueryDTO);
        List<Orders> result = page.getResult();
        ArrayList<OrderVO> orderVOS = new ArrayList<>();
//        将每个订单对应的商品拼接成字符串
        for (Orders orders : result) {
            List<OrderDetail> list = orderMapper.getByUserId(orders.getId());
            ArrayList<String> list1 = new ArrayList<>();
            for (OrderDetail orderDetail : list) {
                String s = orderDetail.getName().toString()+"*"+orderDetail.getNumber();
                list1.add(s);
            }
            String join = String.join("", list1);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders,orderVO);
            orderVO.setOrderDishes(join);
            orderVOS.add(orderVO);
        }
        return new PageResult(page.getTotal(),orderVOS);
    }

//    管理端查询订单详情
    @Override
    public OrderVO details(Long id) {
//        根据id查询商品
        List<OrderDetail> list = orderMapper.getByUserId(id);
//        根据id查询订单详情
        Orders orders = orderMapper.getByOrderId(id);
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderDetailList(list);
        BeanUtils.copyProperties(orders,orderVO);
        return orderVO;
    }

//    根据状态查询当前状态订单总数
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
//        待接单
        orderStatisticsVO.setToBeConfirmed(orderMapper.selectCount(2));
//        待派送
        orderStatisticsVO.setConfirmed(orderMapper.selectCount(3));
//        派送中
        orderStatisticsVO.setDeliveryInProgress(orderMapper.selectCount(4));
        return orderStatisticsVO;
    }

//    管理端接单
    @Override
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
//       根据id拿出订单.判断当前订单状态
        Orders orderId = orderMapper.getByOrderId(ordersConfirmDTO.getId());
        if (orderId.getStatus()!=2){
            throw new OrderException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);
        orderMapper.update(orders);
    }

//    管理端拒单
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
//        修改订单状态,支付状态等
        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setCancelTime(LocalDateTime.now());
        orders.setPayStatus(Orders.REFUND);

        orderMapper.update(orders);
    }

//    管理端取消订单
    @Override
    public void admincancel(OrdersCancelDTO ordersCancelDTO) {
//        查询当前订单状态,如果已完成则不能取消
        Orders orderId = orderMapper.getByOrderId(ordersCancelDTO.getId());
        if (orderId.getStatus()==5){
            throw new OrderException(MessageConstant.ORDER_STATUS_ERROR);
        }
//        修改订单状态,取消原因等
        Orders orders = new Orders();
        orders.setPayStatus(Orders.REFUND);
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

//    派送订单
    @Override
    public void delivery(Long id) {
//        根据id拿出订单,判断订单是否存在
        Orders orders = orderMapper.getByOrderId(id);
        if (orders==null){
            throw new OrderBusinessException("订单不存在");
        }
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(Orders.DELIVERY_IN_PROGRESS);

        orderMapper.update(order);
    }

//    管理端完成订单
    @Override
    public void complete(Long id) {

//        根据id拿出订单,判断订单是否存在
        Orders orders = orderMapper.getByOrderId(id);
        if (orders==null){
            throw new OrderBusinessException("订单不存在");
        }
        Orders order = new Orders();
        order.setId(id);
        order.setStatus(Orders.COMPLETED);
        order.setDeliveryTime(LocalDateTime.now());
        System.out.println(order);
        orderMapper.update(order);

    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getByOrderId(id);
        if (orders==null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Map map = new HashMap<>();
        map.put("type",2);
        map.put("orderId", id);
        map.put("content","订单号："+orders.getNumber());
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }

}

