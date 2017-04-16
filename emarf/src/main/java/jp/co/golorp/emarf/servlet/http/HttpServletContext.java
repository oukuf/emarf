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
package jp.co.golorp.emarf.servlet.http;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.co.golorp.emarf.model.Criteria;
import jp.co.golorp.emarf.servlet.http.form.SessionForm;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.SessionFormUtil;
import jp.co.golorp.emarf.util.mail.MailInfo;

/**
 * アクションクラスに渡すためのリクエストとレスポンスの入れ物
 *
 * @author oukuf@golorp
 */
public final class HttpServletContext {

	/** HttpServletRequest */
	private HttpServletRequest req;

	/** HttpServletResponse */
	@SuppressWarnings("unused")
	private HttpServletResponse resp;

	/**
	 * コンストラクタ
	 *
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 */
	public HttpServletContext(final HttpServletRequest req, final HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	/**
	 * @return parameterMapがカラでないならtrue
	 */
	public boolean isPosted() {
		return !this.req.getParameterMap().isEmpty();
	}

	/**
	 * @param <T>
	 *            取得するデータ型
	 * @param name
	 *            キー
	 * @return コンテキスト属性
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(final String name) {
		return (T) this.req.getServletContext().getAttribute(name);
	}

	/**
	 * @param key
	 *            セッションキー
	 * @param value
	 *            セッション属性値
	 */
	public void setAttribute(final String key, final Object value) {
		this.req.getServletContext().setAttribute(key, value);
	}

	/**
	 * @param <T>
	 *            取得するデータ型
	 * @param name
	 *            キー
	 * @return セッション属性
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttribute(final String name) {
		return (T) this.req.getSession().getAttribute(name);
	}

	/**
	 * セッション属性を設定
	 *
	 * @param name
	 *            キー
	 * @param value
	 *            値
	 */
	public void setSessionAttribute(final String name, final Object value) {
		this.req.getSession().setAttribute(name, value);
	}

	/**
	 * @param <T>
	 *            取得するデータ型
	 * @param modelName
	 *            モデル名
	 * @param propertyName
	 *            プロパティ名
	 * @return ログイン情報に保管済みの値
	 */
	public <T> T getLoginValue(final String modelName, final String propertyName) {
		return RequestUtil.getLoginValue(this.req, modelName, propertyName);
	}

	/**
	 * セッションにメール送信を予約
	 *
	 * @param to
	 *            送信先
	 * @param cc
	 *            CC
	 * @param bcc
	 *            BCC
	 * @param subject
	 *            タイトル
	 * @param text
	 *            本文
	 * @param filePaths
	 *            添付ファイルパスの配列
	 */
	public void reserveMail(final Map<String, String> to, final Map<String, String> cc, final Map<String, String> bcc,
			final String subject, final StringBuilder text, final List<String> filePaths) {

		MailInfo mi = new MailInfo();

		mi.setTo(to);

		mi.setCc(cc);

		mi.setBcc(bcc);

		mi.setSubject(subject);

		mi.addText(text.toString());

		if (filePaths != null) {
			for (String filePath : filePaths) {
				mi.addFile(filePath);
			}
		}

		this.setSessionAttribute(MailInfo.class.getName(), mi);
	}

	/**
	 * 相対パスのURIでセッションフォームを保存
	 *
	 * @param relativePath
	 *            相対パス
	 * @param sessionForm
	 *            セッションフォーム
	 */
	public void setSessionForm(final String relativePath, final SessionForm sessionForm) {
		SessionFormUtil.setSessionForm(this.req, sessionForm, relativePath);
	}

	/**
	 * リクエストURIでセッションフォームを保存
	 *
	 * @param sessionForm
	 *            セッションフォーム
	 */
	public void setSessionForm(final SessionForm sessionForm) {
		SessionFormUtil.setSessionForm(this.req, sessionForm, null);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @param criteria
	 *            criteria
	 * @return 指定モデルについてログイン情報からCriteriaを取得
	 */
	public Criteria addCriteriaLogin(final String modelName, final Criteria criteria) {
		return RequestUtil.addCriteriaLogin(this.req, modelName, criteria);
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @return 指定モデルについてSessionFormからCriteriaを取得
	 */
	public Criteria getCriteria(final String modelName) {
		SessionForm sessionForm = SessionFormUtil.getSessionForm(this.req);
		return Criteria.form2Criteria(sessionForm);
	}

	/**
	 * @return URL内のモデル名
	 */
	public String getModelName() {
		return RequestUtil.getPathModelName(this.req);
	}

}
