package jp.co.golorp.emarfBlank.servlet.http;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

/**
 * MySQL用 コネクション終了リスナ<br>
 * webアプリケーション終了時にMySQLのコネクションが残ってしまう場合に使用する。
 *
 * @author oukuf@golorp
 */
public final class MySqlCleanupListener implements ServletContextListener {

	/***/
	private static final Logger LOG = LoggerFactory.getLogger(MySqlCleanupListener.class);

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		try {
			AbandonedConnectionCleanupThread.shutdown();
			LOG.debug("AbandonedConnectionCleanupThread shutdown.");
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

}
