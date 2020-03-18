package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.xml.ws.Response;
import java.util.List;

@RestController
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5" ) Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "key", required = false) String key
            ) {
        PageResult<Brand> brandPageResult = this.brandService.queryBrandByPageAndSort(page, rows, sortBy, desc, key);

        if (brandPageResult == null || brandPageResult.getItems().size() <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brandPageResult);
    }

    // 根据id查找品牌
    @GetMapping("/brand/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id")Long id) {
        Brand brand = this.brandService.queryBrandId(id);
        if (brand == null) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    @PostMapping("/brand")
    public ResponseEntity<String> addBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.addBrand(brand, cids);
        return ResponseEntity.ok("");
    }

    @PutMapping("/brand")
    public ResponseEntity<String> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        this.brandService.updateBrand(brand, cids);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/brand/delete/{bid}")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid")Long bid) {
        this.brandService.deleteBrand(bid);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 根据分类查询品牌
    @GetMapping("/brand/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid) {
        List<Brand> brand = this.brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brand)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }


}
