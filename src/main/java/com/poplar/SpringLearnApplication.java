package com.poplar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringLearnApplication {

    public static void main(String[] args) {
        System.out.println(SpringLearnApplication.class.getClassLoader());
        SpringApplication.run(SpringLearnApplication.class, args);
    }

}
