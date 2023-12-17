-- --------------------------------------------------------
-- 호스트:                          127.0.0.1
-- 서버 버전:                        11.1.2-MariaDB - mariadb.org binary distribution
-- 서버 OS:                        Win64
-- HeidiSQL 버전:                  12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- pptdb 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `pptdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `pptdb`;

-- 테이블 pptdb.docfile 구조 내보내기
CREATE TABLE IF NOT EXISTS `docfile` (
  `file_Id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '파일 번호',
  `file_name` varchar(200) NOT NULL COMMENT '파일 이름',
  `file_type` varchar(100) NOT NULL COMMENT '파일 확장자',
  `file_path` varchar(200) NOT NULL COMMENT '파일 저장 경로',
  PRIMARY KEY (`file_Id`) USING BTREE,
  UNIQUE KEY `file_name` (`file_name`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 테이블 데이터 pptdb.docfile:~1 rows (대략적) 내보내기
INSERT INTO `docfile` (`file_Id`, `file_name`, `file_type`, `file_path`) VALUES
	(37, 'TEST_upload.docx', 'docx', 'C:/Users/e2yon/Documents/CreatePPT/CreatePPT/create-ppt/src/main/resources/storage/upload/TEST_upload.docx');

-- 테이블 pptdb.member 구조 내보내기
CREATE TABLE IF NOT EXISTS `member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '회원 번호',
  `login_id` varchar(20) NOT NULL COMMENT '로그인 ID',
  `password` varchar(50) NOT NULL COMMENT '비밀번호',
  `name` varchar(20) NOT NULL COMMENT '회원 이름',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_id` (`login_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='회원 정보';

-- 테이블 데이터 pptdb.member:~0 rows (대략적) 내보내기
INSERT INTO `member` (`id`, `login_id`, `password`, `name`) VALUES
	(21, 'test', 'test', 'test');

-- 테이블 pptdb.work 구조 내보내기
CREATE TABLE IF NOT EXISTS `work` (
  `work_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '작업 번호',
  `member_id` bigint(20) NOT NULL COMMENT '회원 번호',
  `uload_file` bigint(20) NOT NULL COMMENT '업로드 파일 번호',
  `ppt_file` bigint(20) NOT NULL COMMENT 'ppt 파일 번호',
  `script_file` bigint(20) NOT NULL COMMENT '대본 파일 번호',
  `upload_file` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`work_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='작업';

-- 테이블 데이터 pptdb.work:~0 rows (대략적) 내보내기

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
