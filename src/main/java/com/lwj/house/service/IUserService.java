package com.lwj.house.service;

import com.lwj.house.entity.User;

/**
 * 用户服务接口
 * @author lwj
 */
public interface IUserService {

    /**
     * 通过name查找用户对象
     * @param userName
     * @return
     */
    User findUserByName(String userName);
}
