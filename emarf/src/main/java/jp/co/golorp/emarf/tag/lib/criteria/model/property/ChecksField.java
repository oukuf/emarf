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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.Legend;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * ChecksFieldタグ
 *
 * @author oukuf@golorp
 */
public class ChecksField extends CriteriaTagSupport implements Propertiable {

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
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            values
	 * @param readonly
	 *            readonly
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
	 * @param isIndex
	 *            検索画面か
	 * @param isChecks
	 *            チェックボックスプロパティか
	 * @return タグ文字列
	 */
	public static String render(final String modelName, final String propertyName, final String htmlName,
			final Object value, final boolean readonly, final Criteria criteria, final String optionModel,
			final String optionValue, final String optionLabel, final boolean notnull, final boolean isIndex,
			final boolean isChecks) {

		StringBuilder sb = new StringBuilder();

		sb.append("<div>");
		sb.append("<fieldset>");
		sb.append(Legend.render(modelName, propertyName, null, notnull));
		sb.append(Checks.render(htmlName, value, readonly, criteria, optionModel, optionValue, optionLabel, isIndex,
				isChecks));
		sb.append("</fieldset>");
		sb.append("</div>");

		return sb.toString();
	}

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 *
	 */
	public ChecksField() {
		super();
	}

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            プロパティ名
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
	public ChecksField(final PageContext pageContext, final String modelName, final String propertyName,
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
	 * @return タグ文字列
	 */
	private String callRender() {

		ServletRequest request = this.pageContext.getRequest();

		String htmlName = Taglib.getHtmlName(this);

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		this.prepareOptionAttributes();

		String pageName = RequestUtil.getPathPageName(request);

		boolean isIndex = StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);

		boolean isChecks = Fieldset.CHECKS_SUFFIX_SET.isEnd(this.propertyName);

		return render(this.modelName, this.propertyName, htmlName, value, readonly, this.getCriteria(),
				this.optionModel, this.optionValue, this.optionLabel, this.notnull, isIndex, isChecks);
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
