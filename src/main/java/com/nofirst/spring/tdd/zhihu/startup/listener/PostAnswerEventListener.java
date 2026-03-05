package com.nofirst.spring.tdd.zhihu.startup.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nofirst.spring.tdd.zhihu.startup.event.PostAnswerEvent;
import com.nofirst.spring.tdd.zhihu.startup.event.PublishQuestionEvent;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.NotificationMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.SubscriptionMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.UserMapper;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Answer;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Notification;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Subscription;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.SubscriptionExample;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.User;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Post answer event listener.
 */
@Component
@AllArgsConstructor
public class PostAnswerEventListener {

    private final NotificationMapper notificationMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @EventListener
    public void notifySubscribedUsers(PostAnswerEvent event) {
        Answer answer = event.getAnswer();
        SubscriptionExample subscriptionExample = new SubscriptionExample();
        subscriptionExample.createCriteria()
                .andQuestionIdEqualTo(answer.getQuestionId())
                .andUserIdNotEqualTo(answer.getUserId());
        List<Subscription> subscriptions = subscriptionMapper.selectByExample(subscriptionExample);
        String data = getData(answer);
        for (Subscription subscription : subscriptions) {
            User user = userMapper.selectByPrimaryKey(subscription.getUserId());
            if (Objects.nonNull(user)) {
                Notification notification = new Notification();
                notification.setType(PublishQuestionEvent.class.getName());
                notification.setNotifiableId(user.getId());
                notification.setNotifiableType(User.class.getName());
                Date now = new Date();
                notification.setCreatedAt(now);
                notification.setUpdatedAt(now);
                notification.setData(data);

                notificationMapper.insert(notification);
            }
        }
    }

    private String getData(Answer answer) {
        Map<String, String> data = new HashMap<>(2);
        data.put("content", answer.getContent());
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
