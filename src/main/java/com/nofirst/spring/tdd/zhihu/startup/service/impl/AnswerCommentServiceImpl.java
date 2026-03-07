package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CommentExample;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.AnswerCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    @Override
    public PageInfo<CommentVo> index(Integer answerId, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andCommentedIdEqualTo(answerId);
        criteria.andCommentedTypeEqualTo(Answer.class.getSimpleName());
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        PageInfo<Comment> commentPageInfo = new PageInfo<>(comments);
        List<CommentVo> result = new ArrayList<>();
        for (Comment comment : comments) {
            CommentVo commentVo = new CommentVo();
            commentVo.setId(comment.getId());
            commentVo.setCommentedId(comment.getCommentedId());
            commentVo.setContent(comment.getContent());
            commentVo.setCreateTime(comment.getCreatedAt());

            result.add(commentVo);
        }
        PageInfo<CommentVo> pageResult = new PageInfo<>();
        pageResult.setTotal(commentPageInfo.getTotal());
        pageResult.setPageNum(commentPageInfo.getPageNum());
        pageResult.setPageSize(commentPageInfo.getPageSize());
        pageResult.setList(result);
        return pageResult;
    }
}
