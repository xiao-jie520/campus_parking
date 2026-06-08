/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 90001 (9.0.1)
 Source Host           : localhost:3306
 Source Schema         : campus_parking

 Target Server Type    : MySQL
 Target Server Version : 90001 (9.0.1)
 File Encoding         : 65001

 Date: 02/06/2026 16:14:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access_record
-- ----------------------------
DROP TABLE IF EXISTS `access_record`;
CREATE TABLE `access_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint NULL DEFAULT NULL COMMENT 'vehicle id',
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'plate number',
  `access_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'IN or OUT',
  `spot_id` bigint NULL DEFAULT NULL COMMENT 'spot id',
  `spot_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'spot number',
  `area_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'area name',
  `access_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'access time',
  `operator_id` bigint NULL DEFAULT NULL COMMENT 'operator id',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'operator name',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'remark',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of access_record
-- ----------------------------
INSERT INTO `access_record` VALUES (1, 3, '冀DE5R64', 'IN', 25, 'A-001', 'A', '2026-05-09 21:52:12', 1, 'admin', '');
INSERT INTO `access_record` VALUES (2, 3, '冀DE5R64', 'OUT', 25, 'A-001', 'A', '2026-05-09 21:55:34', 1, 'admin', '');
INSERT INTO `access_record` VALUES (3, 3, '冀DE5R64', 'IN', 25, 'A-001', 'A', '2026-05-09 21:55:42', 1, 'admin', '');
INSERT INTO `access_record` VALUES (4, 4, '京A99999', 'IN', 26, 'A-002', 'A', '2026-05-09 21:56:58', 1, 'admin', '');
INSERT INTO `access_record` VALUES (5, 4, '京A99999', 'OUT', 26, 'A-002', 'A', '2026-05-09 22:02:28', 1, 'admin', '');
INSERT INTO `access_record` VALUES (6, 3, '冀DE5R64', 'OUT', 25, 'A-001', 'A', '2026-05-09 22:02:35', 1, 'admin', '');
INSERT INTO `access_record` VALUES (7, 4, '京A99999', 'IN', 25, 'A-001', 'A', '2026-05-09 22:03:38', 1, 'admin', '');
INSERT INTO `access_record` VALUES (8, 3, '冀DE5R64', 'IN', 26, 'A-002', 'A', '2026-05-09 22:03:48', 1, 'admin', '');
INSERT INTO `access_record` VALUES (9, 4, '京A99999', 'OUT', 25, 'A-001', 'A', '2026-05-19 11:40:37', 1, 'admin', '');
INSERT INTO `access_record` VALUES (10, 4, '京A99999', 'IN', 25, 'A-001', 'A', '2026-05-19 11:40:44', 1, 'admin', '');
INSERT INTO `access_record` VALUES (11, 3, '冀DE5R64', 'OUT', 26, 'A-002', 'A', '2026-05-31 18:44:26', 1, 'admin', '');
INSERT INTO `access_record` VALUES (12, 3, '冀DE5R64', 'IN', 26, 'A-002', 'A', '2026-05-31 18:44:35', 1, 'admin', '');
INSERT INTO `access_record` VALUES (13, 4, '京A99999', 'OUT', 25, 'A-001', 'A', '2026-06-01 09:58:42', 1, 'admin', '');
INSERT INTO `access_record` VALUES (14, 4, '京A99999', 'IN', 25, 'A-001', 'A', '2026-06-01 09:58:49', 1, 'admin', '');
INSERT INTO `access_record` VALUES (15, 3, '冀DE5R64', 'OUT', 26, 'A-002', 'A', '2026-06-02 15:54:44', 1, 'admin', '');
INSERT INTO `access_record` VALUES (16, 3, '冀DE5R64', 'OUT', 26, 'A-002', 'A', '2026-06-02 15:56:16', 1, 'admin', '');
INSERT INTO `access_record` VALUES (17, 3, '冀DE5R64', 'IN', 27, 'A-003', 'A', '2026-06-02 15:56:18', 1, 'admin', '');
INSERT INTO `access_record` VALUES (18, 3, '冀DE5R64', 'IN', 26, 'A-002', 'A', '2026-06-02 15:57:18', 1, 'admin', '');

-- ----------------------------
-- Table structure for parking_area
-- ----------------------------
DROP TABLE IF EXISTS `parking_area`;
CREATE TABLE `parking_area`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `total_spaces` int NOT NULL,
  `available_spaces` int NOT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_area
-- ----------------------------
INSERT INTO `parking_area` VALUES (4, 'A', 10, 8, 'jiaoxuelou', 1, '2026-05-09 13:36:09', '2026-06-02 15:57:16');
INSERT INTO `parking_area` VALUES (5, 'B', 8, 8, 'tushuguan', 1, '2026-05-09 13:36:09', '2026-05-09 13:36:09');
INSERT INTO `parking_area` VALUES (6, 'C', 6, 6, 'sushelou', 1, '2026-05-09 13:36:09', '2026-05-09 13:36:09');

-- ----------------------------
-- Table structure for parking_space
-- ----------------------------
DROP TABLE IF EXISTS `parking_space`;
CREATE TABLE `parking_space`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `zone_id` bigint NOT NULL COMMENT '所属区域ID',
  `space_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车位编号(如:A-001)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'FREE' COMMENT '状态: FREE(空闲), OCCUPIED(占用), FAULT(故障)',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_space_code`(`space_code` ASC) USING BTREE,
  INDEX `idx_zone_id`(`zone_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车位表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_space
-- ----------------------------

-- ----------------------------
-- Table structure for parking_spot
-- ----------------------------
DROP TABLE IF EXISTS `parking_spot`;
CREATE TABLE `parking_spot`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_id` bigint NOT NULL,
  `spot_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` int NULL DEFAULT 0,
  `vehicle_id` bigint NULL DEFAULT NULL,
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_spot
-- ----------------------------
INSERT INTO `parking_spot` VALUES (25, 4, 'A-001', 1, 4, '京A99999', '2026-05-09 13:36:32', '2026-06-01 09:58:48');
INSERT INTO `parking_spot` VALUES (26, 4, 'A-002', 1, 3, '冀DE5R64', '2026-05-09 13:36:32', '2026-06-02 15:56:03');
INSERT INTO `parking_spot` VALUES (27, 4, 'A-003', 1, 3, '冀DE5R64', '2026-05-09 13:36:32', '2026-06-02 15:56:16');
INSERT INTO `parking_spot` VALUES (28, 4, 'A-004', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (29, 4, 'A-005', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (30, 4, 'A-006', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (31, 4, 'A-007', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (32, 4, 'A-008', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (33, 4, 'A-009', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (34, 4, 'A-010', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:57');
INSERT INTO `parking_spot` VALUES (35, 5, 'B-001', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (36, 5, 'B-002', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (37, 5, 'B-003', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (38, 5, 'B-004', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (39, 5, 'B-005', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (40, 5, 'B-006', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (41, 5, 'B-007', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (42, 5, 'B-008', 0, NULL, NULL, '2026-05-09 13:36:32', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (43, 6, 'C-001', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (44, 6, 'C-002', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (45, 6, 'C-003', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (46, 6, 'C-004', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (47, 6, 'C-005', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');
INSERT INTO `parking_spot` VALUES (48, 6, 'C-006', 0, NULL, NULL, '2026-05-09 13:36:33', '2026-05-09 13:48:58');

-- ----------------------------
-- Table structure for parking_zone
-- ----------------------------
DROP TABLE IF EXISTS `parking_zone`;
CREATE TABLE `parking_zone`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `zone_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '区域名称(如:A区-教学楼)',
  `total_spaces` int NOT NULL DEFAULT 0 COMMENT '总车位数',
  `available_spaces` int NOT NULL DEFAULT 0 COMMENT '剩余车位数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 1启用, 0停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '停车区域表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of parking_zone
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码(加密存储)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN, GUARD, TEACHER, STUDENT',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$BNQFZXi8IsSDxCHA7JbLm.32rpo4PmhTGCCvfUkB98Co2ZVaCQD96', '管理员', 'ADMIN', NULL, 1, '2026-04-21 09:02:58', '2026-04-27 10:50:18');
INSERT INTO `sys_user` VALUES (2, 'guard', '$2a$10$eE/0crYnHMAsTlCKTpdFDeFOlIzogAQOMGyrPagsF6YJMFn0t0UnS', '保安', 'GUARD', '13800138000', 1, '2026-05-09 21:31:21', '2026-05-09 21:31:21');

-- ----------------------------
-- Table structure for vehicle_info
-- ----------------------------
DROP TABLE IF EXISTS `vehicle_info`;
CREATE TABLE `vehicle_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '绑定用户ID(临时车可为空)',
  `plate_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '车牌号',
  `vehicle_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'TEMPORARY' COMMENT '车辆类型: INTERNAL(内部), TEMPORARY(临时)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态: 1正常, 0黑名单',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `owner_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车主姓名',
  `owner_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车主手机号',
  `parking_status` int NULL DEFAULT 0 COMMENT '0未入场 1在场',
  `current_spot_id` bigint NULL DEFAULT NULL COMMENT '当前停放的车位ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_plate`(`plate_number` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '车辆信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vehicle_info
-- ----------------------------
INSERT INTO `vehicle_info` VALUES (3, NULL, '冀DE5R64', 'INTERNAL', 1, '2026-05-09 12:54:57', '张三', '13800138001', 1, 26);
INSERT INTO `vehicle_info` VALUES (4, NULL, '京A99999', 'INTERNAL', 1, '2026-05-09 13:56:38', '李四', '13800138002', 1, 25);

SET FOREIGN_KEY_CHECKS = 1;
