-- [表1] 设备信息表
DROP TABLE IF EXISTS share_dev;
CREATE TABLE share_dev (
  id serial8 primary key,
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
  dev_id int8 NOT NULL,
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
CREATE TABLE gis_measurement
(
  id bigint NOT NULL DEFAULT nextval('gis_measurement_id_seq'::regclass), -- id
  name character varying(32) NOT NULL DEFAULT ''::character varying, -- 名称
  meatured_value numeric NOT NULL DEFAULT (-1), -- 测量值
  remark character varying(254) NOT NULL DEFAULT ''::character varying, -- 备注
  geom geometry NOT NULL, -- 空间信息
  CONSTRAINT gis_measurement_pkey PRIMARY KEY (id)
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


------------------------------增加外键约束begin-------------------------
------------------------------增加外键约束end---------------------------


------------------------------增加唯一性约束begin-----------------------
--- 数据字典中，同一类型下不能有重复的名称
alter table dict_detail add constraint uk_t_n_v unique(type_id,name,val);
alter table gis_dev_tpl_attr add constraint uk_tpl_idx unique(type_id,idx);
------------------------------增加唯一性约束end-------------------------


------------------------------增加索引begin-----------------------------
------------------------------增加索引end-------------------------------