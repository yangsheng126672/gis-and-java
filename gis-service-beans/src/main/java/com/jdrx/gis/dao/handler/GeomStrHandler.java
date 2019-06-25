package com.jdrx.gis.dao.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.postgis.PGgeometry;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Create by dengfan at 2019/6/21 0021 14:32
 */
@MappedJdbcTypes(JdbcType.OTHER)
public class GeomStrHandler extends BaseTypeHandler<String> {
    private static final PGgeometry GEOM = new PGgeometry();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String geom, JdbcType jdbcType) throws SQLException {
        GEOM.setValue(geom);

        ps.setObject(i, GEOM);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
//        String str = g.getValue();
//        String geomStr = rs.getString(columnName);
//        GEOM.setValue(geomStr);
//        GEOM.
//        return GEOM.getValue();
        PGgeometry geom = (PGgeometry) rs.getObject(columnName);
        return geom.getValue();
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//        String geomStr = rs.getString(columnIndex);
//        GEOM.setValue(geomStr);
//        return GEOM.getValue();
        PGgeometry geom = (PGgeometry) rs.getObject(columnIndex);
        return geom.getValue();
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//        String geomStr = cs.getString(columnIndex);
//        GEOM.setValue(geomStr);
//        return GEOM.getValue();

        PGgeometry geom = (PGgeometry) cs.getObject(columnIndex);
        return geom.getValue();
    }
}
