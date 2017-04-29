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

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.tag.interfaces.Valuable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 検索条件タグ<br>
 * CriteriaTagSupportBaseの拡張クラス内で使用する
 *
 * @author oukuf@golorp
 */
public class Criterion extends BaseTagSupport implements Valuable {

	/***/
	private static final String MODEL_DEFAULT = App.get(AppKey.CRITERION_MODEL_DEFAULT);

	/***/
	private static final String PROPERTY_DEFAULT = App.get(AppKey.CRITERION_PROPERTY_DEFAULT);

	/***/
	private static final String VALUE_DEFAULT = App.get(AppKey.CRITERION_VALUE_DEFAULT);

	/*
	 * ************************************************** タグプロパティ
	 */

	/** 検索条件となるモデル名 */
	private String modelName;

	/** 検索条件となるプロパティ名 */
	private String propertyName;

	/** 検索条件の値 */
	private String value;

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
	public String doStart() throws JspException {

		if (StringUtil.isBlank(this.modelName)) {
			this.modelName = MODEL_DEFAULT;
		}

		if (StringUtil.isBlank(this.propertyName)) {
			this.propertyName = PROPERTY_DEFAULT;
		}

		if (StringUtil.isBlank(this.value)) {
			this.value = VALUE_DEFAULT;
		}

		CriteriaTagSupport parent = (CriteriaTagSupport) this.getParent();

		parent.addCriteria(this.modelName, this.propertyName, this.value);

		return null;
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
		return this.value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}

}
