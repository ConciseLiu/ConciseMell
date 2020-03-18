package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 1. 查询符合条件的spu数据
        PageHelper.startPage(page, rows);
        Page<Spu> spus = (Page<Spu>) this.spuMapper.selectByExample(example);

        // 2. 讲category_id和brand_id单独查询出来
        List<SpuBo> spuBos = spus.getResult().stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);

            String brandName = this.brandMapper.selectByPrimaryKey(spuBo.getBrandId()).getName();
            spuBo.setBname(brandName);

            List<String> cNameList = this.categoryService.queryNameByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
            spuBo.setCname(StringUtils.join(cNameList, "-"));

            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(spus.getTotal(), spuBos);
    }

    @Transactional
    public void addGoods(SpuBo spuBo) {
        spuBo.setId(null);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(true);
        spuBo.setSaleable(true);

        // 1. 保存spu

        this.spuMapper.insertSelective(spuBo);

        // 2. 保存spuDetail

        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());

        this.spuDetailMapper.insertSelective(spuDetail);
        
        // 3. 保存sku
        this.saveSku(spuBo);

        this.sendMessage(spuBo.getId(), "insert");
    }

    // 保存sku
    private void saveSku(SpuBo spuBo) {
        List<Sku> skus = spuBo.getSkus();
        skus.forEach(sku -> {
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            this.skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setSeckillStock(sku.getStock());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    public SpuDetail querySpuDetail(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkuById(Long id) {
        Sku sku = new Sku();
        sku.setSpuId(id);

        List<Sku> skuList = this.skuMapper.select(sku);
        skuList.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skuList;
    }

    // 更新商品
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        List<Sku> skus = this.querySkuById(spuBo.getId());
        if (!CollectionUtils.isEmpty(skus)) {
            List<Long> ids = skus.stream().map(Sku::getId).collect(Collectors.toList());

            // 1. 删除stock
            Example example = new Example(Stock.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);
            // 2. 删除sku

            Sku sku = new Sku();
            sku.setSpuId(spuBo.getId());
            this.skuMapper.delete(sku);
        }

        // 新增sku和库存
        this.saveSku(spuBo);

        // 更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        this.sendMessage(spuBo.getId(), "update");
    }

    // 消息队列发送消息
    private void sendMessage(Long id, String type) {
        try {
            this.amqpTemplate.convertAndSend("item."+type, id);
        } catch (AmqpException e) {
            LOGGER.error("{}商品消息发送异常，常品ID:{}", type, id);
        }
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
