package com.lwj.house.service;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务接口通用结构
 * @author lwj
 * @param <T>
 */
@Getter
@Setter
public class ServiceResult<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 返回对象
     */
    private T result;

    public ServiceResult(boolean success) {
        this.success = success;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public static <T> ServiceResult<T> notFound() {
        return new ServiceResult<>(false, Message.NOT_FOUND.getValue());
    }

    public enum Message {
        NOT_FOUND("Not Found Resource!"),
        NOT_LOGIN("User not login!");

        private String value;

        Message(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
