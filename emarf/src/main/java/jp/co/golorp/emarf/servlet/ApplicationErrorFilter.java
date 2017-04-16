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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.scope.SesKey;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * ApplicationError発生時のリダイレクトおよびフォワード用Filter
 *
 * @author oukuf@golorp
 */
public final class ApplicationErrorFilter implements Filter {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationErrorFilter.class);

	@Override
	public void init(final FilterConfig fConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		LOG.trace("filter start.");

		try {

			chain.doFilter(request, response);

		} catch (ApplicationError e) {
			// ApplicationErrorの場合

			// 予約済みのメールをキャンセル
			RequestUtil.cancelReservedMail(request);

			HttpServletRequest req = (HttpServletRequest) request;

			Map<String, String[]> errors = null;

			if (e.getErrorMessages() != null) {
				// エラーメッセージが複数ある場合

				// エラーメッセージをログ出力
				for (Entry<String, String[]> error : e.getErrorMessages().entrySet()) {
					String message = error.getKey();
					String[] htmlNames = error.getValue();
					String errorItems = "";
					for (String htmlName : htmlNames) {
						if (errorItems.length() > 0) {
							errorItems += ", ";
						}
						errorItems += htmlName;
					}
					LOG.error(message + "[" + errorItems + "]");
				}

				errors = e.getErrorMessages();

			} else {
				// 上記以外の場合

				// エラーメッセージをログ出力
				LOG.error(e.getMessage());

				errors = new LinkedHashMap<String, String[]>();
				errors.put(e.getMessage(), null);
			}

			// エラーメッセージをセッションに格納
			req.getSession().setAttribute(SesKey.ERROR, errors);

			HttpServletResponse resp = (HttpServletResponse) response;

			String pageName = RequestUtil.getPathPageName(request);
			String methodName = RequestUtil.getPathMethodName(request);

			if (!pageName.equals(EmarfServlet.PAGE_INDEX) && methodName.equalsIgnoreCase(EmarfServlet.METHOD_GET)) {

				// indexページ以外のgetメソッドなら遷移元に戻す
				String redirectUrl = req.getHeader("REFERER");
				EmarfServlet.redirect(resp, redirectUrl);

			} else if (methodName.equalsIgnoreCase(EmarfServlet.METHOD_DELETE)) {

				// deleteメソッドなら遷移元に戻す
				String redirectUrl = req.getHeader("REFERER");
				EmarfServlet.redirect(resp, redirectUrl);

			} else {

				// 上記以外ならforward
				EmarfServlet.forward(req, resp);
			}

		} catch (Exception e) {
			// ApplicationError以外の場合

			// 予約済みのメールをキャンセル
			RequestUtil.cancelReservedMail(request);

			// SystemErrorを投げる
			if (e.getMessage() != null) {
				try {
					// String message = Message.get(e.getMessage());
					// if (StringUtil.isBlank(message)) {
					// message = e.getMessage();
					// }
					throw new SystemError(e.getMessage(), e);
				} catch (MissingResourceException e1) {
					throw new SystemError(e);
				}
			} else {
				throw new SystemError(e);
			}
		}

		LOG.trace("filter end.");
	}

	@Override
	public void destroy() {
	}

}
