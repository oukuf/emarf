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
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * SpanFieldタグ
 *
 * @author oukuf@golorp
 */
public class SpanField extends CriteriaTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/**
	 * プロパティ
	 */
	private String propertyName;

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
	 *            value
	 * @param label
	 *            label
	 * @return タグ文字列
	 */
	public static final String render(final String modelName, final String propertyName, final String htmlName,
			final Object value, final String label) {
		return render(modelName, propertyName, htmlName, value, label, null, null, null, null);
	}

	/**
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param label
	 *            label
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
	public static final String render(final String modelName, final String propertyName, final String htmlName,
			final Object value, final String label, final Criteria criteria, final String optionModel,
			final String optionValue, final String optionLabel) {

		StringBuilder sb = new StringBuilder();

		sb.append("<div>");
		sb.append(Label.render(toHtmlId(htmlName), label, false));
		sb.append(
				Span.render(modelName, propertyName, htmlName, value, criteria, optionModel, optionValue, optionLabel));
		sb.append("</div>");

		return sb.toString();
	}

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 *
	 */
	public SpanField() {
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
	 */
	public SpanField(final PageContext pageContext, final String modelName, final String propertyName,
			final Criteria criteria, final String optionModel, final String optionValue, final String optionLabel) {
		super();
		this.pageContext = pageContext;
		this.modelName = modelName;
		this.propertyName = propertyName;
		this.criteria = criteria;
		this.optionModel = optionModel;
		this.optionValue = optionValue;
		this.optionLabel = optionLabel;
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
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

		String label = null;
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);
		if (propertyMeis != null) {
			label = ModelUtil.getPropertyMeis(this.modelName).get(this.propertyName);
		}

		return SpanField.render(modelName, propertyName, htmlName, value, label, this.getCriteria(), this.optionModel,
				this.optionValue, this.optionLabel);
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
