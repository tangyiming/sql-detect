package com.tangym.sql.server.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SqlProcessLog implements Serializable {
    private static final long serialVersionUID = -3439213296239654085L;
    private Integer id;
    /**
     * 记录关联sql数据id
     */
    private Integer explainInfoId;
    /**
     * 操作人工号
     */
    private String userId;
    /**
     * 操作人姓名
     */
    private String userName;
    /**
     * 操作类型
     */
    private String type;
    /**
     * 备注
     */
    private String remark;
    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;
    /**
     * 记录修改时间
     */
    private LocalDateTime updateTime;
}
