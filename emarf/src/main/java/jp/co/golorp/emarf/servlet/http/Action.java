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
package jp.co.golorp.emarf.servlet.http;

import jp.co.golorp.emarf.servlet.http.form.SessionForm;

/**
 * EmarfServletの主処理（doGet・doPost・doPut・doDelete）前に実行される事前処理クラスのインターフェース<br>
 * 実クラスは[model+page+method]で命名する
 *
 * @author oukuf@golorp
 */
public interface Action {

	/**
	 * 事前処理実行
	 *
	 * @param ctx
	 *            HttpServletContext
	 * @param sessionForm
	 *            SessionForm
	 * @return 主処理を行わずにリダイレクトする相対パス。nullなら主処理が実行される。
	 */
	String execute(final HttpServletContext ctx, final SessionForm sessionForm);

}
