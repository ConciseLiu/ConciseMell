package com.leyou.service;

import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ArrayUtils;

import java.util.*;

@Service
public class GoodsService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    public Map<String, Object> loadData(Long spuId) {
        HashMap<String, Object> map = new HashMap<>();

        Spu spu = goodsClient.querySpuById(spuId);

        List<Sku> skus = goodsClient.querySkuBySpuId(spuId);

        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> cnames = categoryClient.queryNameByIds(cids);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> categoriesMap = new HashMap<>();
            categoriesMap.put("id", cids.get(i));
            categoriesMap.put("name", cnames.get(i));
            categories.add(categoriesMap);
        }

        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        // 特殊参数
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);

        // 查询规格组
        List<SpecGroup> groups = specificationClient.querySpecByCid(spu.getCid3());
        Map<Long, Object> specGroupMap = new HashMap<>();
        groups.forEach(sg -> {
            specGroupMap.put(sg.getId(), sg.getName());
        });

        map.put("spu", spu);
        map.put("spuDetail", spuDetail);
        map.put("skus", skus);
        map.put("categories", categories);
        map.put("brand", brand);
        map.put("specGroupMap", specGroupMap);
        map.put("groups", groups);

        return map;
    }
}
