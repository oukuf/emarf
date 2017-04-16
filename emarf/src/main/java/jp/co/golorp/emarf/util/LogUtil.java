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
package jp.co.golorp.emarf.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ロガー用ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class LogUtil {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(LogUtil.class);

	/**
	 *
	 */
	private LogUtil() {
	}

	/**
	 *
	 */
	public static void callerLog() {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String className = stackTraceElement.getClassName();
			String methodName = stackTraceElement.getMethodName();
			int i = stackTraceElement.getLineNumber();
			if (className.startsWith("jp.co.golorp.emarf.tag.lib")) {
				LOG.debug("        @" + className + "." + methodName + "() [L:" + i + "]");
				break;
			}
		}
	}

}
