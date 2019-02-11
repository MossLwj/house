package com.lwj.house.service.search;

/**
 * 检索相关接口
 * @author lwj
 */
public interface ISearchService {


    /**
     * 索引目标房源
     * @param houseId
     * @return
     */
    void index(Integer houseId);

    /**
     * 移除房源索引
     * @param houseId
     */
    void remove(Integer houseId);
}
