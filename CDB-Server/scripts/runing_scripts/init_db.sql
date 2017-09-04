CREATE USER crowddb IDENTIFIED by 'crowddb!password';

CREATE database crowddb_meta DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE database crowddb_temp DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE database crowddb_assign DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
CREATE database crowddb_assign DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

USE crowddb_meta;
CREATE TABLE IF NOT EXISTS `query` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `qsql` varchar(1024) DEFAULT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `current_sqlnode_id` int(11) NOT NULL,
  `result_table` varchar(20) NOT NULL,
  `db_name` varchar(255) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  `task_title` varchar(255) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  `gmodel` varchar(1) DEFAULT NULL,
  `process` FLOAT DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

GRANT ALL PRIVILEGES ON *.* TO crowddb;
FLUSH PRIVILEGES;
