import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
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
     * 创建索引库 (一般)
     */
    @Test
    void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("prod");
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 创建索引库(覆盖) (带上分词器: elasticsearch-analysis-ik-7.12.1)
     * ! 服务器内存不足, 无法安装分词器
     */
    @Test
    void createIndexWithIK() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("prod");
        request.source(MAPPING_TEMPLATE_PINYIN, XContentType.JSON);
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

    /**
     * 删除索引
     */
    @Test
    void deleteIndex() throws IOException {
        client.indices().delete(new DeleteIndexRequest("prod"), RequestOptions.DEFAULT);
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


    private static final String MAPPING_TEMPLATE_PINYIN = "{\n" +
            "  \"settings\": {\n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"my_analyzer\": {\n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\"py\"]\n" +
            "        }\n" +
            "      },\n" +
            "      \"filter\": {\n" +
            "        \"py\": {\n" +
            "          \"type\": \"pinyin\",\n" +
            "          \"keep_full_pinyin\": false,\n" +
            "          \"keep_joined_full_pinyin\": true,\n" +
            "          \"keep_original\": true,\n" +
            "          \"limit_first_letter_length\": 16,\n" +
            "          \"remove_duplicated_term\": true,\n" +
            "          \"none_chinese_pinyin_tokenize\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\"type\": \"keyword\"},\n" +
            "      \"name\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"my_analyzer\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
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
