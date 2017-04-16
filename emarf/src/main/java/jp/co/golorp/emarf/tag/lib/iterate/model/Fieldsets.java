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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.constants.model.Crud;
import jp.co.golorp.emarf.model.Model;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.servlet.http.form.SessionModel;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.IterateTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 兄弟モデルのfieldset出力タグ
 *
 * @author oukuf@golorp
 */
public class Fieldsets extends IterateTagSupport implements Modelable {

	/***/
	private String modelName;

	/***/
	private String parentModelName;

	@Override
	public void release() {
		this.modelName = null;
		this.parentModelName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {
		return null;
	}

	@Override
	public String doEnd() throws JspException {
		return null;
	}

	@Override
	protected Iterator<?> getIterator() {

		// モデル名を親モデル名として退避
		this.parentModelName = this.modelName;

		// 兄弟モデル情報を取得
		RelateTablesMap brothers = ModelUtil.getBrothers(this.parentModelName);
		if (brothers == null) {
			return null;
		}

		// 兄弟モデル情報でループ
		return brothers.entrySet().iterator();
	}

	@Override
	protected void doIterate() {

		if (this.iterator == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Entry<String, List<RelateColumnMap>> brother = (Entry<String, List<RelateColumnMap>>) this.iterator.next();

		this.modelName = brother.getKey();

		RelateColumnMap relateColumns = brother.getValue().get(0);

		ServletRequest request = this.pageContext.getRequest();

		SessionForm sessionForm = SessionFormUtil.getSessionForm(request);

		// 親モデルを取得。なければセッションフォームから取得
		Model parent = (Model) request.getAttribute(this.parentModelName);
		if (parent == null) {
			if (sessionForm != null) {
				SessionModel sessionModel = sessionForm.getModel(this.parentModelName);
				parent = SessionFormUtil.validate(Crud.REFER, sessionModel, null);
			}
		}

		Map<String, Object> primaryKeys = null;
		if (parent != null) {

			// 新規登録用に親モデルの外部キーを登録
			primaryKeys = new LinkedHashMap<String, Object>();
			for (Entry<String, String> relateProperty : relateColumns.entrySet()) {
				// TODO カラム名？プロパティ名？
				String propertyName = relateProperty.getKey();
				String propertyName2 = relateProperty.getValue();
				Object value = parent.get(propertyName);
				if (StringUtil.isNotBlank(value)) {
					primaryKeys.put(propertyName2, value);
				}
			}

			// 子モデルをリクエスト属性に登録
			Model model = ModelUtil.getBlankModel(this.modelName).populate(primaryKeys);
			Model referedModel = Models.refer(model);
			if (referedModel != null) {
				model = referedModel;
			}
			request.setAttribute(this.modelName, model);

			// 子モデル情報をSessionFormにも格納
			SessionModel sessionModel = new SessionModel(this.modelName);
			Map<String, Object> properties = model.getProperties();
			for (Entry<String, Object> property : properties.entrySet()) {
				String propertyName = property.getKey();
				Object value = property.getValue();
				String htmlName = Taglib.getHtmlName(this, propertyName);
				sessionModel.put(propertyName, htmlName, value);
			}

			if (sessionForm == null) {
				sessionForm = new SessionForm();
			}
			sessionForm.addModel(sessionModel);

			SessionFormUtil.setSessionForm(request, sessionForm);
		}
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
