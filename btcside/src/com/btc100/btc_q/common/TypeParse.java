package com.btc100.btc_q.common;


public class TypeParse {

	/**
	 * 判断对象是否为Int32类型的数字
	 * 
	 * @param Expression
	 */
	public static Boolean IsInt(Object Expression) {
		if (Expression != null) {
			String str = Expression.toString();
			if (str.length() > 0 && str.length() <= 11 && Regex.IsMatch(str, "^[-]?[0-9]*[.]?[0-9]*$")) {
				if ((str.length() < 10) || (str.length() == 10 && str.startsWith("1") || (str.length() == 11 && str.startsWith("-") && str.startsWith("1", 1)))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断对象是否为Int64类型的数字
	 * 
	 * @param Expression
	 */
	public static Boolean IsNumeric(Object Expression) {
		if (Expression != null) {
			String str = Expression.toString();
			if (str.length() > 0 && str.length() <= 11
					&& Regex.IsMatch(str, "^[-]?[0-9]*[.]?[0-9]*$")) {
				if ((str.length() < 10)
						|| (str.length() == 10 && str.startsWith("1"))
						|| (str.length() == 11 && str.startsWith("-") && str
								.startsWith("1", 1))) {
					return true;
				}
			}
		}
		return false;
	}

	public static Boolean IsDouble(Object Expression) {
		if (Expression != null) {
			return Regex.IsMatch(Expression.toString(),
					"^([0-9])[0-9]*(\\.\\w*)?$");
		}
		return false;
	}

	/**
	 * 将对象转换为Int32类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的int类型结果
	 */
	public static int StrToInt(Object Expression, int defValue) {
		if (Expression != null) {
			String str = Expression.toString();
			// int MaxValue: 2147483647（10位）
			// int MinValue:-2147483648（11位）
			if (str.length() > 0 && str.length() <= 11
					&& Regex.IsMatch(str, "^[-]?[0-9]*$")) {
				if ((str.length() < 10)
						|| (str.length() == 10 && str.startsWith("1"))
						|| (str.length() == 11 && str.startsWith("-") && str
								.startsWith("1", 1))) {
					try {
						return Integer.valueOf(str);
					} catch (Exception ex) {
						return defValue;
					}
				}
			}
		}
		return defValue;
	}

	/**
	 * 将对象转换为Long类型
	 * 
	 * @param Expression
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的long类型结果
	 */
	public static long StrToLong(Object Expression, long defValue) {
		if (Expression != null) {
			String str = Expression.toString();
			// long MaxValue: 9223372036854775807（19位）
			// long MinValue:-9223372036854775808（20位）
			if (str.length() > 0 && str.length() <= 20
					&& Regex.IsMatch(str, "^[-]?[0-9]*$")) {
				if ((str.length() < 19)
						|| (str.length() == 19 && str.startsWith("9"))
						|| (str.length() == 20 && str.startsWith("-") && str
								.startsWith("9", 1))) {
					try {
						// return Convert.ToInt64(str);
						return Long.valueOf(str);
					} catch (Exception ex) {
						return defValue;
					}
				}
			}
		}
		return defValue;
	}

	/**
	 * String型转换为float型
	 * 
	 * @param strValue
	 *            要转换的字符串
	 * @param defValue
	 *            缺省值
	 * @return 转换后的float类型结果
	 */
	public static float StrToFloat(Object strValue, float defValue) {
		if ((strValue == null) || (strValue.toString().length() > 10)) {
			return defValue;
		}

		float intValue = defValue;
		if (strValue != null) {
			Boolean IsFloat = Regex.IsMatch(strValue.toString(),
					"^([-]|[0-9])[0-9]*(\\.\\w*)?$");
			if (IsFloat) {
				intValue = Float.valueOf(strValue.toString()).floatValue();
			}
		}
		return intValue;
	}

	public static Double StrToDouble(Object strValue, Double defValue) {
		if ((strValue == null)) {
			return defValue;
		}

		if (strValue.toString().indexOf('.') != -1) {
			if (strValue.toString().substring(0,
					strValue.toString().indexOf(".")).length() > 10) {
				return defValue;
			}
		}

		Double decValue = defValue;
		if (strValue != null) {
			Boolean IsDouble = Regex.IsMatch(strValue.toString(),
					"^([-]|[0-9])[0-9]*(\\.\\w*)?$");
			if (IsDouble) {
				// decValue = Convert.ToDouble(strValue);
				decValue = Double.valueOf(strValue.toString()).doubleValue();
			}
		}
		return decValue;
	}

	/**
	 * 判断给定的字符串数组(strNumber)中的数据是不是都为数值型
	 * 
	 * @param strNumber
	 *            要确认的字符串数组
	 * @return 是则返加true 不是则返回 false
	 */
	public static Boolean IsNumericArray(String[] strNumber) {
		if (strNumber == null) {
			return false;
		}
		if (strNumber.length < 1) {
			return false;
		}
		for (String id : strNumber) {
			if (!IsNumeric(id)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断给定的字符串数组(strNumber)中的数据是不是都为数值型
	 * 
	 * @param strNumber
	 *            要确认的字符串数组
	 * @return 是则返加true 不是则返回 false
	 */
	public static Boolean IsIntArray(String[] strNumber) {
		if (strNumber == null) {
			return false;
		}
		if (strNumber.length < 1) {
			return false;
		}
		for (String id : strNumber) {
			if (!IsInt(id)) {
				return false;
			}
		}
		return true;
	}
}
