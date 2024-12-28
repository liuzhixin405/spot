<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>	
	
	<div class="block-title" style="border-bottom: none;">
      <h2> Glossary </h2>
    </div>
    <div class="main-wrap">
        <div class="freedom-broker">
          <h2>Contract</h2>
          <div class="help-text">
            <p>
	            Contracts refers to the standardized trading products designed by crypto asset contract trading platform Btcside.
				Both buyers and sellers should follow the specifications and rules set for each contract and pay a certain percentage in USDT as margins.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Underlying Asset</h2>
          <div class="help-text">
            <p>
	            Underlying asset refers to the benchmark asset of each product. For example, the underlying asset for Bitcoin contracts would be Bitcoin Price Index.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Margin Trading</h2>
          <div class="help-text">
            <p>
	            Maintenance margin is the minimum amount of equity that must be maintained in a margin account in order to maintain one’s position. This creates a gearing effect for the contract.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Open a position/Open positions</h2>
          <div class="help-text">
            <p>
	            Taking a position is the action where investors buy (taking a long position) or sell (taking a short position) a certain amount of contracts. 
				Just like in spot trading buyers or sellers must have a counterparty in the opposite position.
            </p>
            <p>
	            For example, if user A want to buy 10 Bitcoin contracts and user B wishes to sell 10 of his contracts, then they will be matched with A taking a long position and B taking a short one.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Close a position/ Close positions</h2>
          <div class="help-text">
            <p>
	            Liquidation means when users trade against their original position, in order to stop losses from unfavorable price movements.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Mandatory Liquidation Trigger Price</h2>
          <div class="help-text">
            <p>
	            Mandatory Liquidation Trigger Price is the price which will be forced to place order by the system at the blow up price when your position margin is insufficient. When the remaining amount of the loss is less than 20% of the margin, the Mandatory 
				Liquidation will be triggered, and will entrust the settlement with the blow up price.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Strike Price</h2>
          <div class="help-text">
            <p>
                Btcside prices are automatically matched by our trading computer with priorities given to the optimization of prices, time, and to liquidations.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Long Position, Short Position</h2>
          <div class="help-text">
            <p>When a user believes the value of a certain asset will increase, the action of buying those contracts is called taking a long position.
            </p>
            <p>When a user believes the value of a certain asset will decrease, the action of selling those contracts is called taking a short position.</p>
            <p>
            	For example: User A buys 10 BTC Contracts, suppose user B sells 10 BTC Contracts at the same time, the trade was executed at $8,000 
				with A taking long position with 10 contracts and B taking short position with 10 contracts. In the event of Bitcoin prices rose to $8,500 
				A liquidated 5 of his/her contracts and B bought 5 contracts. After this trade A is now taking long position with only 5 contracts, and B IS taking short position with only 5 contracts.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Position/ Open interest</h2>
          <div class="help-text">
            <p>Position refers to the state in which the contract trader holds an open contract.
            </p>
            <p>Open interest refers to the number of open contracts held by the contract trader.</p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Normal Market/ Inverted Market</h2>
          <div class="help-text">
            <p>
            	Normal market refers to the situation in which the distant months are at a premium to the nearby months.
            </p>
            <p>
            	inverted market occurs when near maturity contracts are higher in price than far maturity contracts. Another term for this condition is backwardation.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Bullish Market Bearish Market</h2>
          <div class="help-text">
            <p>Bullish Market: Refers to the situation where the market is on the raise</p>
            <p>Bearish Market: Refers to the situation where the market is on the fall.</p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Order Matching</h2>
          <div class="help-text">
            <p>Order matching refers to the process where a computer system matches orders from buyers and sellers. 
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Minimum Tick</h2>
          <div class="help-text">
            <p>Minimum Tick refers to the smallest change in price that will get reflected on Btcside.
            </p>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Hedging</h2>
          <div class="help-text">
            <p>Hedging in futures trading is where a party with interested tied to a certain asset uses the futures contract market to limit his/her exposure to risks.
            </p>
            <p><b>39、Let’s tale Bitcoin contracts as an example:</b>
            </p>
            <p>
            	Using futures contracts to hedge for Bitcoins refers to parties like Bitcoin miners, 
				BTC Payment providers and long term holders utilizes the futures contract market to 
				limit his/her exposure to risks. It can be further dividend into long hedge and short hedge.
            </p>
            <ol>
              <li>
	              The seller's selling period hedge:the miners who produce Bitcoin should do a sell hedging transaction on Bitcoin virtual contract trading market to prevent prices from falling
	              and affecting the mining revenue when in the high price. After that, even if the Bitcoin price plummets, it will not have too much impact on profits;
              </li>
              <li>
	              The operator's hedging: operators who will buy Bitcoin in the future can do a sell hedging transaction if they are afraid that they will get lower price Bitcoin as they believe the current price is higher;
	              operators who will pay Bitcoin in the future can do a buy hedging transaction if they are afraid that they will pay more as they believe the current price is lower, but they do not want to buy Bitcoin with a lot of money now.;
              </li>
              <li>
	              The holder's hedging: If someone temporarily holds a large amount of Bitcoin for some reason, he/she fears that the price will fall or the assets will shrink for the reason of Bitcoin industry in the future,
	              he/she can throw it up and do a sell hedging.
              </li>
            </ol>
          </div>
        </div>
        <div class="freedom-broker">
          <h2>Spread trading</h2>
          <div class="help-text">
            <p>
	            Hedging is a low-risk investment behavior. The participation of spread traders is conducive to the improvement of market pricing mechanism. The formation of a market price mechanism is essential.
	            Spread tradings are divided into calendar spread, intercommodity spread, cross-time spread, and cross-market spread. A brief explanation is as follows:
            </p>
            <ol>
              <li>
                <b>Intercommodity spread refers to the use of price differences between two different but interrelated contracts for arbitrage.</b>
                <p>
	                Buys (sells) a contract for a certain delivery month and sells (buys) another contract with related products in the same delivery month. For example,
	                there is a price difference between Bitcoin and Litecoin, and investors can use the ratio change relationship between the two coins to make intercommodity spread.
				</p>
              </li>
              <li>
                <b>Cross-time spread refers to arbitrage with the price difference of Bitcoin contract and cash commodity</b>
                <p>Cross-time spread refers to arbitrage with the price difference of the underlying contract price and spot price</p>
                <p>
	                In theory, the contract price is the future price of a target product, and the spot price is the current price of a target product. According to the same price theory in economics,
	                the gap between the two is the “basis difference” (basis difference = spot price - contract price), which should be equal to the holding cost of the product. In Bitcoin trading, bitcoin's holding cost are mainly reflected in transaction fees.
                </p>
              </li>
              <li>
                <b>Cross-market spread is to arbitrate with different contract prices in different exchanges of a underlying specific product.</b>
                <p>The arbitrage cost mainly refers to transaction fees. It should be noted, however, that if the delivery underlying assets of the two contract markets are not completely homogenous, there may be a difference.</p>
              </li>
            </ol>
          </div>
        </div>
        <div class="warning-help">
          <p>
	          <b>Statement:</b>The platform reserves the right to modify the relevant trading rules based on changes in the market or industry. If there are any changes, we will notify you through various channels in advance. However, 
	          we do not guarantee that we will be able to notify every user. Therefore, please continue to pay attention to this page or Website announcements to get the latest rules information. In addition, the relevant examples mentioned in this explanation are only for the purpose of making the rules be easy to understand, and the platform cannot guarantee it absolute accurate. As a result, the platform is not liable for any loss.
          </p>
        </div>
      </div>