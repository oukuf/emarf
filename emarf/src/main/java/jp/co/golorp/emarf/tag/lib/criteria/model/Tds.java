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
package jp.co.golorp.emarf.tag.lib.criteria.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.constants.scope.ReqKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.Input;
import jp.co.golorp.emarf.tag.lib.body.model.Anchor;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.Td;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * モデルの全プロパティについてtdタグを出力
 *
 * @author oukuf@golorp
 */
public class Tds extends CriteriaTagSupport implements Modelable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

	/** 照会リンク表示有無 */
	private String cantView;

	/** 編集リンク表示有無 */
	private String cantEdit;

	/** 削除リンク表示有無 */
	private String cantDelete;

	/** 選択チェックボックス表示有無 */
	private String cantCheck;

	/** 編集可否 */
	private String edit;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param criteria
	 *            criteria
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @param cantView
	 *            照会リンク表示有無フラグ
	 * @param cantEdit
	 *            編集リンク表示有無フラグ
	 * @param cantDelete
	 *            削除ボタン表示有無フラグ
	 * @param cantCheck
	 *            選択チェックボックス表示有無フラグ
	 * @param edit
	 *            編集可否
	 * @param propertyNames
	 *            propertyNames
	 * @param htmlNamePrefix
	 *            htmlNamePrefix
	 * @param parentModelName
	 *            親モデル名
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit,
			final Iterable<String> propertyNames, final String htmlNamePrefix, final String parentModelName) {

		ServletRequest request = pageContext.getRequest();

		// tbodyタグで登録されているであろう行番号を、リクエストスコープから取得
		int no = 0;
		if (request.getAttribute(ReqKey.TBODY_NO) != null) {
			no = (int) request.getAttribute(ReqKey.TBODY_NO);
		}
		StringBuilder sb = new StringBuilder("<th class=\"tbodyNo\">").append(no).append("</th>");

		if (propertyNames != null) {
			for (String propertyName : propertyNames) {

				// 削除フラグなら表示しない
				if (StringUtil.equalsIgnoreCase(StringUtil.toCamelCase(BeanGenerator.DELETE_F), propertyName)) {
					continue;
				}

				String htmlName = htmlNamePrefix + "." + propertyName;

				if (Fieldset.AINT_SELECT_SUFFIX_SET.isEnd(propertyName)) {

					// // 検索条件にしない項目は一覧にも出さない
					// continue;

					// 検索条件にしない項目はhidden（update時の更新日時を想定）
					Object value = RequestUtil.lookup(pageContext.getRequest(), modelName, propertyName, htmlName);
					sb.append(Input.render(htmlName, value, null, pageContext, "hidden", null, null, null));

				} else {

					// tdタグを追加
					sb.append(Td.render(pageContext, modelName, criteria, optionModel, optionValue, optionLabel, edit,
							0, 0, null, propertyName, htmlName, parentModelName));
				}
			}
		}

		// 選択画面なら選択ボタンを追加して終了
		if (StringUtil.equals(RequestUtil.getPathPageName(request), EmarfServlet.PAGE_PICK)) {
			sb.append("<td><input type=\"button\" value=\"選択\" onclick=\"" + EmarfServlet.PAGE_PICK
					+ ".decide(this.parentElement.parentElement);\"></td>");
			return sb.toString();
		}

		if (edit) {

			// 編集可の場合は、行追加ボタン・行削除ボタンを出力
			sb.append("<td>");
			sb.append("<input type=\"button\" value=\"＋\" class=\"list_plus\">");
			sb.append("<input type=\"button\" value=\"－\" class=\"list_minus\">");
			sb.append("</td>");

		} else if (!cantView || !cantEdit || !cantDelete) {
			// 照会リンク・編集リンク・削除リンクの何れかを表示する場合

			sb.append("<td>");

			// 照会リンク
			if (!cantView) {
				sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, null, null)).append("照会</a>");
			}

			// 編集リンク
			if (!cantEdit) {
				sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_EDIT, null, null)).append("編集</a>");
			}

			// 削除ボタン
			if (!cantDelete) {
				sb.append(
						Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, EmarfServlet.METHOD_DELETE, null))
						.append("削除</a>");
			}

			sb.append("</td>");
		}

		// 選択不可でなく、主モデル用の一覧なら、チェックボックスを出力
		String pathModelName = RequestUtil.getPathModelName(request);
		if (!cantCheck && StringUtil.equals(pathModelName, modelName)) {

			sb.append("<td><input type=\"checkbox\" id=\"Check").append(htmlNamePrefix)
					.append("\" onclick=\"clickable.check(this)\"");

			Map<String, String> params = new LinkedHashMap<String, String>();
			Model model = (Model) request.getAttribute(modelName);
			Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);
			for (String propertyName : primaryPropertyNames) {
				if (model != null) {
					Object value = model.get(propertyName);
					if (value != null) {
						params.put(propertyName, String.valueOf(value));
					}
				}
			}

			sb.append(toDataParam(htmlNamePrefix, params)).append(" checked></td>");
		}

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.cantView = null;
		this.cantEdit = null;
		this.cantDelete = null;
		this.cantCheck = null;
		this.edit = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {

		boolean edit = StringUtil.is(this.edit);

		Iterable<String> iPropertyMeis = null;
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);
		if (propertyMeis != null) {
			iPropertyMeis = propertyMeis.keySet();
		}

		String htmlNamePrefix = Taglib.getHtmlName(this, null);

		this.prepareOptionAttributes();

		String parentModelName = Taglib.getParentAttribute(this, "parentModelName");

		boolean cantView = StringUtil.is(this.cantView);
		boolean cantEdit = StringUtil.is(this.cantEdit);
		boolean cantDelete = StringUtil.is(this.cantDelete);
		boolean cantCheck = StringUtil.is(this.cantCheck);

		// viewなら各リンクを不可で出力
		TableInfo tableInfo = MetaData.getTableInfo(this.modelName);
		if (tableInfo != null) {
			if (tableInfo.getTableType().equals("VIEW")) {
				cantView = true;
				cantEdit = true;
				cantDelete = true;
				cantCheck = true;
			}
		}

		// タグ文字列を出力
		return render(this.pageContext, this.modelName, this.criteria, this.optionModel, this.optionValue,
				this.optionLabel, cantView, cantEdit, cantDelete, cantCheck, edit, iPropertyMeis, htmlNamePrefix,
				parentModelName);
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
	 * 照会リンク表示有無を取得します。
	 *
	 * @return 照会リンク表示有無
	 */
	public String getCantView() {
		return cantView;
	}

	/**
	 * 照会リンク表示有無を設定します。
	 *
	 * @param cantView
	 *            照会リンク表示有無
	 */
	public void setCantView(final String cantView) {
		this.cantView = cantView;
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

	/**
	 * 選択チェックボックス表示有無を取得します。
	 *
	 * @return 選択チェックボックス表示有無
	 */
	public String getCantCheck() {
		return cantCheck;
	}

	/**
	 * 選択チェックボックス表示有無を設定します。
	 *
	 * @param cantCheck
	 *            選択チェックボックス表示有無
	 */
	public void setCantCheck(final String cantCheck) {
		this.cantCheck = cantCheck;
	}

	/**
	 * @return edit
	 */
	public String getEdit() {
		return edit;
	}

	/**
	 * @param edit
	 *            セットする edit
	 */
	public void setEdit(final String edit) {
		this.edit = edit;
	}

}
