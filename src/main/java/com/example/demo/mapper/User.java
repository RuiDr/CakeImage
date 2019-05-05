package com.example.demo.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface User {
    @Select("select username from user where email=#{email} and password=#{password}")
    public String login(@Param("email") String email,@Param("password")String password);
}
