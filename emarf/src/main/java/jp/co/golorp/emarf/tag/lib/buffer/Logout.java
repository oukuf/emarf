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
package jp.co.golorp.emarf.tag.lib.buffer;

import javax.servlet.ServletContext;

import jp.co.golorp.emarf.constants.scope.CtxKey;
import jp.co.golorp.emarf.tag.lib.BufferTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * ログアウトボタンタグ
 *
 * @author oukuf@golorp
 */
public class Logout extends BufferTagSupport {

	@Override
	public String doEnd() {

		ServletContext sc = this.pageContext.getServletContext();

		// ログインオブジェクトキー文字列をコンテキストから取得。なければ終了。
		Object o = sc.getAttribute(CtxKey.LOGIN_KEYS);
		if (o == null) {
			return null;
		}

		// ログイン中でなければ終了
		if (!RequestUtil.isIfLogin(this.pageContext.getRequest())) {
			return null;
		}

		// ログアウト処理URIをコンテキストから取得
		String logoutURI = (String) sc.getAttribute(CtxKey.LOGOUT_URI);

		// 開始タグから閉じタグまでの文字列をラベルに設定してログアウトボタンを出力
		StringBuffer sb = new StringBuffer("<input type=\"button\" id=\"logoutButton\" onclick=\"location.href='")
				.append(logoutURI).append("'\" value=\"").append(this.buffer).append("\">");

		return sb.toString();
	}

}
