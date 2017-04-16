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
package jp.co.golorp.emarf.tag.lib.base;

import java.util.List;

import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;

/**
 * スクリプトタグ
 *
 * @author oukuf@golorp
 */
public class Script extends BaseTagSupport {

	/** html属性：src */
	private String src;

	@Override
	public void release() {
		this.src = null;
		super.release();
	}

	@Override
	public String doStart() {

		String contextPath = this.pageContext.getServletContext().getContextPath();

		// srcがあればそのスクリプトタグを出力
		if (this.src != null) {
			return "<script type=\"text/javascript\" src=\"" + contextPath + this.src + "\"></script>";
		}

		// ContextRoot/jsフォルダ内のjsファイルパスを取得。なければ終了。
		List<String> filePaths = Taglib.listFiles(this.pageContext, "/js", "js");
		if (filePaths == null) {
			return null;
		}

		// 各ファイルパスのスクリプトタグを作成
		StringBuilder sb = new StringBuilder();
		for (String filePath : filePaths) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append("<script type=\"text/javascript\" src=\"").append(contextPath).append(filePath)
					.append("\"></script>");
		}

		return sb.toString();
	}

	/**
	 * srcを取得します。
	 *
	 * @return src
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * srcを設定します。
	 *
	 * @param src
	 *            src
	 */
	public void setSrc(final String src) {
		this.src = src;
	}

}
