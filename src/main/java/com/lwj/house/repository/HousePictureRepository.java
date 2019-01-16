package com.lwj.house.repository;

import com.lwj.house.entity.HousePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lwj
 */
@Repository
public interface HousePictureRepository extends JpaRepository<HousePicture, Integer> {

    /**
     * 根据houseId获取房屋照片list
     * @param id
     * @return
     */
    List<HousePicture> findByHouseId(Integer id);
}
