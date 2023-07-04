package com.example.AttendanceManagement.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.example.AttendanceManagement.util.Auth;

public class AuthTypeHandler extends BaseTypeHandler<Auth>{

	/*
	 * 値の格納
	 */
	
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Auth parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.toString());
	}

	
	
	/*
	 * 値の取得
	 */
	
	@Override
	public Auth getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return Auth.valueOf(rs.getString(columnName));
	}

	@Override
	public Auth getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return Auth.valueOf(rs.getString(columnIndex));
	}

	@Override
	public Auth getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return Auth.valueOf(cs.getString(columnIndex));
	}

}
