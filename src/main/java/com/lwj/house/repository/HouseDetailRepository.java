package com.lwj.house.repository;

import com.lwj.house.entity.HouseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lwj
 */
@Repository
public interface HouseDetailRepository extends JpaRepository<HouseDetail, Integer> {

    /**
     * 通过houseId获取houseDetail
     * @param id
     * @return
     */
    HouseDetail findByHouseId(Integer id);

    List<HouseDetail> findByHouseIdIn(List<Integer> houseIds);
}
