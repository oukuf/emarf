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
package jp.co.golorp.emarf.tag.lib.base.model;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.tag.lib.base.model.property.Th;
import jp.co.golorp.emarf.tag.lib.body.model.Anchor;
import jp.co.golorp.emarf.tag.lib.criteria.model.Fieldset;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * モデルの全プロパティについてカラム論理名のthタグを出力
 *
 * @author oukuf@golorp
 */
public class Ths extends BaseTagSupport implements Modelable {

	/** 一つ目のセルの規定値 */
	private static final String FIRST_DEFAULT = App.get(AppKey.THS_FIRST_DEFAULT);

	/** タグ属性：モデル名 */
	private String modelName;

	/** 照会リンク表示有無 */
	private String cantView;

	/** 編集リンク表示有無 */
	private String cantEdit;

	/** 削除リンク表示有無 */
	private String cantDelete;

	/** 選択チェックボックス表示有無 */
	private String cantCheck;

	/**
	 * @param pageContext
	 *            ページコンテキスト
	 * @param modelName
	 *            モデル名
	 * @param cantView
	 *            照会リンク禁止か
	 * @param cantEdit
	 *            編集リンク禁止か
	 * @param cantDelete
	 *            削除ボタン禁止か
	 * @param cantCheck
	 *            選択禁止か
	 * @param edit
	 *            編集可否
	 * @return 表示すべきずべてのthタグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit) {

		// １列目を出力
		StringBuilder sb = new StringBuilder("<th>").append(FIRST_DEFAULT).append("</th>");

		// 項目の出力
		Map<String, String> propertyMeis = ModelUtil.getPropertyMeis(modelName);
		if (propertyMeis != null) {
			for (Entry<String, String> propertyMei : propertyMeis.entrySet()) {
				String propertyName = propertyMei.getKey();

				// 削除フラグなら表示しない
				if (StringUtil.equalsIgnoreCase(StringUtil.toCamelCase(BeanGenerator.DELETE_F), propertyName)) {
					continue;
				}

				if (!Fieldset.AINT_SELECT_SUFFIX_SET.isEnd(propertyName)) {
					sb.append(Th.render(modelName, propertyName, 0, 0, propertyMei.getValue()));
				}
			}
		}

		ServletRequest request = pageContext.getRequest();
		String pageName = RequestUtil.getPathPageName(request);
		boolean isPick = pageName != null && pageName.equals(EmarfServlet.PAGE_PICK);
		String pathModelName = RequestUtil.getPathModelName(request);

		if (isPick) {

			// 選択画面の場合は、選択クリアボタンを出力
			sb.append("<th><input type=\"button\" value=\"クリア\" onclick=\"" + EmarfServlet.PAGE_PICK
					+ ".clear(this);\"></th>");

		} else if (edit) {

			// 編集可の場合は、空セルを出力
			sb.append("<th></th>");

		} else if (!cantView || !cantEdit || !cantDelete) {
			// 照会・編集・削除の何れかが有効な場合

			sb.append("<th>");

			// 選択画面以外で新規登録が有効な場合か、URIとモデル名が異なる場合は、新規登録リンクを出力
			// FIXME 検索結果がなかった時だけ新規作成リンクを出すのか？やっぱり使いにくくないか？
			// if ((isNew && !isPick) || !modelName.equals(pathModelName)) {
			sb.append(Anchor.render(pageContext, modelName, EmarfServlet.PAGE_NEW, null, null)).append("新規</a>");
			// }

			sb.append("</th>");
		}

		// 選択画面でなく選択不可でなくURIとモデル名が等しい場合は、全選択チェックボックスを出力
		if (!isPick && !cantCheck && StringUtil.equals(pathModelName, modelName)) {
			sb.append("<th><input type=\"checkbox\" id=\"Check").append(modelName)
					.append("\" onclick=\"clickable.checkAll(this);\" checked></th>");
		}

		return sb.toString();
	}

	@Override
	public void release() {
		this.modelName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		// 検索結果の件数を取得
		// int c = 0;
		// String count = TagUtils.getParentAttribute(this, "count");
		// if (count != null) {
		// c = Integer.valueOf(count);
		// }

		// 検索結果が０件なら新規登録フラグを立てる
		// boolean isNew = c == 0;

		// viewの場合
		TableInfo tableInfo = MetaData.getTableInfo(this.modelName);
		if (tableInfo != null) {
			String tableType = tableInfo.getTableType();
			if (StringUtil.equals(tableType, "VIEW")) {
				return render(this.pageContext, this.modelName, true, true, true, true, false);
			}
		}

		boolean cantView = StringUtil.is(this.cantView);
		boolean cantEdit = StringUtil.is(this.cantEdit);
		boolean cantDelete = StringUtil.is(this.cantDelete);
		boolean cantCheck = StringUtil.is(this.cantCheck);
		boolean edit = StringUtil.is(Taglib.getParentAttribute(this, "edit"));

		// view以外の場合
		return render(this.pageContext, this.modelName, cantView, cantEdit, cantDelete, cantCheck, edit);
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * 照会リンク表示有無を取得します。
	 *
	 * @return 照会リンク表示有無
	 */
	public String getCantView() {
		return cantView;
	}

	/**
	 * 照会リンク表示有無を設定します。
	 *
	 * @param cantView
	 *            照会リンク表示有無
	 */
	public void setCantView(final String cantView) {
		this.cantView = cantView;
	}

	/**
	 * 編集リンク表示有無を取得します。
	 *
	 * @return 編集リンク表示有無
	 */
	public String getCantEdit() {
		return cantEdit;
	}

	/**
	 * 編集リンク表示有無を設定します。
	 *
	 * @param cantEdit
	 *            編集リンク表示有無
	 */
	public void setCantEdit(final String cantEdit) {
		this.cantEdit = cantEdit;
	}

	/**
	 * 削除リンク表示有無を取得します。
	 *
	 * @return 削除リンク表示有無
	 */
	public String getCantDelete() {
		return cantDelete;
	}

	/**
	 * 削除リンク表示有無を設定します。
	 *
	 * @param cantDelete
	 *            削除リンク表示有無
	 */
	public void setCantDelete(final String cantDelete) {
		this.cantDelete = cantDelete;
	}

	/**
	 * cantCheckを取得します。
	 *
	 * @return cantCheck
	 */
	public String getCantCheck() {
		return cantCheck;
	}

	/**
	 * cantCheckを設定します。
	 *
	 * @param cantCheck
	 *            cantCheck
	 */
	public void setCantCheck(final String cantCheck) {
		this.cantCheck = cantCheck;
	}

}
