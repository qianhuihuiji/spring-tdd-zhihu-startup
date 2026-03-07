package com.nofirst.spring.tdd.zhihu.startup.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.CommentMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Comment;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.CommentExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.model.dto.CommentDto;
import com.nofirst.spring.tdd.zhihu.startup.model.vo.CommentVo;
import com.nofirst.spring.tdd.zhihu.startup.publisher.CustomEventPublisher;
import com.nofirst.spring.tdd.zhihu.startup.security.AccountUser;
import com.nofirst.spring.tdd.zhihu.startup.service.QuestionCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@AllArgsConstructor
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final CommentMapper commentMapper;
    private final CustomEventPublisher customEventPublisher;


    @Override
    public void comment(Integer questionId, CommentDto commentDto, AccountUser accountUser) {
        Comment comment = new Comment();
        comment.setUserId(accountUser.getUserId());
        comment.setContent(commentDto.getContent());
        comment.setCommentedId(questionId);
        comment.setCommentedType(Question.class.getSimpleName());
        Date date = new Date();
        comment.setCreatedAt(date);
        comment.setUpdatedAt(date);
        commentMapper.insert(comment);

        // 发布评论事件，触发通知等后续处理
        customEventPublisher.firePostCommentEvent(comment);
    }

    @Override
    public PageInfo<CommentVo> index(Integer questionId, Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andCommentedIdEqualTo(questionId);
        criteria.andCommentedTypeEqualTo(Question.class.getSimpleName());
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
