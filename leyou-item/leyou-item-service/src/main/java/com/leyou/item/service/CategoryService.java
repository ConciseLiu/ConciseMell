package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> findCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);
    }

    public Long addCategory(Category category) {
        //添加分类时，不允许
        category.setId(null);
        this.categoryMapper.insert(category);
        // 设置其父元素有子元素
        Category parent = new Category();
        parent.setId(category.getParentId());
        parent.setIsParent(true);
        this.categoryMapper.updateByPrimaryKeySelective(parent);
        return category.getId();
    }

    public Integer updateCategory(Category category) {
        return this.categoryMapper.updateByPrimaryKey(category);
    }

    public Integer deleteCategory(Long id) {
        return this.categoryMapper.deleteByPrimaryKey(id);
    }

    public List<Category> queryCategoryOfBrand(Long bid) {
        List<Category> categories = this.categoryMapper.queryCategoryOfBrand(bid);
        return categories;
    }

    // 根据ids查找分类名称
    public List<String> queryNameByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);

        List<String> list = new ArrayList<>();
        for (Category category : categories) {
            list.add(category.getName());
        }
        return list;
    }

}
