package com.lwj.house.service.user;

import com.lwj.house.entity.User;
import com.lwj.house.service.ServiceResult;
import com.lwj.house.web.dto.UserDTO;

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

    /**
     * 通过id获取用户DTO对象
     * @param userId
     * @return
     */
    ServiceResult<UserDTO> findById(Integer userId);
}
