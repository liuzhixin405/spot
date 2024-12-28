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
import com.btc100.btc_q.dao.BQMemberTransactionDao;
import com.btc100.btc_q.dao.BQTransactionDao;
import com.btc100.btc_q.dao.BQTransactionRateDao;
import com.btc100.btc_q.dao.BQTransactionTypeDao;
import com.btc100.btc_q.entity.BQMemberTransaction;
import com.btc100.btc_q.entity.BQTransaction;
import com.btc100.btc_q.entity.BQTransactionRate;
import com.btc100.btc_q.entity.BQTransactionType;
import com.btc100.btc_q.service.BQTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("bqTransactionService")
public class BQTransactionServiceImpl implements BQTransactionService {

	@Autowired
	private BQTransactionDao<BQTransaction> bqTransactionDao;
	@Autowired
	private BQTransactionTypeDao<BQTransactionType> bqTransactionTypeDao;
	@Autowired
	private BQMemberTransactionDao<BQMemberTransaction> bqMemberTransactionDao;
	@Autowired
	private BQTransactionRateDao<BQTransactionRate> bqTransactionRateDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public void createTable(String code) {
		bqTransactionDao.createTransactionRelated(code);
	}
	
	public long time() {
		return bqTransactionDao.getUnix_TIMESTAMP();
	}

	public BQTransaction findTransByCode(String code) {
		BQTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = bqTransactionDao.get(" from BQTransaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}
	
	public BQTransaction findTransToUpdateByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return	bqTransactionDao.get(" from BQTransaction t where t.transaction_code = :code ", params);
	}
	
	public BQTransaction readTransByCode(String code) {
		BQTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.TRANSACTION_KEY + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = bqTransactionDao.get(" from BQTransaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}

	public List<BQTransaction> findTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<BQTransaction> list = bqTransactionDao.find("FROM BQTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);

		return list;
	}

	public List<BQTransaction> readTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<BQTransaction> list = bqTransactionDao.find("FROM BQTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);

		return list;
	}

	public PagerModel findTransByState_pm(String code, int state) {
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
		return bqTransactionDao.searchHql("From BQTransaction where 1 = 1 " + whereHql.toString() + " order by transaction_state ASC, transaction_code DESC",
				params);
	}

	public void updateTrans(String bqCode, String multiple, BigDecimal tradeCoefficient) {
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
			bqTransactionDao.updateCondition("UPDATE BQTransaction SET " + strSetHql + " where transaction_code = :bqCode", params);
		}
	}
	
	public int updateTrans(long bqCodeid, BigDecimal highest, BigDecimal lowest) {
		return updateTrans(bqCodeid, highest, lowest, BigDecimal.ZERO);
	}

	public int updateTrans(long bqCodeid, BigDecimal highest, BigDecimal lowest, BigDecimal last) {
		if (highest.compareTo(BigDecimal.ZERO) <= 0 
				&& lowest.compareTo(BigDecimal.ZERO) <= 0 
				&& last.compareTo(BigDecimal.ZERO) <= 0) {
			return 1;
		}
		
		String hql = "UPDATE BQTransaction SET ";
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
		return bqTransactionDao.updateCondition(hql, params);
	}
	
	@Override
	public void addMultipleByCode(String bqCode, String multiple, String multipleTrans, String multipleState, String multipleStateTrans) {
		if (multipleTrans == null || multipleStateTrans == null || multipleTrans.isEmpty() || multipleStateTrans.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("multiple", multiple);
			params.put("multiple_state", multipleState);
			params.put("bqCode", bqCode);
			bqTransactionDao.updateCondition("UPDATE BQTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
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
				bqTransactionDao.updateCondition(
						"UPDATE BQTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
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
				bqTransactionDao.updateCondition("UPDATE BQTransaction SET multiple_state = :multiple_state where transaction_code = :bqCode", params);
			}

		}
	}

	public void delMultipleByCode(String bqCode, String multiple, String multipleTrans, String multipleStateTrans) {
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
			bqTransactionDao.updateCondition("UPDATE BQTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
					params);
		}
	}

	@Override
	public void updateMultipleByCode(String bqCode, String multiple, String multipleState) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("multiple", multiple);
		params.put("multiple_state", multipleState);
		params.put("bqCode", bqCode);
		bqTransactionDao.updateCondition(
				"UPDATE BQTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
	}

	public void addCoefficientByCode(String bqCode, BigDecimal tradeCoefficient) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeCoefficient", tradeCoefficient);
		params.put("bqCode", bqCode);
		bqTransactionDao.updateCondition("UPDATE BQTransaction SET trade_coefficient = :tradeCoefficient where transaction_code = :bqCode", params);
	}

	public Boolean addMultipleByCode(String bqCode, int state, String multiple, String multipleState,
			BigDecimal tradeCoefficient, long endTime, int typeId, int transactionKind, String showName, 
			String enName, String jpName, String krName, String hkName) {
		BigDecimal ZERO = BigDecimal.ZERO;

		BQTransactionType transType = bqTransactionTypeDao.getTransTypeById(typeId);
		if (transType == null) return false;
		
		long linux_time = bqMemberTransactionDao.getUnix_TIMESTAMP();
		BQTransaction transaction = new BQTransaction(bqCode, linux_time, endTime, state, ZERO, ZERO, ZERO, typeId, 
				multiple, tradeCoefficient, multipleState,typeId,transType.getType_code(), transactionKind, showName);
		transaction.setTransaction_show_name_en(enName);
		transaction.setTransaction_show_name_jp(jpName);
		transaction.setTransaction_show_name_kr(krName);
		transaction.setTransaction_show_name_hk(hkName);
		bqTransactionDao.save(transaction);

		BQTransaction bqTransaction = bqTransactionDao.findTranByCode(bqCode);
		BQMemberTransaction bqmt_duo = new BQMemberTransaction(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(), Contents.BQMEMBER_ADMIN,
				Contents.TRADE_TYPE_BUY, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, 1);
		bqMemberTransactionDao.save(bqmt_duo);

		BQMemberTransaction bqmt_kong = new BQMemberTransaction(bqTransaction.getTransaction_id(), bqTransaction.getTransaction_code(),
				Contents.BQMEMBER_ADMIN, Contents.TRADE_TYPE_SELL, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -1);
		bqMemberTransactionDao.save(bqmt_kong);

		bqTransactionDao.createTransactionRelated(bqCode);

		BQTransactionRate bqtRate = new BQTransactionRate();
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
		bqTransactionRateDao.save(bqtRate);
		
		return true;
	}

	public BQTransaction get(long transactionId) {
		return bqTransactionDao.get(BQTransaction.class, transactionId);
	}

	public void update(BQTransaction bqtTemp) {
		bqTransactionDao.update(bqtTemp);

	}

	public String updateTrans(List<BQTransaction> transactionList, HttpServletRequest request) {
		for (BQTransaction trans : transactionList) {
			String bqCode = trans.getTransaction_code();
			int sort = trans.getSort();
			String bqCode_client = request.getParameter(bqCode);

			if (!bqCode.equalsIgnoreCase(bqCode_client)) {
				return "合约号不一致";
			}

			String sort_client = request.getParameter(String.valueOf(sort));
			trans.setSort(ConvertUtil.toInt(sort_client, 10000));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("transaction_id", trans.getTransaction_id());
			params.put("sort", trans.getSort());
			bqTransactionDao.updateCondition("UPDATE BQTransaction SET sort = :sort where transaction_id = :transaction_id",
					params);
		}
		return "success";
	}

	@Override
	public void changePauseAndTransfer(String bqCode, boolean suspend, boolean transferIn, boolean transferOut) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", bqCode);
		params.put("suspend", suspend);
		params.put("transferIn", transferIn);
		params.put("transferOut", transferOut);
		bqTransactionDao.updateCondition("UPDATE BQTransaction SET suspend = :suspend, transfer_in_enable = :transferIn, transfer_out_enable = :transferOut where transaction_code = :code",
				params);
	}

	@Override
	public List<BQTransaction> findTransByKind(int kind, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("kind", kind);

		List<BQTransaction> list = bqTransactionDao.find("FROM BQTransaction WHERE transaction_state = :state AND transaction_kind = :kind ORDER BY sort ASC", params);
		return list;
	}

	@Override
	public List<BQTransaction> readOrderById(int limit) {
		String hql = "FROM BQTransaction ORDER BY transaction_id DESC ";

		return bqTransactionDao.find(hql, 1, limit);
	}

}
