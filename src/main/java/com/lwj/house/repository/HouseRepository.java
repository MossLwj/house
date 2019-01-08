package com.lwj.house.repository;

import com.lwj.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lwj
 */
@Repository
public interface HouseRepository extends JpaRepository<House, Integer> {

}
