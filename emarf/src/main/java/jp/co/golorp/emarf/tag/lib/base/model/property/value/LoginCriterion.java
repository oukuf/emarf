/**
 *
 */
package jp.co.golorp.emarf.tag.lib.base.model.property.value;

import javax.servlet.jsp.JspException;

import jp.co.golorp.emarf.tag.lib.CriteriaTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * ログイン情報の検索条件タグ
 *
 * @author oukuf@golorp
 */
public class LoginCriterion extends Criterion {

	/** ログイン情報のモデル名 */
	private String loginModelName;

	/** ログイン情報のプロパティ名 */
	private String loginPropertyName;

	@Override
	public void release() {
		this.loginModelName = null;
		this.loginPropertyName = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		Object o = RequestUtil.getLoginValue(this.pageContext.getRequest(), this.loginModelName,
				this.loginPropertyName);

		String value = String.valueOf(o);

		CriteriaTagSupport parent = (CriteriaTagSupport) this.getParent();

		parent.addCriteria(this.getModelName(), this.getPropertyName(), value);

		return null;
	}

	/**
	 * @return loginModelName
	 */
	public String getLoginModelName() {
		return loginModelName;
	}

	/**
	 * @param loginModelName
	 *            セットする loginModelName
	 */
	public void setLoginModelName(final String loginModelName) {
		this.loginModelName = loginModelName;
	}

	/**
	 * @return loginPropertyName
	 */
	public String getLoginPropertyName() {
		return loginPropertyName;
	}

	/**
	 * @param loginPropertyName
	 *            セットする loginPropertyName
	 */
	public void setLoginPropertyName(final String loginPropertyName) {
		this.loginPropertyName = loginPropertyName;
	}

}
