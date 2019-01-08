package com.lwj.house.entity;

import com.lwj.house.HouseApplicationTests;
import com.lwj.house.repository.UserRepository;

import com.lwj.house.web.dto.UserDTO;
import lombok.ToString;
import org.junit.Test;
import org.modelmapper.ModelMapper;
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

    @Test
    public void testMapper(){
        User user = new User();
        user.setGender(1);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(UserDTO.userToUserDtoMap);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        System.out.println(userDTO.toString());
    }

}
