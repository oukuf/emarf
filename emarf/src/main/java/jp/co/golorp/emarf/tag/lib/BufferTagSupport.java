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

/**
 * 開始タグと閉じタグをもつタグ用基底クラス<br>
 * ただし開始タグでの出力はせず、bodyContentをbufferに退避していく。<br>
 * taglib.bufferパッケージ内で拡張
 *
 * @author oukuf@golorp
 */
public abstract class BufferTagSupport extends BodyTagSupport {

	/** bodyContentの退避 */
	protected String buffer;

	@Override
	public void release() {
		this.buffer = null;
		super.release();
	}

	@Override
	public int doAfterBody() throws JspException {

		// bodyContentをbufferに退避してクリアする
		BodyContent bodyContent = this.bodyContent;
		if (bodyContent != null) {
			this.buffer = bodyContent.getString();
			try {
				bodyContent.clear();
			} catch (IOException e) {
				throw new JspException(e);
			}
		}

		return SKIP_BODY;
	}

	@Override
	public String doStart() {
		return null;
	}

}
