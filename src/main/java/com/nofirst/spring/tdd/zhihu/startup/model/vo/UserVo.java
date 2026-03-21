package com.nofirst.spring.tdd.zhihu.startup.model.vo;

import lombok.Data;


@Data
public class UserVo {

    private Integer id;

    private String name;

    private String phone;

    private String email;
}