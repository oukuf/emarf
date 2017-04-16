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
package jp.co.golorp.emarf.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.constants.scope.CtxKey;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * ログイン判定用フィルタ<br>
 * web.xmlで記述されていれば有効になる
 *
 * @author oukuf@golorp
 */
public final class LoginFilter implements Filter {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

	/** ログインオブジェクト名初期化パラメータ */
	protected static final String INIT_PARAM_LOGIN_KEYS = "login_keys";

	/** ログイン画面URI初期化パラメータ */
	protected static final String INIT_PARAM_LOGIN_URI = "login_uri";

	/** ログアウト画面URI初期化パラメータ */
	protected static final String INIT_PARAM_LOGOUT_URI = "logout_uri";

	/** ログイン状態なら存在すべきセッション属性のキーのリスト */
	private List<String> loginKeys;

	/** ログインURI */
	private String loginURI;

	/** ログアウトURI */
	private String logoutURI;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {

		// ログインオブジェクト名をリストに取得
		String loginKeys = filterConfig.getInitParameter(INIT_PARAM_LOGIN_KEYS);
		if (loginKeys != null) {
			String[] keys = loginKeys.split(",");
			for (String key : keys) {
				if (this.loginKeys == null) {
					this.loginKeys = new ArrayList<String>();
				}
				this.loginKeys.add(key);
			}
		}

		// ログイン画面、ログアウト画面のURIを初期化パラメータから取得
		this.loginURI = filterConfig.getInitParameter(INIT_PARAM_LOGIN_URI);
		this.logoutURI = filterConfig.getInitParameter(INIT_PARAM_LOGOUT_URI);

		// ログインオブジェクト名リストとログアウト画面URIをコンテキスト属性に退避
		ServletContext sc = filterConfig.getServletContext();
		sc.setAttribute(CtxKey.LOGIN_KEYS, this.loginKeys);
		sc.setAttribute(CtxKey.LOGOUT_URI, this.logoutURI);
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		LOG.debug("filter start.");

		HttpServletRequest req = (HttpServletRequest) request;

		String requestURI = req.getRequestURI();

		// ログアウト画面へのアクセスならセッション破棄
		if (StringUtil.equals(requestURI, this.logoutURI)) {
			HttpSession ses = req.getSession();
			ses.invalidate();
		}

		if (!requestURI.startsWith(this.loginURI)) {
			// ログイン画面以外へのアクセスの場合

			HttpSession ses = req.getSession();

			// String contextPath = req.getContextPath();
			// String servletPath = RequestUtils.getServletPath(req);
			String contextServletPath = RequestUtil.getContextServletPath(request);

			// ログイン情報が一つでもなければログインページにリダイレクト
			if (this.loginKeys != null) {

				for (String loginKey : this.loginKeys) {

					Object o = ses.getAttribute(loginKey);

					if (o == null) {

						// ログアウト画面 か emarfサーブレットへのアクセス ならログイン画面へリダイレクト
						if (requestURI.equals(this.logoutURI) || requestURI.startsWith(contextServletPath)) {
							HttpServletResponse resp = (HttpServletResponse) response;
							EmarfServlet.redirect(resp, this.loginURI);
							return;
						}

						// 上記以外ならセッション切れエラー
						// FIXME 他にセッション切れエラーになるパターンはないか？
						throw new SystemError(MessageKeys.ERRORS_STATE_LOGOUT);
					}
				}
			}
		}

		chain.doFilter(request, response);

		LOG.debug("filter end.");
	}

	@Override
	public void destroy() {
	}

}
