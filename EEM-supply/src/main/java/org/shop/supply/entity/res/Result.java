package org.shop.supply.entity.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用返回结果")
public class Result {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 数据
     */
    private Object data;

    /**
     * 总数
     */
    private Long total;

    /**
     * 返回成功结果
     *
     * @return {@link Result }
     */
    public static Result success() {
        return new Result(true, null, null, null);
    }

    /**
     * 返回成功结果
     *
     * @param data 数据
     * @return {@link Result }
     */
    public static Result success(Object data) {
        return new Result(true, null, data, null);
    }

    /**
     * 返回成功结果
     *
     * @param data  数据
     * @param total 总数
     * @return {@link Result }
     */
    public static Result success(List<?> data, Long total) {
        return new Result(true, null, data, total);
    }

    /**
     * 返回失败结果
     *
     * @param errorMsg 错误信息
     * @return {@link Result }
     */
    public static Result error(String errorMsg) {
        return new Result(false, errorMsg, null, null);
    }
}
