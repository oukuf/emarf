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

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;

/**
 * ウィンドウタイトルを出力
 *
 * @author oukuf@golorp
 */
public class Title extends BaseTagSupport {

	/***/
	private static final String NAME = App.get(AppKey.TITLE_NAME);

	@Override
	public String doStart() {
		return "<title>" + NAME + "</title>";
	}

}
