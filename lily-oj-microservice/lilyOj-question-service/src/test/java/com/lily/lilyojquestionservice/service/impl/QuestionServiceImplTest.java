package com.lily.lilyojquestionservice.service.impl;

import com.lily.lilyojquestionservice.mapper.QuestionMapper;
import com.lily.lilyojquestionservice.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by lily via on 2024/4/27 10:20
 */
@SpringBootTest
class QuestionServiceImplTest {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionMapper questionMapper;

    @Test
    public void validQuestionTest() {
        questionService.validQuestion(null, true);
    }

    @Test
    public void QuestionServiceTest() {
        questionService.getById(1777655253274062850L);
    }

    @Test
    public void mapperTest() {
        questionMapper.selectById(1777655253274062850L);
    }
}