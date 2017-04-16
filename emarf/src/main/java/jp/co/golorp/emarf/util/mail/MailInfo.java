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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * メール送受信情報<br>
 * javax.mailを直接使わせないために使用
 *
 * @author oukuf@golorp
 */
public class MailInfo {

	/** TO */
	private Map<String, String> to = new LinkedHashMap<String, String>();

	/** CC */
	private Map<String, String> cc = new LinkedHashMap<String, String>();

	/** BCC */
	private Map<String, String> bcc = new LinkedHashMap<String, String>();

	/** 件名 */
	private String subject;

	/** 本文 */
	private Map<String, Boolean> contents = new LinkedHashMap<String, Boolean>();

	/**
	 * @return TO
	 */
	public Map<String, String> getTo() {
		return to;
	}

	/**
	 * @param to
	 *            TO
	 */
	public void setTo(final Map<String, String> to) {
		this.to = to;
	}

	/**
	 * @param address
	 *            address
	 * @param personal
	 *            personal
	 */
	public void addTo(final String address, final String personal) {
		this.to.put(address, personal);
	}

	/**
	 * @return CC
	 */
	public Map<String, String> getCc() {
		return cc;
	}

	/**
	 * @param cc
	 *            CC
	 */
	public void setCc(final Map<String, String> cc) {
		this.cc = cc;
	}

	/**
	 * @param address
	 *            address
	 * @param personal
	 *            personal
	 */
	public void addCc(final String address, final String personal) {
		this.cc.put(address, personal);
	}

	/**
	 * @return BCC
	 */
	public Map<String, String> getBcc() {
		return bcc;
	}

	/**
	 * @param bcc
	 *            BCC
	 */
	public void setBcc(final Map<String, String> bcc) {
		this.bcc = bcc;
	}

	/**
	 * @param address
	 *            address
	 * @param personal
	 *            personal
	 */
	public void addBcc(final String address, final String personal) {
		this.bcc.put(address, personal);
	}

	/**
	 * @return 件名
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            件名
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return 本文
	 */
	public Map<String, Boolean> getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            本文
	 */
	public void setContents(final Map<String, Boolean> contents) {
		this.contents = contents;
	}

	/**
	 * @param text
	 *            text
	 */
	public void addText(final String text) {
		this.contents.put(text, false);
	}

	/**
	 * @param filePath
	 *            filename
	 */
	public void addFile(final String filePath) {
		this.contents.put(filePath, true);
	}

}
