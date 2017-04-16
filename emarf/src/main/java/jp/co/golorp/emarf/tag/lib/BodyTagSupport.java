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
package jp.co.golorp.emarf.tag.lib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.commons.lang3.StringUtils;

/**
 * 開始タグと閉じタグをもつタグ用基底クラス<br>
 * taglib.bodyパッケージ内で拡張
 *
 * @author oukuf@golorp
 */
public abstract class BodyTagSupport extends BaseTagSupport implements BodyTag {

	/** 開始タグから閉じタグまでの内容 */
	protected BodyContent bodyContent;

	/** bodyを出力したかのフラグ */
	protected boolean isPrintBody;

	@Override
	public void release() {
		this.bodyContent = null;
		this.isPrintBody = false;
		super.release();
	}

	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public void doInitBody() throws JspException {
	}

	@Override
	public int doAfterBody() throws JspException {

		// bodyContentがあれば出力
		BodyContent bodyContent = this.bodyContent;
		if (bodyContent != null) {
			String text = bodyContent.getString();
			if (StringUtils.isNotBlank(text)) {
				try {
					bodyContent.getEnclosingWriter().print(text);
				} catch (IOException e) {
					throw new JspException(e);
				} finally {
					bodyContent.clearBody();
				}

				// bodyContent出力フラグをたてる
				this.isPrintBody = true;
			}
		}

		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {

		this.print(this.doEnd());

		return super.doEndTag();
	}

	/**
	 * @return 閉じタグ文字列
	 * @throws JspException
	 *             JspException
	 */
	public abstract String doEnd() throws JspException;

	/**
	 * bodyContentを取得します。
	 *
	 * @return bodyContent
	 */
	public BodyContent getBodyContent() {
		return bodyContent;
	}

	/**
	 * bodyContentを設定します。
	 *
	 * @param bodyContent
	 *            bodyContent
	 */
	public void setBodyContent(final BodyContent bodyContent) {
		this.bodyContent = bodyContent;
	}

}
