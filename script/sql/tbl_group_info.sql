SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_group_info
-- ----------------------------
DROP TABLE IF EXISTS `tbl_group_info`;
CREATE TABLE `tbl_group_info` (
  `group_id` varchar(50) NOT NULL COMMENT '主键',
  `group_name` varchar(50) DEFAULT NULL COMMENT '组名',
  `group_desc` varchar(255) DEFAULT NULL COMMENT '组描述',
  `state` varchar(1) DEFAULT '0' COMMENT '状态(0=正常，1=已删除)',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `index_group` (`group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_group_info
-- ----------------------------
INSERT INTO `tbl_group_info` VALUES ('131066a0-c413-11eb-b019-00ffa820f179', 'clivia测试组', '测试', '0', 'admin', '2021-12-11 10:26:09', 'admin', '2021-12-11 10:26:09');
