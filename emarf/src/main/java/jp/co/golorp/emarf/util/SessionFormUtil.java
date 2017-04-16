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

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.model.Crud;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.servlet.http.form.SessionProperty;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.sql.info.ViewInfo;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.Checks;

/**
 * セッションフォーム操作用ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class SessionFormUtil {

	/***/
	private static final Logger LOG = LoggerFactory.getLogger(SessionFormUtil.class);

	/** セッションキー文字列 */
	private static final String SESSION_FORMS = "jp.co.golorp.emarf.session_forms";

	/**
	 * コンストラクタ
	 */
	private SessionFormUtil() {
	}

	/**
	 * @param request
	 *            request
	 * @return 全てのセッションフォーム
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, SessionForm> getSessionForms(final ServletRequest request) {

		HttpSession session = ((HttpServletRequest) request).getSession();
		if (session == null) {
			return null;
		}

		Object o = session.getAttribute(SESSION_FORMS);
		if (o == null) {
			return null;
		}

		return (Map<String, SessionForm>) o;
	}

	/**
	 * セッションスコープにセッションフォームを保管
	 *
	 * @param request
	 *            request
	 * @param sessionForms
	 *            sessionForms
	 */
	public static void setSessionForms(final ServletRequest request, final Map<String, SessionForm> sessionForms) {
		(((HttpServletRequest) request).getSession()).setAttribute(SESSION_FORMS, sessionForms);
	}

	/**
	 * 全てのセッションフォームを削除する
	 *
	 * @param request
	 *            request
	 */
	public static void clearSessionForms(final ServletRequest request) {
		((HttpServletRequest) request).getSession().removeAttribute(SESSION_FORMS);
	}

	/**
	 * RequestURIに該当するセッションフォームを削除する
	 *
	 * @param request
	 *            request
	 */
	public static void removeSessionForm(final ServletRequest request) {

		Map<String, SessionForm> sessionForms = getSessionForms(request);
		if (sessionForms == null) {
			return;
		}

		sessionForms.remove(RequestUtil.getRequestURI(request));
	}

	/**
	 * RequestURIに合致するSessionFormを取得
	 *
	 * @param request
	 *            request
	 * @return Form
	 */
	public static SessionForm getSessionForm(final ServletRequest request) {

		Map<String, SessionForm> sessionForms = getSessionForms(request);
		if (sessionForms == null) {
			return null;
		}

		SessionForm sessionForm = sessionForms.get(RequestUtil.getRequestURI(request));

		if (sessionForm == null) {
			String pathPageName = RequestUtil.getPathPageName(request);
			if (pathPageName.equals("index")) {
				String pathMethodName = RequestUtil.getPathMethodName(request);
				String requestURI = RequestUtil.getRequestURI(request);
				requestURI = requestURI.replaceAll(pathMethodName + "$", "");
				requestURI = requestURI.replaceAll(pathPageName + "\\/$", "");
				sessionForm = sessionForms.get(requestURI);
			}
		}

		return sessionForm;
	}

	/**
	 * @param request
	 *            request
	 * @param sessionForm
	 *            sessionForm
	 */
	public static void setSessionForm(final ServletRequest request, final SessionForm sessionForm) {
		setSessionForm(request, sessionForm, null);
	}

	/**
	 * @param request
	 *            request
	 * @param sessionForm
	 *            sessionForm
	 * @param relativePath
	 *            セッションフォームを保管する、requestURIからの相対パス
	 */
	public static void setSessionForm(final ServletRequest request, final SessionForm sessionForm,
			final String relativePath) {

		Map<String, SessionForm> sessionForms = getSessionForms(request);
		if (sessionForms == null) {
			sessionForms = new TreeMap<String, SessionForm>();
		}

		String requestURI = RequestUtil.getRequestURI(request);

		if (StringUtil.isNotBlank(relativePath)) {
			try {
				URI base = new URI(requestURI);
				URI uri = new URI(relativePath);
				URI res = base.resolve(uri);
				sessionForms.put(res.getPath(), sessionForm);
			} catch (URISyntaxException e) {
				throw new SystemError(e);
			}
		} else {
			sessionForms.put(requestURI, sessionForm);
		}

		SessionFormUtil.setSessionForms(request, sessionForms);
	}

	/**
	 * @param request
	 *            request
	 * @param htmlName
	 *            htmlName
	 * @return htmlNameに合致するセッションフォームの値
	 */
	public static String[] getValues(final ServletRequest request, final String htmlName) {

		// 今回のセッションフォームを取得
		SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
		if (sessionForm == null) {
			return null;
		}

		return sessionForm.getValues(htmlName);
	}

	/**
	 * Viewなど本当のモデルとして入力チェックを行う
	 *
	 * @param crud
	 *            入力チェックのモード
	 * @param sessionModel
	 *            SessionModel
	 * @param pathModelName
	 *            pathModelName
	 * @return プロパティ検証済みのモデル
	 */
	public static Map<String, Model> validates(final int crud, final SessionModel sessionModel,
			final String pathModelName) {

		String allValues = sessionModel.get();
		if (StringUtil.isBlank(allValues)) {
			return null;
		}

		final String modelName = sessionModel.getModelName();
		final Map<String, SessionProperty> propertyMap = sessionModel.getPropertyMap();

		Map<String, Model> modelMap = new HashMap<String, Model>();

		// テーブル情報を取得
		TableInfo tableInfo = MetaData.getTableInfo(modelName);

		// FIXME UnitTest用にNULL避け追加
		if (tableInfo == null) {
			return null;
		}

		// ビュー情報がなければ単一モデルとして入力チェック後に結果返却
		if (!tableInfo.isView()) {
			LOG.trace("validate " + modelName + " as table model.");
			Model model = validate(crud, sessionModel, pathModelName);
			if (model != null) {
				modelMap.put(tableInfo.getTableName(), model);
			}
			return modelMap;
		}

		/*
		 * ビュー情報がある場合
		 */

		LOG.trace("validate " + modelName + " as view model.");

		// プロパティでループしてテーブル別名ごとに実テーブル名のモデル名とSessionModelを再構築
		Map<String, Map<String, SessionModel>> sessionModelAsNameMap = new HashMap<String, Map<String, SessionModel>>();

		for (Entry<String, SessionProperty> property : propertyMap.entrySet()) {
			String propertyName = property.getKey();
			SessionProperty sessionProperty = property.getValue();

			ViewInfo viewInfo = tableInfo.getViewInfo(propertyName);
			if (viewInfo != null) {

				LOG.trace("view property : " + propertyName);
				LOG.trace("    TableName    :" + viewInfo.getTableName());
				LOG.trace("    ColumnName   :" + viewInfo.getColumnName());
				LOG.trace("    OrgTableName :" + viewInfo.getOrgTableName());
				LOG.trace("    OrgColumnName:" + viewInfo.getOrgColumnName());

				String asTableName = viewInfo.getTableName();
				Map<String, SessionModel> sessionModelMap = sessionModelAsNameMap.get(asTableName);
				if (sessionModelMap == null) {
					sessionModelMap = new HashMap<String, SessionModel>();
					sessionModelAsNameMap.put(asTableName, sessionModelMap);
				}

				String orgTableName = viewInfo.getOrgTableName();
				String nodelName = StringUtil.toUpperCamelCase(orgTableName);
				SessionModel sessionNodel = sessionModelMap.get(nodelName);
				if (sessionNodel == null) {
					sessionNodel = new SessionModel(nodelName);
					sessionModelMap.put(nodelName, sessionNodel);
				}

				String orgPropertyName = StringUtil.toCamelCase(viewInfo.getOrgColumnName());
				sessionNodel.put(orgPropertyName, sessionProperty);
			}
		}

		for (Entry<String, Map<String, SessionModel>> sessionModelAsName : sessionModelAsNameMap.entrySet()) {
			String asTableName = sessionModelAsName.getKey();
			Map<String, SessionModel> sessionModelMap = sessionModelAsName.getValue();
			for (SessionModel sessionNodel : sessionModelMap.values()) {
				Model model = validate(crud, sessionNodel, pathModelName);
				modelMap.put(asTableName, model);
			}
		}

		return modelMap;
	}

	/**
	 * Viewなどそのままのモデルとして入力チェックを行う
	 *
	 * @param crud
	 *            入力チェックのモード
	 * @param sessionModel
	 *            SessionModel
	 * @param pathModelName
	 *            pathModelName
	 * @return プロパティ検証済みのモデル
	 */
	public static Model validate(final int crud, final SessionModel sessionModel, final String pathModelName) {

		// 新規登録なら全プロパティ値格納
		if (crud == Crud.CREATE) {
			appendAll(sessionModel);
		}

		String modelName = sessionModel.getModelName();
		LOG.trace("validate " + modelName + ".");

		// モデルインスタンスを取得
		Model model = ModelUtil.getBlankModel(modelName);
		if (model == null) {
			return null;
		}

		// validateエラーを初期化
		List<ApplicationError> errors = null;

		// 当該モデルの主キー情報を取得
		Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);

		// プロパティでループ
		for (Entry<String, SessionProperty> property : sessionModel.entrySet()) {
			String propertyName = property.getKey();
			SessionProperty sessionProperty = property.getValue();

			// 検証用セッターメソッドを取得。なければスキップ。
			Method validator = getValidator(model, propertyName);
			if (validator == null) {
				continue;
			}

			for (Entry<String, String[]> html : sessionProperty.entrySet()) {
				String htmlName = html.getKey();
				String[] values = html.getValue();

				// 配列モデルで全て入力無しの場合は終了
				if (htmlName.matches("[^\\.]+\\[\\d+\\]\\..+")) {
					String allValue = sessionModel.get();
					if (allValue.equals("")) {
						return null;
					}
				}

				for (String value : values) {

					// プロパティ値が空文字ならnullに変換 → 消し込みの場合もあるのでボツ
					// if (value != null && value.equals("")) { value = null; }
					try {
						validator.invoke(model, value.replaceAll("^" + Checks.NOCHECK_VALUE, ""));
						LOG.trace("    " + modelName + "." + propertyName + " is valid. value is [" + value + "].");
					} catch (Exception e) {

						/*
						 * 登録処理の場合
						 */

						if (Crud.isCreate(crud)) {
							if (primaryPropertyNames.contains(propertyName) && (propertyName.endsWith(Models.ID_SUFFIX)
									|| propertyName.endsWith(Models.SEQ_SUFFIX))) {
								// 補完予定の主キー項目ならスキップ
								continue;
							} else if (Models.AINT_INSERT_SET.isEnd(propertyName)) {
								// 非表示の項目ならスキップ
								continue;
							} else if (Models.AUTO_INSERT_MAP.containsKey(propertyName)) {
								// レコード登録日・レコード更新日など、システム付加項目ならスキップ
								continue;
							} else if (Fieldset.CANT_INSERT_SUFFIX_SET.isEnd(propertyName)) {
								// 非活性の項目ならスキップ
								continue;
							}
						}

						/*
						 * 照会処理の場合
						 */
						if (Crud.isRefer(crud)) {
							// 検索画面の入力エラーが拾えないのでコメントアウト
							// // 主キー項目以外ならスキップ
							// if (!primaryPropertyNames.contains(propertyName))
							// { continue; }
							if (Models.AUTO_INSERT_MAP.containsKey(propertyName)) {
								// レコード登録日・レコード更新日など、システム付加項目ならスキップ
								// （新規登録後の照会処理でエラーになってしまうため）
								continue;
							}
						}

						/*
						 * 更新処理の場合
						 */
						if (Crud.isUpdate(crud)) {
							if (Models.AINT_UPDATE_SET.isEnd(propertyName)) {
								// 非表示の項目ならスキップ
								continue;
							} else if (Models.AUTO_UPDATE_MAP.containsKey(propertyName)) {
								// レコード登録日・レコード更新日など、システム付加項目ならスキップ
								continue;
							} else if (Fieldset.CANT_UPDATE_SUFFIX_SET.isEnd(propertyName)) {
								// 非活性の項目ならスキップ
								continue;
							}
							if (!StringUtil.equals(modelName, pathModelName)) {
								// 主モデルでない場合
								if (primaryPropertyNames.contains(propertyName)
										&& (propertyName.endsWith(Models.ID_SUFFIX)
												|| propertyName.endsWith(Models.SEQ_SUFFIX))) {
									// 補完予定の主キー項目ならスキップ
									continue;
								}
							}
						}

						/*
						 * 上記以外の場合
						 */
						if (e.getCause() instanceof ApplicationError) {
							// validateエラーであるならアプリ例外を追加

							if (errors == null) {
								errors = new ArrayList<ApplicationError>();
							}

							ApplicationError ex = (ApplicationError) e.getCause();
							LOG.trace("    " + modelName + "." + propertyName + " is not valid. value is [" + value
									+ "].\n" + ex.getMessage());
							ex.setHtmlName(htmlName);
							errors.add(ex);

						} else {
							// validateエラーでないならシステム例外を発効
							throw new SystemError(e);
						}
					}
				}
			}
		}

		// アプリ例外があるなら発効
		if (errors != null) {
			throw new ApplicationError(errors);
		}

		return model;
	}

	/**
	 * @param sessionModel
	 *            sessionModel
	 */
	private static void appendAll(final SessionModel sessionModel) {

		String modelName = sessionModel.getModelName();

		Map<String, SessionProperty> propertyMap = sessionModel.getPropertyMap();

		// プロパティ名でループ
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);
		if (propertyMeis != null) {
			for (String propertyName : propertyMeis.keySet()) {

				// sessionValueを取得。
				SessionProperty sessionProperty = propertyMap.get(propertyName);

				// なければ作成。
				if (sessionProperty == null) {
					String htmlName = modelName + "." + propertyName;
					String[] values = StringUtil.toStringArray("");
					sessionProperty = new SessionProperty(propertyName, htmlName, values);
				}

				// 検証用の全プロパティ値格納用マップに退避
				propertyMap.put(propertyName, sessionProperty);
			}
		}
	}

	/**
	 * @param model
	 *            model
	 * @param propertyName
	 *            propertyName
	 * @return Method
	 */
	private static Method getValidator(final Model model, final String propertyName) {

		// セッター名を生成
		String setterName = "validate" + StringUtil.toUpperCamelCase(propertyName);

		// メソッド名でループ
		Class<?> clazz = model.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (setterName.equals(method.getName())) {
				return method;
			}
		}

		return null;
	}

}
