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
package jp.co.golorp.emarf.servlet.http.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.golorp.emarf.util.StringUtil;

/**
 * キーがhtml項目名、値がhtml項目値配列のMap。<br>
 * １Propertyにつき１インスタンスを生成する。
 *
 * @author oukuf@golorp
 */
public final class SessionProperty implements Serializable {

	/** Map実体（html項目名：項目値の配列） */
	private Map<String, String[]> valuesMap = new LinkedHashMap<String, String[]>();

	/** プロパティ名 */
	private String propertyName;

	/**
	 * @param propertyName
	 *            プロパティ名
	 * @param htmlName
	 *            html項目名
	 * @param values
	 *            値配列（StringUtils.toStringArrayを外で使う）
	 */
	public SessionProperty(final String propertyName, final String htmlName, final String[] values) {
		this.propertyName = propertyName;
		this.valuesMap.put(htmlName, values);
	}

	/**
	 * @param htmlName
	 *            html項目名
	 * @param values
	 *            値配列
	 */
	public void put(final String htmlName, final String[] values) {
		this.valuesMap.put(htmlName, values);
	}

	/**
	 * @return html項目名に関わらず、当該プロパティの全ての値を連結して返す
	 */
	public String get() {
		List<String> valueList = this.getValueList();
		return StringUtil.join(valueList, ",");
	}

	/**
	 * @return html項目名に関わらず、当該プロパティの全ての値を配列で返す
	 */
	public String[] gets() {
		List<String> valueList = this.getValueList();
		return valueList.toArray(new String[valueList.size() - 1]);
	}

	/**
	 * @param htmlName
	 *            html項目名
	 * @return html項目名に合致する値配列
	 */
	public String[] getValues(final String htmlName) {
		return this.valuesMap.get(htmlName);
		// FIXME 上記でいいはず。だめなら戻す。
		// for (Entry<String, String[]> valuesEntry : this.valuesMap.entrySet())
		// {
		// if (StringUtil.equals(valuesEntry.getKey(), htmlName)) {
		// String[] values = valuesEntry.getValue();
		// if (StringUtil.isNotBlank(values)) {
		// return values;
		// }
		// }
		// }
		// return null;
	}

	/**
	 * @return Map<html項目名：値配列>のEntrySet
	 */
	public Set<Entry<String, String[]>> entrySet() {
		return this.valuesMap.entrySet();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (Entry<String, String[]> html : this.entrySet()) {
			String htmlName = html.getKey();
			String[] values = html.getValue();

			String value = StringUtil.join(values, "\", \"");
			sb.append("\n                    ").append(htmlName).append(" : [ \"").append(value).append("\" ],");
		}

		return sb.toString();
	}

	/**
	 * @return html項目名に関わらず、当該プロパティの全ての値をListで返す
	 */
	private List<String> getValueList() {

		List<String> valueList = new ArrayList<String>();
		for (String[] values : this.valuesMap.values()) {
			for (String value : values) {
				valueList.add(value);
			}
		}

		return valueList;
	}

	/**
	 * @return propertyName
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	// FIXME 利用個所なしのためコメントアウトで様子見
	// /**
	// * @return html項目名に関わらず、当該プロパティ値の反復子
	// */
	// public Collection<String[]> values() {
	// return this.valuesMap.values();
	// }

	// FIXME modelName.pageが増えていくので一旦コメントアウトして様子見る
	// /**
	// * @param htmlName
	// * html項目名
	// * @param values
	// * 値配列
	// */
	// public void merge(final String htmlName, final String[] values) {
	//
	// if (StringUtils.isBlank(values)) {
	// return;
	// }
	//
	// List<String> valueList = new ArrayList<String>();
	//
	// String[] myValues = this.valuesMap.get(htmlName);
	// if (StringUtils.isNotBlank(myValues)) {
	// for (String myValue : myValues) {
	// valueList.add(myValue);
	// }
	// }
	//
	// if (StringUtils.isNotBlank(values)) {
	// for (String value : values) {
	// valueList.add(value);
	// }
	// }
	//
	// this.valuesMap.put(htmlName, valueList.toArray(new
	// String[valueList.size() - 1]));
	// }

}
