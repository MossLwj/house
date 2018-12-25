package com.lwj.house.entity;

import com.lwj.house.HouseApplicationTests;
import com.lwj.house.repository.UserRepository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.util.Optional;

public class UserRepositoryTest extends HouseApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Test
    public void testFindOne(){
//        User user = new User();
//        user.setId(1L);
//        Example<User> example = Example.of(user);
//        Optional<User> productOptional = userRepository.findOne(example);

        User user1 = userRepository.findById(1).get();
        System.out.println(user1.toString());
    }

}
