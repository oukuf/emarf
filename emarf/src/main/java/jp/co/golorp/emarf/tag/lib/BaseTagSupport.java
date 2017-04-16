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
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.TableInfo;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.lib.base.model.property.value.Criterion;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * 開始タグだけのタグ用基底クラス<br>
 * （Emarfタグライブラリの根幹クラス）<br>
 * taglib.baseパッケージ内で拡張
 *
 * @author oukuf@golorp
 */
public abstract class BaseTagSupport extends TagSupport {

	/** ロガー */
	private static final Logger LOG = LoggerFactory.getLogger(BaseTagSupport.class);

	/** URLセパレータ */
	protected static final String SEP = EmarfServlet.SEP;

	/**
	 * @param htmlName
	 *            html項目名
	 * @return html項目名から「[」「]」を除去、「.」を「_」に変換してhtmlIDを取得
	 */
	protected static String toHtmlId(final String htmlName) {
		return htmlName.replaceAll("\\[|\\]", "").replaceAll("\\.", "_");
	}

	/**
	 * @param modelName
	 *            モデル名
	 * @param params
	 *            リンク引数
	 * @return data-params文字列
	 */
	protected static String toDataParam(final String modelName, final Map<String, String> params) {

		if (params != null && !params.isEmpty()) {
			StringBuilder sb = new StringBuilder(" data-params='{ ");
			for (Entry<String, String> param : params.entrySet()) {
				sb.append("\"");
				if (modelName != null) {
					sb.append(modelName).append(".");
				}
				sb.append(param.getKey()).append("\"");
				sb.append(" : ");
				sb.append("\"").append(param.getValue()).append("\", ");
			}
			return sb.deleteCharAt(sb.length() - 2).append("}'").toString();
		}

		return "";
	}

	/**
	 * @param params
	 *            リンク引数
	 * @return data-params文字列
	 */
	protected static String toDataParam(final Map<String, String> params) {
		return toDataParam(null, params);
	}

	@Override
	public int doStartTag() throws JspException {

		try {

			// modelNameプロパティの値を取得
			Field field = this.getClass().getDeclaredField("modelName");
			field.setAccessible(true);
			String modelName = (String) field.get(this);

			// Criterionタグ以外の場合
			if (!(this instanceof Criterion)) {

				// modelNameがなければ親タグから補完
				if (StringUtil.isBlank(modelName)) {
					modelName = Taglib.getParentAttribute(this, "modelName");
				}

				// modelNameがなければpathInfoから補完
				if (StringUtil.isBlank(modelName)) {
					ServletRequest request = this.pageContext.getRequest();
					String pathModelName = RequestUtil.getPathModelName(request);
					if (pathModelName != null) {
						TableInfo tableInfo = MetaData.getTableInfo(pathModelName);
						if (tableInfo != null) {
							modelName = pathModelName;
						}
					}
				}

				field.set(this, modelName);
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SystemError(e);
		} catch (NoSuchFieldException | SecurityException e) {
			LOG.trace("this is not modelable. [" + this + "]");
		}

		// 実クラスの開始タグ描画メソッドの実行結果を出力する
		this.print(this.doStart());

		// SKIP_BODY(=0)を返す
		return super.doStartTag();
	}

	@Override
	public int doEndTag() throws JspException {

		// 実クラスの終了処理を実行する
		this.release();

		// EVAL_PAGE(=6)を返す
		return super.doEndTag();
	}

	/**
	 * @return 開始タグ文字列
	 * @throws JspException
	 *             JspException
	 */
	public abstract String doStart() throws JspException;

	/**
	 * 引数の文字列を出力する
	 *
	 * @param s
	 *            出力文字列
	 * @throws JspException
	 *             JspException
	 */
	protected void print(final String s) throws JspException {
		if (s != null) {
			try {
				// PageContextで出力
				this.pageContext.getOut().print(s);
			} catch (IOException e) {
				throw new JspException(e);
			}
		}
	}

}
