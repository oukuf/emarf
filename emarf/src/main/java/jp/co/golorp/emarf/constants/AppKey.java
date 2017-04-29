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
package jp.co.golorp.emarf.constants;

/**
 * App.propertiesのキー文字列管理クラス
 *
 * @author oukuf@golorp
 */
public abstract class AppKey {

	/*
	 * BeanGenerator
	 */

	/** BeanGenerator.GENERATE */
	public static final String BEANGENERATOR_GENERATE = "BeanGenerator.generate";

	/** BeanGenerator.CLASSES */
	public static final String BEANGENERATOR_CLASSES = "BeanGenerator.classes";

	/** BeanGenerator.PACKAGE */
	public static final String BEANGENERATOR_PACKAGE = "BeanGenerator.package";

	/** BeanGenerator.HISTORY_SEQ_SUFFIX */
	public static final String BEANGENERATOR_HISTORY_SUFFIX = "BeanGenerator.history.suffix";

	/** BeanGenerator.CRYPT_SUFFIX */
	public static final String BEANGENERATOR_CRYPT_SUFFIX = "BeanGenerator.crypt.suffix";

	/** BeanGenerator.VERSION_NO */
	public static final String BEANGENERATOR_VERSIONNO = "BeanGenerator.versionNo";

	/** BeanGenerator.DELETE_F */
	public static final String BEANGENERATOR_DELETE_F = "BeanGenerator.deleteF";

	/** インライン利用 */
	public static final String BEANGENERATOR_TYPE_PREFIX = "BeanGenerator.type.";

	/*
	 * Models
	 */

	/** Models.MAX_ROW */
	public static final String MODELS_ROW_MAX = "Models.row.max";

	/** Models.ID_SUFFIX */
	public static final String MODELS_ID_SUFFIX = "Models.id.suffix";

	/** Models.MAX_SEQ_SUFFIX_SET */
	public static final String MODELS_SEQ_SUFFIX = "Models.seq.suffix";

	/** Models.ORACLE_SEQUENCE_PREFIX */
	public static final String MODELS_ORACLE_SEQUENCE_PREFIX = "Models.oracle_sequence.prefix";

	/** Models.ORACLE_SEQUENCE_SUFFIX */
	public static final String MODELS_ORACLE_SEQUENCE_SUFFIX = "Models.oracle_sequence.suffix";

	/** Models.AINT_INSERTS */
	public static final String MODELS_AINT_INSERTS = "Models.aint.inserts";

	/** Models.AINT_UPDATES */
	public static final String MODELS_AINT_UPDATES = "Models.aint.updates";

	/** Models.AUTO_INSERT_MAP */
	public static final String MODELS_AUTO_INSERT_VALUES = "Models.auto.insert.values";

	/** Models.AUTO_UPDATE_MAP */
	public static final String MODELS_AUTO_UPDATE_VALUES = "Models.auto.update.values";

	/** インライン利用 */
	public static final String MODELS_FK_PREFIX = "Models.fk.";

	/*
	 * Taglib
	 */

	/** インライン利用 */
	public static final String TAGUTIL_GAMEN_PREFIX = "Taglib.gamen.";

	/** インライン利用 */
	public static final String TAGUTIL_PAGE_PREFIX = "Taglib.page.";

	/** Taglib.FOOTER */
	public static final String TAGUTIL_FOOTER = "Taglib.footer";

	/** Taglib.DUMP */
	public static final String TAGUTIL_DUMP = "Taglib.dump";

	/*
	 * CriteriaTagSupport
	 */

	/** CriteriaTagSupport.OPTION_MODEL_DEFAULT */
	public static final String CRITERIA_OPTION_MODEL_DEFAULT = "Criteria.option_model.default";

	/** CriteriaTagSupport.OPTION_VALUE_DEFAULT */
	public static final String CRITERIA_OPTION_VALUE_DEFAULT = "Criteria.option_value.default";

	/** CriteriaTagSupport.OPTION_LABEL_DEFAULT */
	public static final String CRITERIA_OPTION_LABEL_DEFAULT = "Criteria.option_label.default";

	/*
	 * Criterion
	 */

	/** Criterion.MODEL_DEFAULT */
	public static final String CRITERION_MODEL_DEFAULT = "Criterion.model.default";

	/** Criterion.PROPERTY_DEFAULT */
	public static final String CRITERION_PROPERTY_DEFAULT = "Criterion.property.default";

	/** Criterion.VALUE_DEFAULT */
	public static final String CRITERION_VALUE_DEFAULT = "Criterion.value.default";

	/*
	 * Title
	 */

	/** Title.NAME */
	public static final String TITLE_NAME = "Title.name";

	/*
	 * H1
	 */

	/** H1.NAME */
	public static final String H1_NAME = "H1.name";

	/*
	 * Style
	 */

	/** インライン利用 */
	public static final String STYLE_MEDIA_PC_MIN_WIDTH = "Style.media.pc.min.width";

	/** Style.PC_KEYWORD */
	public static final String STYLE_PC_KEYWORD = "Style.pc.keyword";

	/** Style.SP_KEYWORD */
	public static final String STYLE_SP_KEYWORD = "Style.sp.keyword";

	/*
	 * Errors
	 */

	/** Errors.PREFIX */
	public static final String ERRORS_PREFIX = "Errors.prefix";

	/** Errors.MESSAGE_PREFIX */
	public static final String ERRORS_MESSAGE_PREFIX = "Errors.message.prefix";

	/** Errors.MESSAGE_SUFFIX */
	public static final String ERRORS_MESSAGE_SUFFIX = "Errors.message.suffix";

	/** Errors.SUFFIX */
	public static final String ERRORS_SUFFIX = "Errors.suffix";

	/*
	 * Warns
	 */

	/** Warns.PREFIX */
	public static final String WARNS_PREFIX = "Warns.prefix";

	/** Warns.MESSAGE_PREFIX */
	public static final String WARNS_MESSAGE_PREFIX = "Warns.message.prefix";

	/** Warns.MESSAGE_SUFFIX */
	public static final String WARNS_MESSAGE_SUFFIX = "Warns.message.suffix";

	/** Warns.SUFFIX */
	public static final String WARNS_SUFFIX = "Warns.suffix";

	/*
	 * Infos
	 */

	/** Infos.PREFIX */
	public static final String INFOS_PREFIX = "Infos.prefix";

	/** Infos.MESSAGE_PREFIX */
	public static final String INFOS_MESSAGE_PREFIX = "Infos.message.prefix";

	/** Infos.MESSAGE_SUFFIX */
	public static final String INFOS_MESSAGE_SUFFIX = "Infos.message.suffix";

	/** Infos.SUFFIX */
	public static final String INFOS_SUFFIX = "Infos.suffix";

	/*
	 * Headline
	 */

	/** Headline.HIDE_MODEL_SET */
	public static final String HEADLINE_HIDE_MODELS = "Headline.hide.models";

	/*
	 * Legend
	 */

	/** Legend.INDEX_DEFAULT */
	public static final String LEGEND_INDEX_DEFAULT = "Legend.index.default";

	/*
	 * Label
	 */

	/** Label.NOTNULL_MARK */
	public static final String LABEL_NOTNULL_MARK = "Label.notnull.mark";

	/*
	 * Fieldset
	 */

	/** Fieldset.AINT_SELECT_SUFFIX_SET */
	public static final String FIELDSET_AINT_SELECT_SUFFIXS = "Fieldset.aint.select.suffixs";

	/** Fieldset.CANT_INSERT_SUFFIX_SET */
	public static final String FIELDSET_CANT_INSERT_SUFFIXS = "Fieldset.cant.insert.suffixs";

	/** Fieldset.CANT_UPDATE_SUFFIX_SET */
	public static final String FIELDSET_CANT_UPDATE_SUFFIXS = "Fieldset.cant.update.suffixs";

	/** Fieldset.CHECKS_SUFFIX_SET */
	public static final String FIELDSET_CHECKS_SUFFIXS = "Fieldset.checks.suffixs";

	/** Fieldset.DATE_SUFFIX_SET */
	public static final String FIELDSET_DATE_SUFFIXS = "Fieldset.date.suffixs";

	/** Fieldset.DATETIME_SUFFIX_SET */
	public static final String FIELDSET_DATETIME_SUFFIXS = "Fieldset.datetime.suffixs";

	/** Fieldset.PASSWORD_SUFFIX_SET */
	public static final String FIELDSET_PASSWORD_SUFFIXS = "Fieldset.password.suffixs";

	/** Fieldset.RADIOS_SUFFIX_SET */
	public static final String FIELDSET_RADIOS_SUFFIXS = "Fieldset.radios.suffixs";

	/** Fieldset.SELECT_SUFFIX_SET */
	public static final String FIELDSET_SELECT_SUFFIXS = "Fieldset.select.suffixs";

	/** Fieldset.TEXTAREA_SUFFIX_SET */
	public static final String FIELDSET_TEXTAREA_SUFFIXS = "Fieldset.textarea.suffixs";

	/** Fieldset.TIME_SUFFIX_SET */
	public static final String FIELDSET_TIME_SUFFIXS = "Fieldset.time.suffixs";

	/** Fieldset.IMG_SUFFIX_SET */
	public static final String FIELDSET_IMG_SUFFIXS = "Fieldset.img.suffixs";

	/** Fieldset.PART_SUFFIX_SET */
	public static final String FIELDSET_CRITERIA_PART_SUFFIXS = "Fieldset.criteria.part.suffixs";

	/** Fieldset.RANGE_SUFFIX_SET */
	public static final String FIELDSET_CRITERIA_RANGE_SUFFIXS = "Fieldset.criteria.range.suffixs";

	/*
	 * Input
	 */

	/** Input.ID_NAME_SUFFIX */
	public static final String INPUT_ID_NAME_SUFFIX = "Input.id.name.suffix";

	/*
	 * Checks
	 */

	/** Checks.NOCHECK_VALUE */
	public static final String CHECKS_NOCHECK_VALUE = "Checks.nocheck.value";

	/*
	 * Select
	 */

	/** Select.OPTGROUP_SEP */
	public static final String SELECT_OPTGROUP_SEP = "Select.optgroup.sep";

	/*
	 * Span
	 */

	/** Span.ID_NAME_SUFFIXS */
	public static final String SPAN_ID_NAME_SUFFIXS = "Span.id.name.suffixs";

	/** Span.XSS_SANITIZE_MAP */
	public static final String SPAN_XSS_SANITIZE_VALUES = "Span.xss.sanitize.values";

	/*
	 * Tables
	 */

	/** Tables.HIDE_MODEL_SET */
	public static final String TABLES_HIDE_MODELS = "Tables.hide.models";

	/*
	 * Table
	 */

	/** Table.DEFAULT_PAGING_ROWS */
	public static final String TABLE_PAGING_ROWS_DEFAULT = "Table.paging.rows.default";

	/*
	 * Caption
	 */

	/** Caption.INDEX_DEFAULT */
	public static final String CAPTION_INDEX_DEFAULT = "Caption.index.default";

	/*
	 * Ths
	 */

	/** Ths.FIRST_DEFAULT */
	public static final String THS_FIRST_DEFAULT = "Ths.first.default";

}
