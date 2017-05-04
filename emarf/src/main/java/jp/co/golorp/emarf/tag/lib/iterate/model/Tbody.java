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
package jp.co.golorp.emarf.tag.lib.iterate.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import jp.co.golorp.emarf.constants.scope.ReqKey;
import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.servlet.SessionFormFilter;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.IterateTagSupport;
import jp.co.golorp.emarf.tag.lib.base.Errors;
import jp.co.golorp.emarf.tag.lib.criteria.model.Table;
import jp.co.golorp.emarf.tag.lib.criteria.model.Tr;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * Tbodyタグ
 *
 * @author oukuf@golorp
 */
public class Tbody extends IterateTagSupport implements Modelable {

	/*
	 * ************************************************** タグプロパティ
	 */

	/** モデル名 */
	private String modelName;

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

	/***/
	protected int index;

	/***/
	protected int no;

	/*
	 * ************************************************** クラスメソッド
	 */

	/*
	 * ************************************************** コンストラクタ
	 */

	/**
	 *
	 */
	public Tbody() {
	}

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
	 *            cantView
	 * @param cantEdit
	 *            cantEdit
	 * @param cantDelete
	 *            cantDelete
	 * @param cantCheck
	 *            cantCheck
	 * @param edit
	 *            編集可否
	 * @param parent
	 *            親タグ（Tableタグ）
	 */
	public Tbody(final PageContext pageContext, final String modelName, final Criteria criteria,
			final String optionModel, final String optionValue, final String optionLabel, final String cantView,
			final String cantEdit, final String cantDelete, final String cantCheck, final String edit,
			final Tag parent) {

		super();
		this.pageContext = pageContext;
		this.modelName = modelName;
		this.criteria = criteria;
		this.optionModel = optionModel;
		this.optionValue = optionValue;
		this.optionLabel = optionLabel;
		this.cantView = cantView;
		this.cantEdit = cantEdit;
		this.cantDelete = cantDelete;
		this.cantCheck = cantCheck;
		this.edit = edit;
		this.setParent(parent);
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {

		this.modelName = null;
		this.cantView = null;
		this.cantEdit = null;
		this.cantDelete = null;
		this.cantCheck = null;
		this.edit = null;

		this.index = 0;
		this.no = 0;

		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return "<tbody>";
	}

	@Override
	public String doEnd() throws JspException {

		StringBuffer sb = new StringBuffer();

		if (!this.isPrintBody) {
			// JSPに内容を書いていなかった場合

			this.prepareOptionAttributes();

			// tbodyを出力
			sb.append(render());
		}

		sb.append("</tbody>");

		return sb.toString();
	}

	@Override
	public String toString() {

		// タグ文字列を出力
		StringBuilder sb = new StringBuilder();
		sb.append("<tbody>");
		sb.append(render());
		sb.append("</tbody>");

		return sb.toString();
	}

	@Override
	protected final Iterator<?> getIterator() {

		// doIterateで++するので-1で初期化
		this.index = -1;

		// 親タグから表示行数を取得。プロパティファイルから設定。
		String rows = Taglib.getParentAttribute(this, "rows");
		if (StringUtil.isBlank(rows)) {
			rows = Table.DEFAULT_PAGING_ROWS;
		}

		// 親タグからページ番号を取得。なければプロパティファイルから設定。
		String page = Taglib.getParentAttribute(this, "page");
		if (StringUtil.isBlank(page)) {
			page = Table.DEFAULT_PAGING_PAGE;
		}

		// 行番号を算出
		this.no = Integer.parseInt(rows) * (Integer.parseInt(page) - 1);

		ServletRequest request = this.pageContext.getRequest();

		// 行番号をリクエストスコープに登録
		request.setAttribute(ReqKey.TBODY_NO, this.no);

		// 編集モードを評価
		boolean isEdit = StringUtil.is(this.edit);

		// URIのモデル名
		String pathModelName = RequestUtil.getPathModelName(request);

		// URIのページ名
		String pathPageName = RequestUtil.getPathPageName(request);

		// URIのメソッド名
		String pathMethodName = RequestUtil.getPathMethodName(request);

		boolean isPathModel = StringUtil.equals(pathModelName, this.modelName);

		List<Model> datas = null;

		if (request.getAttribute(Errors.ATTRIBUTE_KEY) != null) {
			// エラーがある場合

			// 自モデルの検索処理の場合は一覧を表示しない
			if (isPathModel && pathMethodName.equalsIgnoreCase(EmarfServlet.METHOD_GET)) {
				return null;
			}

			/*
			 * モデル名がURIのモデル名と合致しない場合（子モデルの更新処理の場合）はリクエストパラメータからdatas生成
			 */

			// SessionFormFilterを通過してSessionFormが登録される前に、
			// エラーになって画面表示する可能性があるため、
			// リクエストパラメータからSessionFormを取得する
			SessionForm sessionForm = SessionFormFilter.param2SessionForm(request);
			if (sessionForm == null) {
				return null;
			}

			// populateしなくても、空のmodelを入れておけばrequestからlookupされる
			for (Iterator<SessionModel> i = sessionForm.itr(this.modelName); i.hasNext();) {
				i.next();
				if (datas == null) {
					datas = new ArrayList<Model>();
				}
				Model model = ModelUtil.getBlankModel(this.modelName);
				datas.add(model);
			}

		} else {
			// エラーがない場合

			if (!pathPageName.equals(EmarfServlet.PAGE_NEW)) {
				// 新規画面でない場合はデータ取得

				// TODO ログイン情報から自動でcriteria取得するのは廃止
				// // ログイン情報からクライテリア取得
				// Criteria c = RequestUtil.getCriteriaLogin(request,
				// this.modelName);

				Criteria c = null;

				String parentModelName = Taglib.getParentAttribute(this, "parentModelName");

				// if (!isPathModel) {
				if (parentModelName != null) {
					// 子モデルの場合

					// 編集用リストならページングしない
					if (isEdit) {
						rows = null;
						page = null;
					}

					// if (this.getParent() != null &&
					// this.getParent().getParent() != null
					// && this.getParent().getParent() instanceof Tables) {
					// Tables内なら親モデル・履歴元モデル・集約元モデルからクライテリア取得

					c = RequestUtil.addCriteriaParent(request, parentModelName, this.modelName, c);

				} else {

					// 主モデルならセッションフォームからクライテリア取得
					// （通常の検索処理）
					SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
					if (sessionForm != null) {
						c = Criteria.form2Criteria(sessionForm, c);
					}
				}

				// 主キーでソート指定
				Set<String> primaryPropertyNames = ModelUtil.getPrimaryPropertyNames(this.modelName);
				for (String propertyName : primaryPropertyNames) {
					if (c == null) {
						c = Criteria.ascending(this.modelName, propertyName);
					} else {
						c.asc(this.modelName, propertyName);
					}
				}

				datas = Models.getModels(this.modelName, c, rows, page);
			}

			// 子モデルかビューの編集モードの場合は新規行を追加
			if (isEdit) {
				if (!isPathModel || MetaData.getTableInfo(this.modelName).isView()) {
					if (datas == null) {
						datas = new ArrayList<Model>();
					}
					datas.add(ModelUtil.getBlankModel(this.modelName));
				}
			}
		}

		// データがある場合はループ
		if (datas == null) {
			return null;
		}
		return datas.iterator();
	}

	@Override
	protected void doIterate() {

		ServletRequest request = this.pageContext.getRequest();

		if (this.iterator != null && this.iterator.hasNext()) {

			// インデックスを増加
			++this.index;
			++this.no;

			// リクエストスコープに反復子と行番号を設定
			request.setAttribute(this.modelName, this.iterator.next());
			request.setAttribute(ReqKey.TBODY_NO, this.no);
		} else {
			request.removeAttribute(this.modelName);
			request.removeAttribute(ReqKey.TBODY_NO);
		}
	}

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @return タグ文字列
	 */
	private String render() {

		StringBuilder sb = new StringBuilder();

		boolean cantView = StringUtil.is(this.cantView);
		boolean cantEdit = StringUtil.is(this.cantEdit);
		boolean cantDelete = StringUtil.is(this.cantDelete);
		boolean cantCheck = StringUtil.is(this.cantCheck);
		boolean edit = StringUtil.is(this.edit);

		String parentModelName = Taglib.getParentAttribute(this, "parentModelName");

		this.iterator = getIterator();

		while (true) {

			doIterate();

			Object o = this.pageContext.getRequest().getAttribute(this.modelName);
			if (o == null) {
				break;
			}

			// プロパティ名の反復子
			Iterable<String> propertyNames = ModelUtil.getPropertyMeis(this.modelName).keySet();

			// html項目名の接頭辞
			String htmlNamePrefix = this.modelName + "[" + this.index + "]";

			// 行のタグ文字列を追加
			sb.append(Tr.render(this.pageContext, this.modelName, this.criteria, this.optionModel, this.optionValue,
					this.optionLabel, cantView, cantEdit, cantDelete, cantCheck, edit, propertyNames, htmlNamePrefix,
					parentModelName));
		}

		return sb.toString();
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

}
