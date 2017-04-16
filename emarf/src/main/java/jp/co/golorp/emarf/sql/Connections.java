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

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * スレッドごとにDB接続を管理するクラス
 *
 * @author oukuf@golorp
 */
public final class Connections {

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(Connections.class);

	/** ThreadLocalConnection */
	private static ThreadLocalConnection threadLocalConnection = new ThreadLocalConnection();

	/**
	 * コンストラクタ
	 */
	private Connections() {
	}

	/**
	 * @return コネクションプールから取得したコネクション
	 */
	public static Connection get() {
		Connection cn = threadLocalConnection.get();
		LOG.trace("get connection. [" + cn + "]");
		return cn;
	}

	/**
	 * コミット
	 */
	public static void commit() {
		Connection cn = threadLocalConnection.get();
		LOG.debug("commit connection.");
		DbUtils.commitAndCloseQuietly(cn);
	}

	/**
	 * ロールバック
	 */
	public static void rollback() {
		Connection cn = threadLocalConnection.get();
		LOG.debug("rollback connection.");
		DbUtils.rollbackAndCloseQuietly(cn);
	}

	/**
	 * クローズ
	 */
	public static void close() {
		Connection cn = threadLocalConnection.get();
		LOG.trace("close connection.");
		DbUtils.closeQuietly(cn);
		threadLocalConnection.remove();
	}

}
