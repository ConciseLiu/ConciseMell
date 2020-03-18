package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import javax.persistence.Entity;

@Repository
public interface SpecGroupMapper extends Mapper<SpecGroup> {

}
