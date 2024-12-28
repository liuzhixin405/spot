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
    <title><fmt:message key="msg.pg.userCenter.fillingInAaddress"/></title>
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/style.css?<%=rjs%>">
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/iconfont.css?<%=rjs%>" />
    <jsp:include page="_common.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="<%=basePath%>common/css/luckDraw.css?<%=rjs%>" />
    <script type="text/javascript" src="<%=basePath%>common/js/jquery.1.7.1.min.js"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/city.js?<%=rjs%>"></script>
    <script type="text/javascript" src="<%=basePath%>common/js/base.js?<%=rjs%>"></script>

</head>

<body>
<jsp:include page="_top.jsp"></jsp:include>
<div class="address-container address-center-cont">
    <div class="address-content">
        <div class="activity-banner">
            <c:if test="${locale eq 'zh_CN'}">
                <img src="../common/images/luckDraw/the-seventh-anniversary-banner-zh.png">
            </c:if>
            <c:if test="${locale ne 'zh_CN'}">
                <img src="../common/images/luckDraw/the-seventh-anniversary-banner-en.png">
            </c:if>
        </div>
        <div class="address-filling-part">
            <div class="address-title">
                <h2><fmt:message key="msg.pg.userCenter.AddAdressTitle"/></h2>
                <h3><fmt:message key="msg.pg.userCenter.AddAdressTitle2"/></h3>
            </div>
            <div class="address-detile-content">
                <p><fmt:message key="msg.pg.userCenter.AddAdressTips1"/></p>
                <p><fmt:message key="msg.pg.userCenter.AddAdressTips2"/></p>
                <p><fmt:message key="msg.pg.userCenter.AddAdressTips3"/></p>
                <p><fmt:message key="msg.pg.userCenter.AddAdressTips4"/></p>
                <p><fmt:message key="msg.pg.userCenter.AddAdressTips5"/></p>
<%--                <img alt="app" style="width: 100%;height: 100%;" src="<%=basePath%>common/images/qrcode/<%=serverName%>/app.png?v=1">--%>
            </div>
            <div class="address-input-part">
                <form>
                    <div class="address-input-item">
                        <label for="phoneNumber"><fmt:message key="msg.pg.userCenter.YourPhoneNumber"/></label>
                        <input type="text" autocomplete="off" id="phoneNumber" name="phone" placeholder="<fmt:message key='msg.pg.userCenter.AddPhoneNumber'/>" value="${phone}" onblur="checkPhoneCallNum();">
                        <span class="phone-error" id="phone-error"></span>
                    </div>
                    <div class="address-input-item">
                        <label for="addressee"><fmt:message key="msg.pg.userCenter.addressee"/></label>
                        <input type="text" autocomplete="off" id="addressee" name="addressee" placeholder="<fmt:message key='msg.pg.userCenter.AddAddresses'/>" value="${addressee}" onblur="checkAddresseeIsNull()">
                        <span class="addressee-error" id="addressee-error"></span>
                    </div>
                    <div class="address-input-item">
                        <label for="addressee"><fmt:message key="msg.pg.userCenter.shippingAddress"/></label>
                        <div class="select" id="address-select" style="text-align: center">
                            <input type="hidden" id="myCountry" value="${(empty countryCode) ? 'CN' : countryCode }" />
                            <input type="hidden" id="myProvince" value="${proviceCode}" />
                            <input type="hidden" id="myCity" value="${cityCode}" />
                            <input type="hidden" id="myArea" value="${areaCode}" />
                            <input type="hidden" name="currCountryName" value="${currCountryName}" />
                            <input type="hidden" name="curryAreaName" value="${curryAreaName}" />
                            <select id="countrySelect" name="countryCode" onchange="loadProvice();" style="width:148px;height: 40px;margin-bottom: 10px;border-radius: 2px;border: 1px solid #CCCCCC;text-indent: 10px;" value="${currCountryName}">
                                <c:forEach items="${listCountry}" var="cou">
                                    <c:if test="${locale != 'zh_CN' }">
                                        <c:choose>
                                            <c:when test="${countryCode eq cou.code}">
                                                <option value="${cou.code}" selected='selected'>${cou.en_name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${cou.code}">${cou.en_name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    <c:if test="${locale == 'zh_CN' }">
                                        <c:choose>
                                            <c:when test="${countryCode eq cou.code}">
                                                <option value="${cou.code}" selected='selected'>${cou.zh_name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${cou.code}">${cou.zh_name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </c:forEach>
                            </select>
                            <c:if test="${countryCode == 'CN' }">
                                <select id="proviceSelect" name="proviceCode" onchange="loadCity();" style="width: 148px;height: 40px;margin-bottom: 10px;border-radius: 2px;border: 1px solid #CCCCCC;text-indent: 10px;margin-left: 1px;margin-right: 1px;" value="${currProviceName}">
                                </select>
                                <select id="citySelect" name="cityCode" onchange="loadArea();" style="width: 148px;height: 40px;margin-bottom: 10px;border-radius: 2px;border: 1px solid #CCCCCC;text-indent: 10px;margin-left: 1px;margin-right: 1px;" value="${currCityName}">
                                </select>
                                <select id="areaSelect" name="areaCode" style="width: 148px;height: 40px;margin-bottom: 10px;border-radius: 2px;border: 1px solid #cccccc;text-indent: 10px;" value="${currAreaName }">
                                </select>
                            </c:if>
                        </div>
                        <div class="address-input-select">
                            <input type="text" autocomplete="off" id="address-detial" placeholder="<fmt:message key='msg.pg.userCenter.AddAdressDetail'/>" name="address" value="${address}" onblur="checkDetailAddressIsNull();">
                            <span class="address-detial-error" id="address-detial-error"></span>
                        </div>
                    </div>
                    <div class="address-input-item">
                        <input type="checkbox" checked="checked" class="checked"
                               name="checkbox" id="agreeToUseAddress" onclick="isChecked();"/>
                        <span class="checkbox-text"><fmt:message key="msg.pg.userCenter.addressServices"/></span>
                        <br/>
                        <span class="checkedError" id="checkedError"></span>
                    </div>
                    <div class="address-input-item">
                        <button type="button" class="address-input-botton" id="register_button" onclick="return submitAddress();"><fmt:message key="msg.pg.userCenter.SubmitButton"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <div class="show-msg-box" id="showBox"><div id="showBoxText"></div></div>
</div>

<jsp:include page="_footer.jsp"></jsp:include>
</body>

<script type="text/javascript">
    $(function() {
        loadProvice();
    });
    var message = {};
    message["zh_CN"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "您需要先同意服务协议！",
        addresseeNull: "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    message["zh_HK"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "您需要先同意服務協議！",
        addresseeNull : "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    message["en_US"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "You need to agree with the Service Agreement first !",
        addresseeNull : "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    message["ja_JP"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "先にサービス同意書を同意してから！",
        addresseeNull : "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    message["ko_KR"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "먼저 서비스 계약에 동의해야합니다!",
        addresseeNull : "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    message["ru_RU"] = {
        phoneNull : "手机号不能为空！",
        phoneError : "手机号格式错误！",
        agreementService : "Вы должны сначала согласиться с соглашением об обслуживании !",
        addresseeNull : "收件人不能为空！",
        detailAddressNull:"详细地址不能为空！",
    };
    var MSG = message[locale];

    var name = "${currCountryName }";
    var code = "${countryCode }";
    var a = false;
    function loadCountry() {
        $.get(basePath + "city/loadCountry.do", function(result){
            result = eval(result);
            for(var i = 0; i < result.length-1; i++) {
                if(code != result[i].code) {
                    a = true;
                }
                if(code == result[i].code) {
                    return false;
                }
            }
            if (a) {
                $("#countrySelect").prepend("<option value="+ code +" >" +name+ "</option>");
                loadProvice();
            }
        });
    }

    loadCountry();
    var tel_bol = false;// 手机号
    var addressee_bol = false;// 地址
    var detailAddress_bol = false;// 详细地址
    // 验证手机号正确性
    function checkPhoneCallNum() {
        var phoneCallNum = $("#phoneNumber").val();
        var phone = /^1[0-9]{10}$/;
        if(!phone.test(phoneCallNum)) {
            $("#phone-error").html(MSG['phoneError']).css("color", "#E04444");
            tel_bol = false;
            return false;
        } else {
            $("#phone-error").html("").css("color", "green");
            tel_bol = true;
            return true;
        }
    }
    // 验证收件人不为空
    function checkAddresseeIsNull(){
        var addressee = $("#addressee").val();
        if(addressee == "" || addressee == null) {
            $("#addressee-error").html(MSG['addresseeNull']).css("color", "#E04444");
            addressee_bol = false;
            return false;
        } else {
            $("#addressee-error").html("").css("color", "green");
            addressee_bol = true;
            return true;
        }
    }
    // 验证详细地址不为空
    function checkDetailAddressIsNull(){
        var addressDetial = $("#address-detial").val();
        if(addressDetial == "" || addressDetial == null) {
            $("#address-detial-error").html(MSG['detailAddressNull']).css("color", "#E04444");
            detailAddress_bol = false;
            return false;
        } else {
            $("#address-detial-error").html("").css("color", "green");
            detailAddress_bol = true;
            return true;
        }
    }
    //  同意并勾选采集地址按钮
    function isChecked(){
        if($("#agreeToUseAddress").attr('checked') == 'checked') {
            $("#checkedError").html('');
        }
    };
    // 提交地址
    function submitAddress() {
        var checked = $("#agreeToUseAddress").attr("checked");
        if(!checked) {
            $("#checkedError").html(MSG['agreementService']).css("color", "#E04444");
        } else if(!tel_bol) {
            $("#phone-error").html(MSG['phoneError']).css("color", "#E04444");
        } else if(!addressee_bol) {
            $("#addressee-error").html(MSG['addresseeNull']).css("color", "#E04444");
        } else if(!detailAddress_bol) {
            $("#address-detial-error").html(MSG['detailAddressNull']).css("color", "#E04444");
        }
        if(tel_bol && addressee_bol && detailAddress_bol && checked) {
            presentAddArdess();
        }
    }
    function presentAddArdess() {
        var phoneCallNum = $("#phoneNumber").val();
        var addressee = $("#addressee").val();
        var addressDetial = $("#address-detial").val();
        var countryCode = $("#countrySelect option:selected").val();
        var proviceCode = $("#proviceSelect option:selected").val();
        var cityCode = $("#citySelect option:selected").val();
        var areaCode = $("#areaSelect option:selected").val();
        $.ajax({
            url: basePath + "userLottery/addArdess.do",
            type: "POST",
            data: {
                phone: phoneCallNum,
                address: addressDetial,
                addressee: addressee,
                countryCode: countryCode,
                proviceCode: proviceCode,
                cityCode: cityCode,
                areaCode: areaCode,
            },
            success: function(data) {
                var obj = eval('(' + data + ')');
                showMsgBox(obj.code, obj.msg);
            }

        })
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
