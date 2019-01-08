package com.lwj.house.base;

import com.lwj.house.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户登录工具类
 * @author lwj
 */
public class LoginUserUtil {

    public static User load(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof User) {
            return (User) principal;
        }
        return null;
    }

    public static Integer getLoginUserId(){
        User user = load();
        if (user == null) {
            return -1;
        }
        return user.getId();
    }
}
