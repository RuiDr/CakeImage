package com.example.cakeImage.mapper;
        import com.example.cakeImage.entity.Ahash;

        import org.apache.ibatis.annotations.Delete;
        import org.apache.ibatis.annotations.Insert;
        import org.apache.ibatis.annotations.Param;
        import org.apache.ibatis.annotations.Select;
        import org.springframework.stereotype.Component;
@Component
public interface Common {
    @Select("select tno from stu where sno=#{sno} and password=#{password}")
    String login(@Param("sno") String sno, @Param("password") String password);
    @Delete("delete from stu where sno=#{sno} and password=#{password}")
     int delById(@Param("sno") String id,@Param("password") String password);
    @Insert("insert into ahash(id,address,finger)"+"values(#{id},#{address},#{finger})")
    int addImages(Ahash imagesInfo);

}
