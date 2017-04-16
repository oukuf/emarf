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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.properties.collection.AppSet;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * アプリケーションプロパティファイル
 *
 * @author oukuf@golorp
 */
public final class App {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(App.class.getSimpleName());

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE_BASE = ResourceBundle.getBundle(App.class.getSimpleName() + "_base");

	/**
	 * コンストラクタ
	 */
	private App() {
	}

	/**
	 * キーに合致する値を取得。なくてもエラーにしない。
	 *
	 * @param key
	 *            プロパティキー
	 * @return プロパティ値
	 */
	public static String get(final String key) {
		if (key != null) {
			if (BUNDLE.containsKey(key)) {
				return BUNDLE.getString(key);
			}
			if (BUNDLE_BASE.containsKey(key)) {
				return BUNDLE_BASE.getString(key);
			}
		}
		LOG.trace("key is not exist. [" + key + "]");
		return null;
	}

	/**
	 * キーに合致する値を","でsplitして取得。なくてもエラーにしない。
	 *
	 * @param key
	 *            プロパティキー
	 * @return 配列化したプロパティ値
	 */
	public static String[] gets(final String key) {
		String value = App.get(key);
		if (value != null) {
			return StringUtil.split(value);
		}
		return null;
	}

	/**
	 * キーに合致する値を","でsplitして{@link AppSet}として取得。なくてもエラーにしない。
	 *
	 * @param key
	 *            プロパティキー
	 * @return {@link AppSet}化したプロパティ値
	 */
	public static AppSet<String> getSet(final String key) {

		AppSet<String> ret = new AppSet<String>();

		String[] values = App.gets(key);
		if (values != null) {
			for (String value : values) {
				ret.add(value);
			}
		}

		return ret;
	}

	/**
	 * キーに合致する値を","でsplitしてから、":"でもsplitしてキー＆値化し、LinkedHashMapとして取得。なくてもエラーにしない。
	 *
	 * @param key
	 *            プロパティキー
	 * @return LinkedHashMap化したプロパティ値
	 */
	public static Map<String, String> getMap(final String key) {

		Map<String, String> ret = new LinkedHashMap<String, String>();

		String[] values = App.gets(key);
		if (values != null) {
			for (String value : values) {
				String[] properties = value.split("\\s*:\\s*");
				if (properties.length == 1) {
					ret.put(properties[0], null);
				} else {
					ret.put(properties[0], properties[1]);
				}
			}
		}

		return ret;
	}

}
