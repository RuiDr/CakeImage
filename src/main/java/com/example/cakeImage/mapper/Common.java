package com.example.cakeImage.mapper;
        import com.example.cakeImage.entity.Ahash;

        import com.example.cakeImage.entity.Dhash;
        import com.example.cakeImage.entity.Phash;
        import org.apache.ibatis.annotations.Delete;
        import org.apache.ibatis.annotations.Insert;
        import org.apache.ibatis.annotations.Param;
        import org.apache.ibatis.annotations.Select;
        import org.springframework.stereotype.Component;

        import java.util.ArrayList;
        import java.util.Map;

@Component
public interface Common {
    @Select("select tno from stu where sno=#{sno} and password=#{password}")
    String login(@Param("sno") String sno, @Param("password") String password);
    @Delete("delete from stu where sno=#{sno} and password=#{password}")
     int delById(@Param("sno") String id,@Param("password") String password);
    @Insert("insert into ahash(id,address,finger)"+"values(#{id},#{address},#{finger})")
    int addImagesAhash(Ahash imagesInfo);

    @Insert("insert into phash(id,address,finger)"+"values(#{id},#{address},#{finger})")
    int addImagesPhash(Phash phash);

    @Insert("insert into dhash(id,address,finger)"+"values(#{id},#{address},#{finger})")
    int addImagesDhash(Dhash dhash);

    @Select("select * from ahash")
    ArrayList<Ahash> findAhash();

    @Select("select * from phash")
    ArrayList<Phash> findPhash();

    @Select("select * from dhash")
    ArrayList<Dhash> findDhash();


    @Select("select id from ahash where address=#{address}")
    String AhashByAddress(@Param("address")String address);

    @Select("select id from phash where address=#{address}")
    String PhashByAddress(@Param("address")String address);

    @Select("select id from dhash where address=#{address}")
    String DhashByAddress(@Param("address")String address);
}
