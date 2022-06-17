package com.tangym.sqlservice.demo.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class DemoTable implements Serializable {

    private Integer id;

    private String user;

    private Integer age;

    private static final long serialVersionUID = 227162751497313268L;
}
