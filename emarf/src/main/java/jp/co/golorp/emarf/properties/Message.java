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

import java.util.ResourceBundle;

import jp.co.golorp.emarf.constants.MessageKeys;

/**
 * メッセージファイル
 *
 * @author oukuf@golorp
 */
public final class Message {

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(Message.class.getSimpleName());

	/**
	 * コンストラクタ
	 */
	private Message() {
	}

	/**
	 * @param key
	 *            プロパティキー
	 * @return メッセージ文字列
	 */
	public static String get(final MessageKeys key) {
		return BUNDLE.getString(key.toString());
	}

	/**
	 * @param key
	 *            プロパティキー
	 * @param params
	 *            引数
	 * @return メッセージのプレースホルダを引数で置換した文字列
	 */
	public static String get(final MessageKeys key, final Object... params) {
		String message = BUNDLE.getString(key.toString());
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				// FIXME UnitTest用にNULL避け追加
				if (params[i] != null) {
					String param = params[i].toString();
					message = message.replaceAll("\\{" + i + "\\}", param);
				}
			}
		}
		return message;
	}

}
