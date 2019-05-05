package com.example.cakeImage.mapper;
        import org.apache.ibatis.annotations.Delete;
        import org.apache.ibatis.annotations.Param;
        import org.apache.ibatis.annotations.Select;
        import org.springframework.stereotype.Component;
@Component
public interface Common {
    @Select("select tno from stu where sno=#{sno} and password=#{password}")
    public String login(@Param("sno") String sno, @Param("password") String password);
    @Delete("delete from stu where sno=#{sno} and password=#{password}")
    public int delById(@Param("sno") String id,@Param("password") String password);
}
