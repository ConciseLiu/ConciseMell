package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface CategoryMapper extends Mapper<Category> , SelectByIdListMapper<Category, Long>{

    // 查询品牌的分类
    @Select("select id, name from tb_category where id in(select category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoryOfBrand(Long bid);
}
