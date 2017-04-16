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
package jp.co.golorp.emarf.exception;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.properties.Message;

/**
 * システムエラー<br>
 * アプリケーションエラーとは異なり、常に単体で使用する。
 *
 * @author oukuf@golorp
 */
public class SystemError extends RuntimeException {

	/**
	 * コンストラクタ
	 *
	 * @param message
	 *            詳細メッセージ
	 */
	public SystemError(final String message) {
		super(message);
	}

	/**
	 * コンストラクタ
	 *
	 * @param messageKeys
	 *            Message.propertiesのキー文字列
	 */
	public SystemError(final MessageKeys messageKeys) {
		super(Message.get(messageKeys));
	}

	/**
	 * コンストラクタ
	 *
	 * @param messageKeys
	 *            Message.propertiesのキー文字列
	 * @param params
	 *            メッセージ文字列内のプレースホルダを置換するパラメータ
	 */
	public SystemError(final MessageKeys messageKeys, final Object... params) {
		super(Message.get(messageKeys, params));
	}

	/**
	 * コンストラクタ
	 *
	 * @param message
	 *            詳細メッセージ
	 * @param cause
	 *            原因
	 */
	public SystemError(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ
	 *
	 * @param cause
	 *            原因
	 */
	public SystemError(final Throwable cause) {
		super(cause);
	}

	/**
	 *
	 * @param message
	 *            詳細メッセージ
	 * @param cause
	 *            原因
	 * @param enableSuppression
	 *            抑制を有効化するか、それとも無効化するか
	 * @param writableStackTrace
	 *            スタック・トレースを書込み可能にするかどうか
	 */
	protected SystemError(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
