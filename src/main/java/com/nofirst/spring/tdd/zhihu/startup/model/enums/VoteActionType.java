package com.nofirst.spring.tdd.zhihu.startup.model.enums;

public enum VoteActionType {

    VOTE_UP("vote_up", "赞同"),

    VOTE_DOWN("vote_down", "反对");

    private final String code;
    private final String description;

    VoteActionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
