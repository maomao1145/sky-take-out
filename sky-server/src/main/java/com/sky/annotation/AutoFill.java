package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义注解 用于标识某个方法需要用到功能字段自动填充

//元注解：可以写在注解上面的注解  @Target ：指定注解能在哪里使用  @Retention ：可以理解为保留时间(生命周期)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //数据库操作类型
    OperationType value();
}
