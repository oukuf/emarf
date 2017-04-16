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
package jp.co.golorp.emarf.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文字コード指定フィルタ<br>
 * サーブレットフィルタ初期化パラメータから文字コードを取得する
 *
 * @author oukuf@golorp
 */
public final class EncodingFilter implements Filter {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(EncodingFilter.class);

	/** 文字コード初期化パラメータ名 */
	private static final String INIT_PARAM_ENCODING = "encoding";

	/** 文字コード */
	private String encoding;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		this.encoding = filterConfig.getInitParameter(INIT_PARAM_ENCODING);
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		LOG.trace("filter start.");

		request.setCharacterEncoding(this.encoding);
		response.setContentType("text/html; charset=" + this.encoding);
		chain.doFilter(request, response);

		LOG.trace("filter end.");
	}

	@Override
	public void destroy() {
	}

}
