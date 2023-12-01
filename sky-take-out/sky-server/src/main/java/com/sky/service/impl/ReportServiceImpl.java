package com.sky.service.impl;

import com.sky.config.ExportExcel;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.entity.User;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public TurnoverReportVO turnoverReport(LocalDate begin, LocalDate end) {
        String dateList = "";
        String turnoverList = "";
        ArrayList<String> list = new ArrayList<>();
        while (begin.isBefore(end) || begin.isEqual(end)){
        LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime last = LocalDateTime.of(begin, LocalTime.MAX);

//        时间列表
        if (!begin.isEqual(end)){
        dateList =  dateList +begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+",";}
        else{
            dateList =  dateList +begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

//      营业额列表
        BigDecimal bigDecimal = new BigDecimal(0);
        List<Orders> orders = orderMapper.selectByTime(start,last,Orders.COMPLETED);
           if (orders!=null && orders.size()>0) {
            for (Orders order : orders) {
                bigDecimal = bigDecimal.add(order.getAmount());
            }
               list.add(bigDecimal.toString());
               } else{
               list.add(String.valueOf(0));
        }
        begin=begin.plusDays(1);
       }
        turnoverList = String.join(",", list);
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setTurnoverList(turnoverList);
        turnoverReportVO.setDateList(dateList);
        return turnoverReportVO;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        String dateList = "";
        String newUserList = "";
        String totalUserList = "";
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        while (begin.isBefore(end) || begin.isEqual(end)){
            LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime last = LocalDateTime.of(begin, LocalTime.MAX);
//            时间列表
            list.add(begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//            总用户量
            List<User> selectall = userMapper.selectAll();
            list1.add(String.valueOf(selectall.size()));
//            每日新增用户量
            List<User> users = userMapper.selectUserByTime(start,last);
            if (users!=null && users.size()>0) {
                list2.add(String.valueOf(users.size()));
            }else{
                list2.add(String.valueOf(0));
            }
            begin=begin.plusDays(1);
        }
        dateList = String.join(",",list);
        totalUserList = String.join(",",list1);
        newUserList  = String.join(",",list2);
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(dateList);
        userReportVO.setNewUserList(newUserList);
        userReportVO.setTotalUserList(totalUserList);
        return userReportVO;
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        String dateList = "";
        String orderCountList = "";
        String validOrderCountList = "";
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<Orders> orders = orderMapper.selectByTime(beginTime, endTime,null);
        Integer totalOrderCount = orders.size();
        List<Orders> orders1 = orderMapper.selectByTime(beginTime, endTime, Orders.COMPLETED);
        Integer validOrderCount = orders1.size();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        while (begin.isBefore(end) || begin.isEqual(end)){
            LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime last = LocalDateTime.of(begin, LocalTime.MAX);
            if (!begin.isEqual(end)) {
                dateList = dateList + begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ",";
            }else{
                dateList = dateList + begin.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            List<Orders> all = orderMapper.selectByTime(start,last, null);
            if (all!=null && all.size()>0){
                list.add(String.valueOf(all.size()));
            }else{
                list.add(String.valueOf(0));
            }
            List<Orders> conpleted = orderMapper.selectByTime(start,last, Orders.COMPLETED);
            if (conpleted!=null && conpleted.size()>0){
                list1.add(String.valueOf(conpleted.size()));
            }else{
                list1.add(String.valueOf(0));
            }
            begin=begin.plusDays(1);
        }
        if (totalOrderCount==0){
            return new OrderReportVO("0","0","0",0,0,0.0);
        }else{
            orderCountList = String.join(",",list);
            validOrderCountList =  String.join(",",list1);
            double validOrderCount1 = validOrderCount;
            double totalOrderCount1 = totalOrderCount;
            Double t = Double.valueOf(validOrderCount1/totalOrderCount1);
            System.out.println(t);
            OrderReportVO build = OrderReportVO.builder()
                    .validOrderCount(validOrderCount)
                    .totalOrderCount(totalOrderCount)
                    .validOrderCountList(validOrderCountList)
                    .dateList(dateList)
                    .orderCountList(orderCountList)
                    .orderCompletionRate(t).build();
            return build;
        }
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        LocalDateTime start = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime last = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOS = orderMapper.selectdetail(start, last, Orders.COMPLETED);
       String nameList = "";
       String numberList = "";
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
            if (goodsSalesDTO.getName()==goodsSalesDTOS.get(0).getName()){
                nameList = nameList+goodsSalesDTO.getName().toString();
                numberList = numberList+goodsSalesDTO.getNumber().toString();
            }else {
                nameList = nameList + "," + goodsSalesDTO.getName().toString();
                numberList = numberList + "," + goodsSalesDTO.getNumber().toString();
            }
        }
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    @Override
    public void export(HttpServletRequest request, HttpServletResponse response) {
            //创建excel表头
            List<String> column = new ArrayList<>();
            column.add("时间");
            column.add("营业额");
            column.add("用户总量");
            column.add("新增用户");
            column.add("订单总数");
            column.add("有效订单");
            column.add("订单完成率");
            column.add("销量排名TOP10");
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        TurnoverReportVO turnoverReportVO = turnoverReport(begin, end);
        UserReportVO userReportVO = userStatistics(begin, end);
        OrderReportVO orderReportVO = orderStatistics(begin, end);
        SalesTop10ReportVO salesTop10ReportVO = top10(begin, end);
//        时间
        String[] dateList = turnoverReportVO.getDateList().split(",");
//        营业额
        String[] turnoverList = turnoverReportVO.getTurnoverList().split(",");
//        用户总数
        String[] totalUserList = userReportVO.getTotalUserList().split(",");
//        新增用户
        String[] newUserList = userReportVO.getNewUserList().split(",");
//        每日订单总数
        String[] orderCountList = orderReportVO.getOrderCountList().split(",");
//        每日有效订单数
        String[] validOrderCountList = orderReportVO.getValidOrderCountList().split(",");
//        菜品名称
        String[] nameList = salesTop10ReportVO.getNameList().split(",");
//        菜品数量
        String[] numberList = salesTop10ReportVO.getNumberList().split(",");
        //表头对应的数据
            List<Map<String,Object>> data = new ArrayList<>();

            //遍历获取到的需要导出的数据，key要和表头一样
            for (int i = 0; i < dateList.length; i++) {
                Map<String,Object> dataMap = new HashMap<>();
                dataMap.put("时间",dateList[i]);
                dataMap.put("营业额",turnoverList[i]);
                dataMap.put("用户总量",totalUserList[i]);
                dataMap.put("新增用户",newUserList[i]);
                dataMap.put("订单总数",orderCountList[i]);
                dataMap.put("有效订单",validOrderCountList[i]);
                Double validCount = Double.valueOf(validOrderCountList[i]);
                Double orderCount = Double.valueOf(orderCountList[i]);
                if (orderCount!=0) {
                    dataMap.put("订单完成率", (validCount / orderCount) * 100 + "%");
                }else{
                    dataMap.put("订单完成率", 0);
                }
                String top = "";
                for (int j = 0; j < numberList.length; j++) {
                    if (j!=numberList.length-1){
                        top =top + nameList[j] + "*"+ numberList[j]+",";
                    }else{
                        top =top + nameList[j] + "*"+ numberList[j];
                    }
                }

                dataMap.put("销量排名TOP10",top);

                data.add(dataMap);
            }

            //调用导出工具类
            ExportExcel.exportExcel("苍穹外卖数据统计表",column,data,request,response);

    }
}
