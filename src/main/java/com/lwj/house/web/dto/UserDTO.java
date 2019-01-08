package com.lwj.house.web.dto;

import com.lwj.house.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

/**
 * 用户DTO对象
 * @author lwj
 */
@Getter
@Setter
@ToString
public class UserDTO {
    private Integer id;
    private String name;
    private String avatar;
    private String phoneNumber;
    private String lastLoginTime;

    private String gender;


    private static String[] genders = {"未知", "男", "女"};

    public static PropertyMap<User, UserDTO> userToUserDtoMap = new PropertyMap<User, UserDTO>() {
        @Override
        protected void configure() {
            using(toGender).map(source.getGender(), destination.gender);
        }
    };

    public static Converter<Integer,String> toGender = new AbstractConverter<Integer, String>() {
        @Override
        protected String convert(Integer genderId) {
            return genders[genderId];
        }
    };
}
