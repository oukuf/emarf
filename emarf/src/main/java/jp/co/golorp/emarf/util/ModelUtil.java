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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jp.co.golorp.emarf.constants.model.ModelFieldTypes;
import jp.co.golorp.emarf.constants.model.RelationTypes;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.sql.relation.RelationMap;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;

/**
 * モデル用ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class ModelUtil {

	/**
	 * コンストラクタ
	 */
	private ModelUtil() {
	}

	/**
	 * @param modelName
	 *            modelName
	 * @return モデル名で指定したインスタンス
	 */
	public static Model getBlankModel(final String modelName) {
		String className = BeanGenerator.PACKAGE + Model.SEP + modelName;
		try {
			Class<?> c = Class.forName(className);
			return (Model) c.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			if (!modelName.startsWith(Criteria.UNIT_TEST_MODEL_NAME_PREFIX)) {
				throw new SystemError(e);
			}
			return null;
		}
	}

	/**
	 * @param propertyName
	 *            propertyName
	 * @return 選択肢用プロパティか
	 */
	public static boolean isOptionable(final String propertyName) {
		return Fieldset.CHECKS_SUFFIX_SET.isEnd(propertyName) || Fieldset.RADIOS_SUFFIX_SET.isEnd(propertyName)
				|| Fieldset.SELECT_SUFFIX_SET.isEnd(propertyName);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return モデル論理名
	 */
	public static String getModelMei(final String modelName) {
		return (String) getClassStaticField(modelName, ModelFieldTypes.MODEL_MEI);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 主キープロパティのリスト
	 */
	public static Set<String> getPrimaryPropertyNames(final String modelName) {
		Object o = getClassStaticField(modelName, ModelFieldTypes.PK_PROPERTY_NAMES);
		if (o != null) {
			@SuppressWarnings("unchecked")
			Set<String> primaryPropertyNames = (Set<String>) o;
			return primaryPropertyNames;
		} else {
			return Collections.emptySet();
		}
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 「プロパティ物理名：プロパティ論理名」のマップ
	 */
	public static Map<String, String> getPropertyMeis(final String modelName) {
		Object o = getClassStaticField(modelName, ModelFieldTypes.PROPERTY_MEIS);
		@SuppressWarnings("unchecked")
		Map<String, String> propertyMeis = (Map<String, String>) o;
		return propertyMeis;
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 兄弟モデル情報
	 */
	public static RelateTablesMap getBrothers(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.BROTHER);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 祖先モデル情報
	 */
	public static RelateTablesMap getAncestors(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.ANCESTOR);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 子孫モデル情報
	 */
	public static RelateTablesMap getDescendants(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.DESCENDANT);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 親モデル情報
	 */
	public static RelateTablesMap getParentTablesMap(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.PARENT);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 子モデル情報
	 */
	public static RelateTablesMap getChildren(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.CHILD);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 参照元モデル情報
	 */
	public static RelateTablesMap getReferTos(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.REFER_TO);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 参照先モデル情報
	 */
	public static RelateTablesMap getReferBys(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.REFER_BY);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 履歴元モデル情報
	 */
	public static RelateTablesMap getHistoryOfTablesMap(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.HISTORY_OF);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 履歴モデル情報
	 */
	public static RelateTablesMap getHistoryBys(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.HISTORY_BY);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 集約元モデル情報
	 */
	public static RelateTablesMap getSummaryOfTablesMap(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.SUMMARY_OF);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 集約モデル情報
	 */
	public static RelateTablesMap getSummaryBys(final String modelName) {
		return getRelateTablesMap(modelName, RelationTypes.SUMMARY_BY);
	}

	/**
	 * @return 最上位モデルのリスト
	 */
	public static List<String> getRoots() {

		List<String> roots = new ArrayList<String>();

		Map<String, String> modelMeis = MetaData.getModelMeis();
		for (String modelName : modelMeis.keySet()) {

			RelationMap relationMap = getRelationMap(modelName);

			if (relationMap == null
					|| (relationMap.get(RelationTypes.ANCESTOR) == null && relationMap.get(RelationTypes.PARENT) == null
							&& relationMap.get(RelationTypes.HISTORY_OF) == null
							&& relationMap.get(RelationTypes.SUMMARY_OF) == null)) {
				// && relations.get(Model.RELATION.REFER_TO) == null
				roots.add(modelName);
			}
		}

		return roots;
	}

	/**
	 * @param modelName
	 *            比較元モデル名
	 * @param modelName2
	 *            比較先モデル名
	 * @return ２つのモデル間の関連プロパティ情報
	 */
	public static List<RelateColumnMap> getRelateProperties(final String modelName, final String modelName2) {

		// modelNameの全ての関連情報を取得
		RelationMap relationMap = ModelUtil.getRelationMap(modelName);
		if (relationMap == null) {
			return null;
		}

		// 全ての関連情報のうちmodelName2に合致する関連情報のみ返却
		for (Entry<RelationTypes, RelateTablesMap> relation : relationMap.entrySet()) {
			RelateTablesMap releateTablesMap = relation.getValue();
			for (Entry<String, List<RelateColumnMap>> releateTables : releateTablesMap.entrySet()) {
				if (modelName2.equals(releateTables.getKey())) {
					return releateTables.getValue();
				}
			}
		}

		return null;
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @param field
	 *            フィールド列挙子
	 * @return フィールド列挙子に該当するスタティックフィールド値
	 */
	private static Object getClassStaticField(final String modelName, final ModelFieldTypes field) {

		String className = BeanGenerator.PACKAGE + Model.SEP + modelName;

		// クラスを取得
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}

		// フィールドを取得
		Field f = null;
		try {
			f = clazz.getField(field.toString());
		} catch (SecurityException | NoSuchFieldException e) {
			throw new SystemError(e);
		}

		// フィールド値を取得
		Object o = null;
		try {
			o = f.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SystemError(e);
		}

		return o;
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 指定したモデルの関連情報
	 */
	private static RelationMap getRelationMap(final String modelName) {
		Object o = getClassStaticField(modelName, ModelFieldTypes.RELATION_MAP);
		return (RelationMap) o;
	}

	/**
	 * @param modelName
	 *            modelName
	 * @param relationType
	 *            relation
	 * @return 指定したモデルの、指定した関連情報
	 */
	private static RelateTablesMap getRelateTablesMap(final String modelName, final RelationTypes relationType) {

		RelateTablesMap ret = null;

		RelationMap relationMap = getRelationMap(modelName);
		if (relationMap == null) {
			return ret;
		}

		RelateTablesMap relateTablesMap = relationMap.get(relationType);
		if (relateTablesMap == null) {
			return ret;
		}

		for (Entry<String, List<RelateColumnMap>> relateTables : relateTablesMap.entrySet()) {
			String modelName2 = relateTables.getKey();
			List<RelateColumnMap> relateColumnMaps = relateTables.getValue();

			if (ret == null) {
				ret = new RelateTablesMap();
			}

			ret.put(modelName2, relateColumnMaps);
		}

		return ret;
	}

}
