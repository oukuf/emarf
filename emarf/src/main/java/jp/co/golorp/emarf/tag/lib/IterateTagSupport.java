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

import java.util.Iterator;

import javax.servlet.jsp.JspException;

/**
 * 反復評価するタグの基底クラス<br>
 * taglib.iterateパッケージ内で拡張
 *
 * @author oukuf@golorp
 */
public abstract class IterateTagSupport extends CriteriaTagSupport {

	/** データ反復子 */
	protected Iterator<?> iterator = null;

	@Override
	public void release() {
		this.iterator = null;
		super.release();
	}

	@Override
	public final int doStartTag() throws JspException {
		super.doStartTag();

		// 反復子を取得
		this.iterator = this.getIterator();

		if (this.iterator != null && this.iterator.hasNext()) {
			this.doIterate();
			return EVAL_BODY_AGAIN;
		}

		return SKIP_BODY;
	}

	@Override
	public final int doAfterBody() throws JspException {
		super.doAfterBody();

		if (this.iterator != null && this.iterator.hasNext()) {
			this.doIterate();
			return EVAL_BODY_AGAIN;
		}

		return SKIP_BODY;
	}

	/**
	 * @return Iterator<?>
	 * @throws JspException
	 *             JspException
	 */
	protected abstract Iterator<?> getIterator() throws JspException;

	/**
	 * @throws JspException
	 *             JspException
	 */
	protected abstract void doIterate() throws JspException;

}
