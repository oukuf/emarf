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

import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.collection.AppSet;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.Input;
import jp.co.golorp.emarf.tag.lib.base.model.property.InputField;
import jp.co.golorp.emarf.tag.lib.base.model.property.Legend;
import jp.co.golorp.emarf.tag.lib.base.model.property.TextareaField;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.ChecksField;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.RadiosField;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.SelectField;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.SpanField;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * モデルのメタ情報から入力フォームを生成する
 *
 * @author oukuf@golorp
 */
public class Fieldset extends CriteriaTagSupport implements Modelable {

	/** 検索画面で非表示にするサフィックス */
	public static final AppSet<String> AINT_SELECT_SUFFIX_SET = App.getSet(AppKey.FIELDSET_AINT_SELECT_SUFFIXS);

	/** 登録画面で非活性にするサフィックス */
	public static final AppSet<String> CANT_INSERT_SUFFIX_SET = App.getSet(AppKey.FIELDSET_CANT_INSERT_SUFFIXS);

	/** 更新画面で非活性にするサフィックス */
	public static final AppSet<String> CANT_UPDATE_SUFFIX_SET = App.getSet(AppKey.FIELDSET_CANT_UPDATE_SUFFIXS);

	/** チェックボックス項目のサフィックス */
	public static final AppSet<String> CHECKS_SUFFIX_SET = App.getSet(AppKey.FIELDSET_CHECKS_SUFFIXS);

	/** 日付項目のサフィックス */
	public static final AppSet<String> DATE_SUFFIX_SET = App.getSet(AppKey.FIELDSET_DATE_SUFFIXS);

	/** 日時項目のサフィックス */
	public static final AppSet<String> DATETIME_SUFFIX_SET = App.getSet(AppKey.FIELDSET_DATETIME_SUFFIXS);

	/** パスワード項目のサフィックス */
	public static final AppSet<String> PASSWORD_SUFFIX_SET = App.getSet(AppKey.FIELDSET_PASSWORD_SUFFIXS);

	/** ラジオボタン項目のサフィックス */
	public static final AppSet<String> RADIOS_SUFFIX_SET = App.getSet(AppKey.FIELDSET_RADIOS_SUFFIXS);

	/** プルダウン項目のサフィックス */
	public static final AppSet<String> SELECT_SUFFIX_SET = App.getSet(AppKey.FIELDSET_SELECT_SUFFIXS);

	/** テキストエリア項目のサフィックス */
	public static final AppSet<String> TEXTAREA_SUFFIX_SET = App.getSet(AppKey.FIELDSET_TEXTAREA_SUFFIXS);

	/** 時間項目のサフィックス */
	public static final AppSet<String> TIME_SUFFIX_SET = App.getSet(AppKey.FIELDSET_TIME_SUFFIXS);

	/** 画像項目のサフィックス */
	public static final AppSet<String> IMG_SUFFIX_SET = App.getSet(AppKey.FIELDSET_IMG_SUFFIXS);

	/** 部分一致項目の接尾辞 */
	public static final AppSet<String> PART_SUFFIX_SET = App.getSet(AppKey.FIELDSET_CRITERIA_PART_SUFFIXS);

	/** 範囲指定項目の接尾辞 */
	public static final AppSet<String> RANGE_SUFFIX_SET = App.getSet(AppKey.FIELDSET_CRITERIA_RANGE_SUFFIXS);

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

	/** 編集画面フラグ */
	private String edit;

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.edit = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {

		// テーブル情報がなければ終了
		TableInfo tableInfo = MetaData.getTableInfo(this.modelName);
		if (tableInfo == null) {
			return null;
		}

		this.prepareOptionAttributes();

		ServletRequest request = this.pageContext.getRequest();

		// 編集用フィールドセットフラグ（jspタグで指定したedit属性）
		boolean aintEdit = StringUtil.equalsIgnoreCase(this.edit, "false");

		// URIのページ名
		String pageName = RequestUtil.getPathPageName(request);

		// 検索画面かフラグ
		boolean isIndex = StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);

		// モデルの主キー情報を取得
		Set<String> primaryNames = ModelUtil.getPrimaryPropertyNames(this.modelName);

		/*
		 * タグ文字列 生成開始
		 */

		// 入力フォームのタイトル
		StringBuilder sb = new StringBuilder("<fieldset>").append(Legend.render(this.modelName, null, pageName, false));

		for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {
			// カラム情報でループ

			// 削除フラグなら表示しない
			if (StringUtil.equalsIgnoreCase(BeanGenerator.DELETE_F, columnInfo.getColumnName())) {
				continue;
			}

			// カラム論理名
			String columnMei = columnInfo.getColumnMei();

			// プロパティ名
			String propertyName = columnInfo.getPropertyName();

			// HTML項目名
			String htmlName = Taglib.getHtmlName(this, propertyName);

			// プロパティ値
			Object value = RequestUtil.lookup(request, this.modelName, propertyName, htmlName);

			// データ長
			String maxlength = String.valueOf(columnInfo.getColumnSize());

			// 親タグにParentModelName属性があるか
			boolean isParentModelName = Taglib.getParentAttribute(this, "parentModelName") != null;

			// 主キー項目か
			boolean isPrimaryKey = primaryNames.contains(propertyName);

			// 選択項目か
			boolean isOptionable = ModelUtil.isOptionable(propertyName);

			Criteria c = null;
			if (this.criteria != null) {
				c = this.criteria.clone();
			}

			if (aintEdit) {
				// 編集用フィールドセットでない場合

				// 「fieldsets内のfieldsetの場合など親タグにparentModelNameがあり、従属キー項目」なら表示しない
				if (isParentModelName && isPrimaryKey) {
					continue;
				}

				// スパン表示（選択項目なら名称解決して表示する）
				if (isOptionable) {
					sb.append(new SpanField(this.pageContext, this.modelName, propertyName, c, this.optionModel,
							this.optionValue, this.optionLabel).toString());
				} else {
					sb.append(SpanField.render(this.modelName, propertyName, htmlName, value, columnMei));
				}

			} else if (isIndex) {
				// 検索画面の場合

				// 一覧に表示しない項目なら検索条件にも表示しない
				if (AINT_SELECT_SUFFIX_SET.isEnd(propertyName)) {
					continue;
				}

				if (isOptionable) {
					// 選択項目の検索条件ならチェックボックスリストを表示

					sb.append(new ChecksField(this.pageContext, this.modelName, propertyName, c, this.optionModel,
							this.optionValue, this.optionLabel, false).toString());

				} else if (RANGE_SUFFIX_SET.isEnd(propertyName)) {
					// 範囲指定の検索条件ならfrom-toで表示

					String type = "text";
					if (DATE_SUFFIX_SET.isEnd(propertyName)) {
						type = "date";
					} else if (DATETIME_SUFFIX_SET.isEnd(propertyName)) {
						type = "datetime";
					}

					String htmlName0 = htmlName + "[0]";
					String htmlName1 = htmlName + "[1]";

					Object value0 = RequestUtil.lookup(request, this.modelName, propertyName, htmlName0);
					Object value1 = RequestUtil.lookup(request, this.modelName, propertyName, htmlName1);

					sb.append("<div>").append(Label.render(toHtmlId(htmlName), columnMei, false));
					sb.append(Input.render(htmlName0, value0, false, this.pageContext, type, maxlength, null, null));
					sb.append("&nbsp;～&nbsp;");
					sb.append(Input.render(htmlName1, value1, false, this.pageContext, type, maxlength, null, null));
					sb.append("</div>\n");

				} else {

					// 上記以外ならプロパティに応じて出力
					sb.append(getField(primaryNames, columnMei, propertyName, htmlName, value, maxlength, null, false,
							false));
				}

			} else {
				// 編集用フィールドセットの場合

				// 読み取り専用を評価
				boolean readonly = Taglib.isReadonly(request, this.modelName, propertyName);

				// 読み取り専用でなければ必須属性を評価
				boolean notnull = !readonly && columnInfo.getNullable() != 1;

				if (isParentModelName && isPrimaryKey) {
					// 「親タグにparentModelNameがあり、従属キー項目」ならhiddenを出力
					sb.append(
							Input.render(htmlName, value, readonly, this.pageContext, "hidden", maxlength, null, null));
				} else {
					// 上記以外ならプロパティに応じて出力
					sb.append(getField(primaryNames, columnMei, propertyName, htmlName, value, maxlength, null,
							readonly, notnull));
				}
			}
		}

		return sb.append("</fieldset>").toString();
	}

	/**
	 * @param primaryPropertyNames
	 *            主キープロパティ名セット
	 * @param columnMei
	 *            columnMei
	 * @param propertyName
	 *            propertyName
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param maxlength
	 *            maxlength
	 * @param autocomplete
	 *            autocomplete
	 * @param readonly
	 *            readonly
	 * @param notnull
	 *            notnull
	 * @return プロパティごとの入力タグ文字列
	 */
	private String getField(final Set<String> primaryPropertyNames, final String columnMei, final String propertyName,
			final String htmlName, final Object value, final String maxlength, final String autocomplete,
			final boolean readonly, final boolean notnull) {

		// チェックボックスプロパティの場合
		if (Fieldset.CHECKS_SUFFIX_SET.isEnd(propertyName)) {
			Criteria criteria = null;
			if (this.criteria != null) {
				criteria = this.criteria.clone();
			}
			return new ChecksField(this.pageContext, this.modelName, propertyName, criteria, this.optionModel,
					this.optionValue, this.optionLabel, notnull).toString();
		}

		// ラジオボタンプロパティの場合
		if (Fieldset.RADIOS_SUFFIX_SET.isEnd(propertyName)) {
			Criteria criteria = null;
			if (this.criteria != null) {
				criteria = this.criteria.clone();
			}
			return new RadiosField(this.pageContext, this.modelName, propertyName, criteria, this.optionModel,
					this.optionValue, this.optionLabel, notnull).toString();
		}

		// プルダウンリストプロパティの場合
		if (Fieldset.SELECT_SUFFIX_SET.isEnd(propertyName)) {

			Criteria criteria = null;
			if (this.criteria != null) {
				criteria = this.criteria.clone();
			}

			// TODO 複合キーの最後ならスパン表示？！
			String[] primaryPropertyNameArray = primaryPropertyNames.toArray(new String[primaryPropertyNames.size()]);
			if (primaryPropertyNameArray[primaryPropertyNameArray.length - 1].equals(propertyName)) {
				return new SpanField(this.pageContext, this.modelName, propertyName, criteria, this.optionModel,
						this.optionValue, this.optionLabel).toString();
			}

			return new SelectField(this.pageContext, this.modelName, propertyName, criteria, this.optionModel,
					this.optionValue, this.optionLabel, notnull).toString();
		}

		// テキストエリアプロパティの場合
		if (Fieldset.TEXTAREA_SUFFIX_SET.isEnd(propertyName)) {
			return TextareaField.render(htmlName, value, columnMei, readonly, notnull);
		}

		// パスワードプロパティの場合
		if (Fieldset.PASSWORD_SUFFIX_SET.isEnd(propertyName)) {
			return InputField.render(this.pageContext, "password", htmlName, value, columnMei, maxlength, autocomplete,
					readonly, notnull);
		}

		// 日付プロパティの場合
		if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName)) {
			return InputField.render(this.pageContext, "date", htmlName, value, columnMei, maxlength, autocomplete,
					readonly, notnull);
		}

		// 日時プロパティの場合
		if (Fieldset.DATETIME_SUFFIX_SET.isEnd(propertyName)) {
			return InputField.render(this.pageContext, "datetime", htmlName, value, columnMei, maxlength, autocomplete,
					readonly, notnull);
		}

		// 時間プロパティの場合
		if (Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {
			return InputField.render(this.pageContext, "time", htmlName, value, columnMei, maxlength, autocomplete,
					readonly, notnull);
		}

		// 画像プロパティの場合
		if (Fieldset.IMG_SUFFIX_SET.isEnd(propertyName)) {
			return InputField.render(this.pageContext, "file", htmlName, value, columnMei, maxlength, autocomplete,
					readonly, notnull);
		}

		// テキストプロパティがデフォルト
		return InputField.render(this.pageContext, "text", htmlName, value, columnMei, maxlength, autocomplete,
				readonly, notnull);
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
	 * edit.を取得します。
	 *
	 * @return edit
	 */
	public final String getEdit() {
		return edit;
	}

	/**
	 * edit.を設定します。
	 *
	 * @param edit
	 *            edit
	 */
	public final void setEdit(final String edit) {
		this.edit = edit;
	}

}
