package org.shop.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.admin.entity.Notice;
import org.shop.admin.entity.dto.NoticeAllDTO;


public interface NoticeService extends IService<Notice> {

    /**
     * 发布通知
     */
    void publishNotice(NoticeAllDTO noticeAllDTO);

    /**
     * 更新通知
     */
    void updateNotice(NoticeAllDTO noticeAllDTO);

    /**
     * 删除通知
     */
    void removeNotice(NoticeAllDTO noticeAllDTO);
}
