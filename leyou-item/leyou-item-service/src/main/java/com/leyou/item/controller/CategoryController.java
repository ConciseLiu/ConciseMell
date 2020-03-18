package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.websocket.server.PathParam;
import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 根据父ID查找分类
    @GetMapping("list")
    public ResponseEntity<List<Category>> findCategoriesByPid(@RequestParam("pid")Long pid) {
        if (pid == null || pid < 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = this.categoryService.findCategoriesByPid(pid);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    // 根据ids获取分类名称
    @GetMapping("names")
    public ResponseEntity<List<String>> findNameByIds(@RequestParam List<Long> ids) {
        List<String> list = this.categoryService.queryNameByIds(ids);
        if (CollectionUtils.isEmpty(list)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("add")
    public ResponseEntity<Integer> addCategory(@RequestBody Category category) {
        this.categoryService.addCategory(category);
        return ResponseEntity.ok(200);
    }

    @PostMapping("update")
    public ResponseEntity<Integer> updateCategory(@RequestBody Category category) {
        Integer integer = this.categoryService.updateCategory(category);
        return ResponseEntity.ok(integer);
    }

    @GetMapping("delete")
    public ResponseEntity<Integer> deleteCategory(@RequestParam("id") Long id) {
        Integer integer = this.categoryService.deleteCategory(id);
        return ResponseEntity.ok(integer);
    }

    // 获取品牌的分类
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryCategoryOfBrand(@PathVariable("bid") Long bid) {
        List<Category> categories = this.categoryService.queryCategoryOfBrand(bid);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
}
