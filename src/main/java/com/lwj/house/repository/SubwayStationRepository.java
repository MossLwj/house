package com.lwj.house.repository;

import com.lwj.house.entity.SubwayStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 地铁站Jpa类
 * @author lwj
 */
public interface SubwayStationRepository extends JpaRepository<SubwayStation, Integer> {

    List<SubwayStation> findAllBySubwayId(Integer subwayId);
}
