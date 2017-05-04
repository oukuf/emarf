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

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.collection.AppSet;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.body.model.Anchor;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.SpanField;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * モデルのメタ情報から入力フォームを生成する
 *
 * @author oukuf@golorp
 */
public class DefinitionList extends CriteriaTagSupport implements Modelable {

	/** 検索画面で非表示にするサフィックス */
	public static final AppSet<String> HIDE_SUFFIX_SET = App.getSet(AppKey.DATALIST_HIDE_SUFFIXS);

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {

		// カラム情報がなければ終了
		List<ColumnInfo> columnInfos = MetaData.getColumnInfos(this.modelName);
		if (columnInfos == null) {
			return null;
		}

		// option属性の規定値セット
		this.prepareOptionAttributes();

		Criteria c = null;
		if (this.criteria != null) {
			c = this.criteria.clone();
		}

		// DataList開始タグ
		StringBuilder sb = new StringBuilder("<dl class=\"tree\">");

		// DataTitleタグ
		sb.append("<dt>");
		sb.append(getDefinitionTerm(columnInfos, this.pageContext, this.modelName, c, this.optionModel,
				this.optionValue, this.optionLabel));
		sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, null, null)).append("照会</a>");
		sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_EDIT, null, null)).append("編集</a>");

		List<Model> datas = getDefinitionDescriptionDatas(pageContext, modelName);
		if (datas == null) {
			sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, EmarfServlet.METHOD_DELETE, null))
					.append("削除</a>");
		}

		sb.append("</dt>");

		// DataDefinitionタグ
		sb.append(getDefinitionDescription(datas, columnInfos, this.pageContext, this.modelName, c, this.optionModel,
				this.optionValue, this.optionLabel));

		// DataList閉じタグ
		sb.append("</dl>");

		return sb.toString();
	}

	/**
	 * @param datas
	 *            datas
	 * @param columnInfos
	 *            columnInfos
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param c
	 *            c
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @return DefinitionDescription文字列
	 */
	private static String getDefinitionDescription(final List<Model> datas, final List<ColumnInfo> columnInfos,
			final PageContext pageContext, final String modelName, final Criteria c, final String optionModel,
			final String optionValue, final String optionLabel) {

		StringBuilder sb = new StringBuilder("<dd>");

		// 再帰先データがある場合
		if (datas != null) {

			sb.append("<dl>");

			for (Model model : datas) {

				pageContext.getRequest().setAttribute(modelName, model);

				sb.append("<dt>");
				sb.append(getDefinitionTerm(columnInfos, pageContext, modelName, c, optionModel, optionValue,
						optionLabel));
				sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, null, null)).append("照会</a>");
				sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_EDIT, null, null)).append("編集</a>");

				List<Model> datas2 = getDefinitionDescriptionDatas(pageContext, modelName);
				if (datas2 == null) {
					sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_VIEW, EmarfServlet.METHOD_DELETE,
							null)).append("削除</a>");
				}

				sb.append("</dt>");

				sb.append(getDefinitionDescription(datas2, columnInfos, pageContext, modelName, c, optionModel,
						optionValue, optionLabel));
			}

			pageContext.getRequest().removeAttribute(modelName);

			sb.append("</dl>");
		}

		// DataDefinition閉じタグ
		sb.append("</dd>");

		return sb.toString();
	}

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @return Modelのリスト
	 */
	private static List<Model> getDefinitionDescriptionDatas(final PageContext pageContext, final String modelName) {

		Criteria criteria = null;

		RelateTablesMap recursiveBys = ModelUtil.getRecursiveBys(modelName);

		for (List<RelateColumnMap> recursiveBy : recursiveBys.values()) {

			// 再帰モデルなので一つ目だけでいい
			RelateColumnMap relateColumnMap = recursiveBy.get(0);

			for (Entry<String, String> relateColumn : relateColumnMap.entrySet()) {
				String columnName = relateColumn.getKey();
				String columnName2 = relateColumn.getValue();

				// post画面でもないので一旦これで行く
				String htmlName = modelName + "." + columnName;
				// String htmlName = Taglib.getHtmlName(this,
				// columnName);

				Object value = RequestUtil.lookup(pageContext.getRequest(), modelName, columnName, htmlName);

				if (criteria == null) {
					criteria = Criteria.equal(modelName, columnName2, value);
				} else {
					criteria.eq(modelName, columnName2, value);
				}
			}
		}

		return Models.getModels(modelName, criteria);
	}

	/**
	 * @param columnInfos
	 *            columnInfos
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param c
	 *            c
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @return DefinitionTerm文字列
	 */
	private static String getDefinitionTerm(final List<ColumnInfo> columnInfos, final PageContext pageContext,
			final String modelName, final Criteria c, final String optionModel, final String optionValue,
			final String optionLabel) {

		StringBuilder sb = new StringBuilder();

		for (ColumnInfo columnInfo : columnInfos) {
			// カラム情報でループ

			// 削除フラグなら表示しない
			if (StringUtil.equalsIgnoreCase(BeanGenerator.DELETE_F, columnInfo.getColumnName())) {
				continue;
			}

			// プロパティ名
			String propertyName = columnInfo.getPropertyName();

			// 非表示項目なら表示しない
			if (HIDE_SUFFIX_SET.isEnd(propertyName)) {
				continue;
			}

			// カラム論理名
			String columnMei = columnInfo.getColumnMei();

			// HTML項目名
			// String htmlName = Taglib.getHtmlName(this, propertyName);
			// post画面でもないので一旦これで行く
			String htmlName = modelName + "." + propertyName;

			// プロパティ値
			Object value = RequestUtil.lookup(pageContext.getRequest(), modelName, propertyName, htmlName);

			// 選択項目か
			boolean isOptionable = ModelUtil.isOptionable(propertyName);

			// スパン表示（選択項目なら名称解決して表示する）
			if (isOptionable) {
				sb.append(new SpanField(pageContext, modelName, propertyName, c, optionModel, optionValue, optionLabel)
						.toString());
			} else {
				sb.append(SpanField.render(modelName, propertyName, htmlName, value, columnMei));
			}
		}

		return sb.toString();
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

}
