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
package jp.co.golorp.emarf.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.sql.info.ViewInfo;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * データベースメタ情報
 *
 * @author oukuf@golorp
 */
public final class MetaData {

	/*
	 * ************************************************************ クラス定数
	 */

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(MetaData.class);

	/** MetaData.properties */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MetaData.class.getSimpleName());

	/** 出力済みのカラム情報警告ログ。TABLE_INFOS作成時に利用するのでTABLE_INFOSより前にロードする必要がある。 */
	private static final Set<String> NOT_EXIST_COLUMN_INFO_NAMES = new HashSet<String>();

	/** テーブル情報リスト */
	private static final List<TableInfo> TABLE_INFOS = MetaData.prepareTableInfos();

	/** テーブル論理名の取得元 */
	private static TableCommentSources commentSource = null;

	/**
	 * テーブル論理名の取得元
	 *
	 * @author oukuf@golorp
	 */
	private enum TableCommentSources {

		/** MySQLなど */
		showTableStatus,

		/** Oracleなど */
		userTabComments
	}

	/*
	 * ************************************************************ コンストラクタ
	 */

	/**
	 * デフォルトコンストラクタ
	 */
	private MetaData() {
	}

	/*
	 * ************************************************************ クラスメソッド
	 */

	/**
	 * @return テーブル情報
	 */
	public static List<TableInfo> getTableInfos() {
		return TABLE_INFOS;
	}

	/**
	 * 生成済みクラスのテーブル論理名を取得
	 *
	 * @return 生成済みクラスのテーブル論理名（モデル物理名：モデル論理名）
	 */
	public static Map<String, String> getModelMeis() {

		Map<String, String> ret = new TreeMap<String, String>();
		for (TableInfo tableInfo : TABLE_INFOS) {
			String tableName = tableInfo.getTableName();
			String modelName = StringUtil.toUpperCamelCase(tableName);
			ret.put(modelName, tableInfo.getTableMei());
		}

		return ret;
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return テーブル情報
	 */
	public static TableInfo getTableInfo(final String modelName) {

		for (TableInfo tableInfo : TABLE_INFOS) {
			if (tableInfo.getModelName().equals(modelName)) {
				return tableInfo;
			}
		}

		LOG.debug("table is not exist. [" + modelName + "]");

		return null;
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return モデル名とプロパティ名に合致するカラム情報
	 */
	public static ColumnInfo getColumnInfo(final String modelName, final String propertyName) {

		// カラム情報リストでループしてプロパティ名が合致すれば返す
		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo != null) {
			List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
			for (ColumnInfo columnInfo : columnInfos) {
				if (columnInfo.getPropertyName().equals(propertyName)) {
					return columnInfo;
				}
			}
		}

		return null;
	}

	/**
	 * @return スキーマ内の全テーブル情報
	 */
	private static List<TableInfo> prepareTableInfos() {

		String catalog = BUNDLE.getString("catalog");

		String schemaPattern = BUNDLE.getString("schemaPattern");

		String tableNamePattern = BUNDLE.getString("tableNamePattern");

		String typesText = BUNDLE.getString("types");

		String[] types = null;
		if (StringUtil.isNotBlank(typesText)) {
			types = StringUtil.split(typesText);
		}

		// oracle対応
		if (catalog.equals("")) {
			catalog = null;
		}
		if (schemaPattern.equals("")) {
			schemaPattern = null;
		}
		if (tableNamePattern.equals("")) {
			tableNamePattern = null;
		}
		if (StringUtil.isBlank(types)) {
			types = null;
		}

		List<TableInfo> tableInfos = new ArrayList<TableInfo>();

		Connection cn = Connections.get();
		try {
			ResultSet rs = null;
			try {

				// 全テーブルメタ情報でループ
				rs = cn.getMetaData().getTables(catalog, schemaPattern, tableNamePattern, types);
				while (rs.next()) {

					// テーブル名
					String tableName = rs.getString("TABLE_NAME");

					// テーブルタイプ
					String tableType = rs.getString("TABLE_TYPE");

					// テーブルコメント
					String remarks = rs.getString("REMARKS");
					if (StringUtil.isBlank(remarks)) {
						if (commentSource == TableCommentSources.showTableStatus) {
							// MySQLなど、showTableStatusから取得済みの場合
							remarks = getTableCommentByShowTableStatus(cn, tableName);
						} else if (commentSource == TableCommentSources.userTabComments) {
							// Oracleなど、userTabCommentsから取得済みの場合
							remarks = getTableCommentByUserTabComments(cn, tableName);
						} else {
							// 上記以外の場合
							remarks = getTableComment(cn, tableName);
						}
					}

					// view情報か主キー情報を取得
					Map<String, ViewInfo> viewInfos = null;
					Set<String> primaryKeys = null;
					if (tableType.equals("VIEW")) {
						viewInfos = getViewInfos(cn, tableName);
					} else {
						primaryKeys = MetaData.getPrimaryKeys(cn, tableName);
					}

					// テーブルコメントからテーブル論理名を取得
					String tableMei = null;
					if (StringUtil.isNotBlank(remarks)) {
						tableMei = remarks.split("\t")[0];
					}

					// カラム情報を取得
					List<ColumnInfo> columnInfos = MetaData.getColumnInfos(cn, tableName);

					// モデル名
					String modelName = StringUtil.toUpperCamelCase(tableName);

					// テーブル情報を追加
					tableInfos.add(new TableInfo(modelName, tableName, tableType, tableMei, primaryKeys, columnInfos,
							viewInfos));
				}

			} catch (SQLException e) {
				throw new SystemError(e);
			} finally {
				DbUtils.closeQuietly(rs);
			}
		} catch (Exception e) {
			throw new SystemError(e);
		} finally {
			Connections.close();
		}

		for (String columnInfoName : NOT_EXIST_COLUMN_INFO_NAMES) {
			LOG.trace("Column MetaData [" + columnInfoName + "] is not exists.");
		}

		return tableInfos;
	}

	/**
	 * テーブルコメント取得
	 *
	 * @param cn
	 *            コネクション
	 * @param tableName
	 *            テーブル名
	 * @return テーブルコメント
	 */
	private static String getTableComment(final Connection cn, final String tableName) {

		String s = null;

		// MySQLなどの場合
		if (s == null) {
			s = getTableCommentByShowTableStatus(cn, tableName);
		}

		// Oracleなどの場合
		if (s == null) {
			s = getTableCommentByUserTabComments(cn, tableName);
		}

		return s;
	}

	/**
	 * テーブルコメント取得（主にmysqlの場合）
	 *
	 * @param cn
	 *            コネクション
	 * @param tableName
	 *            テーブル名
	 * @return テーブルコメント
	 */
	private static String getTableCommentByShowTableStatus(final Connection cn, final String tableName) {

		ResultSet rs = null;
		try {
			rs = cn.createStatement().executeQuery("show table status where name = '" + tableName + "'");
			if (rs.next()) {
				commentSource = TableCommentSources.showTableStatus;
				return rs.getString("COMMENT");
			}
		} catch (SQLException e) {
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return null;
	}

	/**
	 * テーブルコメント取得（主にoracleの場合）
	 *
	 * @param cn
	 *            コネクション
	 * @param tableName
	 *            テーブル名
	 * @return テーブルコメント
	 */
	private static String getTableCommentByUserTabComments(final Connection cn, final String tableName) {

		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM USER_TAB_COMMENTS WHERE TABLE_NAME = '" + tableName + "'";
			rs = cn.createStatement().executeQuery(sql);
			if (rs.next()) {
				commentSource = TableCommentSources.userTabComments;
				return rs.getString("COMMENTS");
			}
		} catch (SQLException e) {
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return null;
	}

	/**
	 * @param cn
	 *            コネクション
	 * @param tableName
	 *            テーブル名
	 * @return 主キーカラム名のSet
	 */
	private static Set<String> getPrimaryKeys(final Connection cn, final String tableName) {

		List<String> pkList = null;

		ResultSet rs = null;
		try {

			// テーブルの主キー情報でループ
			DatabaseMetaData dmd = cn.getMetaData();
			rs = dmd.getPrimaryKeys(null, null, tableName);
			while (rs.next()) {

				if (pkList == null) {
					pkList = new ArrayList<String>();
				}

				String columnName = rs.getString("COLUMN_NAME");

				// rdbmsは 1,2,3,･･･
				// sqliteは0,1,2,･･･ になる
				int keySeq = rs.getShort("KEY_SEQ");

				while (pkList.size() <= keySeq) {
					pkList.add(null);
				}
				pkList.set(keySeq, columnName);
			}

		} catch (SQLException e) {
			throw new SystemError(e);
		} finally {
			DbUtils.closeQuietly(rs);
		}

		List<String> primaryKeys = null;

		if (pkList != null) {
			for (String pk : pkList) {
				if (StringUtil.isNotBlank(pk)) {
					if (primaryKeys == null) {
						primaryKeys = new ArrayList<String>();
					}
					primaryKeys.add(pk);
				}
			}
		}

		if (primaryKeys == null) {
			return null;
		}

		return new LinkedHashSet<String>(primaryKeys);
	}

	/**
	 * @param cn
	 *            DBコネクション
	 * @param tableName
	 *            テーブル名
	 * @return 指定されたテーブルのカラム情報のリスト
	 */
	private static List<ColumnInfo> getColumnInfos(final Connection cn, final String tableName) {

		List<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();

		ResultSet rs = null;
		try {

			// テーブルの全列情報でループ
			rs = cn.getMetaData().getColumns(null, null, tableName, null);
			while (rs.next()) {

				// 列情報を取得
				ColumnInfo col = new ColumnInfo();
				col.setTableCat(getString(rs, "TABLE_CAT"));
				col.setTableSchem(getString(rs, "TABLE_SCHEM"));
				col.setTableName(getString(rs, "TABLE_NAME"));
				col.setColumnName(getString(rs, "COLUMN_NAME"));
				col.setDataType(getInt(rs, "DATA_TYPE"));
				col.setTypeName(getString(rs, "TYPE_NAME"));
				col.setColumnSize(getInt(rs, "COLUMN_SIZE"));
				col.setBufferLength(getInt(rs, "BUFFER_LENGTH"));
				col.setDecimalDigits(getInt(rs, "DECIMAL_DIGITS"));
				col.setNumPrecRadix(getInt(rs, "NUM_PREC_RADIX"));
				col.setNullable(getInt(rs, "NULLABLE"));
				col.setRemarks(getString(rs, "REMARKS"));
				col.setColumnDef(getString(rs, "COLUMN_DEF"));
				col.setSqlDataType(getInt(rs, "SQL_DATA_TYPE"));
				col.setSqlDatetimeSub(getInt(rs, "SQL_DATETIME_SUB"));
				col.setCharOctetLength(getInt(rs, "CHAR_OCTET_LENGTH"));
				col.setOrdinalPosition(getInt(rs, "ORDINAL_POSITION"));
				col.setIsNullable(getString(rs, "IS_NULLABLE"));
				col.setScopeCatalog(getString(rs, "SCOPE_CATALOG"));
				col.setScopeSchema(getString(rs, "SCOPE_SCHEMA"));
				col.setScopeTable(getString(rs, "SCOPE_TABLE"));
				col.setSourceDataType(getShort(rs, "SOURCE_DATA_TYPE"));
				col.setIsAutoincrement(getString(rs, "IS_AUTOINCREMENT"));
				col.setIsGeneratedcolumn(getString(rs, "IS_GENERATEDCOLUMN"));

				// カラム名からプロパティ名を設定
				col.setPropertyName(StringUtil.toCamelCase(col.getColumnName()));

				// REMARKS がなければ USER_COL_COMMENTS から取得
				if (col.getRemarks() == null) {
					col.setRemarks(getColumnCommentByUserColComments(cn, tableName, col.getColumnName()));
				}

				// REMARKSからカラム論理名を設定
				if (col.getRemarks() != null) {
					col.setColumnMei(col.getRemarks().split("\t")[0]);
				}

				columnInfos.add(col);
			}

		} catch (SQLException e) {
			throw new SystemError(e);
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return columnInfos;
	}

	/**
	 * @param rs
	 *            rs
	 * @param columnInfoName
	 *            columnLabel
	 * @return String
	 */
	private static String getString(final ResultSet rs, final String columnInfoName) {
		try {
			LOG.trace(columnInfoName + ":" + rs.getString(columnInfoName));
			return rs.getString(columnInfoName);
		} catch (SQLException e) {
			NOT_EXIST_COLUMN_INFO_NAMES.add(columnInfoName);
		}
		return null;
	}

	/**
	 * @param rs
	 *            rs
	 * @param columnInfoName
	 *            columnLabel
	 * @return String
	 */
	private static Integer getInt(final ResultSet rs, final String columnInfoName) {
		try {
			LOG.trace(columnInfoName + ":" + rs.getString(columnInfoName));
			return rs.getInt(columnInfoName);
		} catch (SQLException e) {
			NOT_EXIST_COLUMN_INFO_NAMES.add(columnInfoName);
		}
		return null;
	}

	/**
	 * @param rs
	 *            rs
	 * @param columnInfoName
	 *            columnLabel
	 * @return String
	 */
	private static Short getShort(final ResultSet rs, final String columnInfoName) {
		try {
			LOG.trace(columnInfoName + ":" + rs.getString(columnInfoName));
			return rs.getShort(columnInfoName);
		} catch (SQLException e) {
			NOT_EXIST_COLUMN_INFO_NAMES.add(columnInfoName);
		}
		return null;
	}

	/**
	 * カラムコメント取得（主にOracleの場合）
	 *
	 * @param cn
	 *            コネクション
	 * @param tableName
	 *            テーブル名
	 * @param columnName
	 *            カラム名
	 * @return カラムコメント
	 */
	private static String getColumnCommentByUserColComments(final Connection cn, final String tableName,
			final String columnName) {

		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM USER_COL_COMMENTS WHERE TABLE_NAME = '" + tableName + "' AND COLUMN_NAME = '"
					+ columnName + "'";
			rs = cn.createStatement().executeQuery(sql);
			if (rs.next()) {
				return rs.getString("COMMENTS");
			}
		} catch (SQLException e) {
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return null;
	}

	/***/
	private static final String SENTENCE_SELECT = "\\bSELECT\\b";

	/***/
	private static final int SENTENCE_SELECT_LENGTH = 6;

	/***/
	private static final String SENTENCE_FROM = "\\bFROM\\b";

	/***/
	private static final int SENTENCE_FROM_LENGTH = 4;

	/***/
	private static final String SENTENCE_WHERE = "\\bWHERE\\b";

	/***/
	private static final char COMMA = ',';

	/***/
	private static final char LEFT = '(';

	/***/
	private static final char RIGHT = ')';

	/***/
	private static final int COLUMN_DEF_EXPR_SIZE = 3;

	/**
	 * @param cn
	 *            cn
	 * @param tableName
	 *            tableName
	 * @return view情報
	 */
	public static Map<String, ViewInfo> getViewInfos(final Connection cn, final String tableName) {

		String ddl = null;

		// mysqlの場合
		if (ddl == null) {
			ddl = retrieveCreateViewMySQL(cn, tableName);
		}

		// sqliteの場合
		if (ddl == null) {
			ddl = retrieveCreateViewSqlite(cn, tableName);
		}

		// viewのddlが取得できなければ終了
		if (ddl == null) {
			return null;
		}

		// select句を取得
		int boFrom = StringUtil.indexOfIgnoreCase(ddl, SENTENCE_FROM);
		String select = ddl.substring(SENTENCE_SELECT_LENGTH, boFrom);

		// from句を取得
		int boWhere = StringUtil.indexOfIgnoreCase(ddl, SENTENCE_WHERE);
		if (boWhere < 0) {
			boWhere = ddl.length();
		}
		String from = ddl.substring(boFrom + SENTENCE_FROM_LENGTH, boWhere);

		Map<String, ViewInfo> viewInfos = null;

		int brackets = 0;

		// select句を「,」でsplitしてループ
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < select.length(); i++) {
			char c = select.charAt(i);
			if (c == LEFT) {
				++brackets;
			} else if (c == RIGHT) {
				--brackets;
			}
			if (c == COMMA && brackets > 0) {
				sb.append("，");
			} else {
				sb.append(c);
			}
		}
		String[] columns = sb.toString().split(",");
		for (String columnDef : columns) {

			LOG.trace("columnDef: " + columnDef);

			ViewInfo viewInfo = new ViewInfo();

			// columnを更に「.」と「AS」で分割
			columnDef = StringUtil.trim(columnDef).replaceAll("'|`|\"", "");
			String[] columnDefs = columnDef.split("(?i)(\\.|\\bAS\\b)");
			for (int i = 0; i < columnDefs.length; i++) {
				columnDefs[i] = columnDefs[i].trim();
			}

			List<String> columnDefParts = new ArrayList<String>();
			for (int i = 0; i < columnDefs.length; i++) {
				if (StringUtil.isNotBlank(columnDefs[i])) {
					columnDefParts.add(columnDefs[i]);
				}
			}

			/*
			 * columnName
			 */

			String columnName = columnDefParts.get(columnDefParts.size() - 1);
			viewInfo.setColumnName(columnName);

			/*
			 * orgColumnName
			 */

			String orgColumnName = columnDefs[1];
			if (columnDefParts.size() > COLUMN_DEF_EXPR_SIZE) {
				orgColumnName = "expr";
			}
			viewInfo.setOrgColumnName(orgColumnName);

			/*
			 * tableName
			 */

			String asTableName = columnDefs[0];
			if (columnDefParts.size() > COLUMN_DEF_EXPR_SIZE) {
				asTableName = "expr";
			}
			viewInfo.setTableName(asTableName);

			/*
			 * orgTableName
			 */

			int l = from.indexOf(viewInfo.getTableName());

			int m = from.lastIndexOf("`", l - 2);
			if (m >= 0) {
				int n = from.lastIndexOf("`", m - 1);
				viewInfo.setOrgTableName(from.substring(n + 1, m));
			} else {
				viewInfo.setOrgTableName(asTableName);
			}

			/*
			 * viewInfosに追加
			 */

			if (viewInfos == null) {
				viewInfos = new HashMap<String, ViewInfo>();
			}
			String propertyName = StringUtil.toCamelCase(viewInfo.getColumnName());
			viewInfos.put(propertyName, viewInfo);

			LOG.trace("`" + tableName + "`.`" + propertyName + "` = `" + viewInfo.getOrgTableName() + "`.`"
					+ viewInfo.getOrgColumnName() + "` AS `" + viewInfo.getTableName() + "`.`"
					+ viewInfo.getColumnName() + "`");
		}

		return viewInfos;
	}

	/**
	 * @param cn
	 *            cn
	 * @param tableName
	 *            tableName
	 * @return create view ddl
	 */
	private static String retrieveCreateViewSqlite(final Connection cn, final String tableName) {

		String ddl = null;

		ResultSet rs = null;
		try {
			String sql = "select * from sqlite_master where type = 'view' and name = '" + tableName + "';";
			rs = cn.createStatement().executeQuery(sql);
			if (rs.next()) {
				String createView = rs.getString("sql");
				int i = StringUtil.indexOfIgnoreCase(createView, SENTENCE_SELECT);
				ddl = createView.substring(i);
			}
		} catch (SQLException e) {
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return ddl;
	}

	/**
	 * @param cn
	 *            cn
	 * @param tableName
	 *            tableName
	 * @return create view ddl
	 */
	private static String retrieveCreateViewMySQL(final Connection cn, final String tableName) {

		String ddl = null;

		ResultSet rs = null;
		try {
			String sql = "show create view " + tableName;
			rs = cn.createStatement().executeQuery(sql);
			if (rs.next()) {
				String createView = rs.getString("create View");
				int i = StringUtil.indexOfIgnoreCase(createView, SENTENCE_SELECT);
				ddl = createView.substring(i);
			}
		} catch (SQLException e) {
		} finally {
			DbUtils.closeQuietly(rs);
		}

		return ddl;
	}

}
