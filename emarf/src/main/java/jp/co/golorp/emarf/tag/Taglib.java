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
package jp.co.golorp.emarf.tag;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * タグ処理用共通関数
 *
 * @author oukuf@golorp
 */
public final class Taglib {

	/** URLセパレータ */
	private static final String SEP = EmarfServlet.SEP;

	/** フッタに表示する文字列 */
	public static final String FOOTER = App.get(AppKey.TAGUTIL_FOOTER);

	/** ページ下部にダンプ情報を表示するかのフラグ */
	public static final String DUMP = App.get(AppKey.TAGUTIL_DUMP);

	/**
	 * デフォルトコンストラクタ
	 */
	private Taglib() {
	}

	/**
	 * @param pc
	 *            PageContext
	 * @param path
	 *            コンテキストルートからの相対パス
	 * @param ext
	 *            拡張子
	 * @return ファイルパスのリスト
	 */
	public static List<String> listFiles(final PageContext pc, final String path, final String ext) {

		String realPath = pc.getServletContext().getRealPath(path);
		if (realPath == null) {
			return null;
		}

		File[] files = new File(realPath).listFiles();
		if (files == null) {
			return null;
		}

		List<String> ret = new ArrayList<String>();

		for (File file : files) {
			if (file.isDirectory()) {
				ret.addAll(listFiles(pc, path + SEP + file.getName(), ext));
			}
		}

		for (File file : files) {
			if (file.isFile() && file.getName().endsWith("." + ext)) {
				ret.add(path + SEP + file.getName());
			}
		}

		return ret;
	}

	/**
	 * 親要素から属性値を取得
	 *
	 * @param tag
	 *            tag
	 * @param key
	 *            属性キー
	 * @return 属性値
	 */
	public static String getParentAttribute(final TagSupport tag, final String key) {

		String ret = null;

		TagSupport parent = (TagSupport) tag.getParent();

		if (parent != null) {

			try {
				ret = getAttribute(parent, parent.getClass(), key);
			} catch (Exception e) {
				ret = getParentAttribute(parent, key);
			}
		}

		return ret;
	}

	/**
	 * @param tag
	 *            Modelable
	 * @param propertyName
	 *            プロパティ名
	 * @return html項目名
	 */
	public static String getHtmlName(final Modelable tag, final String propertyName) {
		return getHtmlName((TagSupport) tag, tag.getModelName(), propertyName);
	}

	/**
	 * @param tag
	 *            Propertiable
	 * @return html項目名
	 */
	public static String getHtmlName(final Propertiable tag) {
		String modelName = tag.getModelName();
		String propertyName = tag.getPropertyName();
		return getHtmlName((TagSupport) tag, modelName, propertyName);
	}

	/**
	 * @param request
	 *            request
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @return boolean
	 */
	public static boolean isReadonly(final ServletRequest request, final String modelName, final String propertyName) {

		// ページ名を取得
		String pageName = RequestUtil.getPathPageName(request);

		// 登録画面の場合
		if (pageName.equals(EmarfServlet.PAGE_NEW)) {

			// 自動登録項目 か 登録対象外項目 か 非活性項目 なら編集不可
			if (Models.AINT_INSERT_SET.isEnd(propertyName)) {
				return true;
			} else if (Models.AUTO_INSERT_MAP.containsKey(propertyName)) {
				return true;
			} else if (Fieldset.CANT_INSERT_SUFFIX_SET.isEnd(propertyName)) {
				return true;
			}
		}

		// 更新画面の場合
		if (pageName.equals(EmarfServlet.PAGE_EDIT)) {

			// 自動更新項目 か 更新対象外項目 か 非活性項目 なら編集不可
			if (Models.AINT_UPDATE_SET.isEnd(propertyName)) {
				return true;
			} else if (Models.AUTO_UPDATE_MAP.containsKey(propertyName)) {
				return true;
			} else if (Fieldset.CANT_UPDATE_SUFFIX_SET.isEnd(propertyName)) {
				return true;
			}
		}

		// 主キーでない なら編集可
		Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(modelName);
		if (!primaryPropertyNames.contains(propertyName)) {
			return false;
		}

		/*
		 * 以降は主キーということ
		 */

		// 編集画面 なら編集不可
		if (StringUtil.equalsIgnoreCase(pageName, EmarfServlet.PAGE_EDIT)) {
			return true;
		}

		/*
		 * 以降は登録画面の主キーということ
		 */

		// ユニークキーの「～Id」なら編集不可
		if (propertyName.endsWith(Models.ID_SUFFIX) && primaryPropertyNames.size() == 1) {
			return true;
		}

		String[] primaryKeyNameArray = primaryPropertyNames.toArray(new String[primaryPropertyNames.size()]);

		// 複合キー末端の連番「～Seq」なら編集不可
		if (propertyName.endsWith(Models.SEQ_SUFFIX)
				&& primaryKeyNameArray[primaryPropertyNames.size() - 1].equals(propertyName)) {
			return true;
		}

		// （登録画面で）セッションフォームに値がある なら編集不可
		if (StringUtil.equals(pageName, EmarfServlet.PAGE_NEW)) {

			Map<String, SessionForm> sessionForms = SessionFormUtil.getSessionForms(request);
			if (sessionForms == null) {
				return false;
			}

			String uri = RequestUtil.getRequestURI(request);
			String requestURI = uri.replaceAll("post$", "");

			SessionForm sessionForm = sessionForms.get(requestURI);
			if (sessionForm == null) {
				return false;
			}

			SessionModel sessionModel = sessionForm.getModel(modelName);
			if (sessionModel == null) {
				return false;
			}

			String[] values = sessionModel.gets(propertyName);
			if (StringUtil.isNotBlank(values)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param request
	 *            request
	 * @return テーブル名 + ページ名
	 */
	public static String getGamenMei(final ServletRequest request) {

		String modelName = RequestUtil.getPathModelName(request);
		String pageName = RequestUtil.getPathPageName(request);

		String gamenMei = App.get(AppKey.TAGUTIL_GAMEN_PREFIX + modelName + "." + pageName);

		if (gamenMei != null) {
			return gamenMei;
		}

		if (modelName != null) {
			TableInfo tableInfo = MetaData.getTableInfo(modelName);
			if (tableInfo != null) {
				String tableMei = tableInfo.getTableMei();
				if (StringUtil.isNotEmpty(tableMei)) {
					gamenMei = tableMei;
					String pageMei = App.get(AppKey.TAGUTIL_PAGE_PREFIX + pageName);
					if (StringUtil.isNotEmpty(pageMei)) {
						gamenMei += pageMei;
					}
				}
			}
		}

		if (gamenMei == null) {
			gamenMei = "";
		}

		return gamenMei;
	}

	/**
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @return マスタモデル名
	 */
	public static String[] getMasterInfo(final String modelName, final String propertyName) {

		TableInfo tableInfo = MetaData.getTableInfo(modelName);
		if (tableInfo == null) {
			return null;
		}

		ColumnInfo columnInfo = tableInfo.getColumnInfo(propertyName);
		if (columnInfo == null) {
			return null;
		}

		String columnName = columnInfo.getColumnName();

		List<TableInfo> tableInfos2 = MetaData.getTableInfos();
		for (TableInfo tableInfo2 : tableInfos2) {

			String tableName2 = tableInfo2.getTableName();
			String modelName2 = StringUtil.toUpperCamelCase(tableName2);
			if (modelName.equals(modelName2)) {
				continue;
			}

			Set<String> primaryKeys2 = tableInfo2.getPrimaryKeys();
			if (primaryKeys2 == null || primaryKeys2.size() > 1) {
				continue;
			}

			for (String primaryKey2 : primaryKeys2) {
				if (columnName.endsWith(primaryKey2)) {
					String propertyName2 = StringUtil.toCamelCase(primaryKey2);
					return new String[] { modelName2, propertyName2 };
				}
			}
		}

		return null;
	}

	/**
	 * @param tag
	 *            tag
	 * @param asClazz
	 *            asClazz
	 * @param key
	 *            key
	 * @return String
	 */
	private static String getAttribute(final TagSupport tag, final Class<?> asClazz, final String key) {

		try {

			Field field = asClazz.getDeclaredField(key);
			field.setAccessible(true);
			Object o = field.get(tag);
			if (o == null) {
				return null;
			}
			return String.valueOf(o);

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {

			if (asClazz == Object.class) {
				throw new SystemError(e);
			}

			return getAttribute(tag, asClazz.getSuperclass(), key);
		}
	}

	/**
	 * @param tag
	 *            TagSupport
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return html項目名。親要素が「index」属性を持っていれば、その添え字も付加する。
	 */
	private static String getHtmlName(final TagSupport tag, final String modelName, final String propertyName) {

		if (StringUtil.isBlank(modelName)) {
			return null;
		}

		// モデル名を付加
		StringBuilder sb = new StringBuilder(modelName);

		// 親要素から添え字を取得
		String index = getParentAttribute(tag, "index");
		if (index != null) {
			sb.append("[").append(index).append("]");
		}

		// プロパティ名があれば付加
		if (StringUtil.isNotBlank(propertyName)) {
			sb.append(".").append(propertyName);
		}

		return sb.toString();
	}

}
