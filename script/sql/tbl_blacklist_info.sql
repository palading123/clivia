SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_blacklist_info
-- ----------------------------
DROP TABLE IF EXISTS `tbl_blacklist_info`;
CREATE TABLE `tbl_blacklist_info` (
  `blacklist_id` varchar(50) NOT NULL COMMENT '主键',
  `blacklist_ip` varchar(50) DEFAULT NULL COMMENT '拦截的IP地址(支持全ip段和匹配IP)',
  `state` varchar(1) DEFAULT NULL COMMENT '状态(0=正常,1=已删除)',
  `group_name` varchar(50) DEFAULT NULL COMMENT '所属组',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `ext1` varchar(255) DEFAULT NULL COMMENT '扩展字段',
  PRIMARY KEY (`blacklist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_blacklist_info
-- ----------------------------
