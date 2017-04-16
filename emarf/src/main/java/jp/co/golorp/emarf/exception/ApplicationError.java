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

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.properties.Message;

/**
 * アプリケーションエラー<br>
 * アプリケーションエラーのリストとして使用する場合と、<br>
 * その１要素として使用する場合がある。
 *
 * @author oukuf@golorp
 */
public final class ApplicationError extends RuntimeException {

	/*
	 * アプリケーションエラーのリストを扱う場合のシグニチャ
	 */

	/** 内包するエラーリスト */
	private List<ApplicationError> errors;

	/**
	 * errorsを返す
	 *
	 * @return アプリケーションエラーのリスト
	 */
	public List<ApplicationError> getErrors() {
		return this.errors;
	}

	/**
	 * コンストラクタ
	 *
	 * @param errors
	 *            errors
	 */
	public ApplicationError(final List<ApplicationError> errors) {
		this.errors = errors;
	}

	/**
	 * エラーメッセージのmapを返す
	 *
	 * @return Map<エラーメッセージ, html項目のname属性の配列>
	 */
	public Map<String, String[]> getErrorMessages() {

		if (this.errors == null || this.errors.size() == 0) {
			return null;
		}

		Map<String, String[]> errorMessages = new LinkedHashMap<String, String[]>();

		for (ApplicationError error : this.errors) {

			String message = error.getMessage();

			Set<String> errorItems = null;
			String[] items = errorMessages.get(message);
			if (items == null) {
				errorItems = new HashSet<String>();
			} else {
				errorItems = new HashSet<String>(Arrays.asList(items));
			}
			errorItems.add(error.getHtmlName());
			items = errorItems.toArray(new String[errorItems.size()]);

			errorMessages.put(message, items);
		}

		return errorMessages;
	}

	/*
	 * アプリケーションエラー単体として使用する場合のシグニチャ
	 */

	/** エラーの対象となるhtml項目のname属性 */
	private String htmlName;

	/**
	 * コンストラクタ
	 *
	 * @param messageKeys
	 *            Message.propertiesのキー文字列
	 */
	public ApplicationError(final MessageKeys messageKeys) {
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
	public ApplicationError(final MessageKeys messageKeys, final Object... params) {
		super(Message.get(messageKeys, params));
	}

	/**
	 * htmlNameを取得します。
	 *
	 * @return htmlName
	 */
	public String getHtmlName() {
		return htmlName;
	}

	/**
	 * htmlNameを設定します。
	 *
	 * @param htmlName
	 *            htmlName
	 */
	public void setHtmlName(final String htmlName) {
		this.htmlName = htmlName;
	}

}
