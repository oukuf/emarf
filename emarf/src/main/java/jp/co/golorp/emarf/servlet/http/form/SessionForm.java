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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.golorp.emarf.util.StringUtil;

/**
 * キーがモデル名、値が{@link SessionModel}リストのMap。<br>
 * １リクエストにつき１インスタンスを生成する。
 *
 * @author oukuf@golorp
 */
public final class SessionForm implements Serializable {

	/** Map実体（モデル名：{@link SessionModel}のリスト） */
	private Map<String, List<SessionModel>> modelsMap = new LinkedHashMap<String, List<SessionModel>>();

	/** 当該SessionFormがページ繰りによるものか */
	private boolean paging = false;

	/**
	 * SessionModelを追加する
	 *
	 * @param sessionModel
	 *            SessionModel
	 */
	public void addModel(final SessionModel sessionModel) {
		String modelName = sessionModel.getModelName();
		List<SessionModel> sessionModels = this.getModels(modelName);
		sessionModels.add(sessionModel);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @param i
	 *            取得する添え字
	 * @return 指定番目のSessionModel
	 */
	public SessionModel getModel(final String modelName, final int i) {
		List<SessionModel> sessionModels = this.getModels(modelName);
		while (sessionModels.size() <= i) {
			sessionModels.add(new SessionModel(modelName));
		}
		return sessionModels.get(i);
	}

	/**
	 * モデル名に合致する一つ目の{@link SessionModel}を返す
	 *
	 * @param modelName
	 *            モデル名
	 * @return モデル名に合致する一つ目の{@link SessionModel}
	 */
	public SessionModel getModel(final String modelName) {
		return getModel(modelName, 0);
	}

	/**
	 * 指定した位置にSessionModelを追加する
	 *
	 * @param modelName
	 *            モデル名
	 * @param i
	 *            追加位置
	 * @param sessionModel
	 *            SessionModel
	 */
	public void setModel(final String modelName, final int i, final SessionModel sessionModel) {
		List<SessionModel> sessionModels = this.getModels(modelName);
		this.getModel(modelName, i);
		sessionModels.set(i, sessionModel);
	}

	/**
	 * @param htmlName
	 *            html項目名
	 * @return html項目名に合致する値配列
	 */
	public String[] getValues(final String htmlName) {

		// SessionModelリストのリストでループ
		for (List<SessionModel> sessionModels : this.modelsMap.values()) {

			// SessionModelリストでループ
			for (SessionModel sessionModel : sessionModels) {

				// html項目名に合致した値配列を取得
				String[] values = sessionModel.getValues(htmlName);
				if (StringUtil.isNotBlank(values)) {
					return values;
				}
			}
		}

		return null;
	}

	/**
	 * @return Map<モデル名, {@link SessionModel}リスト>のEntrySet
	 */
	public Set<Entry<String, List<SessionModel>>> entrySet() {
		return this.modelsMap.entrySet();
	}

	/**
	 * @return モデル名に関わらない全ての {@link SessionModel}リスト のリスト
	 */
	public Collection<List<SessionModel>> values() {
		return this.modelsMap.values();
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return モデル名に合致するSessionModelリストの反復子
	 */
	public Iterator<SessionModel> itr(final String modelName) {
		List<SessionModel> sessionModels = this.getModels(modelName);
		return sessionModels.iterator();
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		for (Entry<String, List<SessionModel>> model : this.entrySet()) {

			if (sb.length() == 0) {
				sb.append("\n    {");
			}

			String modelName = model.getKey();
			List<SessionModel> sessionModels = model.getValue();

			sb.append("\n        ").append(modelName).append(" : [");

			for (int i = 0; i < sessionModels.size(); i++) {
				sb.append("\n            ").append(modelName).append("[").append(i).append("] : {");
				sb.append(sessionModels.get(i));
				sb.append("\n            },");
			}
			sb.append("\n        ],");
		}

		if (sb.length() > 0) {
			sb.append("\n    }");
		}

		return sb.toString();
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return モデル名に合致する{@link SessionModel}リスト
	 */
	private List<SessionModel> getModels(final String modelName) {

		List<SessionModel> sessionModels = this.modelsMap.get(modelName);

		if (sessionModels == null) {
			sessionModels = new ArrayList<SessionModel>();
			this.modelsMap.put(modelName, sessionModels);
		}

		return sessionModels;
	}

	/**
	 * @return 当該SessionFormがページングによるものか
	 */
	public boolean isPaging() {
		return paging;
	}

	/**
	 * @param paging
	 *            当該SessionFormがページングによるものか
	 */
	public void setPaging(final boolean paging) {
		this.paging = paging;
	}

	// FIXME 使用箇所なし。コメントアウトで様子見。
	// /**
	// * 一つ目のSessionModelに、Modelから値をコピーする
	// *
	// * @param modelName
	// * モデル名
	// * @param propertyName
	// * プロパティ名
	// * @param valueModel
	// * 値を取得するモデル
	// */
	// public void copy2First(final String modelName, final String propertyName,
	// final Model valueModel) {
	// String value = valueModel.getString(propertyName);
	// put2First(modelName, propertyName, value);
	// }
	//
	// /**
	// * 一つ目のSessionModelに、値を設定する
	// *
	// * @param modelName
	// * モデル名
	// * @param propertyName
	// * プロパティ名
	// * @param value
	// * 値
	// */
	// public void put2First(final String modelName, final String propertyName,
	// final String value) {
	//
	// String htmlName = modelName + "." + propertyName;
	//
	// String[] values = StringUtil.toStringArray(value);
	//
	// SessionProperty sessionProperty = new SessionProperty(propertyName,
	// htmlName, values);
	//
	// SessionModel sessionModel = getFirst(modelName);
	//
	// sessionModel.getPropertyMap().put(propertyName, sessionProperty);
	// }

}
