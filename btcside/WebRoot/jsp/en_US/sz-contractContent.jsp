<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>				
				
				
		<div class="block-title" style="border-bottom: none;">
			<h2>
				EOS Contract
			</h2>
		</div>
		<div class="main-wrap">
			<div class="freedom-broker">
				<h2>
					EOS Contract Introduction
				</h2>
				<div class="help-text">
					<p>
						Btcside EOS contract is a standardized trading contract created by crypto asset trading platform Btcside.
						Btcside Bitcoin contracts are perpetual, and can be traded at any given time. There is no settlement date in the contract,
						a Btcside Bitcoin contract will only be automatically liquidated when there is insufficient maintenance margin.
					</p>
					<h3>
						EOS Contract Specifications
					</h3>
					<table cellpadding="0" cellspacing="0" style="width: 100%;" class="help-table">
						<tr>
							<td width="150">
								Product
							</td>
							<td>
								EOS
							</td>
						</tr>
						<tr>
							<td>
								Underlying Asset
							</td>
							<td>
								EOS Price Index(EPI)
							</td>
						</tr>
						<tr>
							<td>
								Minimum tradable unit
							</td>
							<td>
								10 EOS
							</td>
						</tr>
						<tr>
							<td>
								Pricing Unit
							</td>
							<td>
								USDT/EOS
							</td>
						</tr>
						<tr>
							<td>
								Minimum Tick
							</td>
							<td>
								0.0001USDT/per
							</td>
						</tr>
						<tr>
							<td>
								Contract Type
							</td>
							<td>
								Perpetual Contract
							</td>
						</tr>
						<tr>
							<td>
								Price Fluctuation Range
							</td>
							<td>
								EPI price ±5%
							</td>
						</tr>
						<tr>
							<td>
								Trading Hours
							</td>
							<td>
								24/7
							</td>
						</tr>
					</table>
				</div>
			</div>
			<div class="block-title" style="border-bottom: none;">
				<h2>
					Trading Rules
				</h2>
			</div>
			<div class="freedom-broker">
				<h2>
					P/L calculations
				</h2>
				<div class="help-text">
					<p>
						Btcside EOS Contracts utilizes USDT, which is pegged to USD at 1:1 ratio, as margin deposits and the P/L calculation equation is as follows:
					</p>
					<p>
						<b>Profits (or Losses)= price margin*Contract Size*number of contracts</b>
					</p>
					<p>
						<b>Definitions:</b>
					</p>
					<ol>
						<li>
							Price margin: The difference between market price and average cost of one’s contracts.
						</li>
						<li>
							Contract Size: Contract size is the deliverable quantity of commodities or financial instruments underlying futures and options contracts that are traded on an exchange.
						</li>
					</ol>
					<p>
						<b>P/L calculations examples:</b>
					</p>
					
					<p>
						Assume user A took a long position at $15 per contract for 100 contracts, total contract value= 15×1×100=$1,500.
					</p>
					<ul>
						<li>
							If the value of EOS increased to $15.5 then the profits equal to （15.5-15）×1×100=0.5×100=$50,
						</li>
						<li>
							assume A used 20 times leverage( 5% maintenance margin) then the capital needed is $75 and the ROI is 66.67%.
						</li>
					</ul>
					<ul>
						<li>
							 
							If EOS price fell to $14.5 then the losses equal (15-14.5）×1×100=0.5×100=$50,
						</li>
						<li>
							under 20 times leverage the user would suffer a 66.67% loss.
						<li>
					</ul>
					<p>
						(For simplicity’s sake, transactions fees were excluded from the above calculations.)
					</p>
				</div>
			</div>
			<div class="freedom-broker">
				<h2>
					Quotation Restrictions
				</h2>
				<div class="help-text">
					<p>
						Quote means that you can open a long position (bullish) and open a short position (bearish) according to your judgment of the market trend; 
						accordingly, you can also autonomously carry out the operations of long positions and short positions. However, in order to curb the adverse impact of 
						sudden abnormal price fluctuations on market prices, we have made the following mandatory requirements for your open and close quotes:
					</p>
					<p>
						The sustainable contract normal trading price deviation from the price index is generally not more than 5%. For example, 
						the current EOS price index is $15, the 5% price index deviates from the minimum of $14.25 and the highest $15.75, and the maximum offer price is $15.75, 
						and the shortest offer is $14.25. If the index price is now $16, the lowest and highest quotes are $15.2 and $16.8.
					</p>
				</div>
			</div>
			<div class="freedom-broker">
				<h2>
					Take Profit and Stop Loss
				</h2>
				<div class="help-text">
					<p>
						<b>Take Profit: </b>This function allows users to set a certain price and allow the system to automatically sell the asset in order to profit from their positions. 
						The system will trigger when there’s a 0.1% price difference above the set price.
					</p>
					<p>
						Example on the Take Profit function: If user A bought a contract at $15 and wishes to take long position, 
						then he/she can set the Take Profit price at $15.5 taken the 0.1% price difference into consideration the system will execute the sale at $15.5155
					</p>
					<p>
						<b>Stop-Loss: </b>This function allows users to set a certain price and allow the system to automatically sell the asset in order to prevent further losses. 
						The system will trigger when there’s a 0.1% price difference above the set price.
					</p>
					<p>
						Example on the Stop Loss function: If user B went long at $15 per contract and used 10 times leverage then price for margin call would be $13.5. 
						In order to prevent that from happening he/she could set the Stop Loss price at $13.5 taken the 0.1% price difference into consideration the system will execute the sale at $13.5135
					</p>
					<p>
						<b>It should be noted that:</b>
					</p>
					<ol>
						<li>
							To prevent systemic risk caused by big deviations of your hanging price and market price, we prevent you to fill in the trigger price. 
							We automatically take the deviation price of 0.1% of your intent order price as the trigger price;
						</li>
						<li>
							When one of Take Profit or Stop Loss function is triggered the other one is automatically disabled. For example: when the Take Profit is triggered an order would be posted, 
							regardless of the status of the order it will not be automatically withdrawn and the Stop Loss function will be disabled unless you manually withdraw the order and reset the Stop Loss price.
						</li>
						<li>
							The Take Profit and Stop Loss function only work with your currently available contracts.
						</li>
						<li>
							To lower the systematic risk Btcside only post the orders for you, that does not mean those orders will definitely get executed.
							Users should acknowledge the risk involved and shouldn’t overly depend on those functions.
						</li>
					</ol>
				</div>
			</div>
			<div class="freedom-broker">
						<h2>
							Margin Call
						</h2>
						<div class="help-text">
							<p>
								Btcside requires its users to maintain an account balance, aside from the margin account, larger than the actual loss.
								The system will calculate the actual P/L using the user’s average cost on the contracts and the index price. 
								When the index price hits the force liquidation point, Btcside will place a buy order (if the user took short position) or
								a sell order (in case the user took long position) equal to the amount of his/her margin balance plus the aforementioned account balance. 
								Note that, Btcside only places the order for you we cannot guarantee they will get executed. if those order were not all executed within a minute then Btcside will sell off the same amount of orders from the counterparty with the most profits.
							</p>
							<p>
								<b>Mandatory Liquidation Trigger Price:</b>
								Mandatory Liquidation Trigger Price is the price which will be forced to place order by the system at the blow up price when your position margin is insufficient. When the remaining amount of the loss is less than 20% of the margin, the Mandatory 
								Liquidation will be triggered, and will entrust the settlement with the blow up price.
							</p>
							<p>
								<b>Maintenance Margins:</b>
								 The margin needed to maintain your current position.
							</p>
							<p>
								<b>Funds in Reserve:</b>
								Total funds transferred into the contract account minus the cost of the contracts, maintenance margin and varies fees.<br>
								(Transfer-in Amount of the Contract - Transfer-out Amount of Contract - Open Position Margin - Order Frozen Amount - Open Position Fee - Hanging Order Fee)
							</p>
							<p>
								<b>Funds in Use:</b>
								Refers to the amount deducted from Funds in Reserve when the user has suffered a loss.
							</p>
							<p>
								<b>Available Balance:</b>
								Funds in Reserve minus the losses. The available balance can be used to place new orders.
							</p>
							<p>
								<b>Contract Adjustment Ratio:</b>
								0.2
							</p>
							<p><b>Force Liquidation Price:<br>
								&nbsp;BUY:[(Contract Available + Margin + Reserve Fund Amount)-(Average price of position-Force Liquidation Price)*Number of &nbsp;positions]/Margin=Contract Adjustment Ratio<br>
								&nbsp;SELL:[(Contract Available + Margin + Reserve Fund Amount)-(Force Liquidation Price-Average price of position)*Number of &nbsp;positions]/Margin=Contract Adjustment Ratio<br><br>
								Burned Position Price:<br>
								&nbsp;BUY:[(Contract available + margin + occupancy amount of reserve fund)-(Average price of positions-Burned Position Price)*Number of positions- &nbsp;Burned Position Price*Number of positions*Eat order rate]/margin =0<br>
								&nbsp;SELL:[(Contract available + margin + occupancy amount of reserve fund)-(Burned Position Price-Average price of positions)*Number of positions- &nbsp;Burned Position Price*Number of positions*Eat order rate]/margin =0
							</b></p>
						</div>	
					</div>
					<div class="freedom-broker">
						<h2>
							System Automatic Sale mechanism
						</h2>
						<div class="help-text">
							<p>
								When the Force Liquidation Price has been reached, users’ position will be forcefully liquidated, 
								if not all of the contracts have been executed within a minute Btcside will sell off positions held by
								the counterparty that profited the most until all remaining contracts are sold. 
							</p>
							<p>
								For example,30 Bitcoin contracts has been liquidated and 20 contracts were still remaining after one minute. 
								Assume there were 4 counterparties A, B, C and D holding 5,10,20 and 30 contracts respectively. 
								As a result, 5 of A’s contracts, 10 of B’s contracts and 5 of C’s contracts will be sold off.
							</p>
						</div>
					</div>
					<div class="warning-help">
						<p>
							<b>Disclaimer:</b>Btcside reserves the right to change and modify relative trading rules in response to changes in the market or industry.
							In an event of change of rules, Btcside will send out announcement ahead of time on all official channels.
							However, Btcside cannot guarantee that all users can be notified in time. Please stay up to date on our announcement section of the official
							website in order to better protect your interests. Also, the definitions and examples of terms given by Btcside only serves as a tool to better
							help you understand the rules, we cannot guarantee their 100% accuracy, Btcside is not responsible for any losses incurred.
						</p>
					</div>
		</div>