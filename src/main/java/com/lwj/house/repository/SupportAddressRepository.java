package com.lwj.house.repository;


import com.lwj.house.entity.SupportAddress;
import com.lwj.house.web.dto.SupportAddressDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lwj
 */
@Repository
public interface SupportAddressRepository extends JpaRepository<SupportAddress, Integer> {

    /**
     * 获取所有行政级别的信息
     * @param level
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

    SupportAddress findByEnNameAndLevel(String enName, String level);

    List<SupportAddress> findAllByLevelAndBelongTo(String level, String cityName);

}
