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
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;

/**
 * ThreadLocalConnection
 *
 * @author oukuf@golorp
 */
public class ThreadLocalConnection extends ThreadLocal<Connection> {

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(ThreadLocalConnection.class);

	@Override
	protected Connection initialValue() {
		try {
			DataSource ds = DataSources.get();
			Connection cn = ds.getConnection();
			cn.setAutoCommit(false);
			LOG.debug("initialize ThreadLocalConnection.");
			return cn;
		} catch (SQLException e) {
			throw new SystemError(e);
		}
	}

	@Override
	public void remove() {
		LOG.debug("remove ThreadLocalConnection.");
		super.remove();
	}
}
