

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_api_info
-- ----------------------------
DROP TABLE IF EXISTS `tbl_api_info`;
CREATE TABLE `tbl_api_info` (
  `api_id` varchar(50) NOT NULL COMMENT '主键',
  `api_name` varchar(50) DEFAULT NULL,
  `app_id` varchar(50) DEFAULT NULL COMMENT 'app',
  `group_id` varchar(50) DEFAULT NULL COMMENT '所属组(group_info表group_name)',
  `rpc_type` varchar(20) DEFAULT '' COMMENT '调用服务类型',
  `version` varchar(50) DEFAULT NULL COMMENT '版本号',
  `api_type` varchar(1) DEFAULT NULL COMMENT 'api类型(0=应用,1=接口)',
  `api_enabled` varchar(10) DEFAULT '0' COMMENT '是否开启(0=开启,1=关闭)',
  `anonymous` varchar(1) DEFAULT '0' COMMENT '匿名访问(0=非匿名，1=匿名；当为0时则需要进行appKey校验)',
  `api_header` varchar(500) DEFAULT NULL COMMENT '请求头',
  `api_rewrite` varchar(500) DEFAULT NULL COMMENT '重写',
  `url` varchar(100) DEFAULT NULL COMMENT '请求路径',
  `method_type` varchar(15) DEFAULT '' COMMENT '请求方法类型',
  `api_req_size` varchar(200) DEFAULT NULL COMMENT '请求大小',
  `black_list_enabled` varchar(10) DEFAULT NULL COMMENT '黑名单控制',
  `api_non_http_route` text COMMENT '非http请求属性',
  `api_hystrix` varchar(200) DEFAULT NULL COMMENT '熔断器属性',
  `api_request_limit` varchar(255) DEFAULT NULL COMMENT '限流属性',
  `api_http_route` varchar(1000) DEFAULT NULL COMMENT 'http/springcloud属性',
  `api_auth` varchar(255) DEFAULT NULL COMMENT '验签属性',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`api_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_api_info
-- ----------------------------
INSERT INTO `tbl_api_info` VALUES ('ae819db85c174784a9c0673e5746378b', '获取用户年龄-test', '1', '1', 'http', 'V0.0.1', '1', '0', '0', '{\"enabled\":true,\"addHeader\":\"TEST-HEADER=CLIVIA-TEST,TEST-HEADER2=CLIVIA-TEST2,TEST-HEADER3=CLIVIA-TEST3\",\"removeHeader\":\"TEST-HEADER2,TEST-HEADER3\"}', '{\"enabled\":true,\"rewritePath\":\"/admin/server/test\"}', '/api/test', 'post', '{\"enabled\":true,\"maxSize\":500}', 'true', null, null, '{\"enabled\":true,\"replenishRate\":500,\"burstCapacity\":10000}', '{\"loadbalanceRouters\":[{\"upstreamUrl\":\"http://localhost:1023\",\"upstreamWeight\":1,\"enabled\":true,\"timestamp\":1625715063979,\"warmup\":1},{\"upstreamUrl\":\"http://localhost:1023\",\"upstreamWeight\":3,\"enabled\":true,\"timestamp\":1625715063979,\"warmup\":2}],\"loadbalanceType\":\"roundRobin\",\"clientIp\":null,\"retryTimes\":1,\"timeOutMillis\":0,\"serviceId\":null}', null, 'admin', '2021-07-29 15:23:15', null, '2021-07-29 15:23:15');
