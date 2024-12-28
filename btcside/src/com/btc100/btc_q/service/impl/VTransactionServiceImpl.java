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
import com.btc100.btc_q.dao.VMemberTransactionDao;
import com.btc100.btc_q.dao.VTransactionDao;
import com.btc100.btc_q.dao.VTransactionRateDao;
import com.btc100.btc_q.dao.VTransactionTypeDao;
import com.btc100.btc_q.entity.VMemberTransaction;
import com.btc100.btc_q.entity.VTransaction;
import com.btc100.btc_q.entity.VTransactionRate;
import com.btc100.btc_q.entity.VTransactionType;
import com.btc100.btc_q.service.VTransactionService;
import com.btc100.btc_q.util.ConvertUtil;
import com.btc100.btc_q.vo.PagerModel;

@Service("vTransactionService")
public class VTransactionServiceImpl implements VTransactionService {

	@Autowired
	private VTransactionDao<VTransaction> vTransactionDao;
	@Autowired
	private VTransactionTypeDao<VTransactionType> vTransactionTypeDao;
	@Autowired
	private VMemberTransactionDao<VMemberTransaction> vMemberTransactionDao;
	@Autowired
	private VTransactionRateDao<VTransactionRate> vTransactionRateDao;
	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	
	@Override
	public void createTable(String code) {
		vTransactionDao.createTransactionRelated(code);
	}
	
	public long time() {
		return vTransactionDao.getUnix_TIMESTAMP();
	}

	public VTransaction findTransByCode(String code) {
		VTransaction trans = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTION_KEY + code);
		if (trans == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			trans = vTransactionDao.get(" from VTransaction t where t.transaction_code = :code ", params);
			
			if (trans != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, trans);
			}
		}
		
		return trans;
	}
	
	public VTransaction findTransToUpdateByCode(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return vTransactionDao.get(" from VTransaction t where t.transaction_code = :code ", params);
	}
	
	public VTransaction readTransByCode(String code) {
		VTransaction trans = spyMemcachedClient.get(BTCQMemecacheKeys.V_TRANSACTION_KEY + code);
		if (trans == null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("code", code);
			trans = vTransactionDao.get(" from VTransaction t where t.transaction_code = :code ", params);
			
			if (trans != null) {
				spyMemcachedClient.set(BTCQMemecacheKeys.V_TRANSACTION_KEY + code, BTCQMemecacheKeys.TIMES.TEN_SECOND, trans);
			}
		}
		
		return trans;
	}

	public List<VTransaction> findTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		List<VTransaction> list = vTransactionDao.find("FROM VTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);
		return list;
	}
	
	public List<VTransaction> readTransByState_new(int state) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("state", state);
		List<VTransaction> list = vTransactionDao.find("FROM VTransaction WHERE transaction_state = :state ORDER BY sort ASC", params);
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
		return vTransactionDao.searchHql("From VTransaction where 1 = 1 " + whereHql.toString() + " order by transaction_state ASC, transaction_code DESC",
				params);
	}

	public void updateTrans(String vCode, String multiple, BigDecimal tradeCoefficient) {
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
			params.put("vCode", vCode);
			vTransactionDao.updateCondition("UPDATE VTransaction SET " + strSetHql + " where transaction_code = :vCode", params);
		}
	}

	public int updateTrans(long vCodeid, BigDecimal highest, BigDecimal lowest) {
		String hql = "UPDATE VTransaction SET transaction_highest=:high,transaction_lowest=:low where transaction_id = :vCode";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("high", highest);
		params.put("low", lowest);
		params.put("vCode", vCodeid);
		return vTransactionDao.updateCondition(hql, params);
	}

	public void addMultipleByCode(String vCode, String multiple, String multipleTrans, String multipleState, String multipleStateTrans) {
		if (multipleTrans == null || multipleStateTrans == null || multipleTrans.isEmpty() || multipleStateTrans.isEmpty()) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("multiple", multiple);
			params.put("multiple_state", multipleState);
			params.put("vCode", vCode);
			vTransactionDao.updateCondition("UPDATE VTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :vCode",
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
				params.put("vCode", vCode);
				vTransactionDao.updateCondition(
						"UPDATE VTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :vCode", params);
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
				params.put("vCode", vCode);
				vTransactionDao.updateCondition("UPDATE VTransaction SET multiple_state = :multiple_state where transaction_code = :vCode", params);
			}
		}
	}

	public void delMultipleByCode(String vCode, String multiple, String multipleTrans, String multipleStateTrans) {
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
			params.put("vCode", vCode);
			vTransactionDao.updateCondition("UPDATE VTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :vCode",
					params);
		}
	}

	@Override
	public void updateMultipleByCode(String bqCode, String multiple, String multipleState) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("multiple", multiple);
		params.put("multiple_state", multipleState);
		params.put("bqCode", bqCode);
		vTransactionDao.updateCondition(
				"UPDATE VTransaction SET multiple= :multiple, multiple_state = :multiple_state where transaction_code = :bqCode", params);
	}

	public void addCoefficientByCode(String vCode, BigDecimal tradeCoefficient) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeCoefficient", tradeCoefficient);
		params.put("vCode", vCode);
		vTransactionDao.updateCondition("UPDATE VTransaction SET trade_coefficient = :tradeCoefficient where transaction_code = :vCode", params);
	}

	public Boolean addMultipleByCode(String vCode, int state, int currencyId, String multiple, String multipleState, BigDecimal tradeCoefficient, long endTime, int typeId) {
		BigDecimal ZERO = BigDecimal.ZERO;

		VTransactionType transType = vTransactionTypeDao.getTransTypeById(typeId);
		if (transType == null) return false;
		
		long linux_time = vMemberTransactionDao.getUnix_TIMESTAMP();
		VTransaction transaction = new VTransaction(vCode, linux_time, endTime, state, ZERO, ZERO, ZERO, typeId, 
				multiple, tradeCoefficient, multipleState,typeId,transType.getType_code());
		transaction.setCurrency_type_id(currencyId);
		vTransactionDao.save(transaction);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", vCode);
		VTransaction vTransaction = vTransactionDao.get(" from VTransaction t where t.transaction_code = :code ", params);
		VMemberTransaction vmt_duo = new VMemberTransaction(vTransaction.getTransaction_id(), vTransaction.getTransaction_code(), Contents.BQMEMBER_ADMIN,
				Contents.TRADE_TYPE_BUY, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, 1);
		vMemberTransactionDao.save(vmt_duo);

		VMemberTransaction vmt_kong = new VMemberTransaction(vTransaction.getTransaction_id(), vTransaction.getTransaction_code(),
				Contents.BQMEMBER_ADMIN, Contents.TRADE_TYPE_SELL, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, ZERO, -1);
		vMemberTransactionDao.save(vmt_kong);

		vTransactionDao.createTransactionRelated(vCode);

		VTransactionRate vtRate = new VTransactionRate();
		vtRate.setTransaction_code(vCode);
		vtRate.setUprate(new BigDecimal("0.05"));
		vtRate.setDownrate(new BigDecimal("0.05"));
		vTransactionRateDao.save(vtRate);
		
		return true;
	}

	public void update(VTransaction vtTemp) {
		vTransactionDao.update(vtTemp);

	}

	public String updateTrans(List<VTransaction> transactionList, HttpServletRequest request) {
		for (VTransaction trans : transactionList) {
			String vCode = trans.getTransaction_code();
			int sort = trans.getSort();
			String vCode_client = request.getParameter(vCode);

			if (!vCode.equalsIgnoreCase(vCode_client)) {
				return "合约号不一致";
			}

			String sort_client = request.getParameter(String.valueOf(sort));
			trans.setSort(ConvertUtil.toInt(sort_client, 10000));
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("transaction_id", trans.getTransaction_id());
			params.put("sort", trans.getSort());
			vTransactionDao.updateCondition("UPDATE VTransaction SET sort = :sort where transaction_id = :transaction_id",
					params);
		}
		return "success";
	}

	@Override
	public void changePauseAndTransfer(String vCode, boolean suspend, boolean transferIn, boolean transferOut) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", vCode);
		params.put("suspend", suspend);
		params.put("transferIn", transferIn);
		params.put("transferOut", transferOut);
		vTransactionDao.updateCondition("UPDATE VTransaction SET suspend = :suspend, transfer_in_enable = :transferIn, transfer_out_enable = :transferOut where transaction_code = :code",
				params);
	}

}
