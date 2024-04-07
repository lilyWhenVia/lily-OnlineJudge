package com.lily.onlineJudge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lily.onlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import com.lily.onlineJudge.model.entity.User;
import com.lily.onlineJudge.model.vo.QuestionSubmitVO;
import com.lily.onlineJudge.service.QuestionSubmitService;
import com.lily.onlineJudge.mapper.QuestionSubmitMapper;
import org.springframework.stereotype.Service;

/**
* @author 22825
* @description 针对表【question_submit(题目提交结果表)】的数据库操作Service实现
* @createDate 2024-04-07 23:47:47
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Override
    public QueryWrapper getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        return null;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, Long id) {
        return null;
    }

    @Override
    public int doQuestionSubmit(long questionSubmitId, User loginUser) {
        return 0;
    }
}




