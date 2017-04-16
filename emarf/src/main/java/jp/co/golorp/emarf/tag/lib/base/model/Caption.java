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
package jp.co.golorp.emarf.tag.lib.base.model;

import javax.servlet.ServletRequest;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.tag.interfaces.Modelable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;

/**
 * キャプションタグ
 *
 * @author oukuf@golorp
 */
public class Caption extends BaseTagSupport implements Modelable {

	/** キャプション規定値 */
	private static final String INDEX_DEFAULT = App.get(AppKey.CAPTION_INDEX_DEFAULT);

	/** タグ属性：モデル名 */
	private String modelName;

	/**
	 * @param modelName
	 *            モデル名
	 * @param pageName
	 *            ページ名
	 * @return キャプション文字列
	 */
	public static String render(final String modelName, final String pageName) {

		String caption = INDEX_DEFAULT;

		// ページ名がindexでない場合はモデル論理名を取得
		if (!pageName.equalsIgnoreCase(EmarfServlet.PAGE_INDEX)) {
			caption = ModelUtil.getModelMei(modelName);
		}

		return "<caption>" + caption + "</caption>";
	}

	@Override
	public void release() {
		this.modelName = null;
		super.release();
	}

	@Override
	public String doStart() {
		ServletRequest request = this.pageContext.getRequest();
		return render(this.modelName, RequestUtil.getPathPageName(request));
	}

	@Override
	public String getModelName() {
		return this.modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

}
