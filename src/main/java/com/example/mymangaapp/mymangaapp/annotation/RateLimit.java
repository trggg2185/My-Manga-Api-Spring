package com.example.mymangaapp.mymangaapp.annotation;

import com.example.mymangaapp.mymangaapp.enums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// custom annotation để gắn lên method dùng để rate limiting
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    // số lần tối đa đc truy cập
    int capacity() default 5;

    // thời gian reset đc truy cập
    int resetTimeInSeconds() default 60;

    // Limit theo ip hoặc id người dùng
    LimitType limitType() default LimitType.IP;

}
