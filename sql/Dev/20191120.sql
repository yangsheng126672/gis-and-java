ALTER TABLE public.gis_dev_tpl_attr
  ADD COLUMN fill boolean NOT NULL DEFAULT false;
COMMENT ON COLUMN gis_dev_tpl_attr.fill IS '是否必填';