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
package jp.co.golorp.emarf.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.constants.model.ModelFieldTypes;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.collection.AppSet;
import jp.co.golorp.emarf.sql.Connections;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.util.DateUtil;
import jp.co.golorp.emarf.util.IOUtil;
import jp.co.golorp.emarf.util.LogUtil;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * データベースIO
 *
 * @author oukuf@golorp
 */
public final class Models {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(Models.class);

	/** 取得可能最大レコード件数 */
	private static final String ROW_MAX = App.get(AppKey.MODELS_ROW_MAX);

	/** 識別子サフィックス */
	public static final String ID_SUFFIX = App.get(AppKey.MODELS_ID_SUFFIX);

	/** 最大連番自動補完項目名サフィックスSet */
	public static final String SEQ_SUFFIX = App.get(AppKey.MODELS_SEQ_SUFFIX);

	/** オラクルシーケンスプレフィクス */
	private static final String ORACLE_SEQUENCE_PREFIX = App.get(AppKey.MODELS_ORACLE_SEQUENCE_PREFIX);

	/** オラクルシーケンスサフィックス */
	private static final String ORACLE_SEQUENCE_SUFFIX = App.get(AppKey.MODELS_ORACLE_SEQUENCE_SUFFIX);

	/** 登録処理対象外項目名 */
	public static final AppSet<String> AINT_INSERT_SET = App.getSet(AppKey.MODELS_AINT_INSERTS);

	/** 更新処理対象外項目名 */
	public static final AppSet<String> AINT_UPDATE_SET = App.getSet(AppKey.MODELS_AINT_UPDATES);

	/** 自動登録項目値情報 */
	public static final Map<String, String> AUTO_INSERT_MAP = App.getMap(AppKey.MODELS_AUTO_INSERT_VALUES);

	/** 自動更新項目値情報 */
	public static final Map<String, String> AUTO_UPDATE_MAP = App.getMap(AppKey.MODELS_AUTO_UPDATE_VALUES);

	/** ページ繰り方法 */
	private static PagingBy pagingBy = null;

	/**
	 * ページ繰り方法の列挙子
	 *
	 * @author oukuf@golorp
	 */
	public enum PagingBy {

		/** limitでページ繰り（mysqlの場合） */
		limit,

		/** rownumでページ繰り（oracleの場合） */
		rownum
	}

	/**
	 * デフォルトコンストラクタ
	 */
	private Models() {
	}

	/**
	 * insertを実行
	 *
	 * @param model
	 *            登録するモデル
	 * @return 登録結果のモデル
	 */
	public static Model create(final Model model) {

		// モデル名
		String modelName = model.getClass().getSimpleName();

		// テーブル情報
		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo == null) {
			throw new SystemError(MessageKeys.ABEND_DATA_INSERT, modelName);
		}

		// テーブル名
		String tableName = tableInfo.getTableName();

		// 主キーリスト
		Set<String> primaryKeys = tableInfo.getPrimaryKeys();

		// OracleSequenceでユニークキーを取得してみる
		if (primaryKeys.size() == 1) {
			fillByOracleSequence(model, tableName, primaryKeys.iterator().next());
		}

		// キー重複チェック
		Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);
		if (primaryPropertyNames != null) {

			// 一つ目の主キーを取得
			Iterator<String> iPrimaryPropertyNames = primaryPropertyNames.iterator();
			String primaryPropertyName = iPrimaryPropertyNames.next();
			Object o = model.get(primaryPropertyName);
			if (o != null) {

				// 主キー値が全て揃っているか
				boolean isExistAllKey = true;

				// criteriaに一つ目の主キーを設定
				Criteria c = Criteria.equal(modelName, primaryPropertyName, o);

				// criteriaに二つ目以降の主キーを設定
				while (iPrimaryPropertyNames.hasNext()) {
					primaryPropertyName = iPrimaryPropertyNames.next();
					o = model.get(primaryPropertyName);
					if (o == null) {
						isExistAllKey = false;
						break;
					}
					c.eq(primaryPropertyName, o);
				}

				// 主キー値がすべてそろっている場合は重複エラーチェック
				if (isExistAllKey && Models.getModel(modelName, c) != null) {
					throw new ApplicationError(MessageKeys.ERRORS_DATA_DUPLICATE);
				}
			}
		}

		/*
		 * 登録処理
		 */

		Statement statement = Models.getStatement(ModelFieldTypes.GET_INSERT_STATEMENT, model);
		if (Models.regist(statement.getSql(), statement.getParams()) != 1) {
			throw new SystemError(MessageKeys.ABEND_DATA_INSERT, tableInfo.getTableMei());
		}

		/*
		 * 登録したモデルの主キーを補完
		 */

		// 主キーのプロパティ名でループ
		for (String primaryPropertyName : primaryPropertyNames) {

			// 主キーに値があればスキップ
			if (model.get(primaryPropertyName) != null) {
				continue;
			}

			// カラム情報からカラム名を取得
			ColumnInfo primaryKeyInfo = tableInfo.getColumnInfo(primaryPropertyName);
			String primaryKeyName = primaryKeyInfo.getColumnName();

			if (primaryPropertyName.endsWith(Models.ID_SUFFIX)) {
				// AUTO_INCREMENTのID値を取得

				String sql = "select last_insert_id() as " + primaryKeyName + " from " + tableName;
				List<Map<String, Object>> datas = Models.getDatas(sql);
				if (datas == null) {
					continue;
				}

				Map<String, Object> data = datas.get(0);
				BigInteger value = (BigInteger) data.get(primaryPropertyName);
				if (value.intValue() >= 0) {
					model.set(primaryPropertyName, value.intValue());
				}

			} else if (primaryPropertyName.endsWith(Models.SEQ_SUFFIX)) {
				// 連番の最大値を取得

				StringBuilder sb = new StringBuilder();
				List<Object> params = new ArrayList<Object>();

				// 既に主キープロパティ内でループしているため、ネストループ用に再度取得する
				Set<String> primaryPropertyNames2 = ModelUtil.getPrimaryPropertyNames(modelName);
				for (String primaryPropertyName2 : primaryPropertyNames2) {

					// 最大値取得対象のカラムならスキップ
					if (primaryPropertyName2.equals(primaryPropertyName)) {
						break;
					}

					// 最大値取得対象のカラム以外の主キー項目でwhere句を生成
					if (sb.length() > 0) {
						sb.append(" and");
					}
					ColumnInfo primaryKeyInfo2 = tableInfo.getColumnInfo(primaryPropertyName2);
					String columnName2 = primaryKeyInfo2.getColumnName();
					Object columnValue2 = model.get(primaryPropertyName2);
					sb.append(" ").append(columnName2).append(" = ?");
					params.add(columnValue2);
				}

				// 最大値を取得
				String sql = "SELECT MAX(" + primaryKeyName + ") AS " + primaryKeyName + " from " + tableName
						+ " WHERE " + sb.toString();
				List<Map<String, Object>> datas = Models.getDatas(sql, params.toArray());
				if (datas == null) {
					continue;
				}

				// 最大値が取得できた場合はその最大値を、取得できなかった場合は「0」を設定
				Map<String, Object> data = datas.get(0);
				Object value = data.get(primaryPropertyName);
				if (value != null) {
					model.set(primaryPropertyName, value);
				} else {
					model.set(primaryPropertyName, 0);
				}
			}
		}

		return Models.refer(model);
	}

	/**
	 * 主キー検索
	 *
	 * @param model
	 *            検索するモデル
	 * @return 単一検索結果
	 */
	public static Model refer(final Model model) {
		String modelName = model.getClass().getSimpleName();
		Statement statement = Models.getStatement(ModelFieldTypes.GET_SELECT_STATEMENT, model);
		return Models.getBean(modelName, statement.getSql(), statement.getParams());
	}

	/**
	 * updateを実行
	 *
	 * @param model
	 *            更新するモデル
	 */
	public static void update(final Model model) {
		Statement statement = Models.getStatement(ModelFieldTypes.GET_UPDATE_STATEMENT, model);
		if (Models.regist(statement.getSql(), statement.getParams()) != 1) {
			String modelName = model.getClass().getSimpleName();
			String tableMei = MetaData.getTableInfo(modelName).getTableMei();
			throw new SystemError(MessageKeys.ABEND_DATA_UPDATE, tableMei);
		}
	}

	/**
	 * deleteを実行
	 *
	 * @param model
	 *            削除するモデル
	 */
	public static void delete(final Model model) {
		Statement statement = Models.getStatement(ModelFieldTypes.GET_DELETE_STATEMENT, model);
		if (Models.regist(statement.getSql(), statement.getParams()) != 1) {
			String modelName = model.getClass().getSimpleName();
			String tableMei = MetaData.getTableInfo(modelName).getTableMei();
			throw new SystemError(MessageKeys.ABEND_DATA_DELETE, tableMei);
		}
	}

	/**
	 * @param modelName
	 *            truncateするモデル名
	 */
	public static void truncate(final String modelName) {

		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo == null) {
			throw new SystemError(MessageKeys.ABEND_DATA_DELETE, modelName);
		}

		String tableName = tableInfo.getTableName();
		try {
			Models.regist("TRUNCATE TABLE " + tableName);
		} catch (Exception e) {
			// truncateをサポートしない場合（sqliteなど）
			Models.regist("DELETE FROM " + tableName);
		}
	}

	/**
	 * 該当件数を取得
	 *
	 * @param modelName
	 *            モデル名
	 * @param c
	 *            検索条件
	 * @return 検索件数
	 */
	public static int count(final String modelName, final Criteria c) {

		if (c == null) {
			return 0;
		}

		Statement where = getWhere(modelName, c);

		String selectSQL = "SELECT COUNT(*) CNT FROM " + where.getSql();

		Object[] params = where.getParams();

		List<Map<String, Object>> datas = getDatas(selectSQL, params);

		Map<String, Object> data = datas.get(0);

		return Integer.parseInt(data.get("cnt").toString());
	}

	/**
	 * criteriaによる単一select
	 *
	 * @param modelName
	 *            モデル名
	 * @param c
	 *            検索条件
	 * @return モデル
	 */
	public static Model getModel(final String modelName, final Criteria c) {

		List<Model> models = getModels(modelName, c, null, null);

		if (models == null || models.size() == 0) {
			return null;
		}

		if (models.size() > 1) {
			throw new SystemError(MessageKeys.ERRORS_DATA_PLURAL);
		}

		return (Model) models.get(0);
	}

	/**
	 * criteriaによる複数select
	 *
	 * @param modelName
	 *            モデル名
	 * @param c
	 *            検索条件
	 * @return 複数検索結果
	 */
	public static List<Model> getModels(final String modelName, final Criteria c) {
		return getModels(modelName, c, null, null);
	}

	/**
	 * criteriaによる複数select
	 *
	 * @param modelName
	 *            モデル名
	 * @param c
	 *            Criteria
	 * @param rows
	 *            取得行数
	 * @param page
	 *            取得ページ番号
	 * @return 複数検索結果
	 */
	public static List<Model> getModels(final String modelName, final Criteria c, final String rows,
			final String page) {

		// 行数指定。指定がなければ規定値。
		String rowsValue = ROW_MAX;
		if (rows != null) {
			rowsValue = rows;
		}

		// ページ指定。指定がなければ規定値。
		String pageValue = "1";
		if (page != null) {
			pageValue = page;
		}

		// where句を取得。なければ終了。
		Statement where = getWhere(modelName, c);
		if (where == null) {
			return null;
		}

		String sql = "SELECT * FROM " + where.getSql();

		int r = Integer.valueOf(rowsValue);
		int p = Integer.valueOf(pageValue);
		int offset = r * (p - 1);

		if (pagingBy == null || pagingBy == PagingBy.limit) {
			try {
				sql += " limit " + offset + ", " + rowsValue;
				List<Model> beans = getBeans(modelName, sql, where.getParams());
				pagingBy = PagingBy.limit;
				return beans;
			} catch (Exception e) {
			}
		}

		if (pagingBy == null || pagingBy == PagingBy.rownum) {
			try {
				int f = offset + 1;
				int t = offset + r;
				sql = "SELECT * FROM (SELECT ROWNUM AS RNO, A.* FROM (" + sql + ") A) WHERE RNO BETWEEN " + f + " AND "
						+ t;
				List<Model> beans = getBeans(modelName, sql, where.getParams());
				pagingBy = PagingBy.rownum;
				return beans;
			} catch (Exception e) {
			}
		}

		return null;
	}

	/**
	 * モデルの主キー項目にoracleシーケンスを利用して値を設定
	 *
	 * @param model
	 *            Model
	 * @param tableName
	 *            モデルのテーブル名
	 * @param columnName
	 *            シーケンスから取得する主キー名
	 */
	private static void fillByOracleSequence(final Model model, final String tableName, final String columnName) {

		StringBuilder sb = new StringBuilder("SELECT ");

		if (ORACLE_SEQUENCE_PREFIX != null) {
			sb.append(ORACLE_SEQUENCE_PREFIX);
		}

		sb.append(tableName).append("_").append(columnName);

		if (ORACLE_SEQUENCE_SUFFIX != null) {
			sb.append(ORACLE_SEQUENCE_SUFFIX);
		}

		sb.append(".NEXTVAL AS ").append(columnName).append(" FROM DUAL");

		try {
			List<Map<String, Object>> datas = Models.getDatas(sb.toString());
			Map<String, Object> data = datas.get(0);
			String propertyName = StringUtil.toCamelCase(columnName);
			Object value = data.get(propertyName);
			if (value != null) {
				model.set(propertyName, value);
			}
		} catch (Exception e) {
			LOG.trace(tableName + "." + columnName + " is not use Oracle Sequence.");
		}
	}

	/**
	 * モデルの変更有無判定用に、取得したプロパティの値を変更前プロパティに退避
	 *
	 * @param <T>
	 *            モデル型
	 * @param bean
	 *            処理対象のbean
	 */
	private static <T> void cp2org(final T bean) {
		Class<?> clazz = bean.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (field.getName().startsWith(BeanGenerator.ORG_PREFIX)) {
				try {
					String orgName = field.getName();
					String propertyName = orgName.replaceFirst(BeanGenerator.ORG_PREFIX, "");
					Field property = clazz.getDeclaredField(propertyName);
					field.setAccessible(true);
					property.setAccessible(true);
					Object value = property.get(bean);
					field.set(bean, value);
				} catch (Exception e) {
					throw new SystemError(e);
				}
			}
		}
	}

	/**
	 * 登録処理・更新処理・削除処理を実行
	 *
	 * @param sql
	 *            実行するsql
	 * @param params
	 *            sqlのパラメータ
	 * @return 処理件数
	 */
	private static int regist(final String sql, final Object... params) {

		Date sysDate = DateUtil.getDate();

		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			if (StringUtil.isNotBlank(param) && param.toString().equals("@{sysDate}")) {
				params[i] = new Timestamp(sysDate.getTime());
			}
		}

		statementLog(getRawSql(sql, params));

		QueryRunner runner = new QueryRunner();
		Connection cn = Connections.get();
		try {
			return runner.update(cn, sql, params);
		} catch (SQLException e) {
			throw new SystemError(e);
		}
	}

	/**
	 * crud用ステートメントを取得
	 *
	 * @param kind
	 *            取得するステートメントの名称
	 * @param model
	 *            ステートメントを取得するモデル
	 * @return sqlとパラメータのset
	 */
	private static Statement getStatement(final ModelFieldTypes kind, final Model model) {

		Class<?> clazz = model.getClass();

		Method method = null;
		try {
			method = clazz.getMethod(kind.toString(), clazz);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new SystemError(e);
		}

		Object o = null;
		try {
			o = method.invoke(clazz, model);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SystemError(e);
		}

		return (Statement) o;
	}

	/**
	 * criteria内の関連モデルを評価してjoin句以降のsqlを生成
	 *
	 * @param modelName
	 *            モデル名
	 * @param c
	 *            検索条件
	 * @return sqlとパラメータのset
	 */
	private static Statement getWhere(final String modelName, final Criteria c) {

		StringBuilder sql = new StringBuilder();

		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo == null) {
			return null;
		}

		String tableName = tableInfo.getTableName();

		sql.append(tableName);

		// テーブル結合
		if (c != null) {

			for (String modelName2 : c.getModels()) {

				if (modelName.equals(modelName2)) {
					continue;
				}

				TableInfo tableInfo2 = MetaData.getTableInfo(modelName2);

				String tableName2 = tableInfo2.getTableName();

				sql.append(" INNER JOIN ").append(tableName2);

				List<RelateColumnMap> relateColumnsList = ModelUtil.getRelateProperties(modelName, modelName2);
				if (relateColumnsList != null) {

					sql.append(" ON ");

					StringBuilder sql2 = new StringBuilder();

					for (RelateColumnMap relateColumns : relateColumnsList) {

						for (Entry<String, String> relateColumn : relateColumns.entrySet()) {

							String propertyName = relateColumn.getKey();
							String propertyName2 = relateColumn.getValue();

							ColumnInfo columnInfo = tableInfo.getColumnInfo(propertyName);
							ColumnInfo columnInfo2 = tableInfo2.getColumnInfo(propertyName2);

							String columnName = columnInfo.getColumnName();
							String columnName2 = columnInfo2.getColumnName();

							if (sql2.length() > 0) {
								sql2.append(" AND ");
							}

							sql2.append(tableName).append(".").append(columnName).append(" = ").append(tableName2)
									.append(".").append(columnName2);
						}
					}

					sql.append(sql2);
				}
			}
		}

		List<Object> params = new ArrayList<Object>();

		if (c != null) {
			if (c.is()) {
				sql.append(" WHERE ");
				params.addAll(c.toParameter());
			}
			sql.append(c);
		}

		return new Statement(sql.toString(), params.toArray(new Object[params.size()]));
	}

	/**
	 * Modelに依存しないデータベースIO
	 *
	 * @param sql
	 *            sql
	 * @param params
	 *            params
	 * @return List
	 */
	private static List<Map<String, Object>> getDatas(final String sql, final Object... params) {

		// アドホックなSQL文字列を取得
		String rawSql = getRawSql(sql, params);

		// SQL文字列をキーにキャッシュ済みならそれを返す
		List<Map<String, Object>> datas = ModelsCache.get(rawSql);
		if (datas != null) {
			return datas;
		}

		// ログ出力
		statementLog(rawSql);

		// ステートメントを取得
		PreparedStatement ps = null;
		try {
			ps = Connections.get().prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setString(i + 1, String.valueOf(params[i]));
			}

			// 返却値を初期化
			datas = new ArrayList<Map<String, Object>>();

			ResultSet rs = null;
			try {

				// データ取得してループ
				rs = ps.executeQuery();
				while (rs.next()) {

					// レコードデータを初期化
					Map<String, Object> data = new LinkedHashMap<String, Object>();

					// ResultSetのMETA情報を取得
					ResultSetMetaData meta = rs.getMetaData();

					// META情報からカラム数を取得してループ
					int columnCount = meta.getColumnCount();
					for (int i = 1; i <= columnCount; i++) {

						// カラム名を取得
						String columnName = meta.getColumnName(i);

						// プロパティ名を取得
						String propertyName = StringUtil.toCamelCase(columnName);

						String key = propertyName;
						if (data.containsKey(propertyName)) {
							String modelName = StringUtil.toUpperCamelCase(meta.getTableName(i));
							key = modelName + "." + propertyName;
						}

						// レコードデータを設定
						data.put(key, rs.getObject(columnName));
					}

					// レコードを追加
					datas.add(data);
				}

			} catch (SQLException e) {
				throw new SystemError(e);
			} finally {
				IOUtil.closeQuietly(rs);
			}

		} catch (SQLException e) {
			throw new SystemError(e);
		} finally {
			IOUtil.closeQuietly(ps);
		}

		// SQL文字列をキーにキャッシュ
		// ModelsCache.set(rawSql, datas);

		return datas;
	}

	/**
	 * モデルに準拠したデータベースIO
	 *
	 * @param <T>
	 *            取得するモデル型
	 * @param modelName
	 *            モデル名
	 * @param sql
	 *            発行するsql
	 * @param params
	 *            sqlパラメータ
	 * @return 指定したモデル型のリスト
	 */
	private static <T> List<T> getBeans(final String modelName, final String sql, final Object... params) {

		Class<?> type = ModelUtil.getBlankModel(modelName).getClass();

		RowProcessor rp = getRowProcessor(modelName);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ResultSetHandler rsh = new BeanListHandler(type, rp);

		List<T> list = query(sql, rsh, params);
		for (T bean : list) {
			cp2org(bean);
		}

		return list;
	}

	/**
	 * モデルに準拠したデータベースIO
	 *
	 * @param <T>
	 *            取得するモデル型
	 * @param modelName
	 *            モデル名
	 * @param sql
	 *            発行するsql
	 * @param params
	 *            sqlパラメータ
	 * @return 指定したモデル
	 */
	private static <T> T getBean(final String modelName, final String sql, final Object... params) {

		Class<?> type = ModelUtil.getBlankModel(modelName).getClass();

		RowProcessor rp = getRowProcessor(modelName);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		ResultSetHandler rsh = new BeanHandler(type, rp);

		T bean = query(sql, rsh, params);
		if (bean != null) {
			cp2org(bean);
		}

		return bean;
	}

	/**
	 * ログ出力
	 *
	 * @param rawSql
	 *            ログ出力するsql文字列
	 */
	private static void statementLog(final String rawSql) {

		LOG.info("<query> " + rawSql);

		LogUtil.callerLog();
	}

	/**
	 * ログ出力またはキャッシュ用にアドホックなsqlに置換
	 *
	 * @param sql
	 *            sql文字列
	 * @param params
	 *            パラメータ
	 * @return パラメータ置換済みのsql文字列
	 */
	private static String getRawSql(final String sql, final Object... params) {

		String query = sql;

		for (Object param : params) {
			query = query.replaceFirst("\\?", "'" + String.valueOf(param).replaceAll("\\\\", "\\\\\\\\") + "'");
		}

		return query;
	}

	/**
	 * dbutils用のカラムマッピング
	 *
	 * @param modelName
	 *            モデル名
	 * @return RowProcessor
	 */
	private static RowProcessor getRowProcessor(final String modelName) {

		Map<String, String> mapping = new LinkedHashMap<String, String>();

		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);

		TableInfo tableInfo = MetaData.getTableInfo(modelName);

		for (String propertyName : propertyMeis.keySet()) {
			ColumnInfo columnInfo = tableInfo.getColumnInfo(propertyName);
			String columnName = columnInfo.getColumnName();
			mapping.put(columnName, propertyName);
		}

		BeanProcessor convert = new BeanProcessor(mapping);

		return new BasicRowProcessor(convert);
	}

	/**
	 * 検索用データベースIO
	 *
	 * @param <T>
	 *            取得するモデル型
	 * @param sql
	 *            sql文字列
	 * @param rsh
	 *            ResultSetHandler
	 * @param params
	 *            パラメータ
	 * @return リスト型またはモデル型のオブジェクト
	 */
	@SuppressWarnings("unchecked")
	private static <T> T query(final String sql, final ResultSetHandler<?> rsh, final Object... params) {

		String rawSql = getRawSql(sql, params);

		T result = (T) ModelsCache.get(rawSql);
		if (result != null) {
			return result;
		}

		statementLog(rawSql);

		QueryRunner runner = new QueryRunner();

		Connection cn = Connections.get();

		try {

			if (params == null || params.length == 0) {
				result = (T) runner.query(cn, sql, rsh);
			} else {
				result = (T) runner.query(cn, sql, rsh, params);
			}

			ModelsCache.set(rawSql, result);

			return result;

		} catch (Exception e) {
			throw new SystemError(e);
		}

	}

}
