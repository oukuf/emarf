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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.golorp.emarf.constants.MessageKeys;
import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.servlet.http.form.SessionProperty;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.tag.lib.criteria.model.property.Checks;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 検索条件を構築するクラス。<br>
 *
 * <pre>
Criteria = {
  or : [
    Criteria = {
      and : [
        Criteria = { modelName = MODEL, propertyName = PROPERTY_1, dir = Dir.eq, values = 1 },
        Criteria = { modelName = MODEL, propertyName = PROPERTY_2, dir = Dir.eq, values = 2 }
      ],
      or : [
        Criteria = {
          and : [
            Criteria = { modelName = MODEL, propertyName = PROPERTY_3, dir = Dir.eq, values = 3 }
          ]
        },
        Criteria = {
          and : [
            Criteria = { modelName = MODEL, propertyName = PROPERTY_4, dir = Dir.eq, values = 4 }
          ]
        }
      ]
    },
    Criteria = {
      and : [
        Criteria = { modelName = MODEL, propertyName = PROPERTY_1, dir = Dir.eq, values = 1 },
        Criteria = { modelName = MODEL, propertyName = PROPERTY_2, dir = Dir.eq, values = 2 }
      ],
      or : [
        Criteria = {
          and : [
            Criteria = { modelName = MODEL, propertyName = PROPERTY_3, dir = Dir.eq, values = 3 }
          ]
        },
        Criteria = {
          and : [
            Criteria = { modelName = MODEL, propertyName = PROPERTY_4, dir = Dir.eq, values = 4 }
          ]
        }
      ]
    }
  ],
  order : [
    Criteria = { modelName = MODEL, propertyName = PROPERTY_5, dir = Dir.asc, values = null }
  ]
}

↓↓↓

WHERE
  MODEL.PROPERTY_1 = 1 AND
  MODEL.PROPERTY_2 = 2 AND
  (
    (MODEL.PROPERTY_3 = 3)
    OR
    (MODEL.PROPERTY_4 = 4)
  )
ORDER BY
  MODEL.PROPERTY_5
 * </pre>
 *
 * @author oukuf@golorp
 */
public final class Criteria implements Cloneable {

	/** or条件リスト */
	private List<Criteria> or;

	/** and条件リスト */
	private List<Criteria> and;

	/** ソート順リスト */
	private List<Criteria> order;

	/** モデル名 */
	private String modelName;

	/** プロパティ名 */
	private String propertyName;

	/** 演算子 */
	private Dir dir;

	/** 条件値 */
	private Object values;

	/**
	 * デフォルトコンストラクタ
	 */
	private Criteria() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param dir
	 *            演算子
	 * @param values
	 *            条件値
	 */
	private Criteria(final String modelName, final String propertyName, final Dir dir, final Object values) {
		super();
		this.modelName = modelName;
		this.propertyName = propertyName;
		this.dir = dir;
		this.values = values;
	}

	/**
	 * or条件を追加して、自オブジェクトを返す
	 *
	 * @return Criteria
	 */
	public Criteria or() {
		or(new Criteria());
		return this;
	}

	/**
	 * or条件を追加して、自オブジェクトを返す
	 *
	 * @param c
	 *            Criteria
	 * @return Criteria
	 */
	public Criteria or(final Criteria c) {
		if (this.or == null) {
			this.or = new ArrayList<Criteria>();
		}
		this.or.add(c);
		return this;
	}

	/**
	 * @return 条件を持つか
	 */
	public boolean is() {
		return this.or != null && this.or.size() > 0;
	}

	/**
	 * and条件を追加して、自オブジェクトを返す
	 *
	 * @param c
	 *            Criteria
	 * @return Criteria
	 */
	public Criteria and(final Criteria c) {
		if (this.current().and == null) {
			this.current().and = new ArrayList<Criteria>();
		}
		this.current().and.add(c);
		return this;
	}

	/**
	 * @return 自インスタンスのor条件末尾にあるCriteria
	 */
	private Criteria current() {
		if (this.or == null || this.or.size() == 0) {
			this.or();
		}
		return this.or.get(this.or.size() - 1);
	}

	/**
	 * 同値条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria equal(final String modelName, final String propertyName, final Object value) {
		return new Criteria().eq(modelName, propertyName, value);
	}

	/**
	 * 非同値条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria notEqual(final String modelName, final String propertyName, final Object value) {
		return new Criteria().ne(modelName, propertyName, value);
	}

	/**
	 * 未満条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria lessThan(final String modelName, final String propertyName, final Object value) {
		return new Criteria().lt(modelName, propertyName, value);
	}

	/**
	 * 超過条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria greaterThan(final String modelName, final String propertyName, final Object value) {
		return new Criteria().gt(modelName, propertyName, value);
	}

	/**
	 * 以下条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria lessEqual(final String modelName, final String propertyName, final String value) {
		return new Criteria().le(modelName, propertyName, value);
	}

	/**
	 * 以上条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria greaterEqual(final String modelName, final String propertyName, final String value) {
		return new Criteria().ge(modelName, propertyName, value);
	}

	/**
	 * 部分一致条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria like(final String modelName, final String propertyName, final String value) {
		return new Criteria().lk(modelName, propertyName, value);
	}

	/**
	 * 範囲条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param lowValue
	 *            下限値
	 * @param highValue
	 *            上限値
	 * @return 新規インスタンス
	 */
	public static Criteria between(final String modelName, final String propertyName, final String lowValue,
			final String highValue) {
		return new Criteria().bw(modelName, propertyName, lowValue, highValue);
	}

	/**
	 * 候補条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param values
	 *            条件値
	 * @return 新規インスタンス
	 */
	public static Criteria inAny(final String modelName, final String propertyName, final Object values) {
		return new Criteria().in(modelName, propertyName, values);
	}

	/**
	 * 昇順指定
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return 新規インスタンス
	 */
	public static Criteria ascending(final String modelName, final String propertyName) {
		return new Criteria().asc(modelName, propertyName);
	}

	/**
	 * 降順指定
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return 新規インスタンス
	 */
	public static Criteria descending(final String modelName, final String propertyName) {
		return new Criteria().desc(modelName, propertyName);
	}

	@Override
	public Criteria clone() {

		Criteria clone = new Criteria();

		if (this.or != null) {
			clone.or = new ArrayList<Criteria>();
			for (Criteria or : this.or) {
				clone.or.add(or.clone());
			}
		}

		if (this.and != null) {
			clone.and = new ArrayList<Criteria>();
			for (Criteria and : this.and) {
				clone.and.add(and.clone());
			}
		}

		if (this.modelName != null) {
			clone.modelName = new String(this.modelName);
		}

		if (this.propertyName != null) {
			clone.propertyName = new String(this.propertyName);
		}

		if (this.dir != null) {
			clone.dir = this.dir;
		}

		if (this.values != null) {
			if (this.values instanceof String[]) {
				clone.values = ((String[]) this.values).clone();
			} else {
				clone.values = this.values;
			}
		}

		return clone;
	}

	/**
	 * @return Criteriaに含まれる全てのモデル名のSet
	 */
	public Set<String> getModels() {

		Set<String> modelNames = new LinkedHashSet<String>();

		if (this.modelName != null) {
			modelNames.add(this.modelName);
			return modelNames;
		}

		if (this.and != null) {
			for (Criteria and : this.and) {
				modelNames.addAll(and.getModels());
			}
		}

		if (this.or != null) {
			for (Criteria or : this.or) {
				modelNames.addAll(or.getModels());
			}
		}

		return modelNames;
	}

	/**
	 * 「@{パラメータ指定文字列}」の解決
	 *
	 * @param tag
	 *            CriteriaTagSupport
	 */
	public void solve(final CriteriaTagSupport tag) {

		if (this.modelName != null && this.propertyName != null && this.dir != null && this.values != null) {

			this.modelName = solve(this.modelName, tag);

			this.propertyName = solve(this.propertyName, tag);

			if (this.values instanceof String[]) {
				List<String> values = new ArrayList<String>();
				for (String value : (String[]) this.values) {
					values.add(solve(value, tag));
				}
				this.values = values.toArray();
			} else {
				this.values = solve(this.values.toString(), tag);
			}
		}

		if (this.and != null) {
			for (Criteria and : this.and) {
				and.solve(tag);
			}
		}

		if (this.or != null) {
			for (Criteria or : this.or) {
				or.solve(tag);
			}
		}
	}

	/**
	 * @param param
	 *            パラメータ指定文字列
	 * @param tag
	 *            CriteriaTagSupport
	 * @return "@{"と"}"で囲んだパラメータ指定の値を、タグの属性から取得した値
	 */
	private String solve(final String param, final CriteriaTagSupport tag) {

		// "@{"と"}"に囲まれていなければそのまま返す
		if (!param.startsWith("@{") || !param.endsWith("}")) {
			return param;
		}

		// パラメータ指定文字列から、先頭の「@{」と末尾の「}」を削除してプロパティ名を取得
		String name = param.replaceAll("^\\@\\{|\\}$", "");

		// プロパティ名に合致するプロパティを取得
		Field field = null;
		try {
			field = tag.getClass().getDeclaredField(name);
		} catch (Exception e) {
			throw new SystemError(e);
		}

		// 直接アクセス
		field.setAccessible(true);

		// プロパティ値を取得
		Object o = null;
		try {
			o = field.get(tag);
		} catch (Exception e) {
			throw new SystemError(e);
		}

		return o.toString();
	}

	/**
	 * 同値条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria eq(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.eq, value));
	}

	/**
	 * 同値条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria eq(final String propertyName, final Object value) {
		return this.eq(supplyModel(), propertyName, value);
	}

	/**
	 * 非同値条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria ne(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.ne, value));
	}

	/**
	 * 非同値条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria ne(final String propertyName, final Object value) {
		return this.ne(supplyModel(), propertyName, value);
	}

	/**
	 * 未満条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria lt(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.lt, value));
	}

	/**
	 * 未満条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria lt(final String propertyName, final Object value) {
		return this.lt(supplyModel(), propertyName, value);
	}

	/**
	 * 超過条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria gt(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.gt, value));
	}

	/**
	 * 超過条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria gt(final String propertyName, final Object value) {
		return this.gt(supplyModel(), propertyName, value);
	}

	/**
	 * 以下条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria le(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.le, value));
	}

	/**
	 * 以下条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria le(final String propertyName, final Object value) {
		return this.le(supplyModel(), propertyName, value);
	}

	/**
	 * 以上条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria ge(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.ge, value));
	}

	/**
	 * 以上条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria ge(final String propertyName, final Object value) {
		return this.ge(supplyModel(), propertyName, value);
	}

	/**
	 * 部分一致条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria lk(final String modelName, final String propertyName, final Object value) {
		return this.and(new Criteria(modelName, propertyName, Dir.lk, value));
	}

	/**
	 * 部分一致条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria lk(final String propertyName, final Object value) {
		return this.lk(supplyModel(), propertyName, value);
	}

	/**
	 * 範囲条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param low
	 *            下限値
	 * @param high
	 *            上限値
	 * @return 自オブジェクト
	 */
	public Criteria bw(final String modelName, final String propertyName, final Object low, final Object high) {
		return this.and(new Criteria(modelName, propertyName, Dir.bw, new Object[] { low, high }));
	}

	/**
	 * 範囲条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param low
	 *            下限値
	 * @param high
	 *            上限値
	 * @return 自オブジェクト
	 */
	public Criteria bw(final String propertyName, final Object low, final Object high) {
		return this.bw(supplyModel(), propertyName, low, high);
	}

	/**
	 * 候補条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @param values
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria in(final String modelName, final String propertyName, final Object values) {
		return this.and(new Criteria(modelName, propertyName, Dir.in, values));
	}

	/**
	 * 候補条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param values
	 *            条件値
	 * @return 自オブジェクト
	 */
	public Criteria in(final String propertyName, final Object values) {
		return this.in(supplyModel(), propertyName, values);
	}

	/**
	 * 昇順ソート条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return 自オブジェクト
	 */
	public Criteria asc(final String modelName, final String propertyName) {
		if (this.order == null) {
			this.order = new ArrayList<Criteria>();
		}
		this.order.add(new Criteria(modelName, propertyName, Dir.asc, null));
		return this;
	}

	/**
	 * 昇順ソート条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @return 自オブジェクト
	 */
	public Criteria asc(final String propertyName) {
		return this.asc(supplyModel(), propertyName);
	}

	/**
	 * 降順ソート条件追加
	 *
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return 自オブジェクト
	 */
	public Criteria desc(final String modelName, final String propertyName) {
		if (this.order == null) {
			this.order = new ArrayList<Criteria>();
		}
		this.order.add(new Criteria(modelName, propertyName, Dir.desc, null));
		return this;
	}

	/**
	 * 降順ソート条件追加
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @return 自オブジェクト
	 */
	public Criteria desc(final String propertyName) {
		return this.desc(supplyModel(), propertyName);
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		/*
		 * OR
		 */

		if (this.or != null) {
			for (Criteria c : this.or) {
				if (sb.length() > 0) {
					sb.append(" OR ");
				}
				if (this.or.size() > 1) {
					sb.append("(");
				}
				sb.append(c);
				if (this.or.size() > 1) {
					sb.append(")");
				}
			}
		}

		/*
		 * AND
		 */

		if (this.and != null) {
			for (Criteria c : this.and) {
				if (sb.length() > 0) {
					sb.append(" AND ");
				}
				if (this.and.size() > 1) {
					sb.append("(");
				}
				sb.append(c);
				if (this.and.size() > 1) {
					sb.append(")");
				}
			}
		}

		/*
		 * ORDER
		 */

		if (this.order != null && this.order.size() > 0) {
			StringBuilder order = new StringBuilder();
			for (Criteria c : this.order) {
				if (order.length() > 0) {
					order.append(", ");
				}
				order.append(c);
			}
			sb.append(" ORDER BY ").append(order.toString());
		}

		if (sb.length() > 0) {
			return sb.toString();
		}

		/*
		 * Criteria
		 */

		sb.append(getColumnName()).append(" ");

		if (this.values == null) {

			if (this.dir == Dir.asc) {
				sb.append(Dir.asc);
			} else if (this.dir == Dir.desc) {
				sb.append(Dir.desc);
			} else {
				sb.append("is null");
			}

		} else if (this.dir == Dir.bw) {

			sb.append(this.dir).append(" ? AND ?");

		} else if (this.dir == Dir.in) {

			String[] values = StringUtil.toStringArray(this.values);

			if (values[0].startsWith(Checks.NOCHECK_VALUE)) {
				sb.append("NOT ");
			}

			sb.append(this.dir).append(" (");

			for (int i = 0; i < values.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append("?");
			}

			sb.append(")");

		} else if (this.values != null) {

			if (this.values.toString().startsWith(Checks.NOCHECK_VALUE)) {
				sb.append("IS NULL");
			} else {
				sb.append(this.dir).append(" ?");
			}
		}

		return sb.toString();
	}

	/**
	 * @return sqlパラメータのリスト
	 */
	public List<Object> toParameter() {

		List<Object> params = new ArrayList<Object>();

		if (this.or != null) {
			for (Criteria c : this.or) {
				params.addAll(c.toParameter());
			}
		}

		if (params.size() > 0) {
			return params;
		}

		if (this.and != null) {
			for (Criteria c : this.and) {
				params.addAll(c.toParameter());
			}
		}

		if (params.size() > 0) {
			return params;
		}

		if (this.values != null) {

			if (this.dir == Dir.bw) {

				String[] values = StringUtil.toStringArray(this.values);
				params.add(values[0]);
				params.add(values[1]);

			} else if (this.dir == Dir.in) {

				String[] values = StringUtil.toStringArray(this.values);
				for (String value : values) {
					params.add(value.replaceAll("^" + Checks.NOCHECK_VALUE, ""));
				}

			} else {

				if (!this.values.toString().startsWith(Checks.NOCHECK_VALUE)) {
					params.add(this.values);
				}
			}
		}

		return params;
	}

	/** TODO UnitTestで使用するダミーモデル名の接頭辞 */
	public static final String UNIT_TEST_MODEL_NAME_PREFIX = "Ut";

	/**
	 * @return モデル名とプロパティ名から取得した「テーブル名.カラム名」
	 */
	private String getColumnName() {

		String tableName = this.modelName;
		String columnName = this.propertyName;

		if (!this.modelName.startsWith(UNIT_TEST_MODEL_NAME_PREFIX)) {

			TableInfo tableInfo = MetaData.getTableInfo(this.modelName);
			tableName = tableInfo.getTableName();

			ColumnInfo columnInfo = tableInfo.getColumnInfo(this.propertyName);
			if (columnInfo == null) {
				throw new SystemError(MessageKeys.ABEND_COLUMN_NONE, this.modelName, this.propertyName);
			}
			columnName = columnInfo.getColumnName();
		}

		return tableName + "." + columnName;
	}

	/**
	 * @return Criteriaでchain中の処理タイミングで有効なモデル名
	 */
	private String supplyModel() {
		List<Criteria> ors = this.or;
		if (ors != null && ors.size() > 0) {
			// 最上位のOR条件を降順にループ
			for (int i = ors.size() - 1; i >= 0; i--) {
				List<Criteria> ands = ors.get(i).and;
				if (ands != null && ands.size() > 0) {
					// 検索条件を降順にループ
					for (int j = ands.size() - 1; j >= 0; j--) {
						Criteria and = ands.get(j);
						if (and.modelName != null) {
							return and.modelName;
						}
					}
				}
			}
		}
		List<Criteria> orders = this.order;
		if (orders != null && orders.size() > 0) {
			// order条件を降順にループ
			for (int i = orders.size() - 1; i >= 0; i--) {
				Criteria order = orders.get(i);
				if (order.modelName != null) {
					return order.modelName;
				}
			}
		}
		return null;
	}

	/** セッションフォームからクライテリア化しないプロパティ名の正規表現 */
	private static final String CRITERIA_IGNORE_PROPERTIES_REGEX = "page|cantEdit|cantDelete";

	/** セッションフォームからソート順クライテリアにするプロパティ名の正規表現 */
	private static final Pattern ORDER_CRITERIA_REGEX = Pattern.compile("^orderby(.+)$");

	/** セッションフォームからOR条件クライテリアにするプロパティ名の正規表現 */
	private static final Pattern OR_CRITERIA_REGEX = Pattern.compile("^or(\\d+)([^\\d].+)$");

	/**
	 * @param modelName
	 *            modelName
	 * @param sessionForm
	 *            sessionForm
	 * @param criteria
	 *            criteria
	 * @return セッションフォームから生成したCriteria
	 */
	public static Criteria form2Criteria(final String modelName, final SessionForm sessionForm,
			final Criteria criteria) {

		// SessionFormがなければ終了
		if (sessionForm == null) {
			return null;
		}

		Criteria c = criteria;

		List<Criteria> ors = null;

		// 各モデル名でループ
		for (Entry<String, List<SessionModel>> sessionFormEntry : sessionForm.entrySet()) {

			// modelNameの指定がある場合、formModel名が合致しなければスキップ
			String formModelName = sessionFormEntry.getKey();
			if (modelName != null && !modelName.equals(formModelName)) {
				continue;
			}

			// 各SessionModelでループ
			List<SessionModel> sessionModels = sessionFormEntry.getValue();
			for (SessionModel sessionModel : sessionModels) {

				// 各プロパティでループ
				for (Entry<String, SessionProperty> property : sessionModel.entrySet()) {

					String propertyName = property.getKey();

					// プロパティ名が無視リストに設定済みならスキップ
					if (propertyName.matches(CRITERIA_IGNORE_PROPERTIES_REGEX)) {
						continue;
					}

					// プロパティ名が「orderby」に合致する場合
					Matcher orderMatcher = ORDER_CRITERIA_REGEX.matcher(propertyName);
					if (orderMatcher.find()) {

						String value = sessionModel.get(propertyName);

						propertyName = StringUtil.toCamelCase(orderMatcher.group(1));

						if (value.equals("asc")) {
							if (c == null) {
								c = Criteria.ascending(formModelName, propertyName);
							} else {
								c.asc(formModelName, propertyName);
							}
						} else if (value.equals("desc")) {
							if (c == null) {
								c = Criteria.descending(formModelName, propertyName);
							} else {
								c.desc(formModelName, propertyName);
							}
						}

						continue;
					}

					// プロパティ値がなければスキップ
					String[] values = sessionModel.gets(propertyName);
					if (StringUtil.isBlank(values)) {
						continue;
					}

					// プロパティ名が「or(数値)」に合致する場合
					Matcher orMatcher = OR_CRITERIA_REGEX.matcher(propertyName);
					if (orMatcher.find()) {

						int i = Integer.valueOf(orMatcher.group(1));

						propertyName = StringUtil.toCamelCase(orMatcher.group(2));

						if (ors == null) {
							ors = new ArrayList<Criteria>();
						}

						for (int j = ors.size(); j <= i; j++) {
							ors.add(new Criteria());
						}

						setCriteria(ors.get(i).or(), formModelName, propertyName, values);

						continue;
					}

					// 上記以外ならCriteriaを追加
					c = setCriteria(c, formModelName, propertyName, values);
				}
			}
		}

		if (ors != null) {
			for (Criteria or : ors) {
				if (c == null) {
					c = new Criteria();
				}
				c.or(or);
			}
		}

		return c;
	}

	/**
	 * @param sessionForm
	 *            sessionForm
	 * @return Criteria
	 */
	public static Criteria form2Criteria(final SessionForm sessionForm) {
		return form2Criteria(null, sessionForm, null);
	}

	/**
	 * @param sessionForm
	 *            sessionForm
	 * @param criteria
	 *            criteria
	 * @return Criteria
	 */
	public static Criteria form2Criteria(final SessionForm sessionForm, final Criteria criteria) {
		return form2Criteria(null, sessionForm, criteria);
	}

	/**
	 * @param sessionForm
	 *            sessionForm
	 * @param modelName
	 *            modelName
	 * @return Criteria
	 */
	public static Criteria form2Criteria(final String modelName, final SessionForm sessionForm) {
		return form2Criteria(modelName, sessionForm, null);
	}

	/**
	 * @param criteria
	 *            Criteria
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param values
	 *            values
	 * @return Criteria
	 */
	private static Criteria setCriteria(final Criteria criteria, final String modelName, final String propertyName,
			final String[] values) {

		Criteria c = criteria;
		if (c == null) {
			c = new Criteria();
		}

		if (Fieldset.RANGE_SUFFIX_SET.isEnd(propertyName) && values.length == 2) {

			/*
			 * 範囲指定の場合
			 */

			if (StringUtil.isNotBlank(values[0]) && StringUtil.isBlank(values[1])) {

				c.ge(modelName, propertyName, values[0]);

			} else if (StringUtil.isBlank(values[0]) && StringUtil.isNotBlank(values[1])) {

				c.le(modelName, propertyName, values[1]);

			} else {

				c.bw(modelName, propertyName, values[0], values[1]);
			}

		} else if (Fieldset.PART_SUFFIX_SET.isEnd(propertyName)) {

			/*
			 * 部分一致の場合
			 */

			Criteria or = new Criteria();

			for (String value : values) {
				// TODO サニタイズどうなってる？
				value = value.replaceAll("\\\\", "\\\\\\\\");
				value = value.replaceAll("%", "\\\\%");
				value = value.replaceAll("_", "\\\\_");
				or.or().lk(modelName, propertyName, "%" + value + "%");
			}

			c.and(or);

		} else if (values.length > 1) {

			/*
			 * 複数指定の場合
			 */

			c.in(modelName, propertyName, values);

		} else {

			/*
			 * 上記以外（等価評価）
			 */

			c.eq(modelName, propertyName, values[0]);
		}

		return c;
	}

}
