package com.lily.lilyojquestionservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lily.lilyojcommon.annotation.AuthCheck;
import com.lily.lilyojcommon.common.BaseResponse;
import com.lily.lilyojcommon.common.ErrorCode;
import com.lily.lilyojcommon.common.ResultUtils;
import com.lily.lilyojcommon.constant.UserConstant;
import com.lily.lilyojcommon.exception.BusinessException;
import com.lily.lilyojcommon.exception.ThrowUtils;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.lily.lilyojmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.lily.lilyojmodel.model.entity.QuestionSubmit;
import com.lily.lilyojmodel.model.entity.User;
import com.lily.lilyojmodel.model.vo.QuestionSubmitVO;
import com.lily.lilyojquestionservice.service.QuestionSubmitService;
import com.lily.lilyojserviceclient.service.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserFeignClient userService;

    /**
     * 代码提交
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录 id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
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
     * 查询个人提交记录
     *
     * @param questionSubmitQueryRequest 查询条件
     * @param request                    请求
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
     * @param questionSubmitQueryRequest 查询条件
     * @param request                   请求
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
