package com.tangym.sql.server.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExplainResult implements Serializable {
    private static final long serialVersionUID = 7431080679251530072L;
    private String selectType;
    private String table;
    private String partitions;
    private String type;
    private String possibleKeys;
    private String key;
    private Integer keyLen;
    private String ref;
    private Integer rows;
    private Integer filtered;
    private String Extra;
}
