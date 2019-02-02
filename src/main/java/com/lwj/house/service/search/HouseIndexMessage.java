package com.lwj.house.service.search;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息队列序列化对象
 * @author lwj
 */
@Getter
@Setter
public class HouseIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private Integer houseId;

    private String operation;

    private int retry = 0;

    /**
     * 默认构造器 防止jackson序列化失败
     */
    public HouseIndexMessage() {

    }

    public HouseIndexMessage(Integer houseId, String operation, int retry) {
        this.houseId = houseId;
        this.operation = operation;
        this.retry = retry;
    }
}
