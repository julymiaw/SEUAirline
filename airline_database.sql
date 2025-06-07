-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: Airline
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `Airline`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `Airline` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `Airline`;

--
-- Table structure for table `Admin`
--

DROP TABLE IF EXISTS `Admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Admin` (
  `AdminID` varchar(10) NOT NULL,
  `Username` varchar(50) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Name` varchar(100) NOT NULL,
  PRIMARY KEY (`AdminID`),
  UNIQUE KEY `Username` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Admin`
--

LOCK TABLES `Admin` WRITE;
/*!40000 ALTER TABLE `Admin` DISABLE KEYS */;
INSERT INTO `Admin` VALUES ('A001','admin','admin123','系统管理员'),('A002','manager','manager123','业务管理员');
/*!40000 ALTER TABLE `Admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Aircraft`
--

DROP TABLE IF EXISTS `Aircraft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Aircraft` (
  `AircraftID` varchar(20) NOT NULL COMMENT '飞机编号，飞机的注册号',
  `AircraftType` varchar(50) NOT NULL COMMENT '飞机型号，如 Boeing 787-800',
  `EconomySeats` int NOT NULL COMMENT '经济舱座位数',
  `BusinessSeats` int NOT NULL COMMENT '商务舱座位数',
  PRIMARY KEY (`AircraftID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='飞机表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Aircraft`
--

LOCK TABLES `Aircraft` WRITE;
/*!40000 ALTER TABLE `Aircraft` DISABLE KEYS */;
INSERT INTO `Aircraft` VALUES ('A320-200','空客A320-200',146,24),('A321-200','空客A321-200',170,28),('A330-300','空客A330-300',250,36),('A350-900','空客A350-900',280,42),('B737-800','波音737-800',150,20),('B737-900','波音737-900',160,24),('B777-300ER','波音777-300ER',300,42),('B787-8','波音787-8梦想客机',210,28),('B787-9','波音787-9梦想客机',250,35),('C919-100','国产C919',140,20),('CRJ900','庞巴迪CRJ900',76,12),('ERJ190','巴航工业ERJ190',90,8);
/*!40000 ALTER TABLE `Aircraft` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Airport`
--

DROP TABLE IF EXISTS `Airport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Airport` (
  `AirportID` varchar(10) NOT NULL COMMENT '机场编号，三字代码，如 PVG, SHA',
  `AirportName` varchar(100) NOT NULL COMMENT '机场名称，如 北京首都国际机场',
  PRIMARY KEY (`AirportID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机场表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Airport`
--

LOCK TABLES `Airport` WRITE;
/*!40000 ALTER TABLE `Airport` DISABLE KEYS */;
INSERT INTO `Airport` VALUES ('CAN','广州白云国际机场'),('CKG','重庆江北国际机场'),('CSX','长沙黄花国际机场'),('CTU','成都双流国际机场'),('DLC','大连周水子国际机场'),('HGH','杭州萧山国际机场'),('HRB','哈尔滨太平国际机场'),('KMG','昆明长水国际机场'),('NKG','南京禄口国际机场'),('PEK','北京首都国际机场'),('PKX','北京大兴国际机场'),('PVG','上海浦东国际机场'),('SHA','上海虹桥国际机场'),('SHE','沈阳桃仙国际机场'),('SYX','三亚凤凰国际机场'),('SZX','深圳宝安国际机场'),('TSN','天津滨海国际机场'),('URC','乌鲁木齐地窝堡国际机场'),('WUH','武汉天河国际机场'),('XIY','西安咸阳国际机场');
/*!40000 ALTER TABLE `Airport` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Customer`
--

DROP TABLE IF EXISTS `Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Customer` (
  `CustomerID` varchar(10) NOT NULL COMMENT '客户编号',
  `Name` varchar(50) NOT NULL COMMENT '姓名',
  `Password` varchar(255) DEFAULT NULL COMMENT '加密后的用户密码',
  `AccountBalance` int NOT NULL DEFAULT '0' COMMENT '账户余额',
  `Phone` varchar(20) NOT NULL COMMENT '联系电话',
  `Email` varchar(100) DEFAULT NULL COMMENT '电子邮箱地址',
  `Identity` varchar(20) NOT NULL COMMENT '身份证号',
  `Rank` int NOT NULL DEFAULT '0' COMMENT '会员等级，根据飞行次数计算',
  PRIMARY KEY (`CustomerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customer`
--

LOCK TABLES `Customer` WRITE;
/*!40000 ALTER TABLE `Customer` DISABLE KEYS */;
INSERT INTO `Customer` VALUES ('CT000001','张三','password123',2720,'13800138001','zhangsan@airline.com','110101199001011001',6),('CT000002','李四','password123',3000,'13800138002','lisi@airline.com','110101199002022002',3),('CT000003','王五','password123',8000,'13800138003','wangwu@airline.com','110101199003033003',8),('CT000004','赵六','password123',2000,'13800138004','zhaoliu@airline.com','110101199004044004',2),('CT000005','管理员测试','admin123',10000,'13900000000','admin@airline.com','110101199000000000',10);
/*!40000 ALTER TABLE `Customer` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_customer` BEFORE INSERT ON `Customer` FOR EACH ROW BEGIN
    DECLARE new_customer_id VARCHAR(10);
    SET new_customer_id = CONCAT('CT', LPAD((SELECT COUNT(*) + 1 FROM Customer), 6, '0'));
    SET NEW.CustomerID = new_customer_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `passenger_customer` AFTER INSERT ON `Customer` FOR EACH ROW BEGIN
    INSERT INTO Passenger (HostID, GuestID) VALUES (NEW.CustomerID, NEW.CustomerID);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `Flight`
--

DROP TABLE IF EXISTS `Flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Flight` (
  `FlightID` varchar(10) NOT NULL COMMENT '航班编号，如 CA1234',
  `RouteID` varchar(10) NOT NULL COMMENT '航线编号，关联 Route 表',
  `AircraftID` varchar(20) NOT NULL COMMENT '飞机编号，关联 Aircraft 表',
  `DepartureTime` datetime NOT NULL COMMENT '航班起飞时间',
  `ArrivalTime` datetime NOT NULL COMMENT '航班到达时间',
  `EconomyPrice` decimal(10,2) NOT NULL COMMENT '经济舱票价',
  `BusinessPrice` decimal(10,2) NOT NULL COMMENT '商务舱票价',
  PRIMARY KEY (`FlightID`),
  KEY `RouteID` (`RouteID`),
  KEY `AircraftID` (`AircraftID`),
  CONSTRAINT `Flight_ibfk_1` FOREIGN KEY (`RouteID`) REFERENCES `Route` (`RouteID`),
  CONSTRAINT `Flight_ibfk_2` FOREIGN KEY (`AircraftID`) REFERENCES `Aircraft` (`AircraftID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航班表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Flight`
--

LOCK TABLES `Flight` WRITE;
/*!40000 ALTER TABLE `Flight` DISABLE KEYS */;
INSERT INTO `Flight` VALUES ('SE0001','RT001','B737-800','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0002','RT002','A320-200','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0003','RT003','B787-9','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0004','RT004','A330-300','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0005','RT005','C919-100','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0006','RT006','B737-800','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0007','RT007','A320-200','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0008','RT008','B787-9','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0009','RT009','A330-300','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0010','RT010','C919-100','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0011','RT011','B737-800','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0012','RT012','A320-200','2025-06-08 08:00:00','2025-06-08 11:30:00',800.00,2400.00),('SE0013','RT013','B787-9','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0014','RT014','A330-300','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00),('SE0015','RT015','C919-100','2025-06-08 08:00:00','2025-06-08 11:30:00',1200.00,3600.00);
/*!40000 ALTER TABLE `Flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Order`
--

DROP TABLE IF EXISTS `Order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Order` (
  `OrderID` varchar(10) NOT NULL COMMENT '订单编号，如 OD001',
  `CustomerID` varchar(10) NOT NULL COMMENT '客户编号，关联 Customer 表',
  `BuyerID` varchar(10) NOT NULL COMMENT '购买人的客户编号，关联Customer表',
  `FlightID` varchar(10) NOT NULL COMMENT '航班编号，关联 Flight 表',
  `SeatType` enum('Economy','Business') NOT NULL COMMENT '座位类型，0-Economy, 1-Business',
  `OrderStatus` enum('Established','Paid','Canceled') NOT NULL COMMENT '订单状态',
  `OrderTime` datetime NOT NULL COMMENT '订单时间',
  PRIMARY KEY (`OrderID`),
  KEY `CustomerID` (`CustomerID`),
  KEY `BuyerID` (`BuyerID`),
  KEY `FlightID` (`FlightID`),
  CONSTRAINT `Order_ibfk_1` FOREIGN KEY (`CustomerID`) REFERENCES `Customer` (`CustomerID`),
  CONSTRAINT `Order_ibfk_2` FOREIGN KEY (`BuyerID`) REFERENCES `Customer` (`CustomerID`),
  CONSTRAINT `Order_ibfk_3` FOREIGN KEY (`FlightID`) REFERENCES `Flight` (`FlightID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Order`
--

LOCK TABLES `Order` WRITE;
/*!40000 ALTER TABLE `Order` DISABLE KEYS */;
INSERT INTO `Order` VALUES ('OD000001','CT000001','CT000001','SE0008','Economy','Established','2025-06-08 01:32:10'),('OD000002','CT000002','CT000001','SE0008','Economy','Established','2025-06-08 01:32:10');
/*!40000 ALTER TABLE `Order` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_order` BEFORE INSERT ON `Order` FOR EACH ROW BEGIN
    DECLARE new_order_id VARCHAR(10);
    SET new_order_id = CONCAT('OD', LPAD((SELECT COUNT(*) + 1 FROM `Order`), 6, '0'));
    SET NEW.OrderID = new_order_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `CheckPaidOrders` BEFORE INSERT ON `Order` FOR EACH ROW BEGIN
    DECLARE total_seats INT;
    DECLARE paid_orders INT;

    -- 获取该飞机的总座位数
    SELECT EconomySeats + BusinessSeats INTO total_seats
    FROM Aircraft
    JOIN Flight ON Aircraft.AircraftID = Flight.AircraftID
    WHERE Flight.FlightID = NEW.FlightID;

    -- 获取该飞机已支付订单的数量
    SELECT COUNT(*) INTO paid_orders
    FROM `Order`
    WHERE FlightID = NEW.FlightID AND OrderStatus = 'Paid';

    -- 如果已支付订单数量加上新订单数量超过总座位数，则抛出错误
    IF paid_orders + 1 > total_seats THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Paid orders exceed the total seats of the aircraft.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `Passenger`
--

DROP TABLE IF EXISTS `Passenger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Passenger` (
  `HostID` varchar(10) NOT NULL COMMENT '主人编号',
  `GuestID` varchar(10) NOT NULL COMMENT '客人编号',
  PRIMARY KEY (`HostID`,`GuestID`),
  KEY `GuestID` (`GuestID`),
  CONSTRAINT `Passenger_ibfk_1` FOREIGN KEY (`HostID`) REFERENCES `Customer` (`CustomerID`),
  CONSTRAINT `Passenger_ibfk_2` FOREIGN KEY (`GuestID`) REFERENCES `Customer` (`CustomerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='乘车人表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Passenger`
--

LOCK TABLES `Passenger` WRITE;
/*!40000 ALTER TABLE `Passenger` DISABLE KEYS */;
INSERT INTO `Passenger` VALUES ('CT000001','CT000001'),('CT000002','CT000001'),('CT000001','CT000002'),('CT000002','CT000002'),('CT000001','CT000003'),('CT000003','CT000003'),('CT000004','CT000004'),('CT000005','CT000005');
/*!40000 ALTER TABLE `Passenger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Route`
--

DROP TABLE IF EXISTS `Route`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Route` (
  `RouteID` varchar(10) NOT NULL COMMENT '航线编号，唯一标识一条航线，如 RT001',
  `DepartureAirportID` varchar(10) NOT NULL COMMENT '起点机场编号，关联 Airport 表',
  `ArrivalAirportID` varchar(10) NOT NULL COMMENT '终点机场编号，关联 Airport 表',
  PRIMARY KEY (`RouteID`),
  KEY `DepartureAirportID` (`DepartureAirportID`),
  KEY `ArrivalAirportID` (`ArrivalAirportID`),
  CONSTRAINT `Route_ibfk_1` FOREIGN KEY (`DepartureAirportID`) REFERENCES `Airport` (`AirportID`),
  CONSTRAINT `Route_ibfk_2` FOREIGN KEY (`ArrivalAirportID`) REFERENCES `Airport` (`AirportID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航线表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Route`
--

LOCK TABLES `Route` WRITE;
/*!40000 ALTER TABLE `Route` DISABLE KEYS */;
INSERT INTO `Route` VALUES ('RT001','PEK','PVG'),('RT002','PEK','CAN'),('RT003','PEK','CTU'),('RT004','PEK','XIY'),('RT005','PEK','KMG'),('RT006','PEK','SYX'),('RT007','PEK','DLC'),('RT008','PEK','WUH'),('RT009','PEK','HGH'),('RT010','PVG','PEK'),('RT011','PVG','CAN'),('RT012','PVG','CTU'),('RT013','PVG','KMG'),('RT014','PVG','XIY'),('RT015','SHA','PEK'),('RT016','SHA','CAN'),('RT017','SHA','CTU'),('RT018','SHA','WUH'),('RT019','CAN','PEK'),('RT020','CAN','PVG'),('RT021','CAN','SYX'),('RT022','CAN','CTU'),('RT023','CAN','KMG'),('RT024','CAN','HGH'),('RT025','CAN','WUH'),('RT026','CAN','XIY'),('RT027','CTU','PEK'),('RT028','CTU','PVG'),('RT029','CTU','CAN'),('RT030','CTU','KMG'),('RT031','CTU','XIY'),('RT032','CTU','SYX'),('RT033','CTU','URC'),('RT034','CTU','DLC'),('RT035','WUH','PEK'),('RT036','WUH','PVG'),('RT037','WUH','CAN'),('RT038','WUH','CTU'),('RT039','XIY','PEK'),('RT040','XIY','PVG'),('RT041','XIY','CTU'),('RT042','XIY','CAN'),('RT043','HGH','PEK'),('RT044','HGH','CAN'),('RT045','HGH','CTU'),('RT046','HGH','SYX'),('RT047','KMG','PEK'),('RT048','KMG','PVG'),('RT049','KMG','CAN'),('RT050','KMG','CTU'),('RT051','SYX','PEK'),('RT052','SYX','PVG'),('RT053','SYX','CAN'),('RT054','SYX','HGH'),('RT055','PVG','SHA'),('RT056','SHA','PVG'),('RT057','SZX','PEK'),('RT058','SZX','PVG'),('RT059','SZX','CTU'),('RT060','NKG','PEK'),('RT061','NKG','CAN'),('RT062','NKG','CTU'),('RT063','DLC','PEK'),('RT064','DLC','PVG'),('RT065','DLC','CAN'),('RT066','URC','PEK'),('RT067','URC','XIY'),('RT068','URC','CTU');
/*!40000 ALTER TABLE `Route` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'IGNORE_SPACE,ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER `before_insert_route` BEFORE INSERT ON `Route` FOR EACH ROW BEGIN
    DECLARE new_route_id VARCHAR(10);
    SET new_route_id = CONCAT('RT', LPAD((SELECT COUNT(*) + 1 FROM Route), 3, '0'));
    SET NEW.RouteID = new_route_id;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'Airline'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-08  1:39:59
