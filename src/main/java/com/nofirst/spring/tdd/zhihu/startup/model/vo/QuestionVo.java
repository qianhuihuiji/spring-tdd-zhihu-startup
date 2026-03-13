package com.nofirst.spring.tdd.zhihu.startup.model.vo;

import lombok.Data;

@Data
public class QuestionVo {

    private Integer id;
    private Integer userId;
    private String title;
    private String content;
}