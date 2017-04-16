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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.golorp.emarf.exception.SystemError;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.util.mail.MailInfo;
import jp.co.golorp.emarf.util.mail.PasswordAuthenticator;

/**
 * Mailユーティリティ
 *
 * @author oukuf@golorp
 */
public final class MailUtil {

	/** LOG */
	private static final Logger LOG = LoggerFactory.getLogger(MailUtil.class);

	/** URLセパレータ */
	private static final String SEP = EmarfServlet.SEP;

	/** リソースバンドル */
	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(MailUtil.class.getSimpleName());

	/** 文字コード */
	private static final String CHARSET = BUNDLE.getString("charset"); //$NON-NLS-1$

	/** 送信者アドレス */
	private static final String USERNAME = BUNDLE.getString("username"); //$NON-NLS-1$

	/** 送信者名 */
	private static final String PERSONAL = BUNDLE.getString("personal"); //$NON-NLS-1$

	/** パスワード */
	private static final String PASSWORD = BUNDLE.getString("password"); //$NON-NLS-1$

	/** 受信プロトコル */
	private static final String STORE = BUNDLE.getString("store"); //$NON-NLS-1$

	/** 受信サーバのフォルダ名 */
	private static final String FOLDER = BUNDLE.getString("folder"); //$NON-NLS-1$

	/***/
	private static final String SEND_PROP_SUFFIX = ".send.properties"; //$NON-NLS-1$

	/** 送信プロパティファイル名 */
	private static final String SEND_PROP_NAME = MailUtil.class.getSimpleName() + SEND_PROP_SUFFIX;

	/** メール送信用プロパティ */
	private static final Properties SEND_PROPERTIES = new Properties();
	static {
		try {
			SEND_PROPERTIES.load(MailUtil.class.getResourceAsStream(SEP + SEND_PROP_NAME));
			SEND_PROPERTIES.put("mail.smtp.debug", LOG.isDebugEnabled()); //$NON-NLS-1$
		} catch (IOException e) {
			throw new SystemError(e);
		}
	}

	/***/
	private static final String RECV_PROP_SUFFIX = ".recv.properties"; //$NON-NLS-1$

	/** 受信プロパティファイル名 */
	private static final String RECV_PROP_NAME = MailUtil.class.getSimpleName() + RECV_PROP_SUFFIX;

	/** メール受信用プロパティ */
	private static final Properties RECV_PROPERTIES = new Properties();
	static {
		try {
			RECV_PROPERTIES.load(MailUtil.class.getResourceAsStream(SEP + RECV_PROP_NAME));
		} catch (IOException e) {
			throw new SystemError(e);
		}
	}

	/**
	 * コンストラクタ
	 */
	private MailUtil() {
	}

	/**
	 * メール送信
	 *
	 * @param mi
	 *            MailInfo
	 */
	public static void send(final MailInfo mi) {

		Session session = Session.getInstance(SEND_PROPERTIES, getAuthenticator());
		session.setDebug(LOG.isDebugEnabled());

		try {

			/*
			 * MailInfoをjavax.mailの部品にコピー
			 */

			MimeMessage m = new MimeMessage(session);

			// 送信者
			m.setFrom(new InternetAddress(MailUtil.USERNAME, MailUtil.PERSONAL));

			// 送信先
			m.setRecipients(RecipientType.TO, toArrayAddress(mi.getTo()));

			// CCアドレス
			if (mi.getCc() != null) {
				m.setRecipients(RecipientType.CC, toArrayAddress(mi.getCc()));
			}

			// BCCアドレス
			if (mi.getBcc() != null) {
				m.setRecipients(RecipientType.BCC, toArrayAddress(mi.getBcc()));
			}

			// 送信日
			m.setSentDate(DateUtil.getDate());

			// タイトル
			m.setSubject(mi.getSubject(), CHARSET);

			// 内容・添付ファイル
			Multipart mp = new MimeMultipart();
			for (Entry<String, Boolean> content : mi.getContents().entrySet()) {
				String s = content.getKey();
				Boolean isFile = content.getValue();
				if (isFile) {
					mp.addBodyPart(getFileMimeBodyPart(s));
				} else {
					mp.addBodyPart(getTextMimeBodyPart(s));
				}
			}
			m.setContent(mp);

			// メール送信
			Transport.send(m);

		} catch (MessagingException | UnsupportedEncodingException e) {
			LOG.warn(e.getMessage());
		}
	}

	/**
	 * メール受信（調整中）
	 */
	public static void recv() {

		Session session = Session.getInstance(RECV_PROPERTIES, getAuthenticator());
		session.setDebug(LOG.isDebugEnabled());

		try {

			Store store = session.getStore(STORE);
			store.connect();

			Folder folder = store.getFolder(FOLDER);
			folder.open(Folder.READ_ONLY);

			Message[] msgs = folder.getMessages();
			for (Message msg : msgs) {
				System.out.println("---------------------------------"); //$NON-NLS-1$
				System.out.println("Email Number " + msg.getMessageNumber()); //$NON-NLS-1$
				System.out.println("Subject: " + msg.getSubject()); //$NON-NLS-1$
				System.out.println("From: " + msg.getFrom()[0]); //$NON-NLS-1$
				System.out.println("Text: " + msg.getContent().toString()); //$NON-NLS-1$
				System.out.println("Date: " + msg.getSentDate()); //$NON-NLS-1$
			}

		} catch (MessagingException | IOException e) {
			LOG.warn(e.getMessage());
		}
	}

	/**
	 * @return Authenticator
	 */
	private static Authenticator getAuthenticator() {
		return new PasswordAuthenticator(MailUtil.USERNAME, MailUtil.PASSWORD);
	}

	/**
	 * @param addressList
	 *            sendtos
	 * @return メールアドレス：名前のMapを、InternetAddress[]に変換
	 */
	private static InternetAddress[] toArrayAddress(final Map<String, String> addressList) {

		InternetAddress[] addresses = new InternetAddress[addressList.size()];

		int i = 0;

		for (Entry<String, String> sendto : addressList.entrySet()) {

			String address = sendto.getKey();
			String personal = sendto.getValue();

			try {
				addresses[i] = new InternetAddress(address, personal);
			} catch (UnsupportedEncodingException e) {
				LOG.warn(e.getMessage());
			}
			i++;
		}

		return addresses;
	}

	/**
	 * @param filePath
	 *            ファイルパス
	 * @return 添付ファイルを設定したMimeBodyPart
	 */
	private static MimeBodyPart getFileMimeBodyPart(final String filePath) {

		MimeBodyPart mbp = new MimeBodyPart();

		FileDataSource fds = new FileDataSource(filePath);

		try {

			mbp.setDataHandler(new DataHandler(fds));

			mbp.setFileName(MimeUtility.encodeWord(fds.getName()));

		} catch (MessagingException | UnsupportedEncodingException e) {
			throw new SystemError(e);
		}

		return mbp;
	}

	/**
	 * @param text
	 *            text
	 * @return 本文を設定したMimeBodyPart
	 */
	private static MimeBodyPart getTextMimeBodyPart(final String text) {

		MimeBodyPart mbp = new MimeBodyPart();

		try {
			mbp.setText(text, CHARSET);
		} catch (MessagingException e) {
			throw new SystemError(e);
		}

		return mbp;
	}

}
