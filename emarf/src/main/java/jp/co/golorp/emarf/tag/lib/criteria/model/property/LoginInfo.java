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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * ログイン情報から値を出力する
 *
 * @author oukuf@golorp
 */
public class LoginInfo extends CriteriaTagSupport implements Propertiable {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(LoginInfo.class);

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
	 * @param value
	 *            value
	 * @return タグ文字列
	 */
	public static final String render(final Object value) {
		return render(value, null, null, null, null);
	}

	/**
	 * @param value
	 *            value
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
	public static final String render(final Object value, final Criteria criteria, final String optionModel,
			final String optionValue, final String optionLabel) {

		String label = null;

		// ログイン情報から取得した値をいったん退避（名称だったりコードだったり）
		if (StringUtil.isNotBlank(value)) {
			label = String.valueOf(value);
		}

		if (criteria != null) {
			// criteriaがある場合

			// 選択肢モデルリストでループ
			LOG.info("getModels");
			List<Model> models = Models.getModels(optionModel, criteria);
			for (Model model : models) {

				// 選択項目値と選択項目名を取得
				String v = model.getString(optionValue);
				String l = model.getString(optionLabel);

				// 値と合致すれば退避
				if (v != null && v.equals(value)) {
					label = l;
					break;
				}
			}
		}

		// ラベルがあれば返却
		if (StringUtil.isNotBlank(label)) {
			return label;
		}

		return "";
	}

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 * コンストラクタ
	 */
	public LoginInfo() {
		super();
	}

	/**
	 * コンストラクタ
	 *
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
	public LoginInfo(final PageContext pageContext, final String modelName, final String propertyName,
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
		return callRender();
	}

	@Override
	public String toString() {
		return callRender();
	}

	/**
	 * @return タグ文字列
	 */
	private String callRender() {

		ServletRequest request = this.pageContext.getRequest();

		Object value = RequestUtil.getLoginValue(request, this.modelName, this.propertyName);

		return LoginInfo.render(value, this.getCriteria(), this.optionModel, this.optionValue, this.optionLabel);
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
