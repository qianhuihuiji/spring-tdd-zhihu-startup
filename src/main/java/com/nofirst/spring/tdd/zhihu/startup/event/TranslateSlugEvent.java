package com.nofirst.spring.tdd.zhihu.startup.event;

import com.nofirst.spring.tdd.zhihu.startup.mbg.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateSlugEvent {

    private Question question;
    private Date eventTime;
}
