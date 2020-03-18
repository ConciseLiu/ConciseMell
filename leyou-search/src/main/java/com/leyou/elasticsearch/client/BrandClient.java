package com.leyou.elasticsearch.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "leyou-service")
public interface BrandClient extends BrandApi {
}
