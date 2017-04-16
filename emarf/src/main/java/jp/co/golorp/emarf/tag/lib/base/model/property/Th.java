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

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;

/**
 * thタグ
 *
 * @author oukuf@golorp
 */
public class Th extends BaseTagSupport implements Propertiable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String rowspan;

	/***/
	private String colspan;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param rowspan
	 *            rowspan
	 * @param colspan
	 *            colspan
	 * @param label
	 *            label
	 * @return String
	 */
	public static String render(final String modelName, final String propertyName, final int rowspan, final int colspan,
			final String label) {

		StringBuilder sb = new StringBuilder("<th");

		if (rowspan > 0) {
			sb.append(" rowspan=" + rowspan);
		}

		if (colspan > 0) {
			sb.append(" colspan=" + colspan);
		}

		sb.append(">");

		sb.append("<span id=\"").append(modelName).append("_").append(propertyName).append("\">").append(label)
				.append("</span>");
		sb.append("</th>");

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
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		int rowspan = 0;
		if (StringUtils.isNotBlank(this.rowspan)) {
			rowspan = Integer.parseInt(this.rowspan);
		}

		int colspan = 0;
		if (StringUtils.isNotBlank(this.colspan)) {
			colspan = Integer.parseInt(this.colspan);
		}

		String label = null;
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);
		if (propertyMeis != null) {
			label = propertyMeis.get(this.propertyName);
		}

		return render(this.modelName, this.propertyName, rowspan, colspan, label);
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

}
