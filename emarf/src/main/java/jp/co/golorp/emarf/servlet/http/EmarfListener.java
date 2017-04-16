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
package jp.co.golorp.emarf.servlet.http;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.util.LogUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 各コンテキストのロギング用リスナ
 *
 * @author oukuf@golorp
 */
public final class EmarfListener implements ServletContextListener, ServletContextAttributeListener,
		HttpSessionListener, HttpSessionAttributeListener, HttpSessionActivationListener, HttpSessionBindingListener,
		ServletRequestListener, ServletRequestAttributeListener {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(EmarfListener.class);

	/**
	 * コンストラクタ
	 */
	public EmarfListener() {
		LOG.trace("EmarfListener created.");
	}

	/*
	 * コンテキスト情報
	 */

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		LOG.trace("context initialized.");
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		LOG.trace("context destroyed.");

		// The web application registered the JDBC driver but failed to
		// unregister it when the web application was stopped.
		// To prevent a memory leak, the JDBC Driver has been forcibly
		// unregistered.
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {
				LOG.warn(e.getMessage());
			}
		}
	}

	@Override
	public void attributeAdded(final ServletContextAttributeEvent event) {
		LOG.trace("context attribute added. { " + event.getName() + " = " + event.getValue() + " }");
	}

	@Override
	public void attributeReplaced(final ServletContextAttributeEvent event) {
		LOG.trace("context attribute replaced. { " + event.getName() + " = " + event.getValue() + " }");
	}

	@Override
	public void attributeRemoved(final ServletContextAttributeEvent event) {
		LOG.trace("context attribute removed. { " + event.getName() + " = " + event.getValue() + " }");
	}

	/*
	 * セッション
	 */

	@Override
	public void sessionCreated(final HttpSessionEvent se) {
		LOG.trace("session created.");
	}

	@Override
	public void sessionDidActivate(final HttpSessionEvent se) {
		LOG.trace("session did activate.");
	}

	@Override
	public void sessionWillPassivate(final HttpSessionEvent se) {
		LOG.trace("session will passivate.");
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent se) {
		LOG.trace("session destroyed.");
	}

	@Override
	public void attributeAdded(final HttpSessionBindingEvent event) {
		String eventValue = getEventValue(event.getValue());
		LOG.trace("session attribute added. { " + event.getName() + " =" + eventValue + "}");
	}

	@Override
	public void attributeReplaced(final HttpSessionBindingEvent event) {
		String eventValue = getEventValue(event.getValue());
		LOG.trace("session attribute replaced. { " + event.getName() + " =" + eventValue + "}");
	}

	@Override
	public void attributeRemoved(final HttpSessionBindingEvent event) {
		String eventValue = getEventValue(event.getValue());
		LOG.trace("session attribute removed. { " + event.getName() + " =" + eventValue + "}");
	}

	@Override
	public void valueBound(final HttpSessionBindingEvent event) {
		String eventValue = getEventValue(event.getValue());
		LOG.trace("session value bound. { " + event.getName() + " =" + eventValue + "}");
	}

	@Override
	public void valueUnbound(final HttpSessionBindingEvent event) {
		String eventValue = getEventValue(event.getValue());
		LOG.trace("session value unbound. { " + event.getName() + " =" + eventValue + "}");
	}

	/*
	 * リクエスト
	 */

	@Override
	public void requestInitialized(final ServletRequestEvent sre) {
		if (!isStaticAccess(sre.getServletRequest())) {
			LOG.debug("request initialized.");
		}
	}

	@Override
	public void requestDestroyed(final ServletRequestEvent sre) {
		if (!isStaticAccess(sre.getServletRequest())) {
			LOG.debug("request destroyed.");
		}
	}

	@Override
	public void attributeAdded(final ServletRequestAttributeEvent srae) {
		if (!isStaticAccess(srae.getServletRequest())) {
			String eventValue = getEventValue(srae.getValue());
			LOG.trace("request attribute added. { " + srae.getName() + " = " + eventValue + "}");
			if (LOG.isTraceEnabled()) {
				LogUtil.callerLog();
			}
		}
	}

	@Override
	public void attributeReplaced(final ServletRequestAttributeEvent srae) {
		if (!isStaticAccess(srae.getServletRequest())) {
			String eventValue = getEventValue(srae.getValue());
			LOG.trace("request attribute replaced. { " + srae.getName() + " = " + eventValue + "}");
			if (LOG.isTraceEnabled()) {
				LogUtil.callerLog();
			}
		}
	}

	@Override
	public void attributeRemoved(final ServletRequestAttributeEvent srae) {
		if (!isStaticAccess(srae.getServletRequest())) {
			String eventValue = getEventValue(srae.getValue());
			LOG.trace("request attribute removed. { " + srae.getName() + " = " + eventValue + "}");
			if (LOG.isTraceEnabled()) {
				LogUtil.callerLog();
			}
		}
	}

	/**
	 * @param request
	 *            request
	 * @return 静的アクセスかどうか
	 */
	private boolean isStaticAccess(final ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest) request;
		String requestUri = req.getRequestURI();
		return requestUri.matches(EmarfServlet.staticResourceRegexp);
	}

	/**
	 * @param eventValue
	 *            eventValue
	 * @return ログ文字列
	 */
	private String getEventValue(final Object eventValue) {
		if (eventValue instanceof Map) {
			StringBuilder sb = new StringBuilder();
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) eventValue;
			for (Entry<Object, Object> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				value = StringUtil.getValue(value);
				sb.append(key).append(" = \"").append(value).append("\",\n");
			}
			if (sb.length() > 2) {
				return " {\n" + sb.substring(0, sb.length() - 2) + "\n}";
			} else {
				return " {\n" + sb + "\n}";
			}
		} else {
			return " " + eventValue + " ";
		}
	}

}
