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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.constants.model.Crud;
import jp.co.golorp.emarf.constants.scope.ReqKey;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.Message;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.servlet.http.form.SessionProperty;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.lib.base.Infos;
import jp.co.golorp.emarf.util.DateUtil;
import jp.co.golorp.emarf.util.IOUtil;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;
import net.sf.ehcache.CacheManager;

/**
 * WEBアプリケーションのメインサーブレット
 *
 * @author oukuf@golorp
 */
public final class EmarfServlet extends HttpServlet {

	/**
	 * @author emarf@golorp
	 */
	private class InsertedValue {

		/**
		 * @param modelName
		 *            モデル名
		 * @param value
		 *            プロパティ値
		 */
		InsertedValue(final String modelName, final Object value) {
			// this.modelName = modelName;
			this.value = value;
		}

		// /** モデル名 */
		// private String modelName;

		/** プロパティ値 */
		private Object value;

		/**
		 * @return value
		 */
		public Object getValue() {
			return value;
		}

	}

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(EmarfServlet.class);

	/*
	 * クラス定数
	 */

	/** サーブレット初期化パラメータ名：JSP格納ディレクトリ */
	protected static final String INIT_PARAM_BASE_PATH = "base_path";

	/** サーブレット初期化パラメータ名：テンプレート */
	protected static final String INIT_PARAM_TEMPLATE = "template";

	/** サーブレット初期化パラメータ名：デフォルトコンテンツ */
	protected static final String INIT_PARAM_DEFAULT_CONTENTS = "default_contents";

	/** サーブレット初期化パラメータ名：デフォルトモデル名 */
	protected static final String INIT_PARAM_DEFAULT_MODEL = "default_model";

	/** サーブレット初期化パラメータ名：アクション保管パッケージ */
	protected static final String INIT_PARAM_ACTION_PACKAGE = "action_package";

	/** サーブレット初期化パラメータ名：ログ対象外とする静的アクセスの正規表現 */
	protected static final String INIT_PARAM_STATIC_RESOURCE_REGEXP = "static_resource_regexp";

	/** デフォルトページ */
	public static final String PAGE_INDEX = "index";

	/** 登録ページ */
	public static final String PAGE_NEW = "new";

	/** 更新ページ */
	public static final String PAGE_EDIT = "edit";

	/** 照会ページ */
	public static final String PAGE_VIEW = "view";

	/** 階層ページ */
	public static final String PAGE_TREE = "tree";

	/** 選択ページ */
	public static final String PAGE_PICK = "pick";

	/** 追加（HttpServlet.METHOD_POST はprivate） */
	public static final String METHOD_POST = "POST";

	/** 参照（HttpServlet.METHOD_GET はprivate） */
	public static final String METHOD_GET = "GET";

	/** 更新（HttpServlet.METHOD_PUT はprivate） */
	public static final String METHOD_PUT = "PUT";

	/** 削除（HttpServlet.METHOD_DELETE はprivate） */
	public static final String METHOD_DELETE = "DELETE";

	/** URLセパレータ */
	public static final String SEP = "/";

	/** 拡張子 */
	private static final String EXT = ".jsp";

	/** サーブレット終了時にコンテキスト属性を破棄するための接頭辞 */
	private static final String CTX_KEY_PREFIX = "jp.co.golorp.emarf.";

	/** 登録処理後遷移先 */
	private static final String POSTED_REDIRECT = "../view/";

	/** 更新処理後遷移先 */
	private static final String PUTED_REDIRECT = "../view/";

	/** 削除処理後遷移先 */
	private static final String DELETED_REDIRECT = "../";

	/*
	 * サーブレット初期化パラメータ
	 */

	/** JSP格納ディレクトリ */
	protected static String basePath = "/WEB-INF/jsp";

	/** デフォルトjspファイル */
	private static String defaultContents = PAGE_INDEX;

	/** テンプレート */
	private static String template = "/WEB-INF/jsp/layout.jsp";

	/** デフォルトモデル名 */
	protected static String defaultModel = "default";

	/** 静的アクセスの正規表現 */
	protected static String staticResourceRegexp = ".+(\\.css$|\\.gif$|\\.js$|\\.png$)";

	/** アクション保管パッケージ */
	private static String actionPackage;

	/*
	 * クラスメソッド
	 */

	/**
	 * リクエストURIからページ内容を選定して画面遷移する
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @throws ServletException
	 *             ServletException
	 * @throws IOException
	 *             IOException
	 */
	public static void forward(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		// contentsURIを「/WEB-INF/jsp/index.jsp」に設定
		String contentsURI = basePath + SEP + defaultContents + EXT;

		String modelName = RequestUtil.getPathModelName(req);

		if (modelName != null) {
			// URLにモデル名がある場合

			String pageName = RequestUtil.getPathPageName(req);

			// contentsURIを「/WEB-INF/jsp/[modelName]/[pageName].jsp」に再設定
			contentsURI = basePath + SEP + modelName + SEP + pageName + EXT;

			// リクエストURLの物理絶対パスを取得
			// ex)c:\\pleiades-e4.5-java_20150624\\pleiades\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp2\\wtpwebapps\\deploy\\treleaseirai\\
			// String pathTranslated = req.getPathTranslated().toLowerCase();
			String pathTranslated = req.getPathTranslated();

			// コンテキスト名を取得
			// ex)emarf-blank
			// String contextName =
			// req.getContextPath().toLowerCase().substring(1);
			String contextName = req.getContextPath().substring(1);

			// 物理絶対パスでコンテキスト名の終了位置を取得
			// ex)119
			int endIndex = pathTranslated.lastIndexOf(contextName) + contextName.length();

			// コンテキスト名までの物理絶対パスを取得
			// ex)c:\\pleiades-e4.5-java_20150624\\pleiades\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp2\\wtpwebapps\\deploy
			String contextTranslated = pathTranslated.substring(0, endIndex);

			// ワークスペースが全角フォルダ配下の場合にファイルを見つけられないのでdecodeしておく
			String decodedContextTranslated = URLDecoder.decode(contextTranslated, "UTF-8");

			// ファイルパスを取得
			String fileName = decodedContextTranslated + contentsURI.replace(SEP, File.separator);

			// ファイルがない場合は、
			// contentsURIを「/WEB-INF/jsp/default/[pageName].jsp」に再設定
			if (!new File(fileName).exists()) {
				contentsURI = basePath + SEP + defaultModel + SEP + pageName + EXT;
			}
		}

		LOG.info("ContentsURI : " + contentsURI);

		// contentsURIが「/WEB-INF/jsp/index.jsp」のままならセッションフォームをクリア
		if (contentsURI.equals(basePath + SEP + defaultContents + EXT)) {
			SessionFormUtil.clearSessionForms(req);
		}

		// ページ内容を設定してforward
		req.setAttribute(ReqKey.CONTENTS_URI, contentsURI.replaceAll(basePath, "."));
		ServletContext sc = req.getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(template);
		rd.forward(req, resp);
	}

	/**
	 * 引数のURLにリダイレクトする
	 *
	 * @param resp
	 *            HttpServletResponse
	 * @param sendRedirect
	 *            リダイレクト先文字列
	 * @throws IOException
	 *             IOException
	 */
	public static void redirect(final HttpServletResponse resp, final String sendRedirect) throws IOException {
		LOG.info("redirect to [" + sendRedirect + "].");
		resp.sendRedirect(sendRedirect);
	}

	/**
	 * 当該処理中に採番された項目値を補完する
	 *
	 * @param model
	 *            モデル名
	 * @param propertyNames
	 *            補完する項目名リスト
	 * @param insertedValues
	 *            該当プロパティを登録済みのプロパティ値リスト
	 */
	private static void supply(final Model model, final Set<String> propertyNames,
			final Map<String, InsertedValue> insertedValues) {
		supply(model, propertyNames.toArray(new String[propertyNames.size()]), insertedValues);
	}

	/**
	 * 当該処理中に採番された項目値を補完する
	 *
	 * @param model
	 *            モデル名
	 * @param propertyNames
	 *            補完する項目名リスト
	 * @param insertedValues
	 *            該当プロパティを登録済みのプロパティ値リスト
	 */
	private static void supply(final Model model, final String[] propertyNames,
			final Map<String, InsertedValue> insertedValues) {

		if (propertyNames != null) {

			String modelName = model.getClass().getSimpleName();

			Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);
			Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);
			String[] primaryPropertyNameArray = primaryPropertyNames
					.toArray(new String[primaryPropertyNames.size() - 1]);
			String lastPrimaryPropertyName = primaryPropertyNameArray[primaryPropertyNames.size() - 1];

			// 補完項目名でループ
			for (String propertyName : propertyNames) {

				// 補完項目に値があればスキップ
				if (model.get(propertyName) != null) {
					continue;
				}

				// 補完項目がなければ登録済み主キー情報から設定
				InsertedValue insertedValue = insertedValues.get(propertyName);
				if (insertedValue != null && insertedValue.getValue() != null) {
					if (propertyMeis.containsKey(propertyName)) {
						// if (!insertedValue.modelName.equals(modelName)
						// || !propertyName.equals(lastPrimaryPropertyName)) {
						// // 同一モデルの最後の主キーなら補完しない
						if (!propertyName.equals(lastPrimaryPropertyName)) {
							// 最後の主キーなら補完しない
							model.set(propertyName, insertedValue.getValue());
						}
					}
				}
			}
		}
	}

	/*
	 * ************************************************************ インスタンスメソッド
	 */

	@Override
	public void init() throws ServletException {
		super.init();

		String basePath = this.getInitParameter(INIT_PARAM_BASE_PATH);
		if (StringUtil.isNotBlank(basePath)) {
			EmarfServlet.basePath = basePath;
		}

		String defaultContents = this.getInitParameter(INIT_PARAM_DEFAULT_CONTENTS);
		if (StringUtil.isNotBlank(defaultContents)) {
			EmarfServlet.defaultContents = defaultContents;
		}

		String template = this.getInitParameter(INIT_PARAM_TEMPLATE);
		if (StringUtil.isNotBlank(template)) {
			EmarfServlet.template = template;
		}

		String defaultModel = this.getInitParameter(INIT_PARAM_DEFAULT_MODEL);
		if (StringUtil.isNotBlank(defaultModel)) {
			EmarfServlet.defaultModel = defaultModel;
		}

		String staticResourceRegexp = this.getInitParameter(INIT_PARAM_STATIC_RESOURCE_REGEXP);
		if (StringUtil.isNotBlank(staticResourceRegexp)) {
			EmarfServlet.staticResourceRegexp = staticResourceRegexp;
		}

		EmarfServlet.actionPackage = this.getInitParameter(INIT_PARAM_ACTION_PACKAGE);
		if (StringUtil.isNotBlank(EmarfServlet.actionPackage)) {
			if (!EmarfServlet.actionPackage.endsWith(".")) {
				EmarfServlet.actionPackage += ".";
			}
		}

		// 出力パス取得
		String path = this.getClass().getResource(SEP).getPath();

		// model作成
		BeanGenerator.generate(path);

		// initial投入
		for (Entry<String, String> modelMei : MetaData.getModelMeis().entrySet()) {

			String modelName = modelMei.getKey();

			String filePath = "/initial/" + modelName + ".csv";

			InputStream is = this.getClass().getResourceAsStream(filePath);
			if (is == null) {
				continue;
			}

			LOG.info("initial data : " + filePath);

			// 一旦truncate
			Models.truncate(modelName);

			List<String> propertyNames = null;

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					LOG.trace("read line : " + line);
					List<String> columnNames = new ArrayList<String>();
					if (StringUtil.isNotEmpty(line)) {
						String[] columns = line.split("\",\"");
						for (String column : columns) {
							String[] columns2 = column.split(",");
							for (String column2 : columns2) {
								columnNames.add(column2);
							}
						}
					}
					if (propertyNames == null) {
						propertyNames = new ArrayList<String>();
						for (String columnName : columnNames) {
							String propertyName = StringUtil.toCamelCase(columnName.replaceAll("^\"|\"?,?$", ""));
							propertyNames.add(propertyName);
						}
					} else {
						Map<String, SessionProperty> propertyMap = new HashMap<String, SessionProperty>();
						for (int i = 0; i < columnNames.size(); i++) {
							String propertyName = propertyNames.get(i);
							String htmlName = modelName + "." + propertyName;
							String value = columnNames.get(i).replaceAll("^\"|\"?,?$", "");
							String[] values = StringUtil.toStringArray(value);
							SessionProperty sessionProperty = new SessionProperty(propertyName, htmlName, values);
							propertyMap.put(propertyName, sessionProperty);
						}
						SessionModel sessionModel = new SessionModel(modelName);
						sessionModel.getPropertyMap().putAll(propertyMap);
						Model model = SessionFormUtil.validate(Crud.CREATE, sessionModel, null);
						// deleteでなくtruncateがやっぱり必要
						// try {
						// Models.delete(model);
						// } catch (Exception e) {
						// LOG.debug(e.getMessage());
						// }
						Models.create(model);
					}
				}
			} catch (Exception e) {
				throw new SystemError(e);
			} finally {
				IOUtil.closeQuietly(br);
			}
		}
	}

	@Override
	public void destroy() {

		super.destroy();

		// サーブレットコンテキストから「jp.co.golorp.emarf.」で始まる属性をクリアする
		ServletContext sc = this.getServletContext();
		Enumeration<String> attributeNames = sc.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			if (attributeName.startsWith(CTX_KEY_PREFIX)) {
				LOG.debug("サーブレットコンテキストから" + attributeName + "を削除します。");
				sc.removeAttribute(attributeName);
			}
		}

		// ehcacheをクリアする
		CacheManager.getInstance().shutdown();
	}

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		// セッション日付管理用にIDを取得する
		DateUtil.setSessionId(req.getSession().getId());

		super.service(req, resp);
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		// HTTP1.0対応
		String method = RequestUtil.getPathMethodName(req);
		if (StringUtil.equalsIgnoreCase(method, METHOD_POST)) {
			this.doPost(req, resp);
			return;
		} else if (StringUtil.equalsIgnoreCase(method, METHOD_PUT)) {
			this.doPut(req, resp);
			return;
		} else if (StringUtil.equalsIgnoreCase(method, METHOD_DELETE)) {
			this.doDelete(req, resp);
			return;
		}

		LOG.info("------------------------------ doGet ------------------------------ " + req.getRequestURI());

		SessionForm sessionForm = SessionFormUtil.getSessionForm(req);

		if (invokeAction(req, resp, sessionForm)) {
			return;
		}

		// 一覧に戻った時に各単画面のSessionFormをクリアする
		// □ 一覧で検索（結果なし）
		// → 新規画面（なし条件表示）
		// → 一覧で条件なしで検索（結果なし）
		// → 新規画面 と遷移すると、
		// 前回のSessionFormが残ってしまっているため
		String pageName = RequestUtil.getPathPageName(req);
		if (pageName.equals(EmarfServlet.PAGE_INDEX)) {
			Map<String, SessionForm> sessionForms = SessionFormUtil.getSessionForms(req);
			if (sessionForms != null) {
				String requestURI = RequestUtil.getRequestURI(req);
				sessionForms.remove(requestURI + "new/");
				sessionForms.remove(requestURI + "edit/");
				sessionForms.remove(requestURI + "view/");
			}
		}

		// 一覧画面で検索結果が1件なら照会画面にリダイレクトする場合のロジック
		// String modelName = RequestUtils.getPathModelName(req);
		// if (StringUtils.isNotBlank(modelName) &&
		// pageName.equals(RequestUtils.DEFAULT_PAGE) && sessionForm != null) {
		// TableInfo tableInfo = MetaData.getTableInfo(modelName);
		// if (tableInfo != null) {
		// SessionModel formModel = sessionForm.getModel(modelName);
		// boolean isPK = true;
		// Set<String> primaryKeys = tableInfo.getPrimaryKeys();
		// for (String primaryKey : primaryKeys) {
		// String propertyName = StringUtils.toCamelCase(primaryKey);
		// if (formModel.getValues(propertyName) == null) {
		// isPK = false;
		// break;
		// }
		// }
		// if (isPK) {
		// Criteria c = null;
		// if (sessionForm != null) {
		// c = SessionFormUtils.form2Criteria(sessionForm, c);
		// }
		// if (Models.count(modelName, c) == 1) {
		// SessionFormUtils.setSessionForm(req, sessionForm,
		// INDEX_GETED_REDIRECT);
		// resp.sendRedirect(INDEX_GETED_REDIRECT);
		// return;
		// }
		// }
		// }
		// }

		String modelName = RequestUtil.getPathModelName(req);

		Model model = null;
		if (!pageName.equals(EmarfServlet.PAGE_INDEX) && sessionForm != null
				&& MetaData.getTableInfo(modelName) != null) {
			SessionModel sessionModel = sessionForm.getModel(modelName);
			model = SessionFormUtil.validate(Crud.REFER, sessionModel, null);
			model = Models.refer(model);
			if (model == null && (pageName.equals(EmarfServlet.PAGE_VIEW) || pageName.equals(EmarfServlet.PAGE_EDIT))) {
				throw new SystemError(MessageKeys.ERRORS_DATA_NONE);
			}
		}

		forward(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		// HTTP1.0対応
		String method = RequestUtil.getPathMethodName(req);
		if (StringUtil.equalsIgnoreCase(method, METHOD_GET)) {
			this.doGet(req, resp);
			return;
		} else if (StringUtil.equalsIgnoreCase(method, METHOD_PUT)) {
			this.doPut(req, resp);
			return;
		} else if (StringUtil.equalsIgnoreCase(method, METHOD_DELETE)) {
			this.doDelete(req, resp);
			return;
		}

		LOG.info("------------------------------ doPost:create ------------------------------ " + req.getRequestURI());

		// セッションフォームを取得（なければエラー）
		SessionForm sessionForm = SessionFormUtil.getSessionForm(req);
		checkNoParameters(sessionForm);

		// セッションフォームを一旦削除
		// エラー再描画の際にセッションフォームが残っているとキー情報で再検索してしまったり、
		// クリアした値が必須チェックエラーになった場合に再度表示される。
		// そうでなくてpost値を表示させたいから。
		SessionFormUtil.removeSessionForm(req);

		// アクションがあれば実行。リダイレクトされたなら終了。
		if (invokeAction(req, resp, sessionForm)) {
			return;
		}

		String pathModelName = RequestUtil.getPathModelName(req);

		String messageModelName = null;

		// 登録済み主キー情報（プロパティ名：値）
		Map<String, InsertedValue> insertedValues = new HashMap<String, InsertedValue>();

		// SessionFormでループ
		for (Entry<String, List<SessionModel>> models : sessionForm.entrySet()) {

			// モデル名とSessionModelリストを取得
			String modelName = models.getKey();
			List<SessionModel> sessionModels = models.getValue();

			// 主キー名のSet
			Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);

			// 補完項目名の配列
			String[] foreignPropertyNames = App.gets(AppKey.MODELS_FK_PREFIX + modelName);

			// SessionModelのリストでループ
			for (SessionModel sessionModel : sessionModels) {

				// VIEWのモデル化対応（Viewの時だけ複数回回る）
				Map<String, Model> modelMap = SessionFormUtil.validates(Crud.CREATE, sessionModel, pathModelName);
				if (modelMap != null) {
					for (Entry<String, Model> modelEntry : modelMap.entrySet()) {

						String tableName = modelEntry.getKey();

						if (messageModelName == null) {
							messageModelName = StringUtil.toUpperCamelCase(tableName);
						}

						Model model = modelEntry.getValue();
						if (model == null) {
							continue;
						}

						// 主キーの補完
						supply(model, primaryPropertyNames, insertedValues);

						// 補完項目の補完
						supply(model, foreignPropertyNames, insertedValues);

						// 新規登録
						model = Models.create(model);

						addHistory(model);

						// 主キー名でループ
						for (String primaryPropertyName : primaryPropertyNames) {

							// 主キー情報を退避
							Object value = model.get(primaryPropertyName);
							insertedValues.put(primaryPropertyName, new InsertedValue(modelName, value));

							// 主キー値をセッションフォームに格納
							if (sessionModel.get(primaryPropertyName) == null) {
								sessionModel.put(primaryPropertyName, value);
							} else if (StringUtil.isBlank(sessionModel.get(primaryPropertyName))) {
								Map<String, SessionProperty> propertyMap = sessionModel.getPropertyMap();
								SessionProperty sessionProperty = propertyMap.get(primaryPropertyName);
								for (Entry<String, String[]> sessionProperties : sessionProperty.entrySet()) {
									String htmlName = sessionProperties.getKey();
									sessionModel.put(primaryPropertyName, htmlName, value);
								}
							}
						}
					}
				}
			}
		}

		// indexかそれ以外かで画面遷移
		String pageName = RequestUtil.getPathPageName(req);
		if (pageName.equals(PAGE_INDEX)) {
			redirect(resp, "../");
		} else {
			SessionFormUtil.setSessionForm(req, sessionForm, POSTED_REDIRECT);
			redirect(resp, POSTED_REDIRECT);
		}

		// 完了メッセージを設定
		String modelMei = ModelUtil.getModelMei(messageModelName);
		String message = Message.get(MessageKeys.INFOS_INSERT, modelMei);
		Infos.addMessage(req, message, null);

		// 予約済みならメール送信
		RequestUtil.sendReservedMail(req);
	}

	@Override
	protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		LOG.info("------------------------------ doPut:update ------------------------------ " + req.getRequestURI());

		// セッションフォームを取得（なければエラー）
		SessionForm sessionForm = SessionFormUtil.getSessionForm(req);
		checkNoParameters(sessionForm);

		// セッションフォームを一旦削除
		SessionFormUtil.removeSessionForm(req);

		// アクションがあれば実行。リダイレクトされたなら終了。
		if (invokeAction(req, resp, sessionForm)) {
			return;
		}

		String pathModelName = RequestUtil.getPathModelName(req);

		String messageModelName = null;

		// 登録済み主キー情報（プロパティ名：値）
		Map<String, InsertedValue> insertedValues = new HashMap<String, InsertedValue>();

		// SessionFormでループ
		for (Entry<String, List<SessionModel>> models : sessionForm.entrySet()) {

			// モデル名とSessionModelリストを取得
			String modelName = models.getKey();
			List<SessionModel> sessionModels = models.getValue();

			// 主キー名のSet
			Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);

			// 補完項目名の配列
			String[] foreignPropertyNames = App.gets(AppKey.MODELS_FK_PREFIX + modelName);

			// SessionModelのリストでループ
			for (SessionModel sessionModel : sessionModels) {

				// VIEWのモデル化対応（Viewの時だけ複数回回る）
				Map<String, Model> modelMap = SessionFormUtil.validates(Crud.UPDATE, sessionModel, pathModelName);
				if (modelMap != null) {
					for (Entry<String, Model> modelEntry : modelMap.entrySet()) {

						String tableName = modelEntry.getKey();

						if (messageModelName == null) {
							messageModelName = StringUtil.toUpperCamelCase(tableName);
						}

						Model model = modelEntry.getValue();
						if (model == null) {
							continue;
						}

						// 主キーの補完
						supply(model, primaryPropertyNames, insertedValues);

						// 外部キーの補完
						supply(model, foreignPropertyNames, insertedValues);

						try {

							// 更新処理
							Models.update(model);

							addHistory(model);

						} catch (Exception e) {

							// 今回submitの主モデルでなければ新規追加
							if (model.getClass().getSimpleName().equals(pathModelName)) {
								throw e;
							}

							Models.create(model);

							addHistory(model);
						}

						// 主キー名でループ
						primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(model.getClass().getSimpleName());
						for (String primaryPropertyName : primaryPropertyNames) {

							// 主キー情報を退避
							Object value = model.get(primaryPropertyName);
							insertedValues.put(primaryPropertyName,
									new InsertedValue(model.getClass().getSimpleName(), value));

							// 主キー値をセッションフォームに格納
							if (StringUtil.isBlank(sessionModel.get(primaryPropertyName))) {
								sessionModel.put(primaryPropertyName, value);
							}
						}
					}
				}
			}
		}

		// indexかそれ以外かで画面遷移
		String pageName = RequestUtil.getPathPageName(req);
		if (pageName.equals(PAGE_INDEX)) {
			redirect(resp, "../");
		} else {
			SessionFormUtil.setSessionForm(req, sessionForm, PUTED_REDIRECT);
			redirect(resp, PUTED_REDIRECT);
		}

		// 完了メッセージを設定
		String modelMei = ModelUtil.getModelMei(messageModelName);
		String message = Message.get(MessageKeys.INFOS_UPDATE, modelMei);
		Infos.addMessage(req, message, null);

		// 予約済みならメール送信
		RequestUtil.sendReservedMail(req);
	}

	/**
	 * 履歴モデルを追加
	 *
	 * @param model
	 *            model
	 */
	private static void addHistory(final Model model) {

		RelateTablesMap relateTablesMap = ModelUtil.getHistoryBys(model.getClass().getSimpleName());

		if (relateTablesMap != null) {

			for (Entry<String, List<RelateColumnMap>> relateModels : relateTablesMap.entrySet()) {

				String historyModelName = relateModels.getKey();
				Model historyModel = ModelUtil.getBlankModel(historyModelName);
				historyModel.populate(model.getProperties());
				Models.create(historyModel);
			}
		}
	}

	@Override
	protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		LOG.info("------------------------------ doDelete ------------------------------ " + req.getRequestURI());

		// セッションフォームを取得（なければエラー）
		SessionForm sessionForm = SessionFormUtil.getSessionForm(req);

		checkNoParameters(sessionForm);

		// セッションフォームを一旦削除
		SessionFormUtil.removeSessionForm(req);

		// アクションを実行
		if (invokeAction(req, resp, sessionForm)) {
			return;
		}

		// セッションフォームでループ
		for (Entry<String, List<SessionModel>> models : sessionForm.entrySet()) {

			// モデル名
			String modelName = models.getKey();

			// セッションモデルリストでループ
			List<SessionModel> sessionModels = models.getValue();
			for (SessionModel sessionModel : sessionModels) {

				// セッションモデルが持つ主キーをモデルに設定
				Map<String, SessionProperty> propertyMap = sessionModel.getPropertyMap();

				// VIEWのモデル化対応（Viewの時だけ複数回回る）
				Map<String, Model> modelMap = SessionFormUtil.validates(Crud.DELETE, sessionModel, null);

				if (modelMap != null) {

					for (Entry<String, Model> modelEntry : modelMap.entrySet()) {

						Model model = modelEntry.getValue();
						if (model == null) {
							continue;
						}

						delete(model);

						// 全下位モデルでループ
						RelateTablesMap relateTablesMap = new RelateTablesMap();

						fillDescendants(relateTablesMap, modelName);

						for (Entry<String, List<RelateColumnMap>> relateTable : relateTablesMap.entrySet()) {
							// criteriaで削除対象の子孫モデルを取得してから削除

							String relateModelName = relateTable.getKey();

							Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(relateModelName);

							Criteria c = null;
							for (String primaryPropertyName : primaryPropertyNames) {
								if (propertyMap.containsKey(primaryPropertyName)) {
									SessionProperty sessionProperty = propertyMap.get(primaryPropertyName);
									if (c == null) {
										c = Criteria.equal(relateModelName, primaryPropertyName, sessionProperty.get());
									} else {
										c.eq(relateModelName, primaryPropertyName, sessionProperty.get());
									}
								}
							}

							List<Model> relateModels = Models.getModels(relateModelName, c);
							if (relateModels != null) {
								for (Model relateModel : relateModels) {
									delete(relateModel);
								}
							}
						}
					}
				}
			}
		}

		String modelName = RequestUtil.getPathModelName(req);

		String referer = req.getHeader("REFERER");
		if (referer != null && !referer.contains(PAGE_VIEW)) {

			// 一覧画面で削除した場合は一覧画面に戻る
			// referer=http://localhost:8081/emarf-blank/emarf/TAncestor/
			// request=http://localhost:8081/emarf-blank/emarf/TAncestor/view/delete

			redirect(resp, DELETED_REDIRECT);

		} else if (referer != null && referer.contains(modelName)) {

			// 単画面で削除した場合は一つ前の画面に戻る
			// referer=http://localhost:8081/emarf-blank/emarf/TParent/view/
			// request=http://localhost:8081/emarf-blank/emarf/TParent/view/delete

			String requestURI = RequestUtil.getRequestURI(req).replaceFirst("delete$", "");

			String redirect = DELETED_REDIRECT;
			Map<String, SessionForm> sessionForms = SessionFormUtil.getSessionForms(req);
			for (String uri : sessionForms.keySet()) {
				if (!uri.endsWith("/new/") && !uri.endsWith("/edit/")) {
					if (uri.equals(requestURI)) {
						break;
					}
					redirect = uri;
				}
			}

			redirect(resp, redirect);

		} else {

			// 単画面の明細で子モデルを削除した場合は単画面に戻る
			// referer=http://localhost:8081/emarf-blank/emarf/TParent/view/
			// request=http://localhost:8081/emarf-blank/emarf/TEntity/view/delete

			redirect(resp, referer);
		}

		// メッセージを設定
		String modelMei = ModelUtil.getModelMei(modelName);
		String message = Message.get(MessageKeys.INFOS_DELETE, modelMei);
		Infos.addMessage(req, message, null);

		// 予約したメールを送信
		RequestUtil.sendReservedMail(req);
	}

	/**
	 * 削除フラグがあれば論理削除。なければ物理削除
	 *
	 * @param model
	 *            model
	 */
	private static void delete(final Model model) {

		if (StringUtil.isNotBlank(BeanGenerator.DELETE_F)) {
			// deleteFの指定がある場合

			String modelName = model.getClass().getSimpleName();
			String deleteF = StringUtil.toCamelCase(BeanGenerator.DELETE_F);

			// 当該モデルにもDELETE_Fがある場合はUPDATE
			if (MetaData.getColumnInfo(modelName, deleteF) != null) {
				model.set(BeanGenerator.DELETE_F, "1");
				Models.update(model);
				return;
			}
		}

		// 削除処理
		// ステートメントが主キーを持ってのみ生成されるので、主キーが揃っているかチェックは必要ないと思う
		Models.delete(model);
	}

	/**
	 * @param relateTablesMap
	 *            relateTablesMap
	 * @param modelName
	 *            modelName
	 */
	private static void fillDescendants(final RelateTablesMap relateTablesMap, final String modelName) {

		RelateTablesMap brothers = ModelUtil.getBrothers(modelName);
		if (brothers != null) {
			relateTablesMap.putAll(brothers);
		}

		RelateTablesMap children = ModelUtil.getChildren(modelName);
		if (children != null) {
			relateTablesMap.putAll(children);
		}

		RelateTablesMap descendants = ModelUtil.getDescendants(modelName);
		if (descendants != null) {
			relateTablesMap.putAll(descendants);
		}

		RelateTablesMap historyBys = ModelUtil.getHistoryBys(modelName);
		if (historyBys != null) {
			relateTablesMap.putAll(historyBys);
		}

		RelateTablesMap summaryBys = ModelUtil.getSummaryBys(modelName);
		if (summaryBys != null) {
			relateTablesMap.putAll(summaryBys);
		}
	}

	/**
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param form
	 *            SessionForm
	 * @return Actionからリダイレクト先が指定されたか
	 */
	private boolean invokeAction(final HttpServletRequest req, final HttpServletResponse resp, final SessionForm form) {

		if (StringUtil.isBlank(actionPackage)) {
			return false;
		}

		// モデル名がなければ終了
		String modelName = RequestUtil.getPathModelName(req);
		if (modelName == null) {
			return false;
		}

		// アクションクラス名を取得
		String pageName = RequestUtil.getPathPageName(req);
		String methodName = RequestUtil.getPathMethodName(req);
		String className = actionPackage + modelName + StringUtil.toUpperCamelCase(pageName)
				+ StringUtil.toUpperCamelCase(methodName);

		// アクションクラスを取得
		Object o = null;
		try {
			o = Class.forName(className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			LOG.info("Action implements [" + className + "] is not exists.");
		}

		// アクションクラスを実行してリダイレクト先URIを取得
		String sendRedirect = null;
		if (o != null) {
			LOG.info(className + " start.");
			sendRedirect = ((Action) o).execute(new HttpServletContext(req, resp), form);
			LOG.info(className + " end.");
		}

		// リダイレクト先URIがあればリダイレクト
		if (sendRedirect != null) {
			try {
				redirect(resp, sendRedirect);
				return true;
			} catch (IOException e) {
				throw new SystemError(e);
			}
		}

		return false;
	}

	/**
	 * @param sessionForm
	 *            sessionForm
	 */
	private static void checkNoParameters(final SessionForm sessionForm) {

		boolean isParameters = false;

		if (sessionForm != null) {
			for (Entry<String, List<SessionModel>> model : sessionForm.entrySet()) {
				// String modelName = model.getKey();
				List<SessionModel> sessionModels = model.getValue();
				for (SessionModel sessionModel : sessionModels) {
					if (!sessionModel.get().equals("")) {
						isParameters = true;
						break;
					}
				}
				if (isParameters) {
					break;
				}
			}
		}

		if (!isParameters) {
			throw new ApplicationError(MessageKeys.ERRORS_PARAM_NONE);
		}
	}

}
