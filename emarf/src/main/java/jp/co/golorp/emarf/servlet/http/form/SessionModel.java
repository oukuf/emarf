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
 * キーがプロパティ名、値が{@link SessionProperty}のMap<br>
 * 単画面の入力フォームごとまたは一覧の明細行ごとに１インスタンスを生成する。
 *
 * @author oukuf@golorp
 */
public final class SessionModel implements Serializable {

	/** Map実体 */
	private Map<String, SessionProperty> propertyMap = new LinkedHashMap<String, SessionProperty>();

	/** モデル名 */
	private String modelName;

	/**
	 * @param modelName
	 *            モデル名
	 */
	public SessionModel(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * 指定したプロパティ名に{@link SessionProperty}を設定する。
	 *
	 * @param propertyName
	 *            propertyName
	 * @param sessionProperty
	 *            SessionProperty
	 */
	public void put(final String propertyName, final SessionProperty sessionProperty) {
		this.propertyMap.put(propertyName, sessionProperty);
		// for (Entry<String, String[]> html : sessionProperty.entrySet()) {
		// String htmlName = html.getKey();
		// String[] values = html.getValue();
		// String index = null;
		// if (htmlName.matches("^.+\\[\\d+\\].+$")) {
		// index = htmlName.replaceFirst("^.+\\[", "").replaceFirst("\\].+$",
		// "");
		// putProperty(propertyName, this.modelName + "[" + index + "]." +
		// propertyName, values);
		// } else {
		// putProperty(propertyName, this.modelName + "." + propertyName,
		// values);
		// }
		// }
	}

	/**
	 * 指定したプロパティ名にhtml項目名と値を設定する
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param htmlName
	 *            html項目名
	 * @param o
	 *            プロパティ値
	 */
	public void put(final String propertyName, final String htmlName, final Object o) {

		String[] values = StringUtil.toStringArray(o);

		SessionProperty sessionProperty = this.propertyMap.get(propertyName);
		if (sessionProperty == null) {
			sessionProperty = new SessionProperty(propertyName, htmlName, values);
		} else {
			sessionProperty.put(htmlName, values);
		}

		this.propertyMap.put(propertyName, sessionProperty);
	}

	/**
	 * 指定したプロパティ名に値を設定する。<br>
	 * html項目名に添え字は付かないため単画面で使用する。
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param o
	 *            プロパティ値
	 */
	public void put(final String propertyName, final Object o) {
		put(propertyName, this.modelName + "." + propertyName, o);
	}

	/**
	 * プロパティ名に関わらず、当該モデルの全ての値を連結して返す
	 *
	 * @return 全プロパティ値
	 */
	public String get() {

		StringBuilder sb = new StringBuilder();

		for (SessionProperty sessionProperty : this.propertyMap.values()) {
			if (!sessionProperty.getPropertyName().equals("page")) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(sessionProperty.get());
			}
		}

		return sb.toString();
	}

	/**
	 * html項目名に関わらず、プロパティ名に合致する全ての値を連結して返す
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @return プロパティ値
	 */
	public String get(final String propertyName) {

		SessionProperty sessionProperty = this.propertyMap.get(propertyName);
		if (sessionProperty == null) {
			return null;
		}

		return sessionProperty.get();
	}

	/**
	 * html項目名に関わらず、プロパティ名に合致する全ての値を配列で返す
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @return プロパティ名に合致する全ての値の配列
	 */
	public String[] gets(final String propertyName) {

		SessionProperty sessionProperty = this.propertyMap.get(propertyName);
		if (sessionProperty == null) {
			return null;
		}

		List<String> ret = new ArrayList<String>();

		for (Entry<String, String[]> html : sessionProperty.entrySet()) {
			String htmlName = html.getKey();
			String[] values = html.getValue();

			if (htmlName != null && htmlName.endsWith("]")) {
				// html項目名に添え字あり

				int b = htmlName.lastIndexOf("[") + 1;
				int e = htmlName.length() - 1;
				int i = Integer.parseInt(htmlName.substring(b, e));

				for (int j = ret.size(); j <= i; j++) {
					ret.add(null);
				}

				ret.set(i, StringUtil.join(values, ","));

			} else {
				// html項目名に添え字なし

				if (values != null) {
					for (String value : values) {
						ret.add(value);
					}
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * @param htmlName
	 *            html項目名
	 * @return html項目名に合致する値配列
	 */
	public String[] getValues(final String htmlName) {
		for (SessionProperty sessionProperty : this.propertyMap.values()) {
			String[] values = sessionProperty.getValues(htmlName);
			if (StringUtil.isNotBlank(values)) {
				return values;
			}
		}
		return null;
	}

	/**
	 * @return Map<プロパティ名, {@link SessionProperty}>のEntrySet
	 */
	public Set<Entry<String, SessionProperty>> entrySet() {
		return this.propertyMap.entrySet();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (Entry<String, SessionProperty> property : this.entrySet()) {
			String propertyName = property.getKey();
			SessionProperty sessionProperty = property.getValue();

			sb.append("\n                ").append(propertyName).append(" : {");
			sb.append(sessionProperty);
			sb.append("\n                },");
		}

		return sb.toString();
	}

	/**
	 * @return modelName
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @return propertyMap
	 */
	public Map<String, SessionProperty> getPropertyMap() {
		return this.propertyMap;
	}

}
