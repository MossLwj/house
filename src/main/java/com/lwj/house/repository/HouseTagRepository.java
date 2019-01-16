package com.lwj.house.repository;

import com.lwj.house.entity.HouseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lwj
 */
@Repository
public interface HouseTagRepository extends JpaRepository<HouseTag, Integer> {

    /**
     * 根据houseId获取tags
     * @param id
     * @return
     */
    List<HouseTag> findAllByHouseId(Integer id);
}
