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
package jp.co.golorp.emarf.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;

/**
 * 暗号化ユーティリティ
 *
 * @author oukuf@golorp
 */
public final class CryptUtil {

	/***/
	private static final Logger LOG = LoggerFactory.getLogger(CryptUtil.class);

	/** CryptUtil.properties */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(CryptUtil.class.getSimpleName());

	/** 秘密鍵（16文字） */
	private static final byte[] SECRET_KEY = BUNDLE.getString("secret_key").getBytes(); //$NON-NLS-1$

	/** 暗号化方式 */
	private static final String ALGORITHM = BUNDLE.getString("algorithm"); //$NON-NLS-1$

	/**
	 * コンストラクタ
	 */
	private CryptUtil() {
	}

	/**
	 * 文字列を16文字の秘密鍵でAES暗号化してBase64した文字列で返す
	 *
	 * @param string
	 *            対象文字列
	 * @return 暗号化文字列
	 */
	public static String encrypt(final String string) {

		if (string == null) {
			return null;
		}

		byte[] input = string.getBytes();

		byte[] encryped = cipher(Cipher.ENCRYPT_MODE, input);

		byte[] encoded = Base64.encodeBase64(encryped, false);

		String ret = new String(encoded);

		LOG.debug("Encrypt [" + string + "] to [" + ret + "].");

		return ret;
	}

	/**
	 * Base64されたAES暗号化文字列を元の文字列に復元する
	 *
	 * @param encryped
	 *            暗号化文字列
	 * @return 複合化文字列
	 */
	public static String decrypt(final String encryped) {

		if (encryped == null) {
			return null;
		}

		byte[] input = Base64.decodeBase64(encryped);

		byte[] decrypted = cipher(Cipher.DECRYPT_MODE, input);

		String ret = new String(decrypted);

		LOG.debug("Decrypt [" + encryped + "] to [" + ret + "].");

		return ret;
	}

	/**
	 * 暗号化・複合化の共通部分
	 *
	 * @param opmode
	 *            opmode
	 * @param input
	 *            input
	 * @return byte[]
	 */
	private static byte[] cipher(final int opmode, final byte[] input) {

		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new SystemError(e);
		}

		try {
			cipher.init(opmode, new SecretKeySpec(SECRET_KEY, ALGORITHM));
		} catch (InvalidKeyException e) {
			throw new SystemError(e);
		}

		byte[] bytes = null;
		try {
			LOG.trace(String.valueOf(input));
			bytes = cipher.doFinal(input);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new SystemError(e);
		}

		return bytes;
	}

}