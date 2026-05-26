package com.demo.tmdt.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME) // tồn tại khi chạy run time

public @interface CurrentUser {

}