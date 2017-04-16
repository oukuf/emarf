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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 自動生成される各モデルの抽象クラス
 *
 * @author oukuf@golorp
 */
public abstract class Model implements Serializable {

	/** パッケージのセパレータ */
	public static final String SEP = ".";

	/**
	 * 自インスタンスのプロパティに、入力チェックなしで直接、値を設定する
	 *
	 * @param properties
	 *            プロパティ情報
	 * @return 自インスタンス
	 */
	public final Model populate(final Map<String, ? extends Object> properties) {

		if (properties == null) {
			return null;
		}

		Field[] fields = this.getClass().getDeclaredFields();

		for (Entry<String, ? extends Object> property : properties.entrySet()) {
			String propertyName = property.getKey();
			Object value = property.getValue();

			if (StringUtil.isBlank(value)) {
				value = null;
			}

			for (Field field : fields) {

				if (!propertyName.equals(field.getName())) {
					continue;
				}

				field.setAccessible(true);

				try {
					field.set(this, value);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new SystemError(e);
				}

				break;
			}
		}

		return this;
	}

	/**
	 * @return 自インスタンスのプロパティ情報
	 */
	public final Map<String, Object> getProperties() {

		String modelName = this.getClass().getSimpleName();

		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);

		Method[] methods = this.getClass().getDeclaredMethods();

		Map<String, Object> properties = new LinkedHashMap<String, Object>();

		// プロパティ名でループ
		if (propertyMeis != null) {
			for (String propertyName : propertyMeis.keySet()) {

				// ゲッター名を生成
				String getterName = "get" + StringUtil.toUpperCamelCase(propertyName);

				// メソッド名でループ
				for (Method method : methods) {

					if (getterName.equals(method.getName())) {
						// 対象ゲッターである場合は実行

						try {
							Object value = method.invoke(this);
							if (value != null) {
								properties.put(propertyName, value.toString());
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new SystemError(e);
						}
						break;
					}
				}
			}
		}

		return properties;
	}

	/**
	 * 指定したプロパティ値を取得
	 *
	 * @param <T>
	 *            取得するデータ型
	 * @param propertyName
	 *            プロパティ名
	 * @return プロパティ値
	 */
	public final <T> T get(final String propertyName) {

		String getterName = "get" + StringUtil.toUpperCamelCase(propertyName);

		Method getter = null;
		try {
			getter = this.getClass().getDeclaredMethod(getterName);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}

		try {
			@SuppressWarnings("unchecked")
			T value = (T) getter.invoke(this);
			return value;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * プロパティ値を設定
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @param value
	 *            プロパティ値
	 */
	public final void set(final String propertyName, final Object value) {

		String setterName = "set" + StringUtil.toUpperCamelCase(propertyName);

		Method setter = null;
		try {
			Class<?> modelClass = this.getClass();
			if (value != null) {
				setter = modelClass.getDeclaredMethod(setterName, value.getClass());
			} else {
				Method[] methods = modelClass.getDeclaredMethods();
				for (Method method : methods) {
					if (setterName.equals(method.getName())) {
						setter = method;
						break;
					}
				}
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new SystemError(e);
		}

		try {
			setter.invoke(this, value);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SystemError(e);
		}
	}

	/**
	 * プロパティ値を文字列として取得
	 *
	 * @param propertyName
	 *            プロパティ名
	 * @return 可能ならフォーマットして、文字列化したプロパティ値
	 */
	public final String getString(final String propertyName) {

		String ucase = StringUtil.toUpperCamelCase(propertyName);

		Method getter = null;
		try {
			getter = this.getClass().getDeclaredMethod("format" + ucase);
		} catch (NoSuchMethodException e) {
			try {
				getter = this.getClass().getDeclaredMethod("get" + ucase);
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new SystemError(e1);
			}
		} catch (SecurityException e) {
			throw new SystemError(e);
		}

		try {
			Object o = getter.invoke(this);
			if (o == null) {
				return null;
			}
			return String.valueOf(o);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new SystemError(e);
		}
	}

}
