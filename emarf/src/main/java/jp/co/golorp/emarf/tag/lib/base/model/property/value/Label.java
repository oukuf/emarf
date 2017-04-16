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

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Valuable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * ラベルタグ
 *
 * @author oukuf@golorp
 */
public class Label extends BaseTagSupport implements Valuable {

	/***/
	public static final String NOTNULL_MARK = App.get(AppKey.LABEL_NOTNULL_MARK);

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
	 * @param htmlId
	 *            htmlId
	 * @param label
	 *            label
	 * @param notnull
	 *            notnull
	 * @return タグ文字列
	 */
	public static final String render(final String htmlId, final String label, final boolean notnull) {

		StringBuffer sb = new StringBuffer("<label for=\"").append(htmlId).append("\"");

		if (notnull) {
			sb.append(" class=\"notnull\"");
		}

		sb.append(">").append(label);

		if (notnull) {
			sb.append(" ").append(NOTNULL_MARK);
		}

		sb.append("</label>");

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

		if (StringUtil.isBlank(this.label)) {
			Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(this.modelName);
			if (propertyMeis != null) {
				this.label = propertyMeis.get(this.propertyName);
			}
		}

		String htmlName = Taglib.getHtmlName(this);

		String htmlId = toHtmlId(htmlName);

		if (ModelUtil.isOptionable(this.propertyName)) {
			htmlId += "_" + this.value;
		}

		ServletRequest request = this.pageContext.getRequest();

		String pageName = RequestUtil.getPathPageName(request);

		boolean isIndex = StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);

		boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

		ColumnInfo columnInfo = MetaData.getColumnInfo(this.modelName, this.propertyName);

		boolean notnull = !isIndex && !readonly && columnInfo.getNullable() != 1;

		return Label.render(htmlId, this.label, notnull);
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
