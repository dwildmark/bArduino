package protocol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class UserTools {

	/**
	 * Adds a user to the database
	 * 
	 * @param user
	 *            Username
	 * @param password
	 *            password
	 */
	public synchronized static void addUser(String user, char[] password) {
		String query = " insert into user_data (username, password_hash)"
				+ " values (?, ?)";
		
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, user);
			preparedStmt.setString(2, PasswordHash.createHash(password));
			preparedStmt.execute();
			conn.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	/**
	 * Checks database to see if user credentials provided exists and is correct
	 * 
	 * @param user
	 *            Username
	 * @param password
	 *            Password
	 * @return True if user exists and password is correct, false otherwise
	 */
	public synchronized static boolean confirmUser(String user, char[] password) {
		String query = "SELECT password_hash FROM user_data WHERE username = '"
				+ user + "';";
		boolean validated = false;
		
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();

			if (PasswordHash.validatePassword(password, rs.getString(1))) {
				validated = true;
			}

			conn.close();
			return validated;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Removes user from database
	 * 
	 * @param user
	 *            Username
	 */
	public synchronized static void removeUser(String user) {
		String query = "DELETE FROM user_data WHERE username = '" + user + "';";
		
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
			conn.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	/**
	 * Changes password in the database
	 * @param user
	 *            Username
	 * @param password
	 *            Password 					
	 */
	public synchronized static void changePassword(String user, char[] password) {
		try {
			String query = "UPDATE user_data SET password_hash='" + PasswordHash.createHash(password) +
					"' WHERE username='" + user + "'";
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
			conn.close();
		}catch (Exception e) {
			JOptionPane.showMessageDialog(null,e.getMessage());
		}
	}
	
	
	public synchronized static ResultSet getAllUsers(){
		String query = "SELECT username,credits,approved FROM user_data";
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			return rs;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return null;
		}
	}
	
	public synchronized static boolean testConnection(){
		String query = "SELECT version()";
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.executeQuery();
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}

	
	public synchronized static Connection getConnection() throws SQLException {
		ServerProtocolParser parser = ServerProtocolParser.getInstance();
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUser(parser.getSqlUserName());
		dataSource.setPassword(parser.getSqlPassword());
		dataSource.setServerName(parser.getSqlServerName());
		dataSource.setDatabaseName(parser.getSqlDatabaseName());
		Connection conn = dataSource.getConnection();
		return conn;
	}
	
	
	
}
