package com.nofirst.spring.tdd.zhihu.startup.model.vo;

import lombok.Data;

import java.util.Date;


@Data
public class CommentVo {

    private Integer id;

    private Integer commentedId;

    private String content;

    private Date createTime;
}