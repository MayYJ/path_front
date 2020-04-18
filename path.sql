/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.7.21-log : Database - path
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`path` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `path`;

/*Table structure for table `demo_node` */

DROP TABLE IF EXISTS `demo_node`;

/*使用方法教学体验表*/
CREATE TABLE `demo_node` (
  `pos_id` int(8) NOT NULL COMMENT '点ID',
  `pos_x` double NOT NULL COMMENT '点X轴坐标',
  `pos_y` double NOT NULL COMMENT '点Y轴坐标',
  `is_center` int(1) DEFAULT '0' COMMENT '是否中心点',
  `user_id` varchar(11) DEFAULT NULL COMMENT '用户ID',
  `question_id` int(5) NOT NULL COMMENT '问题ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `distance` */

DROP TABLE IF EXISTS `distance`;

/*路程表*/
CREATE TABLE `distance` (
  `distance_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '距离ID',
  `start_node_id` int(11) NOT NULL COMMENT '起始点ID',
  `end_node_id` int(11) NOT NULL COMMENT '终点ID',
  `start_node_addr` varchar(100) NOT NULL COMMENT '起点地址',
  `end_node_addr` varchar(100) NOT NULL COMMENT '终点地址',
  `dis` int(11) DEFAULT NULL COMMENT '距离',
  `time` int(11) DEFAULT NULL COMMENT '时间',
  `question_id` int(11) NOT NULL COMMENT '问题ID',
  PRIMARY KEY (`distance_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `final_solution` */

DROP TABLE IF EXISTS `final_solution`;

/*最终解决方案表*/
CREATE TABLE `final_solution` (
  `final_solution_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '解决方案ID',
  `question_id` int(11) NOT NULL COMMENT '问题ID',
  `version` int(11) NOT NULL COMMENT '版本号',
  `total_dis` float NOT NULL COMMENT '总距离',
  `user_choice` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户选择的算法',
  `create_time` varchar(20) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`final_solution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `node` */

DROP TABLE IF EXISTS `node`;

/*服务点表*/
CREATE TABLE `node` (
  `node_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '点ID',
  `question_id` int(11) NOT NULL COMMENT '问题ID',
  `node_name` varchar(20) NOT NULL COMMENT '点名称',
  `node_address` varchar(50) NOT NULL COMMENT '点地址',
  `lat` float DEFAULT NULL COMMENT '经度',
  `lng` float DEFAULT NULL COMMENT '纬度',
  `is_center` tinyint(4) DEFAULT '0' COMMENT '0 表示不是中心点;1 表示是中心点',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标记（0：未删除；1：已删除）',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `question` */

DROP TABLE IF EXISTS `question`;

/*问题表（运算一次称为一个问题）*/
CREATE TABLE `question` (
  `question_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '问题ID',
  `question_name` varchar(50) NOT NULL COMMENT '问题名称',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标记',
  `process_state` tinyint(4) DEFAULT '0' COMMENT '当前状态（算到哪一步了）',
  `simple_executed` tinyint(4) DEFAULT '0' COMMENT '简单算法结果',
  `genetic_executed` tinyint(4) DEFAULT '0' COMMENT '遗传算法结果',
  PRIMARY KEY (`question_id`),
  UNIQUE KEY `question_question_name_uindex` (`question_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Table structure for table `solution` */

DROP TABLE IF EXISTS `solution`;

/*解决方案表*/
CREATE TABLE `solution` (
  `solution_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '方案ID',
  `route` varchar(100) DEFAULT NULL COMMENT '路径',
  `total_dis` double DEFAULT NULL COMMENT '总距离',
  `total_time` double DEFAULT NULL COMMENT '总时间',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '删除标记',
  `final_solution_id` int(11) DEFAULT NULL COMMENT '最终方案ID',
  PRIMARY KEY (`solution_id`),
  KEY `time_index` (`total_time`),
  KEY `dis_index` (`total_dis`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

/*用户表*/
CREATE TABLE `user` (
  `user_id` varchar(11) NOT NULL COMMENT '用户ID',
  `user_name` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '登陆密码',
  `email` varchar(30) DEFAULT NULL COMMENT '电子邮箱',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_user_name_uindex` (`user_name`),
  UNIQUE KEY `user_password_uindex` (`password`),
  UNIQUE KEY `user_email_uindex` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `vehicle` */

DROP TABLE IF EXISTS `vehicle`;

/*车辆信息表*/
CREATE TABLE `vehicle` (
  `vehicle_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '车辆编号',
  `question_id` int(11) NOT NULL COMMENT '方案编号',
  `type` varchar(10) DEFAULT NULL COMMENT '车辆类型',
  `capacity` float DEFAULT NULL COMMENT '车辆容量',
  `oil` float DEFAULT NULL COMMENT '排量',
  `date` datetime DEFAULT NULL COMMENT '订单生成日期',
  `price` float DEFAULT NULL COMMENT '价格',
  `owner_id` int(11) NOT NULL COMMENT '拥有者',
  PRIMARY KEY (`vehicle_id`),
  KEY `OWNER_INDEX` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
