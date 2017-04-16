/*
 * 「Apache License, Version 2.0」のライセンスが適用されます。
 * 当ファイルを使用する場合はこのライセンスに従ってください。
 *
 * ライセンスの全文は「http://www.apache.org/licenses/LICENSE-2.0」にあります。
 *
 * 適用法または書面による同意が必要な場合を除き、
 * ライセンスに基づいて配布されるソフトウェアは、
 * 明示的または黙示的にいかなる種類の保証や条件もなく「現状有姿」で配布されています。
 *
 * ライセンスに基づいて許可および制限を規定する特定の言語については上記ライセンスを参照してください。
 */
package jp.co.golorp.emarf.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

/**
 * 日付ユーティリティ
 *
 * @author oukuf@golorp
 */
public class DateUtil extends DateUtils {

	/** セッションID（セッション日時保管用） */
	private static final ThreadLocal<String> SESSION_ID = new ThreadLocal<String>();

	/**
	 * @param sessionId
	 *            現在スレッドのセッションID
	 */
	public static final void setSessionId(final String sessionId) {
		SESSION_ID.set(sessionId);
	}

	/** サーバー日時 */
	private static String serverDateValue;

	/** セッション日時 */
	private static final Map<String, Calendar> SESSION_DATE = new HashMap<String, Calendar>();

	/**
	 * サーバ日付を設定
	 *
	 * @param datetime
	 *            日時文字列
	 * @return 成否
	 */
	public static final boolean setServerDate(final String datetime) {

		try {

			if (StringUtil.isBlank(datetime)) {
				serverDateValue = null;
			} else {
				serverDateValue = datetime;
			}

			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * セッションID毎にセッション日時を設定
	 *
	 * @param datetime
	 *            日時文字列
	 * @return 成否
	 */
	public static final boolean setSessionDate(final String datetime) {
		try {
			if (StringUtil.isBlank(datetime)) {
				SESSION_DATE.remove(SESSION_ID.get());
			} else {
				Calendar sessionDate = Calendar.getInstance();
				sessionDate.setTime(ParseUtil.toDate(datetime));
				SESSION_DATE.put(SESSION_ID.get(), sessionDate);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @return セッション日時。なければサーバー日時。なければシステム日時
	 */
	public static final Date getDate() {

		// セッション日時
		Calendar sessionDate = SESSION_DATE.get(SESSION_ID.get());
		if (sessionDate != null) {
			return sessionDate.getTime();
		}

		// サーバー日時
		if (serverDateValue != null) {
			Calendar serverDate = Calendar.getInstance();
			serverDate.setTime(ParseUtil.toDate(serverDateValue));
			return serverDate.getTime();
		}

		// システム日時
		return Calendar.getInstance().getTime();
	}

	/**
	 * @return 日付文字列
	 */
	public static final String getYmd() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy/MM/dd");
		return sdf.format(getDate());
	}

	/**
	 * @return 日時文字列
	 */
	public static final String getYmdHmsS() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy/MM/dd HH:mm:ss.SSS");
		return sdf.format(getDate());
	}

	/**
	 * @return 日時文字列
	 */
	public static final String getYmdHms() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy/MM/dd HH:mm:ss");
		return sdf.format(getDate());
	}

	/**
	 * @return 4桁の年文字列
	 */
	public static final String getYYYY() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy");
		return sdf.format(getDate());
	}

	/**
	 * @return 2桁の月文字列
	 */
	public static final String getMM() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("MM");
		return sdf.format(getDate());
	}

	/**
	 * @return 1桁の月数値
	 */
	public static final int getM() {
		return Integer.parseInt(getMM());
	}

	/**
	 * @return 2桁の日文字列
	 */
	public static final String getDD() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("dd");
		return sdf.format(getDate());
	}

	/**
	 * @return 1桁の日数値
	 */
	public static final int getD() {
		return Integer.parseInt(getDD());
	}

	/**
	 * @return 2桁の時文字列
	 */
	public static final String getHH() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("HH");
		return sdf.format(getDate());
	}

	/**
	 * @return 1桁の時数値
	 */
	public static final int getH() {
		return Integer.parseInt(getHH());
	}

	/**
	 * @return 2桁の分文字列
	 */
	public static final String getNN() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("mm");
		return sdf.format(getDate());
	}

	/**
	 * @return 1桁の分数値
	 */
	public static final int getN() {
		return Integer.parseInt(getNN());
	}

	/**
	 * @param y
	 *            年
	 * @param m
	 *            月
	 * @return 月末日
	 */
	public static int getEnd(final int y, final int m) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, y);
		now.set(Calendar.MONTH, m);
		now.set(Calendar.DATE, 0);
		int end = now.get(Calendar.DATE);
		return end;
	}

	/**
	 * @param y
	 *            年
	 * @param m
	 *            月
	 * @param d
	 *            日
	 * @return 1:日, 2:月, 3:火, 4:水, 5:木, 6:金, 7:土
	 */
	public static int getDayOfWeek(final int y, final int m, final int d) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, y);
		now.set(Calendar.MONTH, m - 1);
		now.set(Calendar.DATE, d);
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	/**
	 * @return 1:日, 2:月, 3:火, 4:水, 5:木, 6:金, 7:土
	 */
	public static int getDayOfWeek() {
		Calendar now = Calendar.getInstance();
		now.setTime(DateUtil.getDate());
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

}
