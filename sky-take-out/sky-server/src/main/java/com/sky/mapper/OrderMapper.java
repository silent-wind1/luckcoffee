package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders order);

    void insertDetail(OrderDetail orderDetail);

    Page<Orders> pageSelect(Long currentId, Integer status);

    @Select("select * from sky_take_out.order_detail where order_id=#{id}")
    List<OrderDetail> getByUserId(Long id);

    @Update("update sky_take_out.orders set status=#{status},pay_status=#{payStatus}" +
            ",checkout_time=#{checkoutTime} where pay_method=#{payMethod} and " +
            "user_id=#{userId} and number=#{number}")
    void updateStatus(Orders orders);

    @Select("select * from sky_take_out.orders as od where id=#{id}")
    Orders getByOrderId(Long id);

    void update(Orders orders);


    Page<Orders> adminPage(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select count(*) from sky_take_out.orders where status=#{status}")
    Integer selectCount(Integer status);

    @Select("select * from sky_take_out.orders where status=#{pendingPayment} and order_time<#{localDateTime}")
    List<Orders> getByTimeAndStatus(Integer pendingPayment, LocalDateTime localDateTime);

    List<Orders> selectByTime(LocalDateTime begin, LocalDateTime end,Integer status);

    List<GoodsSalesDTO> selectdetail(LocalDateTime start, LocalDateTime last, Integer status);

    @Select("SELECT COUNT(*) FROM sky_take_out.orders")
    Integer selectAll();

}
