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
package jp.co.golorp.emarf.constants.model;

/**
 * 入力チェックのモード
 *
 * @author oukuf@golorp
 */
public final class Crud {

	/** 登録処理 */
	public static final int CREATE = 1;

	/** 照会処理 */
	public static final int REFER = 2;

	/** 更新処理 */
	public static final int UPDATE = 4;

	/** 削除処理 */
	public static final int DELETE = 8;

	/**
	 * デフォルトコンストラクタ
	 */
	private Crud() {
	}

	/**
	 * @param crud
	 *            crud
	 * @return boolean
	 */
	public static boolean isCreate(final int crud) {
		int i = 0;
		if (crud > 0) {
			i = (crud / CREATE) % 2;
		}
		return i == 1;
	}

	/**
	 * @param crud
	 *            crud
	 * @return boolean
	 */
	public static boolean isRefer(final int crud) {
		int i = 0;
		if (crud > 0) {
			i = (crud / REFER) % 2;
		}
		return i == 1;
	}

	/**
	 * @param crud
	 *            crud
	 * @return boolean
	 */
	public static boolean isUpdate(final int crud) {
		int i = 0;
		if (crud > 0) {
			i = (crud / UPDATE) % 2;
		}
		return i == 1;
	}

	/**
	 * @param crud
	 *            crud
	 * @return boolean
	 */
	public static boolean isDelete(final int crud) {
		int i = 0;
		if (crud > 0) {
			i = (crud / DELETE) % 2;
		}
		return i == 1;
	}

};
