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
package jp.co.golorp.emarf.tag.lib.base.model;

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
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 全ての上位モデルのリンクリストを出力
 *
 * @author oukuf@golorp
 */
public class Headline extends BaseTagSupport implements Modelable {

	/***/
	private static final AppSet<String> HIDE_MODELS = App.getSet(AppKey.HEADLINE_HIDE_MODELS);

	/** タグ属性：モデル名 */
	private String modelName;

	@Override
	public void release() {
		this.modelName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		// ログインが必要なのに未ログインの場合はスキップ
		if (!RequestUtil.isIfLogin(request)) {
			return null;
		}

		// リクエストスコープからモデルを取得
		Model model = null;
		if (this.modelName != null) {
			model = (Model) request.getAttribute(this.modelName);
		}

		RelateTablesMap relateTables = new RelateTablesMap();
		if (model != null) {
			// モデルがあった場合

			// 祖先モデル
			RelateTablesMap ancestors = ModelUtil.getAncestors(this.modelName);
			if (ancestors != null) {
				relateTables.putAll(ancestors);
			}

			// 親モデル
			RelateTablesMap parents = ModelUtil.getParentTablesMap(this.modelName);
			if (parents != null) {
				relateTables.putAll(parents);
			}

			// 履歴元モデル
			RelateTablesMap historyOfs = ModelUtil.getHistoryOfTablesMap(this.modelName);
			if (historyOfs != null) {
				relateTables.putAll(historyOfs);
			}

			// サマリ元モデル
			RelateTablesMap summaryOfs = ModelUtil.getSummaryOfTablesMap(this.modelName);
			if (summaryOfs != null) {
				relateTables.putAll(summaryOfs);
			}
		}

		Map<String, String> upperParams = new LinkedHashMap<String, String>();
		if (!relateTables.isEmpty()) {
			// 祖先モデルがある場合

			for (Entry<String, List<RelateColumnMap>> relateModel : relateTables.entrySet()) {
				// 祖先モデル情報でループ

				// 関連モデル名 と 外部キー情報 を取得
				String modelName2 = relateModel.getKey();

				// 非表示のモデルならスキップ
				if (HIDE_MODELS.contains(modelName2)) {
					continue;
				}

				List<RelateColumnMap> foreignKeysList = relateModel.getValue();
				for (RelateColumnMap foreignKeys : foreignKeysList) {

					// パラメータを退避
					Map<String, String> params = new LinkedHashMap<String, String>();
					for (Entry<String, String> foreignKey : foreignKeys.entrySet()) {
						String propertyName = foreignKey.getKey();
						String propertyName2 = foreignKey.getValue();
						Object value = model.get(propertyName);
						if (value != null) {
							params.put(modelName2 + "." + propertyName2, String.valueOf(value));
						}
					}

					// 退避したパラメータを文字列化
					String dataParam = toDataParam(params);

					// 祖先モデルの照会画面PathInfoをキーに、パラメータを退避
					upperParams.put(SEP + modelName2 + SEP + EmarfServlet.PAGE_VIEW + SEP, dataParam);
				}
			}

		} else {
			// 祖先モデルがない場合

			// FIXME ルートモデルのリンクは管理者の時だけ表示するか？
			List<String> roots = ModelUtil.getRoots();
			for (String root : roots) {

				// 非表示のモデルならスキップ
				if (HIDE_MODELS.contains(root)) {
					continue;
				}

				upperParams.put(SEP + root + SEP, null);
			}
		}

		String contextServletPath = RequestUtil.getContextServletPath(request);

		Map<String, String> modelMeis = MetaData.getModelMeis();

		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> upperParam : upperParams.entrySet()) {

			// PathInfoとパラメータ文字列を取得
			String pathInfo = upperParam.getKey();
			String params = upperParam.getValue();

			// モデル論理名を取得
			String ancestor = pathInfo.split("\\" + SEP)[1];
			String modelName = StringUtil.toUpperCamelCase(ancestor);
			String modelMei = modelMeis.get(modelName);

			// モデル論理名をプロパティファイルから補完
			// mysqlの場合は「VIEW」が入ってる
			if (StringUtil.isBlank(modelMei) || modelMei.equals("VIEW")) {
				modelMei = App.get(AppKey.TAGUTIL_GAMEN_PREFIX + modelName + ".index");
			}

			// モデル論理名をモデル名から補完
			if (StringUtil.isBlank(modelMei)) {
				modelMei = modelName;
			}

			// リンクリスト文字列を追加
			sb.append("<li><a href=\"").append(contextServletPath).append(pathInfo).append("\"");
			if (params != null) {
				sb.append(params);
			} else {
				sb.append(" onclick=\"nav.aside()\"");
			}
			sb.append(">").append(modelMei).append("</a></li>");
		}

		return sb.toString();
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

}
