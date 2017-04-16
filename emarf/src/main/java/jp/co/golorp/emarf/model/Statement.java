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
package jp.co.golorp.emarf.model;

/**
 * sqlとパラメータのvo
 *
 * @author oukuf@golorp
 */
public class Statement {

	/***/
	private String sql;

	/***/
	private Object[] params;

	/**
	 * コンストラクタ
	 *
	 * @param sql
	 *            sql文字列
	 * @param params
	 *            パラメータ
	 */
	public Statement(final String sql, final Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	/**
	 * @return sql
	 */
	public String getSql() {
		return this.sql;
	}

	/**
	 * @return パラメータの配列
	 */
	public Object[] getParams() {
		return this.params;
	}

}
