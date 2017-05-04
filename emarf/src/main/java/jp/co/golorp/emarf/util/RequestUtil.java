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
package jp.co.golorp.emarf.util;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.scope.CtxKey;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.util.mail.MailInfo;

/**
 * リクエスト操作用ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class RequestUtil {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(RequestUtil.class);

	/** URLセパレータ */
	private static final String SEP = EmarfServlet.SEP;

	/** デフォルトメソッド名 */
	public static final String DEFAULT_METHOD = "get";

	/***/
	public static final String REQUEST_URI = "javax.servlet.forward.request_uri";

	/***/
	public static final String SERVLET_PATH = "javax.servlet.forward.servlet_path";

	/***/
	public static final String PATH_INFO = "javax.servlet.forward.path_info";

	/***/
	private static final int PATH_MODEL_NAME_POS = 1;

	/***/
	private static final int PATH_PAGE_NAME_POS = 2;

	/***/
	private static final int PATH_METHOD_NAME_POS = 3;

	/**
	 * コンストラクタ
	 */
	private RequestUtil() {
	}

	/**
	 * @param request
	 *            request
	 * @return
	 *
	 *         <pre>
	 * http://[domain]:[port]<b><i>/[contextroot]/[servletpath]/[modelname]/[pagename]/[methodname]</i></b>?key=value
	 *         </pre>
	 *
	 *         リクエスト変数で特に指定があればそれを使用する
	 */
	public static String getRequestURI(final ServletRequest request) {

		Object o = request.getAttribute(REQUEST_URI);
		if (o != null) {
			return (String) o;
		}

		String requestURI = ((HttpServletRequest) request).getRequestURI();

		if (requestURI.endsWith("/index/")) {
			requestURI = requestURI.replaceFirst("index/$", "");
		}

		return requestURI;
	}

	/**
	 * @param request
	 *            request
	 * @return
	 *
	 *         <pre>
	 * http://[domain]:[port]<b><i>/[contextroot]/[servletpath]</i></b>/[modelname]/[pagename]/[methodname]?key=value
	 *         </pre>
	 *
	 *         サーブレットパスはリクエスト変数で特に指定があればそれを使用する
	 */
	public static String getContextServletPath(final ServletRequest request) {

		HttpServletRequest req = (HttpServletRequest) request;
		String contextPath = req.getContextPath();

		Object o = request.getAttribute(SERVLET_PATH);
		if (o != null) {
			return contextPath + (String) o;
		}

		return contextPath + ((HttpServletRequest) request).getServletPath();
	}

	/**
	 * @param request
	 *            request
	 * @return
	 *
	 *         <pre>
	 * http://[domain]:[port]/[contextroot]/[servletpath]<b><i>/[modelname]/[pagename]/[methodname]</i></b>?key=value
	 *         </pre>
	 *
	 *         リクエスト変数で特に指定があればそれを使用する
	 */
	public static String getPathInfo(final ServletRequest request) {

		Object o = request.getAttribute(PATH_INFO);
		if (o != null) {
			return (String) o;
		}

		return ((HttpServletRequest) request).getPathInfo();
	}

	/**
	 * @param request
	 *            request
	 * @return requesturi内のモデル名
	 */
	public static String getPathModelName(final ServletRequest request) {

		String modelName = null;

		String pathInfo = getPathInfo(request);
		if (pathInfo != null) {
			String[] pathInfos = pathInfo.split(SEP);
			if (pathInfos.length > PATH_MODEL_NAME_POS) {
				modelName = pathInfos[PATH_MODEL_NAME_POS];
			}
		}

		return modelName;
	}

	/**
	 * @param request
	 *            request
	 * @return requesturi内のページ名
	 */
	public static String getPathPageName(final ServletRequest request) {

		String pageName = EmarfServlet.PAGE_INDEX;

		String pathInfo = getPathInfo(request);
		if (pathInfo != null) {
			String[] pathInfos = pathInfo.split(SEP);
			if (pathInfos.length > PATH_PAGE_NAME_POS) {
				pageName = pathInfos[PATH_PAGE_NAME_POS];
			}
		}

		return pageName;
	}

	/**
	 * @param request
	 *            request
	 * @return requesturi内のメソッド名（post|get|put|delete）
	 */
	public static String getPathMethodName(final ServletRequest request) {

		String methodName = DEFAULT_METHOD;

		String pathInfo = getPathInfo(request);
		if (pathInfo != null) {
			String[] pathInfos = pathInfo.split(SEP);
			if (pathInfos.length > PATH_METHOD_NAME_POS) {
				methodName = pathInfos[PATH_METHOD_NAME_POS];
			}
		}

		return methodName;
	}

	/**
	 * リクエストスコープのモデル、セッションフォーム、リクエストパラメータ からプロパティ値を取得
	 *
	 * @param request
	 *            request
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param htmlName
	 *            htmlName
	 * @return プロパティ値
	 */
	public static Object lookup(final ServletRequest request, final String modelName, final String propertyName,
			final String htmlName) {

		// リクエスト属性のモデルから取得
		Object value = getStringModelValue(request, modelName, propertyName);
		if (StringUtil.isNotBlank(value)) {
			return value;
		}

		// セッションフォームから取得
		String[] values = SessionFormUtil.getValues(request, htmlName);
		if (StringUtil.isNotBlank(values)) {
			if (values.length == 1) {
				return values[0];
			} else {
				return values;
			}
		}

		// リクエストパラメータから取得
		value = request.getParameterValues(htmlName);
		if (StringUtil.isNotBlank(value)) {
			return value;
		}

		return null;
	}

	/**
	 * @param request
	 *            request
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @return リクエストスコープ内のモデルからフォーマットしたプロパティ値を取得
	 */
	public static String getStringModelValue(final ServletRequest request, final String modelName,
			final String propertyName) {

		Model model = (Model) request.getAttribute(modelName);
		if (model != null) {
			return model.getString(propertyName);
		}

		return null;
	}

	/**
	 * @param <T>
	 *            T
	 * @param request
	 *            request
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @return ログイン情報からモデルプロパティ値を取得
	 */
	public static <T> T getLoginValue(final ServletRequest request, final String modelName, final String propertyName) {

		Object o = request.getServletContext().getAttribute(CtxKey.LOGIN_KEYS);
		if (o == null) {
			return null;
		}

		HttpSession ses = ((HttpServletRequest) request).getSession();

		@SuppressWarnings("unchecked")
		List<String> loginKeys = (List<String>) o;
		for (String loginKey : loginKeys) {

			if (!loginKey.equals(modelName)) {
				continue;
			}

			Model loginModel = (Model) ses.getAttribute(loginKey);
			if (loginModel == null) {
				continue;
			}

			@SuppressWarnings("unchecked")
			T property = (T) loginModel.get(propertyName);
			if (StringUtil.isNotBlank(property)) {
				return property;
			}
		}

		return null;
	}

	/**
	 * ログイン状態を返す
	 *
	 * @param request
	 *            リクエスト
	 * @return
	 *         <dl>
	 *         <dt>ログイン不要の場合
	 *         <dd>true
	 *         <dt>ログイン要の場合
	 *         <dd>
	 *         <dl>
	 *         <dt>ログイン済み
	 *         <dd>true
	 *         <dt>未ログイン
	 *         <dd>false
	 *         </dl>
	 *         </dl>
	 */
	public static boolean isIfLogin(final ServletRequest request) {

		boolean isLogin = true;

		ServletContext servletContext = request.getServletContext();

		Object o = servletContext.getAttribute(CtxKey.LOGIN_KEYS);
		if (o == null) {
			return true;
		}

		@SuppressWarnings("unchecked")
		List<String> loginKeys = (List<String>) o;

		for (String loginKey : loginKeys) {

			HttpSession ses = ((HttpServletRequest) request).getSession();

			if (ses.getAttribute(loginKey) == null) {
				isLogin = false;
				break;
			}
		}

		return isLogin;
	}

	/**
	 * @param request
	 *            request
	 * @param modelName
	 *            modelName
	 * @param criteria
	 *            criteria
	 * @return 指定したモデルの主キーに合致するプロパティがログイン情報にあれば、指定したモデルのCriteriaとして取得する
	 */
	public static Criteria addCriteriaLogin(final ServletRequest request, final String modelName,
			final Criteria criteria) {

		@SuppressWarnings("unchecked")
		List<String> loginKeys = (List<String>) request.getServletContext().getAttribute(CtxKey.LOGIN_KEYS);
		if (loginKeys == null) {
			return null;
		}

		Criteria c = criteria;

		Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);

		HttpSession ses = ((HttpServletRequest) request).getSession();

		for (String loginKey : loginKeys) {

			Model loginModel = (Model) ses.getAttribute(loginKey);
			if (loginModel == null) {
				continue;
			}

			for (String propertyName : primaryPropertyNames) {

				Object value = loginModel.get(propertyName);
				if (StringUtil.isBlank(value)) {
					continue;
				}

				LOG.debug(propertyName + ": " + String.valueOf(value) + ".");
				if (c == null) {
					c = Criteria.equal(modelName, propertyName, value);
				} else {
					c.eq(modelName, propertyName, value);
				}
			}
		}

		return c;
	}

	/**
	 * @param request
	 *            request
	 * @param parentModelName
	 *            parentModelName
	 * @param modelName
	 *            modelName
	 * @param criteria
	 *            criteria
	 * @return リクエストスコープの親モデルから指定したモデルのCriteriaを取得
	 */
	public static Criteria addCriteriaParent(final ServletRequest request, final String parentModelName,
			final String modelName, final Criteria criteria) {

		Criteria c = criteria;

		if (parentModelName != null) {
			// 親タグにモデル名がある場合

			// 親タグのモデルがリクエストスコープになければ終了
			Model parentModel = (Model) request.getAttribute(parentModelName);
			if (parentModel == null) {
				return c;
			}

			RelateColumnMap relateColumnMap = new RelateColumnMap();

			// 当該モデルの親リレーション情報を取得（参照モデルでないので一つ目だけを取得する）
			RelateTablesMap parentTablesMap = ModelUtil.getParents(modelName);
			if (parentTablesMap != null) {
				List<RelateColumnMap> parentColumnMaps = parentTablesMap.get(parentModelName);
				if (parentColumnMaps != null) {
					RelateColumnMap parentColumnMap = parentColumnMaps.get(0);
					relateColumnMap.putAll(parentColumnMap);
				}
			}

			// 当該モデルの再帰元リレーション情報を取得（参照モデルでないので一つ目だけを取得する）
			RelateTablesMap recursiveToTablesMap = ModelUtil.getRecursiveTos(modelName);
			if (recursiveToTablesMap != null) {
				List<RelateColumnMap> recursiveToColumnMaps = recursiveToTablesMap.get(parentModelName);
				if (recursiveToColumnMaps != null) {
					RelateColumnMap recursiveToColumnMap = recursiveToColumnMaps.get(0);
					relateColumnMap.putAll(recursiveToColumnMap);
				}
			}

			// 当該モデルの履歴元リレーション情報を取得（参照モデルでないので一つ目だけを取得する）
			RelateTablesMap historyOfTablesMap = ModelUtil.getHistoryOfs(modelName);
			if (historyOfTablesMap != null) {
				List<RelateColumnMap> historyOfColumnMaps = historyOfTablesMap.get(parentModelName);
				if (historyOfColumnMaps != null) {
					RelateColumnMap historyOfColumnMap = historyOfColumnMaps.get(0);
					relateColumnMap.putAll(historyOfColumnMap);
				}
			}

			// 当該モデルの集約元リレーション情報を取得（参照モデルでないので一つ目だけを取得する）
			RelateTablesMap summaryOfTablesMap = ModelUtil.getSummaryOfs(modelName);
			if (summaryOfTablesMap != null) {
				List<RelateColumnMap> summaryOfColumnMaps = summaryOfTablesMap.get(parentModelName);
				if (summaryOfColumnMaps != null) {
					RelateColumnMap summaryOfColumnMap = summaryOfColumnMaps.get(0);
					relateColumnMap.putAll(summaryOfColumnMap);
				}
			}

			// リレーション情報がなければ終了
			if (relateColumnMap.isEmpty()) {
				return c;
			}

			// リレーション情報でループ
			for (Entry<String, String> relateColumn : relateColumnMap.entrySet()) {
				String columnName = relateColumn.getKey();
				String columnName2 = relateColumn.getValue();

				Object value = parentModel.get(columnName2);

				// 再帰モデルの場合
				if (modelName.equals(parentModelName)) {
					value = parentModel.get(columnName);
				}

				if (value != null) {
					if (c == null) {
						c = Criteria.equal(modelName, columnName, value);
					} else {
						c.eq(modelName, columnName, value);
					}
				}
			}
		}

		return c;
	}

	/**
	 * セッションに予約済みのメールを送信
	 *
	 * @param request
	 *            ServletRequest
	 */
	public static void sendReservedMail(final ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		String name = MailInfo.class.getName();
		Object o = session.getAttribute(name);
		if (o != null) {
			MailInfo mi = (MailInfo) o;
			session.removeAttribute(name);
			MailUtil.send(mi);
		}
	}

	/**
	 * セッションに予約済みのメールを中止
	 *
	 * @param request
	 *            ServletRequest
	 */
	public static void cancelReservedMail(final ServletRequest request) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		String name = MailInfo.class.getName();
		session.removeAttribute(name);
	}

}
