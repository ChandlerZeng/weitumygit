package com.libtop.weitu.utils;

import android.util.TypedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

	/**
	 * 判断两个string是否相等
	 */
	public static boolean checkEquels(Object strObj0, Object strObj1) {
		String str0 = strObj0 + "";
		String str1 = strObj1 + "";
		if (str0.equals(str1)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 同时�?��多个参数是否为空
	 * @param strArray
	 * @return
	 */
	public static boolean isNull(Object... strArray){
		for(Object obj:strArray){
			if (!"".equals(obj + "")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * �?��是否为空
	 */
	public static boolean isNull(Object strObj) {
		String str = strObj + "";
		if (!"".equals(str) && !"null".equals(str)) {
			return false;
		}
		return true;
	}
	
	public static boolean isNullTxt(Object strObj) {
		String str = strObj + "";
		if (!"null".equals(str)) {
			return false;
		}
		return true;
	}
	
	/**
	 * �?��邮箱
	 * @param strObj
	 * @return
	 */
	public static boolean checkEmail(Object strObj){
		String str = strObj + "";
		String match = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern pattern = Pattern.compile(match);
		Matcher matcher = pattern.matcher(str);
		if(matcher.matches()){
			return true;
		}
		return false;
	}

	/**
	 * �?��是否为电话号�?
	 */
	public static boolean checkNumber(Object phoneNumber) {
		boolean isValid = false;
		String expression = "^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{5})$";
		String expression2 = "^\\(?(\\d{3})\\)?[- ]?(\\d{4})[- ]?(\\d{4})$";
		CharSequence inputStr = phoneNumber + "";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		Pattern pattern2 = Pattern.compile(expression2);
		Matcher matcher2 = pattern2.matcher(inputStr);
		if (matcher.matches() || matcher2.matches()) {
			isValid = true;
		}
		return isValid;
	}


	/**
	 *判断是否是数字
	 */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * �?��str的长度是否达到要�?
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean checkLength(Object strObj,int length){
		String str = strObj + "";
		if(str.length() >= length){
			return true;
		}
		return false;
	}
	
	/**
	 * 检查字符串的长度
	 * @param strObj
	 * @param length
	 * @return
	 */
	public static boolean checkLengthEq(Object strObj,int length){
		String str = strObj + "";
		if(str.length() == length){
			return true;
		}
		return false;
	}
	
	public static boolean checkLength(Object strObj,int start,int end){
		String str = strObj + "";
		if(str.length() >= start && str.length() <= end){
			return true;
		}
		return false;
	}
	
	/**
	 * �?��请求返回是否正确
	 */
	public static boolean checkStatusOk(String status){
		if("1".equals(status)){
			return true;
		}
		return false;
	}
	
	public static boolean checkStatusOk(int status){
		if(2000000 == status){
			return true;
		}
		return false;
	}
	
	/**
	 * �?��string是否为o
	 * @param value
	 * @return
	 */
	public static boolean checkZero(String value){
		int valueInt = StringUtil.toInt(value);
		if(valueInt == 0){
			return true;
		}
		return false;
	}
}
