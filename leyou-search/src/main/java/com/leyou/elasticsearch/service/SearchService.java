package com.leyou.elasticsearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.elasticsearch.client.BrandClient;
import com.leyou.elasticsearch.client.CategoryClient;
import com.leyou.elasticsearch.client.GoodsClient;
import com.leyou.elasticsearch.client.SpecifiationClient;
import com.leyou.elasticsearch.pojo.Goods;
import com.leyou.elasticsearch.pojo.SearchRequest;
import com.leyou.elasticsearch.pojo.SearchResult;
import com.leyou.elasticsearch.repository.GoodsRepository;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.xmlunit.util.Mapper;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecifiationClient specifiationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());

        // 保存价格

        List<Double> prices = new ArrayList<>();

        List<Map<String, Object>> skuMapList = new ArrayList<>();

        skus.forEach( sku -> {
            prices.add(sku.getPrice());

            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");

            skuMapList.add(skuMap);
        });


        List<SpecParam> params = this.specifiationClient.queryParams(null, spu.getCid3(), null, true);

        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());

        Map<Long, Object> genericMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });

        Map<Long, List<Object>> speciaMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });

        Map<String, Object> paramMap = new HashMap<>();

        params.forEach(param -> {
            if (param.getGeneric()) {
                String value = genericMap.get(param.getId()).toString();

                if (param.getNumeric()) {
                    value = chooseSegment(value, param);
                }

                paramMap.put(param.getName(), value);

            } else {
                paramMap.put(param.getName(), speciaMap.get(param.getId()));
            }
        });

        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;

    }

    // 返回区间内的字符串
    private String chooseSegment(String value, SpecParam specParam) {
        double v = NumberUtils.toDouble(value);

        String result = "其它";
        for (String segments: specParam.getSegments().split(",")) {
            String[] segment = segments.split("-");

            Double begin = NumberUtils.toDouble(segment[0]);

            Double end = Double.MAX_VALUE;
            if (segment.length == 2) {
                end = NumberUtils.toDouble(segment[1]);
            }

            if (v > begin && v < end) {
                if (segment.length == 1) {
                    result = begin + specParam.getUnit() + "以上";
                } else if(begin == 0) {
                    result = segment[1] + specParam.getUnit() + "以下";
                } else {
                    result = segments + specParam.getUnit();
                }
                break;
            }
        }

        return result;
    }

    public PageResult<Goods> search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("all", key).operator(Operator.AND));

        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus","subTitle"}, null));

        // 获取分页参数
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();

        nativeSearchQueryBuilder.withPageable(PageRequest.of(page - 1, size));

        String categoryAggName = "categories";
        String brandAggName = "breads";

        String sortBy = searchRequest.getSortBy();
        Boolean descending = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }
        Page<Goods> pageInfo = this.goodsRepository.search(nativeSearchQueryBuilder.build());

        Long total = pageInfo.getTotalElements(); //总条数
        Integer totalPage = (total.intValue() + size - 1) / size;

        return new PageResult<>(total, totalPage.longValue(), pageInfo.getContent());
    }

    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
