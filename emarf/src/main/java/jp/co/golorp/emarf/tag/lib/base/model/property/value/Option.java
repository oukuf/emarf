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
package jp.co.golorp.emarf.tag.lib.base.model.property.value;

import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.tag.interfaces.Valuable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.Select;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * オプションタグ
 *
 * @author oukuf@golorp
 */
public class Option extends BaseTagSupport implements Valuable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String value;

	/***/
	private String label;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param value
	 *            value
	 * @param label
	 *            label
	 * @param selected
	 *            selected
	 * @param readonly
	 *            readonly
	 * @return タグ文字列
	 */
	public static final String render(final String value, final String label, final boolean selected,
			final boolean readonly) {

		StringBuffer sb = new StringBuffer("<option");

		if (StringUtil.isNotBlank(value)) {
			sb.append(" value=\"").append(value).append("\"");
		}

		if (selected) {
			sb.append(" selected");
		} else if (readonly) {
			sb.append(" disabled");
		}

		sb.append(">");

		if (StringUtil.isNotBlank(label)) {
			sb.append(label);
		}

		sb.append("</option>");

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		this.value = null;
		this.label = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		if (!(this.getParent() instanceof Select)) {
			return null;
		}

		Select select = (Select) this.getParent();

		String[] values = (String[]) select.getValue("values");

		boolean selected = false;
		if (StringUtil.isNotBlank(values)) {
			for (String value : values) {
				if (this.value.equals(value)) {
					selected = true;
					break;
				}
			}
		}

		return Option.render(this.value, this.label, selected, false);
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

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * ラベルを取得します。
	 *
	 * @return ラベル
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * ラベルを設定します。
	 *
	 * @param label
	 *            ラベル
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

}
