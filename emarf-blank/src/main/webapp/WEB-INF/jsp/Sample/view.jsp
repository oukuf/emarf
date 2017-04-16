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
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <div>
        <e:label modelName="TEntity" propertyName="ancestorId" />
        <e:span modelName="TEntity" propertyName="ancestorId" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="parentSeq" />
        <e:span modelName="TEntity" propertyName="parentSeq" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entitySeq" />
        <e:span modelName="TEntity" propertyName="entitySeq" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entityNm" />
        <e:span modelName="TEntity" propertyName="entityNm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entitySei" />
        <e:span modelName="TEntity" propertyName="entitySei" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entityMei" />
        <e:span modelName="TEntity" propertyName="entityMei" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="passwordEnc" />
        <e:span modelName="TEntity" propertyName="passwordEnc" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="referId" />
        <e:span modelName="TEntity" propertyName="referId" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="uniqueCheckboxF" />
        <e:span modelName="TEntity" propertyName="uniqueCheckboxF" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="multipleCheckboxF" />
        <e:span modelName="TEntity" propertyName="multipleCheckboxF" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="kubunKb" />
        <e:span modelName="TEntity" propertyName="kubunKb" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="codeCd" />
        <e:span modelName="TEntity" propertyName="codeCd" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="bikoMsg" />
        <e:span modelName="TEntity" propertyName="bikoMsg" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="hidukeYmd" />
        <e:span modelName="TEntity" propertyName="hidukeYmd" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nichijiDt" />
        <e:span modelName="TEntity" propertyName="nichijiDt" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengetsuYm" />
        <e:span modelName="TEntity" propertyName="nengetsuYm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiY" />
        <e:span modelName="TEntity" propertyName="nengappiY" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiM" />
        <e:span modelName="TEntity" propertyName="nengappiM" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiD" />
        <e:span modelName="TEntity" propertyName="nengappiD" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="jikokuHm" />
        <e:span modelName="TEntity" propertyName="jikokuHm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="jikanTm" />
        <e:span modelName="TEntity" propertyName="jikanTm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="betsuReferId" />
        <e:span modelName="TEntity" propertyName="betsuReferId" />
      </div>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>フィールド構成</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <e:spanfield modelName="TEntity" propertyName="ancestorId" />
      <e:spanfield modelName="TEntity" propertyName="parentSeq" />
      <e:spanfield modelName="TEntity" propertyName="entitySeq" />
      <e:spanfield modelName="TEntity" propertyName="entityNm" />
      <e:spanfield modelName="TEntity" propertyName="entitySei" />
      <e:spanfield modelName="TEntity" propertyName="entityMei" />
      <e:spanfield modelName="TEntity" propertyName="passwordEnc" />
      <e:spanfield modelName="TEntity" propertyName="referId" />
      <e:spanfield modelName="TEntity" propertyName="uniqueCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="multipleCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="kubunKb" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="codeCd" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="bikoMsg" />
      <e:spanfield modelName="TEntity" propertyName="hidukeYmd" />
      <e:spanfield modelName="TEntity" propertyName="nichijiDt" />
      <e:spanfield modelName="TEntity" propertyName="nengetsuYm" />
      <e:spanfield modelName="TEntity" propertyName="nengappiY" />
      <e:spanfield modelName="TEntity" propertyName="nengappiM" />
      <e:spanfield modelName="TEntity" propertyName="nengappiD" />
      <e:spanfield modelName="TEntity" propertyName="jikokuHm" />
      <e:spanfield modelName="TEntity" propertyName="jikanTm" />
      <e:spanfield modelName="TEntity" propertyName="betsuReferId" />
    </fieldset>
  </e:form>
</section>
<section>
  <h3>入力フィールド構成（option属性を省略）</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <e:spanfield modelName="TEntity" propertyName="uniqueCheckboxF">
        <e:criterion />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="multipleCheckboxF">
        <e:criterion />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="kubunKb">
        <e:criterion />
      </e:spanfield>
      <e:spanfield modelName="TEntity" propertyName="codeCd">
        <e:criterion />
      </e:spanfield>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>モデル構成</h3>
  <e:form>
    <e:fieldset edit="false" modelName="TEntity" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
      <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
    </e:fieldset>
  </e:form>
</section>
<section>
  <h3>モデル構成（option属性を省略）</h3>
  <e:form>
    <e:fieldset edit="false" modelName="TEntity">
      <e:criterion />
    </e:fieldset>
  </e:form>
</section>
