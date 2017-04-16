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

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * SelectFieldタグ
 *
 * @author oukuf@golorp
 */
public class SelectField extends CriteriaTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private boolean notnull;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param label
	 *            label
	 * @param readonly
	 *            readonly
	 * @param notnull
	 *            notnull
	 * @param criteria
	 *            criteria
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @return タグ文字列
	 */
	public static String render(final String htmlName, final Object value, final String label, final boolean readonly,
			final boolean notnull, final Criteria criteria, final String optionModel, final String optionValue,
			final String optionLabel) {

		StringBuilder sb = new StringBuilder();

		sb.append("<div>");
		sb.append(Label.render(toHtmlId(htmlName), label, notnull));
		sb.append(Select.render(htmlName, value, readonly, criteria, optionModel, optionValue, optionLabel, null));
		sb.append("</select>").append("</div>");

		return sb.toString();
	}

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 *
	 */
	public SelectField() {
		super();
	}

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param criteria
	 *            criteria
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @param notnull
	 *            notnull
	 */
	public SelectField(final PageContext pageContext, final String modelName, final String propertyName,
			final Criteria criteria, final String optionModel, final String optionValue, final String optionLabel,
			final boolean notnull) {
		super();
		this.pageContext = pageContext;
		this.modelName = modelName;
		this.propertyName = propertyName;
		this.criteria = criteria;
		this.optionModel = optionModel;
		this.optionValue = optionValue;
		this.optionLabel = optionLabel;
		this.notnull = notnull;
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		this.notnull = false;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {
		return this.callRender();
	}

	@Override
	public String toString() {
		return this.callRender();
	}

	/**
	 * @return String
	 */
	private String callRender() {

		String htmlName = Taglib.getHtmlName(this);

		ServletRequest request = this.pageContext.getRequest();

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);

		String label = null;
		if (propertyMeis != null) {
			label = propertyMeis.get(this.propertyName);
		}

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		String pageName = RequestUtil.getPathPageName(request);

		boolean isIndex = StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);

		boolean notnull = false;
		if (StringUtil.isNotBlank(this.notnull)) {
			notnull = Boolean.valueOf(this.notnull);
		} else {
			ColumnInfo columnInfo = MetaData.getColumnInfo(this.modelName, this.propertyName);
			notnull = !isIndex && !readonly && columnInfo.getNullable() != 1;
		}

		this.prepareOptionAttributes();

		return render(htmlName, value, label, readonly, notnull, this.getCriteria(), this.optionModel, this.optionValue,
				this.optionLabel);
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

}
