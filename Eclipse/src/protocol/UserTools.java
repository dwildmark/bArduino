package protocol;

import helpers.MyServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import server.ServerApp;

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
		Properties users = new Properties();
		File initialFile = new File(ServerApp.usersFileName);
		InputStream inputStream;

		try {
			inputStream = new FileInputStream(initialFile);
			users.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(ServerApp.usersFileName);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		try {
			FileOutputStream out = new FileOutputStream(ServerApp.usersFileName);
			users.setProperty(user, PasswordHash.createHash(password));
			users.store(out, null);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
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
		Properties users = new Properties();
		File initialFile = new File(ServerApp.usersFileName);
		InputStream inputStream;

		try {
			inputStream = new FileInputStream(initialFile);
			users.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(ServerApp.usersFileName);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
		if (users.getProperty(user) == null) {
			return false;
		}
		try {
			return PasswordHash.validatePassword(password,
					users.getProperty(user));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Removes user from database
	 * 
	 * @param user Username
	 */
	public synchronized static void removeUser(String user) {
		Properties users = new Properties();
		File initialFile = new File(ServerApp.usersFileName);
		InputStream inputStream;
		

		try {
			inputStream = new FileInputStream(initialFile);
			users.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(ServerApp.usersFileName);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		try {
			FileOutputStream out = new FileOutputStream(ServerApp.usersFileName);
			users.remove(user);
			users.store(out, null);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
