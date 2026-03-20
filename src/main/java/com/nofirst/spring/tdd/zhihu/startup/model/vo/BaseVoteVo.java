package com.nofirst.spring.tdd.zhihu.startup.model.vo;

import lombok.Data;

/**
 * 包含投票相关属性的基础类
 */
@Data
public abstract class BaseVoteVo {

    private Byte voteType;

    private Integer voteUpCount;
    
    private Integer voteDownCount;
}