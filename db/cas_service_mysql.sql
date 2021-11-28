/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : localhost:3306
 Source Schema         : cloud

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 29/08/2021 17:10:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for regexregisteredservice
-- ----------------------------
DROP TABLE IF EXISTS `regexregisteredservice`;
CREATE TABLE `regexregisteredservice`  (
  `expression_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'regex',
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `access_strategy` longblob NULL,
  `attribute_release` longblob NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `evaluation_order` int(0) NOT NULL,
  `expiration_policy` longblob NULL,
  `informationUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `logo` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `logout_type` int(0) NULL DEFAULT NULL,
  `logout_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `mfa_policy` longblob NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `privacyUrl` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `proxy_policy` longblob NULL,
  `public_key` longblob NULL,
  `required_handlers` longblob NULL,
  `responseType` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `serviceId` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `theme` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `username_attr` longblob NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of regexregisteredservice
-- ----------------------------

-- ----------------------------
-- Table structure for regexregisteredserviceproperty
-- ----------------------------
DROP TABLE IF EXISTS `regexregisteredserviceproperty`;
CREATE TABLE `regexregisteredserviceproperty`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `property_values` longblob NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of regexregisteredserviceproperty
-- ----------------------------

-- ----------------------------
-- Table structure for registeredservice_contacts
-- ----------------------------
DROP TABLE IF EXISTS `registeredservice_contacts`;
CREATE TABLE `registeredservice_contacts`  (
  `AbstractRegisteredService_id` bigint(0) NOT NULL,
  `contacts_id` bigint(0) NOT NULL,
  `contacts_ORDER` int(0) NOT NULL,
  PRIMARY KEY (`AbstractRegisteredService_id`, `contacts_ORDER`) USING BTREE,
  UNIQUE INDEX `UK_s7mf4a23wejqx62tt4vh3tgwi`(`contacts_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of registeredservice_contacts
-- ----------------------------

-- ----------------------------
-- Table structure for registeredserviceimpl_props
-- ----------------------------
DROP TABLE IF EXISTS `registeredserviceimpl_props`;
CREATE TABLE `registeredserviceimpl_props`  (
  `AbstractRegisteredService_id` bigint(0) NOT NULL,
  `properties_id` bigint(0) NOT NULL,
  `properties_KEY` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`AbstractRegisteredService_id`, `properties_KEY`) USING BTREE,
  UNIQUE INDEX `UK_i2mjaqjwxpvurc6aefjkx5x97`(`properties_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of registeredserviceimpl_props
-- ----------------------------

-- ----------------------------
-- Table structure for registeredserviceimplcontact
-- ----------------------------
DROP TABLE IF EXISTS `registeredserviceimplcontact`;
CREATE TABLE `registeredserviceimplcontact`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `department` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of registeredserviceimplcontact
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
