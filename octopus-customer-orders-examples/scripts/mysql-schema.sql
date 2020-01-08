CREATE TABLE `customer`  (
  `id` char(36) ,
  `name` varchar(128),
  `credit_limit` decimal (9, 2),
  `version` bigint,
  PRIMARY KEY (`id`)
);
CREATE TABLE `customer_credit_reservation`  (
  `id` char(36) ,
  `customer_id` char(36),
  `order_id` char (36),
  `amount` decimal (9, 2),
  PRIMARY KEY (`id`)
);
CREATE TABLE `orders`  (
  `id` char(36) ,
  `customer_id` char(36) ,
  `total` decimal (9, 2),
  `state` varchar(16) ,
  `version` bigint,
  PRIMARY KEY (`id`)
);
CREATE TABLE `global_events`  (
  `id` char(36) ,
  `aggregate_root_type` char(128) ,
  `timestamp` bigint,
  `data` varchar(4096),
  `published` smallint ,
  PRIMARY KEY (`id`)
);
