package com.btc100.btc_q.service.impl;

import com.btc100.btc_q.common.BTCQMemecacheKeys;
import com.btc100.btc_q.common.Contents;
import com.btc100.btc_q.common.SpyMemcachedClient;
import com.btc100.btc_q.dao.BQAccountDao;
import com.btc100.btc_q.dao.BQCurrencyTypeDao;
import com.btc100.btc_q.dao.BQExchangeSettingDao;
import com.btc100.btc_q.dao.BQMemberDao;
import com.btc100.btc_q.dao.BQMemberRechargeDao;
import com.btc100.btc_q.entity.BQAccount;
import com.btc100.btc_q.entity.BQCurrencyType;
import com.btc100.btc_q.entity.BQExchangeSetting;
import com.btc100.btc_q.entity.BQMember;
import com.btc100.btc_q.entity.BQMemberRecharge;
import com.btc100.btc_q.service.BQCurrencyTypeService;
import com.btc100.btc_q.vo.PagerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("bqCurrencyTypeService")
public class BQCurrencyTypeServiceImpl implements BQCurrencyTypeService {

	@Autowired
	private SpyMemcachedClient spyMemcachedClient;
	@Autowired
	private BQCurrencyTypeDao<BQCurrencyType> bqCurrencyTypeDao;
	@Autowired
	private BQAccountDao<BQAccount> bqAccountDao;
	@Autowired
	private BQMemberDao<BQMember> bqMemberDao;
	@Autowired
	private BQMemberRechargeDao<BQMemberRecharge> bqMemberRechargeDao;
	@Autowired
	private BQExchangeSettingDao<BQExchangeSetting> bqExchangeSettingDao;

	@Override
	public BQCurrencyType getByTypeName(String name) {
		return bqCurrencyTypeDao.getByTypeName(name);
	}


	@Override
	public BQCurrencyType readByTypeName(String name) {
		return bqCurrencyTypeDao.getByTypeName(name);
	}

	@Override
	public void save(BQCurrencyType type) {
		bqCurrencyTypeDao.save(type);
		
		if ("BTC".equals(type.getType_name().toUpperCase())) {
			List<BQAccount> accounts = bqAccountDao.getAllList(BQAccount.class);
			for (BQAccount account : accounts) {
				if (account.getBtc().compareTo(BigDecimal.ZERO) <= 0) continue;
				BQMemberRecharge recharge = new BQMemberRecharge();
				recharge.setAmount(account.getBtc());
				recharge.setConfirm_time(bqMemberRechargeDao.getUnix_TIMESTAMP());
				recharge.setCurrency_type_id(account.getCurrency_type_id());
				recharge.setDateline(bqMemberRechargeDao.getUnix_TIMESTAMP());
				recharge.setF_user_email(account.getF_user_email());
				recharge.setF_user_id(account.getF_user_id());
				recharge.setFee(BigDecimal.ZERO);
				recharge.setIp("");
				recharge.setOperation(Contents.RECHARGE_TYPE_CZ);
				recharge.setReceice_card_no("");
				recharge.setReceice_card_type(Contents.RECHARGE_FLAG_ADMIN);
				recharge.setRemark("平台btc剩余记录");
				recharge.setState(Contents.RECHARGE_STATE_ED);
				recharge.setWallet_trade_id("");
				bqMemberRechargeDao.save(recharge);
			}
		} else {
			List<BQMember> members = bqMemberDao.getAllList(BQMember.class);
			for (BQMember member : members) {
				bqAccountDao.insertAccount(member, type.getType_id());
			}
		}
		
		BQExchangeSetting exchangeSetting = new BQExchangeSetting();
		exchangeSetting.setCurrency_type_id(type.getType_id());
		exchangeSetting.setSymbol(type.getType_name());
		bqExchangeSettingDao.save(exchangeSetting);
	}

	@Override
	public List<BQCurrencyType> findAll() {
		List<BQCurrencyType> currencyTypes = spyMemcachedClient.get(BTCQMemecacheKeys.CURRENCY_TYPES);
		if (currencyTypes == null) {
			
			currencyTypes = bqCurrencyTypeDao.findAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.CURRENCY_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, currencyTypes);
		}
		
		return currencyTypes;
	}
	
	@Override
	public List<BQCurrencyType> readAll() {
		List<BQCurrencyType> currencyTypes = spyMemcachedClient.get(BTCQMemecacheKeys.CURRENCY_TYPES);
		if (currencyTypes == null) {
			
			currencyTypes = bqCurrencyTypeDao.findAll();
			spyMemcachedClient.set(BTCQMemecacheKeys.CURRENCY_TYPES, BTCQMemecacheKeys.TIMES.TEN_SECOND, currencyTypes);
		}
		
		return currencyTypes;
	}
	
	@Override
	public BQCurrencyType readByTypeId(int id) {
		return bqCurrencyTypeDao.get(BQCurrencyType.class, id);
	}

	@Override
	public BQCurrencyType getByTypeId(int id) {
		return bqCurrencyTypeDao.get(BQCurrencyType.class, id);
	}

	@Override
	public int updateRecharge(int typeId, boolean isRecharge, boolean state) {
		Map<String, Object> map = new HashMap<>();
		String hql = "";
		if (isRecharge) {
			hql = "update BQCurrencyType set enable_recharge=:state where type_id=:id";
		} else {
			hql = "update BQCurrencyType set enable_withdraw=:state where type_id=:id";
		}

		map.put("state", state);
		map.put("id", typeId);

		return bqCurrencyTypeDao.updateCondition(hql, map);
	}

	@Override
	public PagerModel readAllTypes() {
		return bqCurrencyTypeDao.searchHql("from BQCurrencyType where state = 1");
	}

	@Override
	public void update(BQCurrencyType type) {
		bqCurrencyTypeDao.update(type);
	}

	@Override
	public List<BQCurrencyType> readAll(boolean withUnuse) {
		return bqCurrencyTypeDao.findAll(withUnuse);
	}
}
