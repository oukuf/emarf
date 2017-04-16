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
package jp.co.golorp.emarf.tag.lib.body.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.constants.scope.CtxKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BodyTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * リンクタグ
 *
 * @author oukuf@golorp
 */
public class Anchor extends BodyTagSupport implements Modelable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/** pageName */
	private String pageName;

	/** methodName */
	private String methodName;

	/***/
	private String dataParams;

	/** 編集リンク表示有無 */
	private String cantEdit;

	/** 削除リンク表示有無 */
	private String cantDelete;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param pageName
	 *            pageName
	 * @param methodName
	 *            methodName
	 * @param dataParams
	 *            dataParams{ 'A':'1', 'B':'2' }
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final String pageName,
			final String methodName, final String dataParams) {

		ServletRequest request = pageContext.getRequest();

		/*
		 * hrefの出力
		 */

		// モデル名までのリンクタグを作成
		StringBuffer sb = new StringBuffer("<a href=\"").append(RequestUtil.getContextServletPath(request)).append(SEP)
				.append(modelName).append(SEP);

		// 検索画面でなければページ名を付加
		if (!StringUtil.equals(pageName, EmarfServlet.PAGE_INDEX)) {
			sb.append(pageName).append(SEP);

			// メソッド名があれば付加
			if (StringUtil.isNotBlank(methodName)) {
				sb.append(methodName);
			}
		}

		sb.append("\"");

		/*
		 * リンク引数の設定
		 */

		// リンク引数退避先
		Map<String, String> dataParamMap = new LinkedHashMap<String, String>();

		// リクエストのモデル情報
		Model model = (Model) request.getAttribute(modelName);

		// セッションフォームのモデル情報
		SessionModel sessionModel = null;
		SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
		if (sessionForm != null) {
			sessionModel = sessionForm.getModel(modelName);
		}

		// ログイン状態判定用セッション変数キーのリスト
		ServletContext servletContext = pageContext.getServletContext();
		Object loginKeysObject = servletContext.getAttribute(CtxKey.LOGIN_KEYS);
		@SuppressWarnings("unchecked")
		List<String> loginKeys = (List<String>) loginKeysObject;

		// モデルの主キー値でループ
		Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);
		for (String propertyName : primaryPropertyNames) {

			// リクエストから取得してみる
			if (model != null) {
				String value = model.getString(propertyName);
				if (value != null) {
					dataParamMap.put(propertyName, value);
					continue;
				}
			}

			// セッションフォームから取得してみる
			if (sessionModel != null) {
				String value = sessionModel.get(propertyName);
				if (value != null) {
					dataParamMap.put(propertyName, value);
					continue;
				}
			}

			// ログイン情報から取得してみる
			if (loginKeys != null) {
				boolean b = false;
				for (String loginKey : loginKeys) {
					Model loginModel = (Model) pageContext.getSession().getAttribute(loginKey);
					if (loginModel == null) {
						continue;
					}
					Object o = loginModel.get(propertyName);
					if (o == null) {
						continue;
					}
					dataParamMap.put(propertyName, o.toString());
					b = true;
					break;
				}
				if (b) {
					continue;
				}
			}
		}

		// versionNo
		if (StringUtil.isNotBlank(methodName) && StringUtil.equalsIgnoreCase(methodName, EmarfServlet.METHOD_DELETE)) {
			// メソッド名がDELETEの場合

			// リクエストから取得してみる
			if (model != null) {
				if (StringUtil.isNotBlank(BeanGenerator.VERSION_NO)) {
					String propertyName = StringUtil.toCamelCase(BeanGenerator.VERSION_NO);
					String value = model.getString(propertyName);
					if (value != null) {
						dataParamMap.put(propertyName, value);
					}
				}
			}
		}

		if (dataParams != null) {
			// もともとdataParamsの指定があった場合

			// dataParams文字列を要素ごとに分解
			String[] dataParamPieces = dataParams.split("\\{|,|\\}");
			for (String dataParamPiece : dataParamPieces) {
				if (dataParamPiece.equals("")) {
					continue;
				}

				// キーと値を取得してdataParamMapに追加
				String[] keyValue = dataParamPiece.split(":");
				String key = keyValue[0].trim().replaceAll("\"|'", "");
				String value = keyValue[1].trim().replaceAll("\"|'", "");
				dataParamMap.put(key, value);
			}
		}

		// dataParamMapを直列化してタグ文字列に追加
		sb.append(toDataParam(modelName, dataParamMap));

		sb.append(">");

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.pageName = null;
		this.methodName = null;
		this.dataParams = null;
		this.cantEdit = null;
		this.cantDelete = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		if (!cant()) {
			return render(this.pageContext, this.modelName, this.pageName, this.methodName, this.dataParams);
		}
		return null;
	}

	@Override
	public String doEnd() throws JspException {
		if (!cant()) {
			return "</a>";
		}
		return null;
	}

	/**
	 * @return boolean
	 */
	private boolean cant() {

		if (this.cantEdit == null || this.cantDelete == null) {

			// セッションフォームから取得してみる
			ServletRequest request = this.pageContext.getRequest();
			SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
			if (sessionForm != null) {

				// TODO getはよくないかもしれない
				SessionModel sessionModel = sessionForm.getModel(this.modelName);

				if (this.cantEdit == null) {
					String value = sessionModel.get("cantEdit");
					if (value != null) {
						this.cantEdit = value;
					}
				}

				if (this.cantDelete == null) {
					String value = sessionModel.get("cantDelete");
					if (value != null) {
						this.cantDelete = value;
					}
				}
			}
		}

		boolean isEditPage = StringUtil.equals(this.pageName, EmarfServlet.PAGE_EDIT);
		boolean isDeleteMethod = this.methodName != null
				&& this.methodName.equalsIgnoreCase(EmarfServlet.METHOD_DELETE);

		return (isEditPage && StringUtil.is(this.cantEdit)) || (isDeleteMethod && StringUtil.is(this.cantDelete));
	}

	/*
	 * ************************************************** アクセサ
	 */

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * pageName.を取得します。
	 *
	 * @return pageName
	 */
	public final String getPageName() {
		return this.pageName;
	}

	/**
	 * pageName.を設定します。
	 *
	 * @param pageName
	 *            pageName
	 */
	public final void setPageName(final String pageName) {
		this.pageName = pageName;
	}

	/**
	 * methodNameを取得します。
	 *
	 * @return methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * methodNameを設定します。
	 *
	 * @param methodName
	 *            methodName
	 */
	public void setMethodName(final String methodName) {
		this.methodName = methodName;
	}

	/**
	 * dataParamsを取得します。
	 *
	 * @return dataParams
	 */
	public String getDataParams() {
		return dataParams;
	}

	/**
	 * dataParamsを設定します。
	 *
	 * @param dataParams
	 *            dataParams
	 */
	public void setDataParams(final String dataParams) {
		this.dataParams = dataParams;
	}

	/**
	 * 編集リンク表示有無を取得します。
	 *
	 * @return 編集リンク表示有無
	 */
	public String getCantEdit() {
		return cantEdit;
	}

	/**
	 * 編集リンク表示有無を設定します。
	 *
	 * @param cantEdit
	 *            編集リンク表示有無
	 */
	public void setCantEdit(final String cantEdit) {
		this.cantEdit = cantEdit;
	}

	/**
	 * 削除リンク表示有無を取得します。
	 *
	 * @return 削除リンク表示有無
	 */
	public String getCantDelete() {
		return cantDelete;
	}

	/**
	 * 削除リンク表示有無を設定します。
	 *
	 * @param cantDelete
	 *            削除リンク表示有無
	 */
	public void setCantDelete(final String cantDelete) {
		this.cantDelete = cantDelete;
	}

}
