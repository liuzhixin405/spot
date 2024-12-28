package com.btc100.btc_q.autoservice;

import org.apache.log4j.Logger;

import com.btc100.btc_q.init.SpringContext;
import com.wallet.platfrom.filter.AbstractWalletPlatfromFilter;
import com.wallet.platfrom.sdk.IWalletPlatfromInterface;

public class WalletPlatfromAutoServiceFilter extends AbstractWalletPlatfromFilter {
	
	private static final Logger LOGGER = Logger.getLogger(WalletPlatfromAutoServiceFilter.class);
	
	@Override
	public IWalletPlatfromInterface getWalletPlatfromInterface() {
		return SpringContext.getBean(IWalletPlatfromInterface.class);
	}

	@Override
	protected void startFilter(String walletMethod, String data, String sign) {
		LOGGER.debug("[method] <-- " + walletMethod + "\n[data] <-- " + data + "\n[sign] <-- " + sign);
	}

	@Override
	protected void writeData(String data) {
		LOGGER.debug("[data] --> " + data);
	}

	
}
