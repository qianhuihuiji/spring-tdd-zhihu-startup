CREATE TABLE `question`
(
    `id`           int unsigned NOT NULL AUTO_INCREMENT,
    `user_id`      int          NOT NULL,
    `title`        varchar(100)     NOT NULL,
    `content`      text             NOT NULL,
    `created_at` timestamp NOT NULL,
    `updated_at` timestamp NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1;
