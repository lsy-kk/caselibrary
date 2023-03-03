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
    CANNOT_FIND_DATA("30001","找不到数据"),
    CANNOT_SENT_EMAIL("30002","发送邮件失败"),
    NO_PERMISSION("70001","无访问权限"),
    SESSION_TIME_OUT("90001","会话超时"),
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