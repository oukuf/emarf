<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="jp.co.golorp.emarf.constants.AppKey"%>
<%@page import="jp.co.golorp.emarf.properties.App"%>
<%@taglib uri="http://emarf.golorp.co.jp/tags" prefix="e"%>
<%
	String optionModel = App.get(AppKey.CRITERIA_OPTION_MODEL_DEFAULT);
	String optionValue = App.get(AppKey.CRITERIA_OPTION_VALUE_DEFAULT);
	String optionLabel = App.get(AppKey.CRITERIA_OPTION_LABEL_DEFAULT);
	String criteriaModel = App.get(AppKey.CRITERION_MODEL_DEFAULT);
	String criteriaProperty = App.get(AppKey.CRITERION_PROPERTY_DEFAULT);
	String criteriaValue = App.get(AppKey.CRITERION_VALUE_DEFAULT);
%>
URLに実Entityのモデル名があればmodelName属性は不要
<section>
  <h3>単一タグ構成</h3>
  <e:table>
    <e:caption modelName="TEntity" />
    <thead>
      <tr>
        <th>no</th>
        <e:th modelName="TEntity" propertyName="ancestorId" />
        <e:th modelName="TEntity" propertyName="parentSeq" />
        <e:th modelName="TEntity" propertyName="entitySeq" />
        <e:th modelName="TEntity" propertyName="entityNm" />
        <e:th modelName="TEntity" propertyName="entitySei" />
        <e:th modelName="TEntity" propertyName="entityMei" />
        <e:th modelName="TEntity" propertyName="passwordEnc" />
        <e:th modelName="TEntity" propertyName="referId" />
        <e:th modelName="TEntity" propertyName="uniqueCheckboxF" />
        <e:th modelName="TEntity" propertyName="multipleCheckboxF" />
        <e:th modelName="TEntity" propertyName="kubunKb" />
        <e:th modelName="TEntity" propertyName="codeCd" />
        <e:th modelName="TEntity" propertyName="bikoMsg" />
        <e:th modelName="TEntity" propertyName="hidukeYmd" />
        <e:th modelName="TEntity" propertyName="nichijiDt" />
        <e:th modelName="TEntity" propertyName="nengetsuYm" />
        <e:th modelName="TEntity" propertyName="nengappiY" />
        <e:th modelName="TEntity" propertyName="nengappiM" />
        <e:th modelName="TEntity" propertyName="nengappiD" />
        <e:th modelName="TEntity" propertyName="jikokuHm" />
        <e:th modelName="TEntity" propertyName="jikanTm" />
        <e:th modelName="TEntity" propertyName="betsuReferId" />
      </tr>
    </thead>
    <e:tbody modelName="TEntity">
      <tr>
        <td><e:tbodyno /></td>
        <e:td modelName="TEntity" propertyName="ancestorId" />
        <e:td modelName="TEntity" propertyName="parentSeq" />
        <e:td modelName="TEntity" propertyName="entitySeq" />
        <e:td modelName="TEntity" propertyName="entityNm" />
        <e:td modelName="TEntity" propertyName="entitySei" />
        <e:td modelName="TEntity" propertyName="entityMei" />
        <e:td modelName="TEntity" propertyName="passwordEnc" />
        <e:td modelName="TEntity" propertyName="referId" />
        <e:td modelName="TEntity" propertyName="uniqueCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:td>
        <e:td modelName="TEntity" propertyName="multipleCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:td>
        <e:td modelName="TEntity" propertyName="kubunKb" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:td>
        <e:td modelName="TEntity" propertyName="codeCd" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:td>
        <e:td modelName="TEntity" propertyName="bikoMsg" />
        <e:td modelName="TEntity" propertyName="hidukeYmd" />
        <e:td modelName="TEntity" propertyName="nichijiDt" />
        <e:td modelName="TEntity" propertyName="nengetsuYm" />
        <e:td modelName="TEntity" propertyName="nengappiY" />
        <e:td modelName="TEntity" propertyName="nengappiM" />
        <e:td modelName="TEntity" propertyName="nengappiD" />
        <e:td modelName="TEntity" propertyName="jikokuHm" />
        <e:td modelName="TEntity" propertyName="jikanTm" />
        <e:td modelName="TEntity" propertyName="betsuReferId" />
      </tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>単一タグ構成（modelName/option属性省略）</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <thead>
      <tr>
        <th>no</th>
        <e:th propertyName="ancestorId" />
        <e:th propertyName="parentSeq" />
        <e:th propertyName="entitySeq" />
        <e:th propertyName="entityNm" />
        <e:th propertyName="entitySei" />
        <e:th propertyName="entityMei" />
        <e:th propertyName="passwordEnc" />
        <e:th propertyName="referId" />
        <e:th propertyName="uniqueCheckboxF" />
        <e:th propertyName="multipleCheckboxF" />
        <e:th propertyName="kubunKb" />
        <e:th propertyName="codeCd" />
        <e:th propertyName="bikoMsg" />
        <e:th propertyName="hidukeYmd" />
        <e:th propertyName="nichijiDt" />
        <e:th propertyName="nengetsuYm" />
        <e:th propertyName="nengappiY" />
        <e:th propertyName="nengappiM" />
        <e:th propertyName="nengappiD" />
        <e:th propertyName="jikokuHm" />
        <e:th propertyName="jikanTm" />
        <e:th propertyName="betsuReferId" />
      </tr>
    </thead>
    <e:tbody>
      <tr>
        <td><e:tbodyno /></td>
        <e:td propertyName="ancestorId" />
        <e:td propertyName="parentSeq" />
        <e:td propertyName="entitySeq" />
        <e:td propertyName="entityNm" />
        <e:td propertyName="entitySei" />
        <e:td propertyName="entityMei" />
        <e:td propertyName="passwordEnc" />
        <e:td propertyName="referId" />
        <e:td propertyName="uniqueCheckboxF">
          <e:criterion />
        </e:td>
        <e:td propertyName="multipleCheckboxF">
          <e:criterion />
        </e:td>
        <e:td propertyName="kubunKb">
          <e:criterion />
        </e:td>
        <e:td propertyName="codeCd">
          <e:criterion />
        </e:td>
        <e:td propertyName="bikoMsg" />
        <e:td propertyName="hidukeYmd" />
        <e:td propertyName="nichijiDt" />
        <e:td propertyName="nengetsuYm" />
        <e:td propertyName="nengappiY" />
        <e:td propertyName="nengappiM" />
        <e:td propertyName="nengappiD" />
        <e:td propertyName="jikokuHm" />
        <e:td propertyName="jikanTm" />
        <e:td propertyName="betsuReferId" />
      </tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>セルリスト構成</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <thead>
      <tr>
        <e:ths />
      </tr>
    </thead>
    <e:tbody>
      <tr>
        <e:tds optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:tds>
      </tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>セルリスト構成（option属性省略）</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <thead>
      <tr>
        <e:ths />
      </tr>
    </thead>
    <e:tbody>
      <tr>
        <e:tds>
          <e:criterion />
        </e:tds>
      </tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>行構成</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <e:thead>
      <e:tr />
    </e:thead>
    <e:tbody>
      <e:tr optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>行構成（option属性省略）</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <e:thead>
      <e:tr />
    </e:thead>
    <e:tbody>
      <e:tr>
        <e:criterion />
      </e:tr>
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>グループ構成</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <e:thead />
    <e:tbody optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
      <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>グループ構成（option属性省略）</h3>
  <e:table modelName="TEntity">
    <e:caption />
    <e:thead />
    <e:tbody>
      <e:criterion />
    </e:tbody>
  </e:table>
</section>
<section>
  <h3>テーブル構成</h3>
  <e:table modelName="TEntity" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
    <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
  </e:table>
</section>
<section>
  <h3>テーブル構成（option属性省略）</h3>
  <e:table modelName="TEntity">
    <e:criterion />
  </e:table>
</section>
