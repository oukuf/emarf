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

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Radio;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * Radiosタグ
 *
 * @author oukuf@golorp
 */
public class Radios extends CriteriaTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 * デフォルトコンストラクタ
	 */
	public Radios() {
		super();
	}

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            モデル名
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
	 */
	public Radios(final PageContext pageContext, final String modelName, final String propertyName,
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
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
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
	 * @return タグ文字列
	 */
	public static String render(final String htmlName, final Object value, final boolean readonly,
			final Criteria criteria, final String optionModel, final String optionValue, final String optionLabel) {

		// 選択肢モデルのリストを取得
		List<Model> models = Models.getModels(optionModel, criteria);
		if (models == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		// 選択肢モデルのリストでループ
		for (Model model : models) {

			// 選択項目値プロパティ・選択項目名プロパティを取得
			String v = model.getString(optionValue);
			String l = model.getString(optionLabel);

			String htmlId = toHtmlId(htmlName) + "_" + v;

			// 選択状態を評価
			boolean checked = false;
			if (StringUtil.isNotBlank(value)) {
				String[] values = StringUtil.toStringArray(value);
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(v)) {
						checked = true;
						break;
					}
				}
			}

			// タグ文字列を追加
			sb.append("<div>");
			sb.append(Radio.render(htmlId, htmlName, v, checked, readonly));
			sb.append(Label.render(htmlId, l, false));
			sb.append("</div>");
		}

		return sb.toString();
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
		return callRender();
	}

	@Override
	public String toString() {
		return callRender();
	}

	/**
	 * @return renderから返却された文字列
	 */
	private String callRender() {

		String htmlName = Taglib.getHtmlName(this);

		ServletRequest request = this.pageContext.getRequest();

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		this.prepareOptionAttributes();

		return render(htmlName, value, readonly, this.getCriteria(), this.optionModel, this.optionValue,
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
