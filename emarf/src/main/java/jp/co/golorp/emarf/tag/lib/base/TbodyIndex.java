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

import javax.servlet.ServletRequest;

import jp.co.golorp.emarf.constants.scope.ReqKey;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;

/**
 * TBody内でリクエストスコープに設定されたインデックスを出力
 *
 * @author oukuf@golorp
 */
public class TbodyIndex extends BaseTagSupport {

	@Override
	public String doStart() {
		ServletRequest request = this.pageContext.getRequest();
		Object o = request.getAttribute(ReqKey.TBODY_NO);
		if (o != null) {
			int no = (int) request.getAttribute(ReqKey.TBODY_NO);
			int index = no - 1;
			return String.valueOf(index);
		}
		return null;
	}

}
