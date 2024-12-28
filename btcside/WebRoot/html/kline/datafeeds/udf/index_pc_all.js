$(function () {
    var symbol = Symbol;
    var lang = "zh";

    // btc2ckusd    btc2ckusd   eos2eth
    var mydata = {
        symbol: symbol || '',
        interval: '240',
        localStor: {}
    }

    if (localStorage['tradeinterval']) {
        mydata.localStor = JSON.parse(localStorage['tradeinterval']);
        mydata.interval = JSON.parse(localStorage['tradeinterval']).symbol === mydata.symbol ? JSON.parse(localStorage['tradeinterval']).interval : '240';
    }

    TradingView.onready(function () {
        var _datafeed = new Datafeeds.UDFCompatibleDatafeed("https://www.bfx.nu/futuresApi/marketKline.do", 5000);
        var widget = window.tvWidget = new TradingView.widget({
			// debug: true,
			// fullscreen: true,
            width: '100%',
            height: '100%',
            symbol: mydata.symbol,
            interval: mydata.interval,
            container_id: "tv_chart_container",
            timezone: "Asia/Shanghai",
            //	BEWARE: no trailing slash is expected in feed URL
            datafeed: _datafeed,
            library_path: "charting_library/",
            locale: lang,
            //	Regression Trend-related functionality is not implemented yet, so it's hidden for a while
            drawings_access: {
                type: 'black',
                tools: [{
                        name: "Regression Trend"
                    }]
            },
            disabled_features: [
                "use_localstorage_for_settings",
                "header_resolutions",
                "header_symbol_search",
                "header_chart_type",
                "header_compare",
                "header_undo_redo",
                "header_screenshot",
				"header_indicators",
                "header_saveload",
                "study_templates", // 模板
                "timeframes_toolbar",
                "pane_context_menu",
                "volume_force_overlay",
				"header_fullscreen_button"
            ],
            enabled_features: [
                "adaptive_logo",
                "show_dom_first_time",
                "side_toolbar_in_fullscreen_mode",
                "hide_left_toolbar_by_default",
                "hide_last_na_study_output", // 隐藏成交量n/a
            ],
            studies_overrides: {
                "volume.volume.color.0": "rgba(230, 72, 72, .7)",
                "volume.volume.color.1": "rgba(74, 196, 101, .7)"
            },
            overrides: {
                // 处理指标 ＋ — 效果
                "paneProperties.legendProperties.showLegend": false,
                "paneProperties.legendProperties.showStudyArguments": false,
                "paneProperties.legendProperties.showStudyTitles": false,
                "paneProperties.legendProperties.showStudyValues": false,
                "paneProperties.legendProperties.showSeriesTitle": false,
                "paneProperties.legendProperties.showSeriesOHLC": false,
                //    蜡烛样式
                "mainSeriesProperties.candleStyle.upColor": "#4AC465",
                "mainSeriesProperties.candleStyle.downColor": "#E64848",
                "mainSeriesProperties.candleStyle.borderUpColor": "#4AC465",
                "mainSeriesProperties.candleStyle.borderDownColor": "#E64848",
                "mainSeriesProperties.candleStyle.wickUpColor": "#4AC465",
                "mainSeriesProperties.candleStyle.wickDownColor": "#E64848",
                // 背景色
                "paneProperties.background": "#FFFFFF",
                // 竖直网格线颜色
                "paneProperties.vertGridProperties.color": "rgba(148, 171, 192, 0.1)",
                // 水平网格线颜色
                "paneProperties.horzGridProperties.color": "rgba(148, 171, 192, 0.1)",
                // 十指光标颜色
                "paneProperties.crossHairProperties.color": "#4A5F78",
                // 坐标轴线颜色
                "scalesProperties.lineColor": "#FFFFFF",
                // 坐标轴文本颜色
                "scalesProperties.textColor": "#94ABC0",
                // 分时图
                "mainSeriesProperties.areaStyle.color1": "rgba(0, 153, 195, .1)",
                "mainSeriesProperties.areaStyle.color2": "rgba(0, 153, 195, 0)",
                "mainSeriesProperties.areaStyle.linecolor": "rgba(0, 153, 195, 1)",
                // 成交量高度设置  large, medium, small, tiny
                "volumePaneSize": "small",
                // 调整k线顶部的边距
                "paneProperties.topMargin": 5
            },
            custom_css_url: 'bundles/newk.css?v=5', // 样式表
        });

        widget.onChartReady(function () {
            // 学习线
            widget.chart().createStudy('Moving Average', false, false, [5], null, {'plot.color': '#A699D9'});
            widget.chart().createStudy('Moving Average', false, false, [10], null, {'plot.color': '#6A9ACB'});
            widget.chart().createStudy('Moving Average', false, false, [30], null, {'plot.color': '#B27BC2'});
            widget.chart().createStudy('Moving Average', false, false, [60], null, {'plot.color': '#FFBF53'});

            //创建按钮 分辨率
            var interval_btn = [
                {title: '5min', interval: '5', type: 1},
                {title: '30min', interval: '30', type: 1},
                {title: '1hour', interval: '60', type: 1},
                {title: '4hour', interval: '240', type: 1},
                {title: '日线', interval: '1D', type: 1}
            ];
            if (lang === 'en') {
                interval_btn[4].title = 'Day line';
            } else if (lang === 'zh_TW') {
                interval_btn[4].title = '日線';
            }

            var flag = true;
            var currentDom = null;
            interval_btn.forEach(function (v, i) {
                var ss = v.title;
                var _dom = widget.createButton()
                        .on('click', function (e) {
                            widget.chart().setResolution(v.interval);
                            widget.chart().setChartType(v.type);
                            tab(this, v);
                        }).append($('<span>' + v.title + '</span>'));

                if (v.type === mydata.localStor.type && v.interval === mydata.interval) {
                    $(_dom).parent().parent().addClass('active');
                    flag = false;
                }
                if (v.interval === '240') {
                    currentDom = _dom;
                }
            });
            if (flag) {
                $(currentDom).parent().parent().addClass('active');
            }
			
            // 分辨率tab切换 
            function tab(_this, v) {
                $(_this).parent().parent().addClass('active')
                $(_this).parent().parent().siblings().removeClass('active');
                var _obj = {
                    symbol: mydata.symbol,
                    interval: v.interval,
                    type: v.type
                };
                localStorage.setItem('tradeinterval', JSON.stringify(_obj));
            }
        })
    });
})
