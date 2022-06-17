package com.tangym.sql.server.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tangym.sql.server.entity.SqlExplainInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SqlDetailListPageRequest extends PageRequestDTO implements Serializable {
    private static final long serialVersionUID = -1602952120416950952L;
    private SqlExplainInfo sqlExplainInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
