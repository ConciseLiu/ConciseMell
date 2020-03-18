package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPageAndSort(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        PageHelper.startPage(page, rows);

        Example example = new Example(Brand.class);

        if (StringUtils.isNotBlank(key)) {
            example.createCriteria().andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + " " + (desc ? "ASC" : "DESC");
            example.setOrderByClause(orderByClause);
        }

        Page<Brand> pageInfo = (Page<Brand>)this.brandMapper.selectByExample(example);

        return new PageResult<>(pageInfo.getTotal(), pageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBrand(Brand brand, List<Long> cids) {
        this.brandMapper.insertSelective(brand);

        Long bid = brand.getId();
        // 保存分类和品牌的中间表
        for (Long cid : cids) {
            this.brandMapper.saveBrandAndCategory(cid, bid);
        }
    }

    // 更新品牌信息
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(Brand brand, List<Long> cids) {
        Long bid = brand.getId();

        // 1. 删除原来的维护信息
        this.brandMapper.deleteCategoryBrandByBid(bid);

        // 2. 更新品牌信息
        this.brandMapper.updateByPrimaryKeySelective(brand);

        // 3. 维护中间表信息

        for (Long cid : cids) {
            this.brandMapper.saveBrandAndCategory(cid, bid);
        }
    }

    // 删除品牌
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrand(Long bid) {
        // 1. 删除brand
        this.brandMapper.deleteByPrimaryKey(bid);
        // 2. 删除中间表

        this.brandMapper.deleteCategoryBrandByBid(bid);
    }

    // 根据categoryId 查询品牌
    public List<Brand> queryBrandByCid(Long cid) {
        return this.brandMapper.queryBrandByCid(cid);
    }

    public Brand queryBrandId(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
