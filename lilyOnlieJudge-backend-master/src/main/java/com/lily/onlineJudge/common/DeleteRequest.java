package com.lily.onlineJudge.common;

import java.io.Serializable;
import lombok.Data;

/**
 * 删除请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">作者github</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}