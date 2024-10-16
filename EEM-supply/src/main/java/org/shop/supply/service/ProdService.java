package org.shop.supply.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.supply.entity.Prod;
import org.shop.supply.entity.ProdFunc;
import org.shop.supply.entity.dto.ProdGreatDTO;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.vo.ProdAllVO;
import org.shop.supply.entity.vo.ProdGreatVO;

import java.time.LocalDateTime;
import java.util.List;

public interface ProdService extends IService<Prod> {


    //! ES

    /**
     * 搜索单件商品自动补全
     */
    List<String> searchProd4ESSuggestion(String perfix);

    //! Sync 4 MQ

    /**
     * 删除
     */
    void deleteById(Long id);

    /**
     * 插入
     */
    void insertById(Long id);


    //! Func


    /**
     * 管理员审核单件商品
     */
    void checkA(ProdLocateDTO prodLocateDTO);

    /**
     * 管理员冻结单件商品
     */
    void freezeA(ProdLocateDTO prodLocateDTO);

    /**
     * 管理员分页查看需要审核商品
     */
    Page<ProdGreatVO> page2CheckA(Integer current);


    /**
     * 优惠券商品状态修改
     */
    void putProdStatusG(ProdLocateDTO prodLocateDTO, Integer func);


    /**
     * 处理到达失效展示时间的商品对象
     */
    List<ProdFunc> getOutdateProdA(LocalDateTime time);

    /**
     * 提取热搜表
     */
    List<ProdFunc> getHotProdA();


    /**
     * 商品从提升表中移除
     */
    void cooldownUpshowProdA(ProdFunc prodFunc);

    /**
     * 商品从轮播表中移除
     */
    void cooldownRotationProdA(ProdFunc prodFunc);


    /**
     * 添加商品到热搜表
     */
    void add2HotSearchA(ProdFunc prodFunc);


    //! ADD

    /**
     * 用户添加商品
     */
    void postProdG(ProdGreatDTO prodGreatDTO);


    //! DELETE


    /**
     * 管理员删除一件商品
     */
    void deleteProdA(ProdLocateDTO prodLocateDTO);


    /**
     * 用户删除商品
     */
    void deleteProdG(String name);


    //! UPDATE


    /**
     * 用户更新单件商品
     */
    void putProdG(ProdGreatDTO prodGreatDTO) throws InstantiationException, IllegalAccessException;


    /**
     * 用户更新单件商品[缓存]
     */
    void putProd8CG(ProdGreatDTO prodGreatDTO) throws InstantiationException, IllegalAccessException;


    //! QUERY

    /**
     * 管理员查看单件商品(简易)
     */
    Prod getProd8EzA(ProdLocateDTO prodLocateDTO);

    /**
     * 管理员分页查询所有商品列表
     */
    Page<ProdGreatVO> pageProdA(Integer current);

    /**
     * 管理员按Name模糊搜索商品
     */
    Page<ProdGreatVO> searchProdA(String name, Integer current);


    /**
     * 用户查看单件商品详细
     */
    ProdGreatVO getProdG(ProdLocateDTO prodLocateDTO);

    /**
     * 用户查看单件商品详细[缓存]
     */
    ProdGreatVO getProd8CG(ProdLocateDTO prodLocateDTO);

    /**
     * 用户分页分类查商品列表
     */
    Page<Prod> pageProdCateG(String cate, Integer current);

    /**
     * 用户分页查分类下所有商品列表
     */
    Page<Prod> pageProd8CateG(String cate, Integer current);

    /**
     * 用户按Name模糊搜索商品
     */
    Page<ProdAllVO> searchProd8EzG(String name, Integer current);
}
