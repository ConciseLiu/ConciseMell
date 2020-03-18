package com.leyou.elasticsearch.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.elasticsearch.LeyouSearchService;
import com.leyou.elasticsearch.pojo.Goods;
import com.leyou.elasticsearch.repository.GoodsRepository;
import com.leyou.elasticsearch.service.SearchService;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Spu;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchService.class)
public class GoodsClientTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository goodsRepository;



    @Test
    public void testCreate() {
        this.elasticsearchTemplate.createIndex(Goods.class);
        this.elasticsearchTemplate.putMapping(Goods.class);

        Integer page  = 1;
        Integer rows = 100;

        do {
            PageResult<SpuBo> pageResult = this.goodsClient.querySpuByPage(null, true, page, rows);

            List<Goods> collect = pageResult.getItems().stream().map(item -> {

                try {
                    return this.searchService.buildGoods(item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());

            this.goodsRepository.saveAll(collect);

            rows = pageResult.getItems().size();
            page++;
        } while (rows == 100);
    }

}