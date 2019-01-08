package com.lwj.house.repository;

import com.lwj.house.entity.HouseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lwj
 */
@Repository
public interface HouseTagRepository extends JpaRepository<HouseTag, Integer> {

}
