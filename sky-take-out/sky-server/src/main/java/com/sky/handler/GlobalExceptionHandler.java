package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.*;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理SQL异常
     * @param ex
     * @return
     * 用户已存在
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String username = split[2];
            String msg = username + MessageConstant.ALREDAY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

    //修改密码时原密码错误
    @ExceptionHandler
    public Result PasswordIntErroyHandler(PasswordIntErroy ex){
        return Result.error(MessageConstant.PASSWORD_ISERROY);
    }

    //    修改密码时原密码新密码相同
    @ExceptionHandler
    public Result PasswordEditFailedExceptionHandler(PasswordEditFailedException ex){
        return Result.error(MessageConstant.PASSWORD_REPEAT);
    }

    //起售菜品不能删除
    @ExceptionHandler
    public Result DishErroyExceptionHandler(DishErroyException ex){
        return Result.error(MessageConstant.DISH_ON_SALE);
    }

    //    删除套餐时套餐关联菜品或者分类
    @ExceptionHandler
    public Result CategoryErroyExceptionHandler(CategoryErroyException ex){
        return Result.error(MessageConstant.CATEGORY_STATUS_ERROR);
    }

    //    起售中的套餐不能删除
    @ExceptionHandler
    public Result SetmealEnableFailedExceptionHandler(SetmealEnableFailedException ex){
        return Result.error(MessageConstant.SETMEAL_ON_SALE);
    }

    //    菜品关联未停售套餐,不能停售
    @ExceptionHandler
    public Result DishExceptionHandler(DishException ex){
        return Result.error(MessageConstant.DISH_BE_RELATED_BY_SETMEAL_STATUS);
    }

    //    套餐包含未起售菜品 ,不能起售
    @ExceptionHandler
    public Result SetmealStatusFailedExceptionHandler(SetmealStatusFailedException ex){
        return Result.error(MessageConstant.SETMEAL_ENABLE_FAILED);
    }

    //    微信登录异常
    @ExceptionHandler
    public Result LoginFailedExceptionHandler(LoginFailedException ex){
        return Result.error(MessageConstant.LOGIN_FAILED);
    }
    @ExceptionHandler
    public Result AddressBookBusinessExceptionHandler(AddressBookBusinessException ex){
        return Result.error(MessageConstant.ADDRESS_BOOK_IS_NULL);
    }
    @ExceptionHandler
    public Result ShoppingCartBusinessExceptionHandler(ShoppingCartBusinessException ex){
        return Result.error(MessageConstant.SHOPPING_CART_IS_NULL);
    }
    @ExceptionHandler
    public Result OrderBusinessExceptionHandler(OrderBusinessException ex){
        return Result.error(MessageConstant.ORDER_NOT_FOUND);
    }
    @ExceptionHandler
    public Result OrderExceptionHandler(OrderException ex){
        return Result.error(MessageConstant.ORDER_STATUS_ERROR);
    }
}

