package com.nofirst.spring.tdd.zhihu.startup.model.dto;

import lombok.Data;

@Data
public class QuestionDto {

    private String title;

    private String content;

    private Integer categoryId;
}