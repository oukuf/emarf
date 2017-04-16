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
package jp.co.golorp.emarf.tag.lib.criteria.model.property;

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.Input;
import jp.co.golorp.emarf.tag.lib.base.model.property.Textarea;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * tdタグ
 *
 * @author oukuf@golorp
 */
public class Td extends CriteriaTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

	/** プロパティ名 */
	private String propertyName;

	/** rowspan */
	private String rowspan;

	/** colspan */
	private String colspan;

	/** 編集可否 */
	private String edit;

	/** width */
	private String width;

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
	 * @param rowspan
	 *            rowspan
	 * @param colspan
	 *            colspan
	 * @param width
	 *            width
	 * @param propertyName
	 *            propertyName
	 * @param htmlName
	 *            htmlName
	 * @param edit
	 *            編集可否
	 * @param parentModelName
	 *            親モデル名
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel, final boolean edit,
			final int rowspan, final int colspan, final String width, final String propertyName, final String htmlName,
			final String parentModelName) {

		ServletRequest request = pageContext.getRequest();

		StringBuilder sb = new StringBuilder("<td");

		if (rowspan > 0) {
			sb.append(" rowspan=" + rowspan);
		}

		if (colspan > 0) {
			sb.append(" colspan=" + colspan);
		}

		if (width != null) {
			sb.append(" width=" + width);
		}

		// モデルの主キー情報を取得
		boolean isPrimaryKey = false;
		if (StringUtil.isNotBlank(modelName)) {
			Set<String> primaryNames = ModelUtil.getPrimaryPropertyNames(modelName);
			if (primaryNames.contains(propertyName)) {
				isPrimaryKey = true;
			}
		}

		// 一覧画面での「＋」「－」ボタン表示判定を主キーかどうかでなくバージョンNOがあるかで判定するよう変更
		// if (isPrimaryKey) {
		// sb.append(" class=primarykey");
		// }

		sb.append(">");

		Object value = RequestUtil.lookup(request, modelName, propertyName, htmlName);

		Criteria clone = null;
		if (criteria != null) {
			clone = criteria.clone();
		}

		if (edit) {
			// TODO editの対応
			// TODO fieldsetにもほとんど同じ処理がある。共通化できんか？

			// 読み取り専用を評価
			boolean readonly = Taglib.isReadonly(request, modelName, propertyName);
			if (!readonly && StringUtil.isNotBlank(parentModelName)) {
				readonly = Taglib.isReadonly(request, parentModelName, propertyName);
			}

			// データ長
			TableInfo tableInfo = MetaData.getTableInfo(modelName);

			if (tableInfo != null) {

				ColumnInfo columnInfo = tableInfo.getColumnInfo(propertyName);
				String maxlength = String.valueOf(columnInfo.getColumnSize());

				if (Fieldset.CHECKS_SUFFIX_SET.isEnd(propertyName)) {

					// チェックボックスプロパティの場合
					Checks checks = new Checks(pageContext, modelName, propertyName, clone, optionModel, optionValue,
							optionLabel);
					sb.append(Checks.render(htmlName, value, readonly, checks.getCriteria(), optionModel, optionValue,
							optionLabel, false, false));

				} else if (Fieldset.RADIOS_SUFFIX_SET.isEnd(propertyName)) {

					// ラジオボタンプロパティの場合
					Radios radios = new Radios(pageContext, modelName, propertyName, clone, optionModel, optionValue,
							optionLabel);
					sb.append(Radios.render(htmlName, value, readonly, radios.getCriteria(), optionModel, optionValue,
							optionLabel));

				} else if (Fieldset.SELECT_SUFFIX_SET.isEnd(propertyName)) {

					// プルダウンリストプロパティの場合
					Select select = new Select(pageContext, modelName, propertyName, clone, optionModel, optionValue,
							optionLabel);
					sb.append(Select.render(htmlName, value, readonly, select.getCriteria(), optionModel, optionValue,
							optionLabel, null));

				} else if (Fieldset.TEXTAREA_SUFFIX_SET.isEnd(propertyName)) {

					// テキストエリアプロパティの場合
					sb.append(Textarea.render(htmlName, value, readonly));

				} else {

					String type = "text";
					if (Fieldset.PASSWORD_SUFFIX_SET.isEnd(propertyName)) {
						// パスワードプロパティの場合
						type = "password";
					} else if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName)) {
						// 日付プロパティの場合
						type = "date";
					} else if (Fieldset.DATETIME_SUFFIX_SET.isEnd(propertyName)) {
						// 日時プロパティの場合
						type = "datetime";
					} else if (Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {
						// 時間プロパティの場合
						type = "time";
					}

					sb.append(Input.render(htmlName, value, readonly, pageContext, type, maxlength, null, null));
				}

			}

		} else {

			if (ModelUtil.isOptionable(propertyName) && clone != null) {
				// 選択項目の場合

				// Span.callRender()内でprepareOptionAttributes()するよう変更
				// String optionModelName = optionModel;
				// if (StringUtil.isBlank(optionModel)) {
				// optionModelName = OPTION_MODEL_DEFAULT;
				// }
				//
				// String optionValueName = optionValue;
				// if (StringUtil.isBlank(optionValue)) {
				// optionValueName = OPTION_VALUE_DEFAULT;
				// }
				//
				// String optionLabelName = optionLabel;
				// if (StringUtil.isBlank(optionLabel)) {
				// optionLabelName = OPTION_LABEL_DEFAULT;
				// }

				sb.append(new Span(pageContext, modelName, propertyName, clone, optionModel, optionValue, optionLabel));

			} else {
				// 入力項目の場合

				sb.append(Span.render(modelName, propertyName, htmlName, value));
			}

			if (isPrimaryKey) {
				sb.append(Input.render(htmlName, value, null, pageContext, "hidden", null, null, null));
			}
		}

		sb.append("</td>");

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */
	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		this.rowspan = null;
		this.colspan = null;
		this.edit = null;
		this.width = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {

		String htmlName = Taglib.getHtmlName(this, this.propertyName);

		boolean edit = false;
		if (this.edit != null) {
			edit = StringUtil.is(this.edit);
		} else {
			edit = StringUtil.is(Taglib.getParentAttribute(this, "edit"));
		}

		int rowspan = 0;
		if (StringUtil.isNotBlank(this.rowspan)) {
			rowspan = Integer.parseInt(this.rowspan);
		}

		int colspan = 0;
		if (StringUtil.isNotBlank(this.colspan)) {
			colspan = Integer.parseInt(this.colspan);
		}

		String parentModelName = Taglib.getParentAttribute(this, "parentModelName");

		prepareOptionAttributes();

		return render(this.pageContext, this.modelName, this.criteria, this.optionModel, this.optionValue,
				this.optionLabel, edit, rowspan, colspan, this.width, this.propertyName, htmlName, parentModelName);
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

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return rowspan
	 */
	public String getRowspan() {
		return rowspan;
	}

	/**
	 * @param rowspan
	 *            rowspan
	 */
	public void setRowspan(final String rowspan) {
		this.rowspan = rowspan;
	}

	/**
	 * @return colspan
	 */
	public String getColspan() {
		return colspan;
	}

	/**
	 * @param colspan
	 *            colspan
	 */
	public void setColspan(final String colspan) {
		this.colspan = colspan;
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

	/**
	 * @return width
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            セットする width
	 */
	public void setWidth(final String width) {
		this.width = width;
	}

}
