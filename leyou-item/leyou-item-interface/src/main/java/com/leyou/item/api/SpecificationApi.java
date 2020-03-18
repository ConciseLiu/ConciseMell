package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {

    /**
     * 查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    @GetMapping("/spec/params")
    List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false)Long page,
            @RequestParam(value = "cid", required = false)Long rows,
            @RequestParam(value = "generic", required = false)Boolean saleable,
            @RequestParam(value = "searching", required = false)Boolean key
    );

    /**
     * 根据分类id查找规格参数（包含在参数组内）
     * @param cid Long 分类ID
     * @return List 规格参数组（内包含规格参数）
     */
    @GetMapping("/spec/{cid}")
    List<SpecGroup> querySpecByCid(@PathVariable("cid")Long cid);

    /**
     * 根据分类id查找规格组
     * @param cid 分类id
     * @return 规格参数组
     */
    @GetMapping("/spec/groups/{cid}")
    List<SpecGroup> queryGroupsByCid(@PathVariable("cid")Long cid);
}
