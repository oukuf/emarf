-- Project Name : emarf
-- Date/Time    : 2017/04/16 17:01:54
-- Author       : oukuf@golorp
-- RDBMS Type   : sqlite3
-- Application  : A5:SQL Mk-2

-- コード値マスタ
drop table M_CODE_VALUE;

create table M_CODE_VALUE (
  CODE_ID INTEGER not null
  , CODE_VALUE VARCHAR(2) not null
  , CODE_VALUE_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint M_CODE_VALUE_PKC primary key (CODE_ID,CODE_VALUE)
) ;

-- コードマスタ
drop table M_CODE;

create table M_CODE (
  CODE_ID INTEGER not null
  , CODE_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint M_CODE_PKC primary key (CODE_ID)
) ;

-- 義兄弟
drop table T_STEPBROTHER;

create table T_STEPBROTHER (
  STEPBROTHER_ID INTEGER
  , STEPBROTHER_NM VARCHAR(20) not null
  , ANCESTOR_ID INTEGER not null
  , PARENT_SEQ INTEGER not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_STEPBROTHER_PKC primary key (STEPBROTHER_ID)
) ;

-- 他エンティティ
drop table T_OTHERS;

create table T_OTHERS (
  OTHERS_ID INTEGER
  , OTHERS_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_OTHERS_PKC primary key (OTHERS_ID)
) ;

-- 集約
drop table T_SUMMARY;

create table T_SUMMARY (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , OTHERS_ID INTEGER
  , SUMMARY_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_SUMMARY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,OTHERS_ID)
) ;

-- エンティティ履歴
drop table T_ENTITY_HISTORY;

create table T_ENTITY_HISTORY (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , HISTORY_REC_SEQ INTEGER
  , HISTORY_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_ENTITY_HISTORY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,HISTORY_REC_SEQ)
) ;

-- 参照
drop table T_REFER;

create table T_REFER (
  REFER_ID INTEGER
  , REFER_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_REFER_PKC primary key (REFER_ID)
) ;

-- 兄弟
drop table T_BROTHER;

create table T_BROTHER (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , BROTHER_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_BROTHER_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ)
) ;

-- 子孫
drop table T_DESCENDANT;

create table T_DESCENDANT (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , CHILD_SEQ INTEGER
  , DESCENDANT_SEQ INTEGER
  , DESCENDANT_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_DESCENDANT_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,CHILD_SEQ,DESCENDANT_SEQ)
) ;

-- 子
drop table T_CHILD;

create table T_CHILD (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , CHILD_SEQ INTEGER
  , CHILD_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_CHILD_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,CHILD_SEQ)
) ;

-- エンティティ
drop table T_ENTITY;

create table T_ENTITY (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , ENTITY_SEQ INTEGER
  , ENTITY_NM VARCHAR(20) not null
  , ENTITY_SEI VARCHAR(20)
  , ENTITY_MEI VARCHAR(20)
  , PASSWORD_ENC VARCHAR(100)
  , REFER_ID INTEGER
  , UNIQUE_CHECKBOX_F VARCHAR(2)
  , MULTIPLE_CHECKBOX_F VARCHAR(2)
  , KUBUN_KB VARCHAR(2)
  , CODE_CD VARCHAR(2)
  , BIKO_MSG VARCHAR(200)
  , HIDUKE_YMD DATE
  , NICHIJI_DT TIMESTAMP
  , NENGETSU_YM CHAR(6)
  , NENGAPPI_Y CHAR(4)
  , NENGAPPI_M CHAR(2)
  , NENGAPPI_D CHAR(2)
  , JIKOKU_HM TIME
  , JIKAN_TM TIME
  , BETSU_REFER_ID INTEGER
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_ENTITY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ)
) ;

-- 親
drop table T_PARENT;

create table T_PARENT (
  ANCESTOR_ID INTEGER
  , PARENT_SEQ INTEGER
  , PARENT_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_PARENT_PKC primary key (ANCESTOR_ID,PARENT_SEQ)
) ;

-- 祖先
drop table T_ANCESTOR;

create table T_ANCESTOR (
  ANCESTOR_ID INTEGER
  , ANCESTOR_NM VARCHAR(20) not null
  , INSERT_DT TIMESTAMP not null
  , INSERT_BY INTEGER
  , UPDATE_DT TIMESTAMP not null
  , UPDATE_BY INTEGER
  , constraint T_ANCESTOR_PKC primary key (ANCESTOR_ID)
) ;

-- 名称ビュー
drop view V_MEISHO;

create view V_MEISHO as
SELECT
  T_ANCESTOR.ANCESTOR_ID
  , T_PARENT.PARENT_SEQ
  , T_ENTITY.ENTITY_SEQ
  , T_CHILD.CHILD_SEQ
  , T_DESCENDANT.DESCENDANT_SEQ
  , T_ANCESTOR.ANCESTOR_NM
  , T_PARENT.PARENT_NM
  , T_ENTITY.ENTITY_NM
  , T_CHILD.CHILD_NM
  , T_DESCENDANT.DESCENDANT_NM
FROM
  T_ANCESTOR
  INNER JOIN T_PARENT
    ON T_ANCESTOR.ANCESTOR_ID = T_PARENT.ANCESTOR_ID
  INNER JOIN T_ENTITY
    ON T_PARENT.ANCESTOR_ID = T_ENTITY.ANCESTOR_ID
    AND T_PARENT.PARENT_SEQ = T_ENTITY.PARENT_SEQ
  INNER JOIN T_CHILD
    ON T_ENTITY.ANCESTOR_ID = T_CHILD.ANCESTOR_ID
    AND T_ENTITY.PARENT_SEQ = T_CHILD.PARENT_SEQ
    AND T_ENTITY.ENTITY_SEQ = T_CHILD.ENTITY_SEQ
  INNER JOIN T_DESCENDANT
    ON T_CHILD.ANCESTOR_ID = T_DESCENDANT.ANCESTOR_ID
    AND T_CHILD.PARENT_SEQ = T_DESCENDANT.PARENT_SEQ
    AND T_CHILD.ENTITY_SEQ = T_DESCENDANT.ENTITY_SEQ
    AND T_CHILD.CHILD_SEQ = T_DESCENDANT.CHILD_SEQ

;
