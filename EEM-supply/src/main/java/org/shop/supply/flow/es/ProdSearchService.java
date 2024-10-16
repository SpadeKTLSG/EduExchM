package org.shop.supply.flow.es;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.shop.supply.entity.es.ProdES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProdSearchService {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 基础分页搜索商品
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 商品列表
     */
    public Page<ProdES> searchProd(int current, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("prod"); // 指定索引

        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from((current - 1) * size);
        sourceBuilder.size(size);
        sourceBuilder.sort("id", SortOrder.ASC);
        searchRequest.source(sourceBuilder);

        // 执行搜索
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        List<ProdES> prodList = new ArrayList<>();
        searchResponse.getHits().forEach(hit -> {
            ProdES prod = JSON.parseObject(hit.getSourceAsString(), ProdES.class);
            prodList.add(prod);
        });

        // 封装结果到分页对象
        Page<ProdES> page = new Page<>(current, size);
        page.setRecords(prodList);
        page.setTotal(searchResponse.getHits().getTotalHits().value);
        return page;
    }
}
