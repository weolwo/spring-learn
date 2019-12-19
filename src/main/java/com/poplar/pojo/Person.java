package com.poplar.pojo;

import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.Data;

import java.util.Date;

/**
 * BY poplar ON 2019/11/14
 */
@Data
public class Person {
    private String name;

    private Integer age;

    private Date birthday;


}
