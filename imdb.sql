/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50117
Source Host           : localhost:3306
Source Database       : imdb

Target Server Type    : MYSQL
Target Server Version : 50117
File Encoding         : 65001

Date: 2015-10-29 16:53:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `alias_device`
-- ----------------------------
DROP TABLE IF EXISTS `alias_device`;
CREATE TABLE `alias_device` (
  `device_id` varchar(40) NOT NULL COMMENT '主键-设备ID',
  `alias_name` varchar(50) NOT NULL COMMENT '设备别名-每个设备只能设置一个且唯一',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `online` varchar(1) NOT NULL COMMENT '在线状态-0离线 1在线',
  `platform` varchar(10) NOT NULL COMMENT '客户端运行平台-android/ios',
  PRIMARY KEY (`device_id`,`alias_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of alias_device
-- ----------------------------
INSERT INTO `alias_device` VALUES ('ffffffff-f2de-4719-ffff-ffffba5a20ce', 'y', 'y', '0', 'android');
INSERT INTO `alias_device` VALUES ('ffffffff-f2de-4719-ffff-ffffba5a20ce', 'z', 'z', '0', 'android');

-- ----------------------------
-- Table structure for `friend_relation`
-- ----------------------------
DROP TABLE IF EXISTS `friend_relation`;
CREATE TABLE `friend_relation` (
  `uuid` varchar(32) NOT NULL COMMENT '主键',
  `my_device_id` varchar(40) NOT NULL COMMENT '我的设备ID',
  `friend_device_id` varchar(40) NOT NULL COMMENT '好友设备ID',
  PRIMARY KEY (`uuid`),
  KEY `fk_my_device_id` (`my_device_id`),
  KEY `fk_friend_device_id` (`friend_device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='好友关系表';

-- ----------------------------
-- Records of friend_relation
-- ----------------------------

-- ----------------------------
-- Table structure for `im_message`
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message` (
  `msg_id` varchar(32) NOT NULL DEFAULT '' COMMENT '主键-消息ID',
  `send_device_id` varchar(40) NOT NULL COMMENT '发送设备ID',
  `recv_device_id` varchar(40) NOT NULL COMMENT '接收设备ID',
  `text_msg` text COMMENT '文本内容',
  `file_url` varchar(100) DEFAULT NULL COMMENT '音频文件URL',
  `duration` float(15,1) DEFAULT NULL COMMENT '音频时长',
  `time` bigint(20) NOT NULL COMMENT '消息发送时间-毫秒',
  `msg_type` varchar(10) NOT NULL COMMENT '消息类型-text/audio',
  `msg_status` varchar(1) NOT NULL COMMENT '消息状态-0未发送 1已发送',
  `send_alias_name` varchar(50) NOT NULL COMMENT '发送者别名-每个设备只能设置一个且唯一',
  `recv_alias_name` varchar(50) NOT NULL COMMENT '接收者别名-每个设备只能设置一个且唯一',
  PRIMARY KEY (`msg_id`),
  KEY `fk_recv_device_id` (`recv_device_id`),
  KEY `fk_send_device_id` (`send_device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息表';

-- ----------------------------
-- Records of im_message
-- ----------------------------
INSERT INTO `im_message` VALUES ('36867e52f3c54ed1b59352f96454dcca', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ip', null, null, '1446097635648', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('3bb18fa0d192407d858d3c4fd8f74a80', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '2', null, null, '1446104124192', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('6a326ab32eb642a6bdf73c769ad860b9', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ok', null, null, '1446099745806', 'text', '1', 'y', 'z');
INSERT INTO `im_message` VALUES ('6ce01a0e6ac842fea3b1c1953a73c37a', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'hi', null, null, '1446103048449', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('7b86f0da7d35458a90d0a900c3934c90', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '下班了吗？', null, null, '1446105680143', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('7e9dfa8d1151436bb782e23901451c3d', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'hello', null, null, '1446102687307', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('b94f3e3ff5aa43738d3979ff3a4bbf23', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'cs', null, null, '1446097626444', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('cfe76d0b0ea74d78a85742feeae59188', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '您好', null, null, '1446105671997', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('e7cfd2a8a1704711a0c5b9437bdff93a', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'hello', null, null, '1446103067661', 'text', '1', 'z', 'y');
INSERT INTO `im_message` VALUES ('ff7292232bfb42588ff801da68d583a8', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'hi', null, null, '1446104120413', 'text', '1', 'z', 'y');

-- ----------------------------
-- Table structure for `push_message`
-- ----------------------------
DROP TABLE IF EXISTS `push_message`;
CREATE TABLE `push_message` (
  `msg_id` varchar(32) NOT NULL COMMENT '主键-消息ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `msg_content` varchar(1000) DEFAULT NULL COMMENT '内容',
  `extras` varchar(100) DEFAULT NULL COMMENT '附加字段json格式',
  `recv_device_id` varchar(40) NOT NULL COMMENT '接收设备ID',
  `msg_status` varchar(1) NOT NULL COMMENT '消息状态-0未发送 1已发送',
  `time` bigint(20) NOT NULL COMMENT '消息发送时间-毫秒',
  `time_to_live` bigint(20) NOT NULL COMMENT '离线消息保存时间-毫秒',
  `tag` varchar(20) DEFAULT NULL COMMENT '标签',
  PRIMARY KEY (`msg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of push_message
-- ----------------------------
INSERT INTO `push_message` VALUES ('0152e19579904d529a373a4c835635f3', '标题', '测试', '{\"\":\"\"}', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '1', '1446090385855', '172800000', 'z');
INSERT INTO `push_message` VALUES ('32ba0de49ea3432287e2fcae48d206a3', '标题', '测试', '{\"\":\"\"}', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '1', '1446090383088', '172800000', 'y');
INSERT INTO `push_message` VALUES ('6973fc9093be4df19c0369b60c911f59', '标题', '测试', '{\"\":\"\"}', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '1', '1446090545226', '172800000', 'teacher');
INSERT INTO `push_message` VALUES ('878879b57999454193ef3d4ffa910748', '标题', '测试', '{\"\":\"\"}', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', '1', '1446094165716', '172800000', 'z');

-- ----------------------------
-- Table structure for `tag_device`
-- ----------------------------
DROP TABLE IF EXISTS `tag_device`;
CREATE TABLE `tag_device` (
  `uuid` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `tag` varchar(20) NOT NULL COMMENT '标签',
  `device_id` varchar(40) NOT NULL COMMENT '设备唯一码',
  `user_name` varchar(30) NOT NULL COMMENT '用户名',
  `online` varchar(1) NOT NULL DEFAULT '0' COMMENT '在线状态-1在线 0离线',
  `platform` varchar(10) NOT NULL COMMENT '客户端运行平台 -android/ios',
  PRIMARY KEY (`uuid`),
  KEY `fk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标签设备对照表';

-- ----------------------------
-- Records of tag_device
-- ----------------------------
INSERT INTO `tag_device` VALUES ('4c0c688c7eda457e93d37bfecdc8965b', 'teacher', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'z', '0', 'android');
INSERT INTO `tag_device` VALUES ('e48bbb09a9a64b1e96f5157686d31236', 'y', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'y', '0', 'android');
INSERT INTO `tag_device` VALUES ('f3a6b94fd3e049cab164d9e79031cadf', 'z', 'ffffffff-f2de-4719-ffff-ffffba5a20ce', 'z', '0', 'android');
