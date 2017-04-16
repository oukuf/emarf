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
package jp.co.golorp.emarf.sql.info;

/**
 * カラム情報
 *
 * @author oukuf@golorp
 */
public class ColumnInfo {

	/** カラム名 */
	private String columnMei;

	/** プロパティ名 */
	private String propertyName;

	/**
	 * @return columnMei
	 */
	public final String getColumnMei() {
		return columnMei;
	}

	/**
	 * @param columnMei
	 *            セットする columnMei
	 */
	public final void setColumnMei(final String columnMei) {
		this.columnMei = columnMei;
	}

	/**
	 * @return プロパティ名
	 */
	public final String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            プロパティ名
	 */
	public final void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}

	/** TABLE_CAT */
	private String tableCat;
	/** TABLE_SCHEM */
	private String tableSchem;
	/** TABLE_NAME */
	private String tableName;
	/** COLUMN_NAME */
	private String columnName;
	/** DATA_TYPE */
	private Integer dataType;
	/** TYPE_NAME */
	private String typeName;
	/** COLUMN_SIZE */
	private Integer columnSize;
	/** BUFFER_LENGTH */
	private Integer bufferLength;
	/** DECIMAL_DIGITS */
	private Integer decimalDigits;
	/** NUM_PREC_RADIX */
	private Integer numPrecRadix;
	/** NULLABLE */
	private Integer nullable;
	/** REMARKS */
	private String remarks;
	/** COLUMN_DEF */
	private String columnDef;
	/** SQL_DATA_TYPE */
	private Integer sqlDataType;
	/** SQL_DATETIME_SUB */
	private Integer sqlDatetimeSub;
	/** CHAR_OCTET_LENGTH */
	private Integer charOctetLength;
	/** ORDINAL_POSITION */
	private Integer ordinalPosition;
	/** IS_NULLABLE */
	private String isNullable;
	/** SCOPE_CATALOG */
	private String scopeCatalog;
	/** SCOPE_SCHEMA */
	private String scopeSchema;
	/** SCOPE_TABLE */
	private String scopeTable;
	/** SOURCE_DATA_TYPE */
	private Short sourceDataType;
	/** IS_AUTOINCREMENT */
	private String isAutoincrement;
	/** IS_GENERATEDCOLUMN */
	private String isGeneratedcolumn;

	/**
	 * @return tableCat
	 */
	public final String getTableCat() {
		return tableCat;
	}

	/**
	 * @param tableCat
	 *            セットする tableCat
	 */
	public final void setTableCat(final String tableCat) {
		this.tableCat = tableCat;
	}

	/**
	 * @return tableSchem
	 */
	public final String getTableSchem() {
		return tableSchem;
	}

	/**
	 * @param tableSchem
	 *            セットする tableSchem
	 */
	public final void setTableSchem(final String tableSchem) {
		this.tableSchem = tableSchem;
	}

	/**
	 * @return tableName
	 */
	public final String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName
	 *            セットする tableName
	 */
	public final void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return columnName
	 */
	public final String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName
	 *            セットする columnName
	 */
	public final void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return dataType
	 */
	public final Integer getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            セットする dataType
	 */
	public final void setDataType(final Integer dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return typeName
	 */
	public final String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName
	 *            セットする typeName
	 */
	public final void setTypeName(final String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return columnSize
	 */
	public final Integer getColumnSize() {
		return columnSize;
	}

	/**
	 * @param columnSize
	 *            セットする columnSize
	 */
	public final void setColumnSize(final Integer columnSize) {
		this.columnSize = columnSize;
	}

	/**
	 * @return bufferLength
	 */
	public final Integer getBufferLength() {
		return bufferLength;
	}

	/**
	 * @param bufferLength
	 *            セットする bufferLength
	 */
	public final void setBufferLength(final Integer bufferLength) {
		this.bufferLength = bufferLength;
	}

	/**
	 * @return decimalDigits
	 */
	public final Integer getDecimalDigits() {
		return decimalDigits;
	}

	/**
	 * @param decimalDigits
	 *            セットする decimalDigits
	 */
	public final void setDecimalDigits(final Integer decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	/**
	 * @return numPrecRadix
	 */
	public final Integer getNumPrecRadix() {
		return numPrecRadix;
	}

	/**
	 * @param numPrecRadix
	 *            セットする numPrecRadix
	 */
	public final void setNumPrecRadix(final Integer numPrecRadix) {
		this.numPrecRadix = numPrecRadix;
	}

	/**
	 * @return nullable
	 */
	public final Integer getNullable() {
		return nullable;
	}

	/**
	 * @param nullable
	 *            セットする nullable
	 */
	public final void setNullable(final Integer nullable) {
		this.nullable = nullable;
	}

	/**
	 * @return remarks
	 */
	public final String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks
	 *            セットする remarks
	 */
	public final void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return columnDef
	 */
	public final String getColumnDef() {
		return columnDef;
	}

	/**
	 * @param columnDef
	 *            セットする columnDef
	 */
	public final void setColumnDef(final String columnDef) {
		this.columnDef = columnDef;
	}

	/**
	 * @return sqlDataType
	 */
	public final Integer getSqlDataType() {
		return sqlDataType;
	}

	/**
	 * @param sqlDataType
	 *            セットする sqlDataType
	 */
	public final void setSqlDataType(final Integer sqlDataType) {
		this.sqlDataType = sqlDataType;
	}

	/**
	 * @return sqlDatetimeSub
	 */
	public final Integer getSqlDatetimeSub() {
		return sqlDatetimeSub;
	}

	/**
	 * @param sqlDatetimeSub
	 *            セットする sqlDatetimeSub
	 */
	public final void setSqlDatetimeSub(final Integer sqlDatetimeSub) {
		this.sqlDatetimeSub = sqlDatetimeSub;
	}

	/**
	 * @return charOctetLength
	 */
	public final Integer getCharOctetLength() {
		return charOctetLength;
	}

	/**
	 * @param charOctetLength
	 *            セットする charOctetLength
	 */
	public final void setCharOctetLength(final Integer charOctetLength) {
		this.charOctetLength = charOctetLength;
	}

	/**
	 * @return ordinalPosition
	 */
	public final Integer getOrdinalPosition() {
		return ordinalPosition;
	}

	/**
	 * @param ordinalPosition
	 *            セットする ordinalPosition
	 */
	public final void setOrdinalPosition(final Integer ordinalPosition) {
		this.ordinalPosition = ordinalPosition;
	}

	/**
	 * @return isNullable
	 */
	public final String getIsNullable() {
		return isNullable;
	}

	/**
	 * @param isNullable
	 *            セットする isNullable
	 */
	public final void setIsNullable(final String isNullable) {
		this.isNullable = isNullable;
	}

	/**
	 * @return scopeCatalog
	 */
	public final String getScopeCatalog() {
		return scopeCatalog;
	}

	/**
	 * @param scopeCatalog
	 *            セットする scopeCatalog
	 */
	public final void setScopeCatalog(final String scopeCatalog) {
		this.scopeCatalog = scopeCatalog;
	}

	/**
	 * @return scopeSchema
	 */
	public final String getScopeSchema() {
		return scopeSchema;
	}

	/**
	 * @param scopeSchema
	 *            セットする scopeSchema
	 */
	public final void setScopeSchema(final String scopeSchema) {
		this.scopeSchema = scopeSchema;
	}

	/**
	 * @return scopeTable
	 */
	public final String getScopeTable() {
		return scopeTable;
	}

	/**
	 * @param scopeTable
	 *            セットする scopeTable
	 */
	public final void setScopeTable(final String scopeTable) {
		this.scopeTable = scopeTable;
	}

	/**
	 * @return sourceDataType
	 */
	public final Short getSourceDataType() {
		return sourceDataType;
	}

	/**
	 * @param sourceDataType
	 *            セットする sourceDataType
	 */
	public final void setSourceDataType(final Short sourceDataType) {
		this.sourceDataType = sourceDataType;
	}

	/**
	 * @return isAutoincrement
	 */
	public final String getIsAutoincrement() {
		return isAutoincrement;
	}

	/**
	 * @param isAutoincrement
	 *            セットする isAutoincrement
	 */
	public final void setIsAutoincrement(final String isAutoincrement) {
		this.isAutoincrement = isAutoincrement;
	}

	/**
	 * @return isGeneratedcolumn
	 */
	public final String getIsGeneratedcolumn() {
		return isGeneratedcolumn;
	}

	/**
	 * @param isGeneratedcolumn
	 *            セットする isGeneratedcolumn
	 */
	public final void setIsGeneratedcolumn(final String isGeneratedcolumn) {
		this.isGeneratedcolumn = isGeneratedcolumn;
	}

}
