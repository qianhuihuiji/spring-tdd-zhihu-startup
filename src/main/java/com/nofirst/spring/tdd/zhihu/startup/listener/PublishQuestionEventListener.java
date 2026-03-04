package com.nofirst.spring.tdd.zhihu.startup.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.event.PublishQuestionEvent;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.NotificationMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.UserMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Notification;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.User;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.UserExample;
import com.nofirst.spring.tdd.zhihu.startup.util.InviteUserUtil;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class PublishQuestionEventListener {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @EventListener
    public void notifyInvitedUsers(PublishQuestionEvent event) {
        Question question = event.getQuestion();
        String data = getData(question);
        List<String> invitedUsers = InviteUserUtil.extractInvitedUser(question.getContent());
        if (CollectionUtils.isEmpty(invitedUsers)) {
            return;
        }
        invitedUsers.forEach(invitedUser -> {
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(invitedUser);
            List<User> users = userMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(users)) {
                users.forEach(user -> {
                    Notification notification = new Notification();
                    notification.setType(PublishQuestionEvent.class.getName());
                    notification.setNotifiableId(user.getId());
                    notification.setNotifiableType(User.class.getName());
                    Date now = new Date();
                    notification.setCreatedAt(now);
                    notification.setUpdatedAt(now);
                    notification.setData(data);

                    notificationMapper.insert(notification);
                });
            }
        });
    }

    private String getData(Question question) {
        Map<String, String> data = new HashMap<>(2);
        data.put("title", question.getTitle());
        data.put("content", question.getContent());
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
