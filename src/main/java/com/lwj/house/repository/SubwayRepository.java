package com.lwj.house.repository;

import com.lwj.house.entity.Subway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 地铁线Jpa类
 * @author lwj
 */
@Repository
public interface SubwayRepository extends JpaRepository<Subway, Integer> {

    List<Subway> findAllByCityEnName(String cityEnName);
}
