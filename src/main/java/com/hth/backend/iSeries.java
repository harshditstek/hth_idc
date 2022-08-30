package com.hth.backend;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.access.IFSFile;
import com.ibm.as400.access.IFSFileInputStream;
import com.ibm.as400.access.IFSFileOutputStream;

public class iSeries {
	static ApplicationProperties properties = new ApplicationProperties();
	private static final String DRIVER = "com.ibm.as400.access.AS400JDBCDriver";
	private static final String SERVER = "printers.hi-techhealth.com";
	private static final String URL = properties.readProperty("url");
	//private static final String HOSTNAME = "JAVA2018";
	private static final String HOSTNAME = properties.readProperty("hostname");
	private static final String PASSWORD = properties.readProperty("password");

	private static AS400 system = null;

	public static List<String[]> executeSQL(String sql) {
		List<String[]> resultList = new ArrayList<>();
		String[] result;
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		Connection connection = null;

		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, HOSTNAME, PASSWORD);
			statement = connection.createStatement();

			if (sql.substring(0, 6).equalsIgnoreCase("SELECT")) {
				resultSet = statement.executeQuery(sql);
				resultSetMetaData = resultSet.getMetaData();

				while (resultSet.next()) {
					result = new String[resultSetMetaData.getColumnCount()];
					for (int idx = 0; idx < result.length; idx++) {
						result[idx] = resultSet.getString(idx + 1);
					}
					resultList.add(result);
				}
			} else {
				String rowCount = Integer.toString(statement.executeUpdate(sql));
				result = new String[] {rowCount};
				resultList.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}

		return resultList;
	}

	public static List<String[]> executeSQLByAlias(String sql, String alias, String file) {
		String aliasSQL = "CREATE ALIAS " + alias + " FOR " + file;
		List<String[]> resultList = new ArrayList<>();
		String[] result;
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		Connection connection = null;

		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, HOSTNAME, PASSWORD);
			statement = connection.createStatement();
			statement.execute(aliasSQL);

			if (sql.substring(0, 6).equalsIgnoreCase("SELECT")) {
				resultSet = statement.executeQuery(sql);
				resultSetMetaData = resultSet.getMetaData();

				while (resultSet.next()) {
					result = new String[resultSetMetaData.getColumnCount()];
					for (int idx = 0; idx < result.length; idx++) {
						result[idx] = resultSet.getString(idx + 1);
					}
					resultList.add(result);
				}
			} else {
				String rowCount = Integer.toString(statement.executeUpdate(sql));
				result = new String[] {rowCount};
				resultList.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}

		return resultList;
	}


	public static List<String[]> executeSQLByAlias(String sql, String[] alias, String[] file) {
		String[] aliasSQL = new String[alias.length];
		for(int i=0;i<alias.length;i++){
			aliasSQL[i] = "CREATE ALIAS " + alias[i] + " FOR " + file[i];
		}
		List<String[]> resultList = new ArrayList<>();
		String[] result;
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		Connection connection = null;

		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, HOSTNAME, PASSWORD);
			statement = connection.createStatement();
			for(int i=0;i<aliasSQL.length;i++){
				statement.execute(aliasSQL[i]);
			}


			if (sql.substring(0, 6).equalsIgnoreCase("SELECT")) {
				resultSet = statement.executeQuery(sql);
				resultSetMetaData = resultSet.getMetaData();

				while (resultSet.next()) {
					result = new String[resultSetMetaData.getColumnCount()];
					for (int idx = 0; idx < result.length; idx++) {
						result[idx] = resultSet.getString(idx + 1);
					}
					resultList.add(result);
				}
			} else {
				String rowCount = Integer.toString(statement.executeUpdate(sql));
				result = new String[] {rowCount};
				resultList.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}

		return resultList;
	}
	
	
	//-----added to create  alias
	public static void createAlias(String alias, String file){
		String aliasSQL = "CREATE ALIAS " + alias + " FOR " + file;
		List<String[]> resultList = new ArrayList<>();
		String[] result;
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		Connection connection = null;

		try {
			Class.forName(DRIVER);
			connection = DriverManager.getConnection(URL, HOSTNAME, PASSWORD);
			statement = connection.createStatement();
			statement.execute(aliasSQL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public static void executeCL(String clCommand) {
		try {
			if (system == null) {
				system = new AS400(SERVER, HOSTNAME, PASSWORD);
			}

			CommandCall command = new CommandCall(system);
			System.out.println(clCommand);
			command.run(clCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean uploadImage(String path, File file) {
		try {
			if (system == null) {
				system = new AS400(SERVER, HOSTNAME, PASSWORD);
			}

			boolean isReplaced = true;

			FileInputStream inputStream = new FileInputStream(file);

			IFSFile output = new IFSFile(system, path);
			if (!output.exists()) {
				output.createNewFile();
				isReplaced = false;
			}

			IFSFileOutputStream outStream = new IFSFileOutputStream (output);

			byte data[] = new byte[2048 * 4];
			int byteContent;
			while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
				outStream.write(data, 0, byteContent);
			}
			outStream.close();
			inputStream.close();

			return isReplaced;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public static Image[] downloadImages(String[] paths) {
		boolean isLoaded;
		String currPath = "";
		Image[] imgList = new Image[paths.length];
		Image img;

		try {
			if (system == null) {
				system = new AS400(SERVER, HOSTNAME, PASSWORD);
			}

			for (int currIdx = 0; currIdx < paths.length; currIdx++) {
				isLoaded = false;
				img = null;
				if (!paths[currIdx].trim().equals("")) {
					currPath = paths[currIdx];

					for (int prevIdx = currIdx - 1; prevIdx >= 0; prevIdx--) {
						if (paths[prevIdx].equals(currPath)) {
							img = imgList[prevIdx];
							isLoaded = true;
							break;
						}
					}

					if (!isLoaded) {
						IFSFileInputStream imgFile = new IFSFileInputStream(system, currPath);
						img = ImageIO.read(imgFile);
					}
				}
				imgList[currIdx] = img;
				if (img != null) {
					img.flush();
					img = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return imgList;
	}



}
