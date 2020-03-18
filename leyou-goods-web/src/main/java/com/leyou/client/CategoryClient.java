package com.leyou.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "leyou-service")
public interface CategoryClient extends CategoryApi {
}
