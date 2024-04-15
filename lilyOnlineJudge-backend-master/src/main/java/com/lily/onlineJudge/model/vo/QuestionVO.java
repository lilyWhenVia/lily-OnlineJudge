package com.lily.onlineJudge.model.vo;

import cn.hutool.json.JSONUtil;
import com.lily.onlineJudge.model.entity.JudgeCase;
import com.lily.onlineJudge.model.entity.JudgeConfig;
import com.lily.onlineJudge.model.entity.Post;
import com.lily.onlineJudge.model.entity.Question;
import com.lily.onlineJudge.service.UserService;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目的视图展示类--其他信息，脱敏信息
 * @TableName question
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * 题目 id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 题目属性标签列表（json 数组）
     * 便于网络传输时返回一个 **json数组**
     */
    private List<String> tags;


    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;


    /**
     * 题目属性（json对象）
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建题目人的信息
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    private static final long serialVersionUID = 4609271252996463382L;

}