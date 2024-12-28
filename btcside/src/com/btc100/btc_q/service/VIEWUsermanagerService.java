package com.btc100.btc_q.service;

import com.btc100.btc_q.entity.VIEWUsermanager;
import com.btc100.btc_q.vo.PagerModel;

public interface VIEWUsermanagerService {

	public PagerModel findModel(long uid, String usertel, String useremail, String countryName, long inviteId, int userLevelId, int type);

	public PagerModel findModel();

	public VIEWUsermanager findUser(long userid);

}
