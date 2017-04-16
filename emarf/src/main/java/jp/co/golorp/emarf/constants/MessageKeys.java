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
package jp.co.golorp.emarf.constants;

/**
 * Message.propertiesのキー文字列管理クラス
 *
 * @author oukuf@golorp
 */
public enum MessageKeys {

	/*
	 * ABEND
	 */

	/***/
	ABEND_DATA_INSERT("abend.data.insert"),

	/***/
	ABEND_DATA_UPDATE("abend.data.update"),

	/***/
	ABEND_DATA_DELETE("abend.data.delete"),

	/***/
	ABEND_VERSIONNO_NONE("abend.versionNo.none"),

	/***/
	ABEND_COLUMN_NONE("abend.column.none"),

	/***/
	ABEND_PARAM_DATE("abend.param.date"),

	/*
	 * ERRORS
	 */

	/***/
	ERRORS_VALIDATE_REQUIRED("errors.validate.required"),

	/***/
	ERRORS_VALIDATE_FIX_LENGTH("errors.validate.fixlength"),

	/***/
	ERRORS_VALIDATE_LENGTH_RANGE("errors.validate.lengthRange"),

	/***/
	ERRORS_VALIDATE_MAX_LENGTH("errors.validate.maxlength"),

	/***/
	ERRORS_VALIDATE_INVALID("errors.validate.invalid"),

	/***/
	ERRORS_VALIDATE_LESS_THAN("errors.validate.lessThan"),

	/***/
	ERRORS_VALIDATE_LESS_EQUAL("errors.validate.lessEqual"),

	/***/
	ERRORS_VALIDATE_GREATER_EQUAL("errors.validate.greaterEqual"),

	/***/
	ERRORS_VALIDATE_GREATER_THAN("errors.validate.greaterThan"),

	/***/
	ERRORS_VALIDATE_BEFORE("errors.validate.before"),

	/***/
	ERRORS_VALIDATE_NOT_AFTER("errors.validate.notAfter"),

	/***/
	ERRORS_VALIDATE_NOT_BEFORE("errors.validate.notBefore"),

	/***/
	ERRORS_VALIDATE_AFTER("errors.validate.after"),

	/***/
	ERRORS_VALIDATE_NOT_EXIST("errors.validate.notExist"),

	/***/
	ERRORS_VALIDATE("errors.validate"),

	/***/
	ERRORS_VALIDATE_1("errors.validate.1"),

	/***/
	ERRORS_VALIDATE_4("errors.validate.4"),

	/***/
	ERRORS_VALIDATE_12("errors.validate.12"),

	/***/
	ERRORS_VALIDATE_91("errors.validate.91"),

	/***/
	ERRORS_VALIDATE_92("errors.validate.92"),

	/***/
	ERRORS_VALIDATE_93("errors.validate.93"),

	/** 送信値なしエラー */
	ERRORS_PARAM_NONE("errors.param.none"),

	/** ログイン認証エラー */
	ERRORS_PARAM_LOGIN("errors.param.login"),

	/** セッション切れエラー */
	ERRORS_STATE_LOGOUT("errors.state.logout"),

	/** サービス時間外エラー */
	ERRORS_STATE_REFER("errors.state.refer"),

	/** 登録可能時間外エラー */
	ERRORS_STATE_REGIST("errors.state.regist"),

	/** 検索データなしエラー */
	ERRORS_DATA_NONE("errors.data.none"),

	/** 検索データ重複エラー */
	ERRORS_DATA_PLURAL("errors.data.plural"),

	/** 登録データ重複エラー */
	ERRORS_DATA_DUPLICATE("errors.data.duplicate"),

	/*
	 * INFOS
	 */

	/** 登録完了 */
	INFOS_INSERT("infos.insert"),

	/** 更新完了 */
	INFOS_UPDATE("infos.update"),

	/** 削除完了 */
	INFOS_DELETE("infos.delete");

	/** キー文字列 */
	private String key;

	/**
	 * コンストラクタ
	 *
	 * @param key
	 *            メッセージキー
	 */
	MessageKeys(final String key) {
		this.key = key;
	}

	@Override
	public String toString() {
		return this.key;
	}

}
