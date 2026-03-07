package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@AllArgsConstructor
public class AnswerCommentServiceImpl implements AnswerCommentService {

    private final CommentMapper commentMapper;


    @Override
    public void comment(Integer answerId, CommentDto commentDto, AccountUser accountUser) {
        Comment comment = new Comment();
        comment.setUserId(accountUser.getUserId());
        comment.setContent(commentDto.getContent());
        comment.setCommentedId(answerId);
        comment.setCommentedType(Answer.class.getSimpleName());
        Date date = new Date();
        comment.setCreatedAt(date);
        comment.setUpdatedAt(date);
        commentMapper.insert(comment);
    }
}
