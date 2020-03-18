package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid}, #{bid})")
    void saveBrandAndCategory(Long cid, Long bid);

    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryBrandByBid(Long bid);

    @Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandByCid(Long cid);
}
