package com.nofirst.spring.tdd.zhihu.startup.publisher;

import com.nofirst.spring.tdd.zhihu.startup.event.PublishQuestionEvent;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CustomEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public CustomEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void firePublishQuestionEvent(Question question) {
        PublishQuestionEvent publishQuestionEvent = new PublishQuestionEvent(question);
        applicationEventPublisher.publishEvent(publishQuestionEvent);
    }
}
