package com.lily.onlineJudge.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lily.onlineJudge.annotation.AuthCheck;
import com.lily.onlineJudge.common.BaseResponse;
import com.lily.onlineJudge.common.ErrorCode;
import com.lily.onlineJudge.common.ResultUtils;
import com.lily.onlineJudge.constant.UserConstant;
import com.lily.onlineJudge.exception.BusinessException;
import com.lily.onlineJudge.exception.ThrowUtils;
import com.lily.onlineJudge.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.lily.onlineJudge.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.onlineJudge.model.entity.QuestionSubmit;
import com.lily.onlineJudge.model.entity.User;
import com.lily.onlineJudge.model.vo.QuestionSubmitVO;
import com.lily.onlineJudge.service.QuestionSubmitService;
import com.lily.onlineJudge.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 帖子收藏接口
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 代码提交
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@Valid @RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        String language = questionSubmitAddRequest.getLanguage();
        // 参数校验
        if (!questionSubmitService.validLang(language)) {
            log.error("request：{}语言参数有误", request.getPathInfo());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "语言不存在");
        }
        Long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, userId);
        return ResultUtils.success(result);
    }

    /**
     * 查询个人提价记录
     *
     * @param questionSubmitQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listMyQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
            HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, userId));
    }

    /**
     * 管理员分页查询提交记录
     *
     * @param questionSubmitQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    //  pagesize current 存在默认值
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                                 HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, userId));
    }

}
