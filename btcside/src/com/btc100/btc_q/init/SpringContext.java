package com.btc100.btc_q.init;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext cxt) throws BeansException {
		setContext(cxt);
	}

	public static final <T> T getBean(Class<T> clazz) {
		return context.getBean(clazz);
	}
	
	public static final void setContext(ApplicationContext cxt) {
		context = cxt;
	}
	
	public static final String getText(String key, Locale locale) {
		return context.getMessage(key, null, locale);
	}
}
