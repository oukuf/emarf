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
package jp.co.golorp.emarf.tag.lib.base.model.property;

import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import jp.co.golorp.emarf.constants.AppKey;
import jp.co.golorp.emarf.generator.BeanGenerator;
import jp.co.golorp.emarf.model.Models;
import jp.co.golorp.emarf.properties.App;
import jp.co.golorp.emarf.servlet.http.EmarfServlet;
import jp.co.golorp.emarf.sql.MetaData;
import jp.co.golorp.emarf.sql.info.ColumnInfo;
import jp.co.golorp.emarf.sql.relation.RelateColumnMap;
import jp.co.golorp.emarf.sql.relation.RelateTablesMap;
import jp.co.golorp.emarf.tag.Taglib;
import jp.co.golorp.emarf.tag.interfaces.Propertiable;
import jp.co.golorp.emarf.tag.lib.BaseTagSupport;
import jp.co.golorp.emarf.util.ModelUtil;
import jp.co.golorp.emarf.util.RequestUtil;
import jp.co.golorp.emarf.util.StringUtil;

/**
 * inputタグ
 *
 * @author oukuf@golorp
 */
public class Input extends BaseTagSupport implements Propertiable {

	/**
	 * inputタグ内部で関連情報を扱うためのクラス（ロジックがMapだらけで読みにくくなったため作成）
	 *
	 * @author oukuf@golorp
	 */
	private static class RelateModel {

		/** modelName */
		private String modelName = null;

		/** relateColumns */
		private RelateColumnMap relateColumns = null;

	}

	/** 暗号化項目サイズ調整の除数 */
	private static final int CRYPT_DIVISOR = 5;

	/** 識別子名サフィックス */
	private static final String ID_NAME_SUFFIX = App.get(AppKey.INPUT_ID_NAME_SUFFIX);

	/*
	 * ************************************************** タグプロパティ
	 */

	/***/
	private String modelName;

	/***/
	private String propertyName;

	/***/
	private String type;

	/***/
	private String maxlength;

	/***/
	private String autocomplete;

	/***/
	private String readonly;

	/***/
	private String onchange;

	/*
	 * ************************************************** クラスメソッド
	 */

	/**
	 * @param htmlName
	 *            htmlName
	 * @param value
	 *            value
	 * @param readonly
	 *            readonly
	 * @param pageContext
	 *            pageContext
	 * @param type
	 *            type
	 * @param maxlength
	 *            maxlength
	 * @param autocomplete
	 *            autocomplete
	 * @param onchange
	 *            onchange
	 * @return タグ文字列
	 */
	public static final String render(final String htmlName, final Object value, final Boolean readonly,
			final PageContext pageContext, final String type, final String maxlength, final String autocomplete,
			final String onchange) {

		StringBuilder sb = new StringBuilder("<input");

		if (StringUtil.isNotBlank(type)) {
			sb.append(" type=\"").append(type).append("\"");
		} else {
			sb.append(" type=\"text\"");
		}

		sb.append(" id=\"").append(toHtmlId(htmlName)).append("\" name=\"").append(htmlName).append("\"");

		if (StringUtil.isNotBlank(value)) {

			String s = value.toString();

			// valueが配列なら「,」区切りで取得
			if (value instanceof String[]) {
				s = StringUtil.join(((String[]) value), ",");
			}

			sb.append(" value=\"").append(s).append("\"");

			// 「The specified value "2017/02/04" does not conform to the
			// required format, "yyyy-MM-dd".」の対応
			// datepicker.jsでtype=dateをtype=textにしてからvalueに書き戻す
			sb.append(" data-value=\"").append(s).append("\"");
		}

		// html項目名を「[数字]」か「.」で分割して、１つ目をモデル名、最後をプロパティ名として取得
		String[] htmlNames = htmlName.split("\\[\\d+\\]|\\.");
		String modelName = StringUtil.getFirst(htmlNames);
		String propertyName = StringUtil.getLast(htmlNames);

		// versionNoに該当するなら「class=versionNo」を設定
		ColumnInfo columnInfo = MetaData.getColumnInfo(modelName, propertyName);
		if (columnInfo != null && columnInfo.getColumnName().endsWith(BeanGenerator.VERSION_NO)) {
			sb.append(" class=\"versionNo\"");
		}

		// hidden の場合は終了
		if (StringUtil.equals(type, "hidden")) {
			return sb.append(" />").toString();
		}

		if (StringUtil.isNotBlank(autocomplete)) {
			sb.append(" autocomplete=\"").append(autocomplete).append("\"");
		}

		if (readonly != null && readonly) {
			sb.append(" readonly");
		}

		if (onchange != null) {
			sb.append(" onchange=\"").append(onchange).append("\"");
		}

		// maxlength指定があれば、maxlengthとsizeを設定
		if (StringUtil.isNotBlank(maxlength)) {
			String size = maxlength;
			String cryptSuffix = StringUtil.toUpperCamelCase(BeanGenerator.CRYPT_SUFFIX);
			// 暗号化項目はデータ長が長くなるので、html項目のsizeとしては短くする TODO 単純に割るのはどうかと思う
			if (propertyName != null && propertyName.endsWith(cryptSuffix)) {
				size = String.valueOf(Integer.valueOf(maxlength) / CRYPT_DIVISOR);
			}
			sb.append(" size=\"").append(size).append("\"").append(" maxlength=\"").append(size).append("\"");
		}

		// 読み取り専用 の場合は終了
		if (readonly != null && readonly) {
			return sb.append(" />").toString();
		}

		// 関連情報がない場合は終了
		RelateTablesMap relateTables = getRelateTables(modelName);
		if (relateTables.isEmpty()) {
			return sb.append(" />").toString();
		}

		/*
		 * 関連情報がある場合は関連プロパティに合致するなら選択リンクを出力
		 */

		ServletRequest request = pageContext.getRequest();

		// 検索フォームでないなら関連モデルの外部キー判定用プロパティ名（"ID名称（～Nm）"なら一旦"ID名（～Id）"にする）
		String propertyName4Id = propertyName;
		String pathModelName = RequestUtil.getPathModelName(request);
		String pathPageName = RequestUtil.getPathPageName(request);
		boolean isReferForm = pathModelName.equals(modelName) && pathPageName.equals(EmarfServlet.PAGE_INDEX)
				&& !htmlName.matches("^" + modelName + "\\[\\d+\\]\\..+");
		if (!isReferForm) {
			propertyName4Id = propertyName.replaceFirst(ID_NAME_SUFFIX + "$", Models.ID_SUFFIX);
		}

		/*
		 * 最も合致数の多い関連カラム情報を取得
		 */

		RelateModel relateModel = getRelateModel(relateTables, propertyName4Id, isReferForm);

		boolean isReadonly = false;
		StringBuilder sb2 = new StringBuilder();

		if (relateModel.relateColumns != null) {
			// 関連モデルがあった場合

			for (Entry<String, String> columns : relateModel.relateColumns.entrySet()) {
				String columnName = columns.getKey();
				String columnName2 = columns.getValue();

				if (propertyName4Id.equals(StringUtil.toCamelCase(columnName))) {
					// 今回のプロパティ名が外部キーに合致する場合

					// 読み取り専用に設定
					isReadonly = true;

					// もともとのプロパティ名がID名なら選択リンクを出力
					if (propertyName4Id.equals(propertyName)) {
						String pickPrefix = htmlName.replaceAll("(?i)" + columnName2 + "$", "");
						String contextServletPath = RequestUtil.getContextServletPath(request);
						String url = contextServletPath + SEP + relateModel.modelName + SEP + EmarfServlet.PAGE_PICK
								+ SEP;
						sb2.append("<a name=\"").append(pickPrefix)
								.append("\" href=\"javascript:void(0);\" onclick=\"" + EmarfServlet.PAGE_PICK
										+ ".show('")
								.append(url).append("', this.name)\" class=\"" + EmarfServlet.PAGE_PICK + "\">選択</a>");
						break;
					}
				}
			}
		}

		if (isReadonly) {
			sb.append(" readonly");
		}

		sb.append(" />");

		if (sb2.length() > 0) {
			sb.append(sb2);
		}

		return sb.toString();
	}

	/**
	 * @param relateTables
	 *            relateTables
	 * @param propertyName
	 *            propertyName
	 * @param isReferForm
	 *            isReferForm
	 * @return RelateModel
	 */
	private static RelateModel getRelateModel(final RelateTablesMap relateTables, final String propertyName,
			final boolean isReferForm) {

		int numRelateColumns = 0;

		RelateModel relateModel = new RelateModel();

		// String thisRelateModelName = null;
		// RelateColumns thisRelateColumns = null;

		// 関連テーブル情報「関連モデル名：関連カラム情報のリスト」でループ
		// 同一マスタに複数の参照がありうるためリストになっている
		for (Entry<String, List<RelateColumnMap>> relateTable : relateTables.entrySet()) {
			String relateModelName = relateTable.getKey();
			List<RelateColumnMap> relateColumnsList = relateTable.getValue();

			// 関連カラム情報リストでループ（複数存在しうるうち一つの関連カラム情報を処理する）
			for (RelateColumnMap relateColumns : relateColumnsList) {
				if (!relateColumns.containsKey(propertyName)) {
					continue;
				}
				// 検索フォームなら最小合致数の、検索フォーム以外なら最大合致数の関連情報を、全てのモデルをまたいで退避していく
				if ((isReferForm && (numRelateColumns == 0 || numRelateColumns > relateColumns.size()))
						|| (!isReferForm && numRelateColumns < relateColumns.size())) {
					numRelateColumns = relateColumns.size();
					relateModel.modelName = relateModelName;
					relateModel.relateColumns = relateColumns;
				}
			}
		}

		return relateModel;
	}

	/**
	 * @param modelName
	 *            modelName
	 * @return 参照モデル・集約モデル
	 */
	private static RelateTablesMap getRelateTables(final String modelName) {

		RelateTablesMap relateTables = new RelateTablesMap();

		// 参照モデルを取得
		RelateTablesMap referTos = ModelUtil.getReferTos(modelName);
		if (referTos != null) {
			relateTables.putAll(referTos);
		}

		// 集約モデルを取得
		RelateTablesMap summaryOfs = ModelUtil.getSummaryOfTablesMap(modelName);
		if (summaryOfs != null) {
			relateTables.putAll(summaryOfs);
		}

		return relateTables;
	}

	/*
	 * ************************************************** インスタンスメソッド
	 */

	@Override
	public void release() {
		this.modelName = null;
		this.propertyName = null;
		this.type = null;
		this.maxlength = null;
		this.autocomplete = null;
		this.readonly = null;
		this.onchange = null;
		super.release();
	}

	@Override
	public String doStart() throws JspException {

		ServletRequest request = this.pageContext.getRequest();

		String htmlName = Taglib.getHtmlName(this);

		Object value = RequestUtil.lookup(request, this.modelName, this.propertyName, htmlName);

		boolean readonly = StringUtil.is(this.readonly);
		if (!readonly) {
			readonly = Taglib.isReadonly(request, this.modelName, this.propertyName);
		}

		if (this.maxlength == null) {
			ColumnInfo columnInfo = MetaData.getColumnInfo(modelName, propertyName);
			if (columnInfo != null) {
				this.maxlength = String.valueOf(columnInfo.getColumnSize());
			}
		}

		return Input.render(htmlName, value, readonly, this.pageContext, this.type, this.maxlength, this.autocomplete,
				this.onchange);
	}

	/*
	 * ************************************************** アクセサ
	 */

	@Override
	public String getModelName() {
		return modelName;
	}

	@Override
	public void setModelName(final String modelName) {
		this.modelName = modelName;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * typeを取得します。
	 *
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * typeを設定します。
	 *
	 * @param type
	 *            type
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * maxlengthを取得します。
	 *
	 * @return maxlength
	 */
	public String getMaxlength() {
		return maxlength;
	}

	/**
	 * maxlengthを設定します。
	 *
	 * @param maxlength
	 *            maxlength
	 */
	public void setMaxlength(final String maxlength) {
		this.maxlength = maxlength;
	}

	/**
	 * autocompleteを取得します。
	 *
	 * @return autocomplete
	 */
	public String getAutocomplete() {
		return autocomplete;
	}

	/**
	 * autocompleteを設定します。
	 *
	 * @param autocomplete
	 *            autocomplete
	 */
	public void setAutocomplete(final String autocomplete) {
		this.autocomplete = autocomplete;
	}

	/**
	 * readonlyを取得します。
	 *
	 * @return readonly
	 */
	public String getReadonly() {
		return readonly;
	}

	/**
	 * readonlyを設定します。
	 *
	 * @param readonly
	 *            readonly
	 */
	public void setReadonly(final String readonly) {
		this.readonly = readonly;
	}

	/**
	 * onchangeを取得します。
	 *
	 * @return onchange
	 */
	public String getOnchange() {
		return onchange;
	}

	/**
	 * onchangeを設定します。
	 *
	 * @param onchange
	 *            onchange
	 */
	public void setOnchange(final String onchange) {
		this.onchange = onchange;
	}

}
