package org.shop.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.client.UserClient;
import org.shop.trade.common.constant.MessageConstant;
import org.shop.trade.common.constant.ServiceConstant;
import org.shop.trade.common.constant.TestsConstant;
import org.shop.trade.common.context.UserHolder;
import org.shop.trade.common.exception.BadArgsException;
import org.shop.trade.common.exception.SthNotFoundException;
import org.shop.trade.common.exception.TrashException;
import org.shop.trade.entity.Order;
import org.shop.trade.entity.Voucher;
import org.shop.trade.entity.dto.VoucherAllDTO;
import org.shop.trade.entity.dto.VoucherLocateDTO;
import org.shop.trade.entity.remote.UserFunc;
import org.shop.trade.mapper.VoucherMapper;
import org.shop.trade.mapper.repo.TradeRepo;
import org.shop.trade.service.OrderService;
import org.shop.trade.service.VoucherService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {


    private final OrderService orderService;

    private final UserClient userClient;
    private final TradeRepo tradeRepo;

    @Override
    public void putSeckillVoucherA(VoucherAllDTO voucherAllDTO) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherAllDTO, voucher);
        voucher.setUserId(1L);
        voucher.setType(1);
        this.save(voucher);
    }


    @Override
    @Transactional
    public void putVoucherA(VoucherAllDTO voucherAllDTO) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherAllDTO, voucher);
        voucher.setUserId(TestsConstant.STORE_USERID); //存到默认仓库用户
        this.save(voucher);
    }


    @Override
    @Transactional
    public void claimVoucherG(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = tradeRepo.findByVoucherName_UserId(voucherLocateDTO.getName(), TestsConstant.STORE_USERID);

        if (voucher == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        if (voucher.getStock() <= 0) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);


        voucher.setStock(voucher.getStock() - 1);

        //创建一个新的卷对象, 用于用户使用
        Voucher newVoucher = new Voucher();
        BeanUtils.copyProperties(voucher, newVoucher);
        newVoucher.setId(null); //清空ID, 重新插入
        newVoucher.setName(voucher.getName() + UserHolder.getUser().getId() + "-已领取");
        newVoucher.setUserId(UserHolder.getUser().getId());
        newVoucher.setStock(1);


        this.updateById(voucher);
        this.save(newVoucher);
    }


    @Override
    @Transactional
    public Integer useVoucher4Seller(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = tradeRepo.findByVoucherName_UserId(voucherLocateDTO.getName(), UserHolder.getUser().getId());
        if (voucher == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        if (Objects.equals(voucher.getStatus(), Voucher.USED) || Objects.equals(voucher.getStatus(), Voucher.OUTDATE) || voucher.getStock() == 0)
            throw new TrashException(MessageConstant.TRASH_ERROR);

        if (Objects.equals(voucher.getUser(), Voucher.BUYER)) throw new BadArgsException(MessageConstant.BAD_ARGS);


        //执行功能: 修改当前Voucher对象
        voucher.setStatus(Voucher.USED);
        voucher.setBeginTime(LocalDateTime.now());
        voucher.setEndTime(LocalDateTime.now().plusDays(ServiceConstant.UPSHOW_LEVEL_TTL[voucher.getFunc()]));

        this.updateById(voucher);


        //用户增加嘉奖值
        UserFunc userFunc = userClient.getById_UserFunc(UserHolder.getUser().getId());

        int value2Add = voucher.getType() == 0 ? voucher.getValue() : voucher.getValue() * 2;

        userFunc.setCredit(userFunc.getCredit() + value2Add);
        userClient.updateById_UserFunc(userFunc);

        //回传给前端效果字段 : voucher.getFunc(), 0 - 1 - 2 指明其功能类型, 用于后续前端打开窗口,
        // 让用户指定商品对象进行Update操作-> ProdFunc字段修改, 请求见ProdController
        return voucher.getFunc();
    }


    @Override
    @Transactional
    public boolean useVoucher4Buyer(VoucherLocateDTO voucherLocateDTO) {

        Voucher voucher = tradeRepo.findByVoucherName_UserId(voucherLocateDTO.getName(), UserHolder.getUser().getId());

        if (voucher == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        if (Objects.equals(voucher.getStatus(), Voucher.USED) || Objects.equals(voucher.getStatus(), Voucher.OUTDATE) || voucher.getStock() == 0)
            throw new TrashException(MessageConstant.TRASH_ERROR);

        if (Objects.equals(voucher.getUser(), Voucher.SELLER)) throw new BadArgsException(MessageConstant.BAD_ARGS);


        //执行功能: 修改当前Voucher对象
        voucher.setStatus(Voucher.USED);
        voucher.setBeginTime(LocalDateTime.now());
        voucher.setEndTime(LocalDateTime.now().plusDays(ServiceConstant.UPSHOW_LEVEL_TTL[voucher.getFunc()]));

        this.updateById(voucher);

        //用户增加嘉奖值
        UserFunc userFunc = userClient.getById_UserFunc(UserHolder.getUser().getId());

        int value2Add = voucher.getType() == 0 ? voucher.getValue() : voucher.getValue() * 2;

        userFunc.setCredit(userFunc.getCredit() + value2Add);
        userClient.updateById_UserFunc(userFunc);

        //对目前开启的交易判定是否存在, 存在则视为一次准入成功, 对用户进行增加嘉奖值操作(否则没有奖励)

        Order order = orderService.getOne(new LambdaQueryWrapper<Order>().eq(Order::getBuyerId, TestsConstant.BUYER_USERID));

        if (order == null) return false;

        UserFunc userFunc2 = userClient.getById_UserFunc(TestsConstant.BUYER_USERID);
        userFunc2.setGodhit(userFunc2.getGodhit() + 1);
        userClient.updateById_UserFunc(userFunc2);

        return true;
    }


    @Override
    public List<Voucher> getOutdateOnesA(Integer status, LocalDateTime time) {
        List<Voucher> voucherList2Check = this.query().eq("status", status).list();

        //需要手动取出来判断是否过期
        voucherList2Check.removeIf(voucher -> voucher.getEndTime().isAfter(time));

        return voucherList2Check;
    }

    @Override
    public void ruinVoucherA(Voucher voucher) {
        voucher.setStatus(2);
        this.updateById(voucher);
    }

    @Override
    public Page<Voucher> searchVoucherB(String name, Integer current) {

        //分页展示模糊匹配的所有可能结果
        return this.page(new Page<>(current, 10), new LambdaQueryWrapper<Voucher>().like(Voucher::getName, name));
    }


}
