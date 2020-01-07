CREATE TABLE `global_events`  (
  `id` char(36) ,
  `aggregate_root_type` char(128) ,
  `timestamp` bigint,
  `data` varchar(4096),
  `published` smallint ,
  PRIMARY KEY (`id`)
);