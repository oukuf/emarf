-- Project Name : emarf
-- Date/Time    : 2017/04/28 21:43:35
-- Author       : oukuf@golorp
-- RDBMS Type   : MySQL
-- Application  : A5:SQL Mk-2

-- コード値マスタ
drop table if exists M_CODE_VALUE cascade;

create table M_CODE_VALUE (
  CODE_ID INT not null comment 'コードID'
  , CODE_VALUE VARCHAR(2) not null comment 'コード値'
  , CODE_VALUE_NM VARCHAR(20) not null comment 'コード値名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint M_CODE_VALUE_PKC primary key (CODE_ID,CODE_VALUE)
) comment 'コード値マスタ' ;

-- コードマスタ
drop table if exists M_CODE cascade;

create table M_CODE (
  CODE_ID INT not null AUTO_INCREMENT comment 'コードID'
  , CODE_NM VARCHAR(20) not null comment 'コード名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint M_CODE_PKC primary key (CODE_ID)
) comment 'コードマスタ' ;

-- 義兄弟
drop table if exists T_STEPBROTHER cascade;

create table T_STEPBROTHER (
  STEPBROTHER_ID INT AUTO_INCREMENT comment '義兄弟ID'
  , STEPBROTHER_NM VARCHAR(20) not null comment '義兄弟名称'
  , ANCESTOR_ID INT not null comment '祖先ID'
  , PARENT_SEQ INT not null comment '親NO'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_STEPBROTHER_PKC primary key (STEPBROTHER_ID)
) comment '義兄弟' ;

-- 他エンティティ
drop table if exists T_OTHERS cascade;

create table T_OTHERS (
  OTHERS_ID INT AUTO_INCREMENT comment '他エンティティID'
  , OTHERS_NM VARCHAR(20) not null comment '他エンティティ名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_OTHERS_PKC primary key (OTHERS_ID)
) comment '他エンティティ' ;

-- 集約
drop table if exists T_SUMMARY cascade;

create table T_SUMMARY (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , OTHERS_ID INT comment '他エンティティID'
  , SUMMARY_NM VARCHAR(20) not null comment '集約名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_SUMMARY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,OTHERS_ID)
) comment '集約' ;

-- エンティティ履歴
drop table if exists T_ENTITY_HISTORY cascade;

create table T_ENTITY_HISTORY (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , HISTORY_REC_SEQ INT comment '履歴NO'
  , ENTITY_NM VARCHAR(20) not null comment 'エンティティ名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_ENTITY_HISTORY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,HISTORY_REC_SEQ)
) comment 'エンティティ履歴' ;

-- 参照
drop table if exists T_REFER cascade;

create table T_REFER (
  REFER_ID INT AUTO_INCREMENT comment '参照ID'
  , REFER_NM VARCHAR(20) not null comment '参照名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_REFER_PKC primary key (REFER_ID)
) comment '参照' ;

-- 兄弟
drop table if exists T_BROTHER cascade;

create table T_BROTHER (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , BROTHER_NM VARCHAR(20) not null comment '兄弟名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_BROTHER_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ)
) comment '兄弟' ;

-- 子孫
drop table if exists T_DESCENDANT cascade;

create table T_DESCENDANT (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , CHILD_SEQ INT comment '子NO'
  , DESCENDANT_SEQ INT comment '子孫NO'
  , DESCENDANT_NM VARCHAR(20) not null comment '子孫名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_DESCENDANT_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,CHILD_SEQ,DESCENDANT_SEQ)
) comment '子孫' ;

-- 子
drop table if exists T_CHILD cascade;

create table T_CHILD (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , CHILD_SEQ INT comment '子NO'
  , CHILD_NM VARCHAR(20) not null comment '子名称'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_CHILD_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ,CHILD_SEQ)
) comment '子' ;

-- エンティティ
drop table if exists T_ENTITY cascade;

create table T_ENTITY (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , ENTITY_SEQ INT comment 'エンティティNO'
  , ENTITY_NM VARCHAR(20) not null comment 'エンティティ名称'
  , ENTITY_SEI VARCHAR(20) comment 'エンティティ姓'
  , ENTITY_MEI VARCHAR(20) comment 'エンティティ名'
  , PASSWORD_ENC VARCHAR(100) comment 'パスワード'
  , REFER_ID INT comment '参照ID'
  , UNIQUE_CHECKBOX_F VARCHAR(2) comment '単一チェックボックスフラグ'
  , MULTIPLE_CHECKBOX_F VARCHAR(2) comment '複数チェックボックスフラグ'
  , KUBUN_KB VARCHAR(2) comment '区分'
  , CODE_CD VARCHAR(2) comment 'コード'
  , BIKO_MSG VARCHAR(200) comment '備考'
  , HIDUKE_YMD DATE comment '日付'
  , NICHIJI_DT DATETIME comment '日時'
  , NENGETSU_YM CHAR(6) comment '年月'
  , NENGAPPI_Y CHAR(4) comment '年月日（年）'
  , NENGAPPI_M CHAR(2) comment '年月日（月）'
  , NENGAPPI_D CHAR(2) comment '年月日（日）'
  , JIKOKU_HM TIME comment '時刻'
  , JIKAN_TM TIME comment '時間'
  , BETSU_REFER_ID INT comment '別参照ID'
  , GAZO_IMG VARCHAR(20480) comment '画像'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_ENTITY_PKC primary key (ANCESTOR_ID,PARENT_SEQ,ENTITY_SEQ)
) comment 'エンティティ' ;

-- 親
drop table if exists T_PARENT cascade;

create table T_PARENT (
  ANCESTOR_ID INT comment '祖先ID'
  , PARENT_SEQ INT comment '親NO'
  , PARENT_NM VARCHAR(20) not null comment '親名称'
  , DELETE_F VARCHAR(2) comment '削除フラグ'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_PARENT_PKC primary key (ANCESTOR_ID,PARENT_SEQ)
) comment '親' ;

-- 祖先
drop table if exists T_ANCESTOR cascade;

create table T_ANCESTOR (
  ANCESTOR_ID INT AUTO_INCREMENT comment '祖先ID'
  , ANCESTOR_NM VARCHAR(20) not null comment '祖先名称'
  , DELETE_F VARCHAR(2) comment '削除フラグ'
  , INSERT_DT DATETIME not null comment '登録日時'
  , INSERT_BY INT comment '登録者'
  , UPDATE_DT DATETIME not null comment '更新日時'
  , UPDATE_BY INT comment '更新者'
  , constraint T_ANCESTOR_PKC primary key (ANCESTOR_ID)
) comment '祖先' ;

-- 名称ビュー
drop view if exists V_MEISHO;

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

