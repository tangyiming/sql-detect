CREATE DATABASE `sql_detect` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */

create table demo_table
(
    id int auto_increment
        primary key,
    user varchar(32) null,
    age int null
);

create table quartz
(
    id int auto_increment comment 'ID',
    job_name varchar(32) not null comment '任务名称',
    job_class varchar(64) not null comment '任务执行类',
    cron_expression varchar(64) not null comment '任务运行时间表达式',
    run_after_start tinyint(1) default 0 not null comment '是否在应用启动后运行 1是 0否',
    constraint quartz_id_uindex
        unique (id)
)
    comment '定时任务' charset=utf8mb4;

alter table quartz
    add primary key (id);

create table sql_app_configs
(
    id int auto_increment,
    service varchar(32) not null comment '应用名',
    explain_switch tinyint(1) default 1 null comment '应用级别explain检查开关 1开启 0关闭',
    users json null comment '应用负责人列表，通知到SQL分析群里哪些人',
    ding_token varchar(70) null comment '钉群机器人token',
    constraint sql_app_configs_id_uindex
        unique (id),
    constraint sql_app_configs_service_uindex
        unique (service)
)
    comment '慢查应用通知配置信息' charset=utf8mb4;

alter table sql_app_configs
    add primary key (id);

create table sql_explain_config_rules
(
    id int auto_increment comment '主键ID',
    rule_name varchar(32) null comment '规则名称',
    rule_detail varchar(100) not null comment '规则内容',
    rule_key varchar(16) null comment '唯一键，识别规则所属',
    constraint sql_explain_config_rules_id_uindex
        unique (id),
    constraint sql_explain_config_rules_name_uindex
        unique (rule_name),
    constraint sql_explain_config_rules_rule_key_uindex
        unique (rule_key)
)
    comment '慢sql动态规则配置' charset=utf8mb4;

alter table sql_explain_config_rules
    add primary key (id);

create table sql_explain_config_rules_script
(
    id int auto_increment,
    script varchar(256) null comment '规则脚本',
    remark varchar(256) null comment '规则备注信息',
    constraint sql_explain_config_rules_script_id_uindex
        unique (id)
)
    comment '慢查忽略规则表' charset=utf8mb4;

alter table sql_explain_config_rules_script
    add primary key (id);

create table sql_explain_info
(
    id int auto_increment,
    service_name varchar(32) null comment '服务名称/应用名称',
    original_sql text null comment '原始不填参数的sql语句',
    parameterized_sql text null comment '最终可以执行的参数化后的sql',
    server_version int null comment 'mysql server 版本 5 or 8',
    explain_res json null comment 'explain结果',
    is_slow tinyint(1) null comment '是否是慢查sql',
    is_prod_fault tinyint(1) null comment '线上是否出慢查故障',
    db_host varchar(64) null comment 'host',
    db_port varchar(4) null comment '端口号',
    db_name varchar(64) null comment '数据库名',
    db_user varchar(32) null comment '用户名',
    db_pwd varchar(16) null comment '密码',
    is_failed int default 0 null comment '执行explain是否失败 0否 1是',
    is_alert tinyint(1) null comment '是否已告警',
    remark varchar(100) null comment '备注信息',
    create_time timestamp null comment '创建时间',
    update_time timestamp null comment '更新时间',
    tb_fingerprints json null comment '关联hash签名表id列表',
    original_sql_hash varchar(32) null comment '原始语句的sql hash值',
    sql_id varchar(256) null comment 'sql mybatis id 全路径',
    constraint sql_explain_info_id_uindex
        unique (id)
)
    comment '慢查sql信息表' charset=utf8mb4;

create index sql_explain_info_is_alert_index
    on sql_explain_info (is_alert);

create index sql_explain_info_is_failed_index
    on sql_explain_info (is_failed);

create index sql_explain_info_is_slow_index
    on sql_explain_info (is_slow);

create index sql_explain_info_original_sql_hash_index
    on sql_explain_info (original_sql_hash);

create index sql_explain_info_service_name_index
    on sql_explain_info (service_name);

alter table sql_explain_info
    add primary key (id);

create table sql_explain_stars
(
    id int auto_increment comment '主键ID',
    job_number varchar(8) not null comment '工号',
    service_name varchar(32) not null comment '关注应用',
    constraint sql_explain_stars_id_uindex
        unique (id)
)
    comment '关注应用信息表' charset=utf8mb4;

alter table sql_explain_stars
    add primary key (id);

create table sql_explain_statistics
(
    id int auto_increment,
    service_name varchar(32) null comment '应用名',
    health varchar(16) null comment '健康度',
    slow_percent varchar(8) null comment '慢查占比',
    latest_slow_in_seven int null comment '近七日新增慢查数',
    slow_total int null comment '慢查总数',
    explain_total int null comment '分析总数',
    calc_time timestamp null comment '汇总时间点，以后从此时间点进行数据统计，做累加',
    constraint sql_explain_statistics_id_uindex
        unique (id),
    constraint sql_explain_statistics_service_name_uindex
        unique (service_name)
)
    comment '汇总数据表' charset=utf8mb4;

alter table sql_explain_statistics
    add primary key (id);

create table sql_explain_table_fingerprint
(
    id int auto_increment,
    db_name varchar(64) not null comment '数据库名',
    db_host varchar(64) null comment 'host',
    db_port varchar(4) null comment 'port',
    db_user varchar(32) null comment '用户名',
    db_pwd varchar(16) null comment '密码',
    tb_name varchar(64) not null comment '表名',
    fingerprint varchar(32) null comment 'md5 hash值',
    create_time timestamp null comment '创建时间',
    update_time timestamp null comment '更新时间',
    constraint sql_explain_table_fingerprint_id_uindex
        unique (id)
)
    comment '数据表指纹' charset=utf8mb4;

create index sql_explain_table_fingerprint_db_name_index
    on sql_explain_table_fingerprint (db_name);

create index sql_explain_table_fingerprint_tb_name_index
    on sql_explain_table_fingerprint (tb_name);

alter table sql_explain_table_fingerprint
    add primary key (id);

create table sql_process_log
(
    id int auto_increment,
    explain_info_id int null comment '记录关联sql数据id',
    user_id varchar(8) null comment '操作人工号',
    user_name varchar(8) null comment '操作人姓名',
    type varchar(16) null comment '操作类型',
    remark varchar(100) null comment '备注',
    create_time timestamp null comment '记录创建时间',
    update_time timestamp null comment '记录修改时间',
    constraint sql_process_log_id_uindex
        unique (id)
)
    comment '慢查sql数据操作记录' charset=utf8mb4;

create index sql_process_log_explain_info_id_index
    on sql_process_log (explain_info_id);

alter table sql_process_log
    add primary key (id);

