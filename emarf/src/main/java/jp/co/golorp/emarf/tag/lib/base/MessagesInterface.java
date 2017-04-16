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

import java.util.Map;

/**
 * メッセージタグのインターフェース
 *
 * @author oukuf@golorp
 */
public interface MessagesInterface {

	/**
	 * @return pageスコープのメッセージ
	 */
	Map<String, String[]> getPageContextMessage();

	/**
	 * @return メッセージエリアの接頭辞
	 */
	String getPrefix();

	/**
	 * @return メッセージの接頭辞
	 */
	String getMsgPrefix();

	/**
	 * @return メッセージの接尾辞
	 */
	String getMsgSuffix();

	/**
	 * @return メッセージエリアの接尾辞
	 */
	String getSuffix();

	/**
	 * @return セッション変数キー
	 */
	String getAttributeKey();

	/**
	 * @return CSSクラス名
	 */
	String getCssClassName();

}
