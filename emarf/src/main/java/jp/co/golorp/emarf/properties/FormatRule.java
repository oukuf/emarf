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
package jp.co.golorp.emarf.properties;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * 画面表示用フォーマットルール
 *
 * @author oukuf@golorp
 */
public final class FormatRule {

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(FormatRule.class.getSimpleName());

	/**
	 * コンストラクタ
	 */
	private FormatRule() {
	}

	/**
	 * カラム名の末尾が合致する、フォーマット文字列を返す
	 *
	 * @param columnName
	 *            カラム名
	 * @return フォーマット文字列
	 */
	public static String get(final String columnName) {
		Enumeration<String> keys = BUNDLE.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			// (?i)はiオプション。キーがカラム名の末尾と合致すれば返却
			if (columnName.matches("(?i)^.*" + key + "$")) {
				return BUNDLE.getString(key);
			}
		}
		return null;
	}

}
