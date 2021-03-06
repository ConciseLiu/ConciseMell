package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    // 根据多个id查找分类名称
    @GetMapping("names")
    List<String> queryNameByIds(@RequestParam("ids")List<Long> ids);
}
