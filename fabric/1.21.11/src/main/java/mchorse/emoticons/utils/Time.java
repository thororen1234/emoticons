package mchorse.emoticons.utils;

public class Time {
	public static int toTicks(int n) {
		return (int) Math.floor((float) n / 30.0f * 20.0f);
	}
}
