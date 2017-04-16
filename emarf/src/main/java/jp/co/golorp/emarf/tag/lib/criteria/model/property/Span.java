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
import java.util.Map;
import java.util.Map.Entry;

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
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * Spanタグ
 *
 * @author oukuf@golorp
 */
public class Span extends CriteriaTagSupport implements Propertiable {

	/** 名称変換の際に評価するサフィックスの配列 */
	private static final String[] ID_NAME_SUFFIXS = App.gets(AppKey.SPAN_ID_NAME_SUFFIXS);

	/***/
	private static final Map<String, String> XSS_SANITIZE_MAP = App.getMap(AppKey.SPAN_XSS_SANITIZE_VALUES);

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
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
	 * @return タグ文字列
	 */
	public static final String render(final String modelName, final String propertyName, final String htmlName,
			final Object value) {
		return render(modelName, propertyName, htmlName, value, null, null, null, null);
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
			final Object value, final Criteria criteria, final String optionModel, final String optionValue,
			final String optionLabel) {

		// textareaならpreにする
		String tagName = "span";
		if (Fieldset.TEXTAREA_SUFFIX_SET.isEnd(htmlName)) {
			tagName = "pre";
		}

		// 一旦ラベルを設定
		String label = null;
		if (StringUtil.isNotBlank(value)) {
			label = String.valueOf(value);
		}

		if (Fieldset.PASSWORD_SUFFIX_SET.isEnd(htmlName)) {
			if (StringUtil.isNotBlank(label)) {
				label = label.replaceAll(".", "*");
			}
		}

		// 選択肢モデルでラベルを名称変換
		// TODO プロパティ名が「～ID」でない場合だけか？
		if (criteria != null) {
			List<Model> models = Models.getModels(optionModel, criteria);
			if (models != null) {
				for (Model model : models) {
					String v = model.getString(optionValue);
					String l = model.getString(optionLabel);
					if (v != null && v.equals(value)) {
						label = l;
						break;
					}
				}
			}
		}

		String name = "";
		if (propertyName.endsWith(Models.ID_SUFFIX)) {
			// プロパティ名が「～ID」の場合

			// 当該プロパティ名をユニークキーとする他モデル情報を取得
			String[] masterInfo = Taglib.getMasterInfo(modelName, propertyName);

			if (masterInfo != null) {
				// マスタ情報があった場合

				String masterName = masterInfo[0];
				String uniquePropertyName = masterInfo[1];

				Criteria c = Criteria.equal(masterName, uniquePropertyName, label);

				Model master = Models.getModel(masterName, c);

				if (master != null) {
					// 該当データがあった場合

					// ユニークキーに合致する全ての名称カラムをラベルに設定（姓・名など）
					for (String nameSuffix : ID_NAME_SUFFIXS) {
						String key = uniquePropertyName.replaceFirst(Models.ID_SUFFIX + "$", nameSuffix);
						String v = master.get(key);
						if (StringUtil.isNotBlank(v)) {
							name += v;
						}
					}

					// if (StringUtils.isNotBlank(name)) {
					// label = label + ":" + name;
					// }
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		if (StringUtil.isNotBlank(name)) {
			sb.append("<span>");
		}

		// 開始タグ文字列を追加
		sb.append("<").append(tagName).append(" id=\"").append(toHtmlId(htmlName)).append("\">");

		if (StringUtil.isNotBlank(label)) {
			for (Entry<String, String> entry : XSS_SANITIZE_MAP.entrySet()) {
				label = label.replaceAll(entry.getKey(), entry.getValue());
			}
			sb.append(label);
		}

		sb.append("</").append(tagName).append(">");

		if (StringUtil.isNotBlank(name)) {
			sb.append("：" + name);
		}

		if (StringUtil.isNotBlank(name)) {
			sb.append("</span>");
		}

		return sb.toString();
	}

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 *
	 */
	public Span() {
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
	public Span(final PageContext pageContext, final String modelName, final String propertyName,
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
	 * @return renderから返却された文字列
	 */
	private String callRender() {

		ServletRequest request = this.pageContext.getRequest();

		String htmlName = Taglib.getHtmlName(this);

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		this.prepareOptionAttributes();

		return Span.render(this.modelName, this.propertyName, htmlName, value, this.getCriteria(), this.optionModel,
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
