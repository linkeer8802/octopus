CREATE TABLE `book`  (
  `id` char(36) ,
  `name` varchar(128),
  `isbn` varchar(128),
  `publisher` varchar(128),
  `author` varchar(16),
  `status` varchar(16),
  `version` bigint,
  PRIMARY KEY (`id`)
);
CREATE TABLE `reader`  (
  `id` char(36) ,
  `name` varchar(128),
  `unpaid_fine` decimal (9, 2),
  `version` bigint,
  PRIMARY KEY (`id`)
);
CREATE TABLE `book_borrowed_record`  (
  `id` char(36) ,
  `reader_id` varchar(36),
  `book_id` varchar(36),
  `borrowing_date` date,
  `should_return_date` date,
  `returning_date` date,
  `overdue_day` int,
  `overdue_fee` decimal (9, 2),
  `status` varchar(16),
  PRIMARY KEY (`id`)
);
/******************************transform******************************/
CREATE TABLE `bank_account`  (
  `id` char(36) ,
  `name` varchar(128),
  `balance` decimal (9, 2),
  `version` bigint,
  PRIMARY KEY (`id`)
);