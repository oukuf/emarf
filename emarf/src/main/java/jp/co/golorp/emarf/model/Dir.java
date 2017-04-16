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
 * 内部的に演算子を扱う列挙子
 *
 * @author oukuf@golorp
 */
public enum Dir {

	/** equal */
	eq("="),

	/** not equal */
	ne("<>"),

	/** less than */
	lt("<"),

	/** greater than */
	gt(">"),

	/** less equal */
	le("<="),

	/** greater equal */
	ge(">="),

	/** like */
	lk("LIKE"),

	/** between */
	bw("BETWEEN"),

	/** in */
	in("IN"),

	/** asc */
	asc("ASC"),

	/** desc */
	desc("DESC");

	/** 演算子文字列 */
	private String sign;

	/**
	 * コンストラクタ
	 *
	 * @param sign
	 *            演算子文字列
	 */
	Dir(final String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return this.sign;
	}

}
