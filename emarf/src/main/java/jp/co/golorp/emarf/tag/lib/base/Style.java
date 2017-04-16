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

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;

/**
 * スタイルタグ
 *
 * @author oukuf@golorp
 */
public class Style extends BaseTagSupport {

	/** PC用スタイルファイル名キーワード */
	private static final String PC_KEYWORD = App.get(AppKey.STYLE_PC_KEYWORD);

	/** SP用スタイルファイル名キーワード */
	private static final String SP_KEYWORD = App.get(AppKey.STYLE_SP_KEYWORD);

	/** PC版スタイル用media設定 */
	private static final String MEDIA_PC;

	/** スマホ版スタイルmedia設定 */
	private static final String MEDIA_SP;

	static {
		String mediaPcMinWidth = App.get(AppKey.STYLE_MEDIA_PC_MIN_WIDTH);
		int mediaPcMinWidthValue = Integer.valueOf(mediaPcMinWidth);
		String mediaSpMaxWidth = String.valueOf(mediaPcMinWidthValue - 1);
		MEDIA_PC = "screen and (min-width: " + mediaPcMinWidth + "px)";
		MEDIA_SP = "screen and (max-width: " + mediaSpMaxWidth + "px)";
	}

	/** html属性：href */
	private String href;

	@Override
	public void release() {
		this.href = null;
		super.release();
	}

	@Override
	public String doStart() {

		String contextPath = this.pageContext.getServletContext().getContextPath();

		// hrefがあればそのスタイルタグを出力
		if (this.href != null) {
			String media = "";
			if (this.href.contains(PC_KEYWORD)) {
				media = MEDIA_PC;
			} else if (this.href.contains(SP_KEYWORD)) {
				media = MEDIA_SP;
			}
			return "<link rel=\"stylesheet\" type=\"text/css\" media=\"" + media + "\" href=\"" + contextPath
					+ this.href + "\">";
		}

		// ContextRoot/cssフォルダ内のcssファイルパスを取得。なければ終了。
		List<String> filePaths = Taglib.listFiles(this.pageContext, "/css", "css");
		if (filePaths == null) {
			return null;
		}

		// 各ファイルパスのスタイルタグを作成
		StringBuilder sb = new StringBuilder();
		for (String filePath : filePaths) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append("<link rel=\"stylesheet\" type=\"text/css\"");
			if (filePath.contains(PC_KEYWORD)) {
				sb.append(" media=\"").append(MEDIA_PC).append("\"");
			} else if (filePath.contains(SP_KEYWORD)) {
				sb.append(" media=\"").append(MEDIA_SP).append("\"");
			}
			sb.append(" href=\"").append(contextPath).append(filePath).append("\">");
		}

		return sb.toString();
	}

	/**
	 * hrefを取得します。
	 *
	 * @return href
	 */
	public String getHref() {
		return href;
	}

	/**
	 * hrefを設定します。
	 *
	 * @param href
	 *            href
	 */
	public void setHref(final String href) {
		this.href = href;
	}

}
