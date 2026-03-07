package com.nofirst.spring.tdd.zhihu.startup.event;

import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 发表评论事件
 */
public class PostCommentEvent extends ApplicationEvent {

    @Getter
    private final Comment comment;

    public PostCommentEvent(Comment comment) {
        super(comment);
        this.comment = comment;
    }
}
