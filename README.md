该项目为elastic-job动态增删任务以及动态带权重负载到不同服务运行的实现

技术栈:dubbo2.7.1+zookeeper3.4.13(注册中心)+mybatis+jdbc+elastic-job

使用

安装zookeeper

初始化sql:

CREATE DATABASE db_manage

CREATE TABLE db_manage.`t_job_config` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `job_code` varchar(64) NOT NULL COMMENT '作业名称',
  `job_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `business_source` varchar(32) NOT NULL COMMENT '业务来源(report_job,kettle_job...)',
  `cron` varchar(32) NOT NULL COMMENT 'cron表达式，用于控制作业触发时间',
  `shard_total_count` int(1) NOT NULL DEFAULT '3' COMMENT '作业分片总数',
  `sharding_item_parameters` varchar(255) DEFAULT '0=0,1=1,2=2' COMMENT '分片序列,列号从0开始,不可大于或等于作业分片总数.如：0=a,1=b,2=c',
  `job_parameter` varchar(255) DEFAULT NULL COMMENT '作业扩展参数(kettle_job额外参数不能为空 格式如:ktrFile=/home/kettle/ktr;jobDirection=1)',
  `description` varchar(255) DEFAULT NULL COMMENT '作业描述信息',
  `extra_parameter` varchar(255) DEFAULT NULL COMMENT '扩展参数',
  `enable` int(1) NOT NULL DEFAULT '1' COMMENT '是否启用 1-启用 0-不启用',
  `profiles` varchar(15) NOT NULL COMMENT '当前运行环境:dev test prod',
  `delete_flag` int(1) NOT NULL DEFAULT '0' COMMENT '软删除标识 1-已删除不可用 0-未删除可用 默认0',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `CREATE_OPER` varchar(32) DEFAULT NULL COMMENT '创建人',
  `UPDATE_OPER` varchar(32) DEFAULT NULL COMMENT '修改人',
  `job_cycle` tinyint(4) DEFAULT NULL COMMENT '任务运行周期：1-秒，2-日，3-周，4-月，5-季度，6-年',
  `job_period` int(11) DEFAULT NULL COMMENT '任务运行间隔，配合任务运行周期使用',
  `job_can_subscribe` tinyint(4) DEFAULT NULL COMMENT '任务是否可订阅。0-不可订阅1-可订阅',
  `job_org_no` varchar(255) DEFAULT NULL COMMENT '任务归属权限机构',
  `job_dependences` varchar(255) DEFAULT NULL COMMENT '任务的依赖列表，存储结构：kettle_job:syncPaasAgencyHierarchyJob,report_job:agentDevelopDetailPushTask；关键字：ALL表示依赖所有任务',
  `job_first_date` varchar(8) DEFAULT NULL COMMENT '任务首次执行日期，默认为当前日期的前一天yyyyMMdd，若任务是月任务，存储yyyyMM，若任务是年任务，存储yyyy',
  `job_first_time` varchar(6) DEFAULT NULL COMMENT '任务首次执行时间，默认为对应日期的0点HHmmss',
  PRIMARY KEY (`id`),
  UNIQUE KEY `job_code` (`job_code`,`business_source`,`profiles`) USING BTREE,
  KEY `UPDATE_TIME` (`UPDATE_TIME`) USING BTREE,
  KEY `business_source` (`business_source`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='作业配置信息表';

(后面一部分字段为业务字段)

INSERT INTO `db_manage`.`t_job_config`(`id`, `job_code`, `job_name`, `business_source`, `cron`, `shard_total_count`, `sharding_item_parameters`, `job_parameter`, `description`, `extra_parameter`, `enable`, `profiles`, `delete_flag`, `CREATE_TIME`, `UPDATE_TIME`, `CREATE_OPER`, `UPDATE_OPER`, `job_cycle`, `job_period`, `job_can_subscribe`, `job_org_no`, `job_dependences`, `job_first_date`, `job_first_time`) VALUES (1, 'pos_bigdata', 'bigdata测试', 'elastic_job', '0 0 0/10 * * ?', 3, '0=0,1=1,2=2', 'ktrFile=/home/app/nfsdata/ktr/pos/xxx.ktr;kettleLogLevel=Basic', 'bigdata测试', '', 1, 'dev', 0, '2021-07-14 15:48:44', '2021-09-22 09:59:58', '1000030002', '1000030002', 2, 1, 1, '', '', '20210708', '000000');

启动3个服务 访问 http://localhost:28080/job/config/index 通过web页面对elastic-job任务进行动态增删改查 

观察elastic-job-core服务的任务执行