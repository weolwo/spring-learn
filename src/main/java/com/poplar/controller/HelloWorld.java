package com.poplar.controller;

import com.poplar.pojo.Person;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

@RestController
public class HelloWorld {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Object hello() {
        Person person = new Person();
        person.setAge(18);
        person.setName("家静");
        person.setBirthday(Date.from(Instant.now()));
        return person;
    }
}
