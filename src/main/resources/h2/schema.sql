CREATE TABLE `final_solution` (
  `final_solution_id` int(11) NOT NULL AUTO_INCREMENT,
  `question_id` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `total_dis` float NOT NULL,
  `user_choice` tinyint(1) not null default '0',
  `create_time` varchar(20) not null,
  PRIMARY KEY (`final_solution_id`)
);

CREATE TABLE `distance` (
  `distance_id` int(11) NOT NULL AUTO_INCREMENT,
  `start_node_id` int(11) NOT NULL,
  `end_node_id` int(11) NOT NULL,
  `start_node_addr` varchar(100) NOT NULL,
  `end_node_addr` varchar(100) NOT NULL,
  `dis` int(11) DEFAULT NULL,
  `time` int(11) DEFAULT NULL,
  `question_id` int(11) NOT NULL,
  PRIMARY KEY (`distance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `question` (
  `question_id` int(11) NOT NULL AUTO_INCREMENT,
  `question_name` varchar(50) NOT NULL,
  `user_id` int(11) NOT NULL,
  `del_flag` tinyint(4) DEFAULT '0',
  `process_state` tinyint default '0',
  `simple_executed` tinyint default '0',
  `genetic_executed` tinyint default '0',
  PRIMARY KEY (`question_id`),
  UNIQUE KEY `question_question_name_uindex` (`question_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `node` (
  `node_id` int(11) NOT NULL AUTO_INCREMENT,
  `question_id` int(11) NOT NULL,
  `node_name` varchar(20) NOT NULL,
  `node_address` varchar(50) NOT NULL,
  `lat` float DEFAULT NULL,
  `lng` float DEFAULT NULL,
  `is_center` tinyint(4) DEFAULT '0' COMMENT '0 表示不是中心点;1 表示是中心点',
  `del_flag` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`node_id`)
);

CREATE TABLE `solution` (
  `solution_id` int(11) NOT NULL AUTO_INCREMENT,
  `route` varchar(100) DEFAULT NULL,
  `total_dis` double DEFAULT NULL,
  `total_time` double DEFAULT NULL,
  `del_flag` tinyint(4) DEFAULT '0',
  `final_solution_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`solution_id`),
  KEY `time_index` (`total_time`),
  KEY `dis_index` (`total_dis`)
);

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_user_name_uindex` (`user_name`),
  UNIQUE KEY `user_password_uindex` (`password`),
  UNIQUE KEY `user_email_uindex` (`email`)
);

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
)

