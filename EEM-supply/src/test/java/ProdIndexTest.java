import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.shop.supply.SupplyApplication;
import org.shop.supply.entity.Prod;
import org.shop.supply.service.ProdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.List;

@SpringBootTest(classes = {SupplyApplication.class})
@TestPropertySource(locations = "classpath:application-dev.yml")
public class ProdIndexTest {


    @Value("${eduexch.es.host}")
    private String esHost;

    @Value("${eduexch.es.port}")
    private int esPort;

    @Value("${eduexch.es.scheme}")
    private String esScheme;

    @Autowired
    private ProdService prodService;
    @Autowired
    private RestHighLevelClient client;


    /**
     * 创建索引
     */
    @Test
    void createIndex() throws IOException {
        // 1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("prod");
        // 2.准备请求的参数：DSL语句
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        // 3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 同步数据到ES
     */
    @Test
    void syncData() throws IOException {
        List<Prod> prodList = prodService.list();
        for (Prod prod : prodList) {
            IndexRequest request = new IndexRequest("prod").id(prod.getId().toString());
            request.source(JSON.toJSONString(prod), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }
    }


    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\"type\": \"keyword\"},\n" +
            "      \"name\": {\"type\": \"text\", \"analyzer\": \"ik_max_word\"},\n" +
            "      \"categoryId\": {\"type\": \"keyword\"},\n" +
            "      \"price\": {\"type\": \"long\"},\n" +
            "      \"images\": {\"type\": \"keyword\"},\n" +
            "      \"stock\": {\"type\": \"long\"},\n" +
            "      \"description\": {\"type\": \"text\", \"analyzer\": \"ik_max_word\"},\n" +
            "      \"userId\": {\"type\": \"keyword\"}\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
