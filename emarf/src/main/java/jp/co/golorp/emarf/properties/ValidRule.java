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
package jp.co.golorp.emarf.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import jp.co.golorp.emarf.constants.ValidRuleKey;

/**
 * 入力チェックルール
 *
 * @author oukuf@golorp
 */
public final class ValidRule {

	/**
	 * ValidRule.propertiesファイル内容をルール化
	 */
	private static final Map<String, Map<String, String>> VALID_RULES = new HashMap<String, Map<String, String>>() {
		{
			// プロパティファイルを取得
			ResourceBundle bundle = ResourceBundle.getBundle(ValidRule.class.getSimpleName());

			// プロパティキーでループ
			for (String key : bundle.keySet()) {

				// キーを「.」で分割
				String[] keys = key.split("\\.");

				// キーが「.」区切りでなければスキップ
				int keyLength = keys.length;
				if (keyLength < 2) {
					continue;
				}

				// 「.」区切りの最後の要素の直前までを接尾辞として取得
				String suffix = keys[0];
				for (int i = 1; i < keyLength - 1; i++) {
					suffix += "." + keys[i];
				}

				// 「.」区切りの最後の要素をチェック名称として取得
				String name = keys[keyLength - 1];

				// プロパティ値を正規表現として取得
				String check = bundle.getString(key);

				// ルールを追加
				Map<String, String> validRule = this.get(suffix);
				if (validRule == null) {
					validRule = new HashMap<String, String>();
					this.put(suffix, validRule);
				}
				validRule.put(name, check);
			}
		}
	};

	/**
	 * コンストラクタ
	 */
	private ValidRule() {
	}

	/**
	 * @param columnName
	 *            カラム名
	 * @return チェック名称とチェック内容のmap
	 */
	public static Map<String, String> getRules(final String columnName) {

		Map<String, String> ret = null;

		// ルールでループ
		for (Entry<String, Map<String, String>> validRule : VALID_RULES.entrySet()) {

			// 接尾辞を取得
			String suffix = validRule.getKey();

			// カラム名の末尾が接尾辞に合致しなければスキップ
			if (!columnName.matches("(?i)^.*" + suffix + "$")) {
				continue;
			}

			// ルールを取得
			Map<String, String> rules = validRule.getValue();

			// 返却値がなければ初期化
			if (ret == null) {
				ret = new HashMap<String, String>();
			}

			// 返却値にルールを追加
			for (Entry<String, String> rule : rules.entrySet()) {
				String name = rule.getKey();
				String check = rule.getValue();
				ret.put(name, check);
			}
		}

		return ret;
	}

	/**
	 * @param validRules
	 *            チェックルール
	 * @return 正規表現のみのチェックルール
	 */
	public static Map<String, String> getReRules(final Map<String, String> validRules) {

		if (validRules == null) {
			return null;
		}

		Map<String, String> ret = null;

		for (Entry<String, String> validRule : validRules.entrySet()) {

			String name = validRule.getKey();
			String check = validRule.getValue();

			if (name.equals(ValidRuleKey.NOTNULL) || name.equals(ValidRuleKey.MINLENGTH)
					|| name.equals(ValidRuleKey.NUMERIC_LT) || name.equals(ValidRuleKey.NUMERIC_LE)
					|| name.equals(ValidRuleKey.NUMERIC_GE) || name.equals(ValidRuleKey.NUMERIC_GT)
					|| name.equals(ValidRuleKey.DATE_LT) || name.equals(ValidRuleKey.DATE_LE)
					|| name.equals(ValidRuleKey.DATE_GE) || name.equals(ValidRuleKey.DATE_GT)
					|| name.equals(ValidRuleKey.MASTER)) {
				continue;
			}

			if (ret == null) {
				ret = new HashMap<String, String>();
			}

			ret.put(name, check);
		}

		return ret;
	}

}
