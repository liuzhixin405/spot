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
import com.btc100.btc_q.dao.DeliveryMemberTransactionDao;
import com.btc100.btc_q.dao.DeliveryTransactionDao;
import com.btc100.btc_q.dao.DeliveryTransactionRateDao;
import com.btc100.btc_q.dao.DeliveryTransactionTypeDao;
import com.btc100.btc_q.entity.DeliveryMemberTransaction;
import com.btc100.btc_q.entity.DeliveryTransaction;
import com.btc100.btc_q.entity.DeliveryTransactionRate;
import com.btc100.btc_q.entity.DeliveryTransactionType;
import com.btc100.btc_q.service.DeliveryTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.util.DateUtil;
import com.btc100.btc_q.util.StringUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("deliveryTransactionService")
public class DeliveryTransactionServiceImpl implements DeliveryTransactionService {

	@Autowired
	private DeliveryTransactionDao<DeliveryTransaction> deliveryTransactionDao;
	@Autowired
	private DeliveryTransactionTypeDao<DeliveryTransactionType> deliveryTransactionTypeDao;
	@Autowired
	private DeliveryMemberTransactionDao<DeliveryMemberTransaction> deliveryMemberTransactionDao;
	@Autowired
	private DeliveryTransactionRateDao<DeliveryTransactionRate> deliveryTransactionRateDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public void createTable(String transactioCode, String code, String deliveryType) {
		deliveryTransactionDao.createTransactionRelated(transactioCode, code, deliveryType);
	}
	
	public long time() {
		return deliveryTransactionDao.getUnix_TIMESTAMP();
	}

	public DeliveryTransaction findTransByCode(String code) {
		DeliveryTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTION_KEY + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = deliveryTransactionDao.get(" from DeliveryTransaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}
	
	public DeliveryTransaction findTransToUpdateByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return	deliveryTransactionDao.get(" from DeliveryTransaction t where t.transaction_code = :code ", params);
	}
	
	public DeliveryTransaction readTransByCode(String code) {
		DeliveryTransaction transaction = spyMemcachedClient.get(BTCQMemecacheKeys.D_TRANSACTION_KEY + code);
		if (transaction == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			transaction = deliveryTransactionDao.get(" from DeliveryTransaction t where t.transaction_code = :code ", params);
			if (transaction != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.D_TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, transaction);
			}
		}
		
		return transaction;
	}

	public List<DeliveryTransaction> findTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<DeliveryTransaction> list = deliveryTransactionDao.find("FROM DeliveryTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);

		return list;
	}
	
	public List<DeliveryTransaction> readTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);

		List<DeliveryTransaction> list = deliveryTransactionDao.find("FROM DeliveryTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);

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
		return deliveryTransactionDao.searchHql("From DeliveryTransaction where 1 = 1 " + whereHql.toString() + " order by transaction_state ASC, transaction_code DESC",
				params);
	}
	
	public void closeTransaction(String bqCode) {
		deliveryTransactionDao.closeTransaction(bqCode);
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
			deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET " + strSetHql + " where transaction_code = :bqCode", params);
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
		
		String hql = "UPDATE DeliveryTransaction SET ";
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
		return deliveryTransactionDao.updateCondition(hql, params);
	}
	
	@Override
	public void addMultipleByCode(String bqCode, String multiple, String multipleTrans, String multipleState, String multipleStateTrans) {
		if (multipleTrans == null || multipleStateTrans == null || multipleTrans.isEmpty() || multipleStateTrans.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("multiple", multiple);
			params.put("multiple_state", multipleState);
			params.put("bqCode", bqCode);
			deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
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
				deliveryTransactionDao.updateCondition(
						"UPDATE DeliveryTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
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
				deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET multiple_state = :multiple_state where transaction_code = :bqCode", params);
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
			deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode",
					params);
		}
	}

	@Override
	public void updateMultipleByCode(String bqCode, String multiple, String multipleState) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("multiple", multiple);
		params.put("multiple_state", multipleState);
		params.put("bqCode", bqCode);
		deliveryTransactionDao.updateCondition(
				"UPDATE DeliveryTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
	}

	public void addCoefficientByCode(String bqCode, BigDecimal tradeCoefficient) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeCoefficient", tradeCoefficient);
		params.put("bqCode", bqCode);
		deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET trade_coefficient = :tradeCoefficient where transaction_code = :bqCode", params);
	}

	public Boolean addMultipleByCode(String bqCode, String deliveryType, int state, String multiple, String multipleState,
			BigDecimal tradeCoefficient, long endTime, int typeId, int transactionKind, String showName, 
			String enName, String jpName, String krName, String hkName) {
		BigDecimal ZERO = BigDecimal.ZERO;

		DeliveryTransactionType transType = deliveryTransactionTypeDao.getTransTypeById(typeId);
		if (transType == null) return false;
		
		String dateStr = DateUtil.dateToStr(endTime, "yyyyMMdd");
		String transactioCode = bqCode + "_" + dateStr + deliveryType; // 日交割：btc_20191227d，季度交割：btc_20191227q
		if (StringUtil.isEmpty(showName)) {
			showName = transactioCode.toUpperCase();
		}
		
		long linux_time = deliveryMemberTransactionDao.getUnix_TIMESTAMP();
		DeliveryTransaction transaction = new DeliveryTransaction(transactioCode, deliveryType, linux_time, endTime, state, ZERO, ZERO, ZERO,
				typeId, multiple, tradeCoefficient, multipleState, typeId, transType.getType_code(), transactionKind,
				showName, enName, jpName, krName, hkName);
		transaction.setTransaction_show_name_en(enName);
		transaction.setTransaction_show_name_jp(jpName);
		transaction.setTransaction_show_name_kr(krName);
		transaction.setTransaction_show_name_hk(hkName);
		deliveryTransactionDao.save(transaction);

		DeliveryTransaction deliveryTransaction = deliveryTransactionDao.findTranByCode(transactioCode);
		DeliveryMemberTransaction bqmt_duo = new DeliveryMemberTransaction(deliveryTransaction.getTransaction_id(), deliveryTransaction.getTransaction_code(), Contents.BQMEMBER_ADMIN,
				Contents.TRADE_TYPE_BUY, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, 1);
		deliveryMemberTransactionDao.save(bqmt_duo);

		DeliveryMemberTransaction bqmt_kong = new DeliveryMemberTransaction(deliveryTransaction.getTransaction_id(), deliveryTransaction.getTransaction_code(),
				Contents.BQMEMBER_ADMIN, Contents.TRADE_TYPE_SELL, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -1);
		deliveryMemberTransactionDao.save(bqmt_kong);

		deliveryTransactionDao.createTransactionRelated(transactioCode, bqCode, deliveryType);

		DeliveryTransactionRate bqtRate = new DeliveryTransactionRate();
		bqtRate.setTransaction_code(transactioCode);
		if (deliveryTransaction.getTransaction_kind() != null && deliveryTransaction.getTransaction_kind() == 1) {
			bqtRate.setUprate(new BigDecimal("0.1"));
			bqtRate.setDownrate(new BigDecimal("0.1"));
		} else if (deliveryTransaction.getTransaction_kind() != null && deliveryTransaction.getTransaction_kind() == 2) {
			bqtRate.setUprate(new BigDecimal("0.1"));
			bqtRate.setDownrate(new BigDecimal("0.1"));
		} else {
			bqtRate.setUprate(new BigDecimal("0.2"));
			bqtRate.setDownrate(new BigDecimal("0.2"));
		}
		deliveryTransactionRateDao.save(bqtRate);
		
		return true;
	}

	public DeliveryTransaction get(long transactionId) {
		return deliveryTransactionDao.get(DeliveryTransaction.class, transactionId);
	}

	public void update(DeliveryTransaction bqtTemp) {
		deliveryTransactionDao.update(bqtTemp);

	}

	public String updateTrans(List<DeliveryTransaction> transactionList, HttpServletRequest request) {
		for (DeliveryTransaction trans : transactionList) {
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
			deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET sort = :sort where transaction_id = :transaction_id",
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
		deliveryTransactionDao.updateCondition("UPDATE DeliveryTransaction SET suspend = :suspend, transfer_in_enable = :transferIn, transfer_out_enable = :transferOut where transaction_code = :code",
				params);
	}

	@Override
	public List<DeliveryTransaction> findTransByKind(int kind, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("kind", kind);

		List<DeliveryTransaction> list = deliveryTransactionDao.find("FROM DeliveryTransaction WHERE transaction_state = :state AND transaction_kind = :kind ORDER BY sort ASC", params);
		return list;
	}

	@Override
	public List<DeliveryTransaction> findTransByDeliveryType(String deliveryType, int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		params.put("deliveryType", deliveryType);
		
		List<DeliveryTransaction> list = deliveryTransactionDao.find("FROM DeliveryTransaction WHERE transaction_state = :state AND delivery_type = :deliveryType ORDER BY sort ASC", params);
		return list;
	}

	@Override
	public List<DeliveryTransaction> readOrderById(int limit) {
		String hql = "FROM DeliveryTransaction ORDER BY transaction_id DESC ";

		return deliveryTransactionDao.find(hql, 1, limit);
	}

}
