package com.lily.lilyojjudgeservice.service;

import org.springframework.stereotype.Service;

/**
 * 定义了判题的方法
 * Created by lily via on 2024/4/8 21:26
 */
@Service
public interface JudgeService {

    /**
     * 判题方法
     * @param questionSubmitId
     */
    void doJudge(Long questionSubmitId);

    Boolean handledCodeSandboxError(Long questionSubmitId, Integer statue);
}
