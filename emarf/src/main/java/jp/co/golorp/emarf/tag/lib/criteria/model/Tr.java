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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.Ths;
import jp.co.golorp.emarf.tag.lib.iterate.model.Tbody;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * trタグ
 *
 * @author oukuf@golorp
 */
public class Tr extends CriteriaTagSupport implements Modelable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
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
	 * @param cantView
	 *            cantView
	 * @param cantEdit
	 *            cantEdit
	 * @param cantDelete
	 *            cantDelete
	 * @param cantCheck
	 *            cantCheck
	 * @param edit
	 *            編集可否
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit) {

		boolean cantview = cantView;
		boolean cantedit = cantEdit;
		boolean cantdelete = cantDelete;
		boolean cantcheck = cantCheck;

		// viewなら各フラグ文字列を「true」に上書き
		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo != null) {
			if (tableInfo.getTableType().equals("VIEW")) {
				cantview = true;
				cantedit = true;
				cantdelete = true;
				cantcheck = true;
			}
		}

		StringBuilder sb = new StringBuilder("<tr>");
		sb.append(Ths.render(pageContext, modelName, cantview, cantedit, cantdelete, cantcheck, edit));
		sb.append("</tr>");

		return sb.toString();
	}

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param criteria
	 *            criteria（Tdsで使用）
	 * @param optionModel
	 *            optionModel（Tdsで使用）
	 * @param optionValue
	 *            optionValue（Tdsで使用）
	 * @param optionLabel
	 *            optionLabel（Tdsで使用）
	 * @param cantView
	 *            cantView
	 * @param cantEdit
	 *            cantEdit
	 * @param cantDelete
	 *            cantDelete
	 * @param cantCheck
	 *            cantCheck
	 * @param edit
	 *            編集可否（Tdsで使用）
	 * @param propertyNames
	 *            propertyNames（Tdsで使用）
	 * @param htmlNamePrefix
	 *            htmlNamePrefix（Tdsで使用）
	 * @param parentModelName
	 *            親モデル名
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit,
			final Iterable<String> propertyNames, final String htmlNamePrefix, final String parentModelName) {

		boolean cantview = cantView;
		boolean cantedit = cantEdit;
		boolean cantdelete = cantDelete;
		boolean cantcheck = cantCheck;

		// viewなら各フラグ文字列を「true」に上書き
		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo != null) {
			if (tableInfo.getTableType().equals("VIEW")) {
				cantview = true;
				cantedit = true;
				cantdelete = true;
				cantcheck = true;
			}
		}

		StringBuilder sb = new StringBuilder("<tr>");
		sb.append(Tds.render(pageContext, modelName, criteria, optionModel, optionValue, optionLabel, cantview,
				cantedit, cantdelete, cantcheck, edit, propertyNames, htmlNamePrefix, parentModelName));
		sb.append("</tr>");

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

		// boolean isNew = false;

		boolean cantView = StringUtil.is(this.cantView);
		boolean cantEdit = StringUtil.is(this.cantEdit);
		boolean cantDelete = StringUtil.is(this.cantDelete);
		boolean cantCheck = StringUtil.is(this.cantCheck);
		boolean edit = StringUtil.is(this.edit);

		// tbody内のtrタグか
		boolean isTbody = this.getParent() instanceof Tbody;
		if (!isTbody) {
			// String count = TagUtils.getParentAttribute(this, "count");
			// if (count != null) {
			// isNew = Integer.valueOf(count) == 0;
			// }
			return render(this.pageContext, this.modelName, cantView, cantEdit, cantDelete, cantCheck, edit);
		} else {
			Iterable<String> propertyNames = ModelUtil.getPropertyMeis(this.modelName).keySet();
			String htmlNamePrefix = Taglib.getHtmlName(this, null);
			this.prepareOptionAttributes();
			String parentModelName = Taglib.getParentAttribute(this, "parentModelName");
			return render(this.pageContext, this.modelName, this.criteria, this.optionModel, this.optionValue,
					this.optionLabel, cantView, cantEdit, cantDelete, cantCheck, edit, propertyNames, htmlNamePrefix,
					parentModelName);
		}
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
