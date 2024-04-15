package com.lily.onlineJudge.common;

import com.lily.onlineJudge.service.QuestionSubmitService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/8 12:14
 */
@SpringBootTest
class LanguageEnumTest {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Test
    public void LangTest(){
        boolean b = questionSubmitService.validLang("jaVA");
        System.out.println(b);
        for (LanguageEnum value : LanguageEnum.values()) {
            System.out.println(value);
        }

    }

}