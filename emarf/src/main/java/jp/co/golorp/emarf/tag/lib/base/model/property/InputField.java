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
package jp.co.golorp.emarf.tag.lib.base.model.property;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * inputフィールド
 *
 * @author oukuf@golorp
 */
public class InputField extends BaseTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String type;

	/***/
	private String maxlength;

	/***/
	private String autocomplete;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param pageContext
	 *            pageContext
	 * @param type
	 *            type
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param label
	 *            label
	 * @param maxlength
	 *            maxlength
	 * @param autocomplete
	 *            autocomplete
	 * @param readonly
	 *            readonly
	 * @param notnull
	 *            notnull
	 * @return タグ文字列
	 */
	public static final String render(final PageContext pageContext, final String type, final String htmlName,
			final Object value, final String label, final String maxlength, final String autocomplete,
			final boolean readonly, final boolean notnull) {

		StringBuilder sb = new StringBuilder("<div>");
		sb.append(Label.render(toHtmlId(htmlName), label, notnull));
		sb.append(Input.render(htmlName, value, readonly, pageContext, type, maxlength, autocomplete, null));
		sb.append("</div>");

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		this.type = null;
		this.maxlength = null;
		this.autocomplete = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		String htmlName = Taglib.getHtmlName(this);

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		String label = null;
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);
		if (propertyMeis != null) {
			label = propertyMeis.get(this.propertyName);
		}

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		String pageName = RequestUtil.getPathPageName(request);

		boolean isIndex = StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);

		ColumnInfo columnInfo = MetaData.getColumnInfo(this.modelName, this.propertyName);

		boolean notnull = !isIndex && !readonly && columnInfo.getNullable() != 1;

		if (StringUtil.isBlank(this.maxlength)) {
			if (columnInfo != null) {
				this.maxlength = String.valueOf(columnInfo.getColumnSize());
			}
		}

		return InputField.render(this.pageContext, this.type, htmlName, value, label, this.maxlength, this.autocomplete,
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

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * typeを取得します。
	 *
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * typeを設定します。
	 *
	 * @param type
	 *            type
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * maxlengthを取得します。
	 *
	 * @return maxlength
	 */
	public String getMaxlength() {
		return maxlength;
	}

	/**
	 * maxlengthを設定します。
	 *
	 * @param maxlength
	 *            maxlength
	 */
	public void setMaxlength(final String maxlength) {
		this.maxlength = maxlength;
	}

	/**
	 * autocompleteを取得します。
	 *
	 * @return autocomplete
	 */
	public String getAutocomplete() {
		return autocomplete;
	}

	/**
	 * autocompleteを設定します。
	 *
	 * @param autocomplete
	 *            autocomplete
	 */
	public void setAutocomplete(final String autocomplete) {
		this.autocomplete = autocomplete;
	}

}
