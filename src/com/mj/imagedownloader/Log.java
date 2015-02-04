package com.mj.imagedownloader;

public class Log {
	public static void e(String tag, String logMsg) {
		System.err.println(tag + ":" + logMsg);
	}
	
	public static void d(String tag, String logMsg) {
		System.out.println(tag + ":" + logMsg);
	}
	
	public static void i(String tag, String logMsg) {
		System.out.println(tag + ":" + logMsg);
	}
}
