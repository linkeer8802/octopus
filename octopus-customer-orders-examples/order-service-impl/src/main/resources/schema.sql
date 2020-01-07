CREATE TABLE `orders`  (
  `id` char(36) ,
  `customer_id` char(36) ,
  `total` decimal (9, 2),
  `state` varchar(16) ,
  `version` bigint,
  PRIMARY KEY (`id`)
);
