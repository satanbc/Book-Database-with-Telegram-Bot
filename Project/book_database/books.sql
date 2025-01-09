-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: book_database
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `author`
--

DROP TABLE IF EXISTS `author`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `author` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `author`
--

LOCK TABLES `author` WRITE;
/*!40000 ALTER TABLE `author` DISABLE KEYS */;
INSERT INTO `author` VALUES (1,'J.K. Rowling'),(2,'George R.R. Martin'),(3,'J.R.R. Tolkien'),(4,'Mark Twain'),(5,'Agatha Christie'),(6,'Isaac Asimov');
/*!40000 ALTER TABLE `author` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `rating` int NOT NULL,
  `release_year` int NOT NULL,
  `page_count` int NOT NULL,
  `description` varchar(256) NOT NULL,
  `series_id` int NOT NULL,
  `author_id` int NOT NULL,
  PRIMARY KEY (`id`,`series_id`,`author_id`),
  KEY `fk_book_series1` (`series_id`),
  KEY `fk_book_author1` (`author_id`),
  CONSTRAINT `fk_book_author1` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_book_series1` FOREIGN KEY (`series_id`) REFERENCES `series` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES (1,'Harry Potter and the Sorcerer\'s Stone',9,1997,320,'The first book in the Harry Potter series.',1,1),(2,'Harry Potter and the Chamber of Secrets',8,1998,341,'The second book in the Harry Potter series.',1,1),(3,'A Game of Thrones',9,1996,694,'The first book in the A Song of Ice and Fire series.',2,2),(4,'A Clash of Kings',8,1998,768,'The second book in the A Song of Ice and Fire series.',2,2),(5,'The Fellowship of the Ring',10,1954,423,'The first book in The Lord of the Rings series.',3,3),(6,'The Two Towers',9,1954,352,'The second book in The Lord of the Rings series.',3,3),(7,'The Adventures of Tom Sawyer',9,1876,274,'A classic novel by Mark Twain about a young boy.',4,4),(8,'The Prince and the Pauper',8,1881,196,'Another famous story by Mark Twain, exploring issues of social justice.',4,4),(9,'Murder on the Orient Express',10,1934,256,'Agatha Christie\'s iconic mystery novel featuring Hercule Poirot.',5,5),(10,'The Murder of Roger Ackroyd',9,1926,284,'One of Agatha Christie\'s most famous mystery novels.',5,5),(11,'Foundation',10,1951,255,'Isaac Asimov\'s landmark sci-fi novel about the decline of an empire and the foundation of a new order.',6,6),(12,'I, Robot',8,1950,318,'A collection of short stories, exploring Asimov\'s Three Laws of Robotics.',6,6),(48,'Dune',10,1965,412,'The first book in Frank Herbert\'s sci-fi epic.',1,1);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_has_character`
--

DROP TABLE IF EXISTS `book_has_character`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_has_character` (
  `book_id` int NOT NULL,
  `character_id` int NOT NULL,
  PRIMARY KEY (`book_id`,`character_id`),
  KEY `fk_book_has_character_character1` (`character_id`),
  CONSTRAINT `fk_book_has_character_book1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_book_has_character_character1` FOREIGN KEY (`character_id`) REFERENCES `character` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_has_character`
--

LOCK TABLES `book_has_character` WRITE;
/*!40000 ALTER TABLE `book_has_character` DISABLE KEYS */;
INSERT INTO `book_has_character` VALUES (1,1),(2,1),(1,2),(2,2),(3,3),(4,3),(3,4),(4,4),(5,5),(6,5),(5,6),(6,6),(7,7),(8,7),(7,8),(9,9),(11,9),(9,10),(11,10),(10,11),(12,11),(10,12),(12,12);
/*!40000 ALTER TABLE `book_has_character` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `character`
--

DROP TABLE IF EXISTS `character`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `character` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `character`
--

LOCK TABLES `character` WRITE;
/*!40000 ALTER TABLE `character` DISABLE KEYS */;
INSERT INTO `character` VALUES (1,'Harry Potter','Main Protagonist'),(2,'Hermione Granger','Supporting Character'),(3,'Jon Snow','Main Protagonist'),(4,'Tyrion Lannister','Supporting Character'),(5,'Frodo Baggins','Main Protagonist'),(6,'Gandalf','Supporting Character'),(7,'Huckleberry Finn','Main Protagonist'),(8,'Tom Sawyer','Supporting Character'),(9,'Hercule Poirot','Main Protagonist'),(10,'Miss Marple','Supporting Character'),(11,'Hari Seldon','Main Protagonist'),(12,'Salvor Hardin','Supporting Character');
/*!40000 ALTER TABLE `character` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `series`
--

DROP TABLE IF EXISTS `series`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `series` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `author_id` int NOT NULL,
  PRIMARY KEY (`id`,`author_id`),
  KEY `fk_series_author1` (`author_id`),
  CONSTRAINT `fk_series_author1` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `series`
--

LOCK TABLES `series` WRITE;
/*!40000 ALTER TABLE `series` DISABLE KEYS */;
INSERT INTO `series` VALUES (1,'Harry Potter',1),(2,'A Song of Ice and Fire',2),(3,'The Lord of the Rings',3),(4,'The Adventures of Huckleberry Finn',4),(5,'Murder on the Orient Express',5),(6,'Foundation',6),(16,'gyhjgyjgh adventures',16),(17,'hjiljhkl adventures',17),(18,'ЕБАШИТЕЛЬ adventures',18),(19,'main adventures',19),(24,'main adventures',24),(25,'main adventures',25);
/*!40000 ALTER TABLE `series` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-09 13:11:42
