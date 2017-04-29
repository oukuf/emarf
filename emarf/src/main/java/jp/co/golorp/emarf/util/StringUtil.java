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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 文字列操作用ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class StringUtil extends StringUtils {

	/**
	 * コンストラクタ
	 */
	private StringUtil() {
	}

	/**
	 * キャメルケースに変換
	 *
	 * @param s
	 *            対象文字列
	 * @return キャメルケース
	 */
	public static String toCamelCase(final String s) {

		if (s == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		// "_"で分割
		String[] pieces = s.split("_");

		// "_"が含まれず全て大文字でもない
		// （UpperCamelCase 大文字・小文字まじり）
		// の場合は大文字の直前で分割
		if (pieces.length == 1 && !s.matches("^[0-9A-Z]+$")) {
			pieces = s.split("(?=[A-Z])");
		}

		for (String piece : pieces) {
			if (sb.toString().equals("")) {
				// 一つ目は全て小文字
				sb.append(piece.toLowerCase());
			} else {
				// 二つ目以降は１文字目を大文字、２文字目以降は小文字
				sb.append(piece.substring(0, 1).toUpperCase());
				sb.append(piece.substring(1).toLowerCase());
			}
		}

		return sb.toString();
	}

	/**
	 * アッパーキャメルケースに変換
	 *
	 * @param s
	 *            対象文字列
	 * @return キャメルケース
	 */
	public static String toUpperCamelCase(final String s) {
		if (StringUtil.isBlank(s)) {
			return null;
		}
		String camelCase = StringUtil.toCamelCase(s);
		return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
	}

	/**
	 * @param o
	 *            o
	 * @return boolean
	 */
	public static boolean isBlank(final Object o) {
		return !isNotBlank(o);
	}

	/**
	 * @param o
	 *            o
	 * @return boolean
	 */
	public static boolean isNotBlank(final Object o) {
		if (o != null) {
			if (o instanceof String[]) {
				String[] strings = (String[]) o;
				for (String string : strings) {
					CharSequence cs = string;
					if (StringUtil.isNotBlank(cs)) {
						return true;
					}
				}
			} else {
				String s = String.valueOf(o);
				if (StringUtil.isNotBlank(s)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param s
	 *            評価文字列
	 * @return 「true」か
	 */
	public static boolean is(final String s) {
		if (s != null && s.equals("true")) {
			return true;
		}
		return false;
	}

	/**
	 * @param value
	 *            value
	 * @return String
	 */
	public static String getValue(final Object value) {

		if (value instanceof String[]) {
			String s = "";
			for (String v : (String[]) value) {
				if (!s.equals("")) {
					s += ", ";
				}
				s += v;
			}
			return s;
		}

		if (value != null) {
			return value.toString();
		}

		return null;
	}

	/**
	 * @param value
	 *            項目値
	 * @return 値配列
	 */
	public static String[] toStringArray(final Object value) {

		String[] values = null;

		if (value instanceof String[]) {
			values = (String[]) value;
		} else if (value instanceof Object[]) {
			List<String> valueList = new ArrayList<String>();
			for (Object o : (Object[]) value) {
				valueList.add(String.valueOf(o));
			}
			values = valueList.toArray(new String[valueList.size() - 1]);
		} else if (value instanceof List) {
			List<?> valueList = (List<?>) value;
			if (valueList.size() > 0) {
				values = valueList.toArray(new String[valueList.size() - 1]);
			}
		} else if (value != null) {
			values = new String[] { String.valueOf(value) };
		}

		return values;
	}

	/**
	 * @param stringArray
	 *            stringArray
	 * @return 空でない最初の文字列
	 */
	public static String getFirst(final String[] stringArray) {
		String s = null;
		for (int i = 0; i < stringArray.length; i++) {
			s = stringArray[i];
			if (StringUtil.isNotBlank(s)) {
				break;
			}
		}
		return s;
	}

	/**
	 * @param stringArray
	 *            stringArray
	 * @return 空でない最後の文字列
	 */
	public static String getLast(final String[] stringArray) {
		String s = null;
		for (int i = stringArray.length - 1; i > 0; i--) {
			s = stringArray[i];
			if (StringUtil.isNotBlank(s)) {
				break;
			}
		}
		return s;
	}

	/**
	 * @param s
	 *            s
	 * @param str
	 *            str
	 * @return int
	 */
	public static int indexOfIgnoreCase(final String s, final String str) {
		String[] strs = s.split("(?i)" + str);
		if (s.length() == strs[0].length()) {
			return -1;
		}
		return strs[0].length();
	}

	/**
	 * @param s
	 *            s
	 * @return \b,\r,\n," "を除去した文字列
	 */
	public static String trim(final String s) {
		String ret = StringUtils.trim(s);
		if (ret != null) {
			ret.replaceAll("\\b|\\r|\\n", "").trim();
		}
		return ret;
	}

	/**
	 * @param s
	 *            s
	 * @return 「,」で分解した文字列
	 */
	public static String[] split(final String s) {
		if (isNotBlank(s)) {
			return s.split(",\\s*");
		}
		return null;
	}

}
