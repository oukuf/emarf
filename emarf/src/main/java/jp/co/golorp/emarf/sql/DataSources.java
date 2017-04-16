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

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;

/**
 * データソースを管理するクラス
 *
 * @author oukuf@golorp
 */
public final class DataSources {

	/*
	 * ************************************************************ クラス定数
	 */

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(DataSources.class);

	/** DataSource.properties */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(DataSources.class.getSimpleName());

	/** dataSourceName */
	private static final String DATA_SOURCE_NAME = "dataSourceName";

	// /***/
	// private static boolean isMySQL = false;

	// /***/
	// private static boolean isOracle = false;

	/*
	 * ************************************************************ クラス変数
	 */

	/** シングルトンデータソース */
	private static DataSource ds = null;

	/*
	 * ************************************************************ コンストラクタ
	 */

	/**
	 * デフォルトコンストラクタ
	 */
	private DataSources() {
	}

	/*
	 * ************************************************************ クラスメソッド
	 */

	/**
	 * データソース取得
	 *
	 * @return DataSource
	 */
	public static DataSource get() {

		if (ds != null) {
			return ds;
		}

		/*
		 * JNDIから取得
		 */

		String name = BUNDLE.getString(DATA_SOURCE_NAME);
		try {
			Context context = new InitialContext();
			ds = (DataSource) context.lookup(name);
			return ds;
		} catch (NamingException e) {
			LOG.warn(e.getMessage());
		}

		/*
		 * DBCPから取得
		 */

		Properties properties = new Properties();
		Enumeration<String> keys = BUNDLE.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String value = BUNDLE.getString(key);
			properties.put(key, value);
			// if (value.contains("mysql")) {
			// DataSources.isMySQL = true;
			// } else if (value.contains("oracle")) {
			// DataSources.isOracle = true;
			// }
		}

		try {
			ds = BasicDataSourceFactory.createDataSource(properties);
			return ds;
		} catch (Exception e) {
			throw new SystemError(e);
		}
	}

	// /**
	// * @return boolean
	// */
	// public static boolean isMySQL() {
	// return DataSources.isMySQL;
	// }

	// /**
	// * @return boolean
	// */
	// public static boolean isOracle() {
	// return DataSources.isOracle;
	// }

}
