package com.lily.lilyojquestionservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.common.LanguageEnum;
import com.lily.lilyojcommon.constant.CommonConstant;
import com.lily.lilyojcommon.constant.StatusConstant;
import com.lily.lilyojcommon.exception.BusinessException;
import com.lily.lilyojcommon.utils.SqlUtils;
import com.lily.lilyojmodel.model.dto.judge.JudgeInfo;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.lilyojmodel.model.entity.Question;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojmodel.model.entity.User;
import com.lily.lilyojmodel.model.vo.QuestionSubmitVO;
import com.lily.lilyojmodel.model.vo.QuestionVO;
import com.lily.lilyojmodel.model.vo.UserVO;
import com.lily.lilyojquestionservice.mapper.QuestionSubmitMapper;
import com.lily.lilyojquestionservice.service.QuestionService;
import com.lily.lilyojquestionservice.service.QuestionSubmitService;
import com.lily.lilyojserviceclient.service.JudgeFeignClient;
import com.lily.lilyojserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author lily
 * @description 针对表【question_submit(题目提交结果表)】的数据库操作Service实现
 * @createDate 2024-04-07 23:47:47
 */
@Service
@Slf4j
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private UserFeignClient userService;

    @Resource
    private QuestionService questionService;

    @Resource
    private JudgeFeignClient judgeService;

    /**
     * 代码提交
     *
     * @param
     * @param
     * @return
     */
    // 校验valid异常
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, Long userId) {
        // 1. 参数校验
        Long questionId = questionSubmitAddRequest.getQuestionId();
        String language = questionSubmitAddRequest.getLanguage();
        String code = questionSubmitAddRequest.getCode();
        // 2.0 统计提交次数
        Question question = questionService.getById(questionId);
        question.setSubmitNum(question.getSubmitNum() + 1);
        boolean b = questionService.updateById(question);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
        // 2. 存入数据库，更改状态
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(code);
        questionSubmit.setLanguage(language);
        questionSubmit.setUserId(userId);
        questionSubmit.setStatus(StatusConstant.WAITING);
        boolean save = save(questionSubmit);
        if (!save) {
            handleUpdateError(questionSubmit);
        }
        Long questionSubmitId = questionSubmit.getId();
        // 3. 异步处理代码运行等操作
        CompletableFuture.runAsync(()->{
            // 移交判题模块（判题模块调用代码沙箱）
            judgeService.doJudge(questionSubmitId);
        });
        // 4. 返回插入数据库成功信息
        return questionSubmitId;
    }

    private void handleUpdateError(QuestionSubmit questionSubmit) {
        Long questionId = questionSubmit.getQuestionId();
        Long userId = questionSubmit.getUserId();
        log.error("更新提交状态异常，题目id：{}，用户id：{}", questionId, userId);
        QuestionSubmit errorQuestionSub = new QuestionSubmit();
        errorQuestionSub.setQuestionId(questionId);
        errorQuestionSub.setUserId(userId);
        questionSubmit.setStatus(StatusConstant.FAILED);
        boolean save = save(errorQuestionSub);
        if (!save) {
            log.error("更新*提交失败*状态异常，题目id：{}，用户id：{}", questionId, userId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统异常");
        }
    }

    @Override
    public boolean validLang(String lang) {
        for (LanguageEnum value : LanguageEnum.values()) {
            if (StringUtils.equalsIgnoreCase(value.getName(), lang)) return true;
        }
        return false;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);

        /**
         * 特殊属性转化
         */
        String judgeInfo = questionSubmit.getJudgeInfo();
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfo, JudgeInfo.class));

        Long userId = questionSubmit.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            questionSubmitVO.setUserVO(userVO);
        }

        Long questionId = questionSubmit.getQuestionId();
        if (questionId != null) {
            Question question = questionService.getById(questionId);
            QuestionVO questionVO = questionService.getQuestionVO(question);
            questionSubmitVO.setQuestionVO(questionVO);
        }

        return questionSubmitVO;
    }

    // todo stream方法，少查数据库
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, Long id) {
        List<QuestionSubmitVO> questionSubmitVOS = new ArrayList<>();
        for (QuestionSubmit questionSubmit : questionSubmitPage.getRecords()) {
            if (questionSubmit == null) continue;
            QuestionSubmitVO questionSubmitVO = getQuestionSubmitVO(questionSubmit);
            questionSubmitVOS.add(questionSubmitVO);
        }
        // 复制所有属性
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        BeanUtils.copyProperties(questionSubmitPage, questionSubmitVOPage);
        questionSubmitVOPage.setRecords(questionSubmitVOS);
        return questionSubmitVOPage;
    }



    @Override
    public QueryWrapper getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();


        queryWrapper.like(status != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




