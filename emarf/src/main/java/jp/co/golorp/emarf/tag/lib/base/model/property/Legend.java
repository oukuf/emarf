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
package jp.co.golorp.emarf.tag.lib.base.model.property;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Label;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * legendタグ<br>
 * フィールドセットのタイトル部分
 *
 * @author oukuf@golorp
 */
public class Legend extends BaseTagSupport implements Propertiable {

	/** ラベル規定値 */
	private static final String INDEX_DEFAULT = App.get(AppKey.LEGEND_INDEX_DEFAULT);

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param modelName
	 *            modelName
	 * @param propertyName
	 *            propertyName
	 * @param pageName
	 *            pageName
	 * @param notnull
	 *            notnull
	 * @return タグ文字列
	 */
	public static final String render(final String modelName, final String propertyName, final String pageName,
			final boolean notnull) {

		// ラベル文字列にモデル名を設定
		String label = ModelUtil.getModelMei(modelName);

		if (StringUtils.isNotBlank(propertyName)) {

			// プロパティ名があれば、ラベル文字列にプロパティ名を設定
			Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);
			if (propertyMeis != null) {
				label = propertyMeis.get(propertyName);
			}

		} else if (pageName.equalsIgnoreCase(EmarfServlet.PAGE_INDEX)) {

			// プロパティ名がなく検索画面なら、検索画面用のラベルを設定
			label = INDEX_DEFAULT;
		}

		StringBuilder sb = new StringBuilder("<legend>").append(label);

		if (notnull) {
			sb.append(" ").append(Label.NOTNULL_MARK);
		}

		sb.append("</legend>");

		return sb.toString();
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		String pageName = RequestUtil.getPathPageName(request);

		boolean notnull = false;

		ColumnInfo columnInfo = MetaData.getColumnInfo(this.modelName, this.propertyName);
		if (columnInfo != null) {
			// プロパティ用の出力の場合

			boolean isIndex = StringUtils.equalsIgnoreCase(pageName, EmarfServlet.PAGE_INDEX);
			if (!isIndex) {
				// 検索画面でない場合

				boolean readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);
				if (!readonly) {
					// 読み取り専用でない場合

					// NOT NULLか評価
					notnull = columnInfo.getNullable() != 1;
				}
			}
		}

		return Legend.render(this.modelName, this.propertyName, pageName, notnull);
	}

	/*
	 * ************************************************** アクセサ
	 */

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

}
