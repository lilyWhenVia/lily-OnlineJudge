package com.lily.onlineJudge.model.dto.post;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 更新请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">作者github</a>
 */
@Data
public class PostUpdateRequest implements Serializable {

    /**
     * id
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
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}