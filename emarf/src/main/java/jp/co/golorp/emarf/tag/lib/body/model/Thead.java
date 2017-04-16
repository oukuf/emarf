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
package jp.co.golorp.emarf.tag.lib.body.model;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BodyTagSupport;
import jp.co.golorp.emarf.tag.lib.criteria.model.Tr;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * theadタグ
 *
 * @author oukuf@golorp
 */
public class Thead extends BodyTagSupport implements Modelable {

	/***/
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
	 *            pageContext
	 * @param modelName
	 *            モデル名
	 * @param cantView
	 *            照会リンク非表示
	 * @param cantEdit
	 *            編集リンク非表示
	 * @param cantDelete
	 *            削除リンク非表示
	 * @param cantCheck
	 *            選択チェックボックス非表示
	 * @param edit
	 *            編集可
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit) {
		return Tr.render(pageContext, modelName, cantView, cantEdit, cantDelete, cantCheck, edit);
	}

	@Override
	public void release() {
		this.modelName = null;
		this.cantView = null;
		this.cantEdit = null;
		this.cantDelete = null;
		this.cantCheck = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return "<thead>";
	}

	@Override
	public String doEnd() throws JspException {

		String s = "";

		if (!this.isPrintBody) {
			// bodyを出力していない場合（＝JSPにセルをベタ書きしていない場合）

			// 親タグのcountプロパティが0なら新規フラグオン
			// boolean isNew = false;
			// String count = TagUtils.getParentAttribute(this, "count");
			// if (count != null) {
			// isNew = Integer.valueOf(count) == 0;
			// }

			boolean cantView = StringUtil.is(this.cantView);
			boolean cantEdit = StringUtil.is(this.cantEdit);
			boolean cantDelete = StringUtil.is(this.cantDelete);
			boolean cantCheck = StringUtil.is(this.cantCheck);
			boolean edit = StringUtil.is(Taglib.getParentAttribute(this, "edit"));

			// trタグを出力
			s = render(this.pageContext, this.modelName, cantView, cantEdit, cantDelete, cantCheck, edit);
		}

		return s + "</thead>";
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
	 * 選択チェックボックス表示有無を取得します。
	 *
	 * @return 選択チェックボックス表示有無
	 */
	public String getCantCheck() {
		return cantCheck;
	}

	/**
	 * 選択チェックボックス表示有無を設定します。
	 *
	 * @param cantCheck
	 *            選択チェックボックス表示有無
	 */
	public void setCantCheck(final String cantCheck) {
		this.cantCheck = cantCheck;
	}

}
