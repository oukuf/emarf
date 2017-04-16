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
package jp.co.golorp.emarf.model;

import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.util.LogUtil;
import jp.co.golorp.emarf.util.StringUtil;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * データベースIOのキャッシュ
 *
 * @author oukuf@golorp
 */
public final class ModelsCache {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(ModelsCache.class);

	/** ModelsCache.properties */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(ModelsCache.class.getSimpleName());

	/** キャッシュしないモデル名の配列 */
	private static final String[] DENY_MODELS = StringUtil.split(BUNDLE.getString("denyModels"));

	/**
	 * コンストラクタ
	 */
	private ModelsCache() {
	}

	/**
	 * sqlをキーにしてオブジェクトをキャッシュ
	 *
	 * @param query
	 *            キーとなるsql
	 * @param <T>
	 *            取得するデータ型
	 * @return キャッシュ済みのオブジェクト
	 */
	protected static <T> T get(final String query) {

		CacheManager cacheManager = CacheManager.getInstance();

		Cache cache = cacheManager.getCache(ModelsCache.class.getSimpleName());

		Element element = cache.get(query);
		if (element == null) {
			return null;
		}

		Object o = element.getObjectValue();

		@SuppressWarnings("unchecked")
		T t = (T) o;

		LOG.trace("<cache> " + query);
		if (LOG.isTraceEnabled()) {
			LogUtil.callerLog();
		}

		return t;
	}

	/**
	 * キャッシュ済みのオブジェクトを返却
	 *
	 * @param query
	 *            キーとなるsql
	 * @param o
	 *            キャッシュするオブジェクト
	 */
	protected static void set(final String query, final Object o) {

		// キャッシュ不可に設定したモデル以外は検索結果をキャッシュ
		String className = null;
		if (o instanceof List) {
			List<?> list = (List<?>) o;
			className = list.get(0).getClass().getSimpleName();
		} else if (o != null) {
			className = o.getClass().getSimpleName();
		}

		for (String denyModel : DENY_MODELS) {
			if (denyModel.equals(className)) {
				return;
			}
		}

		CacheManager cacheManager = CacheManager.getInstance();

		Cache cache = cacheManager.getCache(ModelsCache.class.getSimpleName());

		cache.put(new Element(query, o));
	}

}
