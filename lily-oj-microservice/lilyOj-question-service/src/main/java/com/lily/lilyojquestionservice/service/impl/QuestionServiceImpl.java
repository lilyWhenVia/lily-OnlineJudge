package com.lily.lilyojquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.constant.CommonConstant;
import com.lily.lilyojcommon.exception.BusinessException;
import com.lily.lilyojcommon.exception.ThrowUtils;
import com.lily.lilyojcommon.utils.SqlUtils;
import com.lily.lilyojmodel.model.dto.judge.JudgeConfig;
import com.lily.lilyojmodel.model.dto.question.QuestionQueryRequest;
import com.lily.lilyojmodel.model.entity.Question;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojmodel.model.entity.User;
import com.lily.lilyojmodel.model.vo.QuestionSubmitVO;
import com.lily.lilyojmodel.model.vo.QuestionVO;
import com.lily.lilyojmodel.model.vo.UserVO;
import com.lily.lilyojquestionservice.mapper.QuestionMapper;
import com.lily.lilyojquestionservice.service.QuestionService;
import com.lily.lilyojserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lily
 * @description 针对表【question(题目表)】的数据库操作Service实现
 * @createDate 2024-04-07 23:42:03
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private UserFeignClient userService;


    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }


    @Override
    public QuestionVO getQuestionVO(Question question, Long id) {
        return getQuestionVO(question);
    }

    /**
     * QuestionVO类转Question
     *
     * @param questionVO
     * @return
     */
    public Question getQuestionByVO(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        // 特殊值转字符串设置
        /**
         * JsonUtil自动判空
         */
        question.setTags(JSONUtil.toJsonStr(tagList));
        question.setTags(JSONUtil.toJsonStr(voJudgeConfig));
        return question;
    }

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        questionVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));
        /**
         * userVO设置
         */
        Long userId = question.getUserId();
        User user = userService.getById(userId);
        if (user != null) {
            UserVO userVO = userService.getUserVO(user);
            questionVO.setUserVO(userVO);
        }
        return questionVO;
    }

    // todo stream方法，少查数据库
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<QuestionVO> questionVOS = new ArrayList<>();
        for (Question question : questionPage.getRecords()) {
            if (question == null) continue;
            QuestionVO questionVO = getQuestionVO(question);
            questionVOS.add(questionVO);
        }
        // 复制所有属性
        Page<QuestionVO> questionVOPage = new Page<>();
        BeanUtils.copyProperties(questionPage, questionVOPage);
        questionVOPage.setRecords(questionVOS);
        return questionVOPage;
    }

    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    private QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        return null;
    }


    /**
     * 获取查询包装类
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        Integer submitNum = questionQueryRequest.getSubmitNum();
        Integer acceptedNum = questionQueryRequest.getAcceptedNum();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

        queryWrapper.like(StringUtils.isNotEmpty(title), "title", title);
        queryWrapper.like(StringUtils.isNotEmpty(content), "content", content);
        /**
         * 根据每个标签查询
         */
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(submitNum!=null&&submitNum > 0, "submitNum", submitNum);
        queryWrapper.eq(acceptedNum!=null&&acceptedNum > 0, "acceptedNum", acceptedNum);
        queryWrapper.eq(userId!=null&&userId > 0, "userId", userId);
        queryWrapper.eq(id!=null&&id > 0, "id", id);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


}




