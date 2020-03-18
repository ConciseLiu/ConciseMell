package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }
    
    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        return this.specParamMapper.select(specParam);
    }

    public void addGroup(SpecGroup specGroup) {
        this.specGroupMapper.insert(specGroup);
    }

    public void updateGroup(SpecGroup specGroup) {
        this.specGroupMapper.updateByPrimaryKeySelective(specGroup);
    }

    // 删除分组
    public void deleteGroup(Long gid) {
        // 1. 先删除该分组参数

        // 2. 删除该分组
        SpecGroup specGroup = new SpecGroup();
        specGroup.setId(gid);
        this.specGroupMapper.deleteByPrimaryKey(specGroup);
    }

    public void addParam(SpecParam specParam) {
        this.specParamMapper.insert(specParam);
    }

    public void updateParam(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKeySelective(specParam);
    }

    public void deleteParam(Long pid) {
        SpecParam specParam = new SpecParam();
        specParam.setId(pid);
        this.specParamMapper.deleteByPrimaryKey(specParam);
    }

    //根据分类id查找分类名称
    public List<SpecGroup> querySepcByCid(Long cid) {
        List<SpecGroup> specGroups = this.queryGroupByCid(cid);
        specGroups.forEach( sg -> {
            sg.setParams(this.querySpecParams(sg.getId(), null, null, null));
        });
        return specGroups;
    }
}
