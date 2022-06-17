package com.tangym.sql.server.dto.request;

import com.tangym.sql.server.entity.SqlAppConfigs;
import lombok.Data;

import java.io.Serializable;

@Data
public class SqlAppConfigsPageRequest extends PageRequestDTO implements Serializable {
    private static final long serialVersionUID = -32665127932663209L;

    private SqlAppConfigs appConfigs;
}
