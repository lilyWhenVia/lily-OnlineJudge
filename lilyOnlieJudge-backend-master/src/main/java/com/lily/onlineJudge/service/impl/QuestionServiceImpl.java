package com.lily.onlineJudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lily.onlineJudge.model.dto.question.QuestionQueryRequest;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.model.vo.QuestionVO;
import com.lily.onlineJudge.service.QuestionService;
import com.lily.onlineJudge.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author 22825
* @description 针对表【question(题目表)】的数据库操作Service实现
* @createDate 2024-04-07 23:42:03
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

    @Override
    public void validQuestion(Question question, boolean b) {

    }

    @Override
    public QuestionVO getQuestionVO(Question question, Long id) {
        QuestionVO questionVO = new QuestionVO();
        return questionVO;
    }

    @Override
    public Wrapper getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        return null;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        return null;
    }
}




