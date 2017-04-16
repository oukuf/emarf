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
package jp.co.golorp.emarf.constants.scope;

/**
 * Request属性のキー文字列管理クラス
 *
 * @author oukuf@golorp
 */
public abstract class ReqKey {

	/** /WEB-INF/jsp/layout.jspで主内容としてincludeするjspのURI用キー文字列 */
	public static final String CONTENTS_URI = "jp.co.golorp.emarf.contents_uri";

	/** TBody内で設定される行番号用キー文字列 */
	public static final String TBODY_NO = "TBody.no";

}
