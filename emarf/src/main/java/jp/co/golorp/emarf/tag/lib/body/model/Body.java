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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.constants.model.Crud;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BodyTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * bodyタグ
 *
 * @author oukuf@golorp
 */
public class Body extends BodyTagSupport implements Modelable {

	/***/
	private String modelName;

	@Override
	public void release() {
		this.modelName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		// モデル名がない場合は終了
		if (StringUtil.isBlank(this.modelName)) {
			return "<body>";
		}

		// 一覧画面の場合は終了
		ServletRequest request = this.pageContext.getRequest();
		String pageName = RequestUtil.getPathPageName(request);
		if (pageName.equals(EmarfServlet.PAGE_INDEX)) {
			return "<body>";
		}

		// セッションフォームの値を使って検索したモデルをリクエストスコープに設定
		SessionForm sessionForm = SessionFormUtil.getSessionForm(request);
		if (sessionForm != null) {
			// セッションフォームがある場合

			// bodyなのでgetでいいと思う
			SessionModel sessionModel = sessionForm.getModel(this.modelName);

			// if (StringUtils.isNotBlank(sessionModel.getAll())) {
			// TODO insert後の検索でエラーになる
			Model model = SessionFormUtil.validate(Crud.REFER, sessionModel, null);
			model = Models.refer(model);
			request.setAttribute(this.modelName, model);
			// }
		}

		return "<body>";
	}

	@Override
	public String doEnd() throws JspException {
		return "</body>";
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

}
