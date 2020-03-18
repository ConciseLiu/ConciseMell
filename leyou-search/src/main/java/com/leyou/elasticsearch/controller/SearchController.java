package com.leyou.elasticsearch.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.elasticsearch.pojo.Goods;
import com.leyou.elasticsearch.pojo.SearchRequest;
import com.leyou.elasticsearch.service.SearchService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest) {
        PageResult<Goods> result = this.searchService.search(searchRequest);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
