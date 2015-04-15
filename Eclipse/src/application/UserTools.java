package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import protocol.PasswordHash;
import server.ServerApp;

public class UserTools {

	public synchronized static void addUser(String user, char[] password){
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
	
	public synchronized static boolean confirmUser(String user, char[] password){
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
			return PasswordHash.validatePassword(password, users.getProperty(user));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public synchronized static void removeUser(String user){
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
