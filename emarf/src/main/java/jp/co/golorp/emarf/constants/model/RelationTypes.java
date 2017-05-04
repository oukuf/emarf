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
 * 関連種別
 *
 * @author oukuf@golorp
 */
public enum RelationTypes {

	/** 兄弟モデル */
	BROTHER,

	/** 親モデル */
	PARENT,

	/** 子モデル */
	CHILD,

	/** 祖先モデル */
	ANCESTOR,

	/** 子孫モデル */
	DESCENDANT,

	/** 被参照モデル */
	REFER_BY,

	/** 参照モデル */
	REFER_TO,

	/** 被履歴モデル */
	HISTORY_BY,

	/** 履歴モデル */
	HISTORY_OF,

	/** 被集約モデル */
	SUMMARY_BY,

	/** 集約モデル */
	SUMMARY_OF,

	/** 再帰モデル */
	RECURSIVE_TO,

	/** 被再帰モデル */
	RECURSIVE_BY

}
