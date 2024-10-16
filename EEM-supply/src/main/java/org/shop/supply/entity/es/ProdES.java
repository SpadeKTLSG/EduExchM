package org.shop.supply.entity.es;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.shop.supply.entity.Prod;

/**
 * 商品ES实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProdES {

    /**
     * 主键Prod唯一
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 价格
     */
    private Long price;

    /**
     * 图片 集合
     */
    private String images;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 描述
     */
    private String description;

    /**
     * 对应用户的ID
     */
    private Long userId;

    /**
     * Prod -> ProdES
     */
    public ProdES(Prod prod) {
        this.id = prod.getId();
        this.name = prod.getName();
        this.categoryId = prod.getCategoryId();
        this.price = prod.getPrice();
        this.images = prod.getImages();
        this.stock = prod.getStock();
        this.description = prod.getDescription();
        this.userId = prod.getUserId();
    }

    // 保存的数据结构
    //{
    //  "prod" : {
    //    "aliases" : { },
    //    "mappings" : {
    //      "properties" : {
    //        "categoryId" : {
    //          "type" : "keyword"
    //        },
    //        "description" : {
    //          "type" : "text",
    //          "analyzer" : "ik_max_word"
    //        },
    //        "id" : {
    //          "type" : "keyword"
    //        },
    //        "images" : {
    //          "type" : "keyword"
    //        },
    //        "name" : {
    //          "type" : "text",
    //          "analyzer" : "ik_max_word"
    //        },
    //        "price" : {
    //          "type" : "long"
    //        },
    //        "stock" : {
    //          "type" : "long"
    //        },
    //        "userId" : {
    //          "type" : "keyword"
    //        }
    //      }
    //    },    ...
    //}
}
