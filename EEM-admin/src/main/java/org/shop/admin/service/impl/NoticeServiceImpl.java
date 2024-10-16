package org.shop.admin.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.common.annotation.AutoFill;
import org.shop.admin.common.constant.MessageConstant;
import org.shop.admin.common.enumeration.OperationType;
import org.shop.admin.common.exception.SthHasCreatedException;
import org.shop.admin.common.exception.SthNotFoundException;
import org.shop.admin.entity.Notice;
import org.shop.admin.entity.dto.NoticeAllDTO;
import org.shop.admin.mapper.NoticeMapper;
import org.shop.admin.service.NoticeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {


    @AutoFill(OperationType.INSERT)
    @Override
    public void publishNotice(NoticeAllDTO noticeAllDTO) {

        Notice notice2 = this.getOne(Wrappers.<Notice>lambdaQuery()
                .eq(Notice::getTitle, noticeAllDTO.getTitle()));
        if (notice2 != null) throw new SthHasCreatedException(MessageConstant.OBJECT_HAS_ALIVE);

        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeAllDTO, notice);
        this.save(notice);
    }


    @AutoFill(OperationType.UPDATE)
    @Override
    public void updateNotice(NoticeAllDTO noticeAllDTO) {

        Notice notice = this.getOne(Wrappers.<Notice>lambdaQuery()
                .eq(Notice::getTitle, noticeAllDTO.getTitle()));
        if (notice == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        BeanUtils.copyProperties(noticeAllDTO, notice);
        this.updateById(notice);
    }


    @Override
    public void removeNotice(NoticeAllDTO noticeAllDTO) {

        Notice notice = this.getOne(Wrappers.<Notice>lambdaQuery()
                .eq(Notice::getTitle, noticeAllDTO.getTitle()));
        if (notice == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        this.removeById(notice.getId());
    }


}
