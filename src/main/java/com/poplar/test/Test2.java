package com.poplar.test;

import java.lang.reflect.Method;

/**
 * BY poplar ON 2019/11/14
 */
public class Test2 {

    public static void main(String[] args) throws Exception {
        Class<Test1> clazz = Test1.class;
        Method method = clazz.getMethod("main", String[].class);
        //当然此处我们也可以使用new Test2()
        method.invoke(null, new Object[]{null});
    }
}
