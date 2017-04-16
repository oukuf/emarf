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
package jp.co.golorp.emarf.constants.model;

/**
 * staticフィールド名の列挙子
 *
 * @author oukuf@golorp
 */
public enum ModelFieldTypes {

	/** 主キー */
	PK_PROPERTY_NAMES("PK_PROPERTY_NAMES"),

	/** カラム論理名 */
	PROPERTY_MEIS("PROPERTY_MEIS"),

	/** テーブル論理名 */
	MODEL_MEI("MODEL_MEI"),

	/** 関連情報 */
	RELATION_MAP("RELATION_MAP"),

	/** 追加メソッド名 */
	GET_INSERT_STATEMENT("getInsertStatement"),

	/** 照会メソッド名 */
	GET_SELECT_STATEMENT("getSelectStatement"),

	/** 更新メソッド名 */
	GET_UPDATE_STATEMENT("getUpdateStatement"),

	/** 削除メソッド名 */
	GET_DELETE_STATEMENT("getDeleteStatement");

	/** キー文字列 */
	private String name;

	/**
	 * コンストラクタ
	 *
	 * @param name
	 *            管理文字列
	 */
	ModelFieldTypes(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

};
