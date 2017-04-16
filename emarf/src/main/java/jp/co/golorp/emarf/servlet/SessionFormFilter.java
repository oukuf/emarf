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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.model.Crud;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.servlet.http.form.SessionProperty;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * リクエストパラメータをSessionForm化するフィルタ
 *
 * @author oukuf@golorp
 */
public final class SessionFormFilter implements Filter {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(SessionFormFilter.class);

	/** html項目名を分割する文字 */
	private static final String SPLIT_REGEX = "\\.";

	/** 添え字つき項目名の正規表現 */
	private static final String INDEXED_REGEX = "[^\\[]+\\[[0-9]+\\]$";

	/** 添え字の正規表現 */
	private static final String INDEX_REGEX = "\\[[0-9]+\\]$";

	/** 添え字つき項目名の数値部分以外の正規表現 */
	private static final String ELSES_REGEX = "[^\\[]+\\[|\\]$";

	/**
	 * @param request
	 *            request
	 * @return SessionForm
	 */
	public static SessionForm param2SessionForm(final ServletRequest request) {

		Map<String, String[]> parameterMap = request.getParameterMap();
		if (parameterMap.isEmpty()) {
			return null;
		}

		String methodName = RequestUtil.getPathMethodName(request);

		boolean isGetMethod = methodName.equalsIgnoreCase(EmarfServlet.METHOD_GET);

		// boolean aintPaging = false;

		SessionForm sessionForm = new SessionForm();

		for (Entry<String, String[]> parameter : parameterMap.entrySet()) {

			// 画面項目名が「.」区切りでなければスキップ
			String htmlName = parameter.getKey();
			String[] htmlNames = htmlName.split(SPLIT_REGEX);
			if (htmlNames.length == 1) {
				continue;
			}

			// メソッド名が「GET」の場合、入力がなかった画面項目はスキップ
			// 検索条件に含めないため
			String[] values = parameter.getValue();
			if (isGetMethod && StringUtil.isBlank(values)) {
				continue;
			}

			// モデル名を取得
			String modelName = htmlNames[0];

			// モデル名が添え字付きの場合は、添え字を除去しインデクスを取得
			Integer i = 0;
			if (modelName.matches(INDEXED_REGEX)) {
				modelName = htmlNames[0].replaceFirst(INDEX_REGEX, "");
				i = new Integer(htmlNames[0].replaceAll(ELSES_REGEX, ""));
			}

			// プロパティ名を取得
			String propertyName = htmlNames[1].replaceFirst(INDEX_REGEX, "");

			// ページ繰りかどうか
			if (propertyName.equals("page")) {
				sessionForm.setPaging(true);
				// TODO 一旦コメントアウト。要確認。
				// } else {
				// aintPaging = true;
			}

			// 指定した位置にあるsessionModelを取得
			SessionModel sessionModel = sessionForm.getModel(modelName, i);

			// 項目値をsessionModelに設定
			sessionModel.put(propertyName, htmlName, values);
		}

		return sessionForm;
	}

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		/*
		 * 事前処理
		 */

		// クエリストリングがあればスキップ
		HttpServletRequest req = (HttpServletRequest) request;
		if (req.getQueryString() != null) {
			chain.doFilter(request, response);
			return;
		}

		// ポスト値がなければスキップ
		Map<String, String[]> parameterMap = req.getParameterMap();
		if (parameterMap.isEmpty()) {
			chain.doFilter(request, response);
			return;
		}

		LOG.debug("filter start.");

		/*
		 * ParameterMapをSessionFormに変換
		 */

		SessionForm sessionForm = param2SessionForm(request);

		String postedForm = sessionForm.toString();
		if (StringUtil.isNotBlank(postedForm)) {
			LOG.info("posted form contents is below:" + postedForm);
		} else {
			LOG.info("values is not posted.");
		}

		/*
		 * セッションフォームを取得
		 */

		String requestURI = RequestUtil.getRequestURI(request);

		Map<String, SessionForm> sessionForms = SessionFormUtil.getSessionForms(request);
		if (sessionForms == null) {
			sessionForms = new LinkedHashMap<String, SessionForm>();
		}

		// paging以外なら一旦セッションフォームをクリア
		// pagingということは一回はチェックOKになった送信値の組なのでそのまま再描画してもいいから
		if (!sessionForm.isPaging() /* || aintPaging */) {
			sessionForms.remove(requestURI);
		}

		/*
		 * 入力チェック
		 */

		List<ApplicationError> errors = validate(request, sessionForm);
		if (errors != null) {
			throw new ApplicationError(errors);
		}

		/*
		 * pagingならセッションフォームをマージ
		 */

		if (sessionForm.isPaging() /* && !aintPaging */) {
			SessionForm orgSessionForm = sessionForms.get(requestURI);
			if (orgSessionForm != null) {
				margeSessionForm(orgSessionForm, sessionForm);
			}
		}

		/*
		 * 入力OKならセッションに保管
		 */

		sessionForms.put(requestURI, sessionForm);

		SessionFormUtil.setSessionForms(request, sessionForms);

		chain.doFilter(request, response);

		LOG.debug("filter end.");
	}

	@Override
	public void destroy() {
	}

	/**
	 * 入力チェック
	 *
	 * @param request
	 *            request
	 * @param sessionForm
	 *            sessionForm
	 * @return List<ApplicationError>
	 */
	private static List<ApplicationError> validate(final ServletRequest request, final SessionForm sessionForm) {

		String pathModelName = RequestUtil.getPathModelName(request);
		String pathMethodName = RequestUtil.getPathMethodName(request);

		int crud = Crud.REFER;
		if (StringUtil.equalsIgnoreCase(pathMethodName, EmarfServlet.METHOD_POST)) {
			crud = Crud.CREATE;
		} else if (StringUtil.equalsIgnoreCase(pathMethodName, EmarfServlet.METHOD_PUT)) {
			crud = Crud.UPDATE;
		} else if (StringUtil.equalsIgnoreCase(pathMethodName, EmarfServlet.METHOD_DELETE)) {
			crud = Crud.DELETE;
		}

		List<ApplicationError> errors = null;

		// SessionModelリストでループ
		for (List<SessionModel> sessionModels : sessionForm.values()) {

			// SessionModelでループ
			for (SessionModel sessionModel : sessionModels) {

				try {

					SessionFormUtil.validate(crud, sessionModel, pathModelName);

				} catch (ApplicationError e) {

					if (errors == null) {
						errors = new ArrayList<ApplicationError>();
					}

					List<ApplicationError> causes = e.getErrors();
					for (ApplicationError cause : causes) {
						errors.add(cause);
					}
				}
			}
		}

		return errors;
	}

	/**
	 * セッションフォームをマージ
	 *
	 * @param org
	 *            セッションにあったセッションフォーム
	 * @param posted
	 *            今回送信されたセッションフォーム
	 */
	protected static void margeSessionForm(final SessionForm org, final SessionForm posted) {

		/*
		 * 一旦、元々存在したSessionFormに今回postされたSessionFormを上書き
		 */

		for (Entry<String, List<SessionModel>> orgModel : org.entrySet()) {
			String orgModelName = orgModel.getKey();
			List<SessionModel> orgSessionModels = orgModel.getValue();

			for (int i = 0; i < orgSessionModels.size(); i++) {

				SessionModel orgSessionModel = orgSessionModels.get(i);

				SessionModel sessionModel = posted.getModel(orgModelName, i);

				for (Entry<String, SessionProperty> property : sessionModel.entrySet()) {
					String propertyName = property.getKey();
					SessionProperty sessionProperty = property.getValue();

					for (Entry<String, String[]> html : sessionProperty.entrySet()) {
						String htmlName = html.getKey();
						String[] values = html.getValue();

						orgSessionModel.put(propertyName, htmlName, values);
					}
				}
			}
		}

		/*
		 * 今回postされたSessionFormに上書き済みの元々存在したSessionFormを改めて上書き
		 */

		for (Entry<String, List<SessionModel>> model : posted.entrySet()) {
			String modelName = model.getKey();
			List<SessionModel> sessionModels = model.getValue();

			for (int i = 0; i < sessionModels.size(); i++) {

				SessionModel sessionModel = sessionModels.get(i);

				SessionModel orgSessionModel = org.getModel(modelName, i);

				for (Entry<String, SessionProperty> orgProperty : orgSessionModel.entrySet()) {
					String orgPropertyName = orgProperty.getKey();
					SessionProperty orgSessionProperty = orgProperty.getValue();

					for (Entry<String, String[]> orgHtml : orgSessionProperty.entrySet()) {
						String orgHtmlName = orgHtml.getKey();
						String[] orgValues = orgHtml.getValue();

						sessionModel.put(orgPropertyName, orgHtmlName, orgValues);
					}
				}
			}
		}
	}

}
