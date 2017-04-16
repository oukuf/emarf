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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.exception.SystemError;

/**
 * 型変換ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class ParseUtil {

	/***/
	private static final Logger LOG = LoggerFactory.getLogger(ParseUtil.class);

	/**
	 * コンストラクタ
	 */
	private ParseUtil() {
	}

	/**
	 * @param o
	 *            Object
	 * @return ByteっぽいモノからByteに変換
	 */
	public static Byte toByte(final Object o) {
		if (o == null) {
			return null;
		}
		return Byte.parseByte(o.toString());
	}

	/**
	 * @param o
	 *            Object
	 * @return 日時っぽいモノからDateに変換
	 */
	public static Date toDate(final Object o) {

		if (o == null) {
			return null;
		}

		Date ret = null;

		ret = parseDateTime(o, "yyyy/MM/dd HH:mm:ss.SSS");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "yyyy/MM/dd HH:mm:ss");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "yyyy/MM/dd");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "yyyyMMdd");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "yyyyMM");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "H:m:s");
		if (ret != null) {
			return ret;
		}

		ret = parseDateTime(o, "H:m");
		if (ret != null) {
			return ret;
		}

		throw new SystemError(MessageKeys.ABEND_PARAM_DATE, o);
	}

	/**
	 * @param o
	 *            Object
	 * @return 日時っぽいモノからTimeに変換
	 */
	public static Time toTime(final Object o) {
		if (o == null) {
			return null;
		}
		return new Time(toDate(o).getTime());
	}

	/**
	 * @param o
	 *            Object
	 * @return 日時っぽいモノからTimestampに変換
	 */
	public static Timestamp toTimestamp(final Object o) {
		if (o == null) {
			return null;
		}
		return new Timestamp(toDate(o).getTime());
	}

	/**
	 * @param o
	 *            Object
	 * @param pattern
	 *            pattern
	 * @return 日時っぽいモノからDateに変換
	 */
	private static Date parseDateTime(final Object o, final String pattern) {
		if (o == null) {
			return null;
		}
		Date parsed = null;
		try {
			parsed = new Date(new SimpleDateFormat(pattern).parse(o.toString()).getTime());
			LOG.debug("parsed. " + o.toString() + " to [" + pattern + "].");
		} catch (ParseException e) {
			LOG.trace("parse failed. " + o.toString() + " to [" + pattern + "].");
		}
		return parsed;
	}

	/**
	 * @param date
	 *            date
	 * @param pattern
	 *            pattern
	 * @return Dateを文字列にフォーマット
	 */
	public static String formatDate(final Date date, final String pattern) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * @param time
	 *            time
	 * @param pattern
	 *            pattern
	 * @return Timeを文字列にフォーマット
	 */
	public static String formatTime(final Time time, final String pattern) {
		if (time == null) {
			return null;
		}
		return formatDate(new Date(time.getTime()), pattern);
	}

	/**
	 * @param timestamp
	 *            timestamp
	 * @param pattern
	 *            pattern
	 * @return Timestampを文字列にフォーマット
	 */
	public static String formatTimestamp(final Timestamp timestamp, final String pattern) {
		if (timestamp == null) {
			return null;
		}
		return formatDate(new Date(timestamp.getTime()), pattern);
	}

	/**
	 * @param i
	 *            i
	 * @param pattern
	 *            pattern
	 * @return Integerを文字列にフォーマット
	 */
	public static String formatInteger(final Integer i, final String pattern) {
		if (i == null) {
			return null;
		}
		DecimalFormat sdf = new DecimalFormat(pattern);
		return sdf.format(i);
	}

	/**
	 * @param o
	 *            o
	 * @return 少数っぽいモノをDoubleに変換
	 */
	public static Double toDouble(final Object o) {
		if (o == null) {
			return null;
		}
		return Double.parseDouble(o.toString());
	}

	/**
	 * @param o
	 *            o
	 * @return 整数っぽいモノをIntegerに変換
	 */
	public static Integer toInteger(final Object o) {
		if (o == null) {
			return null;
		}
		return Integer.parseInt(o.toString());
	}

	/**
	 * @param o
	 *            o
	 * @return 数値っぽいモノをBigIntegerに変換
	 */
	public static BigInteger toBigInteger(final Object o) {
		if (o == null) {
			return null;
		}
		return BigInteger.valueOf(Long.parseLong(o.toString()));
	}

	/**
	 * @param o
	 *            o
	 * @return 数値っぽいモノをBigDecimalに変換
	 */
	public static BigDecimal toBigDecimal(final Object o) {
		if (o == null) {
			return null;
		}
		return BigDecimal.valueOf(Long.parseLong(o.toString()));
	}

	/**
	 * @param o
	 *            文字列っぽいモノをStringに変換
	 * @return String
	 */
	public static String toString(final Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}

}
