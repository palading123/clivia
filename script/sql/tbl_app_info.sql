SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_app_info
-- ----------------------------
DROP TABLE IF EXISTS `tbl_app_info`;
CREATE TABLE `tbl_app_info` (
  `app_id` varchar(50) NOT NULL COMMENT '主键',
  `app_name` varchar(100) DEFAULT NULL COMMENT 'app名称',
  `app_key` varchar(100) DEFAULT NULL COMMENT 'app密钥',
  `group_id` varchar(50) DEFAULT NULL COMMENT 'group',
  `app_desc` varchar(255) DEFAULT NULL COMMENT 'app描述',
  `state` varchar(1) DEFAULT NULL COMMENT 'app状态(0=正常，1=删除)',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_app_info
-- ----------------------------
INSERT INTO `tbl_app_info` VALUES ('1', 'test', 'edfg7755ffas', '1', 'test描述', '0', null, null, null, null);
