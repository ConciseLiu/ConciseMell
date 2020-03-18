package com.leyou.service;

import com.leyou.sms.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    public void createHtml(Long spuId) {
        PrintWriter writer = null;
        Map<String, Object> stringObjectMap = this.goodsService.loadData(spuId);

        Context context = new Context();
        context.setVariables(stringObjectMap);

        try {
            File file = new File("D:\\laragon\\bin\\nginx\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);
            templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\laragon\\bin\\nginx\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
