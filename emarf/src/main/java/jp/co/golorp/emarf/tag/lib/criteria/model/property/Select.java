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

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Option;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * Selectタグ
 *
 * @author oukuf@golorp
 */
public class Select extends CriteriaTagSupport implements Propertiable {

	/** optgroup指定時のセパレータ */
	private static final String OPTGROUP_SEP = App.get(AppKey.SELECT_OPTGROUP_SEP);

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String onchange;

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 * デフォルトコンストラクタ
	 */
	public Select() {
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
	public Select(final PageContext pageContext, final String modelName, final String propertyName,
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
	 * @param onchange
	 *            onchange
	 * @return タグ文字列
	 */
	public static String render(final String htmlName, final Object value, final boolean readonly,
			final Criteria criteria, final String optionModel, final String optionValue, final String optionLabel,
			final String onchange) {

		StringBuilder sb = new StringBuilder();

		// 開始タグ
		sb.append("<select id=\"").append(toHtmlId(htmlName)).append("\" name=\"").append(htmlName).append("\"");
		if (onchange != null) {
			sb.append(" onchange=\"").append(onchange).append("\"");
		}
		sb.append(">");

		// プルダウン項目を追加
		if (criteria != null) {
			sb.append(renderOptions(value, readonly, criteria, optionModel, optionValue, optionLabel));
		}

		return sb.toString();
	}

	/**
	 * @param value
	 *            選択値
	 * @param readonly
	 *            読み取り専用フラグ
	 * @param criteria
	 *            検索条件
	 * @param optionModel
	 *            オプションモデル
	 * @param optionValue
	 *            オプション値
	 * @param optionLabel
	 *            オプションラベル
	 * @return オプション文字列
	 */
	private static String renderOptions(final Object value, final boolean readonly, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel) {

		// 選択肢モデルを検索。なければ終了
		List<Model> models = Models.getModels(optionModel, criteria);
		if (models == null) {
			return "";
		}

		// ブランクの選択肢を追加
		StringBuilder sb = new StringBuilder(Option.render(null, null, false, readonly));

		String preoptgroup = null;

		// 選択肢モデルでループ
		for (Model model : models) {

			// 選択項目値と選択項目名を取得
			String v = model.getString(optionValue);
			String l = model.getString(optionLabel);

			// 選択状態を評価
			boolean selected = false;
			if (StringUtil.isNotBlank(value)) {
				String[] values = StringUtil.toStringArray(value);
				for (int i = 0; i < values.length; i++) {
					if (values[i].equals(v)) {
						selected = true;
						break;
					}
				}
			}

			// ラベルが「:」区切りならoptgroupを退避
			String optgroup = null;
			int pos = l.indexOf(OPTGROUP_SEP);
			if (pos > 0) {
				optgroup = l.substring(0, pos);
				l = l.substring(pos + 1);
			} else {
				optgroup = null;
			}

			// optgroupがbreakしたらoptgroup開始タグを追加
			if (optgroup != null && !optgroup.equals(preoptgroup)) {
				sb.append("<optgroup label=\"").append(optgroup).append("\">");
				preoptgroup = optgroup;
			}

			// optgroupが指定されていなければoptgroup閉じタグを追加
			if (optgroup == null && preoptgroup != null) {
				sb.append("</optgroup>");
				preoptgroup = null;
			}

			// optionタグ文字列を追加
			sb.append(Option.render(v, l, selected, readonly));
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
		this.onchange = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return callRender();
	}

	@Override
	public String doEnd() throws JspException {

		String text = "";

		if (StringUtil.isNotBlank(this.criteria)) {

			this.prepareOptionAttributes();

			String htmlName = Taglib.getHtmlName(this);

			ServletRequest request = this.pageContext.getRequest();

			Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

			boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);

			text = renderOptions(value, readonly, this.getCriteria(), this.optionModel, this.optionValue,
					this.optionLabel);
		}

		return text + "</select>";
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

		return render(htmlName, value, readonly, this.getCriteria(), this.optionModel, this.optionValue,
				this.optionLabel, this.onchange);
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
	 * onchangeを取得します。
	 *
	 * @return onchange
	 */
	public String getOnchange() {
		return onchange;
	}

	/**
	 * onchangeを設定します。
	 *
	 * @param onchange
	 *            onchange
	 */
	public void setOnchange(final String onchange) {
		this.onchange = onchange;
	}

}
