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
package jp.co.golorp.emarf.util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * パスワード認証
 *
 * @author oukuf@golorp
 */
public class PasswordAuthenticator extends Authenticator {

	/***/
	private String userName;

	/***/
	private String password;

	/**
	 * @param userName
	 *            userName
	 * @param password
	 *            password
	 */
	public PasswordAuthenticator(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(this.userName, this.password);
	}

}
