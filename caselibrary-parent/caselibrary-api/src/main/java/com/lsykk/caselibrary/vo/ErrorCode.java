package com.lsykk.caselibrary.vo;

public enum ErrorCode {

    PARAMS_ERROR("10001","参数有误"),
    ACCOUNT_PWD_NOT_EXIST("10002","账号或密码错误"),
    TOKEN_ERROR("10003","token不合法"),
    ACCOUNT_EXIST("10004","账号已存在"),
    EMAIL_CODE_ERROR("10005","验证码错误或已过期"),
    File_Upload_Error("20001","文件上传失败"),
    File_Upload_Illegal("20002","上传文件类型错误"),
    DATABASE_INSERT_ERROR("20003","数据库插入失败"),
    SYSTEM_ERROR("20004","系统错误"),
    REQUEST_LIMIT("20005","请求频繁"),
    CANNOT_FIND_DATA("30001","找不到数据"),
    CANNOT_SENT_EMAIL("30002","发送失败"),
    CANNOT_DELETE_FILE("30003","无法删除云存储中的文件"),
    NO_PERMISSION("40001","无访问权限，请先登录"),
    NO_ENOUGH_PERMISSION("40002","访问权限不足"),
    TOKEN_EXPIRED("90001","登录过期，请重新登录"),
    NO_LOGIN("90002","未登录"),;

    private String code;
    private String msg;

    ErrorCode(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}