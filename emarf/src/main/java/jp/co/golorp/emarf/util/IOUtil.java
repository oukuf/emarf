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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;

/**
 * IOユーティリティ
 *
 * @author oukuf@golorp
 */
public final class IOUtil {

	/** Logger */
	private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);

	/**
	 * コンストラクタ
	 */
	private IOUtil() {
		super();
	}

	/**
	 * IOを閉じる際、エラーがあっても例外としない
	 *
	 * @param autoCloseable
	 *            autoCloseable
	 */
	public static void closeQuietly(final AutoCloseable autoCloseable) {
		if (autoCloseable != null) {
			try {
				autoCloseable.close();
			} catch (Exception e) {
				LOG.info(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * ファイルに文字列を出力する
	 *
	 * @param filePath
	 *            filePath
	 * @param contents
	 *            contents
	 * @param append
	 *            append
	 */
	public static void writeAndCloseQuietly(final String filePath, final String contents, final boolean append) {
		FileWriter w = null;
		try {
			w = new FileWriter(new File(filePath), append);
			w.write(contents);
			w.flush();
		} catch (IOException e) {
			throw new SystemError(e);
		} finally {
			IOUtil.closeQuietly(w);
		}
	}

	/**
	 * 指定したファイル・ディレクトリ以下を全て削除
	 *
	 * @param file
	 *            file
	 */
	public static void delete(final File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory()) {
					delete(f);
				} else {
					f.delete();
				}
			}
		}
		file.delete();
	}

}
