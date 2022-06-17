package com.tangym.sql.server.dto.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PageRequestDTO implements Serializable {

    private static final long serialVersionUID = 443087049123344968L;

    @Min(1)
    @NotNull(message = "页码不能为空！")
    private Integer pageNum;

    @Min(0)
    @NotNull(message = "页大小不能为空！")
    private Integer pageSize;
}
