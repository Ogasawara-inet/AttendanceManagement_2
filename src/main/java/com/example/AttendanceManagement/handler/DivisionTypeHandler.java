package com.example.AttendanceManagement.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.example.AttendanceManagement.util.Division;

public class DivisionTypeHandler extends BaseTypeHandler<Division> {

	/*
	 * 値の格納
	 */
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Division parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.toString());
	}

	
	
	/*
	 * 値の取得
	 */
	
	@Override
	public Division getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return Division.valueOf(rs.getString(columnName));
	}

	@Override
	public Division getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return Division.valueOf(rs.getString(columnIndex));
	}

	@Override
	public Division getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return Division.valueOf(cs.getString(columnIndex));
	}
	
}
