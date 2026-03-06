package com.nofirst.spring.tdd.zhihu.startup.queue.consumer;

import com.nofirst.spring.tdd.zhihu.startup.event.TranslateSlugEvent;
import com.nofirst.spring.tdd.zhihu.startup.mbg.mapper.QuestionMapperExt;
import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import com.nofirst.spring.tdd.zhihu.startup.service.TranslatorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
@Slf4j
public class TranslateSlugEventConsumer {

    private TranslatorService translatorService;
    private QuestionMapperExt questionMapperExt;

    @KafkaListener(topics = "${translate.kafka.topic}", groupId = "${translate.kafka.group}")
    public void listen(TranslateSlugEvent translateSlugEvent) {
        log.info("Received: {}", translateSlugEvent);
        Question question = translateSlugEvent.getQuestion();
        String translatedSlug = translatorService.translate(question.getTitle());

        questionMapperExt.updateSlug(question.getId(), translatedSlug);
    }
}
