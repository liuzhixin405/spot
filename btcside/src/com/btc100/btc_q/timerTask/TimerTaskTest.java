package com.btc100.btc_q.timerTask;

import org.springframework.stereotype.Component;

/**
 * 此Demo是为了分离4个合约，分别用线程计算4个合约的交易Demo
 * @author Administrator
 *
 */
@Component
public class TimerTaskTest {

//	@Autowired
//	private BQTransactionService transactionService;

	public void run() {
//		List<String> lstTrads = transactionService.findTransByState(Contents.TRANSACTION_STATE_ZC);
//
//		for (String code : lstTrads) {
//			TransThread thread = new TransThread(code);
//			thread.start();
//		}
	}

}

class TransThread extends Thread {
	private String bqCode;

	public TransThread(String name) {
		this.bqCode = name;
	}

	public void run() {
		System.out.println(bqCode + "运行");
	}
}