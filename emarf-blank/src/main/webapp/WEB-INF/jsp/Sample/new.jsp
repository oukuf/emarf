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
        <e:input modelName="TEntity" type="text" propertyName="ancestorId" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="parentSeq" />
        <e:input modelName="TEntity" type="text" propertyName="parentSeq" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entitySeq" />
        <e:input modelName="TEntity" type="text" propertyName="entitySeq" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entityNm" />
        <e:input modelName="TEntity" type="text" propertyName="entityNm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entitySei" />
        <e:input modelName="TEntity" type="text" propertyName="entitySei" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="entityMei" />
        <e:input modelName="TEntity" type="text" propertyName="entityMei" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="passwordEnc" />
        <e:input modelName="TEntity" type="password" propertyName="passwordEnc" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="referId" />
        <e:input modelName="TEntity" type="text" propertyName="referId" />
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="uniqueCheckboxF" />
          <div>
            <e:check modelName="TEntity" propertyName="uniqueCheckboxF" value="0" />
            <e:label modelName="TEntity" propertyName="uniqueCheckboxF" value="0" label="オン" />
          </div>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="multipleCheckboxF" />
          <div>
            <e:check modelName="TEntity" propertyName="multipleCheckboxF" value="0" />
            <e:label modelName="TEntity" propertyName="multipleCheckboxF" value="0" label="０" />
            <e:check modelName="TEntity" propertyName="multipleCheckboxF" value="1" />
            <e:label modelName="TEntity" propertyName="multipleCheckboxF" value="1" label="１" />
            <e:check modelName="TEntity" propertyName="multipleCheckboxF" value="2" />
            <e:label modelName="TEntity" propertyName="multipleCheckboxF" value="2" label="２" />
          </div>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="kubunKb" />
          <div>
            <e:radio modelName="TEntity" propertyName="kubunKb" value="0" />
            <e:label modelName="TEntity" propertyName="kubunKb" value="0" label="０" />
            <e:radio modelName="TEntity" propertyName="kubunKb" value="1" />
            <e:label modelName="TEntity" propertyName="kubunKb" value="1" label="１" />
            <e:radio modelName="TEntity" propertyName="kubunKb" value="2" />
            <e:label modelName="TEntity" propertyName="kubunKb" value="2" label="２" />
          </div>
        </fieldset>
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="codeCd" />
        <e:select modelName="TEntity" propertyName="codeCd">
          <e:option />
          <e:option value="0" label="０" />
          <e:option value="1" label="１" />
          <e:option value="2" label="２" />
        </e:select>
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="bikoMsg" />
        <e:textarea modelName="TEntity" propertyName="bikoMsg" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="hidukeYmd" />
        <e:input modelName="TEntity" type="text" propertyName="hidukeYmd" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nichijiDt" />
        <e:input modelName="TEntity" type="text" propertyName="nichijiDt" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengetsuYm" />
        <e:input modelName="TEntity" type="text" propertyName="nengetsuYm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiY" />
        <e:input modelName="TEntity" type="text" propertyName="nengappiY" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiM" />
        <e:input modelName="TEntity" type="text" propertyName="nengappiM" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="nengappiD" />
        <e:input modelName="TEntity" type="text" propertyName="nengappiD" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="jikokuHm" />
        <e:input modelName="TEntity" type="text" propertyName="jikokuHm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="jikanTm" />
        <e:input modelName="TEntity" type="text" propertyName="jikanTm" />
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="betsuReferId" />
        <e:input modelName="TEntity" type="text" propertyName="betsuReferId" />
      </div>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>リスト構成</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="uniqueCheckboxF" />
          <e:checks modelName="TEntity" propertyName="uniqueCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
            <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
          </e:checks>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="multipleCheckboxF" />
          <e:checks modelName="TEntity" propertyName="multipleCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
            <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
          </e:checks>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="kubunKb" />
          <e:radios modelName="TEntity" propertyName="kubunKb" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
            <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
          </e:radios>
        </fieldset>
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="codeCd" />
        <e:select modelName="TEntity" propertyName="codeCd" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
          <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
        </e:select>
      </div>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>リスト構成（option属性を省略）</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="uniqueCheckboxF" />
          <e:checks modelName="TEntity" propertyName="uniqueCheckboxF">
            <e:criterion />
          </e:checks>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="multipleCheckboxF" />
          <e:checks modelName="TEntity" propertyName="multipleCheckboxF">
            <e:criterion />
          </e:checks>
        </fieldset>
      </div>
      <div>
        <fieldset>
          <e:legend modelName="TEntity" propertyName="kubunKb" />
          <e:radios modelName="TEntity" propertyName="kubunKb">
            <e:criterion />
          </e:radios>
        </fieldset>
      </div>
      <div>
        <e:label modelName="TEntity" propertyName="codeCd" />
        <e:select modelName="TEntity" propertyName="codeCd">
          <e:criterion />
        </e:select>
      </div>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>フィールド構成</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <e:inputfield modelName="TEntity" propertyName="ancestorId" />
      <e:inputfield modelName="TEntity" propertyName="parentSeq" />
      <e:inputfield modelName="TEntity" propertyName="entitySeq" />
      <e:inputfield modelName="TEntity" propertyName="entityNm" />
      <e:inputfield modelName="TEntity" propertyName="entitySei" />
      <e:inputfield modelName="TEntity" propertyName="entityMei" />
      <e:inputfield modelName="TEntity" propertyName="passwordEnc" />
      <e:inputfield modelName="TEntity" propertyName="referId" />
      <e:checksfield modelName="TEntity" propertyName="uniqueCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:checksfield>
      <e:checksfield modelName="TEntity" propertyName="multipleCheckboxF" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:checksfield>
      <e:radiosfield modelName="TEntity" propertyName="kubunKb" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:radiosfield>
      <e:selectfield modelName="TEntity" propertyName="codeCd" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
        <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
      </e:selectfield>
      <e:textareafield modelName="TEntity" propertyName="bikoMsg" />
      <e:inputfield modelName="TEntity" propertyName="hidukeYmd" />
      <e:inputfield modelName="TEntity" propertyName="nichijiDt" />
      <e:inputfield modelName="TEntity" propertyName="nengetsuYm" />
      <e:inputfield modelName="TEntity" propertyName="nengappiY" />
      <e:inputfield modelName="TEntity" propertyName="nengappiM" />
      <e:inputfield modelName="TEntity" propertyName="nengappiD" />
      <e:inputfield modelName="TEntity" propertyName="jikokuHm" />
      <e:inputfield modelName="TEntity" propertyName="jikanTm" />
      <e:inputfield modelName="TEntity" propertyName="betsuReferId" />
    </fieldset>
  </e:form>
</section>
<section>
  <h3>入力フィールド構成（option属性を省略）</h3>
  <e:form>
    <fieldset>
      <e:legend modelName="TEntity" />
      <e:checksfield modelName="TEntity" propertyName="uniqueCheckboxF">
        <e:criterion />
      </e:checksfield>
      <e:checksfield modelName="TEntity" propertyName="multipleCheckboxF">
        <e:criterion />
      </e:checksfield>
      <e:radiosfield modelName="TEntity" propertyName="kubunKb">
        <e:criterion />
      </e:radiosfield>
      <e:selectfield modelName="TEntity" propertyName="codeCd">
        <e:criterion />
      </e:selectfield>
    </fieldset>
  </e:form>
</section>
<section>
  <h3>モデル構成</h3>
  <e:form>
    <e:fieldset modelName="TEntity" optionModel="<%=optionModel%>" optionValue="<%=optionValue%>" optionLabel="<%=optionLabel%>">
      <e:criterion modelName="<%=criteriaModel%>" propertyName="<%=criteriaProperty%>" value="<%=criteriaValue%>" />
    </e:fieldset>
  </e:form>
</section>
<section>
  <h3>モデル構成（option属性を省略）</h3>
  <e:form>
    <e:fieldset modelName="TEntity">
      <e:criterion />
    </e:fieldset>
  </e:form>
</section>
