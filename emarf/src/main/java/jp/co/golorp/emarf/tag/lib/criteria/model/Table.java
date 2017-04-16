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
package jp.co.golorp.emarf.tag.lib.criteria.model;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.tag.lib.base.Errors;
import jp.co.golorp.emarf.tag.lib.base.model.Caption;
import jp.co.golorp.emarf.tag.lib.body.model.Thead;
import jp.co.golorp.emarf.tag.lib.iterate.model.Tables;
import jp.co.golorp.emarf.tag.lib.iterate.model.Tbody;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * tableタグ
 *
 * @author oukuf@golorp
 */
public class Table extends CriteriaTagSupport implements Modelable {

	/** ページ行数 規定値 */
	public static final String DEFAULT_PAGING_ROWS = App.get(AppKey.TABLE_PAGING_ROWS_DEFAULT);

	/** 表示ページ 規定値 */
	public static final String DEFAULT_PAGING_PAGE = "1";

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

	/** １ページあたり行数 */
	private String rows;

	/** 表示するページ */
	private String page;

	/** 照会リンク表示有無 */
	private String cantView;

	/** 編集リンク表示有無 */
	private String cantEdit;

	/** 削除リンク表示有無 */
	private String cantDelete;

	/** 選択チェックボックス表示有無 */
	private String cantCheck;

	/** 編集可否 */
	private String edit;

	/*
	 * ************************************************** インスタンス変数
	 */

	/** 総件数 */
	private int count;

	/** 先頭ページ番号 */
	private int head;

	/** 前ページ番号 */
	private int prev;

	/** 次ページ番号 */
	private int next;

	/** 最終ページ番号 */
	private int last;

	/**
	 * @param pageContext
	 *            pageContext
	 * @param modelName
	 *            modelName
	 * @param criteria
	 *            criteria
	 * @param optionModel
	 *            optionModel
	 * @param optionValue
	 *            optionValue
	 * @param optionLabel
	 *            optionLabel
	 * @param cantView
	 *            照会リンク表示有無
	 * @param cantEdit
	 *            編集リンク表示有無
	 * @param cantDelete
	 *            削除ボタン表示有無
	 * @param cantCheck
	 *            選択チェックボックス表示有無
	 * @param edit
	 *            編集可否
	 * @param me
	 *            Tableタグインスタンス
	 * @return タグ文字列
	 */
	public static String render(final PageContext pageContext, final String modelName, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel, final boolean cantView,
			final boolean cantEdit, final boolean cantDelete, final boolean cantCheck, final boolean edit,
			final Tag me) {

		String caption = Caption.render(modelName, RequestUtil.getPathPageName(pageContext.getRequest()));

		String thead = Thead.render(pageContext, modelName, cantView, cantEdit, cantDelete, cantCheck, edit);

		String cantview = String.valueOf(cantView);
		String cantedit = String.valueOf(cantEdit);
		String cantdelete = String.valueOf(cantDelete);
		String cantcheck = String.valueOf(cantCheck);
		String isEdit = String.valueOf(edit);

		String tbody = new Tbody(pageContext, modelName, criteria, optionModel, optionValue, optionLabel, cantview,
				cantedit, cantdelete, cantcheck, isEdit, me).toString();

		StringBuilder sb = new StringBuilder(caption).append("<thead>").append(thead).append("</thead>").append(tbody);

		return sb.toString();
	}

	@Override
	public void release() {

		this.modelName = null;
		this.rows = null;
		this.page = null;
		this.cantView = null;
		this.cantEdit = null;
		this.cantDelete = null;
		this.cantCheck = null;
		this.edit = null;

		this.count = 0;
		this.head = 0;
		this.prev = 0;
		this.next = 0;
		this.last = 0;

		super.release();
	}

	@Override
	public String doStart() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		// 表示行数がなければセッションフォームから取得
		if (StringUtil.isBlank(this.rows)) {
			String[] values = SessionFormUtil.getValues(request, this.modelName + ".rows");
			if (StringUtil.isNotBlank(values)) {
				this.rows = values[0];
			}
		}

		// 表示行数がなければプロパティファイルから設定
		if (StringUtil.isBlank(this.rows)) {
			this.rows = DEFAULT_PAGING_ROWS;
		}

		// ページ番号がなければセッションフォームから取得
		if (StringUtil.isBlank(this.page)) {
			String[] values = SessionFormUtil.getValues(request, this.modelName + ".page");
			if (StringUtil.isNotBlank(values)) {
				this.page = values[0];
			}
		}

		// ページ番号がなければプロパティファイルから設定
		if (StringUtil.isBlank(this.page)) {
			this.page = DEFAULT_PAGING_PAGE;
		}

		if (!RequestUtil.getPathPageName(request).equals(EmarfServlet.PAGE_NEW)) {
			// 新規画面でない場合

			String pathMethodName = RequestUtil.getPathMethodName(request);
			if (!(StringUtil.equalsIgnoreCase(pathMethodName, EmarfServlet.METHOD_GET)
					&& request.getAttribute(Errors.ATTRIBUTE_KEY) != null)) {
				// 検索エラーでない場合

				// TODO ログイン情報から自動でcriteria取得するのは廃止
				// // ログイン情報からクライテリア取得
				// Criteria c = RequestUtil.getCriteriaLogin(request,
				// this.modelName);

				Criteria c = null;

				if (this.getParent() instanceof Tables) {

					// Tables内ならTablesタグのモデルを親・履歴元・集約元としてクライテリア取得
					String parentModelName = Taglib.getParentAttribute(this, "parentModelName");
					c = RequestUtil.addCriteriaParent(request, parentModelName, this.modelName, c);

				} else {

					// Tables内でなければセッションフォームからクライテリア取得
					SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
					if (sessionForm != null) {
						c = Criteria.form2Criteria(sessionForm, c);
					}
				}

				// 総件数を取得
				this.count = Models.count(this.modelName, c);
			}
		}

		/*
		 * ページング用各数値を算出
		 */

		String s = "";

		if (!StringUtil.is(this.edit) || RequestUtil.getPathModelName(request).equals(this.modelName)) {

			// 表示行数
			int rows = Integer.valueOf(this.rows);

			// 表示ページ番号
			int page = Integer.valueOf(this.page);

			// 先頭ページ番号
			this.head = 1;

			// 最終ページ番号
			this.last = ((this.count - 1) / rows) + 1;

			// 前ページ番号
			this.prev = 1;
			if (page > 1) {
				this.prev = page - 1;
			}

			// 次ページ番号
			this.next = this.last;
			if (page < this.last) {
				this.next = page + 1;
			}

			// ページング部品のタグ文字列を追加
			s = paging(this.modelName, this.head, this.prev, this.next, this.last, page);
		}

		return s + "<table>";
	}

	@Override
	public String doEnd() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		String s = "";

		if (!this.isPrintBody) {
			// JSPに内容が書かれていなかった場合

			this.prepareOptionAttributes();

			boolean cantView = StringUtil.is(this.cantView);
			boolean cantEdit = StringUtil.is(this.cantEdit);
			boolean cantDelete = StringUtil.is(this.cantDelete);
			boolean cantCheck = StringUtil.is(this.cantCheck);

			// 新規登録リンク表示有無フラグ
			// boolean isNew = this.count == 0;

			boolean edit = StringUtil.is(this.edit);

			// タグ文字列を追加
			s = render(this.pageContext, this.modelName, this.criteria, this.optionModel, this.optionValue,
					this.optionLabel, cantView, cantEdit, cantDelete, cantCheck, edit, this);
		}

		s += "</table>";

		if (this.page == null) {
			this.page = DEFAULT_PAGING_PAGE;
		}

		// 検索結果があるならページング部品のタグ文字列を追加
		if (/* this.count > 0&& */
		(!StringUtil.is(this.edit) || RequestUtil.getPathModelName(request).equals(this.modelName))) {
			s += paging(this.modelName, this.head, this.prev, this.next, this.last, Integer.valueOf(this.page));
		}

		return s;
	}

	/**
	 * @param modelName
	 *            modelName
	 * @param head
	 *            先頭ページ番号
	 * @param prev
	 *            前ページ番号
	 * @param next
	 *            次ページ番号
	 * @param last
	 *            最終ページ番号
	 * @param page
	 *            表示ページ番号
	 * @return ページング部品のタグ文字列
	 */
	private static String paging(final String modelName, final int head, final int prev, final int next, final int last,
			final int page) {

		// // １ページだけなら表示しない
		// if (last == 1) {
		// return "";
		// }

		// 先頭ページフラグ
		boolean isHead = page == head;

		// 最終ページフラグ
		boolean isLast = page == last;

		// タグ文字列生成開始
		StringBuilder sb = new StringBuilder("<div class=\"paging\">");

		// 先頭ページリンクと前ページリンクを出力
		if (isHead) {
			sb.append(" |&lt; &lt;&lt; ");
		} else {
			sb.append("<a href=\"./\" data-params='{ \"").append(modelName).append(".page\" : \"").append(head)
					.append("\" }'>|&lt;</a>");
			sb.append("<a href=\"./\" data-params='{ \"").append(modelName).append(".page\" : \"").append(prev)
					.append("\" }'>&lt;&lt;</a>");
		}

		sb.append(page).append(SEP).append(last);

		// 次ページリンクと最終ページリンクを出力
		if (isLast) {
			sb.append(" &gt;&gt; &gt;| ");
		} else {
			sb.append("<a href=\"./\" data-params='{ \"").append(modelName).append(".page\" : \"").append(next)
					.append("\" }'>&gt;&gt;</a>");
			sb.append("<a href=\"./\" data-params='{ \"").append(modelName).append(".page\" : \"").append(last)
					.append("\" }'>&gt;|</a>");
		}

		sb.append("</div>");

		return sb.toString();
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

	/**
	 * @return edit
	 */
	public String getEdit() {
		return edit;
	}

	/**
	 * @param edit
	 *            セットする edit
	 */
	public void setEdit(final String edit) {
		this.edit = edit;
	}

	/**
	 * @return rows
	 */
	public String getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            セットする rows
	 */
	public void setRows(final String rows) {
		this.rows = rows;
	}

}
