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
package jp.co.golorp.emarf.tag.lib.iterate.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.collection.AppSet;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.IterateTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 子モデルのtable出力タグ
 *
 * @author oukuf@golorp
 */
public class Tables extends IterateTagSupport implements Modelable {

	/** 子モデルとしてtableタグを出力しないモデルのリスト */
	public static final AppSet<String> HIDE_MODEL_SET = App.getSet(AppKey.TABLES_HIDE_MODELS);

	/***/
	private String modelName;

	/***/
	private String parentModelName;

	/***/
	private String childonly;

	@Override
	public void release() {
		this.modelName = null;
		this.parentModelName = null;
		this.childonly = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {
		return null;
	}

	@Override
	protected Iterator<Entry<String, List<RelateColumnMap>>> getIterator() {

		// モデル名を親モデル名として退避
		this.parentModelName = this.modelName;

		RelateTablesMap relateTables = new RelateTablesMap();

		// 子モデル情報を取得
		RelateTablesMap children = ModelUtil.getChildren(this.parentModelName);
		if (children != null) {
			for (Entry<String, List<RelateColumnMap>> child : children.entrySet()) {
				String modelName = child.getKey();
				List<RelateColumnMap> properties = child.getValue();
				if (!HIDE_MODEL_SET.contains(modelName)) {
					relateTables.put(modelName, properties);
				}
			}
		}

		if (!StringUtil.is(this.childonly)) {

			// 履歴先モデル
			RelateTablesMap historyBys = ModelUtil.getHistoryBys(this.parentModelName);
			if (historyBys != null) {
				for (Entry<String, List<RelateColumnMap>> historyBy : historyBys.entrySet()) {
					String modelName = historyBy.getKey();
					List<RelateColumnMap> properties = historyBy.getValue();
					if (!HIDE_MODEL_SET.contains(modelName)) {
						relateTables.put(modelName, properties);
					}
				}
			}

			// サマリ先モデル
			RelateTablesMap summaryBys = ModelUtil.getSummaryBys(this.parentModelName);
			if (summaryBys != null) {
				for (Entry<String, List<RelateColumnMap>> summaryBy : summaryBys.entrySet()) {
					String modelName = summaryBy.getKey();
					List<RelateColumnMap> properties = summaryBy.getValue();
					if (!HIDE_MODEL_SET.contains(modelName)) {
						relateTables.put(modelName, properties);
					}
				}
			}
		}

		if (relateTables.isEmpty()) {
			return null;
		}

		// 子モデル情報でループ
		return relateTables.entrySet().iterator();
	}

	@Override
	protected void doIterate() {

		if (this.iterator == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Entry<String, List<RelateColumnMap>> models = (Entry<String, List<RelateColumnMap>>) this.iterator.next();

		this.modelName = models.getKey();

		// 参照モデルは対象にしていないので一つ目だけを取得すればよい
		Map<String, String> properties = models.getValue().get(0);

		ServletRequest request = this.pageContext.getRequest();

		// リクエストスコープから親モデルを取得。なければ終了
		Model parent = (Model) request.getAttribute(this.parentModelName);
		if (parent == null) {
			return;
		}

		// 新規登録用に親モデルの外部キーを登録
		Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
		for (Entry<String, String> property : properties.entrySet()) {
			// TODO Fieldsetsに合わせる？
			String propertyName = property.getKey();
			String propertyName2 = property.getValue();
			Object value = parent.get(propertyName);
			if (value != null) {
				primaryKeys.put(propertyName2, value);
			}
		}

		// 子モデルをリクエストスコープに格納
		Model model = ModelUtil.getBlankModel(this.modelName).populate(primaryKeys);
		request.setAttribute(this.modelName, model);
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @return childonly
	 */
	public String getChildonly() {
		return childonly;
	}

	/**
	 * @param childonly
	 *            セットする childonly
	 */
	public void setChildonly(final String childonly) {
		this.childonly = childonly;
	}

}
