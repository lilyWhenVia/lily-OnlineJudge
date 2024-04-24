package com.lily.lilyojcommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 *
* @author lily <a href="https://github.com/lilyWhenVia">come to find lily</a>
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}