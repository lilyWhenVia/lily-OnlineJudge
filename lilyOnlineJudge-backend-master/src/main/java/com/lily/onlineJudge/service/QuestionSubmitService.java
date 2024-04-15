package com.lily.onlineJudge.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lily.onlineJudge.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.lily.onlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lily.onlineJudge.model.entity.User;
import com.lily.onlineJudge.model.vo.QuestionSubmitVO;

import javax.servlet.http.HttpServletRequest;
import java.sql.Wrapper;

/**
* @author 22825
* @description 针对表【question_submit(题目提交结果表)】的数据库操作Service
* @createDate 2024-04-07 23:47:47
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    boolean validLang(String lang);

    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmitPage);

    QueryWrapper getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, Long id);

    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, Long userId);
}
