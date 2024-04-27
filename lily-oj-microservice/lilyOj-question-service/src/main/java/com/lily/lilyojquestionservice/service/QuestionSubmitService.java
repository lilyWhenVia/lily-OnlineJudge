package com.lily.lilyojquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojmodel.model.vo.QuestionSubmitVO;

/**
* @author lily
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
