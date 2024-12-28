package com.btc100.btc_q.common;

/**
 * 存储要往memecached中存储的key的名称
 * 
 * @author Administrator
 * 
 */
public class BTCQMemecacheKeys {

	public class TIMES {
		/**
		 * 有效合约集合 的key
		 * 
		 * @return: List<BQTransaction>
		 */
		public static final int TRANSACTIONLIST = 5;
		/**
		 * 有效合约集合 的key
		 * 
		 * @return: List<BQTransaction>
		 */
		public static final int TRANSACTIONLISTALL = 60;
		/**
		 * 有效合约集合 的key
		 * 
		 * @return: List<BQTransaction>
		 */
		public static final int TRANSACTIONLISTSZ = 5;
		/**
		 * 有效现货交易对（币币交易） 的key
		 * 
		 * @return: List<BQSpotMarketCoinPair>
		 */
		public static final int SPOTCOINPAIRLIST = 5;
		/**
		 * 所有现货交易对（币币交易） 的key
		 * 
		 * @return: List<BQSpotMarketCoinPair>
		 */
		public static final int SPOTCOINPAIRLISTALL = 60;
		/**
		 * 价格指数 的key
		 * 
		 * @return:BigDecimal
		 */
		public static final int BTCINDEXPRICE = 60;
		/**
		 * 最新成交价 的key ：合约号 +_latest_price
		 * 
		 * @return:BigDecimal
		 */
		public static final int BTCLATESTPRICE = 10;

		/**
		 * 杠杆比率 的key ：合约号+_trade_coefficient
		 * 
		 * @return:BigDecimal
		 */
		public static final int BTCCOEFFICIENT = 60;
		
		/**
		 * 买单top5 的key: 合约号+该值 如btc1407buytop5List
		 * 
		 * @return:List<Top5DelegationVO>
		 */
		public static final int BUYTOP5LIST = 60;
		/**
		 * 卖单top5 的key: 合约号+该值 如btc1407selltop5List
		 * 
		 * @return: List<Top5DelegationVO>
		 */
		public static final int SELLTOP5LIST = 60;
		/**
		 * 市场深度前一百条 depth_合约号(小写)
		 * 
		 * @return:BigDecimal
		 */
		public static final int DEPTH_CODE = 600;

		/**
		 * 交易前一百条 depth_合约号(小写)
		 * 
		 * @return:BigDecimal
		 */
		public static final int TRADE_CODE = 600;

		/**
		 * 美元兑换汇率
		 * 
		 * @return:BigDecimal
		 */
		public static final int USD_RATE = 3000;
		
		public static final int BPI = 600;
		
		public static final int SZI = 80;
		
		public static final int HALF_HOUR = 1800;
		
		public static final int FIVE_MIN = 300;
		
		public static final int ONE_MIN = 60;
		
		public static final int TWO_MIN = 120;
		
		public static final int ONE_HOUR = 3600;
		
		public static final int ONE_DAY = 3600*24;
		
		public static final int TWO_DAY = 3600*48;
		
		public static final int ONE_SECOND = 1;
		
		public static final int TWO_SECOND = 2;
		
		public static final int TEN_SECOND = 10;
		
		public static final int TICKER_BTC = 1200;
		
		public static final int SYSTEM_GAINLOSS = 1;
		
		public static final int TICKER_TOTALVOL = 10;
		public static final int TICKER_VOL = 10;
		public static final int TICKER_HOLDVOL = 5;
		public static final int TICKER_TRADEBTC = 5;
		public static final int TICKER_YESTERDAYAVGPRICE = 7200;
		
		public static final int TICKER_ONEMINUTE = 1800;
//		public static final int TICKER_THREEMINUTES = 3600;
//		public static final int TICKER_FIVEMINUTES = 3600 * 2;
//		public static final int TICKER_FIFTEENMINUTES = 3600 * 8;
//		public static final int TICKER_HALFHOUR = 3600 * 16;
//		public static final int TICKER_ONEHOUR = 3600 * 24;
//		public static final int TICKER_TWOHOURS = 3600 * 48;
//		public static final int TICKER_FOURHOURS = 3600 * 72;
//		public static final int TICKER_SIXHOURS = 3600 * 24 * 7;
//		public static final int TICKER_TWELVEHOURS = 3600 * 24 * 15;
		public static final int TICKER_ONEDAY = 3600 * 24 * 30;//失效时间30天
	}

	
	public static final String TICKER_BTC = "ticker";
	public static final String TICKER_BTC_OBJ = "ticker_obj";
	
	//public static final String BPI = "bpi";
	
	/**
	 * 上证指数
	 */
	//public static final String SZI = "szi";
	/**
	 * 上证指数昨日收盘价
	 */
	//public static final String SZI_Other = "szi_other";

	/**
	 * 有效合约集合 的key
	 * 
	 * @return: List<BQTransaction>
	 */
	public static final String TRANSACTIONLIST = "transactionList_key";
	/**
	 * 单合约的key
	 * 
	 * @return: BQTransaction
	 */
	public static final String TRANSACTION_KEY = "transaction_key_";
	/**
	 * 所有合约集合 的key
	 * 
	 * @return: List<BQTransaction>
	 */
	public static final String TRANSACTIONLISTALL = "transactionList_all";
	
	/**
	 * 有效上证合约集合 的key
	 * 
	 * @return: List<BQTransaction>
	 */
	public static final String TRANSACTIONLISTSZ = "transactionList_key_sz";
	
	/**
	 * 有效现货交易对（币币交易） 的key
	 * 
	 * @return: List<BQSpotMarketCoinPair>
	 */
	public static final String SPOTCOINPAIRLIST = "spotCoinPairList_key";
	/**
	 * 所有现货交易对（币币交易） 的key
	 * 
	 * @return: List<BQSpotMarketCoinPair>
	 */
	public static final String SPOTCOINPAIRLISTALL = "spotCoinPairList_all";
	
	/**
	 * 合约分区列表
	 */
	public static final String TRANS_PARTITIONS = "trans_partitions";
	
	/**
	 * 价格指数 的key
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCINDEXPRICE = "btc_index_price_key";
	/**
	 * 最新成交价 的key ：合约号 +_latest_price
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCLATESTPRICE = "_latest_price";

	/**
	 * 给定时间内第一笔成交价 的key ：first_price_+合约号
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCFIRSTPRICE = "first_price_";
	
	/**
	 * 最高成交价 的key ：latest_price_+合约号
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCHIGHSTPRICE = "high_price_";

	/**
	 * 最低成交价 的key ：latest_price_+合约号
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCLOWSTPRICE = "low_price_";
	
	/**
	 * 杠杆比率 的key ：合约号+_trade_coefficient
	 * 
	 * @return:BigDecimal
	 */
	public static final String BTCCOEFFICIENT = "_trade_coefficient";

	/**
	 * 买单top5 的key: 合约号+该值 如btc1407buytop5List
	 * 
	 * @return:List<Top5DelegationVO>
	 */
	public static final String BUYTOP5LIST = "buytop5List";
	/**
	 * 卖单top5 的key: 合约号+该值 如btc1407selltop5List
	 * 
	 * @return: List<Top5DelegationVO>
	 */
	public static final String SELLTOP5LIST = "selltop5List";
	
	public static final String MARK_PRICE_LIST = "mark_price_list_";
	public static final String MARK_PRICE = "mark_price_";
	public static final String MARK_PRICE_SETTING = "setting_mark_price_";

	/**
	 * 市场深度前一百条 depth_合约号(小写)
	 * 
	 * @return:BigDecimal
	 */
	public static final String DEPTH_CODE = "depth_";

	/**
	 * 交易前一百条 depth_合约号(小写)
	 * 
	 * @return:BigDecimal
	 */
	public static final String TRADE_CODE = "trade_";

	/**
	 * 美元兑换汇率
	 * 
	 * @return:BigDecimal
	 */
	public static final String USD_RATE = "usd_rate";
	
	/**
	 * 昨日收盘价
	 * 
	 * @return:BigDecimal
	 */
	public static final String PRECLOSE = "pre_close_";
	
	public static final String MINE_STATISTICS = "mine_statistics";
	
	public static final String PLATFORM_DAYS_AMOUNT = "platform_days_amount";
	
	public static final String INDEX_PRICE_RATE_UP = "_index_price_rate_up";
	public static final String INDEX_PRICE_RATE_DOWN = "_index_price_rate_down";
	public static final String DIGITS = "_digits";
	
	public static final String TEL_CODE = "tel_code_";
	
	public static final String EMAIL_CODE = "email_code_";
	
	public static final String TEL_CODE_LIMIT = "_tel_code_limit_";
	
	public static final String INVITE_COUNT = "invite_count_";
	public static final String ALL_INVITE_COUNT = "all_invite_count_";
	public static final String USER_ALL_INVITE_VOS = "user_all_invite_vos_";
	public static final String ALL_INVITE_VOS = "all_invite_vos_";
	
	public static final String ACCOUNT = "_account";
	
	public static final String ACCOUNT_CONTRACT = "_account_contract_";
	
	public static final String LOAN_LOCK = "loan_lock_";
	
	public static final String TICKER = "tickervo_";
	
	public static final String HUOBI_DEPTH_UPDATE = "huobi_depth_update_";
	
	public static final String ARBITRAGE_SETTING = "arbitrage_setting_";
	
	public static final String BT_ARBITRAGE_SETTING = "bt_arbitrage_setting_";
	
	public static final String FIRST_PRICE = "_first_price";
	
	public static final String HOLD_COUNT = "_hold_count";
	
	public static final String ALL_TRADE_AMOUNT = "all_trade_amount";
	
	public static final String ONE_DAY_TRADE_AMOUNT = "one_day_trade_amount";
	
	public static final String CURRENCY_TYPES = "currency_types";
	
	public static final String TRADE_WHITE_LIST = "trade_white_list";
	
	public static final String API_TRADE_WHITE_LIST = "api_trade_white_list";
	
	public static final String API_TRADE_WHITE_SETTING = "api_trade_white_setting";
	
	public static final String TRADE_OPEN_LIMTI_LIST = "trade_open_limit_list";
	
	public static final String DELIVERY_TRADE_OPEN_LIMTI_LIST = "delivery_trade_open_limit_list";
	
	public static final String INDEX_FUDU = "index_fudu_";
	
	public static final String ALL_INDEXS = "all_indexs_key";
	
	public static final String CFD_INDEX = "cfd_index_";
	
	public static final String MARKET_ORDER_SETTING = "market_order_setting";
	
	/** @return:CommonDataOkcoin
	 */
//	public static final String BPI_OKCOIN = "bpi_okex";
	/** @return:CommonDataHuobi
	 */
	//public static final String BPI_HUOBI = "bpi_huobi";
	
	public static final String BPI_COINMARKETCAP = "bpi_coinmarketcap";
	
	public static final String BPI_DATA = "bpi_data";

	public static final String SYSTEM_GAINLOSS = "system_gainloss";
	
	/**合约api 中sosoBtc用到的缓存名前缀*/
	public static final String API_SOSOBTC_ = "api_sosoBtc_";
	
	/*** Ticker的交易量 的key */
	public static final String TICKER_TOTALVOL = "ticker_totalvol";
	/*** Ticker的交易量 的key */
	public static final String TICKER_TRADEVOL = "ticker_tradevol";
	/*** Ticker的交易量 的key */
	public static final String TICKER_TRADEVOLBTC = "ticker_tradevolbtc";
	public static final String TICKER_TRADEBTC = "ticker_tradebtc";
	/*** 持仓量 */
	public static final String TICKER_HOLDVOL = "ticker_holdvol";
	/*** 昨日均价 */
	public static final String TICKER_YESTERDAYAVGPRICE = "ticker_yesterdayaveprice";
	/*** 今天是否可以正常交易 */
	public static final String Trade_Normal = "trade_normal";
	/*** 股指合约设置 */
	public static final String STOCK_SETTING = "stock_setting";
	/*** 股指合约交易时间 */
	public static final String STOCK_TRADE_TIME = "stock_trade_time_";
	/*** 合约类型 列表*/
	public static final String TRANS_TYPES = "trans_types";
	/*** 合约类型 */
	public static final String TRANS_TYPE = "trans_type";
	/*** 现货市场  列表*/
	public static final String SPOT_MARKET = "spot_market";
	
	public static final String CONTRACT_BASE2 = "contract_base2";
	public static final String CONTRACT_BASE = "contract_base";
	public static final String SPOT_BASE = "spot_base_";
	public static final String SPOT_BASE2 = "spot_base2_";
	/*** 最新10条合约ticker数据*/
	public static final String CONTRACT_LAST = "contract_last";
	/*** 最新10条合约*/
	public static final String TRANSACTION_LAST = "transaction_last";
	
	/*** 邀请前五排名*/
	public static final String INVITE_RANKING = "invite_ranking";
	/*** 返佣前五排名*/
	public static final String REBATE_RANKING = "rebate_ranking";
	public static final String REBATE_RANKING_LAST_TIME = "rebate_ranking_last_time";
	/*** 盈利额前五排名*/
	public static final String INCOME_RANKING = "income_ranking";
	public static final String INCOME_RANKING_LAST_TIME = "income_ranking_last_time";
	
	/*** 合约持仓数据播报*/
	public static final String DATA_BROAD_CAST = "data_broad_cast_";
	public static final String DATA_BROAD_CAST_SETTING = "data_broad_cast_setting";
	
	/*** K线-每隔1分钟统计交易*/
	public static final String TICKER_ONEMINUTE = "ticker_oneminute";
	/*** K线-每隔3分钟统计交易列表*/
	public static final String TICKER_THREEMINUTES = "ticker_threeminutes";
	/*** K线-每隔5分钟统计交易列表*/
	public static final String TICKER_FIVEMINUTES = "ticker_fiveminutes";
	/*** K线-每隔15分钟统计交易列表*/
	public static final String TICKER_FIFTEENMINUTES = "ticker_fifteenminutes";
	/*** K线-每隔30分钟统计交易列表*/
	public static final String TICKER_HALFHOUR = "ticker_halfhour";
	/*** K线-每隔1小时统计交易列表*/
	public static final String TICKER_ONEHOUR = "ticker_onehour";
	/*** K线-每隔2小时统计交易列表*/
	public static final String TICKER_TWOHOURS = "ticker_twohours";
	/*** K线-每隔4小时统计交易列表*/
	public static final String TICKER_FOURHOURS = "ticker_fourhours";
	/*** K线-每隔6小时统计交易列表*/
	public static final String TICKER_SIXHOURS = "ticker_sixhours";
	/*** K线-每隔12小时统计交易列表*/
	public static final String TICKER_TWELVEHOURS = "ticker_twelvehours";
	/*** K线-每隔1天统计交易列表*/
	public static final String TICKER_ONEDAY = "ticker_oneday";
	/*** K线-每隔1周统计交易列表*/
	public static final String TICKER_ONEWEEK = "ticker_oneweek";
	/*** K线-每隔1月统计交易列表*/
	public static final String TICKER_ONEMONTH = "ticker_onemonth";
	
	public static final String LAST_TICKER_TIME= "last_ticker_time";
	
	public static final String D_LAST_TICKER_TIME= "d_last_ticker_time";
	
	/*** 外部市场行情  */
	public static final String EXCHANGE_TICKER = "exchange_ticker_";
	
	public static final String HEDGING_BALANCE = "hedging_balance_";
	
	public static final String SPOT_HEDGING_BALANCE = "spot_hedging_balance_";
	
	/*** 合约介绍详情  */
	public static final String TRANS_INTRODUCE = "trans_introduce_";
	
	/*** 合约量化用户相关配置  */
	public static final String QUANT_SETTING = "quant_setting";
	/*** 币币量化用户相关配置  */
	public static final String SPOT_QUANT_SETTING = "spot_quant_setting";
	
	/*** 缓存挂单锁  */
	public static final String PENDING_CACHE_LOCK = "pending_cache_lock_";
	/*** 挂单锁  */
	public static final String PENDING_LOCK = "pending_lock_";
	/*** 计划挂单锁  */
	public static final String PLAN_PENDING_LOCK = "plan_pending_lock_";
	
	public static final String COMMON_DATA = "common_data_";
	
	public static final String SYMBOLS_DATA = "symbols_data_key";
	public static final String SYMBOLS_DATA2 = "symbols_data_key2";
	
	public static final String SPOT_SYMBOLS_DATA = "spot_symbols_data_key";
	public static final String SPOT_SYMBOLS_DATA2 = "spot_symbols_data_key2";
	
	public static final String KLINE = "kline_";
	public static final String LAST_KLINE = "last_kline_";
	
	public static final String LAST_PRICE_ONE_HOUR_AGO = "last_price_one_hour_ago_";
	
	public static final String LAST_PRICE_ONE_DAY_AGO = "last_price_one_day_ago_";
	
	public static final String OPEN_PRICE = "open_price_";
	
	public static final String FUNDING_FEE_RATE = "funding_fee_rate_";
	
	public static final String FUNDING_FEE_SETTINGS = "funding_fee_settings";
	
	public static final String BFX_FEE_SETTING = "bfx_fee_setting";
	
	public static final String BFX_VIP_SETTINGS = "bfx_vip_settings";
	
	public static final String MINE_SETTING = "mine_setting";
	
	public static final String OVER_MATCH_SETTING = "over_match_setting_";
	
	/*** 综合指数配比，合约基准指数*/
	public static final String BENCHMARK_PRICE = "benchmark_price";
	/*** 上一交易日收盘价格*/
	public static final String COLSING_PRICE = "colsing_price";
	/*** 综合指数各交易对ticker详情*/
	public static final String COMPOSITE_TICKER = "composite_tickervo";
	
	/*** websocket用户的最新交易记录 列表（最多100条）*/
	public static final String WS_ORDER_LIST = "_ws_order_list_";
	public static final String WS_ORDER_LAST_ORDER_ID = "_ws_order_last_order_id_";
	/*** websocket用户的最新挂单 列表*/
	public static final String WS_ORDER_UPDATE = "_ws_order_update_";
	/*** websocket订阅用户 列表*/
	public static final String WS_SUBBED_USERS = "_ws_subbed_users";
	/*** websocket订阅用户的新增挂单 列表锁 */
	public static final String WS_ORDER_NEW_LIST_LOCK = "_ws_order_new_list_lock_";
	/*** websocket订阅用户的新增挂单 列表*/
	public static final String WS_ORDER_NEW_LIST = "_ws_order_new_list_";
	
	
	/*** 交割-上一交易日收盘价格*/
	public static final String D_COLSING_PRICE = "colsing_price";
	/*** 交割-今天是否可以正常交易 */
	public static final String D_Trade_Normal = "trade_normal";
	/*** 交割-股指合约设置 */
	public static final String D_STOCK_SETTING = "stock_setting";
	/*** 交割-股指合约交易时间 */
	public static final String D_STOCK_TRADE_TIME = "stock_trade_time_";
	/*** 交割合约昨日收盘价  */
	public static final String D_PRECLOSE = "d_pre_close_";
	/*** 交割合约介绍详情  */
	public static final String D_TRANS_INTRODUCE = "d_trans_introduce_";
	/*** 交割合约量化用户相关配置*/
	public static final String D_QUANT_SETTING = "d_quant_setting";
	/*** 交割合约分区列表 */
	public static final String D_TRANS_PARTITIONS = "d_trans_partitions";
	/*** 交割合约类型 列表*/
	public static final String D_TRANS_TYPES = "d_trans_types";
	/*** 交割合约持仓量 */
	public static final String D_TICKER_HOLDVOL = "d_ticker_holdvol";
	/*** 有效合约集合 的key */
	public static final String D_TRANSACTIONLIST = "d_transactionList_key";
	/*** 单合约的key */
	public static final String D_TRANSACTION_KEY = "d_transaction_key_";
	/*** Ticker的交易量 的key */
	public static final String D_TICKER_TRADEVOL = "d_ticker_tradevol";
	/*** Ticker的交易量 的key */
	public static final String D_TICKER_TRADEVOLBTC = "d_ticker_tradevolbtc";
	public static final String D_TICKER_TRADEBTC = "d_ticker_tradebtc";
	/*** Ticker的交易量 的key */
	public static final String D_TICKER_TOTALVOL = "d_ticker_totalvol";
	/*** 昨日均价 */
	public static final String D_TICKER_YESTERDAYAVGPRICE = "d_ticker_yesterdayaveprice";
	
	public static final String D_HUOBI_DEPTH_UPDATE = "d_huobi_depth_update_";
	
	/*** 外部市场行情  */
	public static final String D_EXCHANGE_TICKER = "d_exchange_ticker_";
	
	public static final String D_HEDGING_BALANCE = "d_hedging_balance_";
	
	public static final String D_BTCCOEFFICIENT = "_d_trade_coefficient";
	
	public static final String D_BTCLATESTPRICE = "_d_latest_price";
	
	public static final String D_BTCHIGHSTPRICE = "_d_high_price_";
	
	public static final String D_BTCLOWSTPRICE = "_d_low_price_";
	
	public static final String D_BUYTOP5LIST = "d_buytop5List";
	
	public static final String D_SELLTOP5LIST = "d_selltop5List";
	
	public static final String D_DIGITS = "_d_digits";
	
	public static final String D_TICKER_BTC = "d_ticker";
	
	public static final String D_TICKER_BTC_OBJ = "d_ticker_obj";
	
	public static final String D_ARBITRAGE_SETTING = "d_arbitrage_setting_";
	
	public static final String D_DEPTH_CODE = "d_depth_";
	
	public static final String D_TRADE_CODE = "d_trade_";
	
	public static final String D_FIRST_PRICE = "_d_first_price";
	
	public static final String D_HOLD_COUNT = "_d_hold_count";
	
	public static final String D_ALL_TRADE_AMOUNT = "d_all_trade_amount";
	
	public static final String D_ONE_DAY_TRADE_AMOUNT = "d_one_day_trade_amount";
	
	public static final String D_INDEX_PRICE_RATE_UP = "_d_index_price_rate_up";
	
	public static final String D_INDEX_PRICE_RATE_DOWN = "_d_index_price_rate_down";
	
	public static final String D_COMMON_DATA = "d_common_data_";
	
	public static final String D_SYMBOLS_DATA = "d_symbols_data_key";
	public static final String D_SYMBOLS_DATA2 = "d_symbols_data_key2";
	
	public static final String D_ACCOUNT_CONTRACT = "_d_account_contract_";
	
	/*** K线-交割合约1分钟*/
	public static final String D_TICKER_ONEMINUTE = "d_ticker_oneminute_";
	/*** K线-交割合约3分钟*/
	public static final String D_TICKER_THREEMINUTES = "d_ticker_threeminutes_";
	/*** K线-交割合约5分钟*/
	public static final String D_TICKER_FIVEMINUTES = "d_ticker_fiveminutes_";
	/*** K线-交割合约15分钟*/
	public static final String D_TICKER_FIFTEENMINUTES = "d_ticker_fifteenminutes_";
	/*** K线-交割合约30分钟*/
	public static final String D_TICKER_HALFHOUR = "d_ticker_halfhour_";
	/*** K线-交割合约1小时*/
	public static final String D_TICKER_ONEHOUR = "d_ticker_onehour_";
	/*** K线-交割合约2小时*/
	public static final String D_TICKER_TWOHOURS = "d_ticker_twohours_";
	/*** K线-交割合约4小时*/
	public static final String D_TICKER_FOURHOURS = "d_ticker_fourhours_";
	/*** K线-交割合约6小时*/
	public static final String D_TICKER_SIXHOURS = "d_ticker_sixhours_";
	/*** K线-交割合约12小时*/
	public static final String D_TICKER_TWELVEHOURS = "d_ticker_twelvehours_";
	/*** K线-交割合约1天*/
	public static final String D_TICKER_ONEDAY = "d_ticker_oneday_";
	/*** K线-交割合约1周*/
	public static final String D_TICKER_ONEWEEK = "d_ticker_oneweek_";
	/*** K线-交割合约1月*/
	public static final String D_TICKER_ONEMONTH = "d_ticker_onemonth_";
	/*** 本周模拟交易排名*/
	public static final String D_TRADE_WEEK_RANKING = "d_trade_week_ranking";
	/*** 本月模拟交易排名*/
	public static final String D_TRADE_MONTH_RANKING = "d_trade_month_ranking";
	/*** 本日模拟交易排名*/
	public static final String D_TRADE_DAY_RANKING = "d_trade_day_ranking";
	/*** 交割缓存挂单锁  */
	public static final String D_PENDING_CACHE_LOCK = "d_pending_cache_lock_";
	/*** 交割挂单锁  */
	public static final String D_PENDING_LOCK = "d_pending_lock_";
	/*** 交割计划挂单锁  */
	public static final String D_PLAN_PENDING_LOCK = "d_plan_pending_lock_";
	
	public static final String D_KLINE = "d_kline_";
	
	public static final String D_LAST_KLINE = "d_last_kline_";
	
	public static final String D_LAST_PRICE_ONE_HOUR_AGO = "d_last_price_one_hour_ago_";
	
	public static final String D_LAST_PRICE_ONE_DAY_AGO = "d_last_price_one_day_ago_";
	
	public static final String D_OPEN_PRICE = "d_open_price_";
	
	public static final String D_OVER_MATCH_SETTING = "d_over_match_setting_";
	public static final String DELIVERY_BASE2 = "delivery_base2";
	public static final String DELIVERY_BASE = "delivery_base";
	
	public static final String D_MARK_PRICE_LIST = "d_mark_price_list_";

	public static final String D_MARK_PRICE = "d_mark_price_";
	
	/*** websocket用户的最新交易记录 列表（最多100条）*/
	public static final String D_WS_ORDER_LIST = "_d_ws_order_list_";
	public static final String D_WS_ORDER_LAST_ORDER_ID = "_d_ws_order_last_order_id_";
	/*** websocket用户的最新挂单 列表*/
	public static final String D_WS_ORDER_UPDATE = "_d_ws_order_update_";
	/*** websocket订阅用户 列表*/
	public static final String D_WS_SUBBED_USERS = "_d_ws_subbed_users";
	/*** websocket订阅用户的新增挂单 列表锁 */
	public static final String D_WS_ORDER_NEW_LIST_LOCK = "_d_ws_order_new_list_lock_";
	/*** websocket订阅用户的新增挂单 列表*/
	public static final String D_WS_ORDER_NEW_LIST = "_d_ws_order_new_list_";
	
	/*** 模拟合约类型 列表*/
	public static final String V_TRANS_TYPES = "v_trans_types";
	/*** 模拟合约介绍详情  */
	public static final String V_TRANS_INTRODUCE = "v_trans_introduce_";
	/*** 模拟合约持仓量 */
	public static final String V_TICKER_HOLDVOL = "v_ticker_holdvol";
	/*** 有效合约集合 的key */
	public static final String V_TRANSACTIONLIST = "v_transactionList_key";
	/*** 单合约的key */
	public static final String V_TRANSACTION_KEY = "v_transaction_key_";
	/*** Ticker的交易量 的key */
	public static final String V_TICKER_TRADEVOL = "v_ticker_tradevol";
	/*** Ticker的交易量 的key */
	public static final String V_TICKER_TRADEVOLBTC = "v_ticker_tradevolbtc";
	public static final String V_TICKER_TRADEBTC = "v_ticker_tradebtc";
	/*** Ticker的交易量 的key */
	public static final String V_TICKER_TOTALVOL = "v_ticker_totalvol";
	/*** 昨日均价 */
	public static final String V_TICKER_YESTERDAYAVGPRICE = "v_ticker_yesterdayaveprice";
	
	public static final String V_HUOBI_DEPTH_UPDATE = "v_huobi_depth_update_";
	
	/*** 外部市场行情  */
	public static final String V_EXCHANGE_TICKER = "v_exchange_ticker_";
	
	public static final String V_HEDGING_BALANCE = "v_hedging_balance_";
	
	public static final String V_BTCCOEFFICIENT = "_v_trade_coefficient";
	
	public static final String V_BTCLATESTPRICE = "_v_latest_price";
	
	public static final String V_BTCHIGHSTPRICE = "_v_high_price_";

	public static final String V_BTCLOWSTPRICE = "_v_low_price_";
	
	public static final String V_BUYTOP5LIST = "v_buytop5List";
	
	public static final String V_SELLTOP5LIST = "v_selltop5List";
	
	public static final String V_DIGITS = "_v_digits";
	
	public static final String V_TICKER_BTC = "v_ticker";
	
	public static final String V_TICKER_BTC_OBJ = "v_ticker_obj";
	
	public static final String V_ARBITRAGE_SETTING = "v_arbitrage_setting_";
	
	public static final String V_DEPTH_CODE = "v_depth_";

	public static final String V_TRADE_CODE = "v_trade_";
	
	public static final String V_FIRST_PRICE = "_v_first_price";
	
	public static final String V_HOLD_COUNT = "_v_hold_count";
	
	public static final String V_CONTRACT_BASE = "v_contract_base";
	
	public static final String V_INDEX_PRICE_RATE_UP = "_v_index_price_rate_up";
	
	public static final String V_INDEX_PRICE_RATE_DOWN = "_v_index_price_rate_down";
	
	public static final String V_COMMON_DATA = "v_common_data_";
	
	public static final String V_SYMBOLS_DATA = "v_symbols_data_key";
	
	public static final String V_ACCOUNT_CONTRACT = "_v_account_contract_";
	
	/*** K线-模拟交易1分钟*/
	public static final String V_TICKER_ONEMINUTE = "v_ticker_oneminute_";
	/*** K线-模拟交易3分钟*/
	public static final String V_TICKER_THREEMINUTES = "v_ticker_threeminutes_";
	/*** K线-模拟交易5分钟*/
	public static final String V_TICKER_FIVEMINUTES = "v_ticker_fiveminutes_";
	/*** K线-模拟交易15分钟*/
	public static final String V_TICKER_FIFTEENMINUTES = "v_ticker_fifteenminutes_";
	/*** K线-模拟交易30分钟*/
	public static final String V_TICKER_HALFHOUR = "v_ticker_halfhour_";
	/*** K线-模拟交易1小时*/
	public static final String V_TICKER_ONEHOUR = "v_ticker_onehour_";
	/*** K线-模拟交易2小时*/
	public static final String V_TICKER_TWOHOURS = "v_ticker_twohours_";
	/*** K线-模拟交易4小时*/
	public static final String V_TICKER_FOURHOURS = "v_ticker_fourhours_";
	/*** K线-模拟交易6小时*/
	public static final String V_TICKER_SIXHOURS = "v_ticker_sixhours_";
	/*** K线-模拟交易12小时*/
	public static final String V_TICKER_TWELVEHOURS = "v_ticker_twelvehours_";
	/*** K线-模拟交易1天*/
	public static final String V_TICKER_ONEDAY = "v_ticker_oneday_";
	/*** K线-模拟交易1周*/
	public static final String V_TICKER_ONEWEEK = "v_ticker_oneweek_";
	/*** K线-模拟交易1月*/
	public static final String V_TICKER_ONEMONTH = "v_ticker_onemonth_";
	/*** 本周模拟交易排名*/
	public static final String V_TRADE_WEEK_RANKING = "v_trade_week_ranking";
	/*** 本月模拟交易排名*/
	public static final String V_TRADE_MONTH_RANKING = "v_trade_month_ranking";
	/*** 本日模拟交易排名*/
	public static final String V_TRADE_DAY_RANKING = "v_trade_day_ranking";
	/*** 模拟缓存挂单锁  */
	public static final String V_PENDING_CACHE_LOCK = "v_pending_cache_lock_";
	/*** 模拟挂单锁  */
	public static final String V_PENDING_LOCK = "v_pending_lock_";
	/*** 计划挂单锁  */
	public static final String V_PLAN_PENDING_LOCK = "v_plan_pending_lock_";
	
	public static final String V_KLINE = "v_kline_";
	
	public static final String V_LAST_KLINE = "v_last_kline_";
	
	public static final String V_OVER_MATCH_SETTING = "v_over_match_setting_";
	
	public static final String V_MARK_PRICE_LIST = "v_mark_price_list_";

	public static final String V_MARK_PRICE = "v_mark_price_";
	
	public static final String V_FUNDING_FEE_RATE = "v_funding_fee_rate_";
	
	public static final String V_FUNDING_FEE_SETTINGS = "v_funding_fee_settings";
	
	
	public static final String ALL_CONTRACT_PARTITION = "all_contract_partition";
	//获奖记录
	public static final String AWARD_WINNING_LIST ="award_winning_list";
	//奖项设置
	public static final String PRIZE_SETTING ="prize_setting";
}
