package com.lwj.house.repository;

import com.lwj.house.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色数据Dao
 * @author lwj
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * 根据用户id查询角色信息
     * @param userId
     * @return
     */
    List<Role> findRolesByUserId(Integer userId);
}
