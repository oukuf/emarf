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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.constants.model.ModelFieldTypes;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.model.Statement;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.CryptUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * クエリジェネレータ
 *
 * @author oukuf@golorp
 */
public final class QueryGenerator {

	/**
	 * デフォルトコンストラクタ
	 */
	private QueryGenerator() {
	}

	/**
	 * @param tableInfo
	 *            テーブルメタ情報
	 * @return insert文を返却するメソッド文字列
	 */
	public static String getInsertQuery(final TableInfo tableInfo) {

		// 主キー情報がなければ終了
		if (tableInfo.getPrimaryKeys() == null) {
			return null;
		}

		// テーブル名からモデル名を生成
		String tableName = tableInfo.getTableName();
		String modelName = StringUtil.toUpperCamelCase(tableName);

		StringBuilder sb = new StringBuilder();
		sb.append("    public static ").append(Statement.class.getName()).append(" ")
				.append(ModelFieldTypes.GET_INSERT_STATEMENT).append("(").append(modelName).append(" me) {\n");
		sb.append("        StringBuilder sb = new StringBuilder();\n");
		sb.append("        sb.append(\"INSERT INTO ").append(tableName).append(" \");\n");

		// フィールド出力用文字列
		sb.append("        StringBuilder fields = new StringBuilder();\n");

		// プレースホルダ出力用文字列
		sb.append("        StringBuilder places = new StringBuilder();\n");

		// プレースホルダ用値リスト
		sb.append("        ").append(List.class.getName()).append("<Object> values = new ")
				.append(ArrayList.class.getName()).append("<Object>();\n");

		// カラム定義でループ
		for (ColumnInfo columnInfo : tableInfo.getColumnInfos()) {

			// カラム名からプロパティ名を生成
			String columnName = columnInfo.getColumnName();
			String propertyName = columnInfo.getPropertyName();

			// 登録不可項目ならスキップ
			if (Models.AINT_INSERT_SET.isEnd(propertyName)) {
				continue;
			}

			/*
			 * 値がある場合のロジック
			 */

			sb.append("        if (me.").append(propertyName).append(" != null) {\n");

			appendFields(sb, columnName, "?");

			// プレースホルダ用値リスト
			sb.append("            values.add(");
			if (columnName.endsWith(BeanGenerator.CRYPT_SUFFIX)) {
				sb.append(CryptUtil.class.getName()).append(".encrypt(me.").append(propertyName).append(")");
			} else {
				sb.append("me.").append(propertyName);
			}
			sb.append(");\n");

			/*
			 * 値がない場合のロジック
			 */

			if (Models.AUTO_INSERT_MAP.containsKey(propertyName) || Models.AUTO_UPDATE_MAP.containsKey(propertyName)) {
				// 自動登録項目および自動更新項目の場合

				// 設定値を取得
				Object value = Models.AUTO_INSERT_MAP.get(propertyName);
				if (value == null) {
					value = Models.AUTO_UPDATE_MAP.get(propertyName);
				}

				if (value != null) {

					sb.append("        } else {\n");

					appendFields(sb, columnName, "?");

					// プレースホルダ用値リスト
					sb.append("            values.add(\"").append(value).append("\");\n");
				}

			} else if (propertyName.endsWith(Models.SEQ_SUFFIX)) {
				// 最大値自動取得項目

				Set<String> primaryKeys = tableInfo.getPrimaryKeys();

				sb.append("        } else {\n");

				appendFields(sb, columnName, getSeqSubQuery(tableName, columnName, primaryKeys, propertyName));

				// プレースホルダ用値リスト
				for (String primaryKey : primaryKeys) {
					String camelCase = StringUtil.toCamelCase(primaryKey);
					if (camelCase.equals(propertyName)) {
						break;
					}
					sb.append("            values.add(me.").append(camelCase).append(");\n");
				}

				// } else if
				// (propertyName.endsWith(Models.IDENTIFICATION_SUFFIX)) {
				//
				// if (DataSources.isOracle()) {
				//
				// Set<String> primaryKeys = tableInfo.getPrimaryKeys();
				//
				// if (primaryKeys.size() == 1 &&
				// primaryKeys.contains(columnName)) {
				//
				// StringBuilder sequence = new StringBuilder();
				// if (BeanGenerator.ORACLE_SEQUENCE_PREFIX != null) {
				// sequence.append(BeanGenerator.ORACLE_SEQUENCE_PREFIX);
				// }
				// sequence.append(tableName).append("_").append(columnName);
				// if (BeanGenerator.ORACLE_SEQUENCE_SUFFIX != null) {
				// sequence.append(BeanGenerator.ORACLE_SEQUENCE_SUFFIX);
				// }
				//
				// sb.append(" } else {\n");
				// appendFields(sb, columnName, sequence + ".nextval");
				// }
				// }
			}

			sb.append("        }\n");
		}

		sb.append("        fields.append(\")\");\n");
		sb.append("        places.append(\")\");\n");

		sb.append("        sb.append(fields.toString());\n");
		sb.append("        sb.append(\" VALUES \");\n");
		sb.append("        sb.append(places.toString());\n");

		sb.append("        return new ").append(Statement.class.getName())
				.append("(sb.toString(), values.toArray());\n");

		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * StringBuilderにstatement用文字列の追加
	 *
	 * @param sb
	 *            StringBuilder
	 * @param columnName
	 *            カラム名
	 * @param placeHolder
	 *            プレースホルダ
	 */
	private static void appendFields(final StringBuilder sb, final String columnName, final String placeHolder) {

		sb.append("            if (fields.length() == 0) {\n");
		sb.append("                fields.append(\"(\");\n");
		sb.append("            } else {\n");
		sb.append("                fields.append(\", \");\n");
		sb.append("            }\n");
		sb.append("            fields.append(\"").append(columnName).append("\");\n");

		sb.append("            if (places.length() == 0) {\n");
		sb.append("                places.append(\"(\");\n");
		sb.append("            } else {\n");
		sb.append("                places.append(\", \");\n");
		sb.append("            }\n");
		sb.append("            places.append(\"").append(placeHolder).append("\");\n");
	}

	/**
	 * @param tableName
	 *            最大値を取得するテーブル名
	 * @param columnName
	 *            最大値を取得するカラム名
	 * @param primaryKeys
	 *            主キーリスト
	 * @param propertyName
	 *            主キーから除外するプロパティ名
	 * @return 最大値取得用サブクエリ文字列
	 */
	public static String getSeqSubQuery(final String tableName, final String columnName, final Set<String> primaryKeys,
			final String propertyName) {

		StringBuilder where = new StringBuilder();
		for (String primaryKey : primaryKeys) {
			if (StringUtil.toCamelCase(primaryKey).equals(propertyName)) {
				break;
			}
			if (where.length() > 0) {
				where.append(" and");
			}
			where.append(" ").append(primaryKey).append(" = ?");
		}

		return new StringBuilder().append("(SELECT ").append(columnName).append(" FROM (SELECT COALESCE(MAX(")
				.append(columnName).append("), 0) AS ").append(columnName).append(" FROM ").append(tableName)
				.append(" WHERE").append(where).append(") sub) + 1").toString();
	}

	/**
	 * @param tableInfo
	 *            テーブルメタ情報
	 * @return select文を返却するメソッド文字列
	 */
	public static String getSelectQuery(final TableInfo tableInfo) {

		if (tableInfo.getPrimaryKeys() == null) {
			return null;
		}

		String tableName = tableInfo.getTableName();

		String modelName = StringUtil.toUpperCamelCase(tableName);

		StringBuilder sb = new StringBuilder();

		sb.append("    public static ").append(Statement.class.getName()).append(" ")
				.append(ModelFieldTypes.GET_SELECT_STATEMENT).append("(").append(modelName).append(" me) {\n");
		sb.append("        StringBuilder sb = new StringBuilder();\n");
		sb.append("        sb.append(\"SELECT * FROM ").append(tableName).append("\");\n");
		sb.append("        ").append(List.class.getName()).append("<Object> values = new ")
				.append(ArrayList.class.getName()).append("<Object>();\n");

		String propertyName = null;
		for (String primaryKey : tableInfo.getPrimaryKeys()) {
			if (propertyName == null) {
				sb.append("        sb.append(\" WHERE \");\n");
			} else {
				sb.append("        sb.append(\" AND \");\n");
			}
			propertyName = StringUtil.toCamelCase(primaryKey);
			sb.append("        sb.append(\"" + primaryKey + " = ?\");\n");

			if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName) || Fieldset.DATETIME_SUFFIX_SET.isEnd(propertyName)
					|| Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {
				String upperPropertyName = StringUtil.toUpperCamelCase(propertyName);
				sb.append("        values.add(me.format").append(upperPropertyName).append("());\n");
			} else {
				sb.append("        values.add(me.").append(propertyName).append(");\n");
			}
		}

		sb.append("        return new ").append(Statement.class.getName())
				.append("(sb.toString(), values.toArray());\n");

		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param tableInfo
	 *            テーブルメタ情報
	 * @return update文を返却するメソッド文字列
	 */
	public static String getUpdateQuery(final TableInfo tableInfo) {

		// 主キー情報がなければ終了
		if (tableInfo.getPrimaryKeys() == null) {
			return null;
		}

		// テーブル名からモデル名を生成
		String tableName = tableInfo.getTableName();
		String modelName = StringUtil.toUpperCamelCase(tableName);

		StringBuilder sb = new StringBuilder();

		sb.append("    public static ").append(Statement.class.getName()).append(" ")
				.append(ModelFieldTypes.GET_UPDATE_STATEMENT).append("(").append(modelName).append(" me) {\n");
		sb.append("        StringBuilder sb = new StringBuilder();\n");
		sb.append("        sb.append(\"UPDATE ").append(tableName).append(" SET \");\n");

		// プレースホルダ出力用文字列
		sb.append("        StringBuilder sets = new StringBuilder();\n");

		// プレースホルダ用値リスト
		sb.append("        ").append(List.class.getName()).append("<Object> values = new ")
				.append(ArrayList.class.getName()).append("<Object>();\n");

		// カラム定義でループ
		for (ColumnInfo column : tableInfo.getColumnInfos()) {

			// カラム名からプロパティ名を生成
			String columnName = column.getColumnName();
			String propertyName = column.getPropertyName();

			// 主キーならスキップ（where句用に後で処理する）
			if (tableInfo.getPrimaryKeys().contains(columnName)) {
				continue;
			}

			// 更新不可項目ならスキップ
			if (Models.AINT_UPDATE_SET.isEnd(propertyName)) {
				continue;
			}

			if (Models.AUTO_UPDATE_MAP.containsKey(propertyName)) {
				// 自動更新項目の場合

				// プレースホルダ出力用文字列
				sb.append("        if (sets.length() > 0) {\n");
				sb.append("            sets.append(\", \");\n");
				sb.append("        }\n");
				sb.append("        sets.append(\"").append(columnName).append(" = ?\");\n");

				// プレースホルダ用値リスト
				Object value = Models.AUTO_UPDATE_MAP.get(propertyName);
				sb.append("        values.add(");
				if (value == null) {
					sb.append(value);
				} else {
					sb.append("\"").append(value).append("\"");
				}
				sb.append(");\n");

			} else {

				sb.append("        if (me.isEdit_").append(propertyName).append("()) {\n");

				sb.append("            if (sets.length() > 0) {\n");
				sb.append("                sets.append(\", \");\n");
				sb.append("            }\n");
				sb.append("            sets.append(\"").append(columnName).append(" = ?\");\n");

				if (columnName.endsWith(BeanGenerator.CRYPT_SUFFIX)) {

					sb.append("            values.add(").append(CryptUtil.class.getName()).append(".encrypt(me.")
							.append(propertyName).append("));\n");

				} else if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName)
						|| Fieldset.DATETIME_SUFFIX_SET.isEnd(propertyName)
						|| Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {

					sb.append("            values.add(me.format").append(StringUtil.toUpperCamelCase(columnName))
							.append("());\n");

				} else {

					sb.append("            values.add(me.").append(propertyName).append(");\n");
				}

				sb.append("        }\n");
			}
		}

		sb.append("        sb.append(sets.toString());\n");

		String propertyName = null;
		for (String primaryKey : tableInfo.getPrimaryKeys()) {
			if (propertyName == null) {
				sb.append("        sb.append(\" WHERE \");\n");
			} else {
				sb.append("        sb.append(\" AND \");\n");
			}
			propertyName = StringUtil.toCamelCase(primaryKey);
			sb.append("        sb.append(\"").append(primaryKey).append(" = ?\");\n");

			if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName) || Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {
				String upperPropertyName = StringUtil.toUpperCamelCase(propertyName);
				sb.append("        values.add(me.format").append(upperPropertyName).append("());\n");
			} else {
				sb.append("        values.add(me.").append(propertyName).append(");\n");
			}

		}

		sb.append(duplicateCheck(tableInfo));

		sb.append("        return new ").append(Statement.class.getName())
				.append("(sb.toString(), values.toArray());\n");

		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * @param tableInfo
	 *            テーブルメタ情報
	 * @return delete文を返却するメソッド文字列
	 */
	public static String getDeleteQuery(final TableInfo tableInfo) {

		if (tableInfo.getPrimaryKeys() == null) {
			return null;
		}

		String tableName = tableInfo.getTableName();

		String modelName = StringUtil.toUpperCamelCase(tableName);

		StringBuilder sb = new StringBuilder();
		sb.append("    public static ").append(Statement.class.getName()).append(" ")
				.append(ModelFieldTypes.GET_DELETE_STATEMENT).append("(").append(modelName).append(" me) {\n");
		sb.append("        StringBuilder sb = new StringBuilder();\n");
		sb.append("        sb.append(\"DELETE FROM ").append(tableName).append("\");\n");
		sb.append("        ").append(List.class.getName()).append("<Object> values = new ")
				.append(ArrayList.class.getName()).append("<Object>();\n");

		StringBuilder sb2 = new StringBuilder();

		for (String primaryKey : tableInfo.getPrimaryKeys()) {

			// boolean isAnd = false;

			String propertyName = StringUtil.toCamelCase(primaryKey);

			if (sb2.length() == 0) {
				sb2.append("        sb.append(\" WHERE \");\n");
			} else {
				// isAnd = true;
				// sb2.append(" if (me.").append(propertyName).append(" != null)
				// {\n");
				sb2.append("        sb.append(\" AND \");\n");
			}

			sb2.append("        sb.append(\"").append(primaryKey).append(" = ?\");\n");

			if (Fieldset.DATE_SUFFIX_SET.isEnd(propertyName) || Fieldset.DATETIME_SUFFIX_SET.isEnd(propertyName)
					|| Fieldset.TIME_SUFFIX_SET.isEnd(propertyName)) {
				String upperPropertyName = StringUtil.toUpperCamelCase(propertyName);
				sb2.append("        values.add(me.format").append(upperPropertyName).append("());\n");
			} else {
				sb2.append("        values.add(me.").append(propertyName).append(");\n");
			}

			// if (isAnd) {
			// sb2.append(" }\n");
			// }
		}

		sb.append(sb2);

		sb.append(duplicateCheck(tableInfo));

		sb.append("        return new ").append(Statement.class.getName())
				.append("(sb.toString(), values.toArray());\n");

		sb.append("    }\n");

		return sb.toString();
	}

	/**
	 * 排他制御
	 *
	 * @param tableInfo
	 *            tableInfo
	 * @return 排他制御用文字列
	 */
	private static String duplicateCheck(final TableInfo tableInfo) {

		String versionNo = "VERSION_NO";

		if (StringUtil.isNotBlank(BeanGenerator.VERSION_NO)) {
			versionNo = BeanGenerator.VERSION_NO;
		} else if (tableInfo.getColumnInfo(versionNo) == null) {
			// VERSION_NO列がなければエラー
			throw new SystemError(MessageKeys.ABEND_VERSIONNO_NONE);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("        sb.append(\" AND \");\n");
		sb.append("        sb.append(\"").append(versionNo).append(" = ?\");\n");
		sb.append("        values.add(me.").append(StringUtil.toCamelCase(versionNo)).append(");\n");

		return sb.toString();
	}

}
