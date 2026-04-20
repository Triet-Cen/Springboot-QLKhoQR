-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th4 18, 2026 lúc 02:28 PM
-- Phiên bản máy phục vụ: 9.1.0
-- Phiên bản PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `quan_ly_kho`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `companies`
--

DROP TABLE IF EXISTS `companies`;
CREATE TABLE IF NOT EXISTS `companies` (
  `company_id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tax_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `customers`
--

DROP TABLE IF EXISTS `customers`;
CREATE TABLE IF NOT EXISTS `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_code` (`customer_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `customers`
--

INSERT INTO `customers` (`customer_id`, `customer_code`, `customer_name`, `contact_person`, `phone`, `email`, `address`) VALUES
(1, 'KH001', 'Công ty Công nghệ Việt Nam', NULL, '0901234567', NULL, 'Số 1 Đống Đa, Hà Nội'),
(2, 'KH003', 'Công ty Công nghệ Việt Nam', NULL, '0901234567', NULL, 'Số 3 Bình Thới, Cao Bằng'),
(3, 'CUS001', 'Cong ty TNHH Ban Le Sao Mai', 'Nguyen Minh Tuan', '0908111222', 'tuan.nguyen@saomai.vn', '15 Nguyen Van Linh, Quan 7, TP.HCM'),
(4, 'CUS002', 'Cong ty CP Thuong Mai Hoang Gia', 'Tran Thi Hoa', '0919222333', 'hoa.tran@hoanggia.vn', '120 Le Duan, Hai Chau, Da Nang'),
(5, 'CUS003', 'Cua hang Thiet bi An Phat', 'Le Quoc Bao', '0927333444', 'bao.le@anphat.vn', '34 Hung Vuong, Ninh Kieu, Can Tho'),
(6, 'CUS004', 'Sieu thi Mini Minh Chau', 'Pham Ngoc Anh', '0936444555', 'anh.pham@minhchau.vn', '8 Vo Thi Sau, Bien Hoa, Dong Nai'),
(7, 'CUS005', 'Cong ty TNHH Ky Thuat Viet Tin', 'Do Thanh Son', '0945555666', 'son.do@viettin.vn', '56 Tran Phu, Ha Dong, Ha Noi'),
(8, 'CUS006', 'Nha phan phoi Phuong Nam', 'Bui Thi Lan', '0954666777', 'lan.bui@phuongnam.vn', '91 Cach Mang Thang 8, Nha Trang'),
(9, 'CUS007', 'Cong ty CP Van Tai Quoc Te A Chau', 'Ngo Van Hieu', '0963777888', 'hieu.ngo@achau-logistics.vn', '22 Bach Dang, Hai Phong'),
(10, 'CUS008', 'Cua hang Gia dung Thanh Cong', 'Vo Thi My', '0972888999', 'my.vo@thanhcong.vn', '10 Quang Trung, Vung Tau'),
(999, 'KH-TEST', 'Khách hàng VIP của Đạt', NULL, '0901234567', NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `inbound_receipts`
--

DROP TABLE IF EXISTS `inbound_receipts`;
CREATE TABLE IF NOT EXISTS `inbound_receipts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `inbound_receipt_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `received_at` datetime DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` bigint DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  `purchase_order_id` bigint DEFAULT NULL,
  `delivery_note_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `inbound_receipt_code` (`inbound_receipt_code`),
  KEY `created_by` (`created_by`),
  KEY `supplier_id` (`supplier_id`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `fk_inbound_po` (`purchase_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=55 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `inbound_receipts`
--

INSERT INTO `inbound_receipts` (`id`, `inbound_receipt_code`, `created_by`, `created_at`, `received_at`, `status`, `supplier_id`, `warehouse_id`, `purchase_order_id`, `delivery_note_code`) VALUES
(1, 'PN-1775582442324', NULL, '2026-04-08 00:20:42', '2026-04-08 00:20:42', 'COMPLETED', NULL, 1, NULL, NULL),
(2, 'PN-1775582663588', NULL, '2026-04-08 00:24:24', '2026-04-08 00:24:24', 'COMPLETED', 1, 1, 1, NULL),
(3, 'PN-1775582669170', NULL, '2026-04-08 00:24:29', '2026-04-08 00:24:29', 'COMPLETED', 1, 1, 1, NULL),
(4, 'PN-1775582903409', NULL, '2026-04-08 00:28:24', '2026-04-08 00:28:24', 'COMPLETED', 1, 1, 1, NULL),
(5, 'PN-1775583717992', NULL, '2026-04-08 00:41:58', '2026-04-08 00:41:58', 'COMPLETED', 1, 1, 1, NULL),
(6, 'PN-1775583730487', NULL, '2026-04-08 00:42:10', '2026-04-08 00:42:10', 'COMPLETED', 1, 1, 1, NULL),
(7, 'PN-1775583756571', NULL, '2026-04-08 00:42:37', '2026-04-08 00:42:37', 'COMPLETED', 1, 1, 1, NULL),
(8, 'PN-1775586937905', NULL, '2026-04-08 01:35:38', '2026-04-08 01:35:38', 'COMPLETED', 1, 1, 10, NULL),
(9, 'PN-1775587174066', NULL, '2026-04-08 01:39:34', '2026-04-08 01:39:34', 'COMPLETED', 1, 1, 10, NULL),
(10, 'PN-1775587183438', NULL, '2026-04-08 01:39:43', '2026-04-08 01:39:43', 'COMPLETED', 1, 1, 10, NULL),
(11, 'PN-1775587192194', NULL, '2026-04-08 01:39:52', '2026-04-08 01:39:52', 'COMPLETED', 1, 1, 10, NULL),
(12, 'PN-1775587387220', NULL, '2026-04-08 01:43:07', '2026-04-08 01:43:07', 'COMPLETED', 1, 1, 10, NULL),
(13, 'PN-1775587486636', NULL, '2026-04-08 01:44:47', '2026-04-08 01:44:47', 'COMPLETED', 1, 1, 10, NULL),
(14, 'PN-1775587796819', NULL, '2026-04-08 01:49:57', '2026-04-08 01:49:57', 'COMPLETED', 1, 1, 10, NULL),
(15, 'PN-1775587918137', NULL, '2026-04-08 01:51:58', '2026-04-08 01:51:58', 'COMPLETED', 1, 1, 10, NULL),
(16, 'PN-1775589063754', NULL, '2026-04-08 02:11:04', '2026-04-08 02:11:04', 'COMPLETED', 1, 1, 10, NULL),
(17, 'PN-1775590538670', NULL, '2026-04-08 02:35:39', '2026-04-08 02:35:39', 'COMPLETED', 1, 1, 10, NULL),
(18, 'PN-1775623346342', NULL, '2026-04-08 11:42:27', '2026-04-08 11:42:27', 'COMPLETED', 1, 1, 10, NULL),
(19, 'PN-1775633636389', NULL, '2026-04-08 14:33:56', '2026-04-08 14:33:56', 'COMPLETED', 1, 1, 10, NULL),
(20, 'PN-1775645030009', NULL, '2026-04-08 17:43:50', '2026-04-08 17:43:50', 'COMPLETED', 1, 1, 10, NULL),
(21, 'PN-1775645053381', NULL, '2026-04-08 17:44:13', '2026-04-08 17:44:13', 'COMPLETED', 1, 1, 10, NULL),
(22, 'PN-1775645902660', NULL, '2026-04-08 17:58:23', '2026-04-08 17:58:23', 'COMPLETED', 1, 1, 10, NULL),
(24, 'PN-1775907069619', NULL, '2026-04-11 18:31:10', '2026-04-11 18:31:10', 'COMPLETED', 1, 1, NULL, NULL),
(25, 'PN-1775907178565', NULL, '2026-04-11 18:32:59', '2026-04-11 18:32:59', 'COMPLETED', 1, 1, NULL, NULL),
(26, 'PN-1775908958101', NULL, '2026-04-11 19:02:38', '2026-04-11 19:02:38', 'COMPLETED', NULL, 1, NULL, NULL),
(27, 'PN-1775910518813', NULL, '2026-04-11 19:28:39', '2026-04-11 19:28:39', 'COMPLETED', NULL, 1, NULL, NULL),
(28, 'PN-1775910524418', NULL, '2026-04-11 19:28:44', '2026-04-11 19:28:44', 'COMPLETED', NULL, 1, NULL, NULL),
(29, 'PN-1775915210638', NULL, '2026-04-11 20:46:51', '2026-04-11 20:46:51', 'COMPLETED', NULL, 1, NULL, NULL),
(37, 'PN-1775919093736', NULL, '2026-04-11 21:51:34', '2026-04-11 21:51:34', 'COMPLETED', 1, 1, NULL, NULL),
(38, 'PN-1775969681906', NULL, '2026-04-12 11:54:42', '2026-04-12 11:54:42', 'COMPLETED', 1, 1, NULL, NULL),
(39, 'PN-1775970574764', NULL, '2026-04-12 12:09:35', '2026-04-12 12:09:35', 'COMPLETED', 1, 1, NULL, NULL),
(40, 'PN-1775971587040', NULL, '2026-04-12 12:26:27', '2026-04-12 12:26:27', 'COMPLETED', 1, 1, NULL, NULL),
(41, 'PN-1775972241919', NULL, '2026-04-12 12:37:22', '2026-04-12 12:37:22', 'COMPLETED', 7, 1, NULL, NULL),
(42, 'PN-1775975182775', NULL, '2026-04-12 13:26:23', '2026-04-12 13:26:23', 'COMPLETED', 7, 1, NULL, NULL),
(43, 'PN-1775983114989', NULL, '2026-04-12 15:38:35', '2026-04-12 15:38:35', 'COMPLETED', 9, 1, NULL, NULL),
(44, 'PN-1775984116384', NULL, '2026-04-12 15:55:16', '2026-04-12 15:55:16', 'COMPLETED', 1, 1, NULL, NULL),
(45, 'PN-1775985792695', NULL, '2026-04-12 16:23:13', '2026-04-12 16:23:13', 'COMPLETED', 4, 1, NULL, NULL),
(46, 'PN-1775987949131', NULL, '2026-04-12 16:59:09', '2026-04-12 16:59:09', 'COMPLETED', 2, 1, NULL, NULL),
(47, 'PN-1775989305131', NULL, '2026-04-12 17:21:45', '2026-04-12 17:21:45', 'COMPLETED', 9, 1, NULL, NULL),
(48, 'PN-1775990088915', NULL, '2026-04-12 17:34:49', '2026-04-12 17:34:49', 'COMPLETED', 4, 1, NULL, NULL),
(49, 'PN-1776253723156', NULL, '2026-04-15 18:48:43', '2026-04-15 18:48:43', 'COMPLETED', 5, 1, NULL, NULL),
(50, 'PN-1776255909518', NULL, '2026-04-15 19:25:10', '2026-04-15 19:25:10', 'COMPLETED', 4, 1, NULL, NULL),
(51, 'PN-1776256318573', NULL, '2026-04-15 19:31:59', '2026-04-15 19:31:59', 'COMPLETED', 3, 1, NULL, NULL),
(52, 'PN-1776410017007', 1, '2026-04-17 14:13:37', '2026-04-17 14:13:37', 'COMPLETED', 2, 1, NULL, NULL),
(53, 'PN-1776410168478', 1, '2026-04-17 14:16:09', '2026-04-17 14:16:09', 'COMPLETED', 3, 1, NULL, NULL),
(54, 'PN-1776413815336', 1, '2026-04-17 15:16:55', '2026-04-17 15:16:55', 'COMPLETED', 5, 1, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `inbound_receipt_items`
--

DROP TABLE IF EXISTS `inbound_receipt_items`;
CREATE TABLE IF NOT EXISTS `inbound_receipt_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expected_qty` decimal(15,2) NOT NULL,
  `actual_qty` decimal(15,2) DEFAULT NULL,
  `import_price` decimal(15,2) DEFAULT NULL,
  `inbound_receipt_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  `putaway_location_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `inbound_receipt_id` (`inbound_receipt_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`),
  KEY `putaway_location_id` (`putaway_location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `inbound_receipt_items`
--

INSERT INTO `inbound_receipt_items` (`id`, `expected_qty`, `actual_qty`, `import_price`, `inbound_receipt_id`, `product_id`, `batch_id`, `putaway_location_id`) VALUES
(1, 50.00, 50.00, NULL, 15, 65, 1, 1),
(2, 20.00, 20.00, NULL, 15, 66, 1, 1),
(3, 50.00, 50.00, NULL, 16, 65, 1, 1),
(4, 20.00, 20.00, NULL, 16, 66, 1, 1),
(5, 50.00, 50.00, NULL, 17, 65, 1, 1),
(6, 20.00, 20.00, NULL, 17, 66, 1, 1),
(7, 50.00, 50.00, NULL, 18, 65, 1, 1),
(8, 20.00, 20.00, NULL, 18, 66, 1, 1),
(9, 50.00, 50.00, NULL, 19, 65, 1, 1),
(10, 20.00, 20.00, NULL, 19, 66, 1, 1),
(11, 50.00, 50.00, NULL, 20, 65, 1, 1),
(12, 20.00, 20.00, NULL, 20, 66, 1, 1),
(13, 50.00, 50.00, NULL, 21, 65, 1, 1),
(14, 20.00, 20.00, NULL, 21, 66, 1, 1),
(15, 50.00, 50.00, NULL, 22, 65, 1, 1),
(16, 20.00, 20.00, NULL, 22, 66, 1, 1),
(17, 10.00, 10.00, NULL, 24, 79, 51, 1),
(18, 2.00, 2.00, NULL, 24, 65, 37, 1),
(19, 5.00, 5.00, NULL, 24, 68, 40, 1),
(20, 10.00, 10.00, NULL, 25, 79, 51, 1),
(21, 2.00, 2.00, NULL, 25, 65, 37, 1),
(22, 5.00, 5.00, NULL, 25, 68, 40, 1),
(23, 10.00, 10.00, NULL, 26, 79, 51, 1),
(24, 2.00, 2.00, NULL, 26, 65, 37, 1),
(25, 5.00, 5.00, NULL, 26, 68, 40, 1),
(26, 10.00, 10.00, NULL, 27, 79, 51, 1),
(27, 2.00, 2.00, NULL, 27, 65, 37, 1),
(28, 5.00, 5.00, NULL, 27, 68, 40, 1),
(29, 10.00, 10.00, NULL, 28, 79, 51, 1),
(30, 2.00, 2.00, NULL, 28, 65, 37, 1),
(31, 5.00, 5.00, NULL, 28, 68, 40, 1),
(32, 20.00, 20.00, NULL, 29, 79, 51, 1),
(33, 10.00, 10.00, NULL, 29, 65, 37, 1),
(34, 10.00, 10.00, NULL, 29, 68, 40, 1),
(35, 10.00, 10.00, NULL, 37, 79, 51, 1),
(36, 2.00, 2.00, NULL, 37, 65, 37, 1),
(37, 5.00, 5.00, NULL, 37, 68, 40, 1),
(38, 10.00, 10.00, NULL, 38, 79, 51, 1),
(39, 2.00, 2.00, NULL, 38, 65, 37, 1),
(40, 5.00, 5.00, NULL, 38, 68, 40, 1),
(41, 10.00, 10.00, NULL, 39, 79, 51, 1),
(42, 2.00, 2.00, NULL, 39, 65, 37, 1),
(43, 5.00, 5.00, NULL, 39, 68, 40, 1),
(44, 10.00, 10.00, NULL, 40, 79, 51, 1),
(45, 2.00, 2.00, NULL, 40, 65, 37, 1),
(46, 5.00, 5.00, NULL, 40, 68, 40, 1),
(47, 10.00, 10.00, NULL, 41, 79, 51, 1),
(48, 2.00, 2.00, NULL, 41, 65, 37, 1),
(49, 5.00, 5.00, NULL, 41, 68, 40, 1),
(50, 6.00, 6.00, NULL, 42, 82, 54, 1),
(51, 5.00, 5.00, NULL, 42, 80, 52, 10),
(52, 4.00, 4.00, NULL, 42, 67, 39, 1),
(53, 3.00, 3.00, NULL, 42, 69, 41, 11),
(54, 2.00, 2.00, NULL, 42, 73, 45, 10),
(55, 6.00, 6.00, NULL, 43, 82, 54, 1),
(56, 5.00, 5.00, NULL, 43, 80, 52, 10),
(57, 4.00, 4.00, NULL, 43, 67, 39, 1),
(58, 3.00, 3.00, NULL, 43, 69, 41, 11),
(59, 2.00, 2.00, NULL, 43, 73, 45, 10),
(60, 6.00, 6.00, 110000.00, 44, 82, 54, 1),
(61, 5.00, 5.00, 130000.00, 44, 80, 52, 10),
(62, 4.00, 4.00, 240000.00, 44, 67, 39, 1),
(63, 3.00, 3.00, 720000.00, 44, 69, 41, 11),
(64, 2.00, 2.00, 560000.00, 44, 73, 45, 10),
(65, 22.00, 22.00, 110000.00, 45, 82, 54, 1),
(66, 22.00, 22.00, 130000.00, 45, 80, 52, 10),
(67, 22.00, 22.00, 240000.00, 45, 67, 39, 1),
(68, 22.00, 22.00, 720000.00, 45, 69, 41, 11),
(69, 22.00, 22.00, 560000.00, 45, 73, 45, 10),
(70, 6.00, 6.00, 0.00, 46, 82, 54, 1),
(71, 5.00, 5.00, 0.00, 46, 80, 52, 10),
(72, 4.00, 4.00, 0.00, 46, 67, 39, 1),
(73, 3.00, 3.00, 0.00, 46, 69, 41, 11),
(74, 2.00, 2.00, 0.00, 46, 73, 45, 10),
(75, 6.00, 6.00, 110000.00, 47, 82, 54, 1),
(76, 5.00, 5.00, 130000.00, 47, 80, 52, 10),
(77, 4.00, 4.00, 240000.00, 47, 67, 39, 1),
(78, 3.00, 3.00, 720000.00, 47, 69, 41, 11),
(79, 2.00, 2.00, 560000.00, 47, 73, 45, 10),
(80, 6.00, 6.00, 110000.00, 48, 82, 54, 1),
(81, 5.00, 5.00, 130000.00, 48, 80, 52, 10),
(82, 4.00, 4.00, 240000.00, 48, 67, 39, 1),
(83, 3.00, 3.00, 720000.00, 48, 69, 41, 11),
(84, 2.00, 2.00, 560000.00, 48, 73, 45, 10),
(85, 6.00, 6.00, 110000.00, 49, 82, 54, 1),
(86, 5.00, 5.00, 130000.00, 49, 80, 52, 10),
(87, 4.00, 4.00, 240000.00, 49, 67, 39, 1),
(88, 3.00, 3.00, 720000.00, 49, 69, 41, 11),
(89, 2.00, 2.00, 560000.00, 49, 73, 45, 10),
(90, 6.00, 6.00, 110000.00, 50, 82, 54, 1),
(91, 5.00, 5.00, 130000.00, 50, 80, 52, 10),
(92, 4.00, 4.00, 240000.00, 50, 67, 39, 1),
(93, 3.00, 3.00, 720000.00, 50, 69, 41, 11),
(94, 2.00, 2.00, 560000.00, 50, 73, 45, 10),
(95, 57.00, 57.00, 49999.00, 51, 10, 100, 1),
(96, 20.00, 20.00, 300000.00, 51, 74, 46, 1),
(97, 4.00, 4.00, 6000000.00, 51, 72, 44, 1),
(98, 50.00, 50.00, 560000.00, 52, 86, 102, 10),
(99, 50.00, 50.00, 560000.00, 53, 86, 102, 10),
(100, 1.00, 30.00, 9000000.00, 54, 73, 45, 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `inventory_history`
--

DROP TABLE IF EXISTS `inventory_history`;
CREATE TABLE IF NOT EXISTS `inventory_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_location_id` bigint DEFAULT NULL,
  `to_location_id` bigint DEFAULT NULL,
  `qty_change` decimal(38,2) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `qr_code_id` bigint DEFAULT NULL,
  `batch_id` bigint DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `from_location_id` (`from_location_id`),
  KEY `to_location_id` (`to_location_id`),
  KEY `qr_code_id` (`qr_code_id`),
  KEY `batch_id` (`batch_id`),
  KEY `product_id` (`product_id`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=239 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `inventory_history`
--

INSERT INTO `inventory_history` (`id`, `transaction_type`, `from_location_id`, `to_location_id`, `qty_change`, `created_at`, `qr_code_id`, `batch_id`, `product_id`, `warehouse_id`, `user_id`) VALUES
(1, 'INBOUND', NULL, 1, 50.00, '2026-04-08 01:51:58', NULL, NULL, 65, 1, NULL),
(2, 'INBOUND', NULL, 1, 20.00, '2026-04-08 01:51:58', NULL, NULL, 66, 1, NULL),
(3, 'INBOUND', NULL, 1, 50.00, '2026-04-08 02:11:03', NULL, NULL, 65, 1, NULL),
(4, 'INBOUND', NULL, 1, 20.00, '2026-04-08 02:11:03', NULL, NULL, 66, 1, NULL),
(5, 'INBOUND', NULL, 1, 50.00, '2026-04-08 02:35:38', NULL, NULL, 65, 1, NULL),
(6, 'INBOUND', NULL, 1, 20.00, '2026-04-08 02:35:38', NULL, NULL, 66, 1, NULL),
(7, 'INBOUND', NULL, 1, 50.00, '2026-04-08 11:42:26', NULL, NULL, 65, 1, NULL),
(8, 'INBOUND', NULL, 1, 20.00, '2026-04-08 11:42:27', NULL, NULL, 66, 1, NULL),
(9, 'INBOUND', NULL, 1, 50.00, '2026-04-08 14:33:56', NULL, NULL, 65, 1, NULL),
(10, 'INBOUND', NULL, 1, 20.00, '2026-04-08 14:33:56', NULL, NULL, 66, 1, NULL),
(11, 'OUTBOUND', 1, NULL, -5.00, '2026-04-08 14:34:13', NULL, 1, 65, 1, NULL),
(12, 'OUTBOUND', 10, NULL, -10.00, '2026-04-08 14:34:59', NULL, 37, 65, 1, NULL),
(13, 'INBOUND', NULL, 1, 50.00, '2026-04-08 17:43:50', NULL, NULL, 65, 1, NULL),
(14, 'INBOUND', NULL, 1, 20.00, '2026-04-08 17:43:50', NULL, NULL, 66, 1, NULL),
(15, 'INBOUND', NULL, 1, 50.00, '2026-04-08 17:44:13', NULL, NULL, 65, 1, NULL),
(16, 'INBOUND', NULL, 1, 20.00, '2026-04-08 17:44:13', NULL, NULL, 66, 1, NULL),
(17, 'INBOUND', NULL, 1, 50.00, '2026-04-08 17:58:22', NULL, NULL, 65, 1, NULL),
(18, 'INBOUND', NULL, 1, 20.00, '2026-04-08 17:58:22', NULL, NULL, 66, 1, NULL),
(19, 'INBOUND', NULL, 1, 10.00, '2026-04-11 18:31:09', NULL, 51, 79, 1, NULL),
(20, 'INBOUND', NULL, 1, 2.00, '2026-04-11 18:31:09', NULL, 37, 65, 1, NULL),
(21, 'INBOUND', NULL, 1, 5.00, '2026-04-11 18:31:09', NULL, 40, 68, 1, NULL),
(22, 'INBOUND', NULL, 1, 10.00, '2026-04-11 18:32:58', NULL, 51, 79, 1, NULL),
(23, 'INBOUND', NULL, 1, 2.00, '2026-04-11 18:32:58', NULL, 37, 65, 1, NULL),
(24, 'INBOUND', NULL, 1, 5.00, '2026-04-11 18:32:58', NULL, 40, 68, 1, NULL),
(25, 'INBOUND', NULL, 1, 10.00, '2026-04-11 19:02:38', NULL, 51, 79, 1, NULL),
(26, 'INBOUND', NULL, 1, 2.00, '2026-04-11 19:02:38', NULL, 37, 65, 1, NULL),
(27, 'INBOUND', NULL, 1, 5.00, '2026-04-11 19:02:38', NULL, 40, 68, 1, NULL),
(28, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 19:04:20', NULL, 51, 79, 1, NULL),
(29, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 19:04:20', NULL, 37, 65, 1, NULL),
(30, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 19:04:20', NULL, 40, 68, 1, NULL),
(31, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 19:18:32', NULL, 51, 79, 1, NULL),
(32, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 19:18:32', NULL, 37, 65, 1, NULL),
(33, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 19:18:32', NULL, 40, 68, 1, NULL),
(34, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 19:25:30', NULL, 51, 79, 1, NULL),
(35, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 19:25:30', NULL, 37, 65, 1, NULL),
(36, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 19:25:30', NULL, 40, 68, 1, NULL),
(37, 'INBOUND', NULL, 1, 10.00, '2026-04-11 19:28:38', NULL, 51, 79, 1, NULL),
(38, 'INBOUND', NULL, 1, 2.00, '2026-04-11 19:28:38', NULL, 37, 65, 1, NULL),
(39, 'INBOUND', NULL, 1, 5.00, '2026-04-11 19:28:38', NULL, 40, 68, 1, NULL),
(40, 'INBOUND', NULL, 1, 10.00, '2026-04-11 19:28:44', NULL, 51, 79, 1, NULL),
(41, 'INBOUND', NULL, 1, 2.00, '2026-04-11 19:28:44', NULL, 37, 65, 1, NULL),
(42, 'INBOUND', NULL, 1, 5.00, '2026-04-11 19:28:44', NULL, 40, 68, 1, NULL),
(43, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 19:29:03', NULL, 51, 79, 1, NULL),
(44, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 19:29:03', NULL, 37, 65, 1, NULL),
(45, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 19:29:03', NULL, 40, 68, 1, NULL),
(46, 'OUTBOUND', 1, NULL, -9.00, '2026-04-11 19:57:32', NULL, 51, 79, 1, NULL),
(47, 'OUTBOUND', 1, NULL, -1.00, '2026-04-11 19:57:32', NULL, 37, 65, 1, NULL),
(48, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 19:57:32', NULL, 40, 68, 1, NULL),
(49, 'INBOUND', NULL, 1, 20.00, '2026-04-11 20:46:50', NULL, 51, 79, 1, NULL),
(50, 'INBOUND', NULL, 1, 10.00, '2026-04-11 20:46:50', NULL, 37, 65, 1, NULL),
(51, 'INBOUND', NULL, 1, 10.00, '2026-04-11 20:46:50', NULL, 40, 68, 1, NULL),
(52, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 21:04:23', NULL, 51, 79, 1, NULL),
(53, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 21:04:23', NULL, 37, 65, 1, NULL),
(54, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 21:04:23', NULL, 40, 68, 1, NULL),
(55, 'OUTBOUND', 1, NULL, -1.00, '2026-04-11 21:05:48', NULL, 51, 79, 1, NULL),
(56, 'OUTBOUND', 1, NULL, -1.00, '2026-04-11 21:05:48', NULL, 37, 65, 1, NULL),
(57, 'OUTBOUND', 1, NULL, -1.00, '2026-04-11 21:05:48', NULL, 40, 68, 1, NULL),
(58, 'INBOUND', NULL, 1, 10.00, '2026-04-11 21:51:33', NULL, 51, 79, 1, NULL),
(59, 'INBOUND', NULL, 1, 2.00, '2026-04-11 21:51:33', NULL, 37, 65, 1, NULL),
(60, 'INBOUND', NULL, 1, 5.00, '2026-04-11 21:51:33', NULL, 40, 68, 1, NULL),
(61, 'OUTBOUND', 1, NULL, -10.00, '2026-04-11 21:51:52', NULL, 51, 79, 1, NULL),
(62, 'OUTBOUND', 1, NULL, -2.00, '2026-04-11 21:51:52', NULL, 37, 65, 1, NULL),
(63, 'OUTBOUND', 1, NULL, -5.00, '2026-04-11 21:51:52', NULL, 40, 68, 1, NULL),
(64, 'INBOUND', NULL, 1, 10.00, '2026-04-12 11:54:42', NULL, 51, 79, 1, NULL),
(65, 'INBOUND', NULL, 1, 2.00, '2026-04-12 11:54:42', NULL, 37, 65, 1, NULL),
(66, 'INBOUND', NULL, 1, 5.00, '2026-04-12 11:54:42', NULL, 40, 68, 1, NULL),
(67, 'OUTBOUND', 1, NULL, -10.00, '2026-04-12 11:54:59', NULL, 51, 79, 1, NULL),
(68, 'OUTBOUND', 1, NULL, -2.00, '2026-04-12 11:54:59', NULL, 37, 65, 1, NULL),
(69, 'OUTBOUND', 1, NULL, -5.00, '2026-04-12 11:54:59', NULL, 40, 68, 1, NULL),
(70, 'INBOUND', NULL, 1, 10.00, '2026-04-12 12:09:34', NULL, 51, 79, 1, NULL),
(71, 'INBOUND', NULL, 1, 2.00, '2026-04-12 12:09:34', NULL, 37, 65, 1, NULL),
(72, 'INBOUND', NULL, 1, 5.00, '2026-04-12 12:09:34', NULL, 40, 68, 1, NULL),
(73, 'OUTBOUND', 1, NULL, -2.00, '2026-04-12 12:10:49', NULL, 51, 79, 1, NULL),
(74, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 12:10:49', NULL, 37, 65, 1, NULL),
(75, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 12:10:49', NULL, 40, 68, 1, NULL),
(76, 'INBOUND', NULL, 1, 10.00, '2026-04-12 12:26:27', NULL, 51, 79, 1, NULL),
(77, 'INBOUND', NULL, 1, 2.00, '2026-04-12 12:26:27', NULL, 37, 65, 1, NULL),
(78, 'INBOUND', NULL, 1, 5.00, '2026-04-12 12:26:27', NULL, 40, 68, 1, NULL),
(79, 'INBOUND', NULL, 1, 10.00, '2026-04-12 12:37:21', NULL, 51, 79, 1, NULL),
(80, 'INBOUND', NULL, 1, 2.00, '2026-04-12 12:37:21', NULL, 37, 65, 1, NULL),
(81, 'INBOUND', NULL, 1, 5.00, '2026-04-12 12:37:21', NULL, 40, 68, 1, NULL),
(82, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 12:39:13', NULL, 51, 79, 1, NULL),
(83, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 12:39:13', NULL, 37, 65, 1, NULL),
(84, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 12:39:13', NULL, 40, 68, 1, NULL),
(85, 'INBOUND', NULL, 1, 6.00, '2026-04-12 13:26:22', NULL, 54, 82, 1, NULL),
(86, 'INBOUND', NULL, 10, 5.00, '2026-04-12 13:26:22', NULL, 52, 80, 1, NULL),
(87, 'INBOUND', NULL, 1, 4.00, '2026-04-12 13:26:22', NULL, 39, 67, 1, NULL),
(88, 'INBOUND', NULL, 11, 3.00, '2026-04-12 13:26:22', NULL, 41, 69, 1, NULL),
(89, 'INBOUND', NULL, 10, 2.00, '2026-04-12 13:26:22', NULL, 45, 73, 1, NULL),
(90, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 13:26:45', NULL, 54, 82, 1, NULL),
(91, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 13:26:45', NULL, 52, 80, 1, NULL),
(92, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 13:26:45', NULL, 39, 67, 1, NULL),
(93, 'OUTBOUND', 11, NULL, -2.00, '2026-04-12 13:26:45', NULL, 41, 69, 1, NULL),
(94, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 13:26:45', NULL, 45, 73, 1, NULL),
(95, 'INBOUND', NULL, 1, 6.00, '2026-04-12 15:38:35', NULL, 54, 82, 1, NULL),
(96, 'INBOUND', NULL, 10, 5.00, '2026-04-12 15:38:35', NULL, 52, 80, 1, NULL),
(97, 'INBOUND', NULL, 1, 4.00, '2026-04-12 15:38:35', NULL, 39, 67, 1, NULL),
(98, 'INBOUND', NULL, 11, 3.00, '2026-04-12 15:38:35', NULL, 41, 69, 1, NULL),
(99, 'INBOUND', NULL, 10, 2.00, '2026-04-12 15:38:35', NULL, 45, 73, 1, NULL),
(100, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 15:39:08', NULL, 54, 82, 1, NULL),
(101, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 15:39:08', NULL, 52, 80, 1, NULL),
(102, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 15:39:08', NULL, 39, 67, 1, NULL),
(103, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 15:39:08', NULL, 41, 69, 1, NULL),
(104, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 15:39:08', NULL, 45, 73, 1, NULL),
(105, 'INBOUND', NULL, 1, 6.00, '2026-04-12 15:55:16', NULL, 54, 82, 1, NULL),
(106, 'INBOUND', NULL, 10, 5.00, '2026-04-12 15:55:16', NULL, 52, 80, 1, NULL),
(107, 'INBOUND', NULL, 1, 4.00, '2026-04-12 15:55:16', NULL, 39, 67, 1, NULL),
(108, 'INBOUND', NULL, 11, 3.00, '2026-04-12 15:55:16', NULL, 41, 69, 1, NULL),
(109, 'INBOUND', NULL, 10, 2.00, '2026-04-12 15:55:16', NULL, 45, 73, 1, NULL),
(110, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 16:17:26', NULL, 54, 82, 1, NULL),
(111, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 16:17:26', NULL, 52, 80, 1, NULL),
(112, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 16:17:26', NULL, 39, 67, 1, NULL),
(113, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 16:17:26', NULL, 41, 69, 1, NULL),
(114, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 16:17:26', NULL, 45, 73, 1, NULL),
(115, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:19:03', NULL, 54, 82, 1, NULL),
(116, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 16:19:03', NULL, 52, 80, 1, NULL),
(117, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:19:03', NULL, 39, 67, 1, NULL),
(118, 'OUTBOUND', 11, NULL, -1.00, '2026-04-12 16:19:03', NULL, 41, 69, 1, NULL),
(119, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 16:19:03', NULL, 45, 73, 1, NULL),
(123, 'INBOUND', NULL, 1, 22.00, '2026-04-12 16:23:12', NULL, 54, 82, 1, NULL),
(124, 'INBOUND', NULL, 10, 22.00, '2026-04-12 16:23:12', NULL, 52, 80, 1, NULL),
(125, 'INBOUND', NULL, 1, 22.00, '2026-04-12 16:23:12', NULL, 39, 67, 1, NULL),
(126, 'INBOUND', NULL, 11, 22.00, '2026-04-12 16:23:12', NULL, 41, 69, 1, NULL),
(127, 'INBOUND', NULL, 10, 22.00, '2026-04-12 16:23:12', NULL, 45, 73, 1, NULL),
(128, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 16:23:37', NULL, 54, 82, 1, NULL),
(129, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 16:23:37', NULL, 52, 80, 1, NULL),
(130, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 16:23:37', NULL, 39, 67, 1, NULL),
(131, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 16:23:37', NULL, 41, 69, 1, NULL),
(132, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 16:23:37', NULL, 45, 73, 1, NULL),
(133, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:32:00', NULL, 54, 82, 1, NULL),
(134, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 16:32:00', NULL, 52, 80, 1, NULL),
(135, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:32:00', NULL, 39, 67, 1, NULL),
(136, 'OUTBOUND', 11, NULL, -1.00, '2026-04-12 16:32:00', NULL, 41, 69, 1, NULL),
(137, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 16:32:00', NULL, 45, 73, 1, NULL),
(138, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:42:12', NULL, 54, 82, 1, NULL),
(139, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 16:42:12', NULL, 52, 80, 1, NULL),
(140, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 16:42:12', NULL, 39, 67, 1, NULL),
(141, 'OUTBOUND', 11, NULL, -1.00, '2026-04-12 16:42:12', NULL, 41, 69, 1, NULL),
(142, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 16:42:12', NULL, 45, 73, 1, NULL),
(143, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 16:43:56', NULL, 54, 82, 1, NULL),
(144, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 16:43:56', NULL, 52, 80, 1, NULL),
(145, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 16:43:56', NULL, 39, 67, 1, NULL),
(146, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 16:43:56', NULL, 41, 69, 1, NULL),
(147, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 16:43:56', NULL, 45, 73, 1, NULL),
(148, 'INBOUND', NULL, 1, 6.00, '2026-04-12 16:59:09', NULL, 54, 82, 1, NULL),
(149, 'INBOUND', NULL, 10, 5.00, '2026-04-12 16:59:09', NULL, 52, 80, 1, NULL),
(150, 'INBOUND', NULL, 1, 4.00, '2026-04-12 16:59:09', NULL, 39, 67, 1, NULL),
(151, 'INBOUND', NULL, 11, 3.00, '2026-04-12 16:59:09', NULL, 41, 69, 1, NULL),
(152, 'INBOUND', NULL, 10, 2.00, '2026-04-12 16:59:09', NULL, 45, 73, 1, NULL),
(153, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 16:59:32', NULL, 54, 82, 1, NULL),
(154, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 16:59:32', NULL, 52, 80, 1, NULL),
(155, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 16:59:32', NULL, 39, 67, 1, NULL),
(156, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 16:59:32', NULL, 41, 69, 1, NULL),
(157, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 16:59:32', NULL, 45, 73, 1, NULL),
(158, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 17:00:07', NULL, 54, 82, 1, NULL),
(159, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 17:00:07', NULL, 52, 80, 1, NULL),
(160, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 17:00:07', NULL, 39, 67, 1, NULL),
(161, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 17:00:07', NULL, 41, 69, 1, NULL),
(162, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 17:00:07', NULL, 45, 73, 1, NULL),
(163, 'INBOUND', NULL, 1, 6.00, '2026-04-12 17:21:45', NULL, 54, 82, 1, NULL),
(164, 'INBOUND', NULL, 10, 5.00, '2026-04-12 17:21:45', NULL, 52, 80, 1, NULL),
(165, 'INBOUND', NULL, 1, 4.00, '2026-04-12 17:21:45', NULL, 39, 67, 1, NULL),
(166, 'INBOUND', NULL, 11, 3.00, '2026-04-12 17:21:45', NULL, 41, 69, 1, NULL),
(167, 'INBOUND', NULL, 10, 2.00, '2026-04-12 17:21:45', NULL, 45, 73, 1, NULL),
(168, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 17:22:01', NULL, 54, 82, 1, NULL),
(169, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 17:22:01', NULL, 52, 80, 1, NULL),
(170, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 17:22:01', NULL, 39, 67, 1, NULL),
(171, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 17:22:01', NULL, 41, 69, 1, NULL),
(172, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 17:22:01', NULL, 45, 73, 1, NULL),
(173, 'INBOUND', NULL, 1, 6.00, '2026-04-12 17:34:49', NULL, 54, 82, 1, NULL),
(174, 'INBOUND', NULL, 10, 5.00, '2026-04-12 17:34:49', NULL, 52, 80, 1, NULL),
(175, 'INBOUND', NULL, 1, 4.00, '2026-04-12 17:34:49', NULL, 39, 67, 1, NULL),
(176, 'INBOUND', NULL, 11, 3.00, '2026-04-12 17:34:49', NULL, 41, 69, 1, NULL),
(177, 'INBOUND', NULL, 10, 2.00, '2026-04-12 17:34:49', NULL, 45, 73, 1, NULL),
(178, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 17:36:13', NULL, 54, 82, 1, NULL),
(179, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 17:36:13', NULL, 52, 80, 1, NULL),
(180, 'OUTBOUND', 1, NULL, -1.00, '2026-04-12 17:36:13', NULL, 39, 67, 1, NULL),
(181, 'OUTBOUND', 11, NULL, -1.00, '2026-04-12 17:36:13', NULL, 41, 69, 1, NULL),
(182, 'OUTBOUND', 10, NULL, -1.00, '2026-04-12 17:36:13', NULL, 45, 73, 1, NULL),
(183, 'OUTBOUND', 1, NULL, -6.00, '2026-04-12 20:58:56', NULL, 54, 82, 1, NULL),
(184, 'OUTBOUND', 10, NULL, -5.00, '2026-04-12 20:58:56', NULL, 52, 80, 1, NULL),
(185, 'OUTBOUND', 1, NULL, -4.00, '2026-04-12 20:58:56', NULL, 39, 67, 1, NULL),
(186, 'OUTBOUND', 11, NULL, -3.00, '2026-04-12 20:58:56', NULL, 41, 69, 1, NULL),
(187, 'OUTBOUND', 10, NULL, -2.00, '2026-04-12 20:58:56', NULL, 45, 73, 1, NULL),
(188, 'OUTBOUND', 1, NULL, -2.00, '2026-04-15 17:05:33', NULL, 54, 82, 1, NULL),
(189, 'OUTBOUND', 10, NULL, -5.00, '2026-04-15 17:05:33', NULL, 52, 80, 1, NULL),
(190, 'OUTBOUND', 1, NULL, -4.00, '2026-04-15 17:05:33', NULL, 39, 67, 1, NULL),
(191, 'OUTBOUND', 11, NULL, -3.00, '2026-04-15 17:05:33', NULL, 41, 69, 1, NULL),
(192, 'OUTBOUND', 10, NULL, -2.00, '2026-04-15 17:05:33', NULL, 45, 73, 1, NULL),
(193, 'OUTBOUND', 1, NULL, -1.00, '2026-04-15 17:26:09', NULL, 54, 82, 1, NULL),
(194, 'OUTBOUND', 10, NULL, -1.00, '2026-04-15 17:26:09', NULL, 52, 80, 1, NULL),
(195, 'OUTBOUND', 1, NULL, -1.00, '2026-04-15 17:26:09', NULL, 39, 67, 1, NULL),
(196, 'OUTBOUND', 11, NULL, -3.00, '2026-04-15 17:26:09', NULL, 41, 69, 1, NULL),
(197, 'OUTBOUND', 10, NULL, -2.00, '2026-04-15 17:26:09', NULL, 45, 73, 1, NULL),
(198, 'OUTBOUND', 1, NULL, -1.00, '2026-04-15 18:20:07', NULL, 54, 82, 1, NULL),
(199, 'OUTBOUND', 10, NULL, -1.00, '2026-04-15 18:20:07', NULL, 52, 80, 1, NULL),
(200, 'OUTBOUND', 1, NULL, -1.00, '2026-04-15 18:20:07', NULL, 39, 67, 1, NULL),
(201, 'OUTBOUND', 11, NULL, -1.00, '2026-04-15 18:20:07', NULL, 41, 69, 1, NULL),
(202, 'OUTBOUND', 10, NULL, -2.00, '2026-04-15 18:20:07', NULL, 45, 73, 1, NULL),
(203, 'INBOUND', NULL, 1, 6.00, '2026-04-15 18:48:43', NULL, 54, 82, 1, NULL),
(204, 'INBOUND', NULL, 10, 5.00, '2026-04-15 18:48:43', NULL, 52, 80, 1, NULL),
(205, 'INBOUND', NULL, 1, 4.00, '2026-04-15 18:48:43', NULL, 39, 67, 1, NULL),
(206, 'INBOUND', NULL, 11, 3.00, '2026-04-15 18:48:43', NULL, 41, 69, 1, NULL),
(207, 'INBOUND', NULL, 10, 2.00, '2026-04-15 18:48:43', NULL, 45, 73, 1, NULL),
(208, 'OUTBOUND', 1, NULL, -4.00, '2026-04-15 18:49:28', NULL, 39, 67, 1, NULL),
(209, 'OUTBOUND', 11, NULL, -3.00, '2026-04-15 18:49:28', NULL, 41, 69, 1, NULL),
(210, 'OUTBOUND', 10, NULL, -2.00, '2026-04-15 18:49:28', NULL, 45, 73, 1, NULL),
(211, 'INBOUND', NULL, 1, 6.00, '2026-04-15 19:25:09', NULL, 54, 82, 1, NULL),
(212, 'INBOUND', NULL, 10, 5.00, '2026-04-15 19:25:09', NULL, 52, 80, 1, NULL),
(213, 'INBOUND', NULL, 1, 4.00, '2026-04-15 19:25:09', NULL, 39, 67, 1, NULL),
(214, 'INBOUND', NULL, 11, 3.00, '2026-04-15 19:25:09', NULL, 41, 69, 1, NULL),
(215, 'INBOUND', NULL, 10, 2.00, '2026-04-15 19:25:09', NULL, 45, 73, 1, NULL),
(216, 'OUTBOUND', 1, NULL, -6.00, '2026-04-15 19:25:37', NULL, 54, 82, 1, NULL),
(217, 'OUTBOUND', 10, NULL, -5.00, '2026-04-15 19:25:37', NULL, 52, 80, 1, NULL),
(218, 'INBOUND', NULL, 1, 57.00, '2026-04-15 19:31:58', NULL, 100, 10, 1, NULL),
(219, 'INBOUND', NULL, 1, 20.00, '2026-04-15 19:31:58', NULL, 46, 74, 1, NULL),
(220, 'INBOUND', NULL, 1, 4.00, '2026-04-15 19:31:58', NULL, 44, 72, 1, NULL),
(221, 'OUTBOUND', 1, NULL, -4.00, '2026-04-15 19:34:17', NULL, 44, 72, 1, NULL),
(222, 'OUTBOUND', 1, NULL, -5.00, '2026-04-15 19:34:17', NULL, 46, 74, 1, NULL),
(223, 'OUTBOUND', 1, NULL, -2.00, '2026-04-17 01:18:41', NULL, 1, 65, 1, NULL),
(224, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 02:50:23', NULL, 37, 65, 1, NULL),
(225, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 03:46:28', NULL, 37, 65, 1, NULL),
(226, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 13:45:13', NULL, 40, 68, 1, NULL),
(227, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 13:45:13', NULL, 37, 65, 1, NULL),
(228, 'INBOUND', NULL, 10, 50.00, '2026-04-17 14:13:37', NULL, 102, 86, 1, NULL),
(229, 'INBOUND', NULL, 10, 50.00, '2026-04-17 14:16:08', NULL, 102, 86, 1, NULL),
(230, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 15:01:34', NULL, 40, 68, 1, NULL),
(231, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 15:01:34', NULL, 37, 65, 1, NULL),
(232, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 15:05:16', NULL, 40, 68, 1, NULL),
(233, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 15:05:16', NULL, 37, 65, 1, NULL),
(234, 'INBOUND', NULL, 10, 30.00, '2026-04-17 15:16:55', NULL, 45, 73, 1, NULL),
(235, 'OUTBOUND', 1, NULL, -7.00, '2026-04-17 15:43:48', NULL, 46, 74, 1, NULL),
(236, 'OUTBOUND', 1, NULL, -1.00, '2026-04-17 15:43:48', NULL, 37, 65, 1, NULL),
(237, 'OUTBOUND', 1, NULL, -5.00, '2026-04-17 15:50:46', NULL, 1, 66, 1, NULL),
(238, 'OUTBOUND', 1, NULL, -2.00, '2026-04-17 16:31:02', NULL, 40, 68, 1, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `inventory_location_balances`
--

DROP TABLE IF EXISTS `inventory_location_balances`;
CREATE TABLE IF NOT EXISTS `inventory_location_balances` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qty` decimal(15,2) DEFAULT '0.00',
  `update_at` datetime DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  `location_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_stock_balance` (`warehouse_id`,`location_id`,`product_id`,`batch_id`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `location_id` (`location_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `inventory_location_balances`
--

INSERT INTO `inventory_location_balances` (`id`, `qty`, `update_at`, `status`, `warehouse_id`, `location_id`, `product_id`, `batch_id`) VALUES
(1, 148.00, '2026-04-08 17:58:22', NULL, 1, 1, 65, 1),
(2, 55.00, '2026-04-08 17:58:22', NULL, 1, 1, 66, 1),
(3, 37.00, '2026-04-12 12:37:22', 'AVAILABLE', 1, 1, 79, 51),
(4, 6.00, '2026-04-12 12:37:22', 'AVAILABLE', 1, 1, 65, 37),
(5, 15.00, '2026-04-12 12:37:22', 'AVAILABLE', 1, 1, 68, 40),
(6, 7.00, '2026-04-15 19:25:10', 'AVAILABLE', 1, 1, 82, 54),
(7, 5.00, '2026-04-15 19:25:10', 'AVAILABLE', 1, 10, 80, 52),
(8, 7.00, '2026-04-15 19:25:10', 'AVAILABLE', 1, 1, 67, 39),
(9, 6.00, '2026-04-15 19:25:10', 'AVAILABLE', 1, 11, 69, 41),
(10, 38.00, '2026-04-17 15:16:55', 'AVAILABLE', 1, 10, 73, 45),
(11, 57.00, '2026-04-15 19:31:59', 'AVAILABLE', 1, 1, 10, 100),
(12, 8.00, '2026-04-15 19:31:59', 'AVAILABLE', 1, 1, 74, 46),
(13, 0.00, '2026-04-15 19:31:59', 'AVAILABLE', 1, 1, 72, 44),
(14, 100.00, '2026-04-17 14:16:09', 'AVAILABLE', 1, 10, 86, 102);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `outbound_idempotency_records`
--

DROP TABLE IF EXISTS `outbound_idempotency_records`;
CREATE TABLE IF NOT EXISTS `outbound_idempotency_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `idempotency_key` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `outbound_receipt_id` bigint DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_outbound_idempotency_key` (`idempotency_key`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `outbound_idempotency_records`
--

INSERT INTO `outbound_idempotency_records` (`id`, `created_at`, `idempotency_key`, `outbound_receipt_id`, `status`, `updated_at`) VALUES
(1, '2026-04-17 02:50:23.000000', '7950cddf-e5d3-407d-8b43-30d6eccf9eaa', 39, 'COMPLETED', '2026-04-17 02:50:23.000000'),
(2, '2026-04-17 03:46:28.000000', '7df828f0-ac38-4460-99c6-8ca7c0ec54e8', 40, 'COMPLETED', '2026-04-17 03:46:28.000000'),
(3, '2026-04-17 13:45:13.000000', '6da821fd-5e26-4686-859c-671c297bf440', 41, 'COMPLETED', '2026-04-17 13:45:13.000000'),
(4, '2026-04-17 15:01:34.000000', '8c96a0a5-1a8e-4d73-b551-3a61cbf0526d', 42, 'COMPLETED', '2026-04-17 15:01:34.000000'),
(5, '2026-04-17 15:05:16.000000', 'c05744e8-be81-45df-81d0-86f58f86c720', 43, 'COMPLETED', '2026-04-17 15:05:16.000000'),
(6, '2026-04-17 15:43:48.000000', '0a78760e-c6c1-4ba2-8ecc-7793091b9897', 44, 'COMPLETED', '2026-04-17 15:43:48.000000'),
(7, '2026-04-17 15:50:46.000000', 'b301cdcd-ee5e-4313-8749-3665adae2a84', 45, 'COMPLETED', '2026-04-17 15:50:46.000000'),
(8, '2026-04-17 16:31:02.000000', '6f1786fe-1fb1-4deb-bf70-774b3d156412', 46, 'COMPLETED', '2026-04-17 16:31:02.000000');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `outbound_receipts`
--

DROP TABLE IF EXISTS `outbound_receipts`;
CREATE TABLE IF NOT EXISTS `outbound_receipts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `outbound_receipt_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `shipped_at` datetime DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `outbound_receipt_code` (`outbound_receipt_code`),
  KEY `created_by` (`created_by`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `customer_id` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `outbound_receipts`
--

INSERT INTO `outbound_receipts` (`id`, `outbound_receipt_code`, `status`, `created_by`, `created_at`, `shipped_at`, `warehouse_id`, `customer_id`) VALUES
(1, 'PX-1775633653471', 'SHIPPED', 1, '2026-04-08 14:34:13', '2026-04-08 14:34:13', 1, 1),
(2, 'PX-1775633699688', 'SHIPPED', 1, '2026-04-08 14:35:00', '2026-04-08 14:35:00', 1, 1),
(3, 'PX-1775909060734', 'SHIPPED', 1, '2026-04-11 19:04:21', '2026-04-11 19:04:21', 1, 1),
(4, 'PX-1775909912488', 'SHIPPED', 1, '2026-04-11 19:18:32', '2026-04-11 19:18:32', 1, 1),
(5, 'PX-1775910330458', 'SHIPPED', 1, '2026-04-11 19:25:30', '2026-04-11 19:25:30', 1, 1),
(6, 'PX-1775910543719', 'SHIPPED', 1, '2026-04-11 19:29:04', '2026-04-11 19:29:04', 1, 1),
(7, 'PX-1775912252081', 'SHIPPED', 1, '2026-04-11 19:57:32', '2026-04-11 19:57:32', 1, NULL),
(8, 'PX-1775916263764', 'SHIPPED', 1, '2026-04-11 21:04:24', '2026-04-11 21:04:24', 1, 2),
(10, 'PX-1775916348947', 'SHIPPED', 1, '2026-04-11 21:05:49', '2026-04-11 21:05:49', 1, 2),
(11, 'PX-1775919112708', 'SHIPPED', 1, '2026-04-11 21:51:53', '2026-04-11 21:51:53', 1, 2),
(12, 'PX-1775969699453', 'SHIPPED', 1, '2026-04-12 11:54:59', '2026-04-12 11:54:59', 1, 1),
(14, 'PX-1775970649881', 'SHIPPED', 1, '2026-04-12 12:10:50', '2026-04-12 12:10:50', 1, 1),
(15, 'PX-1775972353225', 'SHIPPED', 1, '2026-04-12 12:39:13', '2026-04-12 12:39:13', 1, 8),
(16, 'PX-1775975205270', 'SHIPPED', 1, '2026-04-12 13:26:45', '2026-04-12 13:26:45', 1, 7),
(17, 'PX-1775983148278', 'SHIPPED', 1, '2026-04-12 15:39:08', '2026-04-12 15:39:08', 1, 1),
(18, 'PX-1775985446771', 'SHIPPED', 1, '2026-04-12 16:17:27', '2026-04-12 16:17:27', 1, 4),
(20, 'PX-1775985543320', 'SHIPPED', 1, '2026-04-12 16:19:03', '2026-04-12 16:19:03', 1, 4),
(22, 'PX-1775985817012', 'SHIPPED', 1, '2026-04-12 16:23:37', '2026-04-12 16:23:37', 1, 10),
(23, 'PX-1775986320377', 'SHIPPED', 1, '2026-04-12 16:32:00', '2026-04-12 16:32:00', 1, 8),
(24, 'PX-1775986932824', 'SHIPPED', 1, '2026-04-12 16:42:13', '2026-04-12 16:42:13', 1, 10),
(25, 'PX-1775987036189', 'SHIPPED', 1, '2026-04-12 16:43:56', '2026-04-12 16:43:56', 1, 6),
(26, 'PX-1775987972798', 'SHIPPED', 1, '2026-04-12 16:59:33', '2026-04-12 16:59:33', 1, 9),
(27, 'PX-1775988007593', 'SHIPPED', 1, '2026-04-12 17:00:08', '2026-04-12 17:00:08', 1, 5),
(28, 'PX-1775989321707', 'SHIPPED', 1, '2026-04-12 17:22:02', '2026-04-12 17:22:02', 1, 1),
(29, 'PX-1775990173116', 'SHIPPED', 1, '2026-04-12 17:36:13', '2026-04-12 17:36:13', 1, 10),
(30, 'PX-1776002336297', 'SHIPPED', 1, '2026-04-12 20:58:56', '2026-04-12 20:58:56', 1, 8),
(31, 'PX-1776247533476', 'SHIPPED', 1, '2026-04-15 17:05:33', '2026-04-15 17:05:33', 1, 6),
(32, 'PX-1776248769639', 'SHIPPED', 2, '2026-04-15 17:26:10', '2026-04-15 17:26:10', 1, 10),
(33, 'PX-1776252007844', 'SHIPPED', 2, '2026-04-15 18:20:08', '2026-04-15 18:20:08', 1, 1),
(34, 'PX-1776253768814', 'SHIPPED', 1, '2026-04-15 18:49:29', '2026-04-15 18:49:29', 1, 8),
(35, 'PX-1776255937088', 'SHIPPED', 1, '2026-04-15 19:25:37', '2026-04-15 19:25:37', 1, NULL),
(37, 'PX-1776256457144', 'SHIPPED', 1, '2026-04-15 19:34:17', '2026-04-15 19:34:17', 1, NULL),
(38, 'PX-1776363521314', 'SHIPPED', 2, '2026-04-17 01:18:41', '2026-04-17 01:18:41', 1, NULL),
(39, 'PX-1776369023801', 'SHIPPED', 1, '2026-04-17 02:50:24', '2026-04-17 02:50:24', 1, NULL),
(40, 'PX-1776372388210', 'SHIPPED', 1, '2026-04-17 03:46:28', '2026-04-17 03:46:28', 1, NULL),
(41, 'PX-1776408313420', 'SHIPPED', 1, '2026-04-17 13:45:13', '2026-04-17 13:45:13', 1, NULL),
(42, 'PX-1776412894309', 'SHIPPED', 1, '2026-04-17 15:01:34', '2026-04-17 15:01:34', 1, NULL),
(43, 'PX-1776413116246', 'SHIPPED', 1, '2026-04-17 15:05:16', '2026-04-17 15:05:16', 1, NULL),
(44, 'PX-1776415428793', 'SHIPPED', 1, '2026-04-17 15:43:49', '2026-04-17 15:43:49', 1, NULL),
(45, 'PX-1776415846132', 'SHIPPED', 1, '2026-04-17 15:50:46', '2026-04-17 15:50:46', 1, 999),
(46, 'PX-1776418262602', 'PARTIAL', 1, '2026-04-17 16:31:03', '2026-04-17 16:31:03', 1, 10);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `outbound_receipt_items`
--

DROP TABLE IF EXISTS `outbound_receipt_items`;
CREATE TABLE IF NOT EXISTS `outbound_receipt_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `requested_qty` decimal(38,2) DEFAULT NULL,
  `actual_qty` decimal(38,2) DEFAULT NULL,
  `selling_price` decimal(15,2) DEFAULT NULL,
  `outbound_receipt_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  `picked_location_id` bigint DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `outbound_receipt_id` (`outbound_receipt_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`),
  KEY `picked_location_id` (`picked_location_id`)
) ENGINE=InnoDB AUTO_INCREMENT=143 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `outbound_receipt_items`
--

INSERT INTO `outbound_receipt_items` (`id`, `requested_qty`, `actual_qty`, `selling_price`, `outbound_receipt_id`, `product_id`, `batch_id`, `picked_location_id`, `price`) VALUES
(1, 5.00, 5.00, NULL, 1, 65, 1, 1, NULL),
(2, 5.00, 10.00, NULL, 2, 65, 37, 10, NULL),
(3, 10.00, 10.00, NULL, 3, 79, 51, 1, NULL),
(4, 2.00, 2.00, NULL, 3, 65, 37, 1, NULL),
(5, 5.00, 5.00, NULL, 3, 68, 40, 1, NULL),
(6, 10.00, 10.00, NULL, 4, 79, 51, 1, NULL),
(7, 2.00, 2.00, NULL, 4, 65, 37, 1, NULL),
(8, 5.00, 5.00, NULL, 4, 68, 40, 1, NULL),
(9, 10.00, 10.00, NULL, 5, 79, 51, 1, NULL),
(10, 2.00, 2.00, NULL, 5, 65, 37, 1, NULL),
(11, 5.00, 5.00, NULL, 5, 68, 40, 1, NULL),
(12, 10.00, 10.00, NULL, 6, 79, 51, 1, NULL),
(13, 2.00, 2.00, NULL, 6, 65, 37, 1, NULL),
(14, 5.00, 5.00, NULL, 6, 68, 40, 1, NULL),
(15, 9.00, 9.00, NULL, 7, 79, 51, 1, NULL),
(16, 1.00, 1.00, NULL, 7, 65, 37, 1, NULL),
(17, 2.00, 2.00, NULL, 7, 68, 40, 1, NULL),
(18, 10.00, 10.00, NULL, 8, 79, 51, 1, NULL),
(19, 2.00, 2.00, NULL, 8, 65, 37, 1, NULL),
(20, 5.00, 5.00, NULL, 8, 68, 40, 1, NULL),
(21, 1.00, 1.00, NULL, 10, 79, 51, 1, NULL),
(22, 1.00, 1.00, NULL, 10, 65, 37, 1, NULL),
(23, 1.00, 1.00, NULL, 10, 68, 40, 1, NULL),
(24, 10.00, 10.00, NULL, 11, 79, 51, 1, NULL),
(25, 2.00, 2.00, NULL, 11, 65, 37, 1, NULL),
(26, 5.00, 5.00, NULL, 11, 68, 40, 1, NULL),
(27, 10.00, 10.00, NULL, 12, 79, 51, 1, NULL),
(28, 2.00, 2.00, NULL, 12, 65, 37, 1, NULL),
(29, 5.00, 5.00, NULL, 12, 68, 40, 1, NULL),
(30, 2.00, 2.00, NULL, 14, 79, 51, 1, NULL),
(31, 1.00, 1.00, NULL, 14, 65, 37, 1, NULL),
(32, 1.00, 1.00, NULL, 14, 68, 40, 1, NULL),
(33, 1.00, 1.00, NULL, 15, 79, 51, 1, NULL),
(34, 1.00, 1.00, NULL, 15, 65, 37, 1, NULL),
(35, 1.00, 1.00, NULL, 15, 68, 40, 1, NULL),
(36, 1.00, 1.00, NULL, 16, 82, 54, 1, NULL),
(37, 1.00, 1.00, NULL, 16, 80, 52, 10, NULL),
(38, 1.00, 1.00, NULL, 16, 67, 39, 1, NULL),
(39, 2.00, 2.00, NULL, 16, 69, 41, 11, NULL),
(40, 1.00, 1.00, NULL, 16, 73, 45, 10, NULL),
(41, 6.00, 6.00, NULL, 17, 82, 54, 1, NULL),
(42, 5.00, 5.00, NULL, 17, 80, 52, 10, NULL),
(43, 4.00, 4.00, NULL, 17, 67, 39, 1, NULL),
(44, 3.00, 3.00, NULL, 17, 69, 41, 11, NULL),
(45, 2.00, 2.00, NULL, 17, 73, 45, 10, NULL),
(46, 6.00, 6.00, NULL, 18, 82, 54, 1, NULL),
(47, 5.00, 5.00, NULL, 18, 80, 52, 10, NULL),
(48, 4.00, 4.00, NULL, 18, 67, 39, 1, NULL),
(49, 3.00, 3.00, NULL, 18, 69, 41, 11, NULL),
(50, 2.00, 2.00, NULL, 18, 73, 45, 10, NULL),
(51, 6.00, 1.00, NULL, 20, 82, 54, 1, NULL),
(52, 5.00, 1.00, NULL, 20, 80, 52, 10, NULL),
(53, 4.00, 1.00, NULL, 20, 67, 39, 1, NULL),
(54, 3.00, 1.00, NULL, 20, 69, 41, 11, NULL),
(55, 2.00, 1.00, NULL, 20, 73, 45, 10, NULL),
(59, 6.00, 6.00, NULL, 22, 82, 54, 1, NULL),
(60, 5.00, 5.00, NULL, 22, 80, 52, 10, NULL),
(61, 4.00, 4.00, NULL, 22, 67, 39, 1, NULL),
(62, 3.00, 3.00, NULL, 22, 69, 41, 11, NULL),
(63, 2.00, 2.00, NULL, 22, 73, 45, 10, NULL),
(64, 6.00, 1.00, NULL, 23, 82, 54, 1, 110000),
(65, 5.00, 1.00, NULL, 23, 80, 52, 10, 130000),
(66, 4.00, 1.00, NULL, 23, 67, 39, 1, 240000),
(67, 3.00, 1.00, NULL, 23, 69, 41, 11, 720000),
(68, 2.00, 2.00, NULL, 23, 73, 45, 10, 560000),
(69, 6.00, 1.00, NULL, 24, 82, 54, 1, 110000),
(70, 5.00, 1.00, NULL, 24, 80, 52, 10, 130000),
(71, 4.00, 1.00, NULL, 24, 67, 39, 1, 240000),
(72, 3.00, 1.00, NULL, 24, 69, 41, 11, 720000),
(73, 2.00, 1.00, NULL, 24, 73, 45, 10, 560000),
(74, 6.00, 6.00, NULL, 25, 82, 54, 1, 110000),
(75, 5.00, 5.00, NULL, 25, 80, 52, 10, 130000),
(76, 4.00, 4.00, NULL, 25, 67, 39, 1, 240000),
(77, 3.00, 3.00, NULL, 25, 69, 41, 11, 720000),
(78, 2.00, 2.00, NULL, 25, 73, 45, 10, 560000),
(79, 6.00, 6.00, NULL, 26, 82, 54, 1, 110000),
(80, 5.00, 5.00, NULL, 26, 80, 52, 10, 130000),
(81, 4.00, 4.00, NULL, 26, 67, 39, 1, 240000),
(82, 3.00, 3.00, NULL, 26, 69, 41, 11, 720000),
(83, 2.00, 2.00, NULL, 26, 73, 45, 10, 560000),
(84, 6.00, 6.00, NULL, 27, 82, 54, 1, 110000),
(85, 5.00, 5.00, NULL, 27, 80, 52, 10, 130000),
(86, 4.00, 4.00, NULL, 27, 67, 39, 1, 240000),
(87, 3.00, 3.00, NULL, 27, 69, 41, 11, 720000),
(88, 2.00, 2.00, NULL, 27, 73, 45, 10, 560000),
(89, 6.00, 6.00, NULL, 28, 82, 54, 1, 110000),
(90, 5.00, 5.00, NULL, 28, 80, 52, 10, 130000),
(91, 4.00, 4.00, NULL, 28, 67, 39, 1, 240000),
(92, 3.00, 3.00, NULL, 28, 69, 41, 11, 720000),
(93, 2.00, 2.00, NULL, 28, 73, 45, 10, 560000),
(94, 6.00, 1.00, NULL, 29, 82, 54, 1, 110000),
(95, 5.00, 1.00, NULL, 29, 80, 52, 10, 130000),
(96, 4.00, 1.00, NULL, 29, 67, 39, 1, 240000),
(97, 3.00, 1.00, NULL, 29, 69, 41, 11, 720000),
(98, 2.00, 1.00, NULL, 29, 73, 45, 10, 560000),
(99, 6.00, 6.00, NULL, 30, 82, 54, 1, 110000),
(100, 5.00, 5.00, NULL, 30, 80, 52, 10, 130000),
(101, 4.00, 4.00, NULL, 30, 67, 39, 1, 240000),
(102, 3.00, 3.00, NULL, 30, 69, 41, 11, 720000),
(103, 2.00, 2.00, NULL, 30, 73, 45, 10, 560000),
(104, 6.00, 2.00, NULL, 31, 82, 54, 1, 110000),
(105, 5.00, 5.00, NULL, 31, 80, 52, 10, 130000),
(106, 4.00, 4.00, NULL, 31, 67, 39, 1, 240000),
(107, 3.00, 3.00, NULL, 31, 69, 41, 11, 720000),
(108, 2.00, 2.00, NULL, 31, 73, 45, 10, 560000),
(109, 6.00, 1.00, NULL, 32, 82, 54, 1, 110000),
(110, 5.00, 1.00, NULL, 32, 80, 52, 10, 130000),
(111, 4.00, 1.00, NULL, 32, 67, 39, 1, 240000),
(112, 3.00, 3.00, NULL, 32, 69, 41, 11, 720000),
(113, 2.00, 2.00, NULL, 32, 73, 45, 10, 560000),
(114, 6.00, 1.00, NULL, 33, 82, 54, 1, 110000),
(115, 5.00, 1.00, NULL, 33, 80, 52, 10, 130000),
(116, 4.00, 1.00, NULL, 33, 67, 39, 1, 240000),
(117, 3.00, 1.00, NULL, 33, 69, 41, 11, 720000),
(118, 2.00, 2.00, NULL, 33, 73, 45, 10, 560000),
(119, 4.00, 4.00, NULL, 34, 67, 39, 1, 240000),
(120, 3.00, 3.00, NULL, 34, 69, 41, 11, 720000),
(121, 2.00, 2.00, NULL, 34, 73, 45, 10, 560000),
(122, 6.00, 6.00, NULL, 35, 82, 54, 1, 110000),
(123, 5.00, 5.00, NULL, 35, 80, 52, 10, 130000),
(124, 1.00, 4.00, NULL, 37, 72, 44, 1, 6000000),
(125, 1.00, 5.00, NULL, 37, 74, 46, 1, 300000),
(126, 2.00, 2.00, NULL, 38, 65, 1, 1, 0),
(127, 5.00, 1.00, NULL, 39, 65, 37, 1, 130000),
(128, 2.00, 1.00, NULL, 40, 65, 37, 1, 4),
(129, 1.00, 1.00, NULL, 41, 68, 40, 1, 0),
(130, 2.00, 1.00, NULL, 41, 65, 37, 1, 0),
(131, 3.00, 1.00, NULL, 42, 68, 40, 1, 30000000),
(132, 2.00, 1.00, NULL, 42, 65, 37, 1, 25000000),
(133, 3.00, 1.00, NULL, 43, 68, 40, 1, 30000000),
(134, 2.00, 1.00, NULL, 43, 65, 37, 1, 25000000),
(135, 7.00, 7.00, NULL, 44, 74, 46, 1, 3000000),
(136, 2.00, 0.00, NULL, 44, 71, 1, 1, 7500000),
(137, 1.00, 1.00, NULL, 44, 65, 37, 1, 25000000),
(138, 10.00, 0.00, NULL, 45, 72, 1, 1, 6000000),
(139, 5.00, 5.00, NULL, 45, 66, 1, 1, 30000000),
(140, 2.00, 0.00, NULL, 46, 75, 1, 1, 7000000),
(141, 1.00, 0.00, NULL, 46, 70, 1, 1, 20000000),
(142, 2.00, 2.00, NULL, 46, 68, 40, 1, 30000000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `products`
--

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `sku` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `min_stock` decimal(10,2) DEFAULT '0.00',
  `category_id` bigint DEFAULT NULL,
  `unit_id` bigint DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `sku` (`sku`),
  KEY `FK6t5dtw6tyo83ywljwohuc6g7k` (`category_id`),
  KEY `FKeex0i50vfsa5imebrfdiyhmp9` (`unit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `products`
--

INSERT INTO `products` (`product_id`, `sku`, `product_name`, `description`, `min_stock`, `category_id`, `unit_id`) VALUES
(10, 'TEST-PRO-001', 'Sản phẩm Test Outbound', NULL, 0.00, 1, 1),
(65, 'LAP-001', 'Laptop Dell XPS 15', 'Core i7 16GB RAM 512GB SSD', 5.00, 1, 1),
(66, 'LAP-002', 'MacBook Air M3', 'Apple M3 8GB RAM 256GB SSD', 10.00, 1, 1),
(67, 'LAP-003', 'Lenovo ThinkPad X1', 'Carbon Gen 11 Core i7', 5.00, 1, 1),
(68, 'PHN-001', 'iPhone 15 Pro Max', 'Titanium 256GB', 10.00, 2, 1),
(69, 'PHN-002', 'Samsung Galaxy S24 Ultra', 'AI Phone 512GB', 10.00, 2, 1),
(70, 'PHN-003', 'Xiaomi 14 Pro', 'Leica Camera 256GB', 15.00, 2, 1),
(71, 'AUD-001', 'Tai nghe Sony WH-1000XM5', 'Chống ồn chủ động cao cấp', 20.00, 3, 1),
(72, 'AUD-002', 'AirPods Pro 2', 'Tai nghe không dây Apple', 30.00, 3, 1),
(73, 'AUD-003', 'Loa Bluetooth Marshall Stanmore III', 'Loa decor công suất lớn', 10.00, 3, 1),
(74, 'ACC-001', 'Cáp sạc Anker PowerLine III', 'Cáp USB-C to USB-C 100W', 50.00, 4, 1),
(75, 'ACC-002', 'Củ sạc Baseus GaN5 Pro', 'Sạc nhanh 65W 3 cổng', 40.00, 4, 1),
(76, 'ACC-003', 'Sạc dự phòng Ugreen 20000mAh', 'Sạc nhanh PD 20W', 30.00, 4, 1),
(77, 'MON-001', 'Màn hình LG UltraGear 27inch', '27GL850 144Hz 1ms', 5.00, 5, 1),
(78, 'MON-002', 'Màn hình Dell UltraSharp 27inch', 'U2723QE 4K USB-C', 5.00, 5, 1),
(79, 'MON-003', 'Giá đỡ màn hình NB F80', 'Arm màn hình 17-30 inch', 20.00, 5, 1),
(80, 'LAP-023', 'Asus ROG Zephyrus G14', 'Ryzen 9 16GB RAM 1TB SSD', 8.00, 1, 1),
(81, 'TAB-001', 'iPad Pro 11 inch M4', 'Apple M4 256GB Wifi', 15.00, 2, 1),
(82, 'ACC-020', 'Chuột Logitech MX Master 3S', 'Chuột không dây công thái học', 20.00, 4, 1),
(83, 'MS-G304', 'Chuột không dây Logitech G304', NULL, 0.00, 2, 1),
(84, 'KB-AK3068', 'Bàn phím cơ Akko 3068', NULL, 0.00, 2, 1),
(85, 'LAP-085', 'Laptop Dell XPS 15', 'Core i7 16GB RAM 512GB SSD', 5.00, 3, 1),
(86, 'TEST IMPORT PRODUCTS', 'TEN IMPORT PRODUCTS', 'MO TA IMPORT', 57.00, 1, 1),
(87, 'TEST-IMPORT-001', 'San pham test import', 'Auto test inbound', 5.00, 1, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_batches`
--

DROP TABLE IF EXISTS `product_batches`;
CREATE TABLE IF NOT EXISTS `product_batches` (
  `batch_id` bigint NOT NULL AUTO_INCREMENT,
  `lot_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `serial_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost_price` decimal(38,2) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `supplier_id` bigint DEFAULT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`batch_id`),
  KEY `FKo2hwf6cltkf4qkdim5w29rbgq` (`product_id`),
  KEY `FKb4fd1gxigcyw8sbgf8lvh5lpd` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `product_batches`
--

INSERT INTO `product_batches` (`batch_id`, `lot_code`, `serial_number`, `cost_price`, `expiry_date`, `supplier_id`, `product_id`) VALUES
(1, 'BATCH-2026-001', NULL, NULL, NULL, 1, 1),
(37, 'LOHANG-T4-01', 'SN-DELL-1001', 35000000.00, '2028-12-31', 1, 65),
(38, 'LOHANG-T4-01', 'SN-MAC-2002', 25000000.00, '2029-01-01', 2, 66),
(39, 'LOHANG-T4-02', 'SN-LEN-3003', 40000000.00, '2028-10-15', 3, 67),
(40, 'LOHANG-T4-02', 'SN-IP-1001', 30000000.00, '2027-09-30', 2, 68),
(41, 'LOHANG-T4-03', 'SN-SAM-2002', 28000000.00, '2027-01-31', 4, 69),
(42, 'LOHANG-T4-03', 'SN-XIA-3003', 20000000.00, '2026-12-31', 5, 70),
(43, 'LOHANG-T4-04', 'SN-SONY-101', 7500000.00, '2026-06-30', 6, 71),
(44, 'LOHANG-T4-04', 'SN-AIR-202', 6000000.00, '2026-12-31', 2, 72),
(45, 'LOHANG-T4-04', 'SN-MAR-303', 9000000.00, '2027-05-15', 7, 73),
(46, 'LOHANG-T4-05', 'SN-ANK-001', 300000.00, NULL, 8, 74),
(47, 'LOHANG-T4-05', 'SN-BAS-002', 600000.00, NULL, 9, 75),
(48, 'LOHANG-T4-05', 'SN-UGR-003', 800000.00, '2028-01-01', 10, 76),
(49, 'LOHANG-T4-06', 'SN-LG-001', 8500000.00, '2029-05-20', 11, 77),
(50, 'LOHANG-T4-06', 'SN-DELL-002', 12000000.00, '2029-08-15', 1, 78),
(51, 'LOHANG-T4-06', NULL, 500000.00, NULL, 12, 79),
(52, 'LOHANG-T4-03', 'SN-ASUS-4004', 32000000.00, '2029-05-20', 4, 80),
(53, 'LOHANG-T4-03', 'SN-IPAD-5005', 24000000.00, '2029-05-20', 2, 81),
(54, 'LOHANG-T4-04', '24000000', 2500000.00, '2029-05-20', 5, 82),
(100, 'LOT-SUMMER-01', NULL, NULL, NULL, 2, 10),
(101, 'LOT-SUMMER-02', NULL, NULL, NULL, 2, 10),
(102, 'LOTTEST-01', NULL, 560000.00, NULL, NULL, 86);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
CREATE TABLE IF NOT EXISTS `product_categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_code` (`category_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `product_categories`
--

INSERT INTO `product_categories` (`category_id`, `category_code`, `category_name`) VALUES
(1, 'DT', 'Điện thoại di động'),
(2, 'PK', 'Phụ kiện công nghệ'),
(3, 'LT', 'Laptop & Máy tính bảng');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `purchase_orders`
--

DROP TABLE IF EXISTS `purchase_orders`;
CREATE TABLE IF NOT EXISTS `purchase_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` bigint NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `expected_delivery_date` datetime DEFAULT NULL,
  `total_amount` decimal(38,2) DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `po_code` (`po_code`),
  KEY `supplier_id` (`supplier_id`),
  KEY `created_by` (`created_by`),
  KEY `FK83rqgbblncfji6cyhcqsyl8hm` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `purchase_orders`
--

INSERT INTO `purchase_orders` (`id`, `po_code`, `supplier_id`, `created_by`, `order_date`, `expected_delivery_date`, `total_amount`, `status`, `notes`, `warehouse_id`) VALUES
(1, 'PO-2026-001', 1, NULL, '2026-04-08 00:32:27', NULL, 0.00, 'OPEN', NULL, NULL),
(10, 'PO-TEST-001', 2, NULL, '2026-04-08 01:08:50', NULL, 0.00, 'OPEN', NULL, NULL),
(11, 'PO-1776519306760', 2, 1, '2026-04-18 20:35:07', NULL, 519000000.00, 'DRAFT', NULL, 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `purchase_order_items`
--

DROP TABLE IF EXISTS `purchase_order_items`;
CREATE TABLE IF NOT EXISTS `purchase_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `ordered_qty` decimal(15,2) NOT NULL,
  `received_qty` decimal(15,2) DEFAULT '0.00' COMMENT 'Số lượng thực tế đã nhập kho',
  `unit_price` decimal(19,4) DEFAULT '0.0000',
  `batch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `po_id` (`po_id`),
  KEY `product_id` (`product_id`),
  KEY `fk_poi_batch` (`batch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `purchase_order_items`
--

INSERT INTO `purchase_order_items` (`id`, `po_id`, `product_id`, `ordered_qty`, `received_qty`, `unit_price`, `batch_id`) VALUES
(1, 1, 1, 100.00, 0.00, 0.0000, 1),
(2, 10, 65, 50.00, 400.00, 500000.0000, 1),
(3, 10, 66, 20.00, 160.00, 1200000.0000, 1),
(4, 11, 78, 50.00, 0.00, 2500000.0000, 50),
(5, 11, 86, 70.00, 0.00, 3000000.0000, 102),
(6, 11, 81, 80.00, 0.00, 2300000.0000, 53);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `qr_codes`
--

DROP TABLE IF EXISTS `qr_codes`;
CREATE TABLE IF NOT EXISTS `qr_codes` (
  `qr_code_id` bigint NOT NULL AUTO_INCREMENT,
  `img_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_printed` bit(1) DEFAULT NULL,
  `qr_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `reference_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`qr_code_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `qr_codes`
--

INSERT INTO `qr_codes` (`qr_code_id`, `img_path`, `is_printed`, `qr_content`, `reference_id`, `reference_type`) VALUES
(35, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 37, 'BATCH'),
(36, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB+UlEQVR4Xu2YMY6DQAxFHaVIyRE4yhwtHI2jcISUKVZ4/W0PIJQiMEgrrb6LyIzfUPg7Hg+i39hL9isfjdjOiO3sz7BJwoo93Wff9xK5qQ4ZINaEdYjoUMz5QbSovh0rWMdGYi3YG6k2TB4Ldo+NEroQuwLrkHOrdouGCsQuxjL5KQexK7Aa7VDbtkXWZvKx1RA7giEgqG3D5pAjnAwQa8FWswEDckivjm0DmwdiB7Fa29Y6kPOqy3PE741YI+YdQ8dsHY5BDh3718PjxBow95HwxJzHc9G8jxBrwKLInxYNJ1UIbP9fIHYQs/PO/My5R7HiKmCQWw5KYqcwGCrZO8aMuSIdq3bxOZlYA1ZVkF5zbOtTDjiyF4vYMSxUQOtw3jAk36JDUeeJtWCT+DGH5KO2o0WDR5Fv7s7ETmFaO0Yftf1cjsIRG6sKxM5hk3jy7QRUbyaOwcEJuBWL2BnMzKcIt9mr3e8jthMqEGvDLNWCzlw0GzJ0sY2wvI8QO48tNnlJo3XEpOG8R4idx6ACrABL3mt7wAoEItaCrV/dvaQH7EkVIkSsCXtLDMOKuSKs9pCodmIXYeghRvlK8GuRE2vCJu8h5ktXVYhbHrEmzNNtKvhXyrjcebXjPoI3EGvB4ErePiCH1ORDoLWHEDuFfWHEdkZsZ/8B+wVBxME5LrwikQAAAABJRU5ErkJggg==', b'0', 'LAP-002|LOHANG-T4-01', 38, 'BATCH'),
(37, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB5UlEQVR4Xu2Zsa3DMAxE9ZHilxnBo2i0eLSM4hFcpgiszztKSmKkCEQDAT7uKlp8akjiJNupfKI17VfeSthOwnb6GrYkV17S7/2nIDzfTlsqc00IC2FnVnrOziN7LsbbCtY3YUHsxmy24MQdjcdGtEPYEVhZ25Bz2oUdjRG5XG3IMdvCjsE42VhsXbCNbzxE2BCGRMIJWA++HtSEsAjWVbtw4cP2nBEWwW4otRXfHKPYkGdbYZAmTwkLY+bMU0HxrQvWDgvh1eyLsBC24pijmEXxoboREjaOLZZAqe1egXZw2sH3sRcWxNw6/ODDbPPKwYA3DWERzN6UU/MQZAu6QH5a2Q5hAQwWjVtxtuwd5x2y3Ijci5MLG8G8CwVDzseFZsKNbi/CxrEazylx2i2YeH+DRft7n7AIRmfGCUjrYODFx0ofcmHDGLuQ3UzQDvJm0em1C8KGMI+vtebGr23sq2kLi2FbuxVb8aEeQMIiWFefdgTImns/PETYGLbUYufiX90tKMTA80wUFsHcQ+aaxUWOQ86+PK7HwkYxZFlzvx5f07OZQMKOwKpjPF7uaoOEHYNB7AK/BQGjaQsLYaw0PASGbNniPzUu5GEmwgJYcmVYND4BpWodcwuEBbAPJGwnYTv9B+wPFmUxXaoX1acAAAAASUVORK5CYII=', b'0', 'LAP-003|LOHANG-T4-02', 39, 'BATCH'),
(38, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=', b'0', 'PHN-001|LOHANG-T4-02', 40, 'BATCH'),
(39, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB60lEQVR4Xu2ZMa6DQAxEHaVIyRE4CkeDo3EUjkCZIsp+z3hZ8VGKJEb60tdMgSz7LcXaml0SK+9otWPmpYQdJOygP8MWCw1lvT0iKPfr08pUC8JSWMednjZsnGvVM66nsCR2b8nHxR9zv95QnYbFDO0Qdg7GgBh4YadiHlxL8SHvV/NA2ElYgdqQM2iZF1Yj7CPMQuzCc2sHgloQlsGaFttZxy4vLIfdDVWzOu3Wc9rZF2FZzPccgjPjXmGjp2+FXm3RF2EJrHWh980Hj4XwEH9Dx5KwDBbXiVKrrp4L0Q4PmkUL+woLZ0a5i/tbXYgh33VBWAobCwwZsz1Xr2ZfhGWxDtYBD+E3XWw+PMRvGnWhsO8xfxiMgsGlTjuevGA8ti4IS2ETndl4AoaZbF4tLInh4Bs9QhKbb7RoYL+GXNgX2OYhHGmr4kI3k0IJ+x6LzcfBZ/Fr8DbtNkSDhKUwftOxGp/MfhSSMnj1Fe8RlsA2LXW2PeBCZHDlEJbBlrrj8JBCrNQzccBRCF5YAgsfnoYSfxW5mfD+1voiLIcx6VipVcjb4Yo3CDsDg3VAc0+vru1oHiIsi/Hjbpyx/xf8kmmvhlzYhxj3fjfkC+8VPAqFpTELEeNs9/HvhmdWtkNYAntDwg4SdtB/wH4ANcOn8mkqs8MAAAAASUVORK5CYII=', b'0', 'PHN-002|LOHANG-T4-03', 41, 'BATCH'),
(40, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB60lEQVR4Xu2YMW7DQAwEGaRwmSfoKXqa9DQ/RU9Q6cIww12ezoqSwrkTECBYFsaBHLu4Xawom79Sqx07P5awQwk71J9hi2WNvl7uefDb+8N8LgNhXdgHb3oOjN3pWqbRiXoI68RupRkqvHlgw3rBdB4XM8gh7BTMeXBgwQs7F4tDNG1yTGl7YWdgjoIKTIzZBjaeHWE9mGUVFXyT4/gEFNaE1Vpsl8y7vrA+7GYIZLPidpgcj0LoIqwbi7Ut9wrPxIjOJT64yPFRKKwfMxvKeuwlQxwHCCSsB4tXOdx5nYYcmEZn2+iEtWNbMkeDh+gwOmDynQrCmjEGBafkB+qCjsH/wrqwuGrjtL47O90em0Z+UVgH5kxmXPqKVzkUv5gLBgUS1oPh70rHqxynjA6EtsHt1eTCWjFe/oQDmvB2xb6YXFgDBidThaV42/JRSDmcJawdS2/H2pZTZnXCYwokrAtjhkQNDjlQVQ66XVgXttVSZqNTBXZytRPWjtXpwsQoWW0lVarJhTVimcPzCLfjzq9pcucvCOvG2AwsXzrw1wT2ClT+grAzsEgMh8mvw16OmiHCurE7350tk3mGDt9NLuyXmKNgcu7J0SlyxEFYN4bLNuwVic2MDmTIGPOnyYU1YS+UsEMJO9R/wD4BPoKDR7tnrmMAAAAASUVORK5CYII=', b'0', 'PHN-003|LOHANG-T4-03', 42, 'BATCH'),
(41, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB8ElEQVR4Xu2YMW7DQAwEaaRw6SfoKXqa9TQ9xU9QqSIIw12ezrbgIhENBAh2C+dEzrkgmb2TzX+ixfaRlxK2k7Cd/gy7WWqMp5BPNvj68YVFSlgJu7DS04jPyG4bHyLCKtjagouh+KHljOw0Bs+IsHdgjgV1/jyhL8LeiN2YDTNB1oW9C3NoG/LQwAAi/spqhP0KQ8ZQ/EsefH3REsIqWFcMeXzaFRsx5HcJq2CrwZDNoviOxeDezCTnX1gFi1vxCQ9RfMNI93bMlkehsArm8OHQsGzZ9JDgL2yQsAJ2a8EZNQce084hv3re6ISVMGZw8Fm+dEQ7YNHZjnsXhB3EcrbjKa/HDfP8BmFFLEYaXbCBB5/jntzfnbkQVsF4zGGkje0w8vHHRnzDQ7OEHcLoGKH06glZb2fi4/+CsCNYG2kIWBQfZgIK7ehDLuwYlpPMbLZjxI7gcQJyt7AC5nQMB4+Xu+QpbuzNEnYMW9KZR2Accph2gtworIRtyiwXUIt0ixZ2CMsgi58jjSyceeoRYQUsfXgacQJymRux4E1DWA1bEQysGXLrAgV7EfYWbKs5wrwVz4M//bAmrITRMcJMcD2O7MTfKLBRWAVD4f0+5HOgYSZ29dworIRZasyLXC8+I0+v2MIOYD+QsJ2E7fQfsG/pi4602wd+zQAAAABJRU5ErkJggg==', b'0', 'AUD-001|LOHANG-T4-04', 43, 'BATCH'),
(42, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB7UlEQVR4Xu2ZMW7DMAxFGWTI6CP4KD5afDQfRUfwmCEoq/8p2a7RIZAMFCg+J1p8zkCyn5Rr/omtdj751YSdTNjJ/gxLFjbh4MvsueDp5j6XgLAubEDc5ykNLxyGA6y8KKwPq1F7vG+eqzD66x4vmt2FXYaht3O3hyPsYmyovb2iHMIuwqqG2AOKMdvoHthPDRHWhFkYqoCcsxx0SkBYD7Ybe3vnDwFhHRjmXSS8RFc+bIucsA4s5ShinijREeXJMq4PRoS1Y9nenID56Y7bR62ClRNhXdh6qAIEuSQ/sP1vQVgTFpcOzLsBewWanHzGEMKLwjowRmHMOZrcY21bLH5BWAeGCfiFnPvK9Tg3eZ2J44piCOvCQjHQ29BqRB1NDtEuJqwD4wSkoaVRjsAcYnIalMIaMNoyOqTDqMxwnmUUCuvBEgtQbsrUapywHNnZqiCsDastnZ847yAdvIZkMVlDtIW1Y4mpzjmvmNX9zSDaqIuwdmyzNCD5cKghxYEJa8dSyfiUmHNsGiEd81RfFNaB7f/xDAzHwE5VENaIvQwTcHIKcrZx2+gO+5uwfozRaZ+JjroIuwRLcfvgVhw8TsAL68GYblYhnGTlS6YfPk0Ia8TgGnub39OiHIiiQLuGCGvCPjBhJxN2sv+AfQN9HISLXh793QAAAABJRU5ErkJggg==', b'0', 'AUD-002|LOHANG-T4-04', 44, 'BATCH'),
(43, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB80lEQVR4Xu2YQW6DQAxFHWXBkiNwlDkaHC1H4QgsWURx/e2BItpFOkaKVP2/SKf2Iwvb+p4g+o4WOUd+FbGTiJ30MWyWUJl71ZfI+MB/N9WpJoilsN4rPRX7eCILPg6Iv4glsRWlRna9q9bSR0TkTuwiTJfuaadR40DsWgzT7h4yxGwTuwZTaOsCDrq6RRfEiSUxCVkXVtQc7YhDTRDLYLtm6RQWXWLaDxliWcyy1aKjHYohr+0glsCi+FD1EBmWDl2Y8AyxJLYZssi+Ci1rfYlV6M8Ta8ZipO3aZlnj7WNYHBPw+AZiGUww0rtUH8NuJuJjTyyBqf9k9h8dAZZtAzq/dYFYI+YjHYYcGoIf6zcQy2Czrzk3ZFwn7CDx5ifs5XtREmvBNCZ5QvHRDn2Ib0BfhR0CxBIYHMOz5hj4a1kvPtpBLI8J5B4ShmwRHHwDHm/RxJqw3q/HpsWvE/EKCBge9FVILIPBkD1YvVpiyDcRy2C7YCaouQ85spj/+9YFYm3YXItd1C8YWIW6ovjm1ZUnlsBiA1rQpt3So4XDOuwbfixKYn/Gas3Dor0dzpvcTIhdht3QjuDVG4QIsYswC+Ei18erNqzC4/s3Yk0YkhVz69i8usCiiSUxCRX19zx2qM5s7ThaNLEm7A0RO4nYSf8B+wIUSHZX9vZNRQAAAABJRU5ErkJggg==', b'0', 'AUD-003|LOHANG-T4-04', 45, 'BATCH'),
(44, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB2UlEQVR4Xu2ZMW7DMAxFWWTImCP4KD5afDQfxUfw6CEIq/8p2YLRIZUMFCj+HxKCfFpIhqId80+02tnzo4SdJOykP8MWC421277cpxwQ1oU9GJrGYpg9NmI8+BbWiW1INbDt9jZ7zoNvtzhodhN2GeavZBmiUQVhl2I5+b7eX8IuwxyiMyc/lYMG/MI6MQuNqMK7lANGDgjrwc7asUPCejCMjpzx1Ns+Z/tJAx5h7die82Qk57Fg+Dysd3wL68OY9BHlCN7usWmkOD3C2jFE8ZmiFt0eyQ/s9FsQ9lss9zZuwLJXrLkKUQ5hPVhyckmbLbBQeIx7srB+bMIbiTJD9m5HMYR1YZ6Tj5YOg0Mb3Z4lrAfjiN6rMI2Fn0tdhPVgqAKXYU7mJw/GG7b12N+EtWHIOVo6TYy473K38was1jxhTVjKuTH5EeXoiJUDB3kVCmvHFt53MTpwKGF5f7Oycghrx3YFz+hmpdsZEdaOLQgYnz4QxYiOYYK6VA93wtqw6h9P9HbCeAP6HhLWhSH5ztHh0eS8E6FH/d+HsF6MA3keHOWonqaFXYAtdKYmDw/FYSKsC3PoaPJsQNWrCWGNGEyLKFp6ssFZFxaoekgR1oJ9IGEnCTvpP2Dfz/krPvLQ4qcAAAAASUVORK5CYII=', b'0', 'ACC-001|LOHANG-T4-05', 46, 'BATCH'),
(45, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB8ElEQVR4Xu2YMa6DQAxEHaVIyRE4So5GjsZROAIlRYS/Z3YhBKXIx0hf+pqpNvajsa1Zb8y/0Wj7yEcJ20nYTn+GDVZ0d5/sEsHeGh4eNSEshTUOPe5DM11nsy4w99kigvgsLIlNKHUEx1uEA2vdn4xEg67CTsMQDLVehlzYuditFr/xp7DTMIcYhLr4ATPBh/7JaoT9CitZi+LX7HKoCWEZbBV4zLa1NJNNRlgOq85M64Amg5msEWEJLPYK1Lxvma08Zht78hVfCTuOLfcdAAThIcjRVd6cXNghjMXvfLBb8OUALvpSzURYAuOSFjVHZubF1yxDXnlhGYxbMdtB3trqzD1cZbFoYQcxZll8GDKsgx9CtR3CjmPQTA9pSs0Rhroe7RCWxlhzFJ8eckfkUm7A15ALO4YNhkdHdAF/TTBbsa43jr2wDOY+BYi1rfBLOwqPvgjLYXDmzpHFoTfyEPsiLIMNm0fHMtvFVXADrl0QdgxbVWYbB2I8bD1E2BFsKAUP6yjBYibFVdgXYSmMf0Ss2eIhWJhf7RCWwSarNUcX4tzWjQ4esumCsBTmzMYrr+WdiLfzWK5CYWdgfNOhHbwTDe1gSlgKQ8WJ8fWBBWOZ9pG7h7AMhtpbfWsgiy5UNe9vZ2G/x76QsJ2E7fQfsB+aM44tgCVscwAAAABJRU5ErkJggg==', b'0', 'ACC-002|LOHANG-T4-05', 47, 'BATCH'),
(46, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB+0lEQVR4Xu2ZMW7DMAxFGWTI6CP4KD5afDQfRUfwmCEoy/+p2q7RIZUCFCj+HwKZes5AflBUYv6KVjtHfpSwk4Sd9GdYsdRUBn9e3O7L6I9rbM51Q1gXNjDT8wT+gkWE6yL0IawTe8Q+d28MhmokXrwKexNWjNhsNqTJhb0Ze8LbNjqaibA3YQ5FMJL/YXbnI8ox1YWwHsxSFdt4uJ0S1oPtWmnpMHkujhuHB2G/xbJjOMc2lCNaB8KLRVc5VEFYC8ZpLaZiG3jw7ZGvbxDWg3ltHdgN8fZhSP4ykhfWg5Xvd7qafOOLVcJ6MOYcnfmG2IxSZK+24TiHCGvBYHJ8hqVzPJ7ylsdendc9YT1YWromPzSymaAufqyCsCaM3jacgAhGDyk7z7oI68H8gRBESzP5sQuTR3sR1omtGIZx8K0chmeaHLvL6MJ6sZLetimeaXK63XkU5uEorAMLk6Nj3B3nXW0mmXzWZSuWsCYsqlC9TZ7Jz7pAnD2EtWObSnqbg1xgGTkUS1gLVmq+p7R0mHx0VCHm5MJJQ1gXlsmfgXEXbjd0Zn7DoYcIa8IYDIxjG5I/bD1k/91SWCdWakOOCJp22D55YW/BvFYBBPhY5J9HwrowhwKr0xpOwGft1cK6MWTdcAIyiFueoy7QcLjcCWvCXpCwk4Sd9B+wT5IpRoZDcjuQAAAAAElFTkSuQmCC', b'0', 'ACC-003|LOHANG-T4-05', 48, 'BATCH'),
(47, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB7UlEQVR4Xu2ZO27DQAxEaaRwqSPoKHs06Wh7FB1BpQojGw5JfSKkcEQBAYKZiuI+uSCJWdqW9o5mOWd+FLGTiJ30Z9gkrtLmZ/vURO1nkUdrYxwQS2GdVXosyn8gkL4thhXk8SKxDLag1Ip1SKoUM95eJHYXJhhynXY99S4QuxXbrOP5InYbtnnI0wKRrjmGR2JJTFxF24Gaj1sQB8Qy2K4ZIy1DC+x4cHgg9ksM911UHDWvEQ0WPIilMPWQF5LWBeWHeNH2ZHMVYhnMSu7OjAVDh1xvQOQKXsS0E0thYR22V+ztMGwfcmKXMJcWvV/NRGfbu+DtIJbCbJLRBTcTVdyAVawvxDLYZOuxoPjG6MX3PUMsiVmp43TfisfSzEyIZbA2mzND5hjjZh11nXZiGczuu6H2MdI1LBqZfX8jdg3TmmNt0/vOa66z3TD2dgPCvYllMP2uYUta9QUDWsceXSCWw6LUo8BDHv7D2iKucG9i17FN02rR6AvMBBk7IXYdm6Li6iFIqTP7s/I4OjSL2BUs1jYk4SHWBXNmfIK5CrEMtojPdizDzacd6o7/fRDLYXYD+toGM8HRwcmJ5TAP8MNas0AKNg3wxDJYg9AFDxTwiw/n+ARiGQyhoOah45C7VxO7jr0hYicRO+k/YF8Owh/2O+UPnQAAAABJRU5ErkJggg==', b'0', 'MON-001|LOHANG-T4-06', 49, 'BATCH'),
(48, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB70lEQVR4Xu2YMY6DQAxFHaVImSNwlDla5mgchSNQpoji9bcNyaItIowUafV/NdhvKGzrz4DoJ5plG/lTxDYittHXsElCbbqqPi0wDrPISbVnglgJu3qle9P5AqzLoPoA1hDHRmIV7I5S9zbJ5ZGF9+IjImdiB2H2dFaVm4XBEzsWsy7YSAOL2SZ2DObxXx5yd4tuyROrYBJq1g7UvK+LTBCrYKuMDw9BX57vGWJVzLoQzmyyITf+1Q5iJQyOgeJLeEjLLnRsIVbFluuEPbmw8GkfB7wEBLHd2OSlDuvwIW+Rs4WlcBQSK2DWBUS7hTDkpiHNxDBvELEClsW/GQpnRjadeQS/NovYPix8uKP44czZBWz0fxTEChhOQEyyGXIsLBzFtzf4RmIlDEPuxRfoZryrN2C+jdhubJLlOjHjvMuF80LsAAyT7M4ct+KGsT/lBcO9mlgBu8bXxxgWbVmNdkAvDyG2FwsPsayPdJdhRvFXEatgq2za/eAbJaYdkdeQE9uHTVnspvnDR2T9Dx8NIlbBrObItuAx7frWBTwQq2D3zLozY8j9kxnKaSd2DOaOYUchEJT//RuQ2CGYW4fGhVlx9yBWwxB0zG9ruGDEoi1nIrEChokWPwF9pLtbBzZa0G90xArYByK2EbGN/gP2A+pNshuxyPW5AAAAAElFTkSuQmCC', b'0', 'MON-002|LOHANG-T4-06', 50, 'BATCH'),
(49, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB5ElEQVR4Xu2ZMY7DMAwEebgiZZ7gp+hp8dP8FD8hZYogPO5SdhLjisA0cMBht6KokQutQFGJ+Se62jbzq4RtJGyjP8NmS7X57P6IxDRczb7cxz4hrISdudNji9F3YKMN7ndgDXksFFbBbtjqES7cESybHxnjQmFHYHDB3S6RPpEXdiQ228mBTZbFRNgxmENw4bSUjhtLdENeWBGzVAs7sOfjGvQJYRVsVecvDl8erzPCqli4kJU5NPgtavXTDmEFbGaJXjefp50ujEgLK2Ihbn57CeBCXIVDt0jYfmw5yVFD2LYtCn42vvKEFbC1dNg5SwcCjtuyUNh+bM43HZRnmyF8mYYrOzphFSweHQ8U5EgulZm+kBdWxGLzDS5EQeYhRzo3f/mCsDo2thhBl0TZvwXGZcJ2Y7PlDz5wgfcdA/CTCTsAw0nGfZedRtjB0pENxrOLFrYP668P3HfJO+1Akl8QVsP6m87XG5B2rBJWwVbhtDva46whzLy9nYXtwCKmWpxtJMf++sAN2DtnYQXsTA8iSTpLNE57uoCBsAp267P8S46z62s6eWEHYQ9ehc4MxWeIsEOwvAFbZL7fGmZhFYzbvbiQNyCDhtMurIhZqrnTDmefjIUxYiCsgH0gYRsJ2+g/YD8PqQezuvA5ywAAAABJRU5ErkJggg==', b'0', 'MON-003|LOHANG-T4-06', 51, 'BATCH'),
(50, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB90lEQVR4Xu2ZMY7DMAwEGaRIeU/wU/Q062l+ip7gMkUQHpeUHMNIkUgGDjjsFgeHHKfQEispJ/qJVjlW3orYQcQO+jOsSCjZ50d9uF+fork2iA1hPwpldGsjuqioPokNYuiiuIpcrLFM6w3dnIoI7CB2ChZDPi8it8cFvhA7EbMHKzomVyV2FqZQc8GSefJCVN5FDbFvMAlVF7TZcdwBiXVhm4pFB1xIVsaQv0RsBLtj47M1r9OOIbeIdl+IDWOrj/K8WFcR0Va52eNrKyQ2gjUX7Nj2wAu++E90LEz8REdsAGuJYRmCbvA25LN/wxbRxLowW3MURSbdFh9vYch3LhDrxppiza1bPxeJ6x6xEcy7OWHIsfjg60Euxp7YAKa+A2o8YNpT2IGs3rlAbAjLliFY/IgOC20rWoXYOIbi7Ly7UPynCccitIn1Y6XugKmFif1BdLgd6iLWj6miW2cbyfw6J8OgzSxifVhc5SDv2i2vYoIX8T3EBrCmIpEhVV7Z352J9WDoQglhIuGCYxmpsg05sU4scjinmHa/fdyBuQv7HZBYF+ZFw+LSocukOFe44kViJ2DVDsc8Ouxh/9MEsUEMxVh8x9DfZQixPswryBBH5lqxrJa3/1Am9hXmTRyGFXaYprhEi7+4DTmxLuwDETuI2EH/AfsFTSiGX4XpAu4AAAAASUVORK5CYII=', b'0', 'LAP-023|LOHANG-T4-03', 52, 'BATCH'),
(51, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB3klEQVR4Xu2YMY7CUAxEP6Kg3CPkKDkaOVqOkiOkpEB4PWMnQLQFiiMhrWYaLPulsa35/9PsE81tm/lTwjYSttHXsKmFerPb+dHalZmT2ZAFYSXsh50eepsv/tuuY/PMoyFjDISVsBtaPfRTu9wZePpMzMeBQNghGAPf9pUXdiCGJaeHxLYLOwhDDhjVQrBoZoQVsSz0Pg70nNvOIAvCKthTM6zD1ZndT28FYRWMhtw6YtztG5ac9bOwIpbOPDYaMoVMLDm2XVgJy2o3w5kR0UNwFPp75DkFYfsxs9GbT+sYO5T4DKGEVbApNvnFOqYo+5kY92RhBWyRP5mBtWXbOY74UFgN4xTinkzrQManYHE4Cqtg2XNzzJPhzJl5PwGF7cAydg+ZcVuDM+dzj0chImEFLGM4RvBwZpgJAuy/sArWUoEhmFfsbcmF7cROcGZ4CKzDxxEWzSUXVsXQ83AMr+SSk0IGHwrbj62aiPluexo8B7ROQdg+bGKNjkHruHom/pro40NhJSyvE/3yF9CCRWmdgrCdGFuNni/O/OIhzyUXVsRYxQnIezKUDxNhB2BmK88rR2OAD4VVMDYcGP+IGPKeTJ53D2EVjK32nifG9ILFtgvbj30gYRsJ2+g/YL8+iBOfwzW5zAAAAABJRU5ErkJggg==', b'0', 'TAB-001|LOHANG-T4-03', 53, 'BATCH'),
(52, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB3UlEQVR4Xu2aMY7CQAxFvdoiJUfgKDlacrQcJUegpEB4/b9nIhRRoDgS0ur/Aib2m8a2PJ6A+Se62d7yVsJ2ErbT17DVUuNqgz/DsODpx31uDmEl7MJIz6P7Pbw2wfIANsKOjcIq2B2hniML9gu+Bz8twk7C4omKxfAQdjaWtQ0sa1vYORjMjiwMucDGNz1E2CHMUvAi5sS4aA5hFWwTBgx3OC4Z/E3Cqhhaxw3zG7E7j8KeDmEFrE/F8ZQHn12dWZixQ1gR8zZXIPj4nBzBj7xMC3juF3YYg/HZvLx9LFcehWjaxluesApmvbZ7kVumAxgtwgpYfLCksYCYDrYOpAOXaGEljGtkAQ3ZcBQ2PiSsiGUlZ/DZQ2KR3i0dwo5jr1lgQ46N+QoIPQTPwgrYmndndgx8w8tqnxYTVsfsRT0LqHZrvLASluPEhCfDOAE9gBmyIKyK4SoXuiYxo5mgyLuEVbBNwXM83mob1b4VubBjWKypET/JJZZzBRbcKKyC5QlIo/Maslp7F9RcwkpYCz4GuZYOBB+68A2bsLMwzhXJY6LjUSjsHMz5sh3/NhniK3i4hFUx+Br25CDHRViimbxkQdghzFKBsXVMyAvvzpbpEFbBPpCwnYTt9B+wPxhNb/pP/+OlAAAAAElFTkSuQmCC', b'0', 'ACC-020|LOHANG-T4-04', 54, 'BATCH'),
(53, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAABnElEQVR4Xu3Qy23EMAyEYXWm1lVSOnA4P6l1VmCMPQTIZWRbj+FHHzSuT8bXOJN2mB3D7Bh/z9ZgzKUvnkiY74pZyzIKdtfS7YpZz1blucqpZ+VeFbMHRkX3mcluMfuUrVy1mD2xXaP+o2dXzFo2GMXenqqYdewer+tMdRfMekYl7pklP90qs1Kzji22WrQRolVZ/cCsYWlGgLePJTvMWlbBFiTMF+Go6zU7WRZWnOtLQWb2G4tAezLKUMr5j8usY4o5Z02cWhHNZg2rI3xJ0TXQtZj1LJ6hIK+3Ghddkbyu1+ydqZo15Ruqm+q+XrOTTckhoDDn6qfLrGMcp2b4sdNi1rFKuVzt8pbjVWnqP2YduyImYkyOOtRumrVs6RwhiQ5qjKNenSI1a5hMyk3U8pLamzUslltWqZpzmLVsj+BrIpmzPT+zhkWmMZEAmtJPQbOWkTNXSiPblU1mLVt3GEudsLHVD8wemBCvIl4hDmYPjI32ddJCu9kv7MpLZI4Km9einVnHdKFDcVBdKtGsmLNZwz4YZscwO8b/sG8xJwTSwF+uawAAAABJRU5ErkJggg==', b'0', 'TEST-PRO-001|LOT-SUMMER-01', 100, 'BATCH'),
(54, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 105, 'SO_PICK'),
(55, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAABnElEQVR4Xu3Qy23EMAyEYXWm1lVSOnA4P6l1VmCMPQTIZWRbj+FHHzSuT8bXOJN2mB3D7Bh/z9ZgzKUvnkiY74pZyzIKdtfS7YpZz1blucqpZ+VeFbMHRkX3mcluMfuUrVy1mD2xXaP+o2dXzFo2GMXenqqYdewer+tMdRfMekYl7pklP90qs1Kzji22WrQRolVZ/cCsYWlGgLePJTvMWlbBFiTMF+Go6zU7WRZWnOtLQWb2G4tAezLKUMr5j8usY4o5Z02cWhHNZg2rI3xJ0TXQtZj1LJ6hIK+3Ghddkbyu1+ydqZo15Ruqm+q+XrOTTckhoDDn6qfLrGMcp2b4sdNi1rFKuVzt8pbjVWnqP2YduyImYkyOOtRumrVs6RwhiQ5qjKNenSI1a5hMyk3U8pLamzUslltWqZpzmLVsj+BrIpmzPT+zhkWmMZEAmtJPQbOWkTNXSiPblU1mLVt3GEudsLHVD8wemBCvIl4hDmYPjI32ddJCu9kv7MpLZI4Km9einVnHdKFDcVBdKtGsmLNZwz4YZscwO8b/sG8xJwTSwF+uawAAAABJRU5ErkJggg==', b'0', 'TEST-PRO-001|LOT-SUMMER-01', 1, 'SO_PICK'),
(56, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 100, 'SO_PICK'),
(57, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 107, 'SO_PICK'),
(58, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=', b'0', 'PHN-001|LOHANG-T4-02', 107, 'SO_PICK'),
(59, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 108, 'SO_PICK'),
(60, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=', b'0', 'PHN-001|LOHANG-T4-02', 108, 'SO_PICK'),
(61, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 110, 'SO_PICK'),
(62, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=', b'0', 'PHN-001|LOHANG-T4-02', 110, 'SO_PICK'),
(63, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAABn0lEQVR4Xu3QQXLDMAwDQP2MX/eT+gOVAEin1TCeHDrTC2Q5lsBlDlz7k/W1zmRcZscyO9bfs2txxRU84ruv4FMVs5EFJoj4/oVFV1XMZnb1cPH2hNlSFbMHJsUXjB+zD1jgKP00XrNm+xUyTYmNIytmI1tcjI+nKmYTuxfz0Dc02lpmM8sMm0PGrgMSfs0mhgtpeT6Q/RdmI+MZN1brlM04iZsNLO+iC4tnaLVqm41MdY14IyaBZc1sZMyhgx0Bo2qXzEaG21XxukdaE2Zu9oYxriNkKOua2cBYgGLhNdOLMY9mI6PIegbMcdOMGfR4zX6zVMGZcnVZLUzM3rAMol/uevFkajaxzbOqyNAkxxv+xWxkDHBVQz7IeDd7y6AW6/3VDyrozpPZwHjDhxl72IprHcwGVmVG0TbQzRZUzAbWK8tS6hTV3WxgGuPqiqasjUEjNJsYahpkpXeyaWu8ZifLKm7YCwK/7OgmsweWV5yRMyBSt9kDw2lzuOsnRcVsZCyqRK5WHBAss5ktrqiGFORLL0OzgX2wzI5ldqz/Yd8yZMUDQN7LWQAAAABJRU5ErkJggg==', b'0', 'TEST IMPORT PRODUCTS|LOTTEST-01', 102, 'BATCH'),
(64, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC', b'0', 'LAP-001|LOHANG-T4-01', 113, 'SO_PICK'),
(65, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB2klEQVR4Xu2ZMW6EQAxFHaXYkiNwFI7GHo2jcARKilUc/++BZAlFMkMUKfq/GuxHY1t/vIv5d7TYMXIqYQcJO+jPsNlSw9z548Vt9LlbX9/M7yUhrAnrWOn7ULI+9Q4+IogjIqwFW1HqwMyIfY0IuwDbah5dEPYL2HLjbFufvLBrMIciSA+xcbJ48cRDhFVhlirYzh9vQGFV2K7ZblHz3DRY/F3CWrAVpY7iGzxkb0eoeLWwBgxBwBMSKboK9mS+KKwJ+5htWgciuPj4M4R7srAWbKt5TDvbgTAOHPvyLKwFQxco1HzqF2S5aTx3QVgdBk2WFj3uQ04eYy+sCXsYVIKc7QfvRLwtrBnzMtIr6HF/cdgaJKwBKzU3DDkP/alFC6vFfDNkT+twrsdj5J/3N2EVWE5ydCH35OiC46+JnHZKWAu2xnyj5o4DnHnZxp4pYY1YKT6z6cw8WHEVYQ3YrtnSmWHRhqzDXrYFQ1gdNiMWGjy/xPlkXI/zBvzk5MKqsGLRA6yDzgyh+C7sCmyNsqPmXX7LiCfMNoXdQ9hFGJy58IY7cRZ2NcY9GXdiLsxnQy7sRxgrDQ+hM/uETSPX4/RqYS2YpQbAxDbrKO0Q1oJ9Q8IOEnbQf8DeAVsB6prDXrV2AAAAAElFTkSuQmCC', b'0', 'AUD-001|MAC_DINH', 113, 'SO_PICK'),
(66, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB2UlEQVR4Xu2ZMW7DMAxFWWTImCP4KD5afDQfxUfw6CEIq/8p2YLRIZUMFCj+HxKCfFpIhqId80+02tnzo4SdJOykP8MWC421277cpxwQ1oU9GJrGYpg9NmI8+BbWiW1INbDt9jZ7zoNvtzhodhN2GeavZBmiUQVhl2I5+b7eX8IuwxyiMyc/lYMG/MI6MQuNqMK7lANGDgjrwc7asUPCejCMjpzx1Ns+Z/tJAx5h7die82Qk57Fg+Dysd3wL68OY9BHlCN7usWmkOD3C2jFE8ZmiFt0eyQ/s9FsQ9lss9zZuwLJXrLkKUQ5hPVhyckmbLbBQeIx7srB+bMIbiTJD9m5HMYR1YZ6Tj5YOg0Mb3Z4lrAfjiN6rMI2Fn0tdhPVgqAKXYU7mJw/GG7b12N+EtWHIOVo6TYy473K38was1jxhTVjKuTH5EeXoiJUDB3kVCmvHFt53MTpwKGF5f7Oycghrx3YFz+hmpdsZEdaOLQgYnz4QxYiOYYK6VA93wtqw6h9P9HbCeAP6HhLWhSH5ztHh0eS8E6FH/d+HsF6MA3keHOWonqaFXYAtdKYmDw/FYSKsC3PoaPJsQNWrCWGNGEyLKFp6ssFZFxaoekgR1oJ9IGEnCTvpP2Dfz/krPvLQ4qcAAAAASUVORK5CYII=', b'0', 'ACC-001|LOHANG-T4-05', 113, 'SO_PICK'),
(67, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB7klEQVR4Xu2YsW3DUAxEabhwmRH+KBrNGk2jaASXKgwzvKOkKEIKRxQQILhr8k2+33wS5EXm7+hh+8iPEraTsJ3+DBst1Y0f7i+cmuPg/ZwQVsIiG+oDmxC9I3wF1iH+ElbEkEUws1mXCy9aRoSdgEU5nhe3+2B2ewo7HTM8fmAte1vYOZhDPapADRbl4EX8ElbELBXYhDcHn4c5IayCbfVkvD1ufPxVwkpYDmT36YpfQ4tVyANm9aYKwg5gI89h26K36StQBaxC+mRhVYwbEMH0bzFDjAaD5cBFYQUsetvgK3w0Bvv4wxkSbR/DBBJWw4xBh53Ibp83YB6EVTAOZGYBYjIzC8thmyoIO4Qtb96NdMVZBdbFmm9dtLBDGDegQ4vB8LwYGHeisAKGbLb0ugrpK1AO+7YBhR3B6NZC2e3M0tFZ51uDIewQtjx+F7+fcMVdYLiGJkc5hFUwCHN4CDRX4VKXnT0WdgSbe/trhuDASIjlEFbAVo3b0cGdCMuxLZaw32MjYswuqxB3YDA6pIQVseWru3MgY4b4ZFkFThVhNYzBwB7Z5Nb4WXiVsFMwzBC0NP53TqB5LkdhJ2CedqIHgN7Og7Aq5lBgtMfQ8i3I2e3CShge2+YNCGhoeUAVsu2FHcfekLCdhO30H7BPeQKqTa79DewAAAAASUVORK5CYII=', b'0', 'LAP-002|BATCH-2026-001', 111, 'SO_PICK'),
(68, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB20lEQVR4Xu2ZMY7CUAxEjSgocwSOkqPB0ThKjrBligivZ/wTSHYLwJFWWs1Uxn6h8LfmO2D+ir5sm/lVwjYSttGfYYOl+vh0jNwVgR0iaAVhJaxjp1E9+Z186G7IZCCshI1o9bWP4NiqLRjMEAjbBcskeQ65sD2xHHIMdgQubC8MucSmeODiUc1TQF5YEbNUP8weMgetIKyCrRRVv1nn0+E5K6yETfAQOy/7m49sPurICCtg4SHA/HZOPpXTvrZoYR9g2NbcsRXHSwcUVdyAdolp56YhrIQ9mg/ruALFg4EZ7kRhFQz3HU8hLz6IY8/jgITVsDyBxtM6TrwBw1VGYVWMSfYbhmwwk/VriLAKxpe7cIwzq8DSTOaSsBJmfKdj82evfrx9YOyFlbFwDGSJDZx2x8Kc0y6sgs3W4djWcq/gtEPghZWwbPUt94rWfKwclrywGoaf0RYPsYtDeQpderWwz7FFQ+5vcRxttts3CKtgiKF+yJ7HkPtomPZe2A7Y8qu7ryyap/DkIcI+xOae8xTSQ2AmoY6LnLC9sAjCQ5qZsPQYcmElLG/AwGLBwHFAmRFWwdhxYE8XHx8MD5l+WrSwtzAmsb/52MKOf2os3yCsgL0gYRsJ2+g/YN/0I69vSLCBswAAAABJRU5ErkJggg==', b'0', 'AUD-002|MAC_DINH', 111, 'SO_PICK'),
(69, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=', b'0', 'PHN-001|LOHANG-T4-02', 115, 'SO_PICK'),
(70, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB4UlEQVR4Xu2YMW7DMAxFFWTI6CP4KDpafLQeRUfw6KEIy/8pO4mbIZVcFCj+nwTqeSGZTyrJ3tGc9pGXEraTsJ3+DCsplMtgnydEy7Ccb8mmeiGsCxtwZVMu6WK3lK4fo5GfMuI3YZ3YglTfscdIQjmEHYFFziFhv4HNEUSTOy/sIMwgD9KZ3UNQhRceIqwJS6GKbfx+AgprwjbBos1i02DyNwnrwRak2pOfLr5XbOVwjXElrANDEDCtI0RXwabBD4X1YQwi+bW3CwcfF7l4jwhrx6BwjLl6SByi7XktrB1DqmtzJ+4V3uRoe24aT8US1oidEBzn9XFXm7xGhPVhS1SgBr0K7syciSbsCIwtbdg0UA64Bts+F7q3sC4MEeQchowqePKTf4iIsAMwDj435MDMraMuGN7kd4sW1oYNWDBCTL6bCUahY8+/BWFt2ALiypzjlOMAr+aVsH4MwapctmOMQmHb8efYpjATPKKpGoGrCGvHSkS9t7lXhEUD4wR8cHJhTVj48JTdOoyjcLT6mkb/C+vFljXnDPqBvQ19m4DC2jEaMiyakZiJwg7D4jZhrziv3f7gIcLaMIPcQyysgxYd6zH/AhLWhdXU57WlPfmogh+iHMJ6sDckbCdhO/0H7AvKt4tUkePoeAAAAABJRU5ErkJggg==', b'0', 'PHN-003|MAC_DINH', 115, 'SO_PICK'),
(71, 'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9ElEQVR4Xu2YMW7DMAxFGWTw6CP4KDpafDQfRUfwmMGIyv+lJKrRITUNFCg+h4AmnzKIxBclK5/YavvIjyZsZ8J29mdYtmoJzqWU2aZyvxY41YSFsJE7Pac83oGVBTnwCfGHsCB29zCCK/KOTdWZUZersJOwjKTZzbEBmy/sXIzdbjf8bmxyYWdgiBNrPD5ZheYIi2BWDRj2nDydlhAWwd62ms8VzsN59BlhIawqRikYMDpsMRu2rgrCjmAMujJnQxXQ2yN5PwrrPwiLYL75BsXApeNRpWNg9rlQWAy7Yq5YPA0MYfY2215YEMv2ujKjpTm/PctRj0JhAazuObK8dBDD2OZanW3gh7DjWB7ZyRzbjL3tAzMXLlMn0cKOYagC/MVaS+Np4ooq2LR+L5aw32O43EExEgK1yfnygybnpCEsgj1PQP/ins/Y/I1aLSyOwYd0eLZOa6jL1sSEKWEBzKtgba6gvbMtJSyCIXhBbxslGkaM1ku0sCPYyzJvHyxHq0JTb2EBzLO05BKNsc0oJjwTsRB/ICyAjfA8CAzdnt5VMN77hIUwBl8YwrzlwVAXYadg7nhLd92esoEXdgpW+ODTTRp+FHYSLewgVoO++VUxlokBPk1gobAQZtUSk2zygroYHoqrqgg7jn1gwnYmbGf/AfsCKkA5uiSA8BEAAAAASUVORK5CYII=', b'0', 'ACC-002|MAC_DINH', 115, 'SO_PICK');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `roles`
--

INSERT INTO `roles` (`role_id`, `role_name`) VALUES
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MANAGER'),
(3, 'ROLE_STAFF'),
(4, 'ROLE_USER');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `sales_orders`
--

DROP TABLE IF EXISTS `sales_orders`;
CREATE TABLE IF NOT EXISTS `sales_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `so_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `total_amount` decimal(19,4) DEFAULT '0.0000',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `payment_status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'UNPAID',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'COD hoặc TRANSFER',
  `payment_confirmed_at` datetime(6) DEFAULT NULL,
  `payment_confirmed_by` bigint DEFAULT NULL,
  `payment_note` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `payment_transaction_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `so_code` (`so_code`),
  KEY `fk_so_customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `sales_orders`
--

INSERT INTO `sales_orders` (`id`, `so_code`, `customer_id`, `customer_name`, `status`, `total_amount`, `created_at`, `updated_at`, `payment_status`, `payment_method`, `payment_confirmed_at`, `payment_confirmed_by`, `payment_note`, `payment_transaction_code`) VALUES
(1, 'SO-2026-TEST', NULL, 'Khách hàng Test', 'DRAFT', 0.0000, '2026-04-08 14:18:51', '2026-04-17 15:37:57', 'PAID', 'DEBT', NULL, NULL, NULL, NULL),
(100, 'SO-2026-PICK-TEST', NULL, 'Khách hàng Test Gợi Ý', 'PENDING', 0.0000, '2026-04-08 14:30:07', '2026-04-08 14:30:07', 'PAID', 'TRANSFER', NULL, NULL, NULL, NULL),
(101, 'SO-TEST-999', 1, 'Khách hàng Test SOP', 'PENDING', 50000000.0000, '2026-04-16 21:25:41', '2026-04-16 21:28:53', 'PAID', 'TRANSFER', '2026-04-17 03:07:11.518579', 1, 'xuất kho gấp', '12'),
(102, 'SO-TEST-FULL-001', 1, 'Khach Test Full', 'PENDING', 0.0000, '2026-04-17 02:48:57', '2026-04-17 02:53:25', 'PAID', 'DEBT', NULL, NULL, NULL, NULL),
(103, 'SO-TEST-PARTIAL-001', 1, 'Khach Test Partial', 'PARTIAL', 0.0000, '2026-04-17 02:48:57', '2026-04-17 02:50:24', 'PAID', 'TRANSFER', NULL, NULL, NULL, NULL),
(104, 'SO-TEST-SHORT-001', 1, 'Khach Test Short', 'PENDING', 0.0000, '2026-04-17 02:48:57', '2026-04-17 02:48:57', 'PAID', 'TRANSFER', NULL, NULL, NULL, NULL),
(105, 'SO-TEST-20260417031417', NULL, 'Khach Test SQL', 'PENDING', 2000000.0000, '2026-04-17 03:14:17', '2026-04-17 03:46:28', 'PAID', 'CASH', '2026-04-17 03:44:38.673479', 1, '2', '2'),
(107, 'SO-TRAMTEST-20260417-01', 1, 'Khach test Tram truong', 'PENDING', 100000000.0000, '2026-04-17 13:35:32', '2026-04-17 13:45:13', 'PAID', 'CASH', '2026-04-17 13:43:53.685229', 1, '1', '1'),
(108, 'SO-2026-MOCK01', 3, 'Cong ty TNHH Ban Le Sao Mai', 'PENDING', 140000000.0000, '2026-04-17 14:39:12', '2026-04-17 15:01:34', 'PAID', 'CASH', '2026-04-17 14:40:35.440454', 1, 'thanh toan r', '1'),
(110, 'SO-2026-MOCK012', 3, 'Cong ty duc dat', 'PENDING', 140000000.0000, '2026-04-17 15:03:41', '2026-04-17 15:05:16', 'PAID', 'TRANSFER', '2026-04-17 15:04:22.963900', 1, 'jh', '65'),
(111, 'SO-2026-MOCK02', 4, 'Cong ty CP Thuong Mai Hoang Gia', 'PENDING', 210000000.0000, '2026-04-17 15:14:58', '2026-04-17 15:50:46', 'PAID', 'CASH', '2026-04-17 15:49:35.591569', 1, 'tien mst', '12'),
(112, 'SO-2026-MOCK03', 5, 'Cua hang Thiet bi An Phat', 'DRAFT', 92500000.0000, '2026-04-17 15:15:04', '2026-04-17 15:38:58', 'PAID', 'CASH', NULL, NULL, NULL, NULL),
(113, 'SO-2026-RAW01', 1, 'Công ty Công nghệ Việt Nam', 'PENDING', 61000000.0000, '2026-04-17 15:18:09', '2026-04-17 15:43:49', 'PAID', 'TRANSFER', '2026-04-17 15:41:03.726693', 1, '000', '12'),
(114, 'SO-2026-RAW02', 3, 'Cong ty TNHH Ban Le Sao Mai', 'DRAFT', 120000000.0000, '2026-04-17 15:18:10', '2026-04-17 15:38:32', 'UNPAID', 'CASH', NULL, NULL, NULL, NULL),
(115, 'SO-2026-RAW03', 6, 'Sieu thi Mini Minh Chau', 'PENDING', 94000000.0000, '2026-04-17 15:18:10', '2026-04-17 16:31:03', 'PAID', 'CASH', '2026-04-17 16:29:58.836676', 1, 'tm', 'gh'),
(116, 'SO-2026-RAW04', 8, 'Nha phan phoi Phuong Nam', 'DRAFT', 212000000.0000, '2026-04-17 15:18:10', '2026-04-17 15:38:26', 'UNPAID', 'CASH', NULL, NULL, NULL, NULL),
(117, 'SO-2026-RAW05', 10, 'Cua hang Gia dung Thanh Cong', 'DRAFT', 53000000.0000, '2026-04-17 15:18:10', '2026-04-17 15:38:20', 'UNPAID', 'TRANSFER', NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `sales_order_items`
--

DROP TABLE IF EXISTS `sales_order_items`;
CREATE TABLE IF NOT EXISTS `sales_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `so_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` decimal(15,2) NOT NULL,
  `shipped_qty` decimal(15,2) DEFAULT '0.00',
  `unit_price` decimal(19,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `FKbrb0ia68jv5x0ev89yoogec9i` (`so_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `sales_order_items`
--

INSERT INTO `sales_order_items` (`id`, `so_id`, `product_id`, `quantity`, `shipped_qty`, `unit_price`) VALUES
(1, 1, 10, 5.00, 0.00, NULL),
(2, 100, 65, 5.00, 0.00, NULL),
(3, 101, 65, 2.00, 2.00, 25000000.0000),
(4, 102, 65, 6.00, 0.00, 25000000.0000),
(5, 103, 65, 8.00, 4.00, 25000000.0000),
(6, 104, 72, 5.00, 0.00, 6000000.0000),
(7, 105, 65, 2.00, 1.00, 1000000.0000),
(8, 107, 65, 2.00, 1.00, 35000000.0000),
(9, 107, 68, 1.00, 1.00, 30000000.0000),
(10, 108, 65, 2.00, 1.00, 25000000.0000),
(11, 108, 68, 3.00, 1.00, 30000000.0000),
(12, 110, 65, 2.00, 1.00, 25000000.0000),
(13, 110, 68, 3.00, 1.00, 30000000.0000),
(14, 111, 66, 5.00, 5.00, 30000000.0000),
(15, 111, 72, 10.00, 0.00, 6000000.0000),
(16, 112, 67, 2.00, 0.00, 35000000.0000),
(17, 112, 71, 3.00, 0.00, 7500000.0000),
(18, 113, 65, 1.00, 1.00, 25000000.0000),
(19, 113, 71, 2.00, 0.00, 7500000.0000),
(20, 113, 74, 7.00, 7.00, 3000000.0000),
(21, 114, 66, 4.00, 0.00, 30000000.0000),
(22, 115, 68, 2.00, 2.00, 30000000.0000),
(23, 115, 70, 1.00, 0.00, 20000000.0000),
(24, 115, 75, 2.00, 0.00, 7000000.0000),
(25, 116, 69, 5.00, 0.00, 28000000.0000),
(26, 116, 72, 12.00, 0.00, 6000000.0000),
(27, 117, 67, 1.00, 0.00, 35000000.0000),
(28, 117, 73, 2.00, 0.00, 9000000.0000);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `stocktake_items`
--

DROP TABLE IF EXISTS `stocktake_items`;
CREATE TABLE IF NOT EXISTS `stocktake_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `system_qty` decimal(38,2) DEFAULT NULL,
  `actual_qty` decimal(38,2) DEFAULT NULL,
  `variance_qty` decimal(38,2) DEFAULT NULL,
  `location_id` bigint DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `location_id` (`location_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `stocktake_sessions`
--

DROP TABLE IF EXISTS `stocktake_sessions`;
CREATE TABLE IF NOT EXISTS `stocktake_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `session_code` (`session_code`),
  KEY `created_by` (`created_by`),
  KEY `warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
CREATE TABLE IF NOT EXISTS `suppliers` (
  `supplier_id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `supplier_code` (`supplier_code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `supplier_code`, `supplier_name`, `contact_person`, `phone`, `email`, `address`, `is_active`) VALUES
(1, 'NCC-001', 'Nhà Cung Cấp Mặc Định', NULL, NULL, NULL, NULL, 1),
(2, 'SUP001', 'Công ty TNHH Thiết Bị Sao Việt', 'Nguyễn Văn An', '0901234567', 'an.nguyen@saoviet.vn', '12 Nguyễn Trãi, Q1, TP.HCM', 1),
(3, 'SUP002', 'Công ty CP Phân Phối Minh Long', 'Trần Thị Bình', '0912345678', 'binh.tran@minhlong.vn', '88 Điện Biên Phủ, Bình Thạnh, TP.HCM', 1),
(4, 'SUP003', 'Công ty TNHH Vật Tư Đông Á', 'Lê Quốc Cường', '0923456789', 'cuong.le@donga.vn', '45 Lê Lợi, Hải Châu, Đà Nẵng', 1),
(5, 'SUP004', 'Công ty CP Logistics Hưng Phát', 'Phạm Thu Dung', '0934567890', 'dung.pham@hungphat.vn', '102 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', 1),
(6, 'SUP005', 'Công ty TNHH TM Nam Khánh', 'Võ Minh Đức', '0945678901', 'duc.vo@namkhanh.vn', '27 Phan Chu Trinh, Ninh Kiều, Cần Thơ', 1),
(7, 'SUP006', 'Công ty CP Công Nghiệp Đại Việt', 'Đỗ Gia Huy', '0956789012', 'huy.do@daiviet.vn', '156 Hùng Vương, TP. Thủ Đức, TP.HCM', 1),
(8, 'SUP007', 'Công ty TNHH Kỹ Thuật Tân Tiến', 'Ngô Thị Lan', '0967890123', 'lan.ngo@tantien.vn', '9 Lý Thường Kiệt, TP. Vũng Tàu', 1),
(9, 'SUP008', 'Công ty CP Thương Mại Á Châu', 'Bùi Văn Nam', '0978901234', 'nam.bui@achau.vn', '63 Nguyễn Huệ, Huế', 0);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `temp_import_data`
--

DROP TABLE IF EXISTS `temp_import_data`;
CREATE TABLE IF NOT EXISTS `temp_import_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sku` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `min_stock` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `category_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `batch_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `serial_num` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost_price` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expiry_date` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `supplier_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `quantity` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validation_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `validation_message` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `import_session_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `transfer_orders`
--

DROP TABLE IF EXISTS `transfer_orders`;
CREATE TABLE IF NOT EXISTS `transfer_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transfer_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_warehouse_id` bigint NOT NULL,
  `outbound_receipt_id` bigint DEFAULT NULL,
  `to_warehouse_id` bigint NOT NULL,
  `transfer_date` datetime DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transfer_code` (`transfer_code`),
  KEY `from_warehouse_id` (`from_warehouse_id`),
  KEY `outbound_receipt_id` (`outbound_receipt_id`),
  KEY `to_warehouse_id` (`to_warehouse_id`),
  KEY `created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `transfer_order_items`
--

DROP TABLE IF EXISTS `transfer_order_items`;
CREATE TABLE IF NOT EXISTS `transfer_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qty` decimal(38,2) DEFAULT NULL,
  `from_location_id` bigint NOT NULL,
  `to_location_id` bigint DEFAULT NULL,
  `to_warehouse_id` bigint DEFAULT NULL,
  `transfer_order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `from_location_id` (`from_location_id`),
  KEY `to_location_id` (`to_location_id`),
  KEY `to_warehouse_id` (`to_warehouse_id`),
  KEY `transfer_order_id` (`transfer_order_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `units`
--

DROP TABLE IF EXISTS `units`;
CREATE TABLE IF NOT EXISTS `units` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT,
  `unit_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `unit_name` (`unit_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `units`
--

INSERT INTO `units` (`unit_id`, `unit_name`) VALUES
(1, 'Chiếc'),
(2, 'Hộp'),
(3, 'Thùng');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cccd` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`user_id`, `username`, `password`, `full_name`, `email`, `phone`, `role_id`, `avatar`, `cccd`, `created_at`, `date_of_birth`, `gender`, `status`, `updated_at`) VALUES
(1, 'admin', '$2a$10$oC37m/7Qk1CxfGpdYrES8e570OQX3L9GGmD3qR4.AgNiVxB/CzCQa', 'Administrator', 'admin@example.com', '0123456789', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 'manager', '$2a$10$HsE3UuGkO7iZTNmhYPjE3.s3X2uvitDRl8fcrknP7k0AU3t3quDVS', 'Manager User123', 'manager@warehouse.com', '0987654321', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, 'staff', '$2a$10$HC7kAEHRXY2NFEIFHBhaSufIRo7SjQbYpZZYMa53JgO8rvoFfhVy2', 'Staff Employee', 'staff@warehouse.com', '0111222333', 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, 'user', '$2a$10$GEy42Lpg3PkQD9M6OLOew.40cTk5hmgCqziv2FkZJs6HuZIQPZ2ym', 'Normal User', 'user@example.com', '0444555666', 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(8, 'áđá', '$2a$10$7t.ZeZeWzEQWr1PStCvLh.mVWu7VVxuewURTF8kuJ5DIFJJDhIMWu', 'Leo Messi222', 'test@gmail.com', '0444555666', 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
CREATE TABLE IF NOT EXISTS `warehouses` (
  `warehouse_id` bigint NOT NULL AUTO_INCREMENT,
  `warehouse_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `warehouse_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `warehouse_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`warehouse_id`),
  UNIQUE KEY `warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `warehouses`
--

INSERT INTO `warehouses` (`warehouse_id`, `warehouse_code`, `warehouse_name`, `address`, `warehouse_address`) VALUES
(1, '', 'Kho Tổng', NULL, NULL),
(2, 'testcode', 'testname', '600 QUận 7', NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `warehouse_locations`
--

DROP TABLE IF EXISTS `warehouse_locations`;
CREATE TABLE IF NOT EXISTS `warehouse_locations` (
  `location_id` bigint NOT NULL AUTO_INCREMENT,
  `location_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `zone_id` bigint DEFAULT NULL,
  `qr_code_id` bigint DEFAULT NULL,
  `aisle_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bin_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `capacity` int DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rack_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `used_capacity` int DEFAULT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_code` (`location_code`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `zone_id` (`zone_id`),
  KEY `qr_code_id` (`qr_code_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `warehouse_locations`
--

INSERT INTO `warehouse_locations` (`location_id`, `location_code`, `warehouse_id`, `zone_id`, `qr_code_id`, `aisle_code`, `bin_code`, `capacity`, `description`, `rack_code`, `status`, `used_capacity`) VALUES
(1, 'LOC-DEFAULT-01', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(10, 'KE-A-01', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(11, 'KE-B-02', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `warehouse_zones`
--

DROP TABLE IF EXISTS `warehouse_zones`;
CREATE TABLE IF NOT EXISTS `warehouse_zones` (
  `zone_id` bigint NOT NULL AUTO_INCREMENT,
  `zone_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `zone_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  `warehouses` bigint NOT NULL,
  PRIMARY KEY (`zone_id`),
  UNIQUE KEY `zone_code` (`zone_code`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `FKrasb229u17v9pwhmy2qsed0dp` (`warehouses`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `inbound_receipts`
--
ALTER TABLE `inbound_receipts`
  ADD CONSTRAINT `fk_inbound_po` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`),
  ADD CONSTRAINT `fk_inbound_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  ADD CONSTRAINT `fk_inbound_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_inbound_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `inbound_receipt_items`
--
ALTER TABLE `inbound_receipt_items`
  ADD CONSTRAINT `fk_ini_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_ini_location` FOREIGN KEY (`putaway_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_ini_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_ini_receipt` FOREIGN KEY (`inbound_receipt_id`) REFERENCES `inbound_receipts` (`id`);

--
-- Các ràng buộc cho bảng `inventory_history`
--
ALTER TABLE `inventory_history`
  ADD CONSTRAINT `fk_history_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_history_from_loc` FOREIGN KEY (`from_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_history_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_history_qr` FOREIGN KEY (`qr_code_id`) REFERENCES `qr_codes` (`qr_code_id`),
  ADD CONSTRAINT `fk_history_to_loc` FOREIGN KEY (`to_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_history_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `inventory_location_balances`
--
ALTER TABLE `inventory_location_balances`
  ADD CONSTRAINT `fk_balance_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_balance_location` FOREIGN KEY (`location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_balance_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_balance_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `outbound_receipts`
--
ALTER TABLE `outbound_receipts`
  ADD CONSTRAINT `fk_outbound_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  ADD CONSTRAINT `fk_outbound_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_outbound_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `outbound_receipt_items`
--
ALTER TABLE `outbound_receipt_items`
  ADD CONSTRAINT `fk_outi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_outi_location` FOREIGN KEY (`picked_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_outi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_outi_receipt` FOREIGN KEY (`outbound_receipt_id`) REFERENCES `outbound_receipts` (`id`);

--
-- Các ràng buộc cho bảng `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`category_id`),
  ADD CONSTRAINT `fk_product_unit` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`);

--
-- Các ràng buộc cho bảng `product_batches`
--
ALTER TABLE `product_batches`
  ADD CONSTRAINT `fk_batch_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_batch_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`);

--
-- Các ràng buộc cho bảng `purchase_orders`
--
ALTER TABLE `purchase_orders`
  ADD CONSTRAINT `FK83rqgbblncfji6cyhcqsyl8hm` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `fk_po_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  ADD CONSTRAINT `fk_po_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Các ràng buộc cho bảng `purchase_order_items`
--
ALTER TABLE `purchase_order_items`
  ADD CONSTRAINT `fk_poi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_poi_po` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`id`),
  ADD CONSTRAINT `fk_poi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Các ràng buộc cho bảng `sales_orders`
--
ALTER TABLE `sales_orders`
  ADD CONSTRAINT `fk_so_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`);

--
-- Các ràng buộc cho bảng `sales_order_items`
--
ALTER TABLE `sales_order_items`
  ADD CONSTRAINT `fk_soi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_soi_so` FOREIGN KEY (`so_id`) REFERENCES `sales_orders` (`id`);

--
-- Các ràng buộc cho bảng `stocktake_items`
--
ALTER TABLE `stocktake_items`
  ADD CONSTRAINT `fk_sti_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_sti_location` FOREIGN KEY (`location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_sti_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_sti_session` FOREIGN KEY (`session_id`) REFERENCES `stocktake_sessions` (`id`);

--
-- Các ràng buộc cho bảng `stocktake_sessions`
--
ALTER TABLE `stocktake_sessions`
  ADD CONSTRAINT `fk_stock_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `fk_stock_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `transfer_orders`
--
ALTER TABLE `transfer_orders`
  ADD CONSTRAINT `fk_transfer_from_wh` FOREIGN KEY (`from_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `fk_transfer_outbound` FOREIGN KEY (`outbound_receipt_id`) REFERENCES `outbound_receipts` (`id`),
  ADD CONSTRAINT `fk_transfer_to_wh` FOREIGN KEY (`to_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `fk_transfer_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`);

--
-- Các ràng buộc cho bảng `transfer_order_items`
--
ALTER TABLE `transfer_order_items`
  ADD CONSTRAINT `fk_tfi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  ADD CONSTRAINT `fk_tfi_from_loc` FOREIGN KEY (`from_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_tfi_order` FOREIGN KEY (`transfer_order_id`) REFERENCES `transfer_orders` (`id`),
  ADD CONSTRAINT `fk_tfi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `fk_tfi_to_loc` FOREIGN KEY (`to_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  ADD CONSTRAINT `fk_tfi_to_wh` FOREIGN KEY (`to_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- Các ràng buộc cho bảng `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

--
-- Các ràng buộc cho bảng `warehouse_locations`
--
ALTER TABLE `warehouse_locations`
  ADD CONSTRAINT `fk_location_qr` FOREIGN KEY (`qr_code_id`) REFERENCES `qr_codes` (`qr_code_id`),
  ADD CONSTRAINT `fk_location_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `fk_location_zone` FOREIGN KEY (`zone_id`) REFERENCES `warehouse_zones` (`zone_id`);

--
-- Các ràng buộc cho bảng `warehouse_zones`
--
ALTER TABLE `warehouse_zones`
  ADD CONSTRAINT `fk_zone_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `FKrasb229u17v9pwhmy2qsed0dp` FOREIGN KEY (`warehouses`) REFERENCES `warehouses` (`warehouse_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
