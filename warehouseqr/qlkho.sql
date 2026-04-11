CREATE DATABASE  IF NOT EXISTS `quan_ly_kho` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `quan_ly_kho`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: quan_ly_kho
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `companies`
--

DROP TABLE IF EXISTS `companies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `companies` (
  `company_id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tax_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `companies`
--

LOCK TABLES `companies` WRITE;
/*!40000 ALTER TABLE `companies` DISABLE KEYS */;
/*!40000 ALTER TABLE `companies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_code` (`customer_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inbound_receipt_items`
--

DROP TABLE IF EXISTS `inbound_receipt_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inbound_receipt_items` (
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
  KEY `putaway_location_id` (`putaway_location_id`),
  CONSTRAINT `fk_ini_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_ini_location` FOREIGN KEY (`putaway_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_ini_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_ini_receipt` FOREIGN KEY (`inbound_receipt_id`) REFERENCES `inbound_receipts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inbound_receipt_items`
--

LOCK TABLES `inbound_receipt_items` WRITE;
/*!40000 ALTER TABLE `inbound_receipt_items` DISABLE KEYS */;
INSERT INTO `inbound_receipt_items` VALUES (1,50.00,50.00,NULL,15,65,1,1),(2,20.00,20.00,NULL,15,66,1,1),(3,50.00,50.00,NULL,16,65,1,1),(4,20.00,20.00,NULL,16,66,1,1),(5,50.00,50.00,NULL,17,65,1,1),(6,20.00,20.00,NULL,17,66,1,1),(7,50.00,50.00,NULL,18,65,1,1),(8,20.00,20.00,NULL,18,66,1,1),(9,50.00,50.00,NULL,19,65,1,1),(10,20.00,20.00,NULL,19,66,1,1),(11,50.00,50.00,NULL,20,65,1,1),(12,20.00,20.00,NULL,20,66,1,1),(13,50.00,50.00,NULL,21,65,1,1),(14,20.00,20.00,NULL,21,66,1,1),(15,50.00,50.00,NULL,22,65,1,1),(16,20.00,20.00,NULL,22,66,1,1);
/*!40000 ALTER TABLE `inbound_receipt_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inbound_receipts`
--

DROP TABLE IF EXISTS `inbound_receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inbound_receipts` (
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
  KEY `fk_inbound_po` (`purchase_order_id`),
  CONSTRAINT `fk_inbound_po` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`),
  CONSTRAINT `fk_inbound_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  CONSTRAINT `fk_inbound_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_inbound_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inbound_receipts`
--

LOCK TABLES `inbound_receipts` WRITE;
/*!40000 ALTER TABLE `inbound_receipts` DISABLE KEYS */;
INSERT INTO `inbound_receipts` VALUES (1,'PN-1775582442324',NULL,'2026-04-08 00:20:42','2026-04-08 00:20:42','COMPLETED',NULL,1,NULL,NULL),(2,'PN-1775582663588',NULL,'2026-04-08 00:24:24','2026-04-08 00:24:24','COMPLETED',1,1,1,NULL),(3,'PN-1775582669170',NULL,'2026-04-08 00:24:29','2026-04-08 00:24:29','COMPLETED',1,1,1,NULL),(4,'PN-1775582903409',NULL,'2026-04-08 00:28:24','2026-04-08 00:28:24','COMPLETED',1,1,1,NULL),(5,'PN-1775583717992',NULL,'2026-04-08 00:41:58','2026-04-08 00:41:58','COMPLETED',1,1,1,NULL),(6,'PN-1775583730487',NULL,'2026-04-08 00:42:10','2026-04-08 00:42:10','COMPLETED',1,1,1,NULL),(7,'PN-1775583756571',NULL,'2026-04-08 00:42:37','2026-04-08 00:42:37','COMPLETED',1,1,1,NULL),(8,'PN-1775586937905',NULL,'2026-04-08 01:35:38','2026-04-08 01:35:38','COMPLETED',1,1,10,NULL),(9,'PN-1775587174066',NULL,'2026-04-08 01:39:34','2026-04-08 01:39:34','COMPLETED',1,1,10,NULL),(10,'PN-1775587183438',NULL,'2026-04-08 01:39:43','2026-04-08 01:39:43','COMPLETED',1,1,10,NULL),(11,'PN-1775587192194',NULL,'2026-04-08 01:39:52','2026-04-08 01:39:52','COMPLETED',1,1,10,NULL),(12,'PN-1775587387220',NULL,'2026-04-08 01:43:07','2026-04-08 01:43:07','COMPLETED',1,1,10,NULL),(13,'PN-1775587486636',NULL,'2026-04-08 01:44:47','2026-04-08 01:44:47','COMPLETED',1,1,10,NULL),(14,'PN-1775587796819',NULL,'2026-04-08 01:49:57','2026-04-08 01:49:57','COMPLETED',1,1,10,NULL),(15,'PN-1775587918137',NULL,'2026-04-08 01:51:58','2026-04-08 01:51:58','COMPLETED',1,1,10,NULL),(16,'PN-1775589063754',NULL,'2026-04-08 02:11:04','2026-04-08 02:11:04','COMPLETED',1,1,10,NULL),(17,'PN-1775590538670',NULL,'2026-04-08 02:35:39','2026-04-08 02:35:39','COMPLETED',1,1,10,NULL),(18,'PN-1775623346342',NULL,'2026-04-08 11:42:27','2026-04-08 11:42:27','COMPLETED',1,1,10,NULL),(19,'PN-1775633636389',NULL,'2026-04-08 14:33:56','2026-04-08 14:33:56','COMPLETED',1,1,10,NULL),(20,'PN-1775645030009',NULL,'2026-04-08 17:43:50','2026-04-08 17:43:50','COMPLETED',1,1,10,NULL),(21,'PN-1775645053381',NULL,'2026-04-08 17:44:13','2026-04-08 17:44:13','COMPLETED',1,1,10,NULL),(22,'PN-1775645902660',NULL,'2026-04-08 17:58:23','2026-04-08 17:58:23','COMPLETED',1,1,10,NULL);
/*!40000 ALTER TABLE `inbound_receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_history`
--

DROP TABLE IF EXISTS `inventory_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_history` (
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
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_history_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_history_from_loc` FOREIGN KEY (`from_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_history_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_history_qr` FOREIGN KEY (`qr_code_id`) REFERENCES `qr_codes` (`qr_code_id`),
  CONSTRAINT `fk_history_to_loc` FOREIGN KEY (`to_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_history_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_history_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_history`
--

LOCK TABLES `inventory_history` WRITE;
/*!40000 ALTER TABLE `inventory_history` DISABLE KEYS */;
INSERT INTO `inventory_history` VALUES (1,'INBOUND',NULL,1,50.00,'2026-04-08 01:51:58',NULL,NULL,65,1,NULL),(2,'INBOUND',NULL,1,20.00,'2026-04-08 01:51:58',NULL,NULL,66,1,NULL),(3,'INBOUND',NULL,1,50.00,'2026-04-08 02:11:03',NULL,NULL,65,1,NULL),(4,'INBOUND',NULL,1,20.00,'2026-04-08 02:11:03',NULL,NULL,66,1,NULL),(5,'INBOUND',NULL,1,50.00,'2026-04-08 02:35:38',NULL,NULL,65,1,NULL),(6,'INBOUND',NULL,1,20.00,'2026-04-08 02:35:38',NULL,NULL,66,1,NULL),(7,'INBOUND',NULL,1,50.00,'2026-04-08 11:42:26',NULL,NULL,65,1,NULL),(8,'INBOUND',NULL,1,20.00,'2026-04-08 11:42:27',NULL,NULL,66,1,NULL),(9,'INBOUND',NULL,1,50.00,'2026-04-08 14:33:56',NULL,NULL,65,1,NULL),(10,'INBOUND',NULL,1,20.00,'2026-04-08 14:33:56',NULL,NULL,66,1,NULL),(11,'OUTBOUND',1,NULL,-5.00,'2026-04-08 14:34:13',NULL,1,65,1,NULL),(12,'OUTBOUND',10,NULL,-10.00,'2026-04-08 14:34:59',NULL,37,65,1,NULL),(13,'INBOUND',NULL,1,50.00,'2026-04-08 17:43:50',NULL,NULL,65,1,NULL),(14,'INBOUND',NULL,1,20.00,'2026-04-08 17:43:50',NULL,NULL,66,1,NULL),(15,'INBOUND',NULL,1,50.00,'2026-04-08 17:44:13',NULL,NULL,65,1,NULL),(16,'INBOUND',NULL,1,20.00,'2026-04-08 17:44:13',NULL,NULL,66,1,NULL),(17,'INBOUND',NULL,1,50.00,'2026-04-08 17:58:22',NULL,NULL,65,1,NULL),(18,'INBOUND',NULL,1,20.00,'2026-04-08 17:58:22',NULL,NULL,66,1,NULL);
/*!40000 ALTER TABLE `inventory_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_location_balances`
--

DROP TABLE IF EXISTS `inventory_location_balances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_location_balances` (
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
  KEY `batch_id` (`batch_id`),
  CONSTRAINT `fk_balance_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_balance_location` FOREIGN KEY (`location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_balance_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_balance_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_location_balances`
--

LOCK TABLES `inventory_location_balances` WRITE;
/*!40000 ALTER TABLE `inventory_location_balances` DISABLE KEYS */;
INSERT INTO `inventory_location_balances` VALUES (1,150.00,'2026-04-08 17:58:22',NULL,1,1,65,1),(2,60.00,'2026-04-08 17:58:22',NULL,1,1,66,1);
/*!40000 ALTER TABLE `inventory_location_balances` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outbound_receipt_items`
--

DROP TABLE IF EXISTS `outbound_receipt_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outbound_receipt_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `requested_qty` decimal(38,2) DEFAULT NULL,
  `actual_qty` decimal(38,2) DEFAULT NULL,
  `selling_price` decimal(15,2) DEFAULT NULL,
  `outbound_receipt_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  `picked_location_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `outbound_receipt_id` (`outbound_receipt_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`),
  KEY `picked_location_id` (`picked_location_id`),
  CONSTRAINT `fk_outi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_outi_location` FOREIGN KEY (`picked_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_outi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_outi_receipt` FOREIGN KEY (`outbound_receipt_id`) REFERENCES `outbound_receipts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outbound_receipt_items`
--

LOCK TABLES `outbound_receipt_items` WRITE;
/*!40000 ALTER TABLE `outbound_receipt_items` DISABLE KEYS */;
INSERT INTO `outbound_receipt_items` VALUES (1,5.00,5.00,NULL,1,65,1,1),(2,5.00,10.00,NULL,2,65,37,10);
/*!40000 ALTER TABLE `outbound_receipt_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outbound_receipts`
--

DROP TABLE IF EXISTS `outbound_receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `outbound_receipts` (
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
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `fk_outbound_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`),
  CONSTRAINT `fk_outbound_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_outbound_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outbound_receipts`
--

LOCK TABLES `outbound_receipts` WRITE;
/*!40000 ALTER TABLE `outbound_receipts` DISABLE KEYS */;
INSERT INTO `outbound_receipts` VALUES (1,'PX-1775633653471','SHIPPED',1,'2026-04-08 14:34:13','2026-04-08 14:34:13',1,1),(2,'PX-1775633699688','SHIPPED',1,'2026-04-08 14:35:00','2026-04-08 14:35:00',1,1);
/*!40000 ALTER TABLE `outbound_receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_batches`
--

DROP TABLE IF EXISTS `product_batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_batches` (
  `batch_id` bigint NOT NULL AUTO_INCREMENT,
  `lot_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `serial_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cost_price` decimal(38,2) DEFAULT NULL,
  `expiry_date` date DEFAULT NULL,
  `supplier_id` bigint DEFAULT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`batch_id`),
  KEY `FKo2hwf6cltkf4qkdim5w29rbgq` (`product_id`),
  KEY `FKb4fd1gxigcyw8sbgf8lvh5lpd` (`supplier_id`),
  CONSTRAINT `fk_batch_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_batch_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_batches`
--

LOCK TABLES `product_batches` WRITE;
/*!40000 ALTER TABLE `product_batches` DISABLE KEYS */;
INSERT INTO `product_batches` VALUES (1,'BATCH-2026-001',NULL,NULL,NULL,1,1),(37,'LOHANG-T4-01','SN-DELL-1001',35000000.00,'2028-12-31',1,65),(38,'LOHANG-T4-01','SN-MAC-2002',25000000.00,'2029-01-01',2,66),(39,'LOHANG-T4-02','SN-LEN-3003',40000000.00,'2028-10-15',3,67),(40,'LOHANG-T4-02','SN-IP-1001',30000000.00,'2027-09-30',2,68),(41,'LOHANG-T4-03','SN-SAM-2002',28000000.00,'2027-01-31',4,69),(42,'LOHANG-T4-03','SN-XIA-3003',20000000.00,'2026-12-31',5,70),(43,'LOHANG-T4-04','SN-SONY-101',7500000.00,'2026-06-30',6,71),(44,'LOHANG-T4-04','SN-AIR-202',6000000.00,'2026-12-31',2,72),(45,'LOHANG-T4-04','SN-MAR-303',9000000.00,'2027-05-15',7,73),(46,'LOHANG-T4-05','SN-ANK-001',300000.00,NULL,8,74),(47,'LOHANG-T4-05','SN-BAS-002',600000.00,NULL,9,75),(48,'LOHANG-T4-05','SN-UGR-003',800000.00,'2028-01-01',10,76),(49,'LOHANG-T4-06','SN-LG-001',8500000.00,'2029-05-20',11,77),(50,'LOHANG-T4-06','SN-DELL-002',12000000.00,'2029-08-15',1,78),(51,'LOHANG-T4-06',NULL,500000.00,NULL,12,79),(52,'LOHANG-T4-03','SN-ASUS-4004',32000000.00,'2029-05-20',4,80),(53,'LOHANG-T4-03','SN-IPAD-5005',24000000.00,'2029-05-20',2,81),(54,'LOHANG-T4-04','24000000',2500000.00,'2029-05-20',5,82),(100,'LOT-SUMMER-01',NULL,NULL,NULL,2,10),(101,'LOT-SUMMER-02',NULL,NULL,NULL,2,10);
/*!40000 ALTER TABLE `product_batches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `category_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_code` (`category_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,'DT','Điện thoại di động'),(2,'PK','Phụ kiện công nghệ'),(3,'LT','Laptop & Máy tính bảng');
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
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
  KEY `FKeex0i50vfsa5imebrfdiyhmp9` (`unit_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`category_id`),
  CONSTRAINT `fk_product_unit` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (10,'TEST-PRO-001','Sản phẩm Test Outbound',NULL,0.00,1,1),(65,'LAP-001','Laptop Dell XPS 15','Core i7 16GB RAM 512GB SSD',5.00,1,1),(66,'LAP-002','MacBook Air M3','Apple M3 8GB RAM 256GB SSD',10.00,1,1),(67,'LAP-003','Lenovo ThinkPad X1','Carbon Gen 11 Core i7',5.00,1,1),(68,'PHN-001','iPhone 15 Pro Max','Titanium 256GB',10.00,2,1),(69,'PHN-002','Samsung Galaxy S24 Ultra','AI Phone 512GB',10.00,2,1),(70,'PHN-003','Xiaomi 14 Pro','Leica Camera 256GB',15.00,2,1),(71,'AUD-001','Tai nghe Sony WH-1000XM5','Chống ồn chủ động cao cấp',20.00,3,1),(72,'AUD-002','AirPods Pro 2','Tai nghe không dây Apple',30.00,3,1),(73,'AUD-003','Loa Bluetooth Marshall Stanmore III','Loa decor công suất lớn',10.00,3,1),(74,'ACC-001','Cáp sạc Anker PowerLine III','Cáp USB-C to USB-C 100W',50.00,4,1),(75,'ACC-002','Củ sạc Baseus GaN5 Pro','Sạc nhanh 65W 3 cổng',40.00,4,1),(76,'ACC-003','Sạc dự phòng Ugreen 20000mAh','Sạc nhanh PD 20W',30.00,4,1),(77,'MON-001','Màn hình LG UltraGear 27inch','27GL850 144Hz 1ms',5.00,5,1),(78,'MON-002','Màn hình Dell UltraSharp 27inch','U2723QE 4K USB-C',5.00,5,1),(79,'MON-003','Giá đỡ màn hình NB F80','Arm màn hình 17-30 inch',20.00,5,1),(80,'LAP-023','Asus ROG Zephyrus G14','Ryzen 9 16GB RAM 1TB SSD',8.00,1,1),(81,'TAB-001','iPad Pro 11 inch M4','Apple M4 256GB Wifi',15.00,2,1),(82,'ACC-020','Chuột Logitech MX Master 3S','Chuột không dây công thái học',20.00,4,1),(83,'MS-G304','Chuột không dây Logitech G304',NULL,0.00,2,1),(84,'KB-AK3068','Bàn phím cơ Akko 3068',NULL,0.00,2,1),(85,'LAP-085','Laptop Dell XPS 15','Core i7 16GB RAM 512GB SSD',5.00,3,1);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_order_items`
--

DROP TABLE IF EXISTS `purchase_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_order_items` (
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
  KEY `fk_poi_batch` (`batch_id`),
  CONSTRAINT `fk_poi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_poi_po` FOREIGN KEY (`po_id`) REFERENCES `purchase_orders` (`id`),
  CONSTRAINT `fk_poi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_order_items`
--

LOCK TABLES `purchase_order_items` WRITE;
/*!40000 ALTER TABLE `purchase_order_items` DISABLE KEYS */;
INSERT INTO `purchase_order_items` VALUES (1,1,1,100.00,0.00,0.0000,1),(2,10,65,50.00,400.00,500000.0000,1),(3,10,66,20.00,160.00,1200000.0000,1);
/*!40000 ALTER TABLE `purchase_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_orders`
--

DROP TABLE IF EXISTS `purchase_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `po_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `supplier_id` bigint NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `expected_delivery_date` datetime DEFAULT NULL,
  `total_amount` decimal(19,4) DEFAULT '0.0000',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT 'DRAFT, APPROVED, PARTIAL, COMPLETED, CANCELLED',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `po_code` (`po_code`),
  KEY `supplier_id` (`supplier_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `fk_po_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  CONSTRAINT `fk_po_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_orders`
--

LOCK TABLES `purchase_orders` WRITE;
/*!40000 ALTER TABLE `purchase_orders` DISABLE KEYS */;
INSERT INTO `purchase_orders` VALUES (1,'PO-2026-001',1,NULL,'2026-04-08 00:32:27',NULL,0.0000,'OPEN',NULL),(10,'PO-TEST-001',2,NULL,'2026-04-08 01:08:50',NULL,0.0000,'OPEN',NULL);
/*!40000 ALTER TABLE `purchase_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qr_codes`
--

DROP TABLE IF EXISTS `qr_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qr_codes` (
  `qr_code_id` bigint NOT NULL AUTO_INCREMENT,
  `img_path` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `is_printed` bit(1) DEFAULT NULL,
  `qr_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reference_id` bigint DEFAULT NULL,
  `reference_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`qr_code_id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qr_codes`
--

LOCK TABLES `qr_codes` WRITE;
/*!40000 ALTER TABLE `qr_codes` DISABLE KEYS */;
INSERT INTO `qr_codes` VALUES (35,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB9UlEQVR4Xu2YO27DQAxEabhwqSPoKDqafDQdRUdQqSIwwxmu/FFS2LsCAgQzhUGTTy7IAXdl83e02D7zq4TtJGynP8NmSw3u6/nG5xazk/u1FIQ1YR0qfh3mbmUV/Ba440FhLdiKVm+YjY5x5INmnIuwQzD0PNwe1ZyCsMMxro7Ll7DDMIeAwdsB9e6JIS+sEbMUMUzhHpSCsBbsocW4mWMKxJ4LT1+EfYgVb8fqoKW3uYwTPk/CGjFuDJ+sQxAXjMAwDp/65cK6sHoMK/qWGwMHX2k+NESdbhfWgtHkrNLtXNEMPN/yhLVgxhgHH8aRmEHDHHN5PiiFfY6h1YgnlnDB8OLtKW0vrAVDz0+bt7lM+iXdjoC/IKweg8LbI/iowu3p7RwHysLqsUg6mh9JVMsJCG8joNuF1WNRBYbbWvY8LnLlTHy55gmrwnIKsUOc92RMIe/JsVV+DEvYpxiqhh0S385YJlOfvBmmIKwNm9lqTIGWZvO5TEIch7AW7K7i9jHC1XCvYAYSVo/N2XBuDB58kcYUeNPIjLAGrGMpkiXAM2esjq0krAlbjT2Ht9Hzqc+lHery/2FhR2A8AR1ihnpscmFtWFraXsRlIqwJQxVTYM/zgsEU3kfwC8JaMIQhYiXO5mNAjx0irAp7Q8J2ErbTf8C+AS1EQZtWovLnAAAAAElFTkSuQmCC',_binary '\0','LAP-001|LOHANG-T4-01',37,'BATCH'),(36,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB+UlEQVR4Xu2YMY6DQAxFHaVIyRE4yhwtHI2jcISUKVZ4/W0PIJQiMEgrrb6LyIzfUPg7Hg+i39hL9isfjdjOiO3sz7BJwoo93Wff9xK5qQ4ZINaEdYjoUMz5QbSovh0rWMdGYi3YG6k2TB4Ldo+NEroQuwLrkHOrdouGCsQuxjL5KQexK7Aa7VDbtkXWZvKx1RA7giEgqG3D5pAjnAwQa8FWswEDckivjm0DmwdiB7Fa29Y6kPOqy3PE741YI+YdQ8dsHY5BDh3718PjxBow95HwxJzHc9G8jxBrwKLInxYNJ1UIbP9fIHYQs/PO/My5R7HiKmCQWw5KYqcwGCrZO8aMuSIdq3bxOZlYA1ZVkF5zbOtTDjiyF4vYMSxUQOtw3jAk36JDUeeJtWCT+DGH5KO2o0WDR5Fv7s7ETmFaO0Yftf1cjsIRG6sKxM5hk3jy7QRUbyaOwcEJuBWL2BnMzKcIt9mr3e8jthMqEGvDLNWCzlw0GzJ0sY2wvI8QO48tNnlJo3XEpOG8R4idx6ACrABL3mt7wAoEItaCrV/dvaQH7EkVIkSsCXtLDMOKuSKs9pCodmIXYeghRvlK8GuRE2vCJu8h5ktXVYhbHrEmzNNtKvhXyrjcebXjPoI3EGvB4ErePiCH1ORDoLWHEDuFfWHEdkZsZ/8B+wVBxME5LrwikQAAAABJRU5ErkJggg==',_binary '\0','LAP-002|LOHANG-T4-01',38,'BATCH'),(37,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB5UlEQVR4Xu2Zsa3DMAxE9ZHilxnBo2i0eLSM4hFcpgiszztKSmKkCEQDAT7uKlp8akjiJNupfKI17VfeSthOwnb6GrYkV17S7/2nIDzfTlsqc00IC2FnVnrOziN7LsbbCtY3YUHsxmy24MQdjcdGtEPYEVhZ25Bz2oUdjRG5XG3IMdvCjsE42VhsXbCNbzxE2BCGRMIJWA++HtSEsAjWVbtw4cP2nBEWwW4otRXfHKPYkGdbYZAmTwkLY+bMU0HxrQvWDgvh1eyLsBC24pijmEXxoboREjaOLZZAqe1egXZw2sH3sRcWxNw6/ODDbPPKwYA3DWERzN6UU/MQZAu6QH5a2Q5hAQwWjVtxtuwd5x2y3Ijci5MLG8G8CwVDzseFZsKNbi/CxrEazylx2i2YeH+DRft7n7AIRmfGCUjrYODFx0ofcmHDGLuQ3UzQDvJm0em1C8KGMI+vtebGr23sq2kLi2FbuxVb8aEeQMIiWFefdgTImns/PETYGLbUYufiX90tKMTA80wUFsHcQ+aaxUWOQ86+PK7HwkYxZFlzvx5f07OZQMKOwKpjPF7uaoOEHYNB7AK/BQGjaQsLYaw0PASGbNniPzUu5GEmwgJYcmVYND4BpWodcwuEBbAPJGwnYTv9B+wPFmUxXaoX1acAAAAASUVORK5CYII=',_binary '\0','LAP-003|LOHANG-T4-02',39,'BATCH'),(38,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB6ElEQVR4Xu2ZQW7CUAxEXbFgmSPkKByNHI2j5Agss0C4nvFPWqIsII5UqRovKst+dOGx5v8P5u/E3daVzRC2CmGr+DNstIzL2Pnjy5F20+lpPrSGsBLWcdJDYFNrkI8K6k9hRQxdYjNPNhPIIewIzJ2J3/r7OVUQdiR2N6OH9M7dFnYM5ggUzw9D9CHHhocI24WxxRMwD74laQ1hFWyJUCFmbtebdTn8JYRVsAmjjuFbesglKidPM2FLWAEbadEx/Dj4cviQI7CQw3gUCitg8ac5RvNq66NCrGdBWAlzqBDXNswcSVMh1/4krIZhyfM6wSXntjcVLFvCSpi34XfNmUOOmaccwgpYRnaTDzkisSuTWQVhezF4CIq8Vzjl4KcuEEhYDRsN1zYYcm57mEnyrDCEFTBaR56AaR2WH8wzcVFB2F7M5uEbv/n54V9VELYLy/yGfj7u0pkH8DRtYTUsZs7uvORM5hBWwZZIHnKkV0fll4cI24eNbdihAh8dEd3yBWbywgpYRw2GC+4VWHLPbacuL487YbuwKQbPmb/eNCAH/4OwQzA6hvHJHLvN94iwAzEELRpygPetJRf2GcZJh4dw+EgYocK49YOysM8wDj/vb9hts769nQ1mghC2H3sjhK1C2Cr+A/YNCJelzWLopJkAAAAASUVORK5CYII=',_binary '\0','PHN-001|LOHANG-T4-02',40,'BATCH'),(39,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB60lEQVR4Xu2ZMa6DQAxEHaVIyRE4CkeDo3EUjkCZIsp+z3hZ8VGKJEb60tdMgSz7LcXaml0SK+9otWPmpYQdJOygP8MWCw1lvT0iKPfr08pUC8JSWMednjZsnGvVM66nsCR2b8nHxR9zv95QnYbFDO0Qdg7GgBh4YadiHlxL8SHvV/NA2ElYgdqQM2iZF1Yj7CPMQuzCc2sHgloQlsGaFttZxy4vLIfdDVWzOu3Wc9rZF2FZzPccgjPjXmGjp2+FXm3RF2EJrHWh980Hj4XwEH9Dx5KwDBbXiVKrrp4L0Q4PmkUL+woLZ0a5i/tbXYgh33VBWAobCwwZsz1Xr2ZfhGWxDtYBD+E3XWw+PMRvGnWhsO8xfxiMgsGlTjuevGA8ti4IS2ETndl4AoaZbF4tLInh4Bs9QhKbb7RoYL+GXNgX2OYhHGmr4kI3k0IJ+x6LzcfBZ/Fr8DbtNkSDhKUwftOxGp/MfhSSMnj1Fe8RlsA2LXW2PeBCZHDlEJbBlrrj8JBCrNQzccBRCF5YAgsfnoYSfxW5mfD+1voiLIcx6VipVcjb4Yo3CDsDg3VAc0+vru1oHiIsi/Hjbpyx/xf8kmmvhlzYhxj3fjfkC+8VPAqFpTELEeNs9/HvhmdWtkNYAntDwg4SdtB/wH4ANcOn8mkqs8MAAAAASUVORK5CYII=',_binary '\0','PHN-002|LOHANG-T4-03',41,'BATCH'),(40,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB60lEQVR4Xu2YMW7DQAwEGaRwmSfoKXqa9DQ/RU9Q6cIww12ezoqSwrkTECBYFsaBHLu4Xawom79Sqx07P5awQwk71J9hi2WNvl7uefDb+8N8LgNhXdgHb3oOjN3pWqbRiXoI68RupRkqvHlgw3rBdB4XM8gh7BTMeXBgwQs7F4tDNG1yTGl7YWdgjoIKTIzZBjaeHWE9mGUVFXyT4/gEFNaE1Vpsl8y7vrA+7GYIZLPidpgcj0LoIqwbi7Ut9wrPxIjOJT64yPFRKKwfMxvKeuwlQxwHCCSsB4tXOdx5nYYcmEZn2+iEtWNbMkeDh+gwOmDynQrCmjEGBafkB+qCjsH/wrqwuGrjtL47O90em0Z+UVgH5kxmXPqKVzkUv5gLBgUS1oPh70rHqxynjA6EtsHt1eTCWjFe/oQDmvB2xb6YXFgDBidThaV42/JRSDmcJawdS2/H2pZTZnXCYwokrAtjhkQNDjlQVQ66XVgXttVSZqNTBXZytRPWjtXpwsQoWW0lVarJhTVimcPzCLfjzq9pcucvCOvG2AwsXzrw1wT2ClT+grAzsEgMh8mvw16OmiHCurE7350tk3mGDt9NLuyXmKNgcu7J0SlyxEFYN4bLNuwVic2MDmTIGPOnyYU1YS+UsEMJO9R/wD4BPoKDR7tnrmMAAAAASUVORK5CYII=',_binary '\0','PHN-003|LOHANG-T4-03',42,'BATCH'),(41,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB8ElEQVR4Xu2YMW7DQAwEaaRw6SfoKXqa9TQ9xU9QqSIIw12ezrbgIhENBAh2C+dEzrkgmb2TzX+ixfaRlxK2k7Cd/gy7WWqMp5BPNvj68YVFSlgJu7DS04jPyG4bHyLCKtjagouh+KHljOw0Bs+IsHdgjgV1/jyhL8LeiN2YDTNB1oW9C3NoG/LQwAAi/spqhP0KQ8ZQ/EsefH3REsIqWFcMeXzaFRsx5HcJq2CrwZDNoviOxeDezCTnX1gFi1vxCQ9RfMNI93bMlkehsArm8OHQsGzZ9JDgL2yQsAJ2a8EZNQce084hv3re6ISVMGZw8Fm+dEQ7YNHZjnsXhB3EcrbjKa/HDfP8BmFFLEYaXbCBB5/jntzfnbkQVsF4zGGkje0w8vHHRnzDQ7OEHcLoGKH06glZb2fi4/+CsCNYG2kIWBQfZgIK7ehDLuwYlpPMbLZjxI7gcQJyt7AC5nQMB4+Xu+QpbuzNEnYMW9KZR2Accph2gtworIRtyiwXUIt0ixZ2CMsgi58jjSyceeoRYQUsfXgacQJymRux4E1DWA1bEQysGXLrAgV7EfYWbKs5wrwVz4M//bAmrITRMcJMcD2O7MTfKLBRWAVD4f0+5HOgYSZ29dworIRZasyLXC8+I0+v2MIOYD+QsJ2E7fQfsG/pi4602wd+zQAAAABJRU5ErkJggg==',_binary '\0','AUD-001|LOHANG-T4-04',43,'BATCH'),(42,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB7UlEQVR4Xu2ZMW7DMAxFGWTI6CP4KD5afDQfRUfwmCEoq/8p2a7RIZAMFCg+J1p8zkCyn5Rr/omtdj751YSdTNjJ/gxLFjbh4MvsueDp5j6XgLAubEDc5ykNLxyGA6y8KKwPq1F7vG+eqzD66x4vmt2FXYaht3O3hyPsYmyovb2iHMIuwqqG2AOKMdvoHthPDRHWhFkYqoCcsxx0SkBYD7Ybe3vnDwFhHRjmXSS8RFc+bIucsA4s5ShinijREeXJMq4PRoS1Y9nenID56Y7bR62ClRNhXdh6qAIEuSQ/sP1vQVgTFpcOzLsBewWanHzGEMKLwjowRmHMOZrcY21bLH5BWAeGCfiFnPvK9Tg3eZ2J44piCOvCQjHQ29BqRB1NDtEuJqwD4wSkoaVRjsAcYnIalMIaMNoyOqTDqMxwnmUUCuvBEgtQbsrUapywHNnZqiCsDastnZ847yAdvIZkMVlDtIW1Y4mpzjmvmNX9zSDaqIuwdmyzNCD5cKghxYEJa8dSyfiUmHNsGiEd81RfFNaB7f/xDAzHwE5VENaIvQwTcHIKcrZx2+gO+5uwfozRaZ+JjroIuwRLcfvgVhw8TsAL68GYblYhnGTlS6YfPk0Ia8TgGnub39OiHIiiQLuGCGvCPjBhJxN2sv+AfQN9HISLXh793QAAAABJRU5ErkJggg==',_binary '\0','AUD-002|LOHANG-T4-04',44,'BATCH'),(43,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB80lEQVR4Xu2YQW6DQAxFHWXBkiNwlDkaHC1H4QgsWURx/e2BItpFOkaKVP2/SKf2Iwvb+p4g+o4WOUd+FbGTiJ30MWyWUJl71ZfI+MB/N9WpJoilsN4rPRX7eCILPg6Iv4glsRWlRna9q9bSR0TkTuwiTJfuaadR40DsWgzT7h4yxGwTuwZTaOsCDrq6RRfEiSUxCVkXVtQc7YhDTRDLYLtm6RQWXWLaDxliWcyy1aKjHYohr+0glsCi+FD1EBmWDl2Y8AyxJLYZssi+Ci1rfYlV6M8Ta8ZipO3aZlnj7WNYHBPw+AZiGUww0rtUH8NuJuJjTyyBqf9k9h8dAZZtAzq/dYFYI+YjHYYcGoIf6zcQy2Czrzk3ZFwn7CDx5ifs5XtREmvBNCZ5QvHRDn2Ib0BfhR0CxBIYHMOz5hj4a1kvPtpBLI8J5B4ShmwRHHwDHm/RxJqw3q/HpsWvE/EKCBge9FVILIPBkD1YvVpiyDcRy2C7YCaouQ85spj/+9YFYm3YXItd1C8YWIW6ovjm1ZUnlsBiA1rQpt3So4XDOuwbfixKYn/Gas3Dor0dzpvcTIhdht3QjuDVG4QIsYswC+Ei18erNqzC4/s3Yk0YkhVz69i8usCiiSUxCRX19zx2qM5s7ThaNLEm7A0RO4nYSf8B+wIUSHZX9vZNRQAAAABJRU5ErkJggg==',_binary '\0','AUD-003|LOHANG-T4-04',45,'BATCH'),(44,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB2UlEQVR4Xu2ZMW7DMAxFWWTImCP4KD5afDQfxUfw6CEIq/8p2YLRIZUMFCj+HxKCfFpIhqId80+02tnzo4SdJOykP8MWC421277cpxwQ1oU9GJrGYpg9NmI8+BbWiW1INbDt9jZ7zoNvtzhodhN2GeavZBmiUQVhl2I5+b7eX8IuwxyiMyc/lYMG/MI6MQuNqMK7lANGDgjrwc7asUPCejCMjpzx1Ns+Z/tJAx5h7die82Qk57Fg+Dysd3wL68OY9BHlCN7usWmkOD3C2jFE8ZmiFt0eyQ/s9FsQ9lss9zZuwLJXrLkKUQ5hPVhyckmbLbBQeIx7srB+bMIbiTJD9m5HMYR1YZ6Tj5YOg0Mb3Z4lrAfjiN6rMI2Fn0tdhPVgqAKXYU7mJw/GG7b12N+EtWHIOVo6TYy473K38was1jxhTVjKuTH5EeXoiJUDB3kVCmvHFt53MTpwKGF5f7Oycghrx3YFz+hmpdsZEdaOLQgYnz4QxYiOYYK6VA93wtqw6h9P9HbCeAP6HhLWhSH5ztHh0eS8E6FH/d+HsF6MA3keHOWonqaFXYAtdKYmDw/FYSKsC3PoaPJsQNWrCWGNGEyLKFp6ssFZFxaoekgR1oJ9IGEnCTvpP2Dfz/krPvLQ4qcAAAAASUVORK5CYII=',_binary '\0','ACC-001|LOHANG-T4-05',46,'BATCH'),(45,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB8ElEQVR4Xu2YMa6DQAxEHaVIyRE4So5GjsZROAIlRYS/Z3YhBKXIx0hf+pqpNvajsa1Zb8y/0Wj7yEcJ20nYTn+GDVZ0d5/sEsHeGh4eNSEshTUOPe5DM11nsy4w99kigvgsLIlNKHUEx1uEA2vdn4xEg67CTsMQDLVehlzYuditFr/xp7DTMIcYhLr4ATPBh/7JaoT9CitZi+LX7HKoCWEZbBV4zLa1NJNNRlgOq85M64Amg5msEWEJLPYK1Lxvma08Zht78hVfCTuOLfcdAAThIcjRVd6cXNghjMXvfLBb8OUALvpSzURYAuOSFjVHZubF1yxDXnlhGYxbMdtB3trqzD1cZbFoYQcxZll8GDKsgx9CtR3CjmPQTA9pSs0Rhroe7RCWxlhzFJ8eckfkUm7A15ALO4YNhkdHdAF/TTBbsa43jr2wDOY+BYi1rfBLOwqPvgjLYXDmzpHFoTfyEPsiLIMNm0fHMtvFVXADrl0QdgxbVWYbB2I8bD1E2BFsKAUP6yjBYibFVdgXYSmMf0Ss2eIhWJhf7RCWwSarNUcX4tzWjQ4esumCsBTmzMYrr+WdiLfzWK5CYWdgfNOhHbwTDe1gSlgKQ8WJ8fWBBWOZ9pG7h7AMhtpbfWsgiy5UNe9vZ2G/x76QsJ2E7fQfsB+aM44tgCVscwAAAABJRU5ErkJggg==',_binary '\0','ACC-002|LOHANG-T4-05',47,'BATCH'),(46,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB+0lEQVR4Xu2ZMW7DMAxFGWTI6CP4KD5afDQfRUfwmCEoy/+p2q7RIZUCFCj+HwKZes5AflBUYv6KVjtHfpSwk4Sd9GdYsdRUBn9e3O7L6I9rbM51Q1gXNjDT8wT+gkWE6yL0IawTe8Q+d28MhmokXrwKexNWjNhsNqTJhb0Ze8LbNjqaibA3YQ5FMJL/YXbnI8ox1YWwHsxSFdt4uJ0S1oPtWmnpMHkujhuHB2G/xbJjOMc2lCNaB8KLRVc5VEFYC8ZpLaZiG3jw7ZGvbxDWg3ltHdgN8fZhSP4ykhfWg5Xvd7qafOOLVcJ6MOYcnfmG2IxSZK+24TiHCGvBYHJ8hqVzPJ7ylsdendc9YT1YWromPzSymaAufqyCsCaM3jacgAhGDyk7z7oI68H8gRBESzP5sQuTR3sR1omtGIZx8K0chmeaHLvL6MJ6sZLetimeaXK63XkU5uEorAMLk6Nj3B3nXW0mmXzWZSuWsCYsqlC9TZ7Jz7pAnD2EtWObSnqbg1xgGTkUS1gLVmq+p7R0mHx0VCHm5MJJQ1gXlsmfgXEXbjd0Zn7DoYcIa8IYDIxjG5I/bD1k/91SWCdWakOOCJp22D55YW/BvFYBBPhY5J9HwrowhwKr0xpOwGft1cK6MWTdcAIyiFueoy7QcLjcCWvCXpCwk4Sd9B+wT5IpRoZDcjuQAAAAAElFTkSuQmCC',_binary '\0','ACC-003|LOHANG-T4-05',48,'BATCH'),(47,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB7UlEQVR4Xu2ZO27DQAxEaaRwqSPoKHs06Wh7FB1BpQojGw5JfSKkcEQBAYKZiuI+uSCJWdqW9o5mOWd+FLGTiJ30Z9gkrtLmZ/vURO1nkUdrYxwQS2GdVXosyn8gkL4thhXk8SKxDLag1Ip1SKoUM95eJHYXJhhynXY99S4QuxXbrOP5InYbtnnI0wKRrjmGR2JJTFxF24Gaj1sQB8Qy2K4ZIy1DC+x4cHgg9ksM911UHDWvEQ0WPIilMPWQF5LWBeWHeNH2ZHMVYhnMSu7OjAVDh1xvQOQKXsS0E0thYR22V+ztMGwfcmKXMJcWvV/NRGfbu+DtIJbCbJLRBTcTVdyAVawvxDLYZOuxoPjG6MX3PUMsiVmp43TfisfSzEyIZbA2mzND5hjjZh11nXZiGczuu6H2MdI1LBqZfX8jdg3TmmNt0/vOa66z3TD2dgPCvYllMP2uYUta9QUDWsceXSCWw6LUo8BDHv7D2iKucG9i17FN02rR6AvMBBk7IXYdm6Li6iFIqTP7s/I4OjSL2BUs1jYk4SHWBXNmfIK5CrEMtojPdizDzacd6o7/fRDLYXYD+toGM8HRwcmJ5TAP8MNas0AKNg3wxDJYg9AFDxTwiw/n+ARiGQyhoOah45C7VxO7jr0hYicRO+k/YF8Owh/2O+UPnQAAAABJRU5ErkJggg==',_binary '\0','MON-001|LOHANG-T4-06',49,'BATCH'),(48,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB70lEQVR4Xu2YMY6DQAxFHaVImSNwlDla5mgchSNQpoji9bcNyaItIowUafV/NdhvKGzrz4DoJ5plG/lTxDYittHXsElCbbqqPi0wDrPISbVnglgJu3qle9P5AqzLoPoA1hDHRmIV7I5S9zbJ5ZGF9+IjImdiB2H2dFaVm4XBEzsWsy7YSAOL2SZ2DObxXx5yd4tuyROrYBJq1g7UvK+LTBCrYKuMDw9BX57vGWJVzLoQzmyyITf+1Q5iJQyOgeJLeEjLLnRsIVbFluuEPbmw8GkfB7wEBLHd2OSlDuvwIW+Rs4WlcBQSK2DWBUS7hTDkpiHNxDBvELEClsW/GQpnRjadeQS/NovYPix8uKP44czZBWz0fxTEChhOQEyyGXIsLBzFtzf4RmIlDEPuxRfoZryrN2C+jdhubJLlOjHjvMuF80LsAAyT7M4ct+KGsT/lBcO9mlgBu8bXxxgWbVmNdkAvDyG2FwsPsayPdJdhRvFXEatgq2za/eAbJaYdkdeQE9uHTVnspvnDR2T9Dx8NIlbBrObItuAx7frWBTwQq2D3zLozY8j9kxnKaSd2DOaOYUchEJT//RuQ2CGYW4fGhVlx9yBWwxB0zG9ruGDEoi1nIrEChokWPwF9pLtbBzZa0G90xArYByK2EbGN/gP2A+pNshuxyPW5AAAAAElFTkSuQmCC',_binary '\0','MON-002|LOHANG-T4-06',50,'BATCH'),(49,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB5ElEQVR4Xu2ZMY7DMAwEebgiZZ7gp+hp8dP8FD8hZYogPO5SdhLjisA0cMBht6KokQutQFGJ+Se62jbzq4RtJGyjP8NmS7X57P6IxDRczb7cxz4hrISdudNji9F3YKMN7ndgDXksFFbBbtjqES7cESybHxnjQmFHYHDB3S6RPpEXdiQ228mBTZbFRNgxmENw4bSUjhtLdENeWBGzVAs7sOfjGvQJYRVsVecvDl8erzPCqli4kJU5NPgtavXTDmEFbGaJXjefp50ujEgLK2Ihbn57CeBCXIVDt0jYfmw5yVFD2LYtCn42vvKEFbC1dNg5SwcCjtuyUNh+bM43HZRnmyF8mYYrOzphFSweHQ8U5EgulZm+kBdWxGLzDS5EQeYhRzo3f/mCsDo2thhBl0TZvwXGZcJ2Y7PlDz5wgfcdA/CTCTsAw0nGfZedRtjB0pENxrOLFrYP668P3HfJO+1Akl8QVsP6m87XG5B2rBJWwVbhtDva46whzLy9nYXtwCKmWpxtJMf++sAN2DtnYQXsTA8iSTpLNE57uoCBsAp267P8S46z62s6eWEHYQ9ehc4MxWeIsEOwvAFbZL7fGmZhFYzbvbiQNyCDhtMurIhZqrnTDmefjIUxYiCsgH0gYRsJ2+g/YD8PqQezuvA5ywAAAABJRU5ErkJggg==',_binary '\0','MON-003|LOHANG-T4-06',51,'BATCH'),(50,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB90lEQVR4Xu2ZMY7DMAwEGaRIeU/wU/Q062l+ip7gMkUQHpeUHMNIkUgGDjjsFgeHHKfQEispJ/qJVjlW3orYQcQO+jOsSCjZ50d9uF+fork2iA1hPwpldGsjuqioPokNYuiiuIpcrLFM6w3dnIoI7CB2ChZDPi8it8cFvhA7EbMHKzomVyV2FqZQc8GSefJCVN5FDbFvMAlVF7TZcdwBiXVhm4pFB1xIVsaQv0RsBLtj47M1r9OOIbeIdl+IDWOrj/K8WFcR0Va52eNrKyQ2gjUX7Nj2wAu++E90LEz8REdsAGuJYRmCbvA25LN/wxbRxLowW3MURSbdFh9vYch3LhDrxppiza1bPxeJ6x6xEcy7OWHIsfjg60Euxp7YAKa+A2o8YNpT2IGs3rlAbAjLliFY/IgOC20rWoXYOIbi7Ly7UPynCccitIn1Y6XugKmFif1BdLgd6iLWj6miW2cbyfw6J8OgzSxifVhc5SDv2i2vYoIX8T3EBrCmIpEhVV7Z352J9WDoQglhIuGCYxmpsg05sU4scjinmHa/fdyBuQv7HZBYF+ZFw+LSocukOFe44kViJ2DVDsc8Ouxh/9MEsUEMxVh8x9DfZQixPswryBBH5lqxrJa3/1Am9hXmTRyGFXaYprhEi7+4DTmxLuwDETuI2EH/AfsFTSiGX4XpAu4AAAAASUVORK5CYII=',_binary '\0','LAP-023|LOHANG-T4-03',52,'BATCH'),(51,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB3klEQVR4Xu2YMY7CUAxEP6Kg3CPkKDkaOVqOkiOkpEB4PWMnQLQFiiMhrWYaLPulsa35/9PsE81tm/lTwjYSttHXsKmFerPb+dHalZmT2ZAFYSXsh50eepsv/tuuY/PMoyFjDISVsBtaPfRTu9wZePpMzMeBQNghGAPf9pUXdiCGJaeHxLYLOwhDDhjVQrBoZoQVsSz0Pg70nNvOIAvCKthTM6zD1ZndT28FYRWMhtw6YtztG5ac9bOwIpbOPDYaMoVMLDm2XVgJy2o3w5kR0UNwFPp75DkFYfsxs9GbT+sYO5T4DKGEVbApNvnFOqYo+5kY92RhBWyRP5mBtWXbOY74UFgN4xTinkzrQManYHE4Cqtg2XNzzJPhzJl5PwGF7cAydg+ZcVuDM+dzj0chImEFLGM4RvBwZpgJAuy/sArWUoEhmFfsbcmF7cROcGZ4CKzDxxEWzSUXVsXQ83AMr+SSk0IGHwrbj62aiPluexo8B7ROQdg+bGKNjkHruHom/pro40NhJSyvE/3yF9CCRWmdgrCdGFuNni/O/OIhzyUXVsRYxQnIezKUDxNhB2BmK88rR2OAD4VVMDYcGP+IGPKeTJ53D2EVjK32nifG9ILFtgvbj30gYRsJ2+g/YL8+iBOfwzW5zAAAAABJRU5ErkJggg==',_binary '\0','TAB-001|LOHANG-T4-03',53,'BATCH'),(52,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAAB3UlEQVR4Xu2aMY7CQAxFvdoiJUfgKDlacrQcJUegpEB4/b9nIhRRoDgS0ur/Aib2m8a2PJ6A+Se62d7yVsJ2ErbT17DVUuNqgz/DsODpx31uDmEl7MJIz6P7Pbw2wfIANsKOjcIq2B2hniML9gu+Bz8twk7C4omKxfAQdjaWtQ0sa1vYORjMjiwMucDGNz1E2CHMUvAi5sS4aA5hFWwTBgx3OC4Z/E3Cqhhaxw3zG7E7j8KeDmEFrE/F8ZQHn12dWZixQ1gR8zZXIPj4nBzBj7xMC3juF3YYg/HZvLx9LFcehWjaxluesApmvbZ7kVumAxgtwgpYfLCksYCYDrYOpAOXaGEljGtkAQ3ZcBQ2PiSsiGUlZ/DZQ2KR3i0dwo5jr1lgQ46N+QoIPQTPwgrYmndndgx8w8tqnxYTVsfsRT0LqHZrvLASluPEhCfDOAE9gBmyIKyK4SoXuiYxo5mgyLuEVbBNwXM83mob1b4VubBjWKypET/JJZZzBRbcKKyC5QlIo/Maslp7F9RcwkpYCz4GuZYOBB+68A2bsLMwzhXJY6LjUSjsHMz5sh3/NhniK3i4hFUx+Br25CDHRViimbxkQdghzFKBsXVMyAvvzpbpEFbBPpCwnYTt9B+wPxhNb/pP/+OlAAAAAElFTkSuQmCC',_binary '\0','ACC-020|LOHANG-T4-04',54,'BATCH'),(53,'iVBORw0KGgoAAAANSUhEUgAAASwAAAEsAQAAAABRBrPYAAABnElEQVR4Xu3Qy23EMAyEYXWm1lVSOnA4P6l1VmCMPQTIZWRbj+FHHzSuT8bXOJN2mB3D7Bh/z9ZgzKUvnkiY74pZyzIKdtfS7YpZz1blucqpZ+VeFbMHRkX3mcluMfuUrVy1mD2xXaP+o2dXzFo2GMXenqqYdewer+tMdRfMekYl7pklP90qs1Kzji22WrQRolVZ/cCsYWlGgLePJTvMWlbBFiTMF+Go6zU7WRZWnOtLQWb2G4tAezLKUMr5j8usY4o5Z02cWhHNZg2rI3xJ0TXQtZj1LJ6hIK+3Ghddkbyu1+ydqZo15Ruqm+q+XrOTTckhoDDn6qfLrGMcp2b4sdNi1rFKuVzt8pbjVWnqP2YduyImYkyOOtRumrVs6RwhiQ5qjKNenSI1a5hMyk3U8pLamzUslltWqZpzmLVsj+BrIpmzPT+zhkWmMZEAmtJPQbOWkTNXSiPblU1mLVt3GEudsLHVD8wemBCvIl4hDmYPjI32ddJCu9kv7MpLZI4Km9einVnHdKFDcVBdKtGsmLNZwz4YZscwO8b/sG8xJwTSwF+uawAAAABJRU5ErkJggg==',_binary '\0','TEST-PRO-001|LOT-SUMMER-01',100,'BATCH');
/*!40000 ALTER TABLE `qr_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_MANAGER'),(3,'ROLE_STAFF'),(4,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales_order_items`
--

DROP TABLE IF EXISTS `sales_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `so_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` decimal(15,2) NOT NULL,
  `shipped_qty` decimal(15,2) DEFAULT '0.00',
  `unit_price` decimal(19,4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `FKbrb0ia68jv5x0ev89yoogec9i` (`so_id`),
  CONSTRAINT `fk_soi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_soi_so` FOREIGN KEY (`so_id`) REFERENCES `sales_orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_order_items`
--

LOCK TABLES `sales_order_items` WRITE;
/*!40000 ALTER TABLE `sales_order_items` DISABLE KEYS */;
INSERT INTO `sales_order_items` VALUES (1,1,10,5.00,0.00,NULL),(2,100,65,5.00,0.00,NULL);
/*!40000 ALTER TABLE `sales_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales_orders`
--

DROP TABLE IF EXISTS `sales_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `so_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `total_amount` decimal(19,4) DEFAULT '0.0000',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `so_code` (`so_code`),
  KEY `fk_so_customer` (`customer_id`),
  CONSTRAINT `fk_so_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_orders`
--

LOCK TABLES `sales_orders` WRITE;
/*!40000 ALTER TABLE `sales_orders` DISABLE KEYS */;
INSERT INTO `sales_orders` VALUES (1,'SO-2026-TEST',NULL,'Khách hàng Test','PENDING',0.0000,'2026-04-08 14:18:51','2026-04-08 14:18:51'),(100,'SO-2026-PICK-TEST',NULL,'Khách hàng Test Gợi Ý','PENDING',0.0000,'2026-04-08 14:30:07','2026-04-08 14:30:07');
/*!40000 ALTER TABLE `sales_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocktake_items`
--

DROP TABLE IF EXISTS `stocktake_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stocktake_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `system_qty` decimal(15,2) DEFAULT NULL,
  `actual_qty` decimal(15,2) DEFAULT NULL,
  `variance_qty` decimal(15,2) DEFAULT NULL,
  `location_id` bigint DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `batch_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `location_id` (`location_id`),
  KEY `product_id` (`product_id`),
  KEY `batch_id` (`batch_id`),
  CONSTRAINT `fk_sti_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_sti_location` FOREIGN KEY (`location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_sti_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_sti_session` FOREIGN KEY (`session_id`) REFERENCES `stocktake_sessions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocktake_items`
--

LOCK TABLES `stocktake_items` WRITE;
/*!40000 ALTER TABLE `stocktake_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `stocktake_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stocktake_sessions`
--

DROP TABLE IF EXISTS `stocktake_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stocktake_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `session_code` (`session_code`),
  KEY `created_by` (`created_by`),
  KEY `warehouse_id` (`warehouse_id`),
  CONSTRAINT `fk_stock_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `fk_stock_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stocktake_sessions`
--

LOCK TABLES `stocktake_sessions` WRITE;
/*!40000 ALTER TABLE `stocktake_sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `stocktake_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_order_items`
--

DROP TABLE IF EXISTS `transfer_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer_order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qty` decimal(15,2) NOT NULL,
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
  KEY `batch_id` (`batch_id`),
  CONSTRAINT `fk_tfi_batch` FOREIGN KEY (`batch_id`) REFERENCES `product_batches` (`batch_id`),
  CONSTRAINT `fk_tfi_from_loc` FOREIGN KEY (`from_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_tfi_order` FOREIGN KEY (`transfer_order_id`) REFERENCES `transfer_orders` (`id`),
  CONSTRAINT `fk_tfi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `fk_tfi_to_loc` FOREIGN KEY (`to_location_id`) REFERENCES `warehouse_locations` (`location_id`),
  CONSTRAINT `fk_tfi_to_wh` FOREIGN KEY (`to_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_order_items`
--

LOCK TABLES `transfer_order_items` WRITE;
/*!40000 ALTER TABLE `transfer_order_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `transfer_order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transfer_orders`
--

DROP TABLE IF EXISTS `transfer_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transfer_orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `transfer_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `from_warehouse_id` bigint NOT NULL,
  `outbound_receipt_id` bigint DEFAULT NULL,
  `to_warehouse_id` bigint NOT NULL,
  `transfer_date` datetime DEFAULT NULL,
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transfer_code` (`transfer_code`),
  KEY `from_warehouse_id` (`from_warehouse_id`),
  KEY `outbound_receipt_id` (`outbound_receipt_id`),
  KEY `to_warehouse_id` (`to_warehouse_id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `fk_transfer_from_wh` FOREIGN KEY (`from_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  CONSTRAINT `fk_transfer_outbound` FOREIGN KEY (`outbound_receipt_id`) REFERENCES `outbound_receipts` (`id`),
  CONSTRAINT `fk_transfer_to_wh` FOREIGN KEY (`to_warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  CONSTRAINT `fk_transfer_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transfer_orders`
--

LOCK TABLES `transfer_orders` WRITE;
/*!40000 ALTER TABLE `transfer_orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `transfer_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `units`
--

DROP TABLE IF EXISTS `units`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `units` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT,
  `unit_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `unit_name` (`unit_name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `units`
--

LOCK TABLES `units` WRITE;
/*!40000 ALTER TABLE `units` DISABLE KEYS */;
INSERT INTO `units` VALUES (1,'Chiếc'),(2,'Hộp'),(3,'Thùng');
/*!40000 ALTER TABLE `units` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `full_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$oC37m/7Qk1CxfGpdYrES8e570OQX3L9GGmD3qR4.AgNiVxB/CzCQa','Administrator','admin@example.com','0123456789',1),(2,'manager','$2a$10$HsE3UuGkO7iZTNmhYPjE3.s3X2uvitDRl8fcrknP7k0AU3t3quDVS','Manager User123','manager@warehouse.com','0987654321',2),(3,'staff','$2a$10$HC7kAEHRXY2NFEIFHBhaSufIRo7SjQbYpZZYMa53JgO8rvoFfhVy2','Staff Employee','staff@warehouse.com','0111222333',3),(4,'user','$2a$10$GEy42Lpg3PkQD9M6OLOew.40cTk5hmgCqziv2FkZJs6HuZIQPZ2ym','Normal User','user@example.com','0444555666',4),(8,'áđá','$2a$10$7t.ZeZeWzEQWr1PStCvLh.mVWu7VVxuewURTF8kuJ5DIFJJDhIMWu','Leo Messi222','test@gmail.com','0444555666',3);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_locations`
--

DROP TABLE IF EXISTS `warehouse_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse_locations` (
  `location_id` bigint NOT NULL AUTO_INCREMENT,
  `location_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `zone_id` bigint DEFAULT NULL,
  `qr_code_id` bigint DEFAULT NULL,
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `location_code` (`location_code`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `zone_id` (`zone_id`),
  KEY `qr_code_id` (`qr_code_id`),
  CONSTRAINT `fk_location_qr` FOREIGN KEY (`qr_code_id`) REFERENCES `qr_codes` (`qr_code_id`),
  CONSTRAINT `fk_location_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  CONSTRAINT `fk_location_zone` FOREIGN KEY (`zone_id`) REFERENCES `warehouse_zones` (`zone_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_locations`
--

LOCK TABLES `warehouse_locations` WRITE;
/*!40000 ALTER TABLE `warehouse_locations` DISABLE KEYS */;
INSERT INTO `warehouse_locations` VALUES (1,'LOC-DEFAULT-01',1,NULL,NULL),(10,'KE-A-01',1,NULL,NULL),(11,'KE-B-02',1,NULL,NULL);
/*!40000 ALTER TABLE `warehouse_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse_zones`
--

DROP TABLE IF EXISTS `warehouse_zones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse_zones` (
  `zone_id` bigint NOT NULL AUTO_INCREMENT,
  `zone_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `zone_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `warehouse_id` bigint NOT NULL,
  `warehouses` bigint NOT NULL,
  PRIMARY KEY (`zone_id`),
  UNIQUE KEY `zone_code` (`zone_code`),
  KEY `warehouse_id` (`warehouse_id`),
  KEY `FKrasb229u17v9pwhmy2qsed0dp` (`warehouses`),
  CONSTRAINT `fk_zone_warehouse` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse_zones`
--

LOCK TABLES `warehouse_zones` WRITE;
/*!40000 ALTER TABLE `warehouse_zones` DISABLE KEYS */;
/*!40000 ALTER TABLE `warehouse_zones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouses`
--

DROP TABLE IF EXISTS `warehouses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouses` (
  `warehouse_id` bigint NOT NULL AUTO_INCREMENT,
  `warehouse_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `warehouse_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `warehouse_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`warehouse_id`),
  UNIQUE KEY `warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouses`
--

LOCK TABLES `warehouses` WRITE;
/*!40000 ALTER TABLE `warehouses` DISABLE KEYS */;
INSERT INTO `warehouses` VALUES (1,'','Kho Tổng',NULL,NULL),(2,'testcode','testname','600 QUận 7',NULL);
/*!40000 ALTER TABLE `warehouses` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-10 22:36:05
