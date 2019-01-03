package com.lwj.house.repository;

import com.lwj.house.web.dto.SupportAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author lwj
 */
public interface SupportAddressRepository extends JpaRepository<SupportAddress, Integer> {

    /**
     * 获取所有行政级别的信息
     * @param level
     * @return
     */
    List<SupportAddress> findAllByLevel(String level);

}
