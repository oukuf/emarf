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
package jp.co.golorp.emarf.tag.lib.body;

import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.tag.lib.BodyTagSupport;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * formタグ
 *
 * @author oukuf@golorp
 */
public class Form extends BodyTagSupport {

	/***/
	private String className;

	@Override
	public void release() {
		this.className = null;
		super.release();
	}

	@Override
	public String doStart() {

		String pageName = RequestUtil.getPathPageName(this.pageContext.getRequest());

		if (StringUtil.isBlank(this.className)) {
			if (pageName.equals(EmarfServlet.PAGE_NEW)) {
				this.className = "toroku";
			} else if (pageName.equals(EmarfServlet.PAGE_EDIT)) {
				this.className = "koshin";
			} else if (pageName.equals(EmarfServlet.PAGE_VIEW)) {
				this.className = "shokai";
			} else if (pageName.equals(EmarfServlet.PAGE_TREE)) {
				this.className = "kaisou";
			} else {
				this.className = "kensaku";
			}
		}

		return "<form method=\"POST\" class=\"" + className + "\">";
	}

	@Override
	public String doEnd() {
		return "</form>";
	}

	/**
	 * classNameを取得します。
	 *
	 * @return className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * classNameを設定します。
	 *
	 * @param className
	 *            className
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

}
