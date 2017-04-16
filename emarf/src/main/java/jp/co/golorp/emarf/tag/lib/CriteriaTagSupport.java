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
package jp.co.golorp.emarf.tag.lib;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 入力項目の選択肢として、モデル名・プロパティ名・プロパティ値を設定できるタグの基底クラス<br>
 * taglib.criteriaパッケージ内で拡張
 *
 * @author oukuf@golorp
 */
public abstract class CriteriaTagSupport extends BodyTagSupport {

	/** 選択肢モデル名の規定値 */
	private static final String OPTION_MODEL_DEFAULT = App.get(AppKey.CRITERIA_OPTION_MODEL_DEFAULT);

	/** 選択肢の値用プロパティ名の規定値 */
	private static final String OPTION_VALUE_DEFAULT = App.get(AppKey.CRITERIA_OPTION_VALUE_DEFAULT);

	/** 選択肢のラベル用プロパティ名の規定値 */
	private static final String OPTION_LABEL_DEFAULT = App.get(AppKey.CRITERIA_OPTION_LABEL_DEFAULT);

	/** 設定された検索条件 */
	protected Criteria criteria;

	/** モデル名 */
	protected String optionModel;

	/** 値用プロパティ名 */
	protected String optionValue;

	/** ラベル用プロパティ名 */
	protected String optionLabel;

	@Override
	public void release() {
		this.criteria = null;
		this.optionModel = null;
		this.optionValue = null;
		this.optionLabel = null;
		super.release();
	}

	/**
	 * ネストしたCriterionタグから検索条件を設定する
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            プロパティ値
	 */
	public final void addCriteria(final String modelName, final String propertyName, final String value) {

		String paramModel = modelName;
		if (paramModel == null) {
			paramModel = this.optionModel;
		}

		if (this.criteria == null) {
			this.criteria = Criteria.equal(paramModel, propertyName, value);
		} else {
			this.criteria.eq(paramModel, propertyName, value);
		}
	}

	/**
	 * @return criteria
	 */
	public final Criteria getCriteria() {
		if (this.criteria != null) {
			this.criteria.solve(this);
		}
		return this.criteria;
	}

	/**
	 * @param criteria
	 *            セットする criteria
	 */
	public final void setCriteria(final Criteria criteria) {
		if (criteria != null) {
			this.criteria = criteria.clone();
		}
	}

	/**
	 * @return optionModel
	 */
	public final String getOptionModel() {
		return optionModel;
	}

	/**
	 * @param optionModel
	 *            セットする optionModel
	 */
	public final void setOptionModel(final String optionModel) {
		this.optionModel = optionModel;
	}

	/**
	 * @return optionValue
	 */
	public final String getOptionValue() {
		return optionValue;
	}

	/**
	 * @param optionValue
	 *            セットする optionValue
	 */
	public final void setOptionValue(final String optionValue) {
		this.optionValue = optionValue;
	}

	/**
	 * @return optionLabel
	 */
	public final String getOptionLabel() {
		return optionLabel;
	}

	/**
	 * @param optionLabel
	 *            セットする optionLabel
	 */
	public final void setOptionLabel(final String optionLabel) {
		this.optionLabel = optionLabel;
	}

	/**
	 * オプション属性の規定値セット
	 */
	protected final void prepareOptionAttributes() {

		// 選択肢モデル名
		if (StringUtil.isBlank(this.optionModel)) {
			this.optionModel = OPTION_MODEL_DEFAULT;
		}

		// 選択項目値プロパティ名
		if (StringUtil.isBlank(this.optionValue)) {
			this.optionValue = OPTION_VALUE_DEFAULT;
		}

		// 選択項目名プロパティ名
		if (StringUtil.isBlank(this.optionLabel)) {
			this.optionLabel = OPTION_LABEL_DEFAULT;
		}
	}

}
