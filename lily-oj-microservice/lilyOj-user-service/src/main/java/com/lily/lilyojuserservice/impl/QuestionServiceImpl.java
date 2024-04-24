package com.lily.lilyojuserservice.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lily.onlineJudge.constant.CommonConstant;
import com.lily.onlineJudge.mapper.QuestionMapper;
import com.lily.onlineJudge.model.dto.question.QuestionQueryRequest;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import com.lily.onlineJudge.model.entity.User;
import com.lily.onlineJudge.model.vo.QuestionSubmitVO;
import com.lily.onlineJudge.model.vo.QuestionVO;
import com.lily.onlineJudge.model.vo.UserVO;
import com.lily.onlineJudge.service.QuestionService;
import com.lily.onlineJudge.service.UserService;
import com.lily.onlineJudge.utils.SqlUtils;
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
 * @author 22825
 * @description 针对表【question(题目表)】的数据库操作Service实现
 * @createDate 2024-04-07 23:42:03
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private UserService userService;


    @Override
    public void validQuestion(Question question, boolean b) {

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
        queryWrapper.eq(submitNum > 0, "submitNum", submitNum);
        queryWrapper.eq(acceptedNum > 0, "acceptedNum", acceptedNum);
        queryWrapper.eq(userId > 0, "userId", userId);
        queryWrapper.eq(id > 0, "id", id);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}




