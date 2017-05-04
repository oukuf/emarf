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
package jp.co.golorp.emarf.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.constants.ValidRuleKey;
import jp.co.golorp.emarf.constants.model.ModelFieldTypes;
import jp.co.golorp.emarf.constants.model.RelationTypes;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.properties.FormatRule;
import jp.co.golorp.emarf.properties.ValidRule;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.sql.relation.RelationMap;
import jp.co.golorp.emarf.tag.lib.iterate.model.Tables;
import jp.co.golorp.emarf.util.CryptUtil;
import jp.co.golorp.emarf.util.IOUtil;
import jp.co.golorp.emarf.util.ParseUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * データベースからテーブル定義を取得し各テーブルクラスを生成
 *
 * @author oukuf@golorp
 */
public final class BeanGenerator {

	/**
	 * BeanGenerator単体実行
	 *
	 * @param args
	 *            起動引数
	 */
	public static void main(final String[] args) {

		String path = null;
		if (args != null && args.length > 0 && StringUtil.isNotBlank(args[0])) {
			path = args[0];
		}

		BeanGenerator.generate(path);
		// FIXME 単体で実行するときには必要。UnitTestだとこれがあると終わらなくなる。
		// System.exit(0);
	}

	/**
	 * デフォルトコンストラクタ
	 */
	private BeanGenerator() {
	}

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(BeanGenerator.class);

	/** URLセパレータ */
	protected static final String SEP = EmarfServlet.SEP;

	/** ライブラリフォルダ名 */
	private static final String LIB = "lib";

	/** 編集前値プロパティプレフィクス */
	public static final String ORG_PREFIX = "org_";

	/** モデルを生成するか */
	private static final String GENERATE = App.get(AppKey.BEANGENERATOR_GENERATE);

	/** classesフォルダ */
	private static final String CLASSES = App.get(AppKey.BEANGENERATOR_CLASSES);

	/** 各モデルクラスが出力されたパッケージ */
	public static final String PACKAGE = App.get(AppKey.BEANGENERATOR_PACKAGE);

	/** 履歴連番判定用サフィックス */
	private static final String HISTORY_SUFFIX = App.get(AppKey.BEANGENERATOR_HISTORY_SUFFIX);

	/** 暗号化項目判定用サフィックス */
	public static final String CRYPT_SUFFIX = App.get(AppKey.BEANGENERATOR_CRYPT_SUFFIX);

	/** VERSION_NOカラム名 */
	public static final String VERSION_NO = App.get(AppKey.BEANGENERATOR_VERSIONNO);

	/** 削除フラグカラム名 */
	public static final String DELETE_F = App.get(AppKey.BEANGENERATOR_DELETE_F);

	/**
	 * モデル生成
	 *
	 * @param path
	 *            クラスフォルダ
	 */
	public static void generate(final String path) {

		if (!StringUtil.is(GENERATE)) {
			return;
		}

		/*
		 * ルートディレクトリ作成
		 */

		// クラスフォルダの指定があれば取得
		String classes = CLASSES;
		if (path != null) {
			classes = path;
		}

		String rootPackage = PACKAGE;
		String rootFolder = rootPackage.replace(Model.SEP, File.separator);
		String rootPath = classes + File.separator + rootFolder;
		File rootDir = new File(rootPath);
		if (rootDir.exists() && !classes.contains("/test-classes/")) {
			IOUtil.delete(rootDir);
		}
		rootDir.mkdirs();

		LOG.info("generate models at [" + rootPath + "].");

		/*
		 * 全Javaファイル出力
		 */

		// 全JAVAファイルパス
		Map<String, String> javaPaths = new LinkedHashMap<String, String>();

		List<TableInfo> tableInfos = MetaData.getTableInfos();
		for (TableInfo tableInfo : tableInfos) {

			// JAVAファイル作成
			String javaPath = writeJavaFile(rootPath, tableInfo);

			// テーブル名毎にjavaファイルパスを退避
			String tableName = tableInfo.getTableName();
			javaPaths.put(tableName, javaPath);
		}

		/*
		 * 関連の確認
		 */

		Map<String, RelationMap> tablesRelations = getAllRelations(tableInfos);

		for (Entry<String, String> javaFile : javaPaths.entrySet()) {

			String tableName = javaFile.getKey();
			String javaPath = javaFile.getValue();

			StringBuilder sb = new StringBuilder();

			sb.append("    public static final ").append(RelationMap.class.getName()).append(" ")
					.append(ModelFieldTypes.RELATION_MAP).append(" = new ").append(RelationMap.class.getName())
					.append("() {\n");
			sb.append("        {\n");

			RelationMap relations = tablesRelations.get(tableName);

			if (relations != null) {

				for (Entry<RelationTypes, RelateTablesMap> relation : relations.entrySet()) {

					RelateTablesMap relateTables = relation.getValue();

					sb.append("            put(").append(RelationTypes.class.getName()).append(".")
							.append(relation.getKey()).append(", new ").append(RelateTablesMap.class.getName())
							.append("() {\n");
					sb.append("                {\n");

					for (Entry<String, List<RelateColumnMap>> relateTable : relateTables.entrySet()) {

						String tableName2 = relateTable.getKey();
						List<RelateColumnMap> relateColumnsList = relateTable.getValue();

						String modelName2 = StringUtil.toUpperCamelCase(tableName2);
						sb.append("                    put(\"").append(modelName2).append("\", new ")
								.append(ArrayList.class.getName()).append("<").append(RelateColumnMap.class.getName())
								.append(">() {\n");
						sb.append("                        {\n");

						for (RelateColumnMap relateColumns : relateColumnsList) {

							sb.append("                            add(new ").append(RelateColumnMap.class.getName())
									.append("() {\n");
							sb.append("                                {\n");

							for (Entry<String, String> relateColumn : relateColumns.entrySet()) {
								String propertyName = StringUtil.toCamelCase(relateColumn.getKey());
								String propertyName2 = StringUtil.toCamelCase(relateColumn.getValue());
								sb.append("                                    put(\"").append(propertyName)
										.append("\", \"").append(propertyName2).append("\");\n");
							}

							sb.append("                                }\n");
							sb.append("                            });\n");
						}

						sb.append("                        }\n");
						sb.append("                    });\n");
					}

					sb.append("                }\n");
					sb.append("            });\n");
				}
			}

			sb.append("        }\n");
			sb.append("    };\n");

			sb.append("}\n");

			/*
			 * 関連情報を追記
			 */

			IOUtil.writeAndCloseQuietly(javaPath, sb.toString(), true);
		}

		/*
		 * コンパイル
		 */

		LOG.info("------------------------------ start compile models ------------------------------");

		for (Entry<String, String> javaFile : javaPaths.entrySet()) {
			String javaFilePath = javaFile.getValue();
			BeanGenerator.compile(javaFilePath);
		}

		LOG.info("------------------------------ finish compile models ------------------------------");
	}

	/**
	 * 指定したルートパス内にパッケージ構成を作成し、テーブル情報に基づいてjavaファイルを出力する
	 *
	 * @param rootPath
	 *            ルートパッケージの絶対パス
	 * @param tableInfo
	 *            任意のテーブル情報
	 * @return 出力したjavaファイルパス
	 */
	private static String writeJavaFile(final String rootPath, final TableInfo tableInfo) {

		// テーブル名
		String tableName = tableInfo.getTableName();

		// テーブル論理名
		String tableMei = tableInfo.getTableMei();

		// クラス名
		String modelName = StringUtil.toUpperCamelCase(tableName);

		/*
		 * 出力開始
		 */

		StringBuilder sb = new StringBuilder();

		// パッケージ
		sb.append("package ").append(PACKAGE).append(";\n");

		// クラス宣言
		sb.append("public class ").append(modelName).append(" extends ").append(Model.class.getName()).append(" {\n");

		// テーブル論理名
		sb.append("    public static final String ").append(ModelFieldTypes.MODEL_MEI).append(" = ");
		if (tableMei == null) {
			sb.append("null");
		} else {
			sb.append("\"").append(tableMei).append("\"");
		}
		sb.append(";\n");

		/*
		 * 主キー情報を追加
		 */

		Set<String> primaryKeys = tableInfo.getPrimaryKeys();
		sb.append("    public static final ").append(Set.class.getName()).append("<String> ")
				.append(ModelFieldTypes.PK_PROPERTY_NAMES).append(" = ");
		if (primaryKeys == null) {
			sb.append("null");
		} else {
			sb.append("new ").append(LinkedHashSet.class.getName()).append("<String>() {\n");
			sb.append("        {\n");
			for (String primaryKey : primaryKeys) {
				String propertyName = StringUtil.toCamelCase(primaryKey);
				sb.append("            add(\"").append(propertyName).append("\");\n");
			}
			sb.append("        }\n");
			sb.append("    }");
		}
		sb.append(";\n");

		/*
		 * フィールド情報を追加
		 */

		sb.append("    public static final ").append(Map.class.getName()).append("<String, String> ")
				.append(ModelFieldTypes.PROPERTY_MEIS).append(" = new ").append(LinkedHashMap.class.getName())
				.append("<String, String>() {\n");
		sb.append("        {\n");
		for (ColumnInfo column : tableInfo.getColumnInfos()) {
			sb.append("            put(\"").append(column.getPropertyName()).append("\", \"")
					.append(column.getColumnMei()).append("\");\n");
		}
		sb.append("        }\n");
		sb.append("    };\n");

		/*
		 * フィールド
		 */

		for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {

			// データ型を解決
			String dataType = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType());

			String propertyName = columnInfo.getPropertyName();

			// プロパティ
			sb.append("    private ").append(dataType).append(" ").append(propertyName).append(";\n");

			// 編集前プロパティ
			sb.append("    private ").append(dataType).append(" ").append(ORG_PREFIX).append(propertyName)
					.append(";\n");

			// 編集有無フラグ
			sb.append("    public boolean isEdit_").append(propertyName).append("() {\n");
			sb.append("        return !").append(Objects.class.getName()).append(".equals(").append(propertyName)
					.append(", ").append(ORG_PREFIX).append(propertyName).append(");\n");
			sb.append("    }\n");

			// getter
			sb.append(BeanGenerator.getGetter(columnInfo));

			// getter
			sb.append(BeanGenerator.getFormater(columnInfo));

			// setter
			sb.append(BeanGenerator.getSetter(columnInfo));

			// setter
			boolean isPk = primaryKeys != null && primaryKeys.contains(columnInfo.getColumnName());
			sb.append(BeanGenerator.getValidator(columnInfo, isPk, tableInfo.isView()));
		}

		// INSERTクエリ
		String insertQuery = QueryGenerator.getInsertQuery(tableInfo);
		if (insertQuery != null) {
			sb.append(insertQuery);
		}

		// SELECTクエリ
		String selectQuery = QueryGenerator.getSelectQuery(tableInfo);
		if (selectQuery != null) {
			sb.append(selectQuery);
		}

		// UPDATEクエリ
		String updateQuery = QueryGenerator.getUpdateQuery(tableInfo);
		if (updateQuery != null) {
			sb.append(updateQuery);
		}

		// DELETEクエリ
		String deleteQuery = QueryGenerator.getDeleteQuery(tableInfo);
		if (deleteQuery != null) {
			sb.append(deleteQuery);
		}

		String javaPath = rootPath + File.separator + modelName + ".java";

		IOUtil.writeAndCloseQuietly(javaPath, sb.toString(), false);

		return javaPath;
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return getter文字列
	 */
	private static String getGetter(final ColumnInfo columnInfo) {

		String dataType = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType());

		String upperCamelCase = StringUtil.toUpperCamelCase(columnInfo.getColumnName());

		String camelCase = StringUtil.toCamelCase(columnInfo.getColumnName());

		StringBuilder sb = new StringBuilder();
		sb.append("    public ").append(dataType).append(" get").append(upperCamelCase).append("() {\n");
		sb.append("        return this.").append(camelCase).append(";\n");
		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return フォーマットメソッド文字列
	 */
	private static String getFormater(final ColumnInfo columnInfo) {

		// app.propertiesからデータ型の文字列を取得
		String[] dataTypes = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType()).split("\\.");
		String type = dataTypes[dataTypes.length - 1];

		// プロパティ名を取得
		String columnName = columnInfo.getColumnName();
		String camelCase = StringUtil.toCamelCase(columnName);
		String upperCamelCase = StringUtil.toUpperCamelCase(columnName);

		// FormatRule.propertiesからpattern文字列を取得
		String pattern = FormatRule.get(columnName);

		StringBuilder sb = new StringBuilder("    public String format").append(upperCamelCase).append("() {\n");

		if (columnName.endsWith(CRYPT_SUFFIX)) {

			// 暗号化プロパティの場合
			sb.append("        return ").append(CryptUtil.class.getName()).append(".decrypt(this.").append(camelCase)
					.append(");\n");

		} else if (pattern != null && !type.equals(String.class.getSimpleName())) {

			// String以外でpattern文字列がある場合
			// （sqliteだとDateがなくVarChar(String)で作られるため）
			sb.append("        return ").append(ParseUtil.class.getName()).append(".format").append(type)
					.append("(this.").append(camelCase).append(", \"").append(pattern).append("\");\n");

		} else {

			// 上記以外の場合
			sb.append("        if (this.").append(camelCase).append(" == null) {\n");
			sb.append("            return null;\n");
			sb.append("        }\n");
			sb.append("        return String.valueOf(this.").append(camelCase).append(");\n");
		}

		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return setter文字列
	 */
	private static String getSetter(final ColumnInfo columnInfo) {

		String dataType = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType());

		String upperCamelCase = StringUtil.toUpperCamelCase(columnInfo.getColumnName());

		String camelCase = StringUtil.toCamelCase(columnInfo.getColumnName());

		StringBuilder sb = new StringBuilder();
		sb.append("    public void set").append(upperCamelCase).append("(" + dataType + " " + camelCase + ") {\n");
		sb.append("        this.").append(camelCase).append(" = ").append(camelCase).append(";\n");
		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param isPk
	 *            主キーかのフラグ
	 * @param isView
	 *            VIEWかのフラグ
	 * @return validatorメソッド文字列
	 */
	private static String getValidator(final ColumnInfo columnInfo, final boolean isPk, final boolean isView) {

		String dataType = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType());

		String camelCase = StringUtil.toCamelCase(columnInfo.getColumnName());

		String upperCamelCase = StringUtil.toUpperCamelCase(columnInfo.getColumnName());

		Map<String, String> rules = ValidRule.getRules(columnInfo.getColumnName());

		/*
		 * 出力開始
		 */

		StringBuilder sb = new StringBuilder();
		sb.append("    public void validate").append(upperCamelCase).append("(Object o) {\n");

		/*
		 * 必須チェック
		 */

		if (!isView) {
			if (columnInfo.getNullable() == 0 || columnInfo.getIsNullable().equals("NO")
					|| (rules != null && rules.containsKey(ValidRuleKey.NOTNULL))) {
				sb.append(CheckGenerator.getNullableCheck(columnInfo, isPk));
			}
		}

		// 必須チェック以外
		sb.append("        ").append(dataType).append(" ").append(camelCase).append("= null;\n");
		sb.append("        if (").append(StringUtil.class.getName()).append(".isNotBlank(o)) {\n");

		/*
		 * 型チェック
		 */

		sb.append(CheckGenerator.getParseCheck(columnInfo));

		/*
		 * 型式チェック
		 */

		boolean isReCheck = false;

		Map<String, String> reRules = ValidRule.getReRules(rules);
		if (reRules != null && reRules.size() > 0) {
			for (Entry<String, String> reRule : reRules.entrySet()) {
				sb.append(CheckGenerator.getReCheck(columnInfo, reRule.getKey(), reRule.getValue()));
			}
			isReCheck = true;
		}

		/*
		 * レングスチェック
		 */

		if (columnInfo.getTypeName().equals("CHAR")) {
			// 固定長チェック
			sb.append(CheckGenerator.getFixLengthCheck(columnInfo));
		} else if (rules != null && rules.containsKey(ValidRuleKey.MINLENGTH)) {
			// 最短長チェック
			String minLength = rules.get(ValidRuleKey.MINLENGTH);
			sb.append(CheckGenerator.getMinLengthCheck(columnInfo, minLength));
		} else if (columnInfo.getTypeName().contains("CHAR") || !isReCheck) {
			// 最大長チェック
			sb.append(CheckGenerator.getMaxLengthCheck(columnInfo));
		}

		/*
		 * 数値チェック
		 */

		// 未満チェック
		if (rules != null && rules.containsKey(ValidRuleKey.NUMERIC_LT)) {
			String lessThan = rules.get(ValidRuleKey.NUMERIC_LT);
			sb.append(CheckGenerator.getNumericLtCheck(columnInfo, lessThan));
		}

		// 以下チェック
		if (rules != null && rules.containsKey(ValidRuleKey.NUMERIC_LE)) {
			String lessEqual = rules.get(ValidRuleKey.NUMERIC_LE);
			sb.append(CheckGenerator.getNumericLeCheck(columnInfo, lessEqual));
		}

		// 以上チェック
		if (rules != null && rules.containsKey(ValidRuleKey.NUMERIC_GE)) {
			String greaterEqual = rules.get(ValidRuleKey.NUMERIC_GE);
			sb.append(CheckGenerator.getNumericGeCheck(columnInfo, greaterEqual));
		}

		// 超過チェック
		if (rules != null && rules.containsKey(ValidRuleKey.NUMERIC_GT)) {
			String greaterThan = rules.get(ValidRuleKey.NUMERIC_GT);
			sb.append(CheckGenerator.getNumericGtCheck(columnInfo, greaterThan));
		}

		/*
		 * 日付チェック
		 */

		// 過去チェック
		if (rules != null && rules.containsKey(ValidRuleKey.DATE_LT)) {
			String lessThan = rules.get(ValidRuleKey.DATE_LT);
			sb.append(CheckGenerator.getDateLtCheck(columnInfo, lessThan));
		}

		// 以前チェック
		if (rules != null && rules.containsKey(ValidRuleKey.DATE_LE)) {
			String lessEqual = rules.get(ValidRuleKey.DATE_LE);
			sb.append(CheckGenerator.getDateLeCheck(columnInfo, lessEqual));
		}

		// 以降チェック
		if (rules != null && rules.containsKey(ValidRuleKey.DATE_GE)) {
			String greaterEqual = rules.get(ValidRuleKey.DATE_GE);
			sb.append(CheckGenerator.getDateGeCheck(columnInfo, greaterEqual));
		}

		// 未来チェック
		if (rules != null && rules.containsKey(ValidRuleKey.DATE_GT)) {
			String greaterThan = rules.get(ValidRuleKey.DATE_GT);
			sb.append(CheckGenerator.getDateGtCheck(columnInfo, greaterThan));
		}

		/*
		 * マスタチェック
		 */

		// FIXME 複合キーに対応するか？
		if (rules != null && rules.containsKey(ValidRuleKey.MASTER)) {
			String master = rules.get(ValidRuleKey.MASTER);
			sb.append(CheckGenerator.getMasterCheck(columnInfo, master));
		}

		sb.append("        }\n");

		sb.append("        this.").append(camelCase).append(" = ").append(camelCase).append(";\n");
		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param tableInfos
	 *            全テーブル情報
	 * @return スキーマ内の全関連情報
	 */
	private static Map<String, RelationMap> getAllRelations(final List<TableInfo> tableInfos) {

		// 全テーブルの関連情報
		Map<String, RelationMap> allRelations = new TreeMap<String, RelationMap>();

		// 比較元の主キーを一番満足する祖先モデルの組み合わせ
		// <比較元モデル名, <比較先モデル名, Set<比較先主キー>>>
		Map<String, Map<String, Set<String>>> allUncles = new LinkedHashMap<String, Map<String, Set<String>>>();

		/*
		 * 比較元テーブルの主キー情報でループ
		 */

		for (TableInfo motoTableInfo : tableInfos) {

			// 比較元のテーブル名と主キー情報を取得
			String motoTableName = motoTableInfo.getTableName();
			Set<String> motoPrimaryKeys = motoTableInfo.getPrimaryKeys();

			// 関連情報を初期化
			RelationMap relationMap = new RelationMap();
			allRelations.put(motoTableName, relationMap);

			// 複合親情報を初期化
			Map<String, Set<String>> uncles = new LinkedHashMap<String, Set<String>>();
			allUncles.put(motoTableName, uncles);

			/*
			 * 主キー同士の確認（第二正規）
			 */

			getPrimaryRelation(motoTableInfo, tableInfos, relationMap, uncles);

			/*
			 * 参照外部キーの確認（第三正規）
			 */

			// 比較元テーブルの主キー以外のカラム名リストを取得
			Set<String> orgMotoColumnNames = new LinkedHashSet<String>();
			for (ColumnInfo columnInfo : motoTableInfo.getColumnInfos()) {
				if (motoPrimaryKeys == null || !motoPrimaryKeys.contains(columnInfo.getColumnName())) {
					orgMotoColumnNames.add(columnInfo.getColumnName());
				}
			}

			// 参照先リストを初期化
			RelateTablesMap referTos = new RelateTablesMap();

			LOG.trace(motoTableName);

			// 全テーブル情報でループ
			for (TableInfo sakiTableInfo : tableInfos) {

				// 比較先が変わるたびに比較元をリフレッシュ
				Set<String> motoColumnNames = new LinkedHashSet<String>();
				motoColumnNames.addAll(orgMotoColumnNames);

				// 比較先のテーブル名を取得
				String sakiTableName = sakiTableInfo.getTableName();

				// 対象外のテーブルならスキップ
				if (Tables.HIDE_MODEL_SET.contains(StringUtil.toUpperCamelCase(sakiTableName))) {
					continue;
				}

				LOG.trace("    " + sakiTableName);

				List<RelateColumnMap> relateColumnMaps = new ArrayList<RelateColumnMap>();

				// 比較先の主キー情報を取得
				Set<String> sakiPrimaryKeys = sakiTableInfo.getPrimaryKeys();

				while (true) {

					// 比較先の主キーと自テーブルの主キー以外のカラムで合致情報を取得
					RelateColumnMap matchColumnMap = getMatchColumnMap(motoColumnNames, sakiPrimaryKeys);

					if (matchColumnMap != null) {
						for (Entry<String, String> matchColumn : matchColumnMap.entrySet()) {
							motoColumnNames.remove(matchColumn.getKey());
						}
					}

					// 合致数が比較先の主キー数に等しい場合
					if (matchColumnMap != null && matchColumnMap.size() > 0
							&& matchColumnMap.size() == sakiPrimaryKeys.size()) {
						relateColumnMaps.add(matchColumnMap);
					}

					// これ以上接尾辞が合致しないなら終了
					if (isEnd(motoColumnNames, sakiPrimaryKeys)) {
						break;
					}
				}

				if (!relateColumnMaps.isEmpty()) {
					referTos.put(sakiTableName, relateColumnMaps);
				}
			}

			for (Entry<String, List<RelateColumnMap>> referTo : referTos.entrySet()) {
				String sakiTableName = referTo.getKey();
				List<RelateColumnMap> matchCols = referTo.getValue();
				for (RelateColumnMap relateColumns : matchCols) {
					if (sakiTableName.equals(motoTableName)) {
						relationMap.addRelation(RelationTypes.RECURSIVE_TO, sakiTableName, relateColumns);
					} else {
						relationMap.addRelation(RelationTypes.REFER_TO, sakiTableName, relateColumns);
					}
				}
			}
		}

		/*
		 *
		 */

		setReferBy(allRelations);

		getSummaryRelation(tableInfos, allRelations, allUncles);

		omitSummaryRelation(allRelations);

		dumpRelations(allRelations);

		return allRelations;
	}

	/**
	 * @param allRelations
	 *            allRelations
	 */
	private static void setReferBy(final Map<String, RelationMap> allRelations) {

		for (Entry<String, RelationMap> relation : allRelations.entrySet()) {

			String tableName = relation.getKey();
			RelationMap relationMap = relation.getValue();

			RelateTablesMap referTablesMap = relationMap.get(RelationTypes.REFER_TO);

			if (referTablesMap == null) {
				referTablesMap = relationMap.get(RelationTypes.RECURSIVE_TO);
			} else {
				RelateTablesMap recursiveTablesMap = relationMap.get(RelationTypes.RECURSIVE_TO);
				if (recursiveTablesMap != null) {
					referTablesMap.putAll(recursiveTablesMap);
				}
			}

			if (referTablesMap == null) {
				continue;
			}

			for (Entry<String, List<RelateColumnMap>> referTo : referTablesMap.entrySet()) {
				String referTableName = referTo.getKey();
				List<RelateColumnMap> referColumnsList = referTo.getValue();

				for (RelateColumnMap relateColumns : referColumnsList) {

					RelateColumnMap matchColumnMap = new RelateColumnMap();
					for (Entry<String, String> relateColumn : relateColumns.entrySet()) {
						matchColumnMap.put(relateColumn.getValue(), relateColumn.getKey());
					}

					RelationMap referRelationMap = allRelations.get(referTableName);
					if (referTableName.equals(tableName)) {
						referRelationMap.addRelation(RelationTypes.RECURSIVE_BY, tableName, matchColumnMap);
					} else {
						referRelationMap.addRelation(RelationTypes.REFER_BY, tableName, matchColumnMap);
					}
				}
			}
		}
	}

	/**
	 * @param motoColumnNames
	 *            motoColumnNames
	 * @param sakiPrimaryKeys
	 *            sakiPrimaryKeys
	 * @return これ以上合致しないか
	 */
	private static boolean isEnd(final Set<String> motoColumnNames, final Set<String> sakiPrimaryKeys) {

		for (String motoColumnName : motoColumnNames) {
			if (sakiPrimaryKeys != null) {
				for (String sakiPrimaryKey : sakiPrimaryKeys) {
					if (motoColumnName.endsWith(sakiPrimaryKey) || sakiPrimaryKey.endsWith(motoColumnName)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * summaryをchildからomit
	 *
	 * @param allRelations
	 *            全Relation情報
	 */
	private static void omitSummaryRelation(final Map<String, RelationMap> allRelations) {

		for (RelationMap relations : allRelations.values()) {

			RelateTablesMap summaryBys = relations.get(RelationTypes.SUMMARY_BY);
			if (summaryBys == null) {
				continue;
			}

			RelateTablesMap children = relations.get(RelationTypes.CHILD);
			if (children == null) {
				continue;
			}

			for (Entry<String, List<RelateColumnMap>> summaryBy : summaryBys.entrySet()) {
				String summaryTableName = summaryBy.getKey();
				children.remove(summaryTableName);
			}
		}
	}

	/**
	 * 第二正規型 関連情報の設定
	 *
	 * @param tableInfo
	 *            第二正規情報調査対象のテーブル情報
	 * @param allTableInfos
	 *            全てのテーブル情報
	 * @param relationMap
	 *            関連情報を格納するオブジェクト
	 * @param uncles
	 *            集約情報を格納するオブジェクト
	 */
	private static void getPrimaryRelation(final TableInfo tableInfo, final List<TableInfo> allTableInfos,
			final RelationMap relationMap, final Map<String, Set<String>> uncles) {

		// 調査対象のテーブル名
		String motoTableName = tableInfo.getTableName();

		// 調査対象テーブルの主キー情報
		Set<String> motoPrimaryKeys = tableInfo.getPrimaryKeys();

		// 全テーブル情報でループ
		for (TableInfo sakiTableInfo : allTableInfos) {

			// 比較先テーブル名
			String sakiTableName = sakiTableInfo.getTableName();

			// 比較先テーブル主キー
			Set<String> sakiPrimaryKeys = sakiTableInfo.getPrimaryKeys();

			// 合致する主キー情報を取得
			Map<String, String> matchKeys = getMatchColumnMap(motoPrimaryKeys, sakiPrimaryKeys);

			// 主キー情報が一つも合致しなければスキップ
			if (matchKeys == null || matchKeys.size() == 0) {
				continue;
			}

			/*
			 * 関連の判定
			 */

			RelationTypes relation = null;

			int motoPrimarySize = motoPrimaryKeys.size();
			int sakiPrimarySize = sakiPrimaryKeys.size();
			int matchSize = matchKeys.size();

			if (motoPrimarySize == matchSize && sakiPrimarySize == matchSize) {
				// 主キーが全て合致

				// 自テーブルならスキップ
				if (sakiTableName.equals(motoTableName)) {
					continue;
				}

				relation = RelationTypes.BROTHER;

			} else if (motoPrimarySize == matchSize) {
				// 比較元の主キーは合致（比較先は子孫モデル）

				String lastKey2 = (String) sakiPrimaryKeys.toArray()[sakiPrimarySize - 1];

				if (lastKey2.endsWith(HISTORY_SUFFIX)) {

					if (sakiTableName.startsWith(motoTableName)) {
						relation = RelationTypes.HISTORY_BY;
					}

				} else {

					if (motoPrimarySize + 1 == sakiPrimarySize) {
						relation = RelationTypes.CHILD;
					} else {
						relation = RelationTypes.DESCENDANT;
					}
				}

			} else if (sakiPrimarySize == matchSize) {
				// 比較先の主キーは合致（比較先は祖先モデル）

				String lastKey = (String) motoPrimaryKeys.toArray()[motoPrimarySize - 1];

				if (lastKey.endsWith(HISTORY_SUFFIX)) {

					if (motoTableName.startsWith(sakiTableName)) {
						relation = RelationTypes.HISTORY_OF;
					}

				} else {

					if (motoPrimarySize == sakiPrimarySize + 1) {
						relation = RelationTypes.PARENT;
					} else {
						relation = RelationTypes.ANCESTOR;
					}
				}

				/*
				 * 比較元の主キーを一番満足する祖先モデルの組み合わせを取得
				 */

				boolean isUncle = true;
				String removeUncleName = null;
				for (Entry<String, Set<String>> uncle : uncles.entrySet()) {
					String uncleName = uncle.getKey();
					Set<String> unclePrimaryKeys = uncle.getValue();
					Map<String, String> matchKeys2 = getMatchColumnMap(sakiPrimaryKeys, unclePrimaryKeys);
					int uncleKeySize2 = unclePrimaryKeys.size();
					int matchSize2 = matchKeys2.size();

					// 既により多くの主キーがマッチするテーブルが退避済みならスキップ
					if (sakiPrimarySize < uncleKeySize2 && sakiPrimarySize == matchSize2) {
						isUncle = false;
					}

					// 既に退避されている主キーより多くの主キーがマッチするなら退避済みのテーブル名を取得
					if (sakiPrimarySize > uncleKeySize2 && uncleKeySize2 == matchSize2) {
						removeUncleName = uncleName;
					}
				}

				if (removeUncleName != null) {
					uncles.remove(removeUncleName);
				}

				if (isUncle) {
					uncles.put(sakiTableName, sakiPrimaryKeys);
				}
			}

			if (relation != null) {
				relationMap.addRelation(relation, sakiTableName, matchKeys);
			}
		}
	}

	/**
	 * @param columnNames
	 *            比較元テーブル列名配列
	 * @param columnNames2
	 *            比較先テーブル列名配列
	 * @return 合致するカラム名のmap
	 */
	private static RelateColumnMap getMatchColumnMap(final Set<String> columnNames, final Set<String> columnNames2) {

		// 何れかのカラム情報がなければ終了
		if (columnNames == null || columnNames2 == null) {
			return null;
		}

		RelateColumnMap matchColumnMap = new RelateColumnMap();

		String prefix = null;
		String prefix2 = null;

		// 比較元のカラム情報でループ
		for (String columnName : columnNames) {

			// 比較先のカラム情報でループ
			for (String columnName2 : columnNames2) {

				// 接尾辞が合致しないならスキップ
				if (!columnName.endsWith(columnName2) && !columnName2.endsWith(columnName)) {
					continue;
				}

				String columnPrefix = columnName;
				String column2Prefix = columnName2;
				if (columnName.length() < columnName2.length()) {
					columnPrefix = columnPrefix.replaceAll(columnName + "$", "");
					column2Prefix = column2Prefix.replaceAll(columnName + "$", "");
				} else {
					columnPrefix = columnPrefix.replaceAll(columnName2 + "$", "");
					column2Prefix = column2Prefix.replaceAll(columnName2 + "$", "");
				}

				if (prefix == null) {
					prefix = columnPrefix;
				}

				if (prefix2 == null) {
					prefix2 = column2Prefix;
				}

				if (columnPrefix.equals(prefix) && column2Prefix.equals(prefix2)) {

					LOG.trace("        columnName = " + columnName + ", columnName2 = " + columnName2 + "");

					if (matchColumnMap.get(columnName) == null) {
						matchColumnMap.put(columnName, columnName2);
					}
				}
			}
		}

		return matchColumnMap;
	}

	/**
	 * 全テーブルについて集約情報の評価
	 *
	 * @param tableInfos
	 *            全テーブル情報
	 * @param relation
	 *            全テーブルの関連情報
	 * @param allUncles
	 *            全テーブルの集約モデル情報
	 */
	private static void getSummaryRelation(final List<TableInfo> tableInfos, final Map<String, RelationMap> relation,
			final Map<String, Map<String, Set<String>>> allUncles) {

		for (TableInfo tableInfo : tableInfos) {

			// 比較元テーブル名
			String tableName = tableInfo.getTableName();

			// 比較元主キー情報
			Set<String> primaryKeys = tableInfo.getPrimaryKeys();

			// 比較元複合親情報
			Map<String, Set<String>> uncles = allUncles.get(tableName);

			// 複合親の主キー情報をマージ
			Set<String> summaryKeys = new HashSet<String>();
			if (uncles != null) {
				for (Set<String> uncleKeys : uncles.values()) {
					for (String uncleKey : uncleKeys) {
						summaryKeys.add(uncleKey);
					}
				}
			}

			if (primaryKeys != null && primaryKeys.size() == summaryKeys.size()) {
				// 複合親の主キーが比較元の主キーを全て満たす場合（集約モデルの場合）

				// 比較元の関連情報にサマリ元情報を追加
				for (Entry<String, Set<String>> uncle : uncles.entrySet()) {
					String uncleName = uncle.getKey();

					// 子モデルとして明細表示しないなら集約関係は考慮しない
					// （同じ主キーで優先すべきモデルが他にあると見なせる）
					if (Tables.HIDE_MODEL_SET.contains(StringUtil.toUpperCamelCase(uncleName))) {
						continue;
					}

					Set<String> uncleKeys = uncle.getValue();
					Map<String, String> matchKeys = getMatchColumnMap(primaryKeys, uncleKeys);
					RelationMap relationMap = relation.get(tableName);
					relationMap.addRelation(RelationTypes.SUMMARY_OF, uncleName, matchKeys);
				}

				// 比較先の関連情報にサマリ先情報を追加
				for (Entry<String, Set<String>> uncle : uncles.entrySet()) {
					String uncleName = uncle.getKey();

					// 子モデルとして明細表示しないなら集約関係は考慮しない
					// （同じ主キーで優先すべきモデルが他にあると見なせる）
					if (Tables.HIDE_MODEL_SET.contains(StringUtil.toUpperCamelCase(uncleName))) {
						continue;
					}

					Set<String> uncleKeys = uncle.getValue();
					Map<String, String> matchKeys = getMatchColumnMap(uncleKeys, primaryKeys);
					RelationMap relationMap = relation.get(uncleName);
					if (relationMap == null) {
						relationMap = new RelationMap();
						relation.put(uncleName, relationMap);
					}
					relationMap.addRelation(RelationTypes.SUMMARY_BY, tableName, matchKeys);
				}
			}
		}
	}

	/**
	 * クラスファイルを出力
	 *
	 * @param javaFilePath
	 *            javaファイルパス
	 */
	private static void compile(final String javaFilePath) {

		String classPath = System.getProperty("java.class.path", null);

		String pathes = "";
		File classes = new File(BeanGenerator.class.getResource(SEP).getPath());
		File lib = new File(classes.getParentFile().getAbsolutePath() + SEP + LIB);
		File[] files = lib.listFiles();
		if (files != null) {
			for (File file : files) {
				pathes += ";" + file.getAbsolutePath();
			}
		}

		// コンパイラの取得
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		// コンパイル
		int result = compiler.run(null, null, null, "-classpath", classPath + pathes, javaFilePath);

		// コンパイル結果の出力
		if (result == 0) {
			LOG.info("compile success. [" + javaFilePath + "]");
		} else {
			LOG.error("compile failure. [" + javaFilePath + "]");
		}

	}

	/**
	 * 関連情報のダンプ出力
	 *
	 * @param tableRelations
	 *            全テーブルの関連情報
	 */
	private static void dumpRelations(final Map<String, RelationMap> tableRelations) {
		LOG.info("------------------------------ relations ------------------------------");
		for (Entry<String, RelationMap> allRelation : tableRelations.entrySet()) {
			String tableName = allRelation.getKey();
			RelationMap relations = allRelation.getValue();
			LOG.info("■" + tableName);
			for (Entry<RelationTypes, RelateTablesMap> relation : relations.entrySet()) {
				RelateTablesMap relateTables = relation.getValue();
				LOG.info("    [" + relation.getKey() + "]");
				for (Entry<String, List<RelateColumnMap>> relateTable : relateTables.entrySet()) {
					String tableName2 = relateTable.getKey();
					List<RelateColumnMap> relateColumnsList = relateTable.getValue();
					for (RelateColumnMap relateColumns : relateColumnsList) {
						LOG.info("        □" + tableName2);
						int i = 0;
						for (Entry<String, String> relateColumn : relateColumns.entrySet()) {
							++i;
							String s = "├";
							if (i == relateColumns.size()) {
								s = "└";
							}
							String column = relateColumn.getKey();
							String column2 = relateColumn.getValue();
							LOG.info(
									"            " + s + tableName + "." + column + " = " + tableName2 + "." + column2);
						}
					}
				}
			}
		}
	}

}
