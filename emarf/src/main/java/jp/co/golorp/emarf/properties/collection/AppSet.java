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
package jp.co.golorp.emarf.properties.collection;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.StringUtils;

import jp.co.golorp.emarf.util.StringUtil;

/**
 * プロパティファイル用Set
 *
 * @param <T>
 *            取得するデータ型
 * @author oukuf@golorp
 */
public final class AppSet<T> extends LinkedHashSet<Object> {

	/**
	 * @param name
	 *            項目名称
	 * @return 項目名称がsuffixのset内で合致するか
	 */
	public boolean isEnd(final String name) {

		if (StringUtil.isNotBlank(name)) {
			for (Iterator<?> suffixes = this.iterator(); suffixes.hasNext();) {
				String suffix = (String) suffixes.next();
				if (StringUtils.isNotBlank(suffix) && name.replaceFirst("\\[[0-9]+\\]", "").endsWith(suffix)) {
					return true;
				}
			}
		}

		return false;
	}

}
