package com.lwj.house.repository;

import com.lwj.house.entity.HousePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lwj
 */
@Repository
public interface HousePictureRepository extends JpaRepository<HousePicture, Integer> {

}