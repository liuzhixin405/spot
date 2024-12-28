<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ include file="_basepage.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager"%>
<%@ taglib uri="http://tag.bfx.un" prefix="ut"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title><fmt:message key="msg.pg.userCenter.luckDrawActivity"/></title>
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/swiper.min.css?<%=rjs%>" />
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
    <jsp:include page="_common.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/luckDraw.css?<%=rjs%>" />
    <script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/swiper.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>
</head>

<body>
<div class="add-windows-user" id="addWindowsPrize">
    <div id="tb-overlay-prize" class="tb-overlay display-none"></div>
        <div class="add-prize-wrap" style="top:200px;" id="add-wrap-prize">
            <div class="dialogs-prize-title">
                <div class="dialogs-prize-item">
                    <div class="first-hidden"><span class="prize-title"><fmt:message key="msg.pg.userCenter.congratulationWinning"/></span><span class="prize-lever" id="prizeLever"></span></div>
                </div>
                <div class="dialogs-prize-item" style="text-align: left;">
                    <p class="prize-title-content" id="prizeContent"></p>
                </div>
                <div class="affirm-button">
                    <button type="button" id="closeAffirmButton"><fmt:message key="msg.pg.userCenter.continueToDraw"/></button>
                </div>
            </div>
        </div>
    </div>
</div>
    <jsp:include page="_top.jsp"></jsp:include>
    <div class="lk-container center-cont">
        <c:if test="${sessionScope.userinfo!=null}">
            <input type="hidden" id="userEmail" name="userEmail" value="${sessionScope.userinfo.f_user_email}">
        </c:if>
        <div class="turntable-content">
            <div class="turntable-title">
                <c:if test="${locale eq 'zh_CN'}">
                    <img class="celebrate-bfx" src="../common/images/luckDraw/Celebrate-bfx@2x.png">
                    <img class="celebrate-text" src="../common/images/luckDraw/the-seventh-anniversary.png">
                </c:if>
                <c:if test="${locale ne 'zh_CN'}">
                    <img class="celebrate-bfx" style="width: 321px;height: 57px;" src="../common/images/luckDraw/BFX EXCHANGE.png">
                    <img class="celebrate-text" style="width: 703px;height: 60px;" src="../common/images/luckDraw/the-seventh-anniversary-en.png">
                </c:if>

            </div>
            <div class="turntable-swiper">
                <div class="swiper-container" class="swiper-no-swiping">
                    <div class="swiper-wrapper">
                        <c:forEach items="${obtainRecords}" var="list" varStatus="status">
                            <div class="swiper-slide">
                                <span><fmt:message key="msg.pg.userCenter.congratulations"/><span class="listUserName">${list.userName}</span><fmt:message key="msg.pg.userCenter.winPrizeLottery"/><i class="turntable-prize">${list.notes}</i></span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
            <div class="turntable-luck-draw">
                <div class="turntable-luckdraw-bg"></div>
                <div class="turntable-luckdraw-disc" id="turntableLuckdrawDisc"></div>
                <div class="turntable-luckdraw-click">
                    <div class="turntable-luckdraw-pointer" onclick="getluckDrawResult();" style="pointer-events: none;cursor: default;"></div>
                    <c:if test="${sessionScope.userinfo != null}">
                        <div class="turntable-luckdraw-text luckdraw-position" style="pointer-events: none;cursor: default;<c:if test="${locale ne 'zh_CN' and locale ne 'zh_HK'}">font-size:22px;</c:if><c:if test="${locale eq 'en_US'}">bottom: 364px;left: 459px;</c:if><c:if test="${locale eq 'ja_JP'}">bottom: 364px;left: 462px;</c:if><c:if test="${locale eq 'ru_RU'}">bottom: 364px;left: 464px;</c:if><c:if test="${locale eq 'ko_KR'}">bottom: 364px;left: 464px;</c:if>" onclick="getluckDrawResult();" >
<%--                            <fmt:message key="msg.pg.userCenter.luckyDraw"/>--%>
<%--                            <span class="turntable-luckdraw-num" id="turntable-luckdraw-num">X${count }</span>--%>
                            <fmt:message key="msg.pg.userCenter.EndingActivities"/>
                        </div>
                    </c:if>
                    <c:if test="${sessionScope.userinfo == null}">
                        <div class="turntable-luckdraw-text luckdraw-position" style="pointer-events: none;cursor: default;<c:if test="${locale ne 'zh_CN' and locale ne 'zh_HK'}">font-size:22px;</c:if><c:if test="${locale eq 'en_US'}">bottom: 364px;left: 459px;</c:if><c:if test="${locale eq 'ja_JP'}">bottom: 364px;left: 462px;</c:if><c:if test="${locale eq 'ru_RU'}">bottom: 364px;left: 464px;</c:if><c:if test="${locale eq 'ko_KR'}">bottom: 364px;left: 464px;</c:if>" >
<%--                            <fmt:message key="msg.pg.userCenter.luckyDraw"/>--%>
<%--                            <span class="turntable-luckdraw-num" id="turntable-luckdraw-num">X0</span>--%>
                                <fmt:message key="msg.pg.userCenter.EndingActivities"/>
                        </div>
                    </c:if>
                </div>
            </div>
            <div><a class="batman" style="<c:if test="${locale ne 'zh_CN'}">background: url('../common/images/luckDraw/BFX-Batman-en.png') no-repeat !important;</c:if>" href="<%=basePath%>news/noticedo.do?id=507"></a></div>
            <div class="turntable-bg"></div>
        </div>
        <div class="turntable-items">
            <div class="turntable-items-padding">
                <c:if test="${sessionScope.userinfo != null}">
                    <div class="turntable-service-charge">
                        <div class="service-charge-title">
                            <span></span>
                            <span><fmt:message key="msg.pg.userCenter.todayCommission"/></span>
                            <span></span>
                        </div>
                        <div class="service-charge-content">
                            <div class="service-charge-item">
                                <span><fmt:formatNumber value="${usdtFee }" pattern="#0.0000"></fmt:formatNumber></span>
                                <span>USDT<fmt:message key="msg.pg.userCenter.section"/></span>
                                <span><fmt:message key="msg.pg.userCenter.withdrawFee"/></span>
                            </div>
                            <div class="service-charge-item">
                                <span><fmt:formatNumber value="${usdrFee }" pattern="#0.0000"></fmt:formatNumber></span>
                                <span>USDR<fmt:message key="msg.pg.userCenter.section"/></span>
                                <span><fmt:message key="msg.pg.userCenter.withdrawFee"/></span>
                            </div>
                        </div>
                    </div>
                </c:if>
                <div class="turntable-import-item">
                    <ul class="turntable-item-title">
                        <li class="beEligibleRraffle active" onclick="switchingMode(this, 0);"><fmt:message key="msg.pg.userCenter.BeEligibleRraffle"/></li>
                        <li class="myPrize" onclick="switchingMode(this, 1);"><fmt:message key="msg.pg.userCenter.MyPrize"/></li>
                        <li class="myRaffleRecord" onclick="switchingMode(this,2);"><fmt:message key="msg.pg.userCenter.MyRaffleRecord"/></li>
                    </ul>
                    <div class="turntable-item-content">
                        <div class="turntable-list turntable-item-raffle" id="turntableItemRaffle">
                            <div class="turntable-padding">
                                <div class="raffle-item-left">
                                    <span><i class="icon-contract"></i><fmt:message key="msg.pg.userCenter.MakeContractTrade"/></span>
                                    <p><fmt:message key="msg.pg.userCenter.luckyDrawTips"/></p>
                                </div>
                                <c:if test="${sessionScope.userinfo != null}">
                                    <div class="raffle-item-right">
                                        <a href="<%=basePath%>trade/trade.do"><fmt:message key="msg.pg.userCenter.goTotrade"/></a>
                                    </div>
                                </c:if>
                                <c:if test="${sessionScope.userinfo == null}">
                                    <div class="raffle-item-right">
                                        <a href="<%=basePath%>login/login.do"><fmt:message key="msg.pg.userCenter.LogOnDraw"/></a>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                        <div class="turntable-list-nobg turntable-item-prize hide" id="turntableTtemPrize">
                            <div class="turntable-prize">
                                <div class="prize-item">
                                    <ul>
                                        <c:forEach items="${drawSettings }" var="prizeList" varStatus="status">
                                            <li>
                                                <div class="content-prize">
                                                    <img src="../common/images/luckDraw/${prizeList.photo}">
                                                    <span class="num-of-prize" id="map${prizeList.prize}">
                                                        X${prizeList.count}
                                                    </span>
                                                    <c:if test="${prizeList.notes != ''}">
                                                        <span class="type-of-prize"><i class="icon-cion"></i>${prizeList.notes}</span>
                                                    </c:if>
                                                    <c:if test="${prizeList.notes == ''}">
                                                        <span class="type-of-prize"><fmt:message key="msg.pg.userCenter.thanksParticipation"/></span>
                                                    </c:if>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                                <div class="filling-in-address">
                                    <span>*<fmt:message key="msg.pg.userCenter.firstPrize"/>
                                        <a href="<%=basePath%>userLottery/toAddAdress.do">
                                            <fmt:message key="msg.pg.userCenter.receivingAddress"/>
                                        </a>
                                    </span>
                                </div>
                            </div>
                        </div>
                    <div class="turntable-list turntable-item-record hide" id="turntableItemRecord">
                        <div class="turntable-record-padding">
                            <c:if test="${sessionScope.userinfo != null}">
                                <div class="record-item">
                                    <ul id="record-item-ul">
                                        <li><fmt:message key="msg.pg.index.recordTime"/></li>
                                        <li><fmt:message key="msg.pg.userCenter.prize"/></li>
                                    </ul>
                                    <c:forEach items="${pm.datas}" var="record" varStatus="status">
                                        <ul class="ul-bg-list">
                                            <li id="recordDrawTime">
                                                <ut:formatDate dateline="${record.time}"></ut:formatDate>
                                            </li>
                                            <c:if test="${record.notes != ''}">
                                                <li id="recordAwardName">${record.notes}</li>
                                            </c:if>
                                            <c:if test="${record.notes == ''}">
                                                <li id="recordAwardName"><fmt:message key="msg.pg.userCenter.thanksParticipation"/></li>
                                            </c:if>
                                        </ul>
                                    </c:forEach>
                                </div>
                            </c:if>
                            <c:if test="${sessionScope.userinfo == null}">
                                <div class="prize-item">
                                    <a href="<%=basePath%>login/login.do"><span class="login-to-query"></span><fmt:message key="msg.pg.userCenter.logQuery"/></a>
                                </div>
                            </c:if>
                            <%--分页--%>
                            <ul class="pagination" style="margin-top: 30px;">
                                <pg:pager url="userLottery/toDrawInfo.do" items="${pm.total}" maxIndexPages="10" maxPageItems="${pm.maxitems}" export="currentPageNumber=pageNumber">
                                    <pg:param name="code" value="${code }" />
                                    <pg:prev> <li class="disabled">
                                        <a href="<%=basePath%>${pageUrl}" mce_href="<%=basePath%>${pageUrl}"><fmt:message key="msg.pg.paging.previous"/></a>	</li>
                                    </pg:prev>
                                    <pg:pages>
                                        <c:choose>
                                            <c:when test="${currentPageNumber eq pageNumber }">
                                                <li class="active"><a href="javascript:void(0)">${pageNumber }</a></li>
                                            </c:when>
                                            <c:otherwise><li>
                                                <a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }">${pageNumber}</a></li>
                                            </c:otherwise>
                                        </c:choose>
                                    </pg:pages>
                                    <pg:next>
                                        <li><a href="<%=basePath%>${pageUrl }" mce_href="<%=basePath%>${pageUrl }"><fmt:message key="msg.pg.paging.next"/></a></li>
                                    </pg:next>
                                </pg:pager>
                            </ul>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="show-msg-box" id="showBox"><div id="showBoxText"></div></div>
    </div>
    <jsp:include page="_footer.jsp"></jsp:include>
</body>
<script type="text/javascript" src="<%=basePath%>common/js/jQueryRotate.2.2.js"></script>
<script type="text/javascript">
    $(function() {
        //bannar
        var mySwiper = new Swiper('.swiper-container', {
            spaceBetween: 30,
            centeredSlides: true,
            autoplay: {
                delay: 5000,
                disableOnInteraction: false,
            },
            loop: true,
            direction : 'vertical',
        });
        // var userName = $('.listUserName').html();
        // if (userName) {
        //     var newUserName = userName.substr(0, 3) + '****' + userName.substr(7);
        //     $('.listUserName').text(newUserName);
        // }
        var isOpen = 0; //全局变量，判断是否已经打开弹出框
        var sessionUserChose = window.localStorage.getItem("userChose");
        if(sessionUserChose) {
            if(sessionUserChose == MSG['eligibleForRaffle']) {
                $("#turntableItemRaffle").show();
                $("#turntableTtemPrize").hide();
                $("#turntableItemRecord").hide();
                $(".beEligibleRraffle").addClass("active").siblings().removeClass("active");
            } else if(sessionUserChose == MSG['myPrize']) {
                $("#turntableTtemPrize").show();
                $("#turntableItemRaffle").hide();
                $("#turntableItemRecord").hide();
                $(".myPrize").addClass("active").siblings().removeClass("active");
            } else if (sessionUserChose == MSG['myPrizeList']) {
                $("#turntableItemRecord").show();
                $("#turntableItemRaffle").hide();
                $("#turntableTtemPrize").hide();
                $(".myRaffleRecord").addClass("active").siblings().removeClass("active");
            }
        }
        if($("#userEmail").val() != '' && $("#userEmail").val() != null) {
            getNewLuckyPrizeNum();
        }
        var error = '${error}';
        if(error != '' && error != null) {
            showMsgBox(1, error);
        }
    });
    var message = {};
    message["zh_CN"] = {
        sixthTips: "感谢参与，下次在努力哦！",
        eligibleForRaffle: "获取抽奖资格",
        myPrize: "我的奖品",
        myPrizeList: "我的抽奖记录",
        thanksTips: "感谢参与",
        first: "一等奖",
        second: "二等奖",
        third: "三等奖",
        fourth: "四等奖",
        fifth: "五等奖",
        prizeText: "奖品是",
        prizeText2: "，请您尽快填写收货地址！",
        prizeText3: "，已发放到账户，请查收！",
    };
    message["zh_HK"] = {
        sixthTips: "感謝參與，下次在努力哦！",
        eligibleForRaffle: "獲取抽獎資格",
        myPrize: "我的獎品",
        myPrizeList: "我的抽獎記錄",
        thanksTips: "感謝參與",
        first: "一等獎",
        second: "二等獎",
        third: "三等獎",
        fourth: "四等獎",
        fifth: "五等獎",
        prizeText: "獎品是",
        prizeText2: "，請您盡快填寫收貨地址！",
        prizeText3: "，已發放到賬戶，請查收！",
    };
    message["en_US"] = {
        sixthTips:"Thanks for your participation, try again next time!",
        eligibleForRaffle: "Be eligible for the raffle",
        myPrize: "My prize",
        myPrizeList: "My raffle record",
        thanksTips: "Thanks for your participation",
        first: "First prize",
        second: "Second class prize",
        third: "Third prize",
        fourth: "Fourth prize",
        fifth: "Five award",
        prizeText: "The prize is ",
        prizeText2: ", please fill in the delivery address as soon as possible",
        prizeText3: ", which has been sent to the account, please check!",
    };
    message["ja_JP"] = {
        sixthTips:"Thanks for your participation, try again next time!",
        eligibleForRaffle: "Be eligible for the raffle",
        myPrize: "My prize",
        myPrizeList: "My raffle record",
        thanksTips: "Thanks for your participation",
        first: "First prize",
        second: "Second class prize",
        third: "Third prize",
        fourth: "Fourth prize",
        fifth: "Five award",
        prizeText: "The prize is ",
        prizeText2: ", please fill in the delivery address as soon as possible",
        prizeText3: ", which has been sent to the account, please check!",
    };
    message["ko_KR"] = {
        sixthTips:"Thanks for your participation, try again next time!",
        eligibleForRaffle: "Be eligible for the raffle",
        myPrize: "My prize",
        myPrizeList: "My raffle record",
        thanksTips: "Thanks for your participation",
        first: "First prize",
        second: "Second class prize",
        third: "Third prize",
        fourth: "Fourth prize",
        fifth: "Five award",
        prizeText: "The prize is ",
        prizeText2: ", please fill in the delivery address as soon as possible",
        prizeText3: ", which has been sent to the account, please check!",
    };
    message["ru_RU"] = {
        sixthTips:"Thanks for your participation, try again next time!",
        eligibleForRaffle: "Be eligible for the raffle",
        myPrize: "My prize",
        myPrizeList: "My raffle record",
        thanksTips: "Thanks for your participation",
        first: "First prize",
        second: "Second class prize",
        third: "Third prize",
        fourth: "Fourth prize",
        fifth: "Five award",
        prizeText: "The prize is ",
        prizeText2: ", please fill in the delivery address as soon as possible",
        prizeText3: ", which has been sent to the account, please check!",
    };
    var MSG = message[locale];
    //切换
    function switchingMode(self, type) {
        $(self).addClass('active').siblings().removeClass("active");
        if (type == 0) {
            $("#turntableItemRaffle").show();
            $("#turntableTtemPrize").hide();
            $("#turntableItemRecord").hide();
            remeberUserChose($(self).html());
        } else if(type == 1) {
            $("#turntableTtemPrize").show();
            $("#turntableItemRaffle").hide();
            $("#turntableItemRecord").hide();
            remeberUserChose($(self).html());
        } else if(type == 2) {
            $("#turntableItemRecord").show();
            $("#turntableItemRaffle").hide();
            $("#turntableTtemPrize").hide();
            remeberUserChose($(self).html());
        }
    }
    function remeberUserChose(userChose) {
        window.localStorage.setItem("userChose", userChose);
    }
    var num = ${count };//剩余次数
    var rotate = false;//标记旋转

    //获取抽奖结果
    function getluckDrawResult() {
        if($("#userEmail").val() == '' && $("#userEmail").val() == null) {
            return;
        }
        if(rotate) return; // 如果在执行就退出
        rotate = true; // 标志为 在执行
        if(num == 0) {
            rotate = false;
            return false;
        }
        $.ajax({
            url: basePath + "userLottery/userLuckDraw.do ",
            type: "POST",
            data: {},
            dataType: "json",
            success: function (data) {
                if (data.code == 0) {
                    // console.log(data.date);
                    roundLuckDraw(data.date.prize, data.date.notes, data.date.isMatter);
                } else {
                    $("#prizeContent").html(data.msg);
                    $(".first-hidden").css("display", "none");
                    $(".dialogs-prize-item").css("text-align", "center");
                    center($("#add-wrap-prize"));
                    checkEvent($("#closeAffirmButton"));
                }
            },
            error: function(data) { }
        })
    }
    // 再次获取抽奖次数和记录
    function getLotteryNnumberAndRecord() {
        $.ajax({
            url: basePath + "userLottery/getDrawInfo.do",
            type: "GET",
            data: {},
            dataType:"json",
            success: function (data) {
                $("#turntable-luckdraw-num").html("X" + data.count);//更新抽奖次数
                var obj = eval('(' + data.drawRecords + ')');
                var recordTime = timestampToTime(obj[0].time);
                if(obj[0].notes == '') {
                    obj[0].notes = MSG['thanksTips'];
                }
                $("#record-item-ul").after(
                    "<ul class='ul-bg-list'><li id='recordDrawTime'>" + recordTime + "</li><li id='recordAwardName'>" + obj[0].notes + "</li></ul>"
                );
            }
        })
    };
    function getNewLuckyPrizeNum() {
        $.ajax({
            url: basePath + "userLottery/getDrawInfo.do",
            type: "GET",
            data: {},
            dataType: "json",
            success: function (data) {
                var obj = eval('(' + data.drawSettings + ')');
                // console.log(obj);
                for (var i = 0; i < obj.length; i++) {
                    if(obj[i].prize == 'first') {
                        $("#mapfirst").html("X" + obj[i].count);
                    }  else if (obj[i].prize == 'second') {
                        $("#mapsecond").html("X" + obj[i].count);
                    } else if (obj[i].prize == 'third') {
                        $("#mapthird").html("X" + obj[i].count);
                    } else if (obj[i].prize == 'fourth') {
                        $("#mapfourth").html("X" + obj[i].count);
                    } else if (obj[i].prize == 'fifth') {
                        $("#mapfifth").html("X" + obj[i].count);
                    } else if (obj[i].prize == 'noprize') {
                        $("#mapnoprize").html("X" + obj[i].count);
                    }
                }

            }
        })
    }
    // 我的奖品-获得各个奖品的次数
    function newNumLuckPrize(mac){
        if (mac == "first") {
            $(".first-hidden").css({"display":"block","text-align":"center"});
            $(".dialogs-prize-item").css("text-align", "left");
        } else if(mac == "second") {
            $(".first-hidden").css({"display":"block","text-align":"center"});
            $(".dialogs-prize-item").css("text-align", "left");
        } else if(mac == "third") {
            $(".first-hidden").css({"display":"block","text-align":"center"});
            $(".dialogs-prize-item").css("text-align", "left");
        } else if(mac == "fourth") {
            $(".first-hidden").css({"display":"block","text-align":"center"});
            $(".dialogs-prize-item").css("text-align", "left");
        } else if(mac == "fifth") {
            $(".first-hidden").css({"display":"block","text-align":"center"});
            $(".dialogs-prize-item").css("text-align", "left");
        } else if(mac == "noprize") {
            $(".first-hidden").css("display", "none");
            $(".dialogs-prize-item").css("text-align", "center");
        }
    }
    /*   prizeText: "獎品是",
     *   prizeText2: "，請您盡快填寫收貨地址！",
     *   prizeText3: "，已發放到賬戶，請查收！",
     *
    */
    function roundLuckDraw(level, notes, isMatter) {
        if (level == "first") {
            if(isMatter == 0) {
                rotateFn(-30, MSG['prizeText'] + notes + MSG['prizeText3'], MSG['first'], level)
            } else {
                rotateFn(-30, MSG['prizeText'] + notes + MSG['prizeText2'], MSG['first'], level)
            }
        } else if(level == "second") {
            if(isMatter == 0) {
                rotateFn(-330, MSG['prizeText'] + notes + MSG['prizeText3'], MSG['second'], level)
            } else {
                rotateFn(-330, MSG['prizeText'] + notes + MSG['prizeText2'], MSG['second'], level)
            }
        } else if(level == "third") {
            if(isMatter == 0) {
                rotateFn(-150, MSG['prizeText'] + notes + MSG['prizeText3'], MSG['third'], level)
            } else {
                rotateFn(-150, MSG['prizeText'] + notes + MSG['prizeText2'], MSG['third'], level)
            }
        } else if(level == "fourth") {
            if(isMatter == 0) {
                rotateFn(-270, MSG['prizeText'] + notes + MSG['prizeText3'], MSG['fourth'], level)
            } else {
                rotateFn(-270, MSG['prizeText'] + notes + MSG['prizeText2'], MSG['fourth'], level)
            }
        } else if(level == "fifth") {
            if(isMatter == 0) {
                rotateFn(-210, MSG['prizeText'] + notes + MSG['prizeText3'], MSG['fifth'], level)
            } else {
                rotateFn(-210, MSG['prizeText'] + notes + MSG['prizeText2'], MSG['fifth'], level)
            }
        } else if(level == "noprize") {
            rotateFn(-90, MSG['sixthTips'], MSG['thanksTips'], level)
        }
    }

    var rotateFn = function(angles, txt, classify, level) {
         rotate = true;//正在抽奖
        $('#turntableLuckdrawDisc').stopRotate();
        $('#turntableLuckdrawDisc').rotate({
            angle: 0,
            animateTo: angles + 2160,
            duration: 5000,
            callback: function() {
                rotate = false;//标志为 执行完毕
                $("#prizeLever").html(classify);
                $("#prizeContent").html(txt);
                newNumLuckPrize(level);
                center($("#add-wrap-prize"));
                checkEvent($("#closeAffirmButton"));
                getLotteryNnumberAndRecord();
                getNewLuckyPrizeNum();

            }
        });
    };
    function center(obj) {
        var screenWidth = $(window).width(), screenHeigth = $(window).height();
        var scollTop = $(document).scrollTop();
        var objLeft = (screenWidth - obj.width()) / 2;
        var objTop = (screenHeigth - obj.height()) / 2 + scollTop;
        obj.css({
            left:objLeft + "px",
            top:objTop + "px"
        });
        obj.fadeIn(500);
        $("#tb-overlay-prize").show();
        isOpen = 1;
        $(window).resize(function() {
            if (isOpen == 1) {
                screenWidth = $(window).width();
                screenHeigth = $(window).height();
                var scollTop = $(document).scrollTop();
                objLeft = (screenWidth - obj.width()) / 2;
                var objTop = (screenHeigth - obj.height()) / 2 + scollTop;
                obj.css({
                    left:objLeft + "px",
                    top:objTop + "px"
                });
                obj.fadeIn(500);
                $("#tb-overlay-prize").show();
            }
        });
        $(window).scroll(function() {
            if (isOpen == 1) {
                screenWidth = $(window).width();
                screenHeigth = $(window).height();
                var scollTop = $(document).scrollTop();
                objLeft = (screenWidth - obj.width()) / 2;
                var objTop = (screenHeigth - obj.height()) / 2 + scollTop;
                obj.css({
                    left:objLeft + "px",
                    top:objTop + "px"
                });
                obj.fadeIn(500);
                $("#tb-overlay-prize").show();
            }
        });
    }
    function checkEvent(obj2) {
        obj2.click(function() {
            closed($("#tb-overlay-prize"), $("#add-wrap-prize"));
        });
    }
    function closed(obj1, obj2) {
        obj1.fadeOut(500);
        obj2.fadeOut(500);
        isOpen = 0;
    }
    function timestampToTime(timestamp) {
        var date = new Date();
        var Y = date.getFullYear() + '-';
        var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        var D = (date.getDate() < 10 ? '0'+date.getDate() : date.getDate()) + ' ';
        var h = (date.getHours() < 10 ? '0'+date.getHours() : date.getHours()) + ':';
        var m = (date.getMinutes() < 10 ? '0'+date.getMinutes() : date.getMinutes()) + ':';
        var s = (date.getSeconds() < 10 ? '0'+date.getSeconds() : date.getSeconds());

        strDate = Y+M+D+h+m+s;
        return strDate;
    }
    function showMsgBox(code, text) {
        if(text.length > 0){
            if (code == 0) {
                $("#showBoxText").removeClass('warningNew');
                $("#showBoxText").addClass('successNew');
            } else {
                $("#showBoxText").addClass('warningNew');
                $("#showBoxText").removeClass('successNew');
            }

            $("#showBoxText").html(text);

            var time = 3000;
            if (code == 'error') {
                time = 5000;
            }

            $("#showBox").css("display", "block");

            setTimeout(function() {
                $("#showBox").css("display", "none");
            }, time + 50);
        }
    }
</script>
</html>
