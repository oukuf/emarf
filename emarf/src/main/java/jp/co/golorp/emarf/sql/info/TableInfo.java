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
package jp.co.golorp.emarf.sql.info;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * テーブル情報
 *
 * @author oukuf@golorp
 */
public class TableInfo {

	/**
	 * モデル名
	 */
	private String modelName;

	/**
	 * テーブル物理名
	 */
	private String tableName;

	/**
	 * テーブルタイプ
	 */
	private String tableType;

	/**
	 * テーブル論理名
	 */
	private String tableMei;

	/**
	 * 主キー
	 */
	private Set<String> primaryKeys;

	/**
	 * 列情報
	 */
	private List<ColumnInfo> columnInfos;

	/**
	 * VIEW情報
	 */
	private Map<String, ViewInfo> viewInfos;

	/**
	 * コンストラクタ
	 *
	 * @param modelName
	 *            モデル名
	 * @param tableName
	 *            テーブル物理名
	 * @param tableType
	 *            テーブルタイプ
	 * @param tableMei
	 *            テーブル論理名
	 * @param primaryKeys
	 *            主キー
	 * @param columnInfos
	 *            列情報
	 * @param viewInfos
	 *            VIEW情報
	 */
	public TableInfo(final String modelName, final String tableName, final String tableType, final String tableMei,
			final Set<String> primaryKeys, final List<ColumnInfo> columnInfos, final Map<String, ViewInfo> viewInfos) {
		this.modelName = modelName;
		this.tableName = tableName;
		this.tableType = tableType;
		this.tableMei = tableMei;
		this.primaryKeys = primaryKeys;
		this.columnInfos = columnInfos;
		this.viewInfos = viewInfos;
	}

	/**
	 * モデル名を取得します。
	 *
	 * @return モデル名
	 */
	public final String getModelName() {
		return modelName;
	}

	// /**
	// * モデル名を設定します。
	// *
	// * @param modelName
	// * モデル名
	// */
	// public final void setModelName(final String modelName) {
	// this.modelName = modelName;
	// }

	/**
	 * @return tableName
	 */
	public final String getTableName() {
		return tableName.toUpperCase();
	}

	// /**
	// * @param tableName
	// * セットする tableName
	// */
	// public final void setTableName(final String tableName) {
	// this.tableName = tableName;
	// }

	/**
	 * テーブルタイプを取得します。
	 *
	 * @return テーブルタイプ
	 */
	public String getTableType() {
		return tableType;
	}

	// /**
	// * テーブルタイプを設定します。
	// *
	// * @param tableType
	// * テーブルタイプ
	// */
	// public void setTableType(final String tableType) {
	// this.tableType = tableType;
	// }

	/**
	 * @return tableMei
	 */
	public final String getTableMei() {
		return tableMei;
	}

	// /**
	// * @param tableMei
	// * セットする tableMei
	// */
	// public final void setTableMei(final String tableMei) {
	// this.tableMei = tableMei;
	// }

	/**
	 * @return primaryKeys
	 */
	public final Set<String> getPrimaryKeys() {
		if (this.primaryKeys != null) {
			return new LinkedHashSet<String>(this.primaryKeys);
		}
		return null;
	}

	// /**
	// * @param primaryKeys
	// * セットする primaryKeys
	// */
	// public final void setPrimaryKeys(final Set<String> primaryKeys) {
	// this.primaryKeys = primaryKeys;
	// }

	/**
	 * @param propertyName
	 *            propertyName
	 * @return columnInfo
	 */
	public final ColumnInfo getColumnInfo(final String propertyName) {
		for (ColumnInfo columnInfo : columnInfos) {
			if (columnInfo.getPropertyName().equals(propertyName)) {
				return columnInfo;
			}
		}
		return null;
	}

	/**
	 * @return columnInfos
	 */
	public final List<ColumnInfo> getColumnInfos() {
		return columnInfos;
	}

	// /**
	// * @param columnInfos
	// * セットする columnInfos
	// */
	// public final void setColumnInfos(final List<ColumnInfo> columnInfos) {
	// this.columnInfos = columnInfos;
	// }

	/**
	 * @param propertyName
	 *            propertyName
	 * @return ViewInfo
	 */
	public ViewInfo getViewInfo(final String propertyName) {
		if (this.viewInfos != null) {
			return viewInfos.get(propertyName);
		}
		return null;
	}

	// /**
	// * @return viewInfos
	// */
	// public Map<String, ViewInfo> getViewInfos() {
	// return viewInfos;
	// }

	/**
	 * @return isView
	 */
	public boolean isView() {
		return this.viewInfos != null;
	}

	// /**
	// * @param viewInfos
	// * セットする viewInfos
	// */
	// public void setViewInfos(final Map<String, ViewInfo> viewInfos) {
	// this.viewInfos = viewInfos;
	// }

}
