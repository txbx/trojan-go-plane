package com.tan.trojangoplane.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : tan
 * @date: 2020/10/31 11:21
 * @description :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable{
    private boolean flag;
    private String message;
    private Object data;
    public Result(boolean flag, String message) {
        super();
        this.flag = flag;
        this.message = message;
    }
}
