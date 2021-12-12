SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_client_security
-- ----------------------------
DROP TABLE IF EXISTS `tbl_client_security`;
CREATE TABLE `tbl_client_security` (
  `id` varchar(50) NOT NULL COMMENT '主键',
  `client_name` varchar(100) NOT NULL COMMENT '客户端地址',
  `token` varchar(255) NOT NULL COMMENT '令牌',
  `client_pwd` text NOT NULL COMMENT '密码',
  `state` varchar(1) DEFAULT NULL COMMENT '状态(0=正常,1=删除)',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_token_state` (`token`,`state`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_client_security
-- ----------------------------
INSERT INTO `tbl_client_security` VALUES ('1', 'clivia_node1', '5d8632fb725f18e1c8a6a58463cceb1a', 'f4ba091a5f702d62304720dc757b6782', '0', '', '2021-12-11 15:33:26', '', '2021-12-11 15:33:26');
INSERT INTO `tbl_client_security` VALUES ('2', 'clivia_mode2', 'asdasd2213123mlasd1231231132311', 'f4ba091a5f702d62304720dc757b6782', '0', null, '2021-12-11 14:41:10', null, '2021-12-11 14:41:10');
