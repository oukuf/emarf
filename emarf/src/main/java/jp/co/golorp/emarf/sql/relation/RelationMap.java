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
package jp.co.golorp.emarf.sql.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jp.co.golorp.emarf.constants.model.RelationTypes;

/**
 * 関連種別ごとに関連テーブルを管理するMap
 * <dl>
 * <dt>K
 * <dd>{@link RelationTypes}
 * <dt>V
 * <dd>{@link RelateTablesMap}
 * </dl>
 *
 * @author oukuf@golorp
 */
public class RelationMap extends TreeMap<RelationTypes, RelateTablesMap> {

	/**
	 * テーブルの関連情報を設定
	 *
	 * @param relationType
	 *            関連タイプ
	 * @param tableName2
	 *            比較先テーブル名
	 * @param matchColumnMap
	 *            合致するカラム名のmap
	 */
	public void addRelation(final RelationTypes relationType, final String tableName2,
			final Map<String, String> matchColumnMap) {

		// 関連テーブル情報を取得
		RelateTablesMap relateTablesMap = null;
		if (this.containsKey(relationType)) {
			relateTablesMap = this.get(relationType);
		} else {
			relateTablesMap = new RelateTablesMap();
		}

		// 関連列情報を取得
		List<RelateColumnMap> relateColumnMaps = null;
		if (relateTablesMap.containsKey(tableName2)) {
			relateColumnMaps = relateTablesMap.get(tableName2);
		} else {
			relateColumnMaps = new ArrayList<RelateColumnMap>();
		}

		// 関連列情報を設定
		RelateColumnMap relateColumnMap = new RelateColumnMap();
		for (Entry<String, String> matchColumn : matchColumnMap.entrySet()) {
			relateColumnMap.put(matchColumn.getKey(), matchColumn.getValue());
		}

		relateColumnMaps.add(relateColumnMap);
		relateTablesMap.put(tableName2, relateColumnMaps);

		// テーブル名の昇順に並び替えて退避
		Map<String, List<RelateColumnMap>> sortedTablesMap = new TreeMap<String, List<RelateColumnMap>>();
		for (Entry<String, List<RelateColumnMap>> relateTables : relateTablesMap.entrySet()) {
			String tableName = relateTables.getKey();
			List<RelateColumnMap> releateColumnMaps = relateTables.getValue();
			sortedTablesMap.put(tableName, releateColumnMaps);
		}

		// 退避した関連テーブル情報を関連情報に書き戻す
		relateTablesMap = new RelateTablesMap();
		for (Entry<String, List<RelateColumnMap>> sortedTables : sortedTablesMap.entrySet()) {
			String tableName = sortedTables.getKey();
			List<RelateColumnMap> releateColumnMaps = sortedTables.getValue();
			relateTablesMap.put(tableName, releateColumnMaps);
		}

		this.put(relationType, relateTablesMap);
	}

}
