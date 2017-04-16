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

import javax.servlet.ServletRequest;

import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Valuable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * checkboxタグ
 *
 * @author oukuf@golorp
 */
public class Check extends BaseTagSupport implements Valuable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String value;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param htmlId
	 *            htmlId
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param checked
	 *            checked
	 * @param readonly
	 *            readonly
	 * @return タグ文字列
	 */
	public static final String render(final String htmlId, final String htmlName, final String value,
			final boolean checked, final boolean readonly) {

		StringBuilder sb = new StringBuilder("<input type=\"checkbox\" id=\"").append(htmlId).append("\" name=\"")
				.append(htmlName).append("\" value=\"").append(value).append("\"");

		if (checked) {
			sb.append(" checked");
		}

		if (readonly) {
			sb.append(" onclick=\"return false;\"");
		}

		sb.append(" />");

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
		super.release();
	}

	@Override
	public String doStart() {

		ServletRequest request = this.pageContext.getRequest();

		String htmlName = Taglib.getHtmlName(this);

		String htmlId = toHtmlId(htmlName) + "_" + this.value;

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		boolean checked = false;
		if (StringUtil.isNotBlank(value)) {
			if (value instanceof String[]) {
				String[] values = (String[]) value;
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(this.value)) {
						checked = true;
						break;
					}
				}
			} else if (String.valueOf(value).equals(this.value)) {
				checked = true;
			}
		}

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		return render(htmlId, htmlName, this.value, checked, readonly);
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

}
