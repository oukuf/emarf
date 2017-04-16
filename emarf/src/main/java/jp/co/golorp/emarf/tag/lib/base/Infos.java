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
package jp.co.golorp.emarf.tag.lib.base;

import javax.servlet.ServletRequest;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.constants.scope.SesKey;
import jp.co.golorp.emarf.properties.App;

/**
 * 情報メッセージタグ
 *
 * @author oukuf@golorp
 */
public class Infos extends Messages {

	/** メッセージエリアの接頭辞 */
	private static final String PREFIX = App.get(AppKey.INFOS_PREFIX);

	/** メッセージの接頭辞 */
	private static final String MESSAGE_PREFIX = App.get(AppKey.INFOS_MESSAGE_PREFIX);

	/** メッセージの接尾辞 */
	private static final String MESSAGE_SUFFIX = App.get(AppKey.INFOS_MESSAGE_SUFFIX);

	/** メッセージエリアの接尾辞 */
	private static final String SUFFIX = App.get(AppKey.INFOS_SUFFIX);

	/** エラーメッセージ用セッション変数キー */
	private static final String ATTRIBUTE_KEY = SesKey.INFO;

	/** CSSクラス名 */
	private static final String CSS_CLASS_NAME = "info";

	/**
	 * セッションスコープにメッセージを追加する
	 *
	 * @param request
	 *            リクエスト
	 * @param message
	 *            メッセージ
	 * @param items
	 *            メッセージの該当する画面項目html名
	 */
	public static final void addMessage(final ServletRequest request, final String message, final String[] items) {
		Messages.addMessage(request, message, items, ATTRIBUTE_KEY);
	}

	@Override
	public String getPrefix() {
		return PREFIX;
	}

	@Override
	public String getMsgPrefix() {
		return MESSAGE_PREFIX;
	}

	@Override
	public String getMsgSuffix() {
		return MESSAGE_SUFFIX;
	}

	@Override
	public String getSuffix() {
		return SUFFIX;
	}

	@Override
	public String getAttributeKey() {
		return ATTRIBUTE_KEY;
	}

	@Override
	public String getCssClassName() {
		return CSS_CLASS_NAME;
	}

}
