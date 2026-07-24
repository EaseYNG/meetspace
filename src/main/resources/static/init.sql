-- 创建并使用数据库
CREATE DATABASE IF NOT EXISTS `meetspace` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `meetspace`;

-- 设置字符集
/*!40101 SET NAMES utf8mb4 */;
SET FOREIGN_KEY_CHECKS = 0;

-- 删除已存在的表（注意顺序，先删除有外键的表）
DROP TABLE IF EXISTS `activity_participant`;
DROP TABLE IF EXISTS `activity`;
DROP TABLE IF EXISTS `users`;

-- 创建用户表
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `nickname` VARCHAR(255) DEFAULT NULL COMMENT '昵称',
  `username` VARCHAR(255) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `gender` VARCHAR(50) DEFAULT NULL COMMENT '性别',
  `email` VARCHAR(255) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(64) DEFAULT NULL COMMENT '电话',
  `firstname` VARCHAR(255) DEFAULT NULL COMMENT '名',
  `lastname` VARCHAR(255) DEFAULT NULL COMMENT '姓',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建活动表
CREATE TABLE `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` VARCHAR(255) NOT NULL COMMENT '活动标题',
  `description` TEXT COMMENT '活动描述',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '活动地址',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '活动图片',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `signup_deadline` DATETIME NOT NULL COMMENT '报名截止时间',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '活动状态：READY/CLOSED/DELETED/OVER',
  `owner_id` BIGINT NOT NULL COMMENT '创建者ID',
  `min_participants` INT DEFAULT NULL COMMENT '最小参与人数',
  `max_participants` INT DEFAULT NULL COMMENT '最大参与人数',
  `latitude` DOUBLE DEFAULT NULL COMMENT '纬度',
  `longitude` DOUBLE DEFAULT NULL COMMENT '经度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 创建活动参与表
CREATE TABLE `activity_participant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '参与记录ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `participant_id` BIGINT NOT NULL COMMENT '参与者ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_participant` (`activity_id`, `participant_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_participant_id` (`participant_id`),
  CONSTRAINT `fk_activity_participant_activity` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_activity_participant_user` FOREIGN KEY (`participant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动参与表';

SET FOREIGN_KEY_CHECKS = 1;
