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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.golorp.emarf.tag.lib.BaseTagSupport;

/**
 * メッセージタグ
 *
 * @author oukuf@golorp
 */
public abstract class Messages extends BaseTagSupport implements MessagesInterface {

	@Override
	public String doStart() {

		Map<String, String[]> messages = null;

		/*
		 * pageスコープのメッセージを捕捉
		 */

		messages = getPageContextMessage();

		/*
		 * sessionスコープのメッセージを捕捉
		 */

		String attributeKey = this.getAttributeKey();

		ServletRequest request = pageContext.getRequest();
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();

		Object sesObject = session.getAttribute(attributeKey);

		if (sesObject != null) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> map = (Map<String, String[]>) sesObject;

			if (messages == null) {
				messages = new LinkedHashMap<String, String[]>();
			}

			for (Entry<String, String[]> entry : map.entrySet()) {

				String message = entry.getKey();
				String[] items = entry.getValue();

				if (messages.containsKey(message)) {

					List<String> orgItems = new ArrayList<String>(Arrays.asList(messages.get(message)));

					for (String item : items) {
						orgItems.add(item);
					}

					messages.put(message, orgItems.toArray(new String[orgItems.size()]));

				} else {

					messages.put(message, items);
				}
			}

			// セッションからメッセージを削除
			session.removeAttribute(attributeKey);
		}

		/*
		 * requestスコープのメッセージを捕捉
		 */

		Object reqObject = request.getAttribute(attributeKey);

		if (reqObject != null) {

			@SuppressWarnings("unchecked")
			Map<String, String[]> map = (Map<String, String[]>) reqObject;

			if (messages == null) {
				messages = new LinkedHashMap<String, String[]>();
			}

			for (Entry<String, String[]> entry : map.entrySet()) {

				String message = entry.getKey();
				String[] items = entry.getValue();

				if (messages.containsKey(message)) {

					List<String> orgItems = new ArrayList<String>(Arrays.asList(messages.get(message)));

					for (String item : items) {
						orgItems.add(item);
					}

					messages.put(message, orgItems.toArray(new String[orgItems.size()]));

				} else {

					messages.put(message, items);
				}
			}
		}

		// セッションからリクエストに詰め替える（メッセージより後ろに書かれたタグで、当該カテゴリのメッセージがあったことを知るため）
		if (reqObject == null && sesObject != null) {
			request.setAttribute(attributeKey, sesObject);
		}

		if (messages == null || messages.size() == 0) {
			return null;
		}

		/*
		 * タグ文字列生成
		 */

		StringBuilder sb = new StringBuilder();

		sb.append(getPrefix());

		for (Entry<String, String[]> message : messages.entrySet()) {

			String msg = message.getKey();
			String[] items = message.getValue();

			sb.append(getMsgPrefix()).append(msg.replaceAll("<", "&lt;").replaceAll("\n", "<br>"))
					.append(getMsgSuffix());

			if (items == null) {
				continue;
			}

			for (String item : items) {
				sb.append("<script>$(function() {$('[name=\"").append(item).append("\"]').attr('title', '").append(msg)
						.append("').addClass('").append(getCssClassName()).append("');});</script>");
			}
		}

		sb.append(getSuffix());

		return sb.toString();
	}

	@Override
	public Map<String, String[]> getPageContextMessage() {
		return null;
	}

	/**
	 * セッションスコープにメッセージを追加する
	 *
	 * @param request
	 *            リクエスト
	 * @param message
	 *            メッセージ文字列
	 * @param items
	 *            メッセージの該当する画面項目html名
	 * @param attributeKey
	 *            セッション変数キー文字列
	 */
	protected static void addMessage(final ServletRequest request, final String message, final String[] items,
			final String attributeKey) {

		HttpSession session = ((HttpServletRequest) request).getSession();

		Map<String, String[]> messages = null;

		Object o = session.getAttribute(attributeKey);
		if (o != null) {
			@SuppressWarnings("unchecked")
			Map<String, String[]> map = (Map<String, String[]>) o;
			messages = map;
		} else {
			messages = new LinkedHashMap<String, String[]>();
		}

		messages.put(message, items);

		session.setAttribute(attributeKey, messages);
	}

}
