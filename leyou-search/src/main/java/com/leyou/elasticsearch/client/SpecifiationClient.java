package com.leyou.elasticsearch.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "leyou-service")
public interface SpecifiationClient extends SpecificationApi {
}
