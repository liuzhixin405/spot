package com.btc100.btc_q.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.NMemberTransactionDao;
import com.btc100.btc_q.dao.NTransactionDao;
import com.btc100.btc_q.dao.NTransactionRateDao;
import com.btc100.btc_q.dao.NTransactionTypeDao;
import com.btc100.btc_q.entity.NMemberTransaction;
import com.btc100.btc_q.entity.NTransaction;
import com.btc100.btc_q.entity.NTransactionRate;
import com.btc100.btc_q.entity.NTransactionType;
import com.btc100.btc_q.service.NTransactionService;
import com.btc100.btc_q.vo.PagerModel;

@Service("nTransactionService")
public class NTransactionServiceImpl implements NTransactionService {

	@Autowired
	private NTransactionDao<NTransaction> nTransactionDao;
	@Autowired
	private NTransactionTypeDao<NTransactionType> bqTransactionTypeDao;
	@Autowired
	private NMemberTransactionDao<NMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private NTransactionRateDao<NTransactionRate> bqTransactionRateDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public void createTable(String partition, String code) {
		nTransactionDao.createTransactionRelated(partition, code);
	}
	
	public long time() {
		return nTransactionDao.getUnix_TIMESTAMP();
	}

	public NTransaction findTransByCode(String partition, String code) {
		NTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = nTransactionDao.getBySql("select * from " + partition + "_transaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}
	
	public NTransaction findTransToUpdateByCode(String partition, String code) {
		return nTransactionDao.findTranByCode(partition, code);
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("code", code);
//		return	nTransactionDao.getBySql("select * from " + partition + "_transaction t where t.transaction_code = :code ", params);
	}
	
	public NTransaction readTransByCode(String partition, String code) {
		NTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = nTransactionDao.getBySql("select * from " + partition + "_transaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + partition + "_" + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}

	public List<NTransaction> findTransByState_new(String partition, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<NTransaction> list = nTransactionDao.findBySql("select * from " + partition + "_transaction WHERE transaction_state = :state ORDER BY sort ASC", params);

		return list;
	}

	public List<NTransaction> readTransByState_new(String partition, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<NTransaction> list = nTransactionDao.findBySql("select * from " + partition + "_transaction WHERE transaction_state = :state ORDER BY sort ASC", params);

		return list;
	}

	public PagerModel findTransByState_pm(String partition, String code, int state) {
		StringBuilder whereHql = new StringBuilder("");
		Map<String, Object> params = new HashMap<String, Object>();
		if (state != 0) {
			params.put("state", state);
			whereHql.append(" and transaction_state = :state");
		}
		if (code != null && !code.isEmpty()) {
			params.put("code", code.trim());
			whereHql.append(" and transaction_code = :code");
		}
		return nTransactionDao.searchSql("select * from " + partition + "_transaction where 1 = 1 " + whereHql.toString() + " order by transaction_state ASC, transaction_code DESC",
				params);
	}

	public void updateTrans(String partition, String bqCode, String multiple, BigDecimal tradeCoefficient) {
		StringBuilder setHql = new StringBuilder("");
		Map<String, Object> params = new HashMap<String, Object>();
		if (multiple != null && !multiple.isEmpty()) {
			setHql.append(", multiple = :multiple");
			params.put("multiple", multiple);
		}
		if (tradeCoefficient.compareTo(BigDecimal.ZERO) > 0) {
			setHql.append(", trade_coefficient = :tradeCoefficient");
			params.put("tradeCoefficient", tradeCoefficient);
		}
		String strSetHql = setHql.toString();
		if (strSetHql.startsWith(",")) {
			strSetHql = strSetHql.substring(1, strSetHql.length());
			params.put("bqCode", bqCode);
			nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET " + strSetHql + " where transaction_code = :bqCode", params);
		}
	}
	
	public int updateTrans(String partition, long bqCodeid, BigDecimal highest, BigDecimal lowest) {
		return updateTrans(partition, bqCodeid, highest, lowest, BigDecimal.ZERO);
	}

	public int updateTrans(String partition, long bqCodeid, BigDecimal highest, BigDecimal lowest, BigDecimal last) {
		if (highest.compareTo(BigDecimal.ZERO) <= 0 
				&& lowest.compareTo(BigDecimal.ZERO) <= 0 
				&& last.compareTo(BigDecimal.ZERO) <= 0) {
			return 1;
		}
		
		String hql = "UPDATE " + partition + "_transaction SET ";
		Map<String, Object> params = new HashMap<String, Object>();
		if (highest.compareTo(BigDecimal.ZERO) > 0) {
			hql += "transaction_highest=:high,";
			params.put("high", highest);
		}
		
		if (lowest.compareTo(BigDecimal.ZERO) > 0) {
			hql += "transaction_lowest=:low,";
			params.put("low", lowest);
		}
		
		if (last.compareTo(BigDecimal.ZERO) > 0) {
			hql += "latest_transaction_price=:last,";
			params.put("last", last);
		}
		
		hql = hql.substring(0, hql.length() - 1);
		
		hql += " where transaction_id = :bqCode";
		
		params.put("bqCode", bqCodeid);
		return nTransactionDao.updateConditionBySql(hql, params);
	}
	
	@Override
	public void addMultipleByCode(String partition, String bqCode, String multiple, String multipleTrans, String multipleState, String multipleStateTrans) {
		if (multipleTrans == null || multipleStateTrans == null || multipleTrans.isEmpty() || multipleStateTrans.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("multiple", multiple);
			params.put("multiple_state", multipleState);
			params.put("bqCode", bqCode);
			nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
					params);
		} else {
			int index = -1;
			String[] multiples = multipleTrans.split(",");
			for (int i = 0; i < multiples.length; i++) {
				if (multiples[i].equals(multiple)) {
					index = i;
					break;
				}
			}
			if (index < 0) { // 不包含，添加
				String multipleNew = multipleTrans + "," + multiple;
				String multipleStateNew = multipleStateTrans + "," + multipleState;
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("multiple", multipleNew);
				params.put("multiple_state", multipleStateNew);
				params.put("bqCode", bqCode);
				nTransactionDao.updateConditionBySql(
						"UPDATE " + partition + "_transaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
			} else { // 包含，替换
				String[] multipleStates = multipleStateTrans.split(",");
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < multipleStates.length; i++) {
					if (i == index)
						sb.append(multipleState).append(",");
					else
						sb.append(multipleStates[i]).append(",");
				}
				String multipleStateNew = sb.toString();
				if (multipleStateNew.endsWith(",")) {
					multipleStateNew = multipleStateNew.substring(0, multipleStateNew.length() - 1);
				}
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("multiple_state", multipleStateNew);
				params.put("bqCode", bqCode);
				nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET multiple_state = :multiple_state where transaction_code = :bqCode", params);
			}

		}
	}

	public void delMultipleByCode(String partition, String bqCode, String multiple, String multipleTrans, String multipleStateTrans) {
		int index = -1;

		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		String[] multiples = multipleTrans.split(",");
		for (int i = 0; i < multiples.length; i++) {
			if (multiples[i].equals(multiple)) {
				index = i;
			} else
				sb1.append(multiples[i]).append(",");
		}

		String multipleNew = sb1.toString();
		if (multipleNew.endsWith(",")) {
			multipleNew = multipleNew.substring(0, multipleNew.length() - 1);
		}

		if (index >= 0) {
			String[] multipleStates = multipleStateTrans.split(",");
			for (int i = 0; i < multipleStates.length; i++) {
				if (i != index)
					sb2.append(multipleStates[i]).append(",");
			}

			String multipleStateNew = sb2.toString();
			if (multipleStateNew.endsWith(",")) {
				multipleStateNew = multipleStateNew.substring(0, multipleStateNew.length() - 1);
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("multiple", multipleNew);
			params.put("multiple_state", multipleStateNew);
			params.put("bqCode", bqCode);
			nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
					params);
		}
	}

	@Override
	public void updateMultipleByCode(String partition, String bqCode, String multiple, String multipleState) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("multiple", multiple);
		params.put("multiple_state", multipleState);
		params.put("bqCode", bqCode);
		nTransactionDao.updateConditionBySql(
				"UPDATE " + partition + "_transaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
	}

	public void addCoefficientByCode(String partition, String bqCode, BigDecimal tradeCoefficient) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeCoefficient", tradeCoefficient);
		params.put("bqCode", bqCode);
		nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET trade_coefficient = :tradeCoefficient where transaction_code = :bqCode", params);
	}

	public Boolean addMultipleByCode(String partition, int currencyId, String bqCode, int state, String multiple, String multipleState,
			BigDecimal tradeCoefficient, long endTime, int typeId, int transactionKind, String showName, 
			String enName, String jpName, String krName, String hkName) {
		BigDecimal ZERO = BigDecimal.ZERO;

		NTransactionType transType = bqTransactionTypeDao.getTransTypeById(partition, typeId);
		if (transType == null) return false;
		
		long linux_time = bqMemberTransactionDao.getUnix_TIMESTAMP();
		NTransaction transaction = new NTransaction(bqCode, linux_time, endTime, state, ZERO, ZERO, ZERO, typeId, 
				multiple, tradeCoefficient, multipleState,typeId,transType.getType_code(), transactionKind, currencyId, showName);
		transaction.setTransaction_show_name_en(enName);
		transaction.setTransaction_show_name_jp(jpName);
		transaction.setTransaction_show_name_kr(krName);
		transaction.setTransaction_show_name_hk(hkName);
		nTransactionDao.insert(partition, transaction);

		NTransaction bqTransaction = nTransactionDao.findTranByCode(partition, bqCode);
		NMemberTransaction bqmt_duo = new NMemberTransaction(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(), Contents.BQMEMBER_ADMIN,
				Contents.TRADE_TYPE_BUY, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, 1);
		bqMemberTransactionDao.insert(partition, bqmt_duo);

		NMemberTransaction bqmt_kong = new NMemberTransaction(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.BQMEMBER_ADMIN, Contents.TRADE_TYPE_SELL, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -1);
		bqMemberTransactionDao.insert(partition, bqmt_kong);

		nTransactionDao.createTransactionRelated(partition, bqCode);

		NTransactionRate bqtRate = new NTransactionRate();
		bqtRate.setTransaction_code(bqCode);
		if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 1) {
			bqtRate.setUprate(new BigDecimal("0.1"));
			bqtRate.setDownrate(new BigDecimal("0.1"));
		} else if (bqTransaction.getTransaction_kind() != null && bqTransaction.getTransaction_kind() == 2) {
			bqtRate.setUprate(new BigDecimal("0.1"));
			bqtRate.setDownrate(new BigDecimal("0.1"));
		} else {
			bqtRate.setUprate(new BigDecimal("0.05"));
			bqtRate.setDownrate(new BigDecimal("0.05"));
		}
		bqTransactionRateDao.insert(partition, bqtRate);
		
		return true;
	}

	public NTransaction get(String partition, long transactionId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transaction_id", transactionId);
		return nTransactionDao.getBySql("select * from " + partition + "_transaction where transaction_id = :transaction_id", params);
	}

	public String updateTrans(String partition, List<NTransaction> transactionList, HttpServletRequest request) {
		for (NTransaction trans : transactionList) {
			String bqCode = trans.getTransaction_code();
			int sort = trans.getSort();
			String bqCode_client = request.getParameter(bqCode);

			if (!bqCode.equalsIgnoreCase(bqCode_client)) {
				return "合约号不一致";
			}

			String sort_client = request.getParameter(String.valueOf(sort));
//			trans.setSort(ConvertUtil.toInt(sort_client, 10000));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("transaction_id", trans.getTransaction_id());
			params.put("sort", sort_client);
			nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET sort = :sort where transaction_id = :transaction_id",
					params);
		}
		return "success";
	}

	@Override
	public void changePauseAndTransfer(String partition, String bqCode, boolean suspend, boolean transferIn, boolean transferOut) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", bqCode);
		params.put("suspend", suspend);
		params.put("transferIn", transferIn);
		params.put("transferOut", transferOut);
		nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET suspend = :suspend, transfer_in_enable = :transferIn, transfer_out_enable = :transferOut where transaction_code = :code",
				params);
	}

	@Override
	public List<NTransaction> findTransByKind(String partition, int kind, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("kind", kind);

		List<NTransaction> list = nTransactionDao.findBySql("select * from " + partition + "_transaction WHERE transaction_state = :state AND transaction_kind = :kind ORDER BY sort ASC", params);
		return list;
	}

	@Override
	public List<NTransaction> readOrderById(String partition, int limit) {
		String hql = "select * from " + partition + "_transaction ORDER BY transaction_id DESC ";

		return nTransactionDao.findBySql(hql, 1, limit);
	}

	@Override
	public void updateShowName(String partition, long id, String name, String en, String jp, String kr, String hk) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("en", en);
		params.put("jp", jp);
		params.put("kr", kr);
		params.put("hk", hk);
		params.put("id", id);
		nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET transaction_show_name=:name, transaction_show_name_en=:en, transaction_show_name_jp=:jp,"
				+ "transaction_show_name_kr =:kr,transaction_show_name_hk=:hk where transaction_id = :id",
				params);
	}

	@Override
	public void updateKind(String partition, long id, int kind) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("kind", kind);
		params.put("id", id);
		nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET transaction_kind=:kind where transaction_id = :id",
				params);
	}

	@Override
	public void updateState(String partition, long id, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("id", id);
		nTransactionDao.updateConditionBySql("UPDATE " + partition + "_transaction SET transaction_state=:state where transaction_id = :id",
				params);
	}

}
