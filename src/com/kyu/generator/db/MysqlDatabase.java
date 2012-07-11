package com.kyu.generator.db;

import java.sql.Connection;
import java.sql.DriverManager;

import com.kyu.common.Conf;

/**
 * @FileName : MysqlDatabase.java
 * @Project : sample_project
 * @Date : 2012. 7. 9.
 * @작성자 : 이남규
 * @프로그램설명 :
 */
public class MysqlDatabase implements Database {

	/** driver */
	private final String driver;
	/** JDBC URL */
	private final String jdbcUrl;
	/** 유저 아이디 */
	private final String userId;
	/** 패스워드 */
	private final String password;

	/**
	 * 생성자
	 */
	public MysqlDatabase() {
		driver = Conf.getValue("db.mysql.jdbc.driver");
		jdbcUrl = Conf.getValue("db.mysql.jdbc.url");
		userId = Conf.getValue("db.mysql.jdbc.username");
		password = Conf.getValue("db.mysql.jdbc.password");
	}

	/* (non-Javadoc)
	 * @see com.kyu.generator.db.Database#getConnection()
	 */
	@Override
	public Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(jdbcUrl, userId, password);
		} catch (Exception ex) {
			System.out.println("##getConnection## exception failed");
			ex.printStackTrace();
		}

		return conn;
	}
}