package com.mj.imagedownloader;


import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
	
public class SqliteManager {
	
	public static final String DATABASE_PATH = Container.ROOT_DIR + "/tumblr.db";
	private static SqliteManager mInstance = null;
	private int mTimeout = 30;
	private Connection mConnection = null;
	
    public static final String PHOTO_TABLE_NAME = "photo";
    
	private SqliteManager() {
		
	}
	
	public boolean isCreated() {
		File file = new File(DATABASE_PATH);
		
		return file.exists();
	}
	
	public static SqliteManager getInstance() {
		if (mInstance == null) {
			mInstance = new SqliteManager();
			if (!mInstance.connect(DATABASE_PATH))
				return null;
		}
		
		return mInstance;
	}
	
	public boolean connect(String dbPath) {
		try {
			Class.forName("org.sqlite.JDBC");
			mConnection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_PATH);
		} catch (ClassNotFoundException e) {
			System.err.print("Error org.sqlite.JDBC not found:\n");
			e.printStackTrace();
			return false;
		} catch (SQLException e) {
			System.err.print("Error connect error:\n");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean createDataBase() {
        String tumblr_table_create =
                "CREATE TABLE " + PHOTO_TABLE_NAME + " (" +
                " _id          INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " TUMBLR_ID	   INTEGER  NOT NULL, " +
                " NAME         TEXT     NOT NULL, " +
                " DATE         DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                " PATH         TEXT     NOT NULL, " +
                " TYPE	       TEXT     NOT NULL, " +
                " SIZE		   INTEGER  NOT NULL )";
        
        return sqliteUpdate(tumblr_table_create);
	}
	
	private boolean sqliteUpdate(String sql) {
        Statement stmt = null;
        
		try {
			stmt = mConnection.createStatement();
            stmt.setQueryTimeout(mTimeout);
            stmt.executeUpdate(sql); 
		} catch (SQLException e) {
			System.err.print("Error insert database error:\n");
			e.printStackTrace();
			return false;
		}finally {
            try { stmt.close(); } catch (Exception ignore) {}
		}
		
		return true;
	}
	
	public boolean insert(TumblrPost post) {
		System.out.print("Insert sql:" + post.getInsertSql());
		
		return sqliteUpdate(post.getInsertSql());
	}
	
	public void delete() {
		
	}
	
	public boolean isInserted(Long tumblrId, String fileName, String type) {
		String sql = "SELECT COUNT(*) from " + PHOTO_TABLE_NAME + 
				" WHERE TUMBLR_ID = '" + tumblrId + "'" +
				" AND NAME = '" + fileName + "'" +
				" AND TYPE = '" + type + "';";
		
		ResultSet res = doSelect(sql);
		int count = 0;
		try {
			while(res.next()) {
				count = res.getInt("COUNT");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return count > 0 ? true : false;
	}
	
	private ResultSet doSelect(String sql) {
        Statement stmt = null;
        ResultSet rs = null;
		try {
			stmt = mConnection.createStatement();
            stmt.setQueryTimeout(mTimeout);
            rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			System.err.print("Error create database error:\n");
			e.printStackTrace();
			return null;
		}finally {
            try { stmt.close(); } catch (Exception ignore) {}
		}		
		return rs;
	}
	
	public List<TumblrPost> select(String type, Long maxId) {
		String sql = "SELECT _id, TUMBLR_ID, NAME, DATE, PATH, SIZE from " + PHOTO_TABLE_NAME + " WHERE " + ""
				+ " TYPE = '" + type + "'";
		
		if (maxId > 0)
			sql += " AND ID >= '" + maxId + "'";
		
		sql += ";";
		
		ArrayList<TumblrPost> posts = new ArrayList<TumblrPost>();
		
	   	try {
	   		ResultSet res = doSelect(sql);
	   		while (res.next()) {
	   			TumblrPost post = new TumblrPost();
				post.setId(res.getLong("_id"));
				post.setTumblrId(res.getLong("TUMBLR_ID"));
				post.setFileName(res.getString("NAME"));
				post.setDate(res.getDate("DATE").toString());
				post.setPath(res.getString("PATH"));
				post.setType(res.getString("TYPE"));
				post.setFileSize(res.getInt("SIZE"));
				posts.add(post);
	   		}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	   	
	   	return posts;
	}
	
	public void modify() {
		
	}
	
	  public static void main( String args[] )
	  {
	      SqliteManager sqlite = SqliteManager.getInstance();
	      if (sqlite != null) {
	    	  System.out.println("Opened database successfully\n");
	    	  if (sqlite.createDataBase())
	    			System.out.println("create database successfully\n");
	      }
	  }
}
