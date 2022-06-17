package com.tangym.sql.server.dto.request;

import com.tangym.sql.server.entity.SqlExplainTableFingerprint;
import lombok.Data;

import java.io.Serializable;

@Data
public class SqlTableHashPageRequest extends PageRequestDTO implements Serializable {
    private static final long serialVersionUID = 1426655952362719905L;

    private SqlExplainTableFingerprint sqlExplainTableFingerprint;
}
