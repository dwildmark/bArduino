package protocol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A Tool for communicating with a MySQL server. Uses credentials stored in
 * {@link ServerProtocolParser}.
 * 
 * @author Jonathan Böcker, Olle Casperson
 *
 */
public class UserTools {

	/**
	 * Adds a user to the database
	 * 
	 * @param user
	 *            Username
	 * @param password
	 *            password
	 */
	public synchronized static boolean addUser(String user, char[] password) {
		String query = " insert into user_data (username, password_hash)"
				+ " values (?, ?)";

		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, user);
			preparedStmt.setString(2, PasswordHash.createHash(password));
			preparedStmt.execute();
			conn.close();
			return true;
		} catch (Exception e) {
			return false;
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
	 * 
	 * @param user
	 *            Username
	 * @param password
	 *            Password
	 */
	public synchronized static void changePassword(String user, char[] password) {
		try {
			String query = "UPDATE user_data SET password_hash='"
					+ PasswordHash.createHash(password) + "' WHERE username='"
					+ user + "'";
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
			conn.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	/**
	 * Adds an amount to provided users credits, can be negative.
	 * 
	 * @param username
	 * @param credit
	 */
	public synchronized static void alterCredits(String username, int credit) {
		String query = "UPDATE user_data set credits = credits + " + credit
				+ " WHERE username = '" + username + "'";
		Connection conn;
		try {
			conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
			conn.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	/**
	 * Returns all users with their credits and approved status in a ResultSet
	 * 
	 * @return A ResultSet with user data
	 */
	public synchronized static ResultSet getAllUsers() {
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

	/**
	 * Returns a users balnce
	 * 
	 * @param user
	 * @return Users credit balance
	 */
	public synchronized static int getCredits(String user) {
		String query = "SELECT credits FROM user_data WHERE username = '"
				+ user + "';";
		int credit = 0;

		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			credit = rs.getInt("credits");
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return credit;
	}

	/**
	 * Resets a users credit balance, and returns whatever was on the account
	 * before resetting.
	 * 
	 * @param username
	 * @return Funds before resetting balance
	 */
	public synchronized static int getRefund(String username) {
		String query = "SELECT credits FROM user_data WHERE username = '"
				+ username + "';";
		int credit = 0;

		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			credit = rs.getInt("credits");
			conn.close();
			alterCredits(username, (0 - credit));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return credit;
	}

	/**
	 * Changes the "approved" status for a user. If not approved, the status
	 * will be approved after method execution etc.
	 * 
	 * @param username
	 */
	public synchronized static void changeApproved(String username) {
		String query = "SELECT approved FROM user_data WHERE username = '"
				+ username + "';";
		String approved;
		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			approved = rs.getString("approved");

			if (approved.equals("yes")) {
				query = "UPDATE user_data set approved = 'no' "
						+ " WHERE username = '" + username + "'";
			} else {
				query = "UPDATE user_data set approved = 'yes' "
						+ " WHERE username = '" + username + "'";
			}

			preparedStmt = conn.prepareStatement(query);
			preparedStmt.execute();
			conn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param user
	 * @return The users "approved" status.
	 */
	public synchronized static String getApproved(String user) {
		String query = "SELECT approved FROM user_data WHERE username = '"
				+ user + "';";
		String approved = null;

		try {
			Connection conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			ResultSet rs = preparedStmt.executeQuery();
			rs.next();
			approved = rs.getString("approved");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return approved;
	}

	/**
	 * A simple method to test the connection with the MySQL server
	 * 
	 * @return True if connection is established, false if not
	 */
	public synchronized static boolean testConnection() {
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

	/**
	 * Returns a {@link Connection} using credentials stored in
	 * {@link ServerProtocolParser}.
	 * 
	 * @return A Connection to the MySQL server
	 * @throws SQLException
	 */
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
