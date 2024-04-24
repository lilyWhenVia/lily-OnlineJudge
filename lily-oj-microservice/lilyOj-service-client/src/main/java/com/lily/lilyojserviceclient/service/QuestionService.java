package com.lily.lilyojserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lily.onlineJudge.model.dto.question.QuestionQueryRequest;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 22825
* @description 针对表【question(题目表)】的数据库操作Service
* @createDate 2024-04-07 23:42:03
*/
public interface QuestionService extends IService<Question> {

    void validQuestion(Question question, boolean b);

    QuestionVO getQuestionVO(Question question, Long id);

    QuestionVO getQuestionVO(Question question);

    QueryWrapper getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
