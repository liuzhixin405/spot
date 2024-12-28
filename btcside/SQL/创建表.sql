CREATE TABLE `bq_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` int(11) NOT NULL DEFAULT '1' COMMENT '1:btc; 2:ck.usd',
  `f_user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `btc` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT 'btc金额',
  `btc_locked` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '锁定金额',
  `lastupdate` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最后金额变更,unix时间格式(用户活跃分析)',
  `f_user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_coin_index` (`f_user_id`,`currency_type_id`) USING BTREE,
  KEY `user_id_index` (`f_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=469790 DEFAULT CHARSET=utf8 COMMENT='用户账户金额表';

CREATE TABLE `bq_account_amount_log` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL DEFAULT '' COMMENT '描述标题',
  `currency_type_id` tinyint(11) unsigned NOT NULL DEFAULT '0' COMMENT '币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',
  `operation` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '操作类型(0:其它/1:充值/2:提现/3:交易/4:追加保证金/5:购买积分)',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:加款 -1:减款 ',
  `current_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '当前账户余额(bq_acount:btc/bq_account:ltc)',
  `change_amount` decimal(20,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '增减',
  `lastest_amount` decimal(20,8) NOT NULL COMMENT '变更后的金额',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作时间,unix时间格式',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trans_no` char(20) NOT NULL DEFAULT '' COMMENT '交易流水号',
  `trans_type` varchar(150) NOT NULL DEFAULT '0' COMMENT '描述',
  `record_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '交易分表id',
  `before_frozen` decimal(18,8) NOT NULL COMMENT '变更前的冻结',
  `after_frozen` decimal(18,8) NOT NULL COMMENT '变更后的冻结',
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `uniq_trans_no` (`trans_no`),
  KEY `idx_f_user_id` (`f_user_id`,`currency_type_id`),
  KEY `idx_currencytypeid_operation` (`currency_type_id`,`operation`)
) ENGINE=InnoDB AUTO_INCREMENT=3012899 DEFAULT CHARSET=utf8 COMMENT='用户提现/充值记录,主要记录用户金额变更(个人财务流水）';

CREATE TABLE `bq_account_contract` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL,
  `f_code` varchar(10) NOT NULL COMMENT '合约号',
  `f_initbtc` decimal(18,8) DEFAULT NULL COMMENT '初始btc数量',
  `f_btc` decimal(18,8) DEFAULT NULL COMMENT '开仓可用',
  `f_lockbtc` decimal(18,8) DEFAULT NULL COMMENT '合约冻结资金',
  `f_settled` decimal(18,8) DEFAULT NULL COMMENT '已结算资金',
  PRIMARY KEY (`f_id`),
  KEY `idx_uid_code` (`f_user_id`,`f_code`),
  KEY `idx_fcode` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=399222 DEFAULT CHARSET=utf8 COMMENT='合约钱包';

CREATE TABLE `bq_account_contract_yjg` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL,
  `f_code` varchar(10) NOT NULL COMMENT '合约号',
  `f_initbtc` decimal(18,8) DEFAULT NULL COMMENT '初始btc数量',
  `f_btc` decimal(18,8) DEFAULT NULL COMMENT '开仓可用',
  `f_lockbtc` decimal(18,8) DEFAULT NULL COMMENT '合约冻结资金',
  `f_settled` decimal(18,8) DEFAULT NULL COMMENT '已结算资金',
  PRIMARY KEY (`f_id`),
  KEY `idx_uid_code` (`f_user_id`,`f_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='已交割合约钱包';

CREATE TABLE `bq_account_deposit_log` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `mt_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '持仓ID(bq_member_transaction:mt_id)',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '-1:从可用金额扣除保证金 1:返还保证金到账户可用金额',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '变更金额',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `dateline` int(10) unsigned NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id_dateline` (`f_user_id`,`dateline`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户所持合约保证金不足时，系统自动"追加/返还"保证金记录';

CREATE TABLE `bq_account_gain` (
  `f_gain_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL COMMENT '用户ID',
  `transaction_id` int(11) NOT NULL DEFAULT '0' COMMENT '合约ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_btc` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约盈利的BTC金额',
  PRIMARY KEY (`f_gain_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合约资金盈利情况';

CREATE TABLE `bq_app` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL DEFAULT 'bfx' COMMENT '名称',
  `platform` varchar(45) NOT NULL DEFAULT '' COMMENT '平台',
  `version` varchar(45) NOT NULL COMMENT '版本号',
  `ipa_download_times` int(11) NOT NULL DEFAULT '0' COMMENT 'IOS版下载次数',
  `apk_download_times` int(11) NOT NULL DEFAULT '0' COMMENT 'Android版下载次数',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '版本更新时间',
  `download_url` varchar(256) NOT NULL DEFAULT 'https://www.bfx.nu/app/download' COMMENT '下载路径',
  `note` varchar(256) NOT NULL DEFAULT '' COMMENT '更新日志',
  `download_times` int(11) NOT NULL DEFAULT '0' COMMENT '下载次数',
  `force_version` varchar(45) NOT NULL DEFAULT '' COMMENT '最后一个强制更新的版本',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='APP配置表';

CREATE TABLE `bq_appoint_luck` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户ID',
  `prize` varchar(8) NOT NULL COMMENT '奖项',
  `number` int(8) NOT NULL COMMENT '抽取多少次获得',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `nouser_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='指定用户奖品表';

CREATE TABLE `bq_arbitrage_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) DEFAULT NULL COMMENT '合约号',
  `depth_uid` int(10) NOT NULL DEFAULT '0' COMMENT '深度用户',
  `depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '深度数据获取路径',
  `spare_depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '备用深度路径',
  `depth_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度买单价格调整幅度（%）',
  `depth_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度卖单价格调整幅度（%）',
  `depth_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所允许的深度最小单',
  `depth_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '深度系统状态(0-停止，1-开启)',
  `scalping` bit(1) NOT NULL DEFAULT b'0' COMMENT '刷单系统状态（0-停止，1-开启）',
  `scalping_frequency_min` int(11) NOT NULL DEFAULT '0' COMMENT '刷单频率最小（笔数/每分钟）',
  `scalping_frequency_max` int(11) NOT NULL COMMENT '刷单频率最大（笔数/每分钟）',
  `scalping_min_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最小量（在最小和最大之间随机）',
  `scalping_max_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最大量（在最小和最大之间随机）',
  `auto_trade` bit(1) NOT NULL DEFAULT b'0' COMMENT '自动对平，当有对手仓的情况下，及时以买一或卖一价对平',
  `hedging` bit(1) NOT NULL DEFAULT b'1' COMMENT '外部市场对冲',
  `hedging_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲买单价格调整幅度（%）',
  `hedging_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲卖单价格调整幅度（%）',
  `scalping_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数范围',
  `scalping_depth_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盘口宽度限制',
  `curr_use_state` smallint(5) NOT NULL DEFAULT '0' COMMENT '当前使用深度（0：主用深度，1：备用深度）',
  `risk_manage` bit(1) NOT NULL DEFAULT b'0' COMMENT '风险控制（根据对冲账户金额限制交易）',
  `depth_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格增量',
  `depth_amount_gap_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最小增量',
  `depth_amount_gap_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最大增量',
  `depth_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数偏离（小数）',
  `depth_size` smallint(5) NOT NULL DEFAULT '0' COMMENT '深度数量',
  `price_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '价格精度',
  `amount_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '数量精度',
  `can_trade_self` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可自成交',
  `speed_type` smallint(5) NOT NULL DEFAULT '5' COMMENT '下单速度（值*300ms）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `f_code_UNIQUE` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 COMMENT='平台套利系统设置';

CREATE TABLE `bq_area_area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(6) NOT NULL,
  `name` varchar(20) NOT NULL,
  `citycode` char(6) NOT NULL DEFAULT '' COMMENT '城市代码',
  PRIMARY KEY (`id`),
  KEY `idx_citycode` (`citycode`)
) ENGINE=InnoDB AUTO_INCREMENT=3145 DEFAULT CHARSET=utf8 COMMENT='城市区域表，此表暂用不到';

CREATE TABLE `bq_area_city` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(6) NOT NULL,
  `name` varchar(20) NOT NULL,
  `provincecode` char(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_provincecode` (`provincecode`)
) ENGINE=InnoDB AUTO_INCREMENT=347 DEFAULT CHARSET=utf8 COMMENT='区域城市表';

CREATE TABLE `bq_area_country` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `code` char(2) NOT NULL DEFAULT '' COMMENT '国际域名缩写',
  `zh_name` char(10) NOT NULL DEFAULT '' COMMENT '中文国家名称',
  `en_name` varchar(50) NOT NULL DEFAULT '' COMMENT '英文国家名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 COMMENT='国家表';

CREATE TABLE `bq_area_province` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` char(6) NOT NULL DEFAULT '' COMMENT '省份代码',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '省份名称',
  `country_code` char(2) NOT NULL DEFAULT 'CN' COMMENT '国家缩写代码',
  PRIMARY KEY (`id`),
  KEY `idx_country_code` (`country_code`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='区域省份表';

CREATE TABLE `bq_article` (
  `article_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '分类id(1公告咨询, 2信息披露)',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '新闻标题',
  `keyword` varchar(80) NOT NULL DEFAULT '' COMMENT '关键词',
  `description` varchar(200) NOT NULL DEFAULT '' COMMENT '描述',
  `username` char(20) NOT NULL DEFAULT '' COMMENT '用户名',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '发布时间',
  `istop` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:普通  1:置顶',
  `displayorder` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未发布 1:发布中',
  `page` tinyblob,
  `language` varchar(16) NOT NULL DEFAULT 'zh_CN' COMMENT '语言',
  PRIMARY KEY (`article_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=534 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_article_content` (
  `article_id` int(10) unsigned NOT NULL,
  `content` longtext COMMENT '新闻内容',
  `credit_log_id` bigint(20) NOT NULL,
  `credit_type_id` int(11) NOT NULL,
  `dateline` bigint(20) NOT NULL,
  `f_user_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_bcex_member` (
  `f_user_email` char(40) NOT NULL COMMENT '用户邮箱',
  `f_user_paypwd` char(32) NOT NULL DEFAULT '' COMMENT '用户资金密码',
  `f_user_pwd` char(32) NOT NULL DEFAULT '' COMMENT '用户登录密码',
  `f_user_tel` varchar(45) DEFAULT '' COMMENT '用户手机号',
  PRIMARY KEY (`f_user_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='bcex用户备用表';

CREATE TABLE `bq_benchmark_price` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `benchmark_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '基准价格',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `dateline` int(10) NOT NULL DEFAULT '0' COMMENT '基准时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COMMENT='记录合约基准价格';

CREATE TABLE `bq_bft_reward_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '币种ID',
  `reason` smallint(5) NOT NULL DEFAULT '0' COMMENT '原因（1、每天登陆 2、邀请奖励）',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '奖励金额',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `note` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=690165 DEFAULT CHARSET=utf8 COMMENT='代币奖励记录';

CREATE TABLE `bq_bft_reward_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '币种id',
  `login_reward` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '登录奖励',
  `invite_reward` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '邀请奖励',
  `bfx_rewarded` bit(1) NOT NULL DEFAULT b'0' COMMENT '持有bfx是否已奖励',
  `register_reward` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '注册奖励',
  `old_register_rewarded` bit(1) NOT NULL DEFAULT b'0' COMMENT '老用户是否已经赠送',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='BFT代币奖励相关设置';

CREATE TABLE `bq_bfx_fee_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coin_id` smallint(5) NOT NULL DEFAULT '0',
  `f_code` varchar(16) NOT NULL DEFAULT '' COMMENT '合约号',
  `part` varchar(10) NOT NULL DEFAULT '' COMMENT '合约分区',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_time` int(11) NOT NULL DEFAULT '0' COMMENT '交易时间',
  `fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵扣的手续费(USDT)',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '扣除金额',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx价格',
  `discount` decimal(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '折扣',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_tradetime` (`f_user_id`,`trade_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=197655 DEFAULT CHARSET=utf8 COMMENT='bfx抵扣手续费记录表';

CREATE TABLE `bq_bfx_fee_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `vip_state` smallint(3) NOT NULL DEFAULT '0' COMMENT '持平台币享vip等级是否开启',
  `fee_state` smallint(3) NOT NULL DEFAULT '0' COMMENT '抵扣手续费是否开启',
  `discount` decimal(8,4) NOT NULL DEFAULT '1.0000' COMMENT '平台币手续费折扣',
  `coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '平台币币种Id',
  `bfx_avg_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx前一日均价',
  `update_pirce_time` int(11) NOT NULL DEFAULT '0' COMMENT '均价更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='平台币交易手续费相关配置';

CREATE TABLE `bq_bfx_min_hold` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '币种id',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `min_hold` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '一天内最小持仓',
  `day_time` int(11) NOT NULL DEFAULT '0' COMMENT '每天0点时间戳',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_daytime` (`f_user_id`,`day_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1064718 DEFAULT CHARSET=utf8 COMMENT='用户一天内持有bfx最小值';

CREATE TABLE `bq_bfx_vip_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `hold_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台币持有量',
  `level_id` smallint(5) NOT NULL DEFAULT '1' COMMENT 'vip等级id',
  `level_name` varchar(45) NOT NULL DEFAULT '' COMMENT 'vip等级名称',
  `note` varchar(45) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='持有平台币享VIP等级设置';

CREATE TABLE `bq_bonus_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '2',
  `rate` decimal(22,12) NOT NULL DEFAULT '0.000000000000' COMMENT '分红比例（一个币可分多少钱）',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '分红金额',
  `total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '当天总手续费',
  `total_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '当天总挖币量+IEO解冻',
  `hold_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '用户平台币币量',
  `frozen_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '用户平台币挂单冻结（币币交易挂单）',
  `day` varchar(45) NOT NULL DEFAULT '' COMMENT '日期',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `note` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18265 DEFAULT CHARSET=utf8 COMMENT='平台分红记录';

CREATE TABLE `bq_bonus_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '状态（0-停止，1-运行中）',
  `begin_time` int(10) NOT NULL DEFAULT '1532059200' COMMENT '分红开始时间',
  `first_done` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='分红设置';

CREATE TABLE `bq_certificate_type` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `certificate_name` varchar(80) DEFAULT NULL COMMENT '证书荣誉名称',
  `create_time` int(10) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_common_credit_log` (
  `credit_log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `credit_type_id` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '积分类别(后台分析统计)(1.注册/2.登录/3.绑定手机/4.绑定Google/5.充值/6.交易/7.购买/8.奖励)',
  `credits` bigint(18) unsigned NOT NULL DEFAULT '0' COMMENT '积分数量',
  `title` varchar(100) NOT NULL DEFAULT '' COMMENT '积分标题',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '积分时间(unix格式，排序快)',
  PRIMARY KEY (`credit_log_id`),
  KEY `idx_uid` (`f_user_id`),
  KEY `idx_dateline` (`dateline`),
  KEY `idx_credit_type_id` (`credit_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=347538 DEFAULT CHARSET=utf8 COMMENT='积分日志表(网站可以手工添加积分/用户自行购买积分/进行一些资料完善奖励/交易奖励/其它奖励)';

CREATE TABLE `bq_common_credit_type` (
  `credit_type_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `credit_type_name` char(20) NOT NULL DEFAULT '' COMMENT '积分类别名称',
  `credit_limit_count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '最多奖励次数 (0:表示不限制 >0:同一个积分类型最多奖励次数）',
  PRIMARY KEY (`credit_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='积分类别表,解决积分规则(一种可以多次添加积分，另一种是只允许添加一次积分)';

CREATE TABLE `bq_common_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '分割盈利',
  `content` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_common_failedlogin` (
  `ip` char(15) NOT NULL DEFAULT '' COMMENT '失败IP',
  `email` char(40) NOT NULL DEFAULT '' COMMENT '失败时的用户名',
  `count` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '尝试次数',
  `lastupdate` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最后一次尝试时间',
  `username` varchar(255) NOT NULL,
  `google_count` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '谷歌验证错误次数',
  `google_last` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '谷歌验证错误最后一次时间',
  PRIMARY KEY (`ip`,`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录失败次数统计，可以根据时间字段自行设置多长时间段内用户登录失败次数并进行限制(用户+ip 限制|ip限制)';

CREATE TABLE `bq_company_award_log` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `title` char(80) NOT NULL DEFAULT '' COMMENT '标题(奖励原因)',
  `commission` decimal(14,6) NOT NULL DEFAULT '0.000000' COMMENT '佣金',
  `commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '佣金比例',
  `award_amount` decimal(14,10) unsigned NOT NULL DEFAULT '0.0000000000' COMMENT '奖励金额(佣金*佣金比例)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '奖励日期,unix时间格式',
  `uid` int(10) NOT NULL DEFAULT '0' COMMENT '下线用户uid',
  `email` char(40) NOT NULL DEFAULT '' COMMENT '下线用户邮箱 ',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易ID(trade_log:trade_id)',
  PRIMARY KEY (`log_id`),
  KEY `idx_dateline` (`dateline`,`f_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3598 DEFAULT CHARSET=utf8 COMMENT='总公司交易收益日志(同表 bq_invite_award_log 结构一样)';

CREATE TABLE `bq_composite_index_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `index_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '综合指数类型',
  `rate` decimal(8,4) NOT NULL DEFAULT '0.0000' COMMENT '权重',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `last` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最新指数价',
  `market` varchar(128) NOT NULL DEFAULT '' COMMENT '市场来源',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最新价更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='综合指数配比详情';

CREATE TABLE `bq_composite_index_type` (
  `type_id` smallint(6) NOT NULL AUTO_INCREMENT,
  `is_using` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用',
  `type_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类型名称',
  `transaction_type_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约类型号(关联综合指数合约类型号)',
  `benchmark_index` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '基准指数',
  `closing_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '每日0点记录成交价格',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='综合指数配比类型';

CREATE TABLE `bq_count_account` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_amount` decimal(18,8) DEFAULT NULL COMMENT '币数',
  `f_type` smallint(6) DEFAULT NULL COMMENT '1 平帐 2 已交割手续费',
  `f_content` varchar(50) DEFAULT NULL COMMENT '描述',
  `f_time` int(11) DEFAULT NULL,
  `f_trans_code` varchar(10) DEFAULT NULL COMMENT '合约号',
  PRIMARY KEY (`f_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='平款及各个已交割合约的手续费金额';

CREATE TABLE `bq_currency_type` (
  `type_id` tinyint(11) unsigned NOT NULL AUTO_INCREMENT,
  `type_name` varchar(15) NOT NULL DEFAULT '' COMMENT '币种名称',
  `description_zh` varchar(1024) DEFAULT '' COMMENT '中文币种描述',
  `state` smallint(5) NOT NULL DEFAULT '1' COMMENT '状态（1：使用中，0：不使用）',
  `recharge_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `max_widthdraw` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `day_max_widthdraw` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `manul_max_widthdraw` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单笔提币需人工审核额度',
  `withdraw_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `min_withdraw` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单笔最少提币数',
  `description_en` varchar(1024) DEFAULT '' COMMENT '英文币种描述',
  `description_jp` varchar(255) DEFAULT NULL COMMENT '日文描述',
  `description_kr` varchar(255) DEFAULT NULL COMMENT '韩文描述',
  `description_hk` varchar(255) DEFAULT NULL COMMENT '繁体描述',
  `erc20` bit(1) NOT NULL DEFAULT b'0',
  `trc20` bit(1) NOT NULL DEFAULT b'0',
  `withdraw_fee_erc20` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'erc20地址提币手续费',
  `recharge_fee_erc20` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'erc20地址充值手续费',
  `withdraw_fee_trc20` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'trc20地址提币手续费',
  `recharge_fee_trc20` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'trc20地址充值手续费',
  `inside_withdraw_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '站内提币手续费',
  `inside_recharge_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '站内充值手续费',
  `enable_recharge` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启充值',
  `enable_withdraw` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启提币',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name_UNIQUE` (`type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='货币类型';

CREATE TABLE `bq_data_broad_cast_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `elite_count` int(5) NOT NULL DEFAULT '10' COMMENT '精英持仓人数',
  `f_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `magnified_multiple` decimal(18,2) NOT NULL DEFAULT '1.00' COMMENT '放大倍数',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='数据播报-持仓总量统计设置';

CREATE TABLE `bq_data_broad_cast_stats` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `buy_hold_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买多持仓总量',
  `elite_buy_hold_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '精英买多持仓总量',
  `elite_buy_ratio` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '精英买多持仓比例',
  `elite_sell_hold_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '精英卖空持仓总量',
  `elite_sell_ratio` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '精英卖空持仓比例',
  `f_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `sell_hold_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖空持仓总量',
  `total_change` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '较上一日变化量（正数代表增加，负数代表减少）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=991 DEFAULT CHARSET=utf8 COMMENT='数据播报-持仓总量统计';

CREATE TABLE `bq_exchange_bfc_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `ori_currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '源币种id',
  `target_currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '目标币种id',
  `ori_currency_name` varchar(15) NOT NULL COMMENT '源币种名称',
  `target_currency_name` varchar(15) NOT NULL COMMENT '目标币种名称',
  `frozen_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `ori_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '源币量',
  `target_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '目标币量',
  `note` varchar(255) NOT NULL COMMENT '备注',
  `order_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '状态（0、已提交，1、已审核，2、转人工审核，3、自动处理中，4、已处理，5、已取消）',
  `oper_time` int(10) NOT NULL DEFAULT '0' COMMENT '操作时间',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '提交记录时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_exchange_bfc_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` int(11) NOT NULL COMMENT '币种id',
  `symbol` varchar(15) NOT NULL COMMENT '币种名称',
  `exchange_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '兑换价格（后台管理设置）',
  `min_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最小兑换数量',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启兑换（默认开启）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `currency_type_id_UNIQUE` (`currency_type_id`),
  UNIQUE KEY `symbol_UNIQUE` (`symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='BFX与BFC互兑设置';

CREATE TABLE `bq_exchange_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `ori_currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '源币种id',
  `target_currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '目标币种id',
  `ori_currency_name` varchar(16) NOT NULL DEFAULT '' COMMENT '源币种名称',
  `target_currency_name` varchar(16) NOT NULL DEFAULT '' COMMENT '目标币种名称',
  `frozen_rate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '冻结幅度',
  `frozen_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `frozen_amount_back` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `ori_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '源币量',
  `target_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '目标币量',
  `present_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '现价',
  `price_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '价格调整幅度',
  `direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '对冲方向（1、买入；2、卖出）',
  `fee_rate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '手续费率',
  `fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `obtain_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '实际获得的目标币量',
  `hedging_times` smallint(5) NOT NULL DEFAULT '0' COMMENT '对冲尝试次数',
  `order_id` varchar(45) NOT NULL DEFAULT '' COMMENT '订单号',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '状态（0、已提交，1、已审核，2、转人工审核，3、自动处理中，4、已处理，5、已取消）',
  `note` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `oper_time` int(10) NOT NULL DEFAULT '0' COMMENT '处理时间',
  `order_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '下单价',
  `avg_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交均价',
  `deal_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交量',
  `deal_cash` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交额',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `user_id_index` (`f_user_id`),
  KEY `ori_currency_index` (`ori_currency_type_id`),
  KEY `target_currency_index` (`target_currency_type_id`),
  KEY `state_index` (`state`)
) ENGINE=InnoDB AUTO_INCREMENT=14032 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_exchange_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` int(11) NOT NULL COMMENT '币种id',
  `symbol` varchar(15) NOT NULL COMMENT '币种符号（大写）',
  `frozen_rate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT 'ckusd兑换该币种所冻结幅度（%）',
  `fee_rate_to_usdt` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '该币种兑换ckusd手续（%）',
  `fee_rate_from_usdt` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT 'ckusd兑换该币种手续费（%）',
  `outside_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000',
  `outside_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000',
  `digits` smallint(5) NOT NULL DEFAULT '0',
  `min_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '外部对冲状态(0-停止，1-开启)',
  `inside_exchange` bit(1) NOT NULL DEFAULT b'0' COMMENT '内部兑换',
  `exchange_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '汇率（内部兑换使用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `currency_type_id_UNIQUE` (`currency_type_id`),
  UNIQUE KEY `symbol_UNIQUE` (`symbol`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='兑换功能设置';

CREATE TABLE `bq_exchange_ticker` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `high` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `low` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `last` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `buy` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `sell` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `vol` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `dateline` int(10) NOT NULL DEFAULT '0',
  `market` varchar(32) NOT NULL DEFAULT '',
  `trans_pair` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `currency_type_id_UNIQUE` (`currency_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_express_news` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dateline` int(10) NOT NULL DEFAULT '0' COMMENT '时间',
  `en_content` text NOT NULL COMMENT '英文内容',
  `ja_content` text NOT NULL COMMENT '日文内容',
  `zh_content` text NOT NULL COMMENT '中文内容',
  `ko_content` text NOT NULL COMMENT '韩文内容',
  `hk_content` text NOT NULL COMMENT '繁体内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3824 DEFAULT CHARSET=utf8 COMMENT='快讯';

CREATE TABLE `bq_for_sosobtc` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(7) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '合约号',
  `position_flag` smallint(3) NOT NULL DEFAULT '0' COMMENT 'sosobtc那边对应的坑位号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='sosoBtc获取数据专用码表';

CREATE TABLE `bq_funding_fee_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约名称',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '开启状态(0-停止，1-开启)',
  `running_time` varchar(45) NOT NULL DEFAULT '' COMMENT '执行时间点，逗号隔开（10:00,22:00）',
  `interest_rate_diff` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率差',
  `divisor` decimal(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '除数',
  `clamp_up` decimal(18,8) NOT NULL DEFAULT '0.20000000' COMMENT '限制区间上边界（%）',
  `clamp_down` decimal(18,8) NOT NULL DEFAULT '-0.20000000' COMMENT '限制区间下边界（%）',
  `fixed_fee_open` bit(1) NOT NULL DEFAULT b'0' COMMENT '固定资金费率是否开启',
  `fixed_fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '固定资金费率',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 COMMENT='资金费配置表';

CREATE TABLE `bq_hedging_balance_notice` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_name` varchar(15) NOT NULL COMMENT '币种名称',
  `email` char(40) NOT NULL COMMENT '邮箱',
  `threshold` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '设定发送邮件的余额临界值',
  `type` smallint(5) NOT NULL COMMENT '类型（1、合约账户；2、币币兑换）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='对冲账户资产通知';

CREATE TABLE `bq_hedging_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_id` int(10) NOT NULL DEFAULT '0' COMMENT '交易id来自trade_log',
  `trade_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易数量',
  `trade_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交价格',
  `trade_direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '交易方向1-买入，2-卖出（相对深度用户而言）',
  `trade_time` int(10) NOT NULL DEFAULT '0' COMMENT '成交时间',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '与深度用户Id',
  `direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '对冲方向（1：买入，2：卖出）',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '对冲价格',
  `process_time` int(10) NOT NULL DEFAULT '0' COMMENT '操作时间',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '记录状态(0:未对冲, 1: 已对冲, 2: 失败)',
  `note` longtext,
  `send_times` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`f_user_id`),
  KEY `idx_transactioncode_state` (`transaction_code`,`state`)
) ENGINE=InnoDB AUTO_INCREMENT=77053 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_hourly_account` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `currency_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '币种数量',
  `currency_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '币种id',
  `hour_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  PRIMARY KEY (`id`),
  KEY `idx_currencyamount` (`currency_amount`),
  KEY `idx_userid_currencyid_hourtime` (`user_id`,`currency_id`,`hour_time`),
  KEY `idx_hourtime` (`hour_time`)
) ENGINE=InnoDB AUTO_INCREMENT=114960061 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_ieo_account` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '币种id',
  `initial` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '初始量（充值-提币）',
  `locked` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '锁仓量',
  `unlocked` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '解锁量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_currency_index` (`f_user_id`,`currency_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='IEO账户';

CREATE TABLE `bq_ieo_recharge_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `currency_type_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '币种id',
  `operation` smallint(6) NOT NULL DEFAULT '0' COMMENT '操作类型(1:充值/2:提现)',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '金额',
  `unlock_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '已解锁',
  `note` varchar(256) NOT NULL DEFAULT '' COMMENT '备注',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `update_time` int(10) NOT NULL DEFAULT '0' COMMENT '更新时间，取消提币，直接减少金额，方便ieo解锁',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='ieo充提记录';

CREATE TABLE `bq_ieo_unlock_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `currency_type_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '币种',
  `total_mine` decimal(18,8) DEFAULT NULL COMMENT '当日挖矿总量',
  `unlock_rate` decimal(18,12) NOT NULL DEFAULT '0.000000000000' COMMENT '解锁比率',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '解锁量',
  `day` varchar(45) DEFAULT NULL COMMENT '日期',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='IEO账户资金解锁记录';

CREATE TABLE `bq_ieo_unlock_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `unlock_rate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '解锁比例',
  `unlock_time` int(10) NOT NULL DEFAULT '0' COMMENT '解锁时间',
  `lock_time_begin` int(10) NOT NULL DEFAULT '0' COMMENT '私募时间区间开始（大于该时间的将按解锁比例释放）',
  `lock_time_end` int(10) NOT NULL DEFAULT '0' COMMENT '私募时间区间结束（小于该时间的将按解锁比例释放）',
  PRIMARY KEY (`id`),
  KEY `idx_unlock_time` (`unlock_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Ieo解锁设置';

CREATE TABLE `bq_index_description` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dateline` bigint(20) NOT NULL COMMENT '创建时间',
  `zh_description` longtext NOT NULL COMMENT '中文指数说明',
  `en_description` longtext NOT NULL COMMENT '英文指数说明',
  `ja_description` longtext,
  `ko_description` longtext NOT NULL COMMENT '韩文指数说明',
  `hk_description` longtext NOT NULL COMMENT '繁体指数说明',
  `type_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约类型code',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_code_UNIQUE` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='指数说明表';

CREATE TABLE `bq_index_state` (
  `id` int(11) NOT NULL,
  `okex_state` smallint(5) NOT NULL COMMENT 'okex指数状态（1：使用中，0：不使用）',
  `huobi_state` smallint(5) NOT NULL COMMENT '火币指数状态（1：使用中，0：不使用）',
  `binance_state` smallint(5) NOT NULL COMMENT '币安指数状态（1：使用中，0：不使用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='指数状态设置\r\n';

CREATE TABLE `bq_invite_award_count` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `day_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '单日奖励总金额',
  `week_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '当周奖励总金额',
  `month_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '当月奖励总金额',
  `total_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '账户奖励总金额',
  `day` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '当天时间，unix时间格式，只保存yyyy-mm-dd这部分的信息',
  PRIMARY KEY (`f_user_id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人奖励总统计汇总';

CREATE TABLE `bq_invite_award_count_day` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '当日奖励汇总金额',
  `day` date NOT NULL COMMENT '当天日期(yyyy-mm-dd)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人奖励日统计汇总';

CREATE TABLE `bq_invite_award_count_month` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '当月奖励汇总金额',
  `month` mediumint(6) NOT NULL DEFAULT '0' COMMENT '月份(数字类型:201405)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`f_user_id`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人奖励月统计汇总';

CREATE TABLE `bq_invite_award_count_week` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '奖励汇总金额',
  `year` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '年份，数字类型 2014',
  `week` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '每n周',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id_year` (`f_user_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人奖励周统计汇总';

CREATE TABLE `bq_invite_award_log` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID(自由经纪人uid或者区域代理uid)',
  `title` char(80) NOT NULL DEFAULT '' COMMENT '标题(奖励原因)',
  `commission` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '佣金',
  `commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '佣金比例',
  `award_amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '奖励金额(佣金*佣金比例)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '奖励日期,unix时间格式',
  `uid` int(10) NOT NULL DEFAULT '0' COMMENT '下线用户uid',
  `email` char(40) NOT NULL DEFAULT '' COMMENT '下线用户邮箱',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易ID(trade_log:trade_id)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`f_user_id`,`dateline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='经纪人邀请奖励日志，每笔交易都会进行奖励。';

CREATE TABLE `bq_invite_click_count` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `click_day_count` int(6) unsigned NOT NULL DEFAULT '0' COMMENT '单日邀请数量',
  `click_week_count` int(8) unsigned NOT NULL COMMENT '周累计',
  `click_month_count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '月累计',
  `click_total_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '总数量',
  `day` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '当天时间，unix时间格式，只保存yyyy-mm-dd这部分的信息',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='经纪人邀请链接统计';

CREATE TABLE `bq_invite_click_count_day` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当日奖励汇总金额',
  `day` date NOT NULL COMMENT '当天日期(yyyy-mm-dd)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广点击日统计汇总';

CREATE TABLE `bq_invite_click_count_month` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当月点击汇总',
  `month` mediumint(6) NOT NULL DEFAULT '0' COMMENT '月份(数字类型:201405)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广点击月统计汇总';

CREATE TABLE `bq_invite_click_count_week` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当周点击汇总',
  `year` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '年份，数字类型 2014',
  `week` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '每n周',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广点击周统计汇总';

CREATE TABLE `bq_invite_click_log` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '日志ID(后期需要分表，不然此表特别的大)',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `referer_url` varchar(250) NOT NULL DEFAULT '' COMMENT '来源url',
  `dateline` int(10) NOT NULL DEFAULT '0' COMMENT '点击时间',
  `ip` char(15) NOT NULL DEFAULT '' COMMENT '点击ip',
  PRIMARY KEY (`log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='经纪人推广邀请链接点击记录，此表为只写表';

CREATE TABLE `bq_last_ticker` (
  `f_ticker_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_ticker_code` varchar(10) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `f_ticker_high` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_low` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_buy` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_sell` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_last` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_vol` decimal(25,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_trades` decimal(25,8) unsigned NOT NULL DEFAULT '0.00000000',
  PRIMARY KEY (`f_ticker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_level_buy_bfx_unlock_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '币种',
  `unlock_rate` decimal(18,15) NOT NULL DEFAULT '0.000000000000000' COMMENT '解锁比例',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '解锁量',
  `record_time` int(11) NOT NULL DEFAULT '0' COMMENT '解锁记录时间',
  `level_buy_record_id` int(11) NOT NULL DEFAULT '0' COMMENT '购买vip的记录id',
  `total_lock` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'vip购买记录对应锁仓量',
  PRIMARY KEY (`id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=104787 DEFAULT CHARSET=utf8 COMMENT='会员购买vip，锁仓Bfx解锁记录';

CREATE TABLE `bq_loan_adjust_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户Id',
  `loan_record_id` int(10) NOT NULL DEFAULT '0' COMMENT '借款订单Id',
  `pledge_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '抵押币种id',
  `pledge_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '抵押币种名称',
  `borrow_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出币种Id',
  `borrow_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '借出币种名称',
  `pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵押数量',
  `adjust_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '调整数量',
  `old_pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '原抵押率',
  `new_pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '新抵押率',
  `direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '调整方向（0、新增，1、追加，-1、减少）',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '调整时间',
  `note` varchar(45) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='质押金调整历史记录';

CREATE TABLE `bq_loan_back_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录Id',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户Id',
  `loan_record_id` int(11) NOT NULL DEFAULT '0' COMMENT '借款订单Id',
  `pledge_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '抵押币种id',
  `pledge_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '抵押币种名称',
  `borrow_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出币种Id',
  `borrow_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '借出币种名称',
  `borrow_uid` int(11) NOT NULL DEFAULT '0' COMMENT '借出方用户Id',
  `borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '借出数量',
  `pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵押数量',
  `day_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率（日）',
  `fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费率',
  `back_all_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '归还数量',
  `back_borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '归还的本金数量',
  `back_pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '释放的抵押数量',
  `interest` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利息',
  `fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `back_time` int(11) NOT NULL DEFAULT '0' COMMENT '还款时间',
  `type` smallint(5) NOT NULL DEFAULT '0' COMMENT '平仓类型（0-清偿，1-失败）',
  `note` varchar(45) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_uid_time` (`f_user_id`,`back_time`),
  KEY `idx_bid_time` (`borrow_uid`,`back_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='质押借币归还记录';

CREATE TABLE `bq_loan_close_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录Id',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户Id',
  `loan_record_id` int(11) NOT NULL DEFAULT '0' COMMENT '借款订单Id',
  `pledge_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '抵押币种id',
  `pledge_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '抵押币种名称',
  `borrow_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出币种Id',
  `borrow_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '借出币种名称',
  `borrow_uid` int(11) NOT NULL DEFAULT '0' COMMENT '借出方用户Id',
  `pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵押数量',
  `back_pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '退回抵押数量',
  `payment_pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵偿本金',
  `interest` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵偿利息',
  `fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `default_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '拖欠数量',
  `day_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率（日）',
  `fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费率',
  `close_result` smallint(5) NOT NULL DEFAULT '0' COMMENT '平仓结果（1、成功，2、失败）',
  `price_index` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格指数',
  `record_time` int(11) NOT NULL DEFAULT '0' COMMENT '平仓时间',
  `note` varchar(45) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_uid_time` (`f_user_id`,`record_time`),
  KEY `idx_bid_time` (`borrow_uid`,`record_time`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='质押借款平仓记录';

CREATE TABLE `bq_loan_rate_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `loan_setting_id` int(11) NOT NULL DEFAULT '0' COMMENT '借贷配置Id',
  `cycle` int(11) NOT NULL DEFAULT '0' COMMENT '周期（天数）',
  `day_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率（日）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='质押借币利率设置';

CREATE TABLE `bq_loan_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录Id',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户Id',
  `pledge_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '抵押币种id',
  `pledge_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '抵押币种名称',
  `borrow_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出币种Id',
  `borrow_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '借出币种名称',
  `borrow_uid` int(11) NOT NULL DEFAULT '0' COMMENT '借出方用户Id',
  `borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '借出数量',
  `received_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '借入到账金额',
  `pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵押数量',
  `pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵押率',
  `day_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率（日）',
  `fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费率',
  `back_borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '已归还的借出数量',
  `back_pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '已释放的抵押数量',
  `interest` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '已付利息',
  `fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '已产生的手续费',
  `payment_pledge_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵偿本金的质押数量',
  `payment_interest` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵偿利息的质押数量',
  `record_time` int(11) NOT NULL DEFAULT '0' COMMENT '借贷时间',
  `payment_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '抵偿的手续费',
  `cycle` int(11) NOT NULL DEFAULT '0' COMMENT '周期（天数）',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '状态（0-提交，1-借款中，2-已还款，3-已平仓',
  `note` varchar(45) NOT NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_uid_time` (`f_user_id`,`record_time`),
  KEY `idx_bid_time` (`borrow_uid`,`record_time`),
  KEY `idx_time` (`record_time`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8 COMMENT='质押借币记录';

CREATE TABLE `bq_loan_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pledge_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '抵押币种id',
  `pledge_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '抵押币种名称',
  `pledge_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '质押数量小数位数',
  `borrow_coin_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出币种Id',
  `borrow_symbol` varchar(15) NOT NULL DEFAULT '' COMMENT '借出币种名称',
  `borrow_uids` varchar(45) NOT NULL DEFAULT '' COMMENT '可借出用户列表（逗号分隔）',
  `borrow_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出数量小数位数',
  `max_borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最大借出数量',
  `min_borrow_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最少借出数量',
  `init_pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '初始抵押率',
  `curr_pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '补仓抵押率',
  `close_pledge_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓抵押率',
  `fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费率',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '状态（0-已关闭，1-进行中）',
  `borrow_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '借出用户方式（0-顺序，1-随机）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='质押借币配置';

CREATE TABLE `bq_luck_draw_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `prize` varchar(20) NOT NULL COMMENT '奖项',
  `prize_name` varchar(20) DEFAULT NULL COMMENT '奖品名称',
  `draw_time` bigint(20) NOT NULL COMMENT '抽奖时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `no_uid_time` (`user_id`,`draw_time`) USING BTREE COMMENT '用户id唯一'
) ENGINE=InnoDB AUTO_INCREMENT=4850 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户抽奖记录';

CREATE TABLE `bq_luck_draw_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '奖品ID',
  `prize` varchar(10) NOT NULL COMMENT '奖项(first,second )',
  `name_zh_CN` varchar(15) NOT NULL COMMENT '中文名称(一等奖)',
  `name_en_US` varchar(20) NOT NULL COMMENT '英文名称(firstprize)',
  `probability` double(10,4) NOT NULL COMMENT '中奖概率',
  `prize_matter` varchar(20) NOT NULL DEFAULT '' COMMENT '实物奖品',
  `prize_usdt` decimal(20,8) NOT NULL COMMENT 'USDT奖品',
  `notes` varchar(15) DEFAULT NULL COMMENT '奖品说明',
  `photo_name` varchar(30) NOT NULL COMMENT '照片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='抽奖设置表';

CREATE TABLE `bq_luck_obtain_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `user_name` varchar(40) NOT NULL COMMENT '用户名(4984****45@qq.com)',
  `prize_id` int(10) NOT NULL COMMENT '奖项id',
  `prize_name_CN` varchar(20) NOT NULL COMMENT '奖项中文名称',
  `prize_name_US` varchar(20) NOT NULL COMMENT '奖项英文名称',
  `prize_matter` varchar(20) NOT NULL DEFAULT '' COMMENT '实物奖励',
  `prize_usdt` decimal(20,8) NOT NULL COMMENT 'usdt奖励',
  `obtain_time` bigint(20) NOT NULL COMMENT '获奖时间',
  `random` bit(1) NOT NULL COMMENT '0，随机抽得；1，管理员指定抽得',
  `notes` varchar(20) NOT NULL DEFAULT '' COMMENT '奖品说明',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `notime` (`obtain_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='获奖记录表';

CREATE TABLE `bq_manager_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `link` varchar(128) NOT NULL,
  `menu_name` varchar(128) NOT NULL,
  `menu_no` varchar(128) NOT NULL,
  `state` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_mark_price_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(15) NOT NULL DEFAULT '' COMMENT '合约号',
  `running` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否启用',
  `fixed` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用固定价格',
  `moving_period` int(11) NOT NULL DEFAULT '1' COMMENT '移动周期',
  `open_width_filter` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '盘口宽度过滤',
  `index_rate_filter` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '指数浮动范围',
  `fixed_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '固定价格值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='标记价格设置';

CREATE TABLE `bq_market_order_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '价格阈值范围比例',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='市价委托设置';

CREATE TABLE `bq_member` (
  `f_user_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `f_user_email` char(40) NOT NULL DEFAULT '' COMMENT '会员邮箱(支持登录)',
  `f_user_email_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '邮箱认证(1:已认证 0:未认证)',
  `f_user_pwd` char(32) NOT NULL DEFAULT '' COMMENT '密码(32位)',
  `f_user_credits` bigint(18) unsigned NOT NULL DEFAULT '0' COMMENT '积分(活跃度)',
  `f_user_level_id` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '用户VIP级别(bq_member_level:level_id)',
  `f_user_state` tinyint(2) NOT NULL DEFAULT '0' COMMENT '用户状态（0:无效,1:有效,-1:冻结）',
  `f_user_salt` char(6) NOT NULL DEFAULT '' COMMENT '密码随机码6位salt',
  `f_user_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '注册时间,unix时间格式',
  `f_real_email` char(40) DEFAULT NULL COMMENT '用户真实邮箱',
  `has_open_position` smallint(3) NOT NULL DEFAULT '0' COMMENT '用户是否开过仓',
  `f_user_origin` char(40) NOT NULL DEFAULT '' COMMENT '用户来源',
  `f_first_login` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'bcex用户是否第一次登陆(0:否,1:是)',
  `f_fee_rate_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '手续费扣费方式（0-USDT，1-BFX）',
  `is_sub_user` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是子账号(0:否,1:是)',
  `sub_user_note` varchar(45) NOT NULL DEFAULT '' COMMENT '子账号备注名',
  `sub_user_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '子账号是否删除(0:否,1:是)',
  `open_invite_management` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启会员管理(0:否,1:是)',
  `f_user_nick_name` char(40) DEFAULT NULL COMMENT '用户昵称',
  `f_user_head_img` varchar(255) DEFAULT '',
  `f_user_identity` smallint(3) NOT NULL DEFAULT '0' COMMENT '用户身份(0:普通用户,1:代理人)',
  `f_user_oms` smallint(3) NOT NULL DEFAULT '0' COMMENT '用户是否属于代理系统(0:不属于,1:属于)',
  PRIMARY KEY (`f_user_id`),
  UNIQUE KEY `uniq_user_email` (`f_user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=534056 DEFAULT CHARSET=utf8 COMMENT='用户基本信息表，支持邮箱登录,邮箱地址必须唯一(f_user_id为1表示系统用户)';

CREATE TABLE `bq_member_amount_trans` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(19,2) DEFAULT NULL,
  `dateline` bigint(20) NOT NULL,
  `f_user_id` bigint(20) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `trade_id` bigint(20) NOT NULL,
  PRIMARY KEY (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_amount_trans_fee` (
  `record_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `title` char(100) NOT NULL DEFAULT '' COMMENT '标题说明',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '金额',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '与其交易ID (trade_log:trade_id)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
  PRIMARY KEY (`record_id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易的第二个操作，会扣除手续费,后期用来代理商和经纪人分成';

CREATE TABLE `bq_member_apply` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '申请人uid',
  `idcard` varchar(20) NOT NULL DEFAULT '' COMMENT '身份证号码',
  `mobile` varchar(15) NOT NULL DEFAULT '' COMMENT '手机号码',
  `address` varchar(80) NOT NULL DEFAULT '' COMMENT '通讯地址',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '申请时间',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:通过审核 0:待审 -1:拒绝(同步bq_member_apply_agent_area)',
  `confirm_dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '审核时间',
  `user_type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '1:自由经纪人 2:区域代理商',
  `reason` varchar(80) NOT NULL DEFAULT '' COMMENT '资料审核未通过原因(通过后，此字段内容要清空)',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='申请经纪人(user_type字段为身份识别字段)';

CREATE TABLE `bq_member_apply_agent_area` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '申请用户ID',
  `province_code` char(6) NOT NULL DEFAULT '' COMMENT '省份代码',
  `province_name` char(6) NOT NULL DEFAULT '' COMMENT '省份名称',
  `country_code` char(10) NOT NULL DEFAULT '' COMMENT '国家代码',
  `country_name` char(20) NOT NULL DEFAULT '' COMMENT '国家名称',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:通过审核 0:待审 -1:拒绝',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='申请区域代理商时，要代理的区域省份信息';

CREATE TABLE `bq_member_apply_idcard` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `photo_1` varchar(250) NOT NULL DEFAULT '' COMMENT '手持身份证照片',
  `photo_2` varchar(250) NOT NULL DEFAULT '' COMMENT '身份证（正面）',
  `photo_3` varchar(250) NOT NULL DEFAULT '' COMMENT '身份证（反面）',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_area` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `country_code` varchar(6) NOT NULL DEFAULT '' COMMENT '国家代码',
  `country_name` varchar(20) NOT NULL DEFAULT '' COMMENT '国家名称',
  `province_code` varchar(6) NOT NULL DEFAULT '' COMMENT '省份代码',
  `province_name` varchar(20) NOT NULL DEFAULT '' COMMENT '省份名称',
  `city_code` varchar(6) NOT NULL DEFAULT '' COMMENT '城市代码',
  `city_name` varchar(20) NOT NULL DEFAULT '' COMMENT '城市名称',
  PRIMARY KEY (`f_user_id`),
  KEY `idx_code` (`country_code`,`province_code`,`city_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员信息所在区域登记表';

CREATE TABLE `bq_member_bind_btc100` (
  `f_id` int(4) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `btc_uid` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT 'btc100网站对应的uid',
  `btc_user_name` varchar(50) NOT NULL DEFAULT '' COMMENT 'btc100用户名',
  `bind_time` int(10) unsigned NOT NULL COMMENT '关系绑定时间,unix时间格式',
  `bind_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '绑定状态',
  PRIMARY KEY (`f_id`),
  UNIQUE KEY `uniq_uid` (`f_user_id`,`btc_uid`)
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_certificate` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `activity_name` varchar(80) DEFAULT NULL COMMENT '大赛活动名称',
  `certificate_id` int(10) NOT NULL COMMENT '证书类型id',
  `certificate_name` varchar(80) DEFAULT NULL COMMENT '证书荣誉名称',
  `issue_time` int(10) NOT NULL COMMENT '颁发时间',
  `user_id` int(10) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_credit_count` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `credit_type_id` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '积分类型',
  `credit_count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '积分次数(每积分一次+1),不包含扣除',
  `credits` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '总积分数量',
  PRIMARY KEY (`id`),
  KEY `idx_user_credit_type` (`f_user_id`,`credit_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户积分类型统计表，每一个用户每一种积分类型对应一条记录，按类型统计.可以用在积分奖励功能(多次奖励积分和一次奖励积分)';

CREATE TABLE `bq_member_deposit_onoff` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `value` smallint(11) unsigned NOT NULL DEFAULT '1' COMMENT '0:未启用(记录不存在时也表示未启用) 1:启用',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户保证金自动追加开关';

CREATE TABLE `bq_member_draw` (
  `f_user_id` int(10) NOT NULL COMMENT '用户ID',
  `f_draw_count` int(11) NOT NULL COMMENT '可抽奖次数',
  `f_already_draw_count` int(11) NOT NULL COMMENT '已抽奖次数',
  `f_produce_fee_usdt` decimal(20,8) NOT NULL COMMENT '已产生usdt手续费',
  `f_produce_fee_usdr` decimal(20,8) NOT NULL COMMENT '已产生usdr手续费',
  `f_surplus_fee` decimal(20,8) NOT NULL COMMENT '取余手续费',
  `address` varchar(200) DEFAULT NULL COMMENT '收货地址(只有是实物奖励才有收货地址)',
  PRIMARY KEY (`f_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户抽奖次数统计表';

CREATE TABLE `bq_member_favorite_market` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL COMMENT '用户id',
  `type` smallint(5) NOT NULL COMMENT '合约类型：1、正式盘，2、模拟盘',
  `codes` varchar(255) DEFAULT NULL COMMENT '合约号('',''逗号隔开)',
  `part` varchar(10) NOT NULL DEFAULT '' COMMENT '分区',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3054 DEFAULT CHARSET=utf8 COMMENT='用户收藏合约（自选）';

CREATE TABLE `bq_member_invite_amount` (
  `f_user_id` int(11) NOT NULL,
  `f_day_click` int(11) NOT NULL COMMENT '日点击量',
  `f_day_success` int(11) NOT NULL COMMENT '日成功量',
  `f_date` int(11) NOT NULL COMMENT '时间',
  PRIMARY KEY (`f_user_id`,`f_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_invite_count` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `day_count` int(6) unsigned NOT NULL DEFAULT '0' COMMENT '单日邀请数量',
  `week_count` int(8) unsigned NOT NULL COMMENT '周累计',
  `month_count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '月累计',
  `total_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '总数量',
  `day` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '当天时间，yyyyMMdd',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='经纪人邀请人数统计,只记录成功的';

CREATE TABLE `bq_member_invite_count_day` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当日成功邀请人数',
  `day` date NOT NULL COMMENT '当天日期(yyyy-mm-dd)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广成功邀请人数统计汇总';

CREATE TABLE `bq_member_invite_count_month` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当月邀请人数汇总',
  `month` mediumint(6) unsigned NOT NULL DEFAULT '0' COMMENT '月份(数字类型:201405)',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广成功邀请人数月统计汇总';

CREATE TABLE `bq_member_invite_count_week` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `count` int(8) unsigned NOT NULL DEFAULT '0' COMMENT '当周成功邀请汇总',
  `year` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '年份，数字类型 2014',
  `week` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '每n周',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`log_id`,`year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='记录经纪人推广成功邀请统计汇总';

CREATE TABLE `bq_member_invite_log` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `invite_uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '受邀请人uid',
  `invite_time` int(10) unsigned NOT NULL COMMENT '邀请时间,unix时间格式',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='经纪人用户邀请关系表';

CREATE TABLE `bq_member_invite_rebate_log` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `bq_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `part` varchar(10) NOT NULL DEFAULT '' COMMENT '合约分区',
  `commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '用户返佣比例',
  `commission_rate_diff` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '返佣比例差',
  `rebate_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '返佣',
  `record_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '返佣时间',
  `sub_commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '下级返佣比例',
  `sub_user_email` char(40) NOT NULL DEFAULT '' COMMENT '下级用户邮箱',
  `sub_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '下级用户id',
  `sub_user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '下级用户等级名称',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易id',
  `user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '用户等级名称',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '2' COMMENT '币种',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`),
  KEY `idx_recordtime_rebateamount_userid_useremail` (`record_time`,`rebate_amount`,`user_id`,`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=1273947 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_invite_rebate_reconcilia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `f_user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '返利金额',
  `cut_off_time` int(10) NOT NULL DEFAULT '0' COMMENT '截止时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=614 DEFAULT CHARSET=utf8 COMMENT='返利统计表';

CREATE TABLE `bq_member_level` (
  `level_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '级别名称',
  `level_credits` bigint(18) unsigned NOT NULL DEFAULT '0' COMMENT '等级所需积分',
  `transaction_commission_rate` decimal(6,5) unsigned NOT NULL DEFAULT '0.00000' COMMENT '期货交易佣金比例',
  `broker_commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '经纪人返佣金比例',
  `withdraws_cash` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '虚拟货币提现手续费',
  `delegation_tel` tinyint(1) NOT NULL DEFAULT '0' COMMENT '电话委托(-1:不支持 0:不限次数 >0支持n次/每)',
  `pending_commission_rate` decimal(6,5) NOT NULL,
  `eat_commission_rate` decimal(6,5) NOT NULL DEFAULT '0.00000',
  `state` smallint(5) NOT NULL DEFAULT '0',
  `level_buy_amount` decimal(20,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '购买VIP所需具体金额，0表示不能购买和使用',
  PRIMARY KEY (`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='用户vip等级表(level_credits值为1000000000的时候，表示付费服务)';

CREATE TABLE `bq_member_level_buy_amount_log` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` tinyint(11) unsigned NOT NULL DEFAULT '0' COMMENT '币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',
  `change_amount` decimal(20,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '增减',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作时间,unix时间格式',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `before_level_name_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '变更前的等级ID',
  `after_level_name_id` tinyint(3) NOT NULL DEFAULT '0' COMMENT '变更后的等级ID',
  `before_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '变更前的级别名称',
  `after_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '变更后的级别名称',
  `bfx_lock` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx锁定量（买vip送bfx）',
  `bfx_unlock` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx已解锁',
  PRIMARY KEY (`log_id`) USING BTREE,
  KEY `idx_f_user_id` (`f_user_id`,`currency_type_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1331 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户购买VIP等级记录';

CREATE TABLE `bq_member_level_buy_rebate_reconcilia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `f_user_email` varchar(45) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `total_buy_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '总买入额',
  `total_bfx_lock` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所有会员总锁仓量',
  `total_bfx_unlock` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所有会员总解锁量',
  `self_bfx_lock` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '自己购买vip的锁仓量',
  `superior` int(11) NOT NULL DEFAULT '0' COMMENT '直接上级会员',
  `superior2` int(11) NOT NULL DEFAULT '0' COMMENT '二级上级（上级的上级）',
  PRIMARY KEY (`id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1017 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_level_buy_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pay_coin_id` tinyint(6) NOT NULL DEFAULT '0' COMMENT '支付币种（默认为BFX）',
  `pay_coin_name` varchar(45) NOT NULL DEFAULT '' COMMENT '支付币种名称（默认为BFX）',
  `exchange_rate` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '与BFX汇率（1BFX等于多少支付币种）',
  `start_unlock_gap_day` int(5) NOT NULL DEFAULT '0' COMMENT '多少天后开始释放',
  `unlock_times` int(5) NOT NULL DEFAULT '0' COMMENT '总释放次数（分多少次释放完）',
  `first_rebate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '一级上级返佣（直接上级）',
  `second_rebate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '二级上级返佣（上级的上级）',
  `back_price` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '回购指导价格',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='等级购买设置';

CREATE TABLE `bq_member_login_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL,
  `ip` char(15) DEFAULT NULL,
  `note` varchar(100) DEFAULT NULL,
  `login_time` int(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_logintime` (`f_user_id`,`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=496968 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_modify_information_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL COMMENT '用户id',
  `f_old_information` char(40) NOT NULL COMMENT '原联系方式',
  `f_new_information` char(40) NOT NULL COMMENT '修改之后的联系方式',
  `f_information_type` smallint(5) NOT NULL COMMENT '修改类型：1-手机，2-邮箱，3-谷歌',
  `f_modify_time` int(10) NOT NULL COMMENT '修改时间',
  `f_modify_ip` char(15) NOT NULL COMMENT '修改ip地址',
  `f_modify_server` smallint(5) NOT NULL COMMENT '修改设备：1-PC，2-api',
  `f_modify_user` char(40) NOT NULL DEFAULT '' COMMENT '修改人',
  `f_operation_type` smallint(5) NOT NULL COMMENT '操作类型：1、注册；2、绑定；3、更改',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_uid` (`f_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=865 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='安全验证修改记录';

CREATE TABLE `bq_member_partner` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '开通api时间',
  `partner` char(16) NOT NULL DEFAULT '' COMMENT '商户标识',
  `secret_key` char(32) NOT NULL DEFAULT '' COMMENT '密钥',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员申请的交易api';

CREATE TABLE `bq_member_payword` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `pwd` char(32) NOT NULL DEFAULT '' COMMENT '支付密码',
  `salt` char(6) NOT NULL DEFAULT '' COMMENT '加密salt',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:启用 0:不启用',
  `f_readszms` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否阅读过上证合约用户须知',
  `failed_times` tinyint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户支付密码，默认用户没有设置密码的时候，不在这里出现记录';

CREATE TABLE `bq_member_rebate_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '返利币种id',
  `coin_name` varchar(45) NOT NULL DEFAULT '' COMMENT '返利币种名称',
  `commission_rate` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '返佣比例',
  `rebate_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '产生返佣的动作（ 1-购买vip返佣）',
  `user_level` smallint(5) NOT NULL DEFAULT '0' COMMENT '用户vip等级',
  `sub_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员账户id',
  `rebate_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '返佣金额',
  `record_time` int(11) NOT NULL DEFAULT '0' COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_f_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2504 DEFAULT CHARSET=utf8 COMMENT='vip会员返佣记录（交易除外）';

CREATE TABLE `bq_member_recharge` (
  `record_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '币种（btc/ltc,gtc)1:btc 2:ltc 3...(后期可添加币种)',
  `operation` smallint(6) NOT NULL DEFAULT '1' COMMENT '操作类型(1:充值/2:提现)',
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '(充值/提现)金额',
  `fee` decimal(18,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '手续费(只在提现的时候有)',
  `receice_card_type` smallint(6) NOT NULL DEFAULT '1' COMMENT '1.普通充值, 2.在线充值,3、奖励',
  `receice_card_no` varchar(128) NOT NULL DEFAULT '' COMMENT '接收卡号(比特币、莱特币钱包地址)',
  `ip` char(15) NOT NULL DEFAULT '' COMMENT '申请地址',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '申请时间',
  `confirm_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '确认时间(充值的时候，需要人工确认后，再手动加款）',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:未受理 1:受理成功 2:拒绝',
  `remark` varchar(200) NOT NULL DEFAULT '' COMMENT '备注说明',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `f_user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `wallet_trade_id` varchar(80) NOT NULL DEFAULT '' COMMENT '钱包交易ID(确认到账)',
  `f_user_name` varchar(255) DEFAULT NULL,
  `usdt_erc20` bit(1) NOT NULL DEFAULT b'0',
  `usdt_trc20` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`record_id`),
  KEY `idx_f_user_id` (`f_user_id`),
  KEY `idx_wallet_trade_id` (`wallet_trade_id`)
) ENGINE=InnoDB AUTO_INCREMENT=171584 DEFAULT CHARSET=utf8 COMMENT='用户“充值/提现"表';

CREATE TABLE `bq_member_redgift` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_amount` decimal(5,3) DEFAULT NULL COMMENT '数量',
  `f_type` smallint(6) DEFAULT NULL COMMENT '类型 1 btc',
  `f_code` varchar(35) CHARACTER SET latin1 DEFAULT NULL COMMENT 'MD5(btc+毫秒级时间戳+5位随机码)',
  `f_state` smallint(6) DEFAULT NULL COMMENT '0 不可用 1 开仓可用 2已使用',
  `f_user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `f_time` int(11) DEFAULT NULL COMMENT '使用时间',
  `f_pendingcode` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`f_id`),
  UNIQUE KEY `un_code` (`f_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_safe_settings` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `f_user_safetype` tinyint(2) unsigned NOT NULL DEFAULT '0' COMMENT '用户安全类型(1短信验证，2google验证，3双重验证)',
  `f_user_zone` varchar(10) NOT NULL DEFAULT '' COMMENT '手机区号',
  `f_user_tel` varchar(40) NOT NULL DEFAULT '' COMMENT '用户电话(手机/固话/海外电话),可能手机号认证',
  `f_user_safegoogle` varchar(20) NOT NULL DEFAULT '' COMMENT ' 只有f_user_safetype值为2,3时才有效',
  `f_user_safequertion` tinyint(1) unsigned NOT NULL COMMENT '用户密保(用密保问题的ID(1|3|4)),以json格式存储',
  `f_user_safeanswer` varchar(500) NOT NULL DEFAULT '' COMMENT '用户密保答案(用密保问题的答案(aa|ce|hh)),以json格式存储',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户安全设置项';

CREATE TABLE `bq_member_settle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `f_settle_time` int(11) NOT NULL DEFAULT '0' COMMENT '结算时间',
  `f_settle_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '结算时价格',
  `f_settle_amount` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '结算的数量',
  `f_settle_gain` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '结算时总盈利',
  `f_settle_loss` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '结算是总亏损',
  `f_settle_apportion` decimal(15,8) NOT NULL DEFAULT '0.00000000' COMMENT '分摊额',
  `f_settle_rate` decimal(5,3) NOT NULL DEFAULT '0.000' COMMENT '分摊比',
  PRIMARY KEY (`id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_status` (
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `reg_ip` char(15) NOT NULL DEFAULT '' COMMENT '用户注册ip',
  `last_time` int(10) unsigned NOT NULL COMMENT '最后登录时间,unix时间格式',
  `last_ip` char(15) NOT NULL DEFAULT '' COMMENT '基本后登录ip',
  `last_comein` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息状态表';

CREATE TABLE `bq_member_transaction` (
  `mt_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户合约挂单号',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_type_id` enum('1','2') NOT NULL DEFAULT '1' COMMENT '成交类型(1:多单开仓,买多/2:空头开仓,卖空)',
  `trade_multiple` decimal(8,4) unsigned NOT NULL DEFAULT '1.0000' COMMENT '杠杆',
  `hold_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓量',
  `lock_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `average_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '合约均价',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `deposit2` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '追加保证金',
  `commission` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '佣金',
  `f_min_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单所需的最小亏损备用金',
  `f_frozen_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单量',
  `f_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单对应的亏损备用金',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额的量',
  `f_totalmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '持仓总价值',
  `f_lockmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '冻结总价值',
  `f_type_forjs` tinyint(1) DEFAULT '0' COMMENT '用于计算的类型 买1卖-1',
  `flag` smallint(5) NOT NULL COMMENT '0-默认，-1-爆仓操作中，其他操作中',
  `flag_time` int(10) NOT NULL DEFAULT '0' COMMENT 'flag设置时间',
  PRIMARY KEY (`mt_id`),
  KEY `idx_code` (`transaction_code`) USING BTREE,
  KEY `idx_tid_typeid` (`transaction_id`,`trade_type_id`) USING BTREE,
  KEY `idx_f_uid` (`f_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=33714574 DEFAULT CHARSET=utf8 COMMENT='用户合约表/持仓表';

CREATE TABLE `bq_member_transaction_overload_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓均价',
  `trade_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '数量',
  `trade_deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `trade_overload_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '爆仓价',
  `trade_force_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '强平价',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `direction` tinyint(1) NOT NULL DEFAULT '1' COMMENT '爆仓的方向(1:多单爆仓 2:卖空爆仓)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '爆仓时间',
  `delegation_id` int(10) NOT NULL DEFAULT '0' COMMENT '委托挂单表主键id(btc1407_pending_delegation:delegation_id)',
  `balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '结余',
  `pending_no` bigint(19) NOT NULL DEFAULT '0' COMMENT '挂单流水号',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '同持仓表',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_code_pending` (`f_user_id`,`transaction_code`,`pending_no`)
) ENGINE=InnoDB AUTO_INCREMENT=605021 DEFAULT CHARSET=utf8 COMMENT='会员合约爆仓日志(表结构与btc1407_trade_log类似)';

CREATE TABLE `bq_member_transaction_settle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_type_id` tinyint(5) NOT NULL DEFAULT '0' COMMENT '成交类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓/5:多单爆仓/6:空单爆仓)',
  `trade_multiple` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '杠杆',
  `hold_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓量',
  `lock_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `average_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '合约均价',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `profit_lost` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈亏金额',
  `apportion` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '分推金额',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结算时间',
  `trade_fee` decimal(18,8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员合约结算历史表,记录一个合约在结算的时候，所有持有此合约会员的盈亏信息(比持仓表多一个盈亏和分推字段)';

CREATE TABLE `bq_member_validate_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `card_back_img` varchar(255) DEFAULT NULL COMMENT '证件背面图片路径',
  `card_due_time` varchar(255) DEFAULT NULL COMMENT '证件到期时间',
  `card_front_img` varchar(255) DEFAULT NULL COMMENT '证件正面图片路径',
  `card_img` varchar(255) DEFAULT NULL COMMENT '证件图片',
  `card_no` varchar(32) DEFAULT NULL COMMENT '证件号码',
  `card_type` varchar(32) DEFAULT NULL,
  `last_validate_time` int(10) DEFAULT NULL COMMENT '最新认证时间',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `real_name` varchar(32) NOT NULL COMMENT '实名认证真实姓名',
  `user_id` int(10) NOT NULL COMMENT '用户id',
  `validate_source` varchar(255) DEFAULT NULL COMMENT '认证来源',
  `validate_state` varchar(32) DEFAULT NULL COMMENT '认证状态',
  `validate_time` int(10) DEFAULT NULL COMMENT '初次认证时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_member_wallet` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` int(10) NOT NULL COMMENT '货币类型',
  `wallet_address` varchar(128) NOT NULL DEFAULT '' COMMENT '钱包地址',
  `wallet_address_pic` varchar(250) NOT NULL DEFAULT '' COMMENT '钱包二维码图片地址(路径进行hash算法存储)',
  `erc20` bit(1) NOT NULL DEFAULT b'0',
  `trc20` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_f_user_id` (`f_user_id`,`currency_type_id`),
  UNIQUE KEY `uniq_f_wallet_address` (`wallet_address`)
) ENGINE=InnoDB AUTO_INCREMENT=20546 DEFAULT CHARSET=utf8 COMMENT='用户钱包表(新用户时时，系统自动从钱包队列表bq_wallet_queue_{btc}中读取一个钱包地址，与其绑定，将记录到此表中，将队列表中的钱包记录删除。一个用户每种币种(btc/ltc)对应一条记录';

CREATE TABLE `bq_member_wallet_address` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '币种ID',
  `wallet_address` char(128) NOT NULL DEFAULT '' COMMENT '钱包地址',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '钱包标签',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认 (1:默认 0:非默认)',
  `erc20` bit(1) NOT NULL DEFAULT b'0',
  `trc20` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `idx_f_user_id` (`f_user_id`),
  KEY `uniq_wallet_address` (`wallet_address`)
) ENGINE=InnoDB AUTO_INCREMENT=3207 DEFAULT CHARSET=utf8 COMMENT='用户提现地址管理表';

CREATE TABLE `bq_members` (
  `f_user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_comment1` varchar(255) DEFAULT NULL,
  `f_comment2` varchar(255) DEFAULT NULL,
  `f_comment3` varchar(255) DEFAULT NULL,
  `f_comment4` varchar(255) DEFAULT NULL,
  `f_comment5` varchar(255) DEFAULT NULL,
  `f_user_area` varchar(255) DEFAULT NULL,
  `f_user_btc100` varchar(255) DEFAULT NULL,
  `f_user_email` varchar(255) DEFAULT NULL,
  `f_user_invert` bigint(20) NOT NULL,
  `f_user_ip` varchar(255) DEFAULT NULL,
  `f_user_level` int(11) NOT NULL,
  `f_user_name` varchar(255) DEFAULT NULL,
  `f_user_pwd` varchar(255) DEFAULT NULL,
  `f_user_safeanswer` varchar(255) DEFAULT NULL,
  `f_user_safegoogle` varchar(255) DEFAULT NULL,
  `f_user_safequertion` varchar(255) DEFAULT NULL,
  `f_user_safetype` varchar(255) DEFAULT NULL,
  `f_user_salt` varchar(255) DEFAULT NULL,
  `f_user_state` varchar(255) DEFAULT NULL,
  `f_user_tel` varchar(255) DEFAULT NULL,
  `f_user_time` bigint(20) NOT NULL,
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_mine_record` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '8' COMMENT '挖矿币种',
  `trade_type` varchar(45) NOT NULL DEFAULT '' COMMENT '交易类型',
  `trade_id` int(12) NOT NULL DEFAULT '0' COMMENT '交易id',
  `trade_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易额',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易手续费',
  `mine_rate` decimal(15,6) NOT NULL DEFAULT '0.000000' COMMENT '挖矿比例',
  `mine_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '挖币量',
  `record_time` int(10) NOT NULL DEFAULT '0' COMMENT '记录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15020 DEFAULT CHARSET=utf8 COMMENT='挖矿记录';

CREATE TABLE `bq_mine_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `currency_type_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '挖矿币种',
  `total_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '挖矿总量',
  `mined_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '平台已挖数量',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启',
  `rebate` bit(1) NOT NULL DEFAULT b'0' COMMENT '邀请返佣是否开启',
  `begin_time` int(10) NOT NULL DEFAULT '1531893600',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='平台挖矿相关设置';

CREATE TABLE `bq_mine_stage_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `num` smallint(5) NOT NULL DEFAULT '0' COMMENT '阶段编号',
  `stage_amount` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '该阶段起始币量',
  `rate` decimal(15,6) NOT NULL DEFAULT '0.000000' COMMENT '挖一个币所需要的手续费',
  PRIMARY KEY (`id`),
  UNIQUE KEY `num_UNIQUE` (`num`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='平台挖矿各阶段参数设置';

CREATE TABLE `bq_notradetime` (
  `h_id` int(11) NOT NULL AUTO_INCREMENT,
  `h_time` int(11) DEFAULT NULL COMMENT '不交易日时间',
  `h_type` smallint(6) DEFAULT NULL COMMENT '类型',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`h_id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_overload_match_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) NOT NULL COMMENT '合约号',
  `f_user_id` int(11) unsigned NOT NULL COMMENT '指定撮合用户ID',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启',
  PRIMARY KEY (`id`),
  KEY `idx_fcode` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COMMENT='未消耗爆仓单指定账户撮合';

CREATE TABLE `bq_price_index_btc_log` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `price` decimal(18,8) unsigned NOT NULL COMMENT '当前指数价格',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '时间',
  `currency_type_id` smallint(5) NOT NULL COMMENT '币种id',
  PRIMARY KEY (`id`),
  KEY `idx_dateline` (`dateline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='btc 币的价格指数记录日志表，每一种币种使用一个独立的指数表记录';

CREATE TABLE `bq_price_index_cache` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `currency_type_id` smallint(6) unsigned NOT NULL COMMENT '币种id(currency_type:currency_type_id)',
  `price` decimal(18,8) unsigned NOT NULL COMMENT '价格指数',
  `otherprice` decimal(18,8) DEFAULT NULL COMMENT '用于上证昨日收盘价',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '时间',
  `type_code` varchar(16) NOT NULL,
  `high` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `low` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `last` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `turnover` decimal(25,8) DEFAULT '0.00000000',
  `vol` decimal(25,8) NOT NULL DEFAULT '0.00000000',
  `cap` decimal(25,8) DEFAULT '0.00000000',
  `change_1h` decimal(18,8) DEFAULT '0.00000000',
  `change_24h` decimal(18,8) DEFAULT '0.00000000',
  `change_7d` decimal(18,8) DEFAULT '0.00000000',
  `trans_pair` varchar(16) DEFAULT '0',
  `market` varchar(16) NOT NULL,
  `ratio` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=331 DEFAULT CHARSET=utf8 COMMENT='价格指数,一种币种对应一个价格指数';

CREATE TABLE `bq_quant_setting` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `part` varchar(10) NOT NULL DEFAULT '' COMMENT '分区',
  `uids` varchar(128) NOT NULL DEFAULT '' COMMENT '量化用户id（逗号隔开）',
  `is_auto_cancel` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启自动撤单',
  `cancel_timeout` int(11) NOT NULL DEFAULT '10' COMMENT '超时撤单时间（秒）',
  `keep_nums` smallint(5) NOT NULL DEFAULT '40' COMMENT '保留挂单笔数',
  `auto_duiping` bit(1) NOT NULL DEFAULT b'1' COMMENT '量化用户自动对平',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='量化用户相关配置';

CREATE TABLE `bq_redgift` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_amount` decimal(5,3) DEFAULT NULL,
  `f_code` varchar(32) CHARACTER SET latin1 DEFAULT NULL COMMENT '红包码 MD5(btc+毫秒级时间戳+5位随机码)',
  `f_time` int(11) DEFAULT NULL COMMENT '分发时间',
  `f_state` smallint(6) DEFAULT NULL COMMENT '1 可用 2不可用',
  `f_user_id` int(11) DEFAULT NULL COMMENT '分发给经纪人',
  PRIMARY KEY (`f_id`),
  UNIQUE KEY `un_code` (`f_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_spot_arbitrage_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) DEFAULT NULL COMMENT '交易对',
  `depth_uid` int(10) NOT NULL DEFAULT '0' COMMENT '深度用户',
  `depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '深度数据获取路径',
  `spare_depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '备用深度路径',
  `depth_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度买单价格调整幅度（%）',
  `depth_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度卖单价格调整幅度（%）',
  `depth_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所允许的深度最小单',
  `depth_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '深度系统状态(0-停止，1-开启)',
  `scalping` bit(1) NOT NULL DEFAULT b'0' COMMENT '刷单系统状态（0-停止，1-开启）',
  `scalping_frequency_min` int(11) NOT NULL DEFAULT '0' COMMENT '刷单频率最小（笔数/每分钟）',
  `scalping_frequency_max` int(11) NOT NULL COMMENT '刷单频率最大（笔数/每分钟）',
  `scalping_min_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最小量（在最小和最大之间随机）',
  `scalping_max_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最大量（在最小和最大之间随机）',
  `hedging` bit(1) NOT NULL DEFAULT b'1' COMMENT '外部市场对冲',
  `hedging_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲买单价格调整幅度（%）',
  `hedging_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲卖单价格调整幅度（%）',
  `scalping_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数范围',
  `scalping_depth_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盘口宽度限制',
  `curr_use_state` smallint(5) NOT NULL DEFAULT '0' COMMENT '当前使用深度（0：主用深度，1：备用深度）',
  `risk_manage` bit(1) NOT NULL DEFAULT b'1' COMMENT '风险控制（根据对冲账户金额限制交易）',
  `depth_buy_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘价格增量',
  `depth_sell_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘价格增量',
  `depth_buy_amount_other_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘数量额外叠加最小量',
  `depth_sell_amount_other_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘数量额外叠加最小量',
  `depth_buy_amount_other_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘数量额外叠加最大量',
  `depth_sell_amount_other_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘数量额外叠加最大量',
  `depth_buy_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实买盘数量最小量',
  `depth_sell_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实卖盘数量最小量',
  `depth_buy_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实买盘数量最大量',
  `depth_sell_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实卖盘数量最大量',
  `depth_buy_backhand_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘反手挂单差价',
  `depth_sell_backhand_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘反手挂单差价',
  `open_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
  `depth_self` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否系统自己挂单(或者引入外部深度)',
  `depth_backhand` bit(1) NOT NULL DEFAULT b'1' COMMENT '手否反手挂单',
  `depth_increasing` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否递增叠加',
  `depth_reset_open_price` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否充值开盘价',
  `can_trade_self` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可以自成交',
  `price_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '价格精度',
  `amount_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '数量精度',
  `depth_size` smallint(5) NOT NULL DEFAULT '0' COMMENT '盘口数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `f_code_UNIQUE` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='币币套利系统设置';

CREATE TABLE `bq_spot_better_token_arbitrage_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `depth_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度买单价格调整幅度（%）',
  `depth_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度卖单价格调整幅度（%）',
  `depth_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所允许的深度最小单',
  `depth_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '深度系统状态(0-停止，1-开启)',
  `scalping` bit(1) NOT NULL DEFAULT b'0' COMMENT '刷单系统状态（0-停止，1-开启）',
  `scalping_frequency_min` int(11) NOT NULL DEFAULT '0' COMMENT '刷单频率最小（笔数/每分钟）',
  `scalping_frequency_max` int(11) NOT NULL COMMENT '刷单频率最大（笔数/每分钟）',
  `scalping_min_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最小量（在最小和最大之间随机）',
  `scalping_max_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最大量（在最小和最大之间随机）',
  `scalping_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数范围',
  `scalping_depth_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盘口宽度限制',
  `depth_buy_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘价格增量',
  `depth_sell_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘价格增量',
  `depth_buy_amount_other_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘数量额外叠加最小量',
  `depth_sell_amount_other_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘数量额外叠加最小量',
  `depth_buy_amount_other_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘数量额外叠加最大量',
  `depth_sell_amount_other_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘数量额外叠加最大量',
  `depth_buy_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实买盘数量最小量',
  `depth_sell_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实卖盘数量最小量',
  `depth_buy_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实买盘数量最大量',
  `depth_sell_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实卖盘数量最大量',
  `depth_buy_backhand_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买盘反手挂单差价',
  `depth_sell_backhand_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖盘反手挂单差价',
  `open_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
  `depth_backhand` bit(1) NOT NULL DEFAULT b'1' COMMENT '手否反手挂单',
  `depth_increasing` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否递增叠加',
  `depth_reset_open_price` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否充值开盘价',
  `price_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '价格精度',
  `amount_digits` smallint(5) NOT NULL DEFAULT '0' COMMENT '数量精度',
  `depth_size` smallint(5) NOT NULL DEFAULT '0' COMMENT '盘口数量',
  `volatility` decimal(5,4) NOT NULL DEFAULT '0.0000' COMMENT '达到该振幅邮箱提醒',
  `reminder_open` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否提醒',
  `reminder_email` char(40) DEFAULT '' COMMENT '提醒邮箱',
  `depth_buy_limit_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `depth_sell_limit_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `check_time` bigint(13) NOT NULL DEFAULT '0',
  `cycle_time` int(5) NOT NULL DEFAULT '0',
  `cycle_buy_size` int(5) NOT NULL DEFAULT '0',
  `cycle_sell_size` int(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='币币套利系统设置';

CREATE TABLE `bq_spot_better_token_trade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `average_price` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `broker_id` int(11) NOT NULL DEFAULT '0',
  `create_on` bigint(13) NOT NULL DEFAULT '0',
  `deal_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `deal_quote_amount` decimal(18,8) DEFAULT NULL,
  `entrust_price` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `is_self` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否自成交',
  `not_strike` decimal(18,8) DEFAULT NULL,
  `open_amount` decimal(18,8) DEFAULT NULL,
  `order_id` bigint(20) NOT NULL,
  `pair_code` varchar(40) NOT NULL DEFAULT '',
  `quote_amount` decimal(18,8) DEFAULT NULL,
  `side` varchar(40) NOT NULL DEFAULT '',
  `source_info` varchar(40) DEFAULT NULL,
  `status` smallint(3) NOT NULL DEFAULT '0',
  `symbol` varchar(40) NOT NULL DEFAULT '',
  `system_order_type` varchar(40) NOT NULL DEFAULT '',
  `trun_over` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `update_on` bigint(13) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id_UNIQUE` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_spot_hedging_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '交易对',
  `trade_id` int(10) NOT NULL DEFAULT '0' COMMENT '交易id来自trade_log',
  `trade_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易数量',
  `trade_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交价格',
  `trade_direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '交易方向1-买入，2-卖出（相对深度用户而言）',
  `trade_time` int(10) NOT NULL DEFAULT '0' COMMENT '成交时间',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '与深度用户Id',
  `direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '对冲方向（1：买入，2：卖出）',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '对冲价格',
  `process_time` int(10) NOT NULL DEFAULT '0' COMMENT '操作时间',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '记录状态(0:未对冲, 1: 已对冲, 2: 失败)',
  `note` longtext,
  `send_times` smallint(5) NOT NULL DEFAULT '0',
  `coin_name` varchar(10) NOT NULL DEFAULT '' COMMENT '币种名称',
  `market_coin_name` varchar(10) NOT NULL DEFAULT '' COMMENT '市场币种名称',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid` (`f_user_id`) USING BTREE,
  KEY `idx_state` (`state`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17005 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_spot_market` (
  `id` smallint(6) unsigned NOT NULL AUTO_INCREMENT,
  `market_name` varchar(20) NOT NULL DEFAULT '' COMMENT '交易区名称',
  `coin_name` varchar(15) NOT NULL DEFAULT '' COMMENT '交易区币种',
  `coin_id` int(11) NOT NULL DEFAULT '1' COMMENT '交易区币种id',
  `is_using` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用',
  `index_address` varchar(255) DEFAULT NULL COMMENT '指数地址',
  `index_value` decimal(18,8) DEFAULT NULL COMMENT '指数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='交易区表(币币交易)';

CREATE TABLE `bq_spot_market_coin_pair` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `coin_pair_name` varchar(10) NOT NULL DEFAULT '' COMMENT '交易对名称',
  `coin_name` varchar(15) NOT NULL DEFAULT '' COMMENT '交易对币种',
  `coin_id` int(11) NOT NULL DEFAULT '1' COMMENT '交易对币种id',
  `state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '交易对状态(1:正常 2:关闭 3:其他)',
  `latest_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最新价格',
  `highest_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最高价格',
  `lowest_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最低价格',
  `market_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '交易区id',
  `market_coin_id` int(11) NOT NULL DEFAULT '1' COMMENT '交易区币种id',
  `market_coin_name` varchar(45) NOT NULL DEFAULT '' COMMENT '交易区币种名称',
  `coin_buy_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '交易对币种买入费比例',
  `coin_sell_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '交易对币种卖出费比例',
  `min_trade_nums` decimal(14,4) NOT NULL DEFAULT '0.0000' COMMENT '最小交易数量',
  `min_float_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最小浮动价格',
  `decimals` varchar(45) NOT NULL DEFAULT '4' COMMENT '交易数量保留小数位数',
  `suspend` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否暂停',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='现货交易对(币币交易)';

CREATE TABLE `bq_spot_quant_setting` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uids` varchar(128) NOT NULL DEFAULT '' COMMENT '量化用户id（逗号隔开）',
  `is_auto_cancel` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启自动撤单',
  `cancel_timeout` int(11) NOT NULL DEFAULT '10' COMMENT '超时撤单时间（秒）',
  `keep_nums` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='币币交易-量化用户相关配置';

CREATE TABLE `bq_stock_setting` (
  `id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `am_begin_time` int(11) NOT NULL DEFAULT '34200' COMMENT '上午开市时间',
  `am_end_time` int(11) NOT NULL DEFAULT '41400' COMMENT '上午闭市时间',
  `pm_begin_time` int(11) NOT NULL DEFAULT '46800' COMMENT '下午开市时间',
  `pm_end_time` int(11) NOT NULL DEFAULT '54000' COMMENT '下午闭市时间',
  `holiday_url` varchar(128) NOT NULL DEFAULT '' COMMENT '假期判断api',
  `holiday_url_using` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用假期判断',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约号id',
  `mon_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周一是否为交易日（默认是）',
  `mon_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周一交易时间,逗号","隔开,"-"连接开始和结束',
  `tue_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周二是否为交易日（默认是）',
  `tue_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周二交易时间,逗号","隔开,"-"连接开始和结束',
  `wed_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周三是否为交易日（默认是）',
  `wed_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周三交易时间,逗号","隔开,"-"连接开始和结束',
  `thu_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周四是否为交易日（默认是）',
  `thu_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周四交易时间,逗号","隔开,"-"连接开始和结束',
  `fri_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周五是否为交易日（默认是）',
  `fri_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周五交易时间,逗号","隔开,"-"连接开始和结束',
  `sat_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周六是否为交易日（默认是）',
  `sat_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周六交易时间,逗号","隔开,"-"连接开始和结束',
  `sun_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周日是否为交易日（默认是）',
  `sun_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周日交易时间,逗号","隔开,"-"连接开始和结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='股指参数设置';

CREATE TABLE `bq_stop_loss` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `stop_loss_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1止盈 2止损',
  `transaction_member_id` int(11) NOT NULL DEFAULT '0' COMMENT '持仓ID',
  `stop_loss_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '止损价',
  `pending_trans_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '委托成交',
  `trade_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1 买多, 2卖空',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '添加记录时间',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '挂单类型（0：限价委托;1：市价委托）',
  `pending_amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '挂单数量',
  PRIMARY KEY (`id`),
  KEY `idx_ccid` (`transaction_member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86962 DEFAULT CHARSET=utf8 COMMENT='止损表';

CREATE TABLE `bq_sub_member` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `mother_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '母账户id',
  `sub_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '子账号id',
  `dateline` int(10) NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3138 DEFAULT CHARSET=utf8 COMMENT='母子账户关系表';

CREATE TABLE `bq_sub_member_info` (
  `f_user_id` int(10) NOT NULL COMMENT '子账号id',
  `dateline` int(10) NOT NULL DEFAULT '0' COMMENT '时间',
  `f_user_email` char(40) NOT NULL DEFAULT '' COMMENT '子账号登录名',
  `unencrypted_password` char(32) NOT NULL DEFAULT '' COMMENT '子账号密码（未加密）',
  `allow_ip` varchar(512) NOT NULL DEFAULT '' COMMENT '访问IP',
  `app_key` varchar(45) NOT NULL DEFAULT '' COMMENT '访问公钥',
  `app_secret` varchar(45) NOT NULL DEFAULT '' COMMENT '访问私钥',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='批量创建子账号信息';

CREATE TABLE `bq_sub_member_transfer_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '划转数量',
  `currency_name` varchar(15) NOT NULL DEFAULT '' COMMENT '币种名称',
  `currency_type_id` tinyint(11) NOT NULL DEFAULT '0' COMMENT '币种id',
  `dateline` int(10) NOT NULL DEFAULT '0' COMMENT '时间',
  `mother_user_email` char(40) NOT NULL DEFAULT '' COMMENT '母账户用户名',
  `mother_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '母账户id',
  `operation` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作类型(1:母账户转入子账户/2:子账户转出到母账户)',
  `remark` varchar(150) NOT NULL DEFAULT '' COMMENT '备注说明',
  `sub_user_email` char(40) NOT NULL DEFAULT '' COMMENT '子账户用户名',
  `sub_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '子账户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3475 DEFAULT CHARSET=utf8 COMMENT='母子账户划转钱包记录';

CREATE TABLE `bq_suggest` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '建议用户ID',
  `f_user_email` varchar(40) CHARACTER SET latin1 NOT NULL DEFAULT '' COMMENT '建议用户邮箱',
  `f_sug_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '类型 1.页面错误 2. 逻辑错误 3 Bug 4其他',
  `f_sug_content` text NOT NULL COMMENT '内容',
  `f_sug_time` int(10) NOT NULL DEFAULT '0' COMMENT '提交时间',
  `f_sug_state` smallint(1) NOT NULL DEFAULT '0' COMMENT '状态 0 未处理 1 采纳 2拒绝',
  `f_sug_reply` varchar(250) NOT NULL DEFAULT '' COMMENT '采纳回复',
  `f_sug_endtime` int(10) NOT NULL DEFAULT '0' COMMENT '回复时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_surecome` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(25) DEFAULT NULL,
  `user_pwd` varchar(32) DEFAULT NULL,
  `user_thpwd` varchar(32) DEFAULT NULL,
  `user_role` varchar(512) DEFAULT NULL,
  `user_salt` char(6) DEFAULT NULL,
  `user_lastip` char(20) DEFAULT NULL,
  `user_last_time` bigint(20) DEFAULT NULL,
  `google_no` varchar(32) DEFAULT NULL,
  `pwd_count` tinyint(1) unsigned DEFAULT '0' COMMENT '密码错误次数',
  `pwd_error_time` int(10) unsigned DEFAULT '0' COMMENT '密码错误最后一次时间',
  `google_count` tinyint(1) unsigned DEFAULT '0' COMMENT '谷歌验证错误次数',
  `google_error_time` int(10) unsigned DEFAULT '0' COMMENT '谷歌验证错误最后一次时间',
  `user_coins` varchar(128) DEFAULT NULL COMMENT '币币风控，按交易对分配权限',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_template_member_trade` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '会员与交易对应关系ID',
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易ID',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '交易时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_trade` (`f_user_id`,`trade_id`,`dateline`),
  KEY `idx_dateline` (`dateline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员交易记录关系表(多对多),一个交易，需要将进行这个交易的双方与交易的对应关系都写入此表一份';

CREATE TABLE `bq_template_member_trade_detail` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `f_buy_trade_id` int(12) unsigned NOT NULL COMMENT '买方对应的交易ID',
  `f_buy_uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '买家uid',
  `f_buy_user_email` char(40) NOT NULL DEFAULT '' COMMENT '买家用户邮箱',
  `f_sell_uid` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '卖家uid',
  `f_sell_user_email` char(40) NOT NULL DEFAULT '' COMMENT '卖家用户邮箱',
  `f_sell_trade_id` int(10) unsigned NOT NULL COMMENT '卖方对应的交易id',
  `trade_buy_type_id` enum('1','2','3','4') NOT NULL DEFAULT '1' COMMENT '成交类型ID(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓，见_trade_log:trade_type_id)',
  `trade_sell_type_id` enum('1','2','3','4') NOT NULL DEFAULT '1' COMMENT '同上',
  `trade_flag` enum('1','2','3') NOT NULL DEFAULT '1' COMMENT '交易类型标识(1:普通交易, 2:交割 3:强平)',
  `trade_trans_dirction` enum('1','2') NOT NULL DEFAULT '1' COMMENT '这笔交易的成交方向(1:买 2:卖)',
  `trade_price` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '成交价格',
  `trade_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '成交数量',
  `trade_time` int(10) unsigned NOT NULL COMMENT '成交时间,unix时间格式',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易详情表';

CREATE TABLE `bq_template_pending_delegation` (
  `delegation_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '委托编号',
  `pending_no` bigint(19) NOT NULL COMMENT '挂单流水号',
  `delegation_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '委托时间,unix时间格式',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作方向(1:买 2:卖)',
  `delegation_count` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量',
  `delegation_price` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托价格',
  `deal_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '已成交数量',
  `undeal_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '未成交数量',
  `multiple` smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆',
  `deposit` decimal(9,4) NOT NULL DEFAULT '0.0000' COMMENT '保证金',
  `priority` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '挂单成交优先级',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `flag` smallint(5) NOT NULL DEFAULT '0' COMMENT '0默认，1交易中，2撤单中（其他）',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额',
  PRIMARY KEY (`delegation_id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托挂单表（非全部成交）';

CREATE TABLE `bq_template_pending_delegation_cache` (
  `delegation_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '委托编号',
  `pending_no` bigint(19) NOT NULL COMMENT '挂单流水号',
  `delegation_time` int(10) unsigned NOT NULL COMMENT '委托时间,unix时间格式',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作方向',
  `delegation_count` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量',
  `delegation_price` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托价格',
  `deal_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '已成交数量',
  `undeal_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '未成交数量',
  `multiple` smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆',
  `deposit` decimal(9,4) NOT NULL DEFAULT '0.0000' COMMENT '保证金',
  `priority` tinyint(3) unsigned NOT NULL COMMENT '挂单成交优先级(1:开仓, 2:平仓, 3:爆仓)',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `flag` smallint(5) NOT NULL DEFAULT '0' COMMENT '0默认，1交易中，2撤单中（其他）',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额',
  PRIMARY KEY (`delegation_id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='委托挂单表（非全部成交）';

CREATE TABLE `bq_template_pending_log` (
  `log_id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `delegation_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '委托编号',
  `pending_no` bigint(19) NOT NULL COMMENT '挂单流水号',
  `delegation_time` int(10) unsigned NOT NULL COMMENT '委托时间,unix时间格式',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_type_id` tinyint(1) NOT NULL DEFAULT '0' COMMENT '操作类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓)',
  `delegation_count` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托数量',
  `delegation_price` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '委托价格',
  `multiple` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '杠杆',
  `deal_count` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '已成交数量',
  `undeal_count` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '未成交数量',
  `deposit` decimal(9,4) NOT NULL DEFAULT '0.0000' COMMENT '保证金',
  `action` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1:成交/2:结算/3:强平/4:撤单',
  `dateline` int(10) unsigned NOT NULL COMMENT '操作时间,unix时间格式',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `flag` smallint(5) NOT NULL DEFAULT '0' COMMENT '0表示正常挂单日志，1标识交易异常时转过来的挂单',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id` (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_template_trade_log` (
  `trade_id` int(12) unsigned NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_type_id` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '成交类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓/5:多单爆仓/6:空单爆仓)',
  `trade_trans_dirction` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '这笔交易的成交方向',
  `trade_average_price` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '成交均价',
  `trade_count` decimal(8,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '成交数量',
  `trade_multiple` smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '杠杆',
  `trade_deposit` decimal(9,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '保证金',
  `trade_income` decimal(13,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈亏额',
  `trade_fee` decimal(8,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '手续费',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '成交时间,unix时间格式',
  PRIMARY KEY (`trade_id`),
  KEY `idx_trade_time` (`trade_time`,`trade_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='交易记录日志表。一个交易会产生两个用户对这个交易的操作记录，基本一份是更新，另一份是写入新记录';

CREATE TABLE `bq_trans_no_relation` (
  `trans_no` varchar(255) NOT NULL,
  `record_id` bigint(20) NOT NULL,
  `trans_type` int(11) NOT NULL,
  PRIMARY KEY (`trans_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bq_transaction` (
  `transaction_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `transaction_begin_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'unix时间格式（旧合约清算时，自动写入新合约信息，将并时间自动调整为30分钟后的时间点）',
  `transaction_end_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
  `transaction_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '合约状态(1:正常 2:清算中 3:关闭)',
  `latest_transaction_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最新成交价',
  `transaction_highest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最高报价',
  `transaction_lowest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最低报价',
  `sort` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '合约排序值, YYMM  (1月 2日 3周 4季 5上证)',
  `multiple` varchar(20) NOT NULL DEFAULT '' COMMENT '具有的杠杆(1,5,10,20,50)',
  `multiple_state` varchar(20) NOT NULL DEFAULT '' COMMENT '每个杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `trade_coefficient` decimal(9,4) NOT NULL COMMENT '交易系数(分母)',
  `transaction_type` smallint(6) NOT NULL DEFAULT '1' COMMENT '合约类型(1 btc合约 2 ltc合约 3sz合约)',
  `transaction_type_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约类型号',
  `suspend` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否暂停',
  `transfer_in_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '合约可转入',
  `transfer_out_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '可约可转出',
  `transaction_kind` smallint(6) NOT NULL DEFAULT '0' COMMENT '合约类别（0-虚拟货币，1-股指）',
  `transaction_show_name` varchar(100) NOT NULL DEFAULT '' COMMENT '合约显示名称',
  `transaction_show_name_en` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_jp` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_kr` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_hk` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uniq_transaction_code` (`transaction_code`),
  KEY `idx_transaction_begin_time` (`transaction_begin_time`),
  KEY `idx_type` (`transaction_type`),
  KEY `idx_state` (`transaction_state`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8 COMMENT='合约表（当前进行中的合约/已经清算完的合约/未开始的合约）';

CREATE TABLE `bq_transaction_fee_rate` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `level_id` int(10) NOT NULL DEFAULT '0' COMMENT '等级ID',
  `level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '等级名称',
  `pending_commission_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '挂单佣金比例',
  `eat_commission_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '吃单佣金比例',
  PRIMARY KEY (`id`),
  KEY `idx_fcode_levelid` (`f_code`,`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=806 DEFAULT CHARSET=utf8 COMMENT='合约设置独立手续费率';

CREATE TABLE `bq_transaction_introduce` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `type` smallint(5) NOT NULL DEFAULT '1' COMMENT '合约类型：1、正式盘，2、模拟盘',
  `part` varchar(10) NOT NULL DEFAULT '' COMMENT '分区',
  `en_introduce` longtext COMMENT '英文介绍',
  `ja_introduce` longtext COMMENT '日文介绍',
  `zh_introduce` longtext COMMENT '中文介绍',
  `ko_introduce` longtext COMMENT '韩文介绍',
  `hk_introduce` longtext COMMENT '繁体介绍',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8 COMMENT='合约介绍详情';

CREATE TABLE `bq_transaction_partition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_cn` varchar(45) NOT NULL DEFAULT '' COMMENT '分区中文名称',
  `name_en` varchar(45) NOT NULL DEFAULT '' COMMENT '分区英文名称',
  `name_jp` varchar(45) NOT NULL DEFAULT '' COMMENT '分区日文名称',
  `name_kr` varchar(255) DEFAULT NULL COMMENT '韩文名称',
  `name_hk` varchar(255) DEFAULT NULL COMMENT '繁体名称',
  `sort` smallint(5) NOT NULL DEFAULT '0' COMMENT '排序',
  `codes` varchar(256) NOT NULL DEFAULT '' COMMENT '合约列表（逗号分隔）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='合约分区设置';

CREATE TABLE `bq_transaction_rate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL COMMENT '合约号',
  `uprate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的上波动比例',
  `downrate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的下波动比例',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_transaction_settle` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '结算价格',
  `trans_num` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '成交量',
  `system_profit_loss` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '系统盈亏',
  `rate` decimal(8,3) NOT NULL DEFAULT '0.000' COMMENT '净盈利部分分摊比例',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结算时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合约结算表公共表(一个合约一条记录)';

CREATE TABLE `bq_transaction_settle_apportion_member` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约ID',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合约系统亏损用户分推表(只有当bq_transaction_settle:system_profit_loss值为负数的时候，才会将参与分推用户的信息记录在这个表里)';

CREATE TABLE `bq_transaction_type` (
  `type_id` smallint(6) unsigned NOT NULL AUTO_INCREMENT COMMENT '合约类型id',
  `type_name` varchar(20) NOT NULL COMMENT '合约类型名称',
  `type_code` varchar(10) NOT NULL COMMENT '合约类型号',
  `default_multiple` varchar(20) DEFAULT NULL COMMENT '合约默认杠杆(1,5,10,20,50)',
  `default_multiple_state` varchar(20) DEFAULT NULL COMMENT '默认杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `default_coefficient` decimal(9,4) DEFAULT NULL COMMENT '默认交易系数(分母)',
  `is_using` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用中',
  `index_address` varchar(255) DEFAULT NULL COMMENT '指数地址',
  `index_value` decimal(18,8) DEFAULT NULL COMMENT '指数',
  `min_kc_nums` decimal(14,4) DEFAULT NULL COMMENT '最小开仓数量',
  `min_float_price` decimal(18,8) DEFAULT NULL COMMENT '最小浮动价格',
  `max_kdc_nums` decimal(18,4) NOT NULL DEFAULT '0.0000' COMMENT '最大可开多仓数量',
  `max_kkc_nums` decimal(18,4) NOT NULL DEFAULT '0.0000' COMMENT '最大可开空仓数量',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name_UNIQUE` (`type_name`),
  UNIQUE KEY `type_code_UNIQUE` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8 COMMENT='合约类型表';

CREATE TABLE `bq_transfer_with_otc_record` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量',
  `currency_name` varchar(15) NOT NULL DEFAULT '' COMMENT '币种名称',
  `currency_type_id` int(11) NOT NULL DEFAULT '0' COMMENT '币种id',
  `direction` int(11) NOT NULL DEFAULT '2' COMMENT '方向(1：转入，2：转出)',
  `f_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `frozen_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结数量',
  `note` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `oper_time` int(11) NOT NULL DEFAULT '0' COMMENT '操作时间',
  `record_time` int(11) NOT NULL DEFAULT '0' COMMENT '记录时间',
  `state` smallint(3) NOT NULL DEFAULT '1' COMMENT '状态(1：提交，人工处理，2：成功，3：失败)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=285 DEFAULT CHARSET=utf8 COMMENT='币币账户与OTC账户互转，人工处理记录';

CREATE TABLE `bq_usdrate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ex_type` smallint(6) DEFAULT NULL COMMENT '货币类型(1 美元对人民币)',
  `ex_rate` decimal(8,4) DEFAULT NULL COMMENT '美元对人民币的汇率 1美元=?人民币',
  `ex_time` int(11) DEFAULT NULL,
  `ex_use` tinyint(1) DEFAULT NULL COMMENT '当前是使用汇率 1为使用, 0 为未使用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_user_api_setting` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `delay_time` int(10) NOT NULL DEFAULT '0' COMMENT '延迟时间(毫秒)',
  `is_open` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启(默认关闭)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='api延迟设置';

CREATE TABLE `bq_user_api_white` (
  `f_user_id` int(10) NOT NULL COMMENT '用户id',
  `add_time` int(10) NOT NULL DEFAULT '0' COMMENT '添加时间',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='api白名单';

CREATE TABLE `bq_user_app_key` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `app_key` varchar(45) NOT NULL DEFAULT '',
  `app_secret` varchar(45) NOT NULL DEFAULT '',
  `allow_ip` varchar(512) NOT NULL DEFAULT '' COMMENT '允许的ip列表',
  `update_time` int(10) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `over_time` int(10) NOT NULL DEFAULT '0' COMMENT '过期时间',
  `state` smallint(4) NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`f_user_id`),
  UNIQUE KEY `app_key_UNIQUE` (`app_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='api接口AppKey';

CREATE TABLE `bq_user_monitor` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户ID',
  `threshold` decimal(20,8) NOT NULL DEFAULT '0.00000000' COMMENT '资金余额低于多少时平仓',
  `price_rate` decimal(20,8) NOT NULL DEFAULT '0.00500000' COMMENT '平仓价格比率',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='被监控的用户表';

CREATE TABLE `bq_user_open_limit` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `max_kdc_nums` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最大开多仓',
  `max_kkc_nums` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最大开空仓',
  `add_time` int(10) NOT NULL DEFAULT '0' COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=149 DEFAULT CHARSET=utf8 COMMENT='用户合约开仓限制';

CREATE TABLE `bq_user_risk_control` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `add_time` int(10) NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='提币和兑换风控';

CREATE TABLE `bq_user_trade_white` (
  `f_user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `add_time` int(10) NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`f_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户交易白名单（不受风控影响）';

CREATE TABLE `bq_wallet_queue_btc` (
  `wallet_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wallet_address` varchar(128) NOT NULL DEFAULT '' COMMENT '钱包地址',
  `wallet_pic` varchar(250) NOT NULL DEFAULT '' COMMENT '图片地址',
  `currency_type_id` smallint(4) NOT NULL DEFAULT '0',
  `erc20` bit(1) NOT NULL DEFAULT b'0',
  `trc20` bit(1) NOT NULL DEFAULT b'0' COMMENT 'trc20地址',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '状态（0-未使用，1-锁定）',
  PRIMARY KEY (`wallet_id`),
  UNIQUE KEY `un_wallet_address` (`wallet_address`)
) ENGINE=InnoDB AUTO_INCREMENT=20933 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='钱包存储队列，一量钱包被使用后，将从此表删除，转存储到bq_member_waller_btc(后期可以使用mq代码，只要一个address值就可以了)';

CREATE TABLE `bq_wallet_trans_btc` (
  `f_trans_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_wallet_id` varchar(255) DEFAULT NULL,
  `f_category` varchar(50) DEFAULT NULL,
  `f_trans_type` int(11) DEFAULT NULL,
  `f_account` varchar(255) DEFAULT NULL,
  `f_address` varchar(255) DEFAULT NULL,
  `f_credit` decimal(18,8) DEFAULT NULL,
  `f_debit` decimal(18,8) DEFAULT NULL,
  `f_debit_fee` decimal(18,8) DEFAULT NULL,
  `f_amount` decimal(18,8) DEFAULT NULL,
  `f_balance` decimal(18,8) DEFAULT NULL,
  `f_confirms` int(11) DEFAULT NULL,
  `f_txid` varchar(255) DEFAULT NULL,
  `f_time` int(11) DEFAULT NULL,
  `f_timereceived` int(11) DEFAULT NULL,
  `f_state` int(11) DEFAULT NULL,
  `f_comment1` varchar(255) DEFAULT NULL,
  `f_comment2` varchar(255) DEFAULT NULL,
  `f_comment3` varchar(255) DEFAULT NULL,
  `f_comment4` varchar(255) DEFAULT NULL,
  `f_comment5` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`f_trans_id`),
  UNIQUE KEY `uq_id` (`f_address`,`f_amount`,`f_txid`,`f_time`)
) ENGINE=InnoDB AUTO_INCREMENT=35447 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_work_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_create_time` bigint(20) NOT NULL,
  `f_file` varchar(255) DEFAULT NULL,
  `f_from_order_state` varchar(255) DEFAULT NULL,
  `f_order_code` varchar(255) DEFAULT NULL,
  `f_order_type` varchar(255) DEFAULT NULL,
  `f_reply_content` varchar(255) DEFAULT NULL,
  `f_theme` varchar(255) DEFAULT NULL,
  `f_to_order_state` varchar(255) DEFAULT NULL,
  `f_update_time` bigint(20) NOT NULL,
  `f_user_email` varchar(255) DEFAULT NULL,
  `f_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`f_user_id`),
  KEY `order_code_index` (`f_order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3327 DEFAULT CHARSET=utf8;

CREATE TABLE `bq_work_order_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_create_time` bigint(20) NOT NULL,
  `f_direction` varchar(255) DEFAULT NULL,
  `f_file` varchar(255) DEFAULT NULL,
  `f_order_code` varchar(255) DEFAULT NULL,
  `f_reply_content` varchar(255) DEFAULT NULL,
  `f_order_id` bigint(20) NOT NULL,
  `f_user_name` varchar(255) DEFAULT NULL COMMENT '回复人员名称',
  PRIMARY KEY (`id`),
  KEY `order_code_index` (`f_order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=20997 DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_account_contract` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL,
  `f_code` varchar(40) DEFAULT NULL COMMENT '合约号',
  `f_initbtc` decimal(18,8) DEFAULT NULL COMMENT '初始btc数量',
  `f_btc` decimal(18,8) DEFAULT NULL COMMENT '开仓可用',
  `f_lockbtc` decimal(18,8) DEFAULT NULL COMMENT '合约冻结资金',
  `f_settled` decimal(18,8) DEFAULT NULL COMMENT '已结算资金',
  PRIMARY KEY (`f_id`),
  KEY `idx_uid_code` (`f_user_id`,`f_code`),
  KEY `idx_fcode` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=286 DEFAULT CHARSET=utf8 COMMENT='合约钱包';

CREATE TABLE `delivery_arbitrage_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_code` varchar(40) NOT NULL COMMENT '合约号',
  `depth_uid` int(10) NOT NULL DEFAULT '0' COMMENT '深度用户',
  `depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '深度数据获取路径',
  `spare_depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '备用深度路径',
  `depth_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度买单价格调整幅度（%）',
  `depth_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度卖单价格调整幅度（%）',
  `depth_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所允许的深度最小单',
  `depth_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '深度系统状态(0-停止，1-开启)',
  `scalping` bit(1) NOT NULL DEFAULT b'0' COMMENT '刷单系统状态（0-停止，1-开启）',
  `scalping_frequency_min` int(11) NOT NULL DEFAULT '0' COMMENT '刷单频率最小（笔数/每分钟）',
  `scalping_frequency_max` int(11) NOT NULL COMMENT '刷单频率最大（笔数/每分钟）',
  `scalping_min_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最小量（在最小和最大之间随机）',
  `scalping_max_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最大量（在最小和最大之间随机）',
  `auto_trade` bit(1) NOT NULL DEFAULT b'0' COMMENT '自动对平，当有对手仓的情况下，及时以买一或卖一价对平',
  `hedging` bit(1) NOT NULL DEFAULT b'1' COMMENT '外部市场对冲',
  `hedging_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲买单价格调整幅度（%）',
  `hedging_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲卖单价格调整幅度（%）',
  `scalping_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数范围',
  `scalping_depth_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盘口宽度限制',
  `curr_use_state` smallint(5) NOT NULL DEFAULT '0' COMMENT '当前使用深度（0：主用深度，1：备用深度）',
  `risk_manage` bit(1) NOT NULL DEFAULT b'0' COMMENT '风险控制（根据对冲账户金额限制交易）',
  `depth_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格增量',
  `depth_amount_gap_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最小增量',
  `depth_amount_gap_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最大增量',
  `depth_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数偏离（小数）',
  `depth_size` smallint(5) NOT NULL DEFAULT '0' COMMENT '深度数量',
  `price_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '价格精度',
  `amount_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '数量精度',
  `can_trade_self` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可自成交',
  `speed_type` smallint(5) NOT NULL DEFAULT '5' COMMENT '下单速度（值*300ms）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `f_code_UNIQUE` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='平台套利系统设置';

CREATE TABLE `delivery_auto_create_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `delivery_type` varchar(45) NOT NULL DEFAULT '' COMMENT '交割类型',
  `end_time` varchar(45) NOT NULL DEFAULT '16:00' COMMENT '交割时间点',
  `is_open` tinyint(1) NOT NULL,
  `multiple` varchar(20) NOT NULL DEFAULT '',
  `multiple_state` varchar(20) NOT NULL DEFAULT '',
  `trade_coefficient` decimal(9,4) NOT NULL,
  `transaction_kind` smallint(6) NOT NULL DEFAULT '0' COMMENT '合约种类',
  `transaction_type_id` smallint(6) NOT NULL COMMENT '合约类型id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_hedging_record` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(40) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_id` int(10) NOT NULL DEFAULT '0' COMMENT '交易id来自trade_log',
  `trade_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '交易数量',
  `trade_price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '成交价格',
  `trade_direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '交易方向1-买入，2-卖出（相对深度用户而言）',
  `trade_time` int(10) NOT NULL DEFAULT '0' COMMENT '成交时间',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '与深度用户Id',
  `direction` smallint(5) NOT NULL DEFAULT '0' COMMENT '对冲方向（1：买入，2：卖出）',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '对冲价格',
  `process_time` int(10) NOT NULL DEFAULT '0' COMMENT '操作时间',
  `state` smallint(5) NOT NULL DEFAULT '0' COMMENT '记录状态(0:未对冲, 1: 已对冲, 2: 失败)',
  `note` longtext,
  `send_times` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`f_user_id`),
  KEY `idx_transactioncode_state` (`transaction_code`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_last_ticker` (
  `f_ticker_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_ticker_code` varchar(40) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `f_ticker_high` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_low` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_buy` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_sell` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_last` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_vol` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_trades` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  PRIMARY KEY (`f_ticker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_member_invite_rebate_log` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `bq_code` varchar(40) NOT NULL DEFAULT '' COMMENT '合约号',
  `commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '用户返佣比例',
  `commission_rate_diff` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '返佣比例差',
  `rebate_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '返佣',
  `record_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '返佣时间',
  `sub_commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '下级返佣比例',
  `sub_user_email` char(40) NOT NULL DEFAULT '' COMMENT '下级用户邮箱',
  `sub_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '下级用户id',
  `sub_user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '下级用户等级名称',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易id',
  `user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '用户等级名称',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '2' COMMENT '币种',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`),
  KEY `idx_recordtime_rebateamount_userid_useremail` (`record_time`,`rebate_amount`,`user_id`,`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=494 DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_member_transaction` (
  `mt_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户合约挂单号',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(40) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_type_id` enum('1','2') NOT NULL DEFAULT '1' COMMENT '成交类型(1:多单开仓,买多/2:空头开仓,卖空)',
  `trade_multiple` decimal(8,4) unsigned NOT NULL DEFAULT '1.0000' COMMENT '杠杆',
  `hold_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓量',
  `lock_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `average_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '合约均价',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `deposit2` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '追加保证金',
  `commission` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '佣金',
  `f_min_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单所需的最小亏损备用金',
  `f_frozen_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单量',
  `f_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单对应的亏损备用金',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '保证金来源于余额的量',
  `f_totalmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '持仓总价值',
  `f_lockmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '冻结总价值',
  `f_type_forjs` tinyint(1) DEFAULT '0' COMMENT '用于计算的类型 买1卖-1',
  `flag` smallint(5) NOT NULL COMMENT '0-默认，-1-爆仓操作中，其他操作中',
  `flag_time` int(10) NOT NULL DEFAULT '0' COMMENT 'flag设置时间',
  PRIMARY KEY (`mt_id`),
  KEY `idx_code` (`transaction_code`) USING BTREE,
  KEY `idx_tid_typeid` (`transaction_id`,`trade_type_id`) USING BTREE,
  KEY `idx_f_uid` (`f_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3579146 DEFAULT CHARSET=utf8 COMMENT='用户合约表/持仓表';

CREATE TABLE `delivery_member_transaction_overload_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(40) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓均价',
  `trade_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '数量',
  `trade_deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `trade_overload_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '爆仓价',
  `trade_force_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '强平价',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `direction` tinyint(1) NOT NULL DEFAULT '1' COMMENT '爆仓的方向(1:多单爆仓 2:卖空爆仓)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '爆仓时间',
  `delegation_id` int(10) NOT NULL DEFAULT '0' COMMENT '委托挂单表主键id(btc1407_pending_delegation:delegation_id)',
  `balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '结余',
  `pending_no` bigint(19) NOT NULL DEFAULT '0' COMMENT '挂单流水号',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '同持仓表',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_code_pending` (`f_user_id`,`transaction_code`,`pending_no`)
) ENGINE=InnoDB AUTO_INCREMENT=359 DEFAULT CHARSET=utf8 COMMENT='会员合约爆仓日志(表结构与btc1407_trade_log类似)';

CREATE TABLE `delivery_member_transaction_settle` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_type_id` tinyint(5) NOT NULL DEFAULT '0' COMMENT '成交类型(1:多单开仓/2:多单平仓/3:空头开仓/4:空头平仓/5:多单爆仓/6:空单爆仓)',
  `trade_multiple` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '杠杆',
  `hold_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓量',
  `lock_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `average_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '合约均价',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `profit_lost` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈亏金额',
  `apportion` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '分推金额',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结算时间',
  `trade_fee` decimal(18,8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员合约结算历史表,记录一个合约在结算的时候，所有持有此合约会员的盈亏信息(比持仓表多一个盈亏和分推字段)';

CREATE TABLE `delivery_no_trade_time` (
  `h_id` int(11) NOT NULL AUTO_INCREMENT,
  `h_time` int(11) DEFAULT NULL COMMENT '不交易日时间',
  `h_type` smallint(6) DEFAULT NULL COMMENT '类型',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0',
  PRIMARY KEY (`h_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_overload_match_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(45) NOT NULL COMMENT '合约号',
  `f_user_id` int(11) unsigned NOT NULL COMMENT '指定撮合用户ID',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启',
  PRIMARY KEY (`id`),
  KEY `idx_fcode` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='未消耗爆仓单指定账户撮合';

CREATE TABLE `delivery_quant_setting` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `uids` varchar(128) NOT NULL DEFAULT '' COMMENT '量化用户id（逗号隔开）',
  `is_auto_cancel` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启自动撤单',
  `cancel_timeout` int(11) NOT NULL DEFAULT '10' COMMENT '超时撤单时间（秒）',
  `keep_nums` smallint(5) NOT NULL DEFAULT '40' COMMENT '保留挂单笔数',
  `auto_duiping` bit(1) NOT NULL DEFAULT b'1' COMMENT '量化用户自动对平',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='量化用户相关配置';

CREATE TABLE `delivery_statistical_form` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL DEFAULT '',
  `contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `statistical_time` int(10) NOT NULL,
  `total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `total_trade_user` int(10) NOT NULL,
  `trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_user` int(10) NOT NULL,
  `rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单个合约返佣金额',
  `total_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '总返佣金额',
  `total_bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返佣总额',
  `trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费',
  `total_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费总额',
  `bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返利',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=916 DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_statistical_hourly_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL DEFAULT '',
  `contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `hourly_login` int(10) NOT NULL,
  `hourly_register` int(10) NOT NULL,
  `statistical_time` int(10) NOT NULL,
  `total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `total_register` int(10) NOT NULL,
  `total_trade_user` int(10) NOT NULL,
  `trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_user` int(10) NOT NULL,
  `rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单个合约返佣金额',
  `total_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '总返佣金额',
  `total_bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返佣总额',
  `trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费',
  `total_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费总额',
  `bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返利',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='每小时统计网站数据';

CREATE TABLE `delivery_stock_setting` (
  `id` int(6) unsigned NOT NULL AUTO_INCREMENT,
  `am_begin_time` int(11) NOT NULL DEFAULT '34200' COMMENT '上午开市时间',
  `am_end_time` int(11) NOT NULL DEFAULT '41400' COMMENT '上午闭市时间',
  `pm_begin_time` int(11) NOT NULL DEFAULT '46800' COMMENT '下午开市时间',
  `pm_end_time` int(11) NOT NULL DEFAULT '54000' COMMENT '下午闭市时间',
  `holiday_url` varchar(128) NOT NULL DEFAULT '' COMMENT '假期判断api',
  `holiday_url_using` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用假期判断',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约号id',
  `mon_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周一是否为交易日（默认是）',
  `mon_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周一交易时间,逗号","隔开,"-"连接开始和结束',
  `tue_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周二是否为交易日（默认是）',
  `tue_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周二交易时间,逗号","隔开,"-"连接开始和结束',
  `wed_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周三是否为交易日（默认是）',
  `wed_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周三交易时间,逗号","隔开,"-"连接开始和结束',
  `thu_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周四是否为交易日（默认是）',
  `thu_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周四交易时间,逗号","隔开,"-"连接开始和结束',
  `fri_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周五是否为交易日（默认是）',
  `fri_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周五交易时间,逗号","隔开,"-"连接开始和结束',
  `sat_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周六是否为交易日（默认是）',
  `sat_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周六交易时间,逗号","隔开,"-"连接开始和结束',
  `sun_state` bit(1) NOT NULL DEFAULT b'1' COMMENT '周日是否为交易日（默认是）',
  `sun_trade_time` varchar(100) NOT NULL DEFAULT '' COMMENT '周日交易时间,逗号","隔开,"-"连接开始和结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='股指参数设置';

CREATE TABLE `delivery_stop_loss` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `stop_loss_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1止盈 2止损',
  `transaction_member_id` int(11) NOT NULL DEFAULT '0' COMMENT '持仓ID',
  `stop_loss_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '止损价',
  `pending_trans_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '委托成交',
  `trade_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1 买多, 2卖空',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '添加记录时间',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '挂单类型（0：限价委托;1：市价委托）',
  `pending_amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '挂单数量',
  PRIMARY KEY (`id`),
  KEY `idx_ccid` (`transaction_member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=244 DEFAULT CHARSET=utf8 COMMENT='止损表';

CREATE TABLE `delivery_trade_log_reconcilia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `time` int(10) NOT NULL DEFAULT '0' COMMENT '时间,每月1号0点,unix时间格式',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `trade_income` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈亏额',
  `transaction_code` varchar(40) NOT NULL DEFAULT '' COMMENT '合约号',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约id',
  PRIMARY KEY (`id`),
  KEY `idx_trade_log_reconcilia_f_user_id` (`f_user_id`),
  KEY `idx_trade_log_reconcilia_time` (`time`)
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8 COMMENT='成交盈亏及手续费统计';

CREATE TABLE `delivery_transaction` (
  `transaction_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `delivery_type` varchar(45) NOT NULL DEFAULT '' COMMENT '交割类型(日交割:''d'',当周交割:''w'',次周交割:''nw'',当月交割:''m'',次月交割:''nm'',季度交割:''q'',次季度交割:''nq'',半年交割:''hy'',年交割:''y'',)',
  `transaction_begin_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'unix时间格式（旧合约清算时，自动写入新合约信息，将并时间自动调整为30分钟后的时间点）',
  `transaction_end_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
  `transaction_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '合约状态(1:正常 2:清算中 3:关闭)',
  `latest_transaction_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最新成交价',
  `transaction_highest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最高报价',
  `transaction_lowest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最低报价',
  `sort` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '合约排序值, YYMM  (1月 2日 3周 4季 5上证)',
  `multiple` varchar(20) NOT NULL DEFAULT '' COMMENT '具有的杠杆(1,5,10,20,50)',
  `multiple_state` varchar(20) NOT NULL DEFAULT '' COMMENT '每个杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `trade_coefficient` decimal(9,4) NOT NULL COMMENT '交易系数(分母)',
  `transaction_type` smallint(6) NOT NULL DEFAULT '1' COMMENT '合约类型(1 btc合约 2 ltc合约 3sz合约)',
  `transaction_type_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约类型号',
  `suspend` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否暂停',
  `transfer_in_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '合约可转入',
  `transfer_out_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '可约可转出',
  `transaction_kind` smallint(6) NOT NULL DEFAULT '0' COMMENT '合约类别（0-虚拟货币，1-股指）',
  `transaction_show_name` varchar(100) NOT NULL DEFAULT '' COMMENT '合约显示名称',
  `transaction_show_name_en` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_jp` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_kr` varchar(45) NOT NULL DEFAULT '',
  `transaction_show_name_hk` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uniq_transaction_code` (`transaction_code`),
  KEY `idx_transaction_begin_time` (`transaction_begin_time`),
  KEY `idx_type` (`transaction_type`),
  KEY `idx_state` (`transaction_state`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='合约表（当前进行中的合约/已经清算完的合约/未开始的合约）';

CREATE TABLE `delivery_transaction_fee_rate` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `level_id` int(10) NOT NULL DEFAULT '0' COMMENT '等级ID',
  `level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '等级名称',
  `pending_commission_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '挂单佣金比例',
  `eat_commission_rate` decimal(6,5) NOT NULL DEFAULT '0.00000' COMMENT '吃单佣金比例',
  PRIMARY KEY (`id`),
  KEY `idx_fcode_levelid` (`f_code`,`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COMMENT='合约设置独立手续费率';

CREATE TABLE `delivery_transaction_partition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_cn` varchar(45) NOT NULL DEFAULT '' COMMENT '分区中文名称',
  `name_en` varchar(45) NOT NULL DEFAULT '' COMMENT '分区英文名称',
  `name_jp` varchar(45) NOT NULL DEFAULT '' COMMENT '分区日文名称',
  `name_kr` varchar(255) DEFAULT NULL COMMENT '韩文名称',
  `name_hk` varchar(255) DEFAULT NULL COMMENT '繁体名称',
  `sort` smallint(5) NOT NULL DEFAULT '0' COMMENT '排序',
  `codes` varchar(256) NOT NULL DEFAULT '' COMMENT '合约列表（逗号分隔）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='合约分区设置';

CREATE TABLE `delivery_transaction_rate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(45) NOT NULL COMMENT '合约号',
  `uprate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的上波动比例',
  `downrate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的下波动比例',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `delivery_transaction_settle` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约ID',
  `transaction_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `price` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '结算价格',
  `trans_num` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '成交量',
  `system_profit_loss` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '系统盈亏',
  `rate` decimal(8,3) NOT NULL DEFAULT '0.000' COMMENT '净盈利部分分摊比例',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结算时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合约结算表公共表(一个合约一条记录)';

CREATE TABLE `delivery_transaction_type` (
  `type_id` smallint(6) unsigned NOT NULL AUTO_INCREMENT COMMENT '合约类型id',
  `type_name` varchar(20) NOT NULL COMMENT '合约类型名称',
  `type_code` varchar(10) NOT NULL COMMENT '合约类型号',
  `default_multiple` varchar(20) DEFAULT NULL COMMENT '合约默认杠杆(1,5,10,20,50)',
  `default_multiple_state` varchar(20) DEFAULT NULL COMMENT '默认杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `default_coefficient` decimal(9,4) DEFAULT NULL COMMENT '默认交易系数(分母)',
  `is_using` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用中',
  `index_address` varchar(255) DEFAULT NULL COMMENT '指数地址',
  `index_value` decimal(18,8) DEFAULT NULL COMMENT '指数',
  `min_kc_nums` decimal(14,4) DEFAULT NULL COMMENT '最小开仓数量',
  `min_float_price` decimal(18,8) DEFAULT NULL COMMENT '最小浮动价格',
  `max_kdc_nums` decimal(18,4) NOT NULL DEFAULT '0.0000' COMMENT '最大可开多仓数量',
  `max_kkc_nums` decimal(18,4) NOT NULL DEFAULT '0.0000' COMMENT '最大可开空仓数量',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name_UNIQUE` (`type_name`),
  UNIQUE KEY `type_code_UNIQUE` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='合约类型表';

CREATE TABLE `delivery_user_open_limit` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(45) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `max_kdc_nums` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最大开多仓',
  `max_kkc_nums` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '最大开空仓',
  `add_time` int(10) NOT NULL DEFAULT '0' COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户合约开仓限制';

CREATE TABLE `n_contract_partition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `part` varchar(45) NOT NULL DEFAULT '' COMMENT '分区名称',
  `currency_type_id` smallint(6) NOT NULL DEFAULT '0' COMMENT '币种Id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='合约分区表';

CREATE TABLE `other_tradesdata` (
  `d_id` int(11) NOT NULL AUTO_INCREMENT,
  `d_amount` decimal(9,3) DEFAULT NULL,
  `d_price` decimal(9,3) DEFAULT NULL,
  `d_oid` int(11) DEFAULT NULL,
  `d_type` char(1) DEFAULT NULL,
  `d_time` int(11) DEFAULT NULL,
  `d_flag` char(1) DEFAULT NULL COMMENT '1 btc100 2 OK',
  PRIMARY KEY (`d_id`),
  KEY `idx_flag` (`d_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `spot_statistical_form` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `statistical_time` int(10) NOT NULL COMMENT '记录时间',
  `total_trade_user` int(10) NOT NULL COMMENT '所有交易对的交易总人数',
  `total_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'BFX抵扣手续费总额',
  `total_market_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '市场币种返利总额（卖出返利）',
  `total_coin_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '本币手续费总额',
  `total_market_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '市场币种返利总额',
  `total_bfx_rebate` decimal(18,8) NOT NULL COMMENT 'bfx抵扣返佣总额',
  `code` varchar(10) NOT NULL DEFAULT '' COMMENT '交易对',
  `market` varchar(10) NOT NULL DEFAULT '' COMMENT '市场币种名称',
  `coin` varchar(10) NOT NULL DEFAULT '' COMMENT '本币种名称',
  `trade_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '真实交易额',
  `trade_user` int(10) NOT NULL COMMENT '单个交易对交易人数',
  `buy_trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '买入手续费',
  `sell_trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '卖出手续费',
  `buy_trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'BFX抵扣买入手续费',
  `sell_trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'BFX抵扣卖出手续费',
  `coin_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '本币种返利（买入返利）',
  `market_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '市场币种返利（卖出返利）',
  `bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返利',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4337 DEFAULT CHARSET=utf8 COMMENT='币币交易每日统计报表';

CREATE TABLE `statistical_form` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL DEFAULT '',
  `contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `statistical_time` int(10) NOT NULL,
  `today_login` int(10) NOT NULL,
  `today_register` int(10) NOT NULL,
  `total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `total_register` int(10) NOT NULL,
  `total_trade_user` int(10) NOT NULL,
  `trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `trade_user` int(10) NOT NULL,
  `rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单个合约返佣金额',
  `total_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '总返佣金额',
  `total_bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返佣总额',
  `trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费',
  `total_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费总额',
  `bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返利',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33147 DEFAULT CHARSET=utf8;

CREATE TABLE `statistical_hourly_form` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约可用总额',
  `deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约保证金总量',
  `hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约持仓量',
  `hourly_login` int(10) NOT NULL COMMENT '每小时登录用户数量',
  `hourly_register` int(10) NOT NULL COMMENT '每小时注册用户数量',
  `statistical_time` int(10) NOT NULL COMMENT '统计时间（前一个小时的时间点）',
  `total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费总额',
  `total_register` int(10) NOT NULL COMMENT '总注册人数',
  `total_trade_user` int(10) NOT NULL COMMENT '总交易人数',
  `trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约真实交易总量',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '该合约交易手续费金额',
  `trade_user` int(10) NOT NULL COMMENT '该合约交易人数',
  `rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '单个合约返佣金额',
  `total_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '总返佣金额',
  `total_bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返佣总额',
  `trade_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费',
  `total_bfx_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx抵扣手续费总额',
  `bfx_rebate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT 'bfx返利',
  PRIMARY KEY (`id`,`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1016385 DEFAULT CHARSET=utf8 COMMENT='每小时统计网站数据';

CREATE TABLE `temp_fee_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) NOT NULL,
  `fee` decimal(25,10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8106 DEFAULT CHARSET=utf8;

CREATE TABLE `trade_log` (
  `trade_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `f_user_id` bigint(20) NOT NULL,
  `trade_average_price` decimal(19,2) DEFAULT NULL,
  `trade_count` decimal(19,2) DEFAULT NULL,
  `trade_deposit` decimal(19,2) DEFAULT NULL,
  `trade_fee` decimal(19,2) DEFAULT NULL,
  `trade_income` decimal(19,2) DEFAULT NULL,
  `trade_multiple` int(11) NOT NULL,
  `trade_time` bigint(20) NOT NULL,
  `trade_type_id` int(11) NOT NULL,
  `trade_trans_dirction` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '这毛交易的成交方向',
  `transaction_code` varchar(255) DEFAULT NULL,
  `transaction_id` bigint(20) NOT NULL,
  PRIMARY KEY (`trade_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trade_log_income_reconcilia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `income` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈利额',
  `cut_off_time` int(10) NOT NULL DEFAULT '0' COMMENT '截止时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3492 DEFAULT CHARSET=utf8 COMMENT='交易收益统计表';

CREATE TABLE `trade_log_reconcilia` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `time` int(10) NOT NULL DEFAULT '0' COMMENT '时间,每月1号0点,unix时间格式',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `trade_income` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盈亏额',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约id',
  PRIMARY KEY (`id`),
  KEY `idx_trade_log_reconcilia_f_user_id` (`f_user_id`),
  KEY `idx_trade_log_reconcilia_time` (`time`)
) ENGINE=InnoDB AUTO_INCREMENT=171892 DEFAULT CHARSET=utf8 COMMENT='成交盈亏及手续费统计';

CREATE TABLE `v_account_contract` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT,
  `f_user_id` int(11) DEFAULT NULL,
  `f_code` varchar(10) DEFAULT NULL COMMENT '合约号',
  `f_initbtc` decimal(18,8) DEFAULT NULL COMMENT '初始btc数量',
  `f_btc` decimal(18,8) DEFAULT NULL COMMENT '开仓可用',
  `f_lockbtc` decimal(18,8) DEFAULT NULL COMMENT '合约冻结资金',
  `f_settled` decimal(18,8) DEFAULT NULL COMMENT '已结算资金-当前合约持单总盈亏',
  PRIMARY KEY (`f_id`),
  UNIQUE KEY `f_user_id` (`f_user_id`,`f_code`),
  KEY `idx_uid_code` (`f_user_id`,`f_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=48834 DEFAULT CHARSET=utf8 COMMENT='合约钱包';

CREATE TABLE `v_account_deposit_log` (
  `log_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `mt_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '持仓ID(v_member_transaction:mt_id)',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `direction` tinyint(1) NOT NULL DEFAULT '0' COMMENT '-1:从可用金额扣除保证金 1:返还保证金到账户可用金额',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '变更金额',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `dateline` int(10) unsigned NOT NULL COMMENT '操作时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_f_user_id_dateline` (`f_user_id`,`dateline`),
  KEY `idx_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户所持合约保证金不足时，系统自动"追加/返还"保证金记录';

CREATE TABLE `v_arbitrage_setting` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) DEFAULT NULL COMMENT '合约号',
  `depth_uid` int(10) NOT NULL DEFAULT '0' COMMENT '深度用户',
  `depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '深度数据获取路径',
  `spare_depth_url` varchar(256) NOT NULL DEFAULT '' COMMENT '备用深度路径',
  `depth_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度买单价格调整幅度（%）',
  `depth_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '深度卖单价格调整幅度（%）',
  `depth_amount_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '所允许的深度最小单',
  `depth_amount_max` decimal(18,8) NOT NULL DEFAULT '0.00000000',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '深度系统状态(0-停止，1-开启)',
  `scalping` bit(1) NOT NULL DEFAULT b'0' COMMENT '刷单系统状态（0-停止，1-开启）',
  `scalping_frequency_min` int(11) NOT NULL DEFAULT '0' COMMENT '刷单频率最小（笔数/每分钟）',
  `scalping_frequency_max` int(11) NOT NULL COMMENT '刷单频率最大（笔数/每分钟）',
  `scalping_min_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最小量（在最小和最大之间随机）',
  `scalping_max_each` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '刷单每笔最大量（在最小和最大之间随机）',
  `auto_trade` bit(1) NOT NULL DEFAULT b'0' COMMENT '自动对平，当有对手仓的情况下，及时以买一或卖一价对平',
  `hedging` bit(1) NOT NULL DEFAULT b'1' COMMENT '外部市场对冲',
  `hedging_buy_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲买单价格调整幅度（%）',
  `hedging_sell_range` decimal(8,5) NOT NULL DEFAULT '0.00000' COMMENT '外部对冲卖单价格调整幅度（%）',
  `scalping_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数范围',
  `scalping_depth_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '盘口宽度限制',
  `curr_use_state` smallint(5) NOT NULL DEFAULT '0' COMMENT '当前使用深度（0：主用深度，1：备用深度）',
  `risk_manage` bit(1) NOT NULL DEFAULT b'1' COMMENT '风险控制（根据对冲账户金额限制交易）',
  `depth_price_gap` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '价格增量',
  `depth_amount_gap_min` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最小增量',
  `depth_amount_gap_max` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '数量最大增量',
  `depth_index_range` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '指数偏离（小数）',
  `depth_size` smallint(5) NOT NULL DEFAULT '0' COMMENT '深度数量',
  `price_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '价格精度',
  `amount_digits` smallint(5) NOT NULL DEFAULT '-1' COMMENT '数量精度',
  `can_trade_self` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可自成交',
  `speed_type` smallint(5) NOT NULL DEFAULT '5' COMMENT '下单速度（值*300ms）',
  `amount_mutil` decimal(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '深度量增量（乘）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `f_code_UNIQUE` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='平台套利系统设置';

CREATE TABLE `v_funding_fee_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约名称',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '开启状态(0-停止，1-开启)',
  `running_time` varchar(45) NOT NULL DEFAULT '' COMMENT '执行时间点，逗号隔开（10:00,22:00）',
  `interest_rate_diff` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '利率差',
  `divisor` decimal(18,8) NOT NULL DEFAULT '1.00000000' COMMENT '除数',
  `clamp_up` decimal(18,8) NOT NULL DEFAULT '0.20000000' COMMENT '限制区间上边界（%）',
  `clamp_down` decimal(18,8) NOT NULL DEFAULT '-0.20000000' COMMENT '限制区间下边界（%）',
  `fixed_fee_open` bit(1) NOT NULL DEFAULT b'0' COMMENT '固定资金费率是否开启',
  `fixed_fee_rate` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '固定资金费率',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资金费配置表';

CREATE TABLE `v_last_ticker` (
  `f_ticker_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_ticker_code` varchar(10) CHARACTER SET latin1 NOT NULL DEFAULT '',
  `f_ticker_high` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_low` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_buy` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_sell` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_last` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_vol` decimal(25,8) unsigned NOT NULL DEFAULT '0.00000000',
  `f_ticker_trades` decimal(25,8) unsigned NOT NULL DEFAULT '0.00000000',
  PRIMARY KEY (`f_ticker_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `v_member_invite_rebate_log` (
  `id` int(12) unsigned NOT NULL AUTO_INCREMENT,
  `bq_code` varchar(10) DEFAULT '' COMMENT '合约号',
  `commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '用户返佣比例',
  `commission_rate_diff` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '返佣比例差',
  `rebate_amount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '返佣',
  `record_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '返佣时间',
  `sub_commission_rate` decimal(6,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '下级返佣比例',
  `sub_user_email` char(40) NOT NULL DEFAULT '' COMMENT '下级用户邮箱',
  `sub_user_id` int(10) NOT NULL DEFAULT '0' COMMENT '下级用户id',
  `sub_user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '下级用户等级名称',
  `trade_id` int(12) unsigned NOT NULL DEFAULT '0' COMMENT '交易id',
  `user_email` char(40) NOT NULL DEFAULT '' COMMENT '用户邮箱',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户id',
  `user_level_name` varchar(10) NOT NULL DEFAULT '' COMMENT '用户等级名称',
  `currency_type_id` smallint(5) NOT NULL DEFAULT '2' COMMENT '币种',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=127347 DEFAULT CHARSET=utf8;

CREATE TABLE `v_member_transaction` (
  `mt_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户合约挂单号',
  `transaction_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '合约id',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `trade_type_id` enum('1','2') NOT NULL DEFAULT '1' COMMENT '成交类型(1:多单开仓,买多/2:空头开仓,卖空)',
  `trade_multiple` decimal(8,4) unsigned NOT NULL DEFAULT '1.0000' COMMENT '杠杆',
  `hold_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓量',
  `lock_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '冻结量',
  `average_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '合约均价',
  `deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `deposit2` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '追加保证金',
  `commission` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '佣金',
  `f_min_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单所需的最小亏损备用金',
  `f_frozen_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单量',
  `f_frozenremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '平仓挂单亏损超过保证金的挂单对应的亏损备用金',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '亏损备用金（指数价格计算，且除去亏损超过保证金的平仓挂单量）',
  `f_totalmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '持仓总价值',
  `f_lockmoney` decimal(18,8) DEFAULT '0.00000000' COMMENT '冻结总价值',
  `f_type_forjs` tinyint(1) DEFAULT '0' COMMENT '用于计算的类型 买1卖-1',
  `flag` smallint(5) NOT NULL DEFAULT '0' COMMENT '0-默认，-1-爆仓操作中，其他操作中',
  `flag_time` int(10) NOT NULL DEFAULT '0' COMMENT 'flag设置时间',
  PRIMARY KEY (`mt_id`),
  KEY `idx_f_user_id` (`f_user_id`),
  KEY `idx_trade_type_id` (`trade_type_id`),
  KEY `idx_code` (`transaction_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8617836 DEFAULT CHARSET=utf8 COMMENT='用户合约表/持仓表';

CREATE TABLE `v_member_transaction_overload_log` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `trade_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '持仓均价',
  `trade_count` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '数量',
  `trade_deposit` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '保证金',
  `trade_overload_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '爆仓价',
  `trade_force_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '强平价',
  `trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '手续费',
  `direction` tinyint(1) NOT NULL DEFAULT '1' COMMENT '爆仓的方向(1:多单爆仓 2:卖空爆仓)',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '爆仓时间',
  `delegation_id` int(10) NOT NULL DEFAULT '0' COMMENT '委托挂单表主键id(btc1407_pending_delegation:delegation_id)',
  `balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '结余',
  `pending_no` bigint(19) NOT NULL DEFAULT '0' COMMENT '挂单流水号',
  `f_fromremain` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '同持仓表',
  PRIMARY KEY (`id`),
  KEY `idx_fuserid_code_pending` (`f_user_id`,`transaction_code`,`pending_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3860 DEFAULT CHARSET=utf8 COMMENT='会员合约爆仓日志(表结构与btc1407_trade_log类似)';

CREATE TABLE `v_overload_match_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `f_code` varchar(10) NOT NULL COMMENT '合约号',
  `f_user_id` int(11) unsigned NOT NULL COMMENT '指定撮合用户ID',
  `running` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否开启',
  PRIMARY KEY (`id`),
  KEY `idx_fcode` (`f_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='未消耗爆仓单指定账户撮合';

CREATE TABLE `v_statistical_form` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL DEFAULT '' COMMENT '模拟交易-合约号',
  `statistical_time` int(10) NOT NULL COMMENT '统计时间',
  `v_contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-合约可用总额',
  `v_deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-合约保证金总量',
  `v_hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-持仓数量',
  `v_total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-手续费总额',
  `v_total_trade_user` int(10) NOT NULL COMMENT '模拟交易-所有合约总交易人数',
  `v_trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-真实交易总量',
  `v_trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-手续费金额',
  `v_trade_user` int(10) NOT NULL COMMENT '模拟交易-交易人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7364 DEFAULT CHARSET=utf8 COMMENT='每天统计模拟盘交易数据';

CREATE TABLE `v_statistical_hourly_form` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL DEFAULT '' COMMENT '模拟交易-合约号',
  `statistical_time` int(10) NOT NULL COMMENT '统计时间',
  `v_contract_balance` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-合约可用总额',
  `v_deposit_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-合约保证金总量',
  `v_hold_mount` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-持仓数量',
  `v_total_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-手续费总额',
  `v_total_trade_user` int(10) NOT NULL COMMENT '模拟交易-所有合约总交易人数',
  `v_trade_count` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-真实交易总量',
  `v_trade_fee` decimal(18,8) NOT NULL DEFAULT '0.00000000' COMMENT '模拟交易-手续费金额',
  `v_trade_user` int(10) NOT NULL COMMENT '模拟交易-交易人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=228716 DEFAULT CHARSET=utf8 COMMENT='每小时统计模拟盘交易数据';

CREATE TABLE `v_stop_loss` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_id` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '合约号ID',
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `stop_loss_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1止盈 2止损',
  `transaction_member_id` int(11) NOT NULL DEFAULT '0' COMMENT '持仓ID',
  `stop_loss_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '止损价',
  `pending_trans_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '委托成交',
  `trade_type_id` smallint(6) NOT NULL DEFAULT '1' COMMENT '1 买多, 2卖空',
  `dateline` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '添加记录时间',
  `f_user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_type` smallint(5) NOT NULL DEFAULT '0' COMMENT '挂单类型（0：限价委托;1：市价委托）',
  `pending_amount` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '挂单数量',
  PRIMARY KEY (`id`),
  KEY `idx_ccid` (`transaction_member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7134 DEFAULT CHARSET=utf8 COMMENT='止损表';

CREATE TABLE `v_trade_day_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) DEFAULT '0' COMMENT '用户ID',
  `sum_depoist` decimal(18,8) DEFAULT '0.00000000' COMMENT '保证金总额',
  `sum_income` decimal(18,8) DEFAULT '0.00000000' COMMENT '盈亏总额',
  `user_name` char(40) DEFAULT '' COMMENT '用户邮箱',
  `yield_rate` decimal(18,8) DEFAULT '0.00000000' COMMENT '收益率（%）',
  `record_time` int(11) DEFAULT '0' COMMENT '日度开始时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=723 DEFAULT CHARSET=utf8 COMMENT='日度模拟交易排名表';

CREATE TABLE `v_trade_month_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) DEFAULT '0' COMMENT '用户ID',
  `sum_depoist` decimal(18,8) DEFAULT '0.00000000' COMMENT '保证金总额',
  `sum_income` decimal(18,8) DEFAULT '0.00000000' COMMENT '盈亏总额',
  `user_name` char(40) DEFAULT '' COMMENT '用户邮箱',
  `yield_rate` decimal(18,8) DEFAULT '0.00000000' COMMENT '收益率（%）',
  `record_time` int(11) DEFAULT '0' COMMENT '月度统计开始时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=799 DEFAULT CHARSET=utf8 COMMENT='月度模拟交易排名表';

CREATE TABLE `v_trade_week_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) DEFAULT '0' COMMENT '用户ID',
  `sum_depoist` decimal(18,8) DEFAULT '0.00000000' COMMENT '保证金总额',
  `sum_income` decimal(18,8) DEFAULT '0.00000000' COMMENT '盈亏总额',
  `user_name` char(40) DEFAULT '' COMMENT '用户邮箱',
  `yield_rate` decimal(18,8) DEFAULT '0.00000000' COMMENT '收益率（%）',
  `record_time` int(11) DEFAULT '0' COMMENT '周度开始时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1960 DEFAULT CHARSET=utf8 COMMENT='周度模拟交易排名表';

CREATE TABLE `v_transaction` (
  `transaction_id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约号',
  `transaction_begin_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT 'unix时间格式（旧合约清算时，自动写入新合约信息，将并时间自动调整为30分钟后的时间点）',
  `transaction_end_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '结束时间',
  `transaction_state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '合约状态(1:正常 2:清算中 3:关闭)',
  `latest_transaction_price` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最新成交价',
  `transaction_highest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最高报价',
  `transaction_lowest` decimal(18,8) unsigned NOT NULL DEFAULT '0.00000000' COMMENT '最低报价',
  `sort` smallint(4) unsigned NOT NULL DEFAULT '0' COMMENT '合约排序值, YYMM  (1月 2日 3周 4季 5上证)',
  `multiple` varchar(20) NOT NULL DEFAULT '' COMMENT '具有的杠杆(1,5,10,20,50)',
  `multiple_state` varchar(20) NOT NULL DEFAULT '' COMMENT '每个杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `trade_coefficient` decimal(9,4) NOT NULL COMMENT '交易系数(分母)',
  `transaction_type` smallint(6) NOT NULL DEFAULT '1' COMMENT '合约类型(1 btc合约 2 ltc合约 3sz合约)',
  `transaction_type_code` varchar(10) NOT NULL DEFAULT '' COMMENT '合约类型号',
  `suspend` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否暂停',
  `transfer_in_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '合约可转入',
  `transfer_out_enable` bit(1) NOT NULL DEFAULT b'1' COMMENT '可约可转出',
  `currency_type_id` int(6) NOT NULL DEFAULT '0' COMMENT '交易币种ID',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uniq_transaction_code` (`transaction_code`),
  KEY `idx_transaction_begin_time` (`transaction_begin_time`),
  KEY `idx_type` (`transaction_type`) USING BTREE,
  KEY `idx_state` (`transaction_state`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='合约表（当前进行中的合约/已经清算完的合约/未开始的合约）';

CREATE TABLE `v_transaction_rate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_code` varchar(10) NOT NULL COMMENT '合约号',
  `uprate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的上波动比例',
  `downrate` decimal(6,2) DEFAULT '0.00' COMMENT '该合约价格指数的下波动比例',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

CREATE TABLE `v_transaction_type` (
  `type_id` smallint(6) unsigned NOT NULL AUTO_INCREMENT COMMENT '合约类型id',
  `type_name` varchar(20) NOT NULL COMMENT '合约类型名称',
  `type_code` varchar(10) NOT NULL COMMENT '合约类型号',
  `default_multiple` varchar(20) DEFAULT NULL COMMENT '合约默认杠杆(1,5,10,20,50)',
  `default_multiple_state` varchar(20) DEFAULT NULL COMMENT '默认杠杆的状态(1,0,1,2,1)(0不显示,1显示,2禁用)',
  `default_coefficient` decimal(9,4) DEFAULT NULL COMMENT '默认交易系数(分母)',
  `is_using` tinyint(1) NOT NULL DEFAULT '0' COMMENT '使用中',
  `index_address` varchar(255) DEFAULT NULL COMMENT '指数地址',
  `index_value` decimal(18,8) DEFAULT NULL COMMENT '指数',
  `min_kc_nums` decimal(14,4) DEFAULT NULL COMMENT '最小开仓数量',
  `min_float_price` decimal(18,8) DEFAULT NULL COMMENT '最小浮动价格',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `type_name_UNIQUE` (`type_name`),
  UNIQUE KEY `type_code_UNIQUE` (`type_code`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='合约类型表';

CREATE TABLE `bq_mine_personal_amount`  (
  `uid` int(10) NOT NULL COMMENT '用户id',
  `mined_amount` decimal(18, 8) NOT NULL COMMENT '用户已挖数量',
  UNIQUE INDEX `index_uid`(`uid`) USING BTREE COMMENT '用户id索引'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

alter table bq_mine_setting add column personal_max_amount decimal(18, 8) NOT NULL DEFAULT 0 COMMENT '个人挖矿数量上限';
alter table bq_mine_record add column trade_mode int(8) NOT NULL DEFAULT 1 COMMENT '交易模式：1-永续合约，2-现货';

DROP VIEW IF EXISTS `view_invite_amount` ;

CREATE ALGORITHM=UNDEFINED DEFINER=`btcside`@`%` SQL SECURITY INVOKER VIEW `view_invite_amount` AS select sum(`bq_member_invite_amount`.`f_day_click`) AS `f_day_click`,sum(`bq_member_invite_amount`.`f_day_success`) AS `f_day_success`,`bq_member_invite_amount`.`f_user_id` AS `f_user_id` from `bq_member_invite_amount` group by `bq_member_invite_amount`.`f_user_id`;

DROP VIEW IF EXISTS `view_invite_award_count` ;

CREATE ALGORITHM=UNDEFINED DEFINER=`btcside`@`%` SQL SECURITY INVOKER VIEW `view_invite_award_count` AS select sum(`bq_invite_award_count`.`day_count`) AS `day_count`,`bq_invite_award_count`.`f_user_id` AS `f_user_id` from `bq_invite_award_count` group by `bq_invite_award_count`.`f_user_id`;

DROP VIEW IF EXISTS `view_usergain` ;

CREATE ALGORITHM=UNDEFINED DEFINER=`btcside`@`%` SQL SECURITY INVOKER VIEW `view_usergain` AS select sum(`bq_account_contract`.`f_btc`) AS `f_btc`,`bq_account_contract`.`f_user_id` AS `f_user_id` from `bq_account_contract` group by `bq_account_contract`.`f_user_id`;

DROP VIEW IF EXISTS `view_usermanager` ;

CREATE ALGORITHM=UNDEFINED DEFINER=`btcside`@`%` SQL SECURITY INVOKER VIEW `view_usermanager` AS select `bq_member`.`f_user_id` AS `f_user_id`,`bq_member`.`is_sub_user` AS `is_sub_user`,`bq_member`.`f_user_level_id` AS `f_user_level_id`,ifnull(`bq_member`.`f_user_credits`,0) AS `credits`,`bq_member`.`f_user_time` AS `f_user_time`,`bq_member`.`f_user_state` AS `f_user_state`,`bq_member`.`f_real_email` AS `f_user_email`,`bq_member_safe_settings`.`f_user_tel` AS `f_user_tel`,ifnull(`bq_account`.`btc`,0) AS `btc`,ifnull(`bq_account`.`btc_locked`,0) AS `btc_locked`,`bq_member_area`.`country_name` AS `country_name`,`bq_member_area`.`city_name` AS `city_name`,`bq_member_area`.`province_name` AS `province_name`,ifnull(`a`.`f_btc`,0) AS `btc_gain`,ifnull(`bq_member_invite_log`.`invite_uid`,0) AS `invite_uid` from (((((`bq_member` left join `bq_member_safe_settings` on((`bq_member`.`f_user_id` = `bq_member_safe_settings`.`f_user_id`))) left join `bq_member_area` on((`bq_member`.`f_user_id` = `bq_member_area`.`f_user_id`))) left join `view_usergain` `a` on((`bq_member`.`f_user_id` = `a`.`f_user_id`))) left join `bq_account` on((`bq_member`.`f_user_id` = `bq_account`.`f_user_id`))) left join `bq_member_invite_log` on((`bq_member`.`f_user_id` = `bq_member_invite_log`.`f_user_id`))) where (`bq_account`.`currency_type_id` = 2);


CREATE TABLE
IF
	NOT EXISTS btcusdt_one_minute_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS btcusdt_three_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS btcusdt_fifteen_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
		CREATE TABLE
IF
	NOT EXISTS btcusdt_half_hour_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
			CREATE TABLE
IF
	NOT EXISTS btcusdt_one_hour_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
	CREATE TABLE
IF
	NOT EXISTS btcusdt_two_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		
	CREATE TABLE
IF
	NOT EXISTS btcusdt_four_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS btcusdt_six_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
			CREATE TABLE
IF
	NOT EXISTS btcusdt_twelve_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
	CREATE TABLE
IF
	NOT EXISTS btcusdt_five_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS btcusdt_one_day_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS btcusdt_one_week_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS btcusdt_one_month_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_one_minute_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_three_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS ethusdt_fifteen_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
		CREATE TABLE
IF
	NOT EXISTS ethusdt_half_hour_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
			CREATE TABLE
IF
	NOT EXISTS ethusdt_one_hour_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_two_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		
	CREATE TABLE
IF
	NOT EXISTS ethusdt_four_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS ethusdt_six_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
			CREATE TABLE
IF
	NOT EXISTS ethusdt_twelve_hours_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_five_minutes_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_one_day_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
	CREATE TABLE
IF
	NOT EXISTS ethusdt_one_week_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	
		CREATE TABLE
IF
	NOT EXISTS ethusdt_one_month_trade (
		id INT ( 12 ) UNSIGNED NOT NULL AUTO_INCREMENT,
		CODE VARCHAR ( 10 ) NOT NULL DEFAULT '' COMMENT '交易对名称',
		high DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最高价',
		low DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '最低价',
		FIRST DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '开盘价',
		last DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '收盘价',
		vol DECIMAL ( 18, 8 ) UNSIGNED NOT NULL DEFAULT '0.00000000' COMMENT '成交数量',
		TIME INT ( 10 ) UNSIGNED NOT NULL DEFAULT '0' COMMENT '时间,unix时间格式',
		PRIMARY KEY ( id ),
	KEY idx_time ( TIME ) USING BTREE 
	) ENGINE = INNODB DEFAULT CHARSET = utf8;
	