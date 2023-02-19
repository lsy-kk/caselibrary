package com.lsykk.caselibrary.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/* 所有后端接口的统一返回格式
{
    "success": true,
    "code": "A00000",
    "msg": "success",
    "data": null
}
*/
@Data
// 自动生成所有构造方法
@AllArgsConstructor
public class ApiResult {

    private boolean success;

    private String code;

    private String msg;

    private Object data;

    // 成功
    public static ApiResult success(){
        return new ApiResult(true, "A00000","success", null);
    }

    public static ApiResult success(Object data){
        return new ApiResult(true, "A00000","success", data);
    }

    // 失败
    public static ApiResult fail(String code, Object data){
        return new ApiResult(false, code, "fail", data);
    }

    // 失败
    public static ApiResult fail(ErrorCode errorCode){
        return new ApiResult(false, errorCode.getCode(), "fail", errorCode.getMsg());
    }
}
