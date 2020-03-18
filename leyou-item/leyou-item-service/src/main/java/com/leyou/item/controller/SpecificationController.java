package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    // 查询分组
    @GetMapping("/spec/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid")Long cid) {
        List<SpecGroup> groups = this.specificationService.queryGroupByCid(cid);
        if (CollectionUtils.isEmpty(groups)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }

    // 增加分组
    @PostMapping("/spec/group")
    public ResponseEntity<Void> addGroup(@RequestBody SpecGroup specGroup) {
        this.specificationService.addGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 修改分组
    @PutMapping("/spec/group")
    public ResponseEntity<Void> updateGroup(@RequestBody SpecGroup specGroup) {
        this.specificationService.updateGroup(specGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 删除分组
    @DeleteMapping("/spec/group/{gid}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("gid")Long gid) {
        this.specificationService.deleteGroup(gid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //  查询分组参数
    @GetMapping("/spec/params")
    public ResponseEntity<List<SpecParam>> queryParamsByGid(
            @RequestParam(value = "gid", required = false)Long gid,
            @RequestParam(value = "cid", required = false)Long cid,
            @RequestParam(value = "generic", required = false)Boolean generic,
            @RequestParam(value = "searching", required = false)Boolean searching
    ) {
        List<SpecParam> params = this.specificationService.querySpecParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(params)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    @PostMapping("/spec/param")
    public ResponseEntity<Void> addParam(@RequestBody SpecParam specParam) {
      this.specificationService.addParam(specParam);
      return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/spec/param")
    public ResponseEntity<Void> updateParam(@RequestBody SpecParam specParam) {
        this.specificationService.updateParam(specParam);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/spec/param/{pid}")
    public ResponseEntity<Void> deleteParam(@PathVariable("pid")Long pid) {
        this.specificationService.deleteParam(pid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 根据分类id查找规格参数
    @GetMapping("/spec/{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecByCid(@PathVariable("cid")Long cid) {
        List<SpecGroup> specGroupList = this.specificationService.querySepcByCid(cid);
        if (CollectionUtils.isEmpty(specGroupList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroupList);
    }
}
