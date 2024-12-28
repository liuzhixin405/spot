package com.btc100.btc_q.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.dao.NArbitrageSettingDao;
import com.btc100.btc_q.entity.NArbitrageSetting;
import com.btc100.btc_q.service.NArbitrageSettingService;

@Service("nArbitrageSettingService")
public class NArbitrageSettingServiceImpl implements NArbitrageSettingService {

	@Autowired
	private NArbitrageSettingDao<NArbitrageSetting> nArbitrageSettingDao;

	@Override
	public NArbitrageSetting getArbitrageSettingByCode(String partition, String code) {
		return nArbitrageSettingDao.getArbitrageSettingByCode(partition, code);
	}
	
	@Override
	public NArbitrageSetting readArbitrageSettingByCode(String partition, String code) {
		return nArbitrageSettingDao.getArbitrageSettingByCode(partition, code);
	}

	@Override
	public void save(final String partition, final NArbitrageSetting ba) {
		
		nArbitrageSettingDao.getSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				PreparedStatement psmt = conn.prepareStatement("insert into " + partition + "_arbitrage_setting "
						+ "(f_code,depth_uid,depth_url,spare_depth_url,depth_buy_range,depth_sell_range,depth_amount_min,depth_amount_max,running,scalping,"
						+ "scalping_frequency_min,scalping_frequency_max,scalping_min_each,scalping_max_each,auto_trade,hedging,hedging_buy_range,hedging_sell_range,"
						+ "scalping_index_range,scalping_depth_range,curr_use_state,risk_manage,depth_price_gap,depth_amount_gap_min,depth_amount_gap_max,"
						+ "depth_index_range,depth_size,price_digits,amount_digits,can_trade_self,speed_type)"
						+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, ba.getF_code());
				psmt.setLong(2, ba.getDepth_uid());
				psmt.setString(3, ba.getDepth_url());
				psmt.setString(4, ba.getSpare_depth_url());
				psmt.setBigDecimal(5, ba.getDepth_buy_range());
				psmt.setBigDecimal(6, ba.getDepth_sell_range());
				psmt.setBigDecimal(7, ba.getDepth_amount_min());
				psmt.setBigDecimal(8, ba.getDepth_amount_max());
				psmt.setBoolean(9, ba.getRunning());
				psmt.setBoolean(10, ba.getScalping());
				psmt.setLong(11, ba.getScalping_frequency_min());
				psmt.setLong(12, ba.getScalping_frequency_max());
				psmt.setBigDecimal(13, ba.getScalping_min_each());
				psmt.setBigDecimal(14, ba.getScalping_max_each());
				psmt.setBoolean(15, ba.getAuto_trade());
				psmt.setBoolean(16, ba.getHedging());
				psmt.setBigDecimal(17, ba.getHedging_buy_range());
				psmt.setBigDecimal(18, ba.getHedging_sell_range());
				psmt.setBigDecimal(19, ba.getScalping_index_range());
				psmt.setBigDecimal(20, ba.getScalping_depth_range());
				psmt.setLong(21, ba.getCurr_use_state());
				psmt.setBoolean(22, ba.getRisk_manage());
				psmt.setBigDecimal(23, ba.getDepth_price_gap());
				psmt.setBigDecimal(24, ba.getDepth_amount_gap_min());
				psmt.setBigDecimal(25, ba.getDepth_amount_gap_max());
				psmt.setBigDecimal(26, ba.getDepth_index_range());
				psmt.setLong(27, ba.getDepth_size());
				psmt.setLong(28, ba.getPrice_digits());
				psmt.setLong(29, ba.getAmount_digits());
				psmt.setBoolean(30, ba.isCan_trade_self());
				psmt.setLong(31, ba.getSpeed_type());
				
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				ba.setId(num);
			}
		});
	}

	@Override
	public void update(final String partition, final NArbitrageSetting ba) {
		nArbitrageSettingDao.getSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				PreparedStatement psmt = conn.prepareStatement("update " + partition + "_arbitrage_setting set "
						+ "f_code=?,depth_uid=?,depth_url=?,spare_depth_url=?,depth_buy_range=?,depth_sell_range=?,depth_amount_min=?,depth_amount_max=?,running=?,scalping=?,"
						+ "scalping_frequency_min=?,scalping_frequency_max=?,scalping_min_each=?,scalping_max_each=?,auto_trade=?,hedging=?,hedging_buy_range=?,hedging_sell_range=?,"
						+ "scalping_index_range=?,scalping_depth_range=?,curr_use_state=?,risk_manage=?,depth_price_gap=?,depth_amount_gap_min=?,depth_amount_gap_max=?,"
						+ "depth_index_range=?,depth_size=?,price_digits=?,amount_digits=?,can_trade_self=?,speed_type=? "
						+ "where id=?",
						Statement.RETURN_GENERATED_KEYS);
				psmt.setString(1, ba.getF_code());
				psmt.setLong(2, ba.getDepth_uid());
				psmt.setString(3, ba.getDepth_url());
				psmt.setString(4, ba.getSpare_depth_url());
				psmt.setBigDecimal(5, ba.getDepth_buy_range());
				psmt.setBigDecimal(6, ba.getDepth_sell_range());
				psmt.setBigDecimal(7, ba.getDepth_amount_min());
				psmt.setBigDecimal(8, ba.getDepth_amount_max());
				psmt.setBoolean(9, ba.getRunning());
				psmt.setBoolean(10, ba.getScalping());
				psmt.setLong(11, ba.getScalping_frequency_min());
				psmt.setLong(12, ba.getScalping_frequency_max());
				psmt.setBigDecimal(13, ba.getScalping_min_each());
				psmt.setBigDecimal(14, ba.getScalping_max_each());
				psmt.setBoolean(15, ba.getAuto_trade());
				psmt.setBoolean(16, ba.getHedging());
				psmt.setBigDecimal(17, ba.getHedging_buy_range());
				psmt.setBigDecimal(18, ba.getHedging_sell_range());
				psmt.setBigDecimal(19, ba.getScalping_index_range());
				psmt.setBigDecimal(20, ba.getScalping_depth_range());
				psmt.setLong(21, ba.getCurr_use_state());
				psmt.setBoolean(22, ba.getRisk_manage());
				psmt.setBigDecimal(23, ba.getDepth_price_gap());
				psmt.setBigDecimal(24, ba.getDepth_amount_gap_min());
				psmt.setBigDecimal(25, ba.getDepth_amount_gap_max());
				psmt.setBigDecimal(26, ba.getDepth_index_range());
				psmt.setLong(27, ba.getDepth_size());
				psmt.setLong(28, ba.getPrice_digits());
				psmt.setLong(29, ba.getAmount_digits());
				psmt.setBoolean(30, ba.isCan_trade_self());
				psmt.setLong(31, ba.getSpeed_type());
				psmt.setLong(32, ba.getId());
				
				psmt.executeUpdate();
				ResultSet results = psmt.getGeneratedKeys();
				long num = -1;
				if (results.next()) {
					num = results.getLong(1);
				}

				ba.setId(num);
			}
		});
	}

}
