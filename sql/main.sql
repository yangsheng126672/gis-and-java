--创建空间信息存储扩展
CREATE extension postgis;

-- [表1] 设备信息表
DROP TABLE IF EXISTS share_dev;
CREATE TABLE share_dev (
  id char(12) primary key,
  type_id int8 NOT NULL,
  name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  status int2 NOT NULL DEFAULT -1,
  sn varchar(128) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  lng varchar(16) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  lat varchar(16) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  addr varchar(128) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  platform_code varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  del_flag int2 NOT NULL DEFAULT 0,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN share_dev.id IS '主键';
COMMENT ON COLUMN share_dev.type_id IS '设备类型ID';
COMMENT ON COLUMN share_dev.name IS '名称';
COMMENT ON COLUMN share_dev.status IS '状态';
COMMENT ON COLUMN share_dev.sn IS '序列号';
COMMENT ON COLUMN share_dev.lng IS '经度';
COMMENT ON COLUMN share_dev.lat IS '纬度';
COMMENT ON COLUMN share_dev.addr IS '详细地址';
COMMENT ON COLUMN share_dev.platform_code IS '平台编码';
COMMENT ON COLUMN share_dev.status IS '状态';
COMMENT ON COLUMN share_dev.del_flag IS '是否删除,0-正常，1-删除';
COMMENT ON COLUMN share_dev.create_by IS '创建人';
COMMENT ON COLUMN share_dev.create_at IS '创建时间';
COMMENT ON COLUMN share_dev.update_by IS '修改人';
COMMENT ON COLUMN share_dev.update_at IS '修改时间';
COMMENT ON TABLE share_dev is '设备信息表';
-- [表2] 设备类型表
DROP TABLE IF EXISTS share_dev_type;
CREATE TABLE share_dev_type (
  id serial8 primary key,
  name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  val varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  p_id int8 NOT NULL DEFAULT '-1'::integer,
  platform_code varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  limb_leaf int2 NOT NULL DEFAULT -1,
  del_flag int2 NOT NULL DEFAULT 0,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN share_dev_type.id IS '主键';
COMMENT ON COLUMN share_dev_type.name IS '设备类型名称';
COMMENT ON COLUMN share_dev_type.val IS '名称对应的值';
COMMENT ON COLUMN share_dev_type.p_id IS '父ID';
COMMENT ON COLUMN share_dev_type.platform_code IS '平台编码';
COMMENT ON COLUMN share_dev_type.limb_leaf IS '枝干还是叶子，1-枝干，2-叶子';
COMMENT ON COLUMN share_dev_type.del_flag IS '是否删除,0-正常，1-删除';
COMMENT ON COLUMN share_dev_type.create_by IS '创建人';
COMMENT ON COLUMN share_dev_type.create_at IS '创建时间';
COMMENT ON COLUMN share_dev_type.update_by IS '修改人';
COMMENT ON COLUMN share_dev_type.update_at IS '修改时间';
COMMENT ON TABLE share_dev_type is '设备类型表';
-- [表3] 设备扩展信息表
DROP TABLE IF EXISTS gis_dev_ext;
CREATE TABLE gis_dev_ext (
  id serial8 primary key,
  dev_id char(12) NOT NULL,
  name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  code varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  caliber int4,
  material varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  geom geometry not null,
  tpl_type_id int8 NOT NULL,
  data_info jsonb NOT NULL,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN gis_dev_ext.id IS '主键';
COMMENT ON COLUMN gis_dev_ext.dev_id IS '设备ID';
COMMENT ON COLUMN gis_dev_ext.name IS '设备名称';
COMMENT ON COLUMN gis_dev_ext.code IS '设备编码';
COMMENT ON COLUMN gis_dev_ext.caliber IS '管径';
COMMENT ON COLUMN gis_dev_ext.material IS '材质';
COMMENT ON COLUMN gis_dev_ext.geom IS '空间信息';
COMMENT ON COLUMN gis_dev_ext.tpl_type_id IS '模板类型ID';
COMMENT ON COLUMN gis_dev_ext.data_info IS 'JSON数据';
COMMENT ON COLUMN gis_dev_ext.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_dev_ext.create_by IS '创建人';
COMMENT ON COLUMN gis_dev_ext.create_at IS '创建时间';
COMMENT ON COLUMN gis_dev_ext.update_by IS '修改人';
COMMENT ON COLUMN gis_dev_ext.update_at IS '修改时间';
COMMENT ON TABLE gis_dev_ext is '设备扩展信息表';
-- [表4] 设备属性
DROP TABLE IF EXISTS gis_dev_tpl_attr;
CREATE TABLE gis_dev_tpl_attr (
  id serial8 primary key,
  type_id int8 NOT NULL,
  field_desc varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  field_name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  data_type varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  idx int2 NOT NULL DEFAULT 0,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN gis_dev_tpl_attr.id IS '主键';
COMMENT ON COLUMN gis_dev_tpl_attr.type_id IS '设备类型ID';
COMMENT ON COLUMN gis_dev_tpl_attr.field_desc IS '字段中文名称';
COMMENT ON COLUMN gis_dev_tpl_attr.field_name IS '字段英文名称';
COMMENT ON COLUMN gis_dev_tpl_attr.data_type IS '字段数据类型';
COMMENT ON COLUMN gis_dev_tpl_attr.idx IS '字段显示序号';
COMMENT ON COLUMN gis_dev_tpl_attr.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_dev_tpl_attr.create_by IS '创建人';
COMMENT ON COLUMN gis_dev_tpl_attr.create_at IS '创建时间';
COMMENT ON COLUMN gis_dev_tpl_attr.update_by IS '修改人';
COMMENT ON COLUMN gis_dev_tpl_attr.update_at IS '修改时间';
COMMENT ON TABLE gis_dev_tpl_attr is '设备属性';

-- [表5] 系统参数表
DROP TABLE IF EXISTS dict_detail;
CREATE TABLE dict_detail (
  id serial8 primary key,
  type_id int8 NOT NULL,
  name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  val varchar(256) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  platform_code varchar(16) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN dict_detail.id IS '主键';
COMMENT ON COLUMN dict_detail.type_id IS '类型ID';
COMMENT ON COLUMN dict_detail.name IS '名称';
COMMENT ON COLUMN dict_detail.val IS '值';
COMMENT ON COLUMN dict_detail.platform_code IS '平台编码';
COMMENT ON COLUMN dict_detail.delete_flag IS '是否删除';
COMMENT ON COLUMN dict_detail.create_by IS '创建人';
COMMENT ON COLUMN dict_detail.create_at IS '创建时间';
COMMENT ON COLUMN dict_detail.update_by IS '修改人';
COMMENT ON COLUMN dict_detail.update_at IS '修改时间';
COMMENT ON TABLE dict_detail is '系统参数表';
-- [表6] 系统参数类型表
DROP TABLE IF EXISTS dict_type;
CREATE TABLE dict_type (
  id serial8 primary key,
  name varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  val varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  p_id int8 NOT NULL DEFAULT '-1'::integer,
  platform_code varchar(16) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN dict_type.id IS '主键';
COMMENT ON COLUMN dict_type.name IS '类型名称';
COMMENT ON COLUMN dict_type.val IS '名称对应的值';
COMMENT ON COLUMN dict_type.p_id IS '父ID';
COMMENT ON COLUMN dict_type.platform_code IS '平台编码';
COMMENT ON COLUMN dict_type.delete_flag IS '是否删除';
COMMENT ON COLUMN dict_type.create_by IS '创建人';
COMMENT ON COLUMN dict_type.create_at IS '创建时间';
COMMENT ON COLUMN dict_type.update_by IS '修改人';
COMMENT ON COLUMN dict_type.update_at IS '修改时间';
COMMENT ON TABLE dict_type is '系统参数类型表';


-- [表7] 空间测量记录
DROP TABLE IF EXISTS gis_measurement;
CREATE TABLE gis_measurement
(
  id serial8 primary key, -- id
  name character varying(32) NOT NULL DEFAULT ''::character varying, -- 名称
  meatured_value character varying(32) NOT NULL DEFAULT ''::character varying, -- 测量值
  remark character varying(254) NOT NULL DEFAULT ''::character varying, -- 备注
  geom geometry NOT NULL, -- 空间信息
  delete_flag boolean NOT NULL DEFAULT false, -- 是否删除
  create_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 创建人
  create_at timestamp without time zone NOT NULL DEFAULT now(), -- 创建时间
  update_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 修改人
  update_at timestamp without time zone NOT NULL DEFAULT now(), -- 修改时间
  zoom integer NOT NULL DEFAULT 14
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_measurement
  OWNER TO postgres;
COMMENT ON TABLE gis_measurement
  IS '空间测量表';
COMMENT ON COLUMN gis_measurement.id IS 'id';
COMMENT ON COLUMN gis_measurement.name IS '名称';
COMMENT ON COLUMN gis_measurement.meatured_value IS '测量值';
COMMENT ON COLUMN gis_measurement.remark IS '备注';
COMMENT ON COLUMN gis_measurement.geom IS '空间信息';
COMMENT ON COLUMN gis_measurement.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_measurement.create_by IS '创建人';
COMMENT ON COLUMN gis_measurement.create_at IS '创建时间';
COMMENT ON COLUMN gis_measurement.update_by IS '修改人';
COMMENT ON COLUMN gis_measurement.update_at IS '修改时间';

-- [表8] 属性查询条件记录表
DROP TABLE IF EXISTS gis_attr_condition_record;
CREATE TABLE "gis_attr_condition_record" (
  id serial8 NOT NULL,
  criteria_exe varchar(200) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  type_id int8 NOT NULL,
  tpl_id int8 NOT NULL,
  criteria varchar(200) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  create_at timestamp(6) NOT NULL DEFAULT now(),
  update_by varchar(32) COLLATE "pg_catalog"."default" NOT NULL DEFAULT ''::character varying,
  update_at timestamp(6) NOT NULL DEFAULT now(),
  CONSTRAINT "gis_attr_condition_record_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "gis_attr_condition_record"
  OWNER TO "postgres";

COMMENT ON COLUMN "gis_attr_condition_record"."id" IS '主键';
COMMENT ON COLUMN "gis_attr_condition_record"."criteria_exe" IS '可执行的条件';
COMMENT ON COLUMN "gis_attr_condition_record"."type_id" IS '类型ID';
COMMENT ON COLUMN "gis_attr_condition_record"."tpl_id" IS '模板的类型ID';
COMMENT ON COLUMN "gis_attr_condition_record"."criteria" IS '中文表述的筛选条件';
COMMENT ON COLUMN "gis_attr_condition_record"."delete_flag" IS '是否删除';
COMMENT ON COLUMN "gis_attr_condition_record"."create_by" IS '创建人';
COMMENT ON COLUMN "gis_attr_condition_record"."create_at" IS '创建时间';
COMMENT ON COLUMN "gis_attr_condition_record"."update_by" IS '修改人';
COMMENT ON COLUMN "gis_attr_condition_record"."update_at" IS '修改时间';
COMMENT ON TABLE "gis_attr_condition_record" IS '属性查询的筛选条件记录表';

-- [表9] 共享类型扩展表
DROP TABLE IF EXISTS gis_share_dev_type_ext;
CREATE TABLE gis_share_dev_type_ext (
  id serial8 primary key,
  type_id int8 NOT NULL DEFAULT -1,
  sort_by int4 NOT NULL DEFAULT -1,
  delete_flag bool NOT NULL DEFAULT false,
  create_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_by varchar(32) COLLATE pg_catalog.default NOT NULL DEFAULT ''::character varying,
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN gis_share_dev_type_ext.id IS '主键';
COMMENT ON COLUMN gis_share_dev_type_ext.type_id IS '类型ID';
COMMENT ON COLUMN gis_share_dev_type_ext.sort_by IS '排序';
COMMENT ON COLUMN gis_share_dev_type_ext.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_share_dev_type_ext.create_by IS '创建人';
COMMENT ON COLUMN gis_share_dev_type_ext.create_at IS '创建时间';
COMMENT ON COLUMN gis_share_dev_type_ext.update_by IS '修改人';
COMMENT ON COLUMN gis_share_dev_type_ext.update_at IS '修改时间';
COMMENT ON TABLE gis_share_dev_type_ext is '设备类型扩展表';

-- [表10] 爆管历史记录表
DROP TABLE IF EXISTS gis_pipe_analysis;
CREATE TABLE gis_pipe_analysis
(
  id serial8 primary key,
  code character varying(32) NOT NULL, -- 爆管编号
  name character varying(200) NOT NULL DEFAULT ''::character varying, -- 爆管记录名称
  x numeric NOT NULL, -- 经纬
  y numeric NOT NULL, -- 纬度
  area geometry NOT NULL, -- 爆管影响范围空间信息
  delete_flag boolean NOT NULL DEFAULT false, -- 是否删除
  create_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 创建人
  create_at timestamp without time zone NOT NULL DEFAULT now(), -- 创建时间
  update_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 修改人
  update_at timestamp without time zone NOT NULL DEFAULT now() -- 修改时间
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_pipe_analysis
  OWNER TO postgres;
COMMENT ON TABLE gis_pipe_analysis
  IS '爆管历史记录';
COMMENT ON COLUMN gis_pipe_analysis.code IS '爆管编号';
COMMENT ON COLUMN gis_pipe_analysis.name IS '爆管记录名称';
COMMENT ON COLUMN gis_pipe_analysis.x IS '经纬';
COMMENT ON COLUMN gis_pipe_analysis.y IS '纬度';
COMMENT ON COLUMN gis_pipe_analysis.area IS '爆管影响范围空间信息';
COMMENT ON COLUMN gis_pipe_analysis.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_pipe_analysis.create_by IS '创建人';
COMMENT ON COLUMN gis_pipe_analysis.create_at IS '创建时间';
COMMENT ON COLUMN gis_pipe_analysis.update_by IS '修改人';
COMMENT ON COLUMN gis_pipe_analysis.update_at IS '修改时间';

-- [表11] 爆管历史记录关阀详情表
DROP TABLE IF EXISTS gis_pipe_analysis_valve;
CREATE TABLE gis_pipe_analysis_valve
(
  id serial8 primary key,
  rid bigint NOT NULL, -- 关联爆管记录id
  valve_first character varying(32) NOT NULL DEFAULT ''::character varying, -- 一次关阀列表
  valve_second character varying(32) NOT NULL DEFAULT ''::character varying, -- 二次关阀列表
  valve_failed character varying(32) NOT NULL DEFAULT ''::character varying, -- 关阀失败阀门
  delete_flag boolean NOT NULL DEFAULT false, -- 是否删除
  create_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 创建人
  create_at timestamp without time zone NOT NULL DEFAULT now(), -- 创建时间
  update_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 修改人
  update_at timestamp without time zone NOT NULL DEFAULT now() -- 修改时间
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_pipe_analysis_valve
  OWNER TO postgres;
COMMENT ON TABLE gis_pipe_analysis_valve
  IS '爆管历史记录关阀详情';
COMMENT ON COLUMN gis_pipe_analysis_valve.rid IS '关联爆管记录id';
COMMENT ON COLUMN gis_pipe_analysis_valve.valve_first IS '一次关阀列表';
COMMENT ON COLUMN gis_pipe_analysis_valve.valve_second IS '二次关阀列表';
COMMENT ON COLUMN gis_pipe_analysis_valve.valve_failed IS '关阀失败阀门';
COMMENT ON COLUMN gis_pipe_analysis_valve.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_pipe_analysis_valve.create_by IS '创建人';
COMMENT ON COLUMN gis_pipe_analysis_valve.create_at IS '创建时间';
COMMENT ON COLUMN gis_pipe_analysis_valve.update_by IS '修改人';
COMMENT ON COLUMN gis_pipe_analysis_valve.update_at IS '修改时间';


-- [表12] 供水用户表
DROP TABLE IF EXISTS gis_water_userinfo;
CREATE TABLE gis_water_userinfo
(
  id serial8 primary key,
  userid bigint NOT NULL, -- 用户编号
  usertype character varying(32) NOT NULL DEFAULT ''::character varying, -- 用户类型
  username character varying(32) NOT NULL DEFAULT ''::character varying, -- 用户姓名
  tel character varying(11) NOT NULL DEFAULT ''::character varying, -- 电话
  address character varying(200) NOT NULL DEFAULT ''::character varying, -- 地址
  meterid bigint NOT NULL, -- 绑定水表id
  delete_flag boolean NOT NULL DEFAULT false, -- 是否删除
  create_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 创建人
  create_at timestamp without time zone NOT NULL DEFAULT now(), -- 创建时间
  update_by character varying(32) NOT NULL DEFAULT ''::character varying, -- 修改人
  update_at timestamp without time zone NOT NULL DEFAULT now(), -- 修改时间
  lineid bigint -- 关联线dev_id
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gis_water_userinfo
  OWNER TO postgres;
COMMENT ON TABLE gis_water_userinfo
  IS '供水用户表';
COMMENT ON COLUMN gis_water_userinfo.userid IS '用户编号';
COMMENT ON COLUMN gis_water_userinfo.usertype IS '用户类型';
COMMENT ON COLUMN gis_water_userinfo.username IS '用户姓名';
COMMENT ON COLUMN gis_water_userinfo.tel IS '电话';
COMMENT ON COLUMN gis_water_userinfo.address IS '地址';
COMMENT ON COLUMN gis_water_userinfo.meterid IS '绑定水表id';
COMMENT ON COLUMN gis_water_userinfo.delete_flag IS '是否删除';
COMMENT ON COLUMN gis_water_userinfo.create_by IS '创建人';
COMMENT ON COLUMN gis_water_userinfo.create_at IS '创建时间';
COMMENT ON COLUMN gis_water_userinfo.update_by IS '修改人';
COMMENT ON COLUMN gis_water_userinfo.update_at IS '修改时间';
COMMENT ON COLUMN gis_water_userinfo.lineid IS '关联线dev_id';

-- [13] GIS日志记录
DROP TABLE IF EXISTS gis_trans_log;
CREATE table gis_trans_log(
		id serial8 primary key,
		trans_id char(32) not null default '',
		api_name varchar(64) not null default '',
		api varchar(128) not null default '',
		req_params VARCHAR(512) not null default '',
		return_code varchar(8) not null default -1,
		req_host varchar(16) not null default '',
		operator varchar(16) not null default '',
		cost int4 not null default -1,
		create_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN gis_trans_log.id IS '主键';
COMMENT ON COLUMN gis_trans_log.trans_id IS '交易ID';
COMMENT ON COLUMN gis_trans_log.api_name IS '接口名称';
COMMENT ON COLUMN gis_trans_log.api IS '接口';
COMMENT ON COLUMN gis_trans_log.req_params IS '接口的请求参数';
COMMENT ON COLUMN gis_trans_log.return_code IS '响应码，0-成功，其它失败';
COMMENT ON COLUMN gis_trans_log.req_host IS '客户端host';
COMMENT ON COLUMN gis_trans_log.operator IS '操作人员';
COMMENT ON COLUMN gis_trans_log.cost IS '接口消耗时间，单位ms';
COMMENT ON COLUMN gis_trans_log.create_at IS '创建时间';
COMMENT ON TABLE  gis_trans_log is 'GIS日志记录';

-- [14] 序列生成
DROP TABLE IF EXISTS share_sequence_define;
CREATE TABLE share_sequence_define (
  id serial8 primary key,
  key varchar(32) NOT NULL DEFAULT ''::character varying,
	platform_code varchar(32) NOT NULL DEFAULT ''::character varying,
	val int8 NOT NULL DEFAULT -1,
	polling_interval varchar(32) NOT NULL DEFAULT 'N',
  update_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	remarks varchar(500) NOT NULL DEFAULT ''::character varying
)
;
COMMENT ON COLUMN share_sequence_define.id IS '主键';
COMMENT ON COLUMN share_sequence_define.key IS '自增值的KEY定义';
COMMENT ON COLUMN share_sequence_define.platform_code IS '平台编码（与key建组合唯一约束）';
COMMENT ON COLUMN share_sequence_define.val IS '自增值';
COMMENT ON COLUMN share_sequence_define.polling_interval IS 'Y年、M月、D日、W周、N不轮询';
COMMENT ON COLUMN share_sequence_define.update_at IS '更新时间';
COMMENT ON COLUMN share_sequence_define.remarks IS '备注';
COMMENT ON TABLE share_sequence_define is '序列生成';

-- [15] 导入数据的日志表
DROP TABLE IF EXISTS data_import_log;
CREATE TABLE data_import_log(
	id serial8 primary key,
	dev_id char(12) NOT NULL,
	code varchar(32) not null default '',
	belong_to int8 NOT NULL,
	batch_number  varchar(20) not null default ''
);
COMMENT ON COLUMN data_import_log.id IS '主键';
COMMENT ON COLUMN data_import_log.dev_id IS '设备编号';
COMMENT ON COLUMN data_import_log.code IS '编号';
COMMENT ON COLUMN data_import_log.belong_to IS '数据权限ID';
COMMENT ON COLUMN data_import_log.batch_number IS '批次号';
COMMENT ON TABLE data_import_log IS '导入数据的日志表';

------------------------------增加唯一性约束begin-----------------------
-- 数据字典中，同一类型下不能有重复的名称和值
alter table dict_detail add constraint uk_t_n_v unique(type_id,name,val);
-- 数据字典类型表中，val不能重复
alter table dict_type add constraint uk_dict_type_v unique (val);
-- 序列表中key 和 platform_code 复合唯一
alter table share_sequence_define add constraint uk_seq_kp unique (key,platform_code);
------------------------------增加唯一性约束end-------------------------


------------------------------增加索引begin-----------------------------
------------------------------增加索引end-------------------------------