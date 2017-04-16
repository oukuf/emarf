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

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.exception.ApplicationError;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.Checks;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.ParseUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 入力チェックメソッド文字列生成クラス
 *
 * @author oukuf@golorp
 */
public final class CheckGenerator {

	/** マスタチェック用パラメータの要素数 */
	private static final int MASTER_RULE_PARAMS_LENGTH = 3;

	/** コンストラクタ */
	private CheckGenerator() {
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param isPk
	 *            主キーかのフラグ
	 * @return 必須チェック文字列
	 */
	public static String getNullableCheck(final ColumnInfo columnInfo, final boolean isPk) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		String operation = "入力";
		if (ModelUtil.isOptionable(propertyName)) {
			operation = "選択";
		}
		if (!isPk && propertyName.endsWith(Models.ID_SUFFIX)) {
			operation = "選択";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("        if (").append(StringUtil.class.getName()).append(".isBlank(o)) {\n");
		sb.append("            throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_REQUIRED.name())
				.append(", \"").append(columnMei).append("\", \"").append(operation).append("\");\n");
		sb.append("        }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return 型変換チェック文字列
	 */
	public static String getParseCheck(final ColumnInfo columnInfo) {

		String[] dataTypes = App.get(AppKey.BEANGENERATOR_TYPE_PREFIX + columnInfo.getDataType()).split("\\.");
		String type = dataTypes[dataTypes.length - 1];

		String propertyName = StringUtil.toCamelCase(columnInfo.getColumnName());

		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            try {\n");
		sb.append("                ").append(propertyName).append(" = ").append(ParseUtil.class.getName()).append(".to")
				.append(type).append("(o);\n");
		sb.append("            } catch (Exception e) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE.name()).append("_")
				.append(columnInfo.getDataType()).append(", \"").append(columnMei).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return 固定長チェック文字列
	 */
	public static String getFixLengthCheck(final ColumnInfo columnInfo) {

		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (o.toString().length() != ").append(columnInfo.getColumnSize()).append(") {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_FIX_LENGTH.name())
				.append(", \"").append(columnMei).append("\", \"").append(columnInfo.getColumnSize()).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param minLength
	 *            最小文字列長
	 * @return 最小文字列長チェック文字列
	 */
	public static String getMinLengthCheck(final ColumnInfo columnInfo, final String minLength) {

		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (o.toString().length() < ").append(minLength).append(" || o.toString().length() > ")
				.append(columnInfo.getColumnSize()).append(") {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_LENGTH_RANGE.name())
				.append(", \"").append(columnMei).append("\", \"").append(minLength).append("\", \"")
				.append(columnInfo.getColumnSize()).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @return 最大文字列長チェック文字列
	 */
	public static String getMaxLengthCheck(final ColumnInfo columnInfo) {

		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (o.toString().length() > ").append(columnInfo.getColumnSize()).append(") {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_MAX_LENGTH.name())
				.append(", \"").append(columnMei).append("\", \"").append(columnInfo.getColumnSize()).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param ruleName
	 *            メッセージに表示するルール名称
	 * @param rule
	 *            正規表現
	 * @return 正規表現チェック文字列
	 */
	public static String getReCheck(final ColumnInfo columnInfo, final String ruleName, final String rule) {

		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!o.toString().matches(\"").append(rule).append("\")) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_INVALID.name())
				.append(", \"").append(columnMei).append("\", \"").append(ruleName).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param lessThan
	 *            未満チェック閾値
	 * @return 未満チェック文字列
	 */
	public static String getNumericLtCheck(final ColumnInfo columnInfo, final String lessThan) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName).append(" < ").append(lessThan).append(")) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_LESS_THAN.name())
				.append(", \"").append(columnMei).append("\", \"").append(lessThan).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param lessEqual
	 *            以下チェック閾値
	 * @return 以下チェック文字列
	 */
	public static String getNumericLeCheck(final ColumnInfo columnInfo, final String lessEqual) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName).append(" <= ").append(lessEqual).append(")) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_LESS_EQUAL.name())
				.append(", \"").append(columnMei).append("\", \"").append(lessEqual).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param greaterEqual
	 *            以上チェック閾値
	 * @return 以上チェック文字列
	 */
	public static String getNumericGeCheck(final ColumnInfo columnInfo, final String greaterEqual) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName).append(" >= ").append(greaterEqual).append(")) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".")
				.append(MessageKeys.ERRORS_VALIDATE_GREATER_EQUAL.name()).append(", \"").append(columnMei)
				.append("\", \"").append(greaterEqual).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param greaterThan
	 *            超過チェック閾値
	 * @return 超過チェック文字列
	 */
	public static String getNumericGtCheck(final ColumnInfo columnInfo, final String greaterThan) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName).append(" > ").append(greaterThan).append(")) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_GREATER_THAN.name())
				.append(", \"").append(columnMei).append("\", \"").append(greaterThan).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param lessThan
	 *            過去チェック閾値
	 * @return 過去チェック文字列
	 */
	public static String getDateLtCheck(final ColumnInfo columnInfo, final String lessThan) {

		String columnName = columnInfo.getColumnName();
		String propertyName = StringUtil.toCamelCase(columnName);
		String columnMei = columnInfo.getColumnMei();

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName)
				.append(".before(jp.co.golorp.emarf.util.DateUtils.parse10fig(\"").append(lessThan)
				.append("\")))) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_BEFORE.name())
				.append(", \"").append(columnMei).append("\", \"").append(lessThan).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param lessEqual
	 *            以前チェック閾値
	 * @return 以前チェック文字列
	 */
	public static String getDateLeCheck(final ColumnInfo columnInfo, final String lessEqual) {

		String columnName = columnInfo.getColumnName();
		String columnMei = columnInfo.getColumnMei();

		String propertyName = StringUtil.toCamelCase(columnName);

		StringBuilder sb = new StringBuilder();
		sb.append("            if (").append(propertyName)
				.append(".after(jp.co.golorp.emarf.util.DateUtils.parse10fig(\"").append(lessEqual).append("\"))) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_NOT_AFTER.name())
				.append(", \"").append(columnMei).append("\", \"").append(lessEqual).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param greaterEqual
	 *            以後チェック閾値
	 * @return 以後チェック文字列
	 */
	public static String getDateGeCheck(final ColumnInfo columnInfo, final String greaterEqual) {

		String columnName = columnInfo.getColumnName();
		String columnMei = columnInfo.getColumnMei();

		String propertyName = StringUtil.toCamelCase(columnName);

		StringBuilder sb = new StringBuilder();
		sb.append("            if (").append(propertyName)
				.append(".before(jp.co.golorp.emarf.util.DateUtils.parse10fig(\"").append(greaterEqual)
				.append("\"))) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_NOT_BEFORE.name())
				.append(", \"").append(columnMei).append("\", \"").append(greaterEqual).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param greaterThan
	 *            未来チェック閾値
	 * @return 未来チェック文字列
	 */
	public static String getDateGtCheck(final ColumnInfo columnInfo, final String greaterThan) {

		String columnName = columnInfo.getColumnName();
		String columnMei = columnInfo.getColumnMei();

		String propertyName = StringUtil.toCamelCase(columnName);

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!(").append(propertyName)
				.append(".after(jp.co.golorp.emarf.util.DateUtils.parse10fig(\"").append(greaterThan)
				.append("\")))) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_AFTER.name())
				.append(", \"").append(columnMei).append("\", \"").append(greaterThan).append("\");\n");
		sb.append("            }\n");

		return sb.toString();
	}

	/**
	 * @param columnInfo
	 *            カラム情報
	 * @param master
	 *            マスタチェック用パラメータ文字列
	 * @return マスタチェック文字列
	 */
	public static String getMasterCheck(final ColumnInfo columnInfo, final String master) {

		String columnName = columnInfo.getColumnName();

		String propertyName = StringUtil.toCamelCase(columnName);

		String columnMei = columnInfo.getColumnMei();

		String modelName = null;

		StringBuilder criteria = new StringBuilder();

		String[] sources = master.split("\\[|\\]");
		for (String source : sources) {
			String[] parts = source.split(",");

			if (parts.length == MASTER_RULE_PARAMS_LENGTH) {
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i].trim();
					if (part.equals("@{propertyName}")) {
						parts[i] = "\"" + propertyName + "\"";
					} else if (part.equals("@{value}")) {
						parts[i] = propertyName;
					} else {
						parts[i] = "\"" + part + "\"";
					}
				}

				if (criteria.length() == 0) {
					modelName = parts[0].replaceAll("^\"|\"$", "");
					criteria.append(Criteria.class.getName()).append(".equal(").append(parts[0]).append(", ")
							.append(parts[1]).append(", ").append(parts[2]).append(")");
				} else {
					criteria.append(".eq(").append(parts[0]).append(", ").append(parts[1]).append(", ").append(parts[2])
							.append(")");
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append("            if (!").append(propertyName).append(".equals(\"").append(Checks.NOCHECK_VALUE)
				.append("\") && ").append(Models.class.getName()).append(".count(\"").append(modelName).append("\", ")
				.append(criteria).append(") != 1) {\n");
		sb.append("                throw new ").append(ApplicationError.class.getName()).append("(")
				.append(MessageKeys.class.getName()).append(".").append(MessageKeys.ERRORS_VALIDATE_NOT_EXIST.name())
				.append(", \"").append(columnMei).append("\", ").append(propertyName).append(");\n");
		sb.append("            }\n");

		return sb.toString();
	}

}
