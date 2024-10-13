package org.shop.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.SystemConstant;
import org.shop.supply.entity.dto.NoticeAllDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.service.NoticeService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理面板 4合一
 */
@Slf4j
@Tag(name = "Panel", description = "管理面板")
@RequestMapping("/admin/panel")
@RestController
@RequiredArgsConstructor
public class PanelController {


    private final NoticeService noticeService;


    //* -- notice公告 --


    //! ADD


    /**
     * 发布公告
     * <p>使用公共字段填充</p>
     */
    @PostMapping("/notice/save")
    @Operation(summary = "发布公告")
    @Parameters(@Parameter(name = "noticeAllDTO", description = "公告发布DTO", required = true))
    public Result publishNotice(@RequestBody NoticeAllDTO noticeAllDTO) {
        noticeService.publishNotice(noticeAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/notice/save


    //! DELETE

    /**
     * 删除公告
     */
    @DeleteMapping("/notice/delete")
    @Operation(summary = "删除公告")
    @Parameters(@Parameter(name = "noticeAllDTO", description = "公告删除DTO", required = true))
    public Result removeNotice(@RequestBody NoticeAllDTO noticeAllDTO) {
        noticeService.removeNotice(noticeAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/notice/delete


    //! UPDATE

    /**
     * 更新公告
     */
    @PutMapping("/notice/update")
    @Operation(summary = "更新公告")
    @Parameters(@Parameter(name = "noticeAllDTO", description = "公告更新DTO", required = true))
    public Result updateNotice(@RequestBody NoticeAllDTO noticeAllDTO) {
        noticeService.updateNotice(noticeAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/notice/update


    //! QUERY

    /**
     * 分页查询公告
     */
    @GetMapping("/notice/query/page")
    @Operation(summary = "分页查询公告")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result queryNoticePage(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(noticeService.page(new Page<>(current, SystemConstant.DEFAULT_PAGE_SIZE)));
    }


}
