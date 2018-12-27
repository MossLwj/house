package com.lwj.house.repository;

import com.lwj.house.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户Jpa类
 * @author lwj
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
