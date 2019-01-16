package com.lwj.house.repository;

import com.lwj.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

/**
 * @author lwj
 */
@Repository
public interface HouseRepository extends JpaRepository<House, Integer>, JpaSpecificationExecutor<House> {

    /**
     * 修改房屋封面
     * @param targetId
     * @param path
     */
    @Modifying
    @Query("update House as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id") Integer targetId, @Param(value = "cover") String path);

    /**
     * 修改房源的状态
     * @param id
     * @param status
     */
    @Modifying
    @Query("update House as house set house.status = :status where house.id = :id")
    void updateStatus(@Param(value = "id") Integer id, @Param(value = "status") Integer status);

}
