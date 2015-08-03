package com.echo5bravo.govre.DAL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.echo5bravo.govre.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteFactoryAdapter extends SQLiteOpenHelper{
		protected static final String TAG = "SQLiteFactoryAdapter";
	    private static String DATABASE_PATH = "";
	    private static final String DATABASE_NAME = "dbgovre.db"; 
	    private static final int SCHEMA_VERSION = 4; 
	    private SQLiteDatabase myDataBase;
	    private final Context myContext;	 
	    private static SQLiteFactoryAdapter mDBConnection;    
	 
	    /**
	     * Constructor
	     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	     * @param context
	     */
	    protected SQLiteFactoryAdapter(Context context) {
	        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	        this.myContext = context;
	        DATABASE_PATH = "/data/data/"
	                + context.getApplicationContext().getPackageName()
	                + "/databases/";
	        // The Android's default system path of your application database is
	        // "/data/data/mypackagename/databases/"
	    }
	 
	    /**
	     * getting Instance
	     * @param context
	     * @return SQLiteFactoryAdapter
	     */
	    public static synchronized SQLiteFactoryAdapter getSQLiteFactoryAdapterInstance(Context context) {
	        if (mDBConnection == null) {
	            mDBConnection = new SQLiteFactoryAdapter(context);
	        }
	        return mDBConnection;
	    }
	 
	    /**
	     * Creates an empty database on the system and rewrites it with your own database.
	     **/
	    public void createOrUseDataBase() throws IOException {
	    	
	    	 boolean dbExist = checkDataBase();
		        if (!dbExist) {
		        			        	
		        	InputStream inputStream = myContext.getResources().openRawResource(R.raw.dbscript_v2_0_1_0); 
		        	BufferedReader br = new BufferedReader(new InputStreamReader(inputStream), 1016688); //1016688 max 
		        	String buffer;
		        	try {
			        	createTempDataBase();	
			        	myDataBase = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH + DATABASE_NAME, null);
			        	myDataBase.beginTransaction();
			        	
			        	//openDataBase();
						while ((buffer = br.readLine()) != null) 
			        	{ 
			        			String[] execSql = buffer.split("\n");  
			        			execMultipleSQL(myDataBase, execSql);
			        			        			
			        	} 
						myDataBase.setTransactionSuccessful();		
							//close();			    	
							//Log.d(TAG, "onCreated sql: CREATED TABLES and INSERTED RECORDS");
		        	} catch (SQLException e) {
						//Log.e("Error creating tables and debug data", e.toString());
					} finally {
						myDataBase.endTransaction();
						myDataBase.close();
					}
		        } 
	    }
	    
	    /**
	     * Creates a empty database on the system and rewrites it with your own database. 
	     * */	    
	    private void createTempDataBase(){ //throws IOException{
	        
	        boolean dbExist = checkDataBase();
	        if(!dbExist){
	        	//By calling this method an empty database will be created into the default system path
		        //of your application so we are going be able to overwrite that database with our database.
		        this.getReadableDatabase(); 
	        }              
	    }
	    
	    /**
		 * Execute all of the SQL statements in the String[] array
		 * @param db The database on which to execute the statements
		 * @param sql An array of SQL statements to execute
		 */
		private void execMultipleSQL(SQLiteDatabase db, String[] sql){
			for( String s : sql )
				if (s.trim().length()>0)
					db.execSQL(s);
		}	
	 
	    /**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase() {
	        SQLiteDatabase checkDB = null;
	        try {
	            String myPath = DATABASE_PATH + DATABASE_NAME;
	            checkDB = SQLiteDatabase.openDatabase(myPath, null,
	                    SQLiteDatabase.OPEN_READONLY);
	 
	        } catch (SQLiteException e) {
	        	//Log.e("SQLLiteFactoryAdapter checkDataBase Error!", e.toString());
	        }
	        if (checkDB != null) {
	            checkDB.close();
	        }
	        return checkDB != null ? true : false;
	    }
	    
//------------ CODE NOT IN USE ----------------------------------------------------	 
//	    /**
//	     * Copies your database from your local assets-folder to the just created
//	     * empty database in the system folder, from where it can be accessed and
//	     * handled. This is done by transfering bytestream.
//	     * */
//	    private void copyDataBase() throws IOException {
//	            // Open your local db as the input stream
//	        InputStream myInput = myContext.getAssets().open(DB_NAME);
//	            // Path to the just created empty db
//	        String outFileName = DB_PATH + DB_NAME;
//	            // Open the empty db as the output stream
//	        OutputStream myOutput = new FileOutputStream(outFileName);
//	            // transfer bytes from the inputfile to the outputfile
//	        byte[] buffer = new byte[1024];
//	        int length;
//	        while ((length = myInput.read(buffer)) > 0) {
//	            myOutput.write(buffer, 0, length);
//	        }
//	            // Close the streams
//	        myOutput.flush();
//	        myOutput.close();
//	        myInput.close();
//	    }
//------------ CODE NOT IN USE ----------------------------------------------------	
	 
	    /**
	     * Open the database
	     * @throws SQLException
	     */
	    public void openDataBase() throws SQLException {
	    	
	    	try
			{	
	    		/* When the database is 1st installed on the device, this helper class creates a blank database using getWritableDatabase().
	    		 * The actual database is then populated using the SQLiteDatabase.openDatabase(PATH, null, Open_ReadWrite) to exec the sql.
	    		 * 
	    		 * PROBLEM - SQLiteDatabase.openDatabase() Does not call onUpgrade() so we're 
	    		 * unable to update the database for future versions
	    		 * 
	    		 * SOLUTION - Check to see if the dbExists, if not use SQLiteDatabase.openDatabase() 
	    		 * because it will create the DB from scripts, we don't care about onUpgrade();
	    		 * If the db does exist, call getWritableDatabase() to invoke onUpgrade checks for 
	    		 * future releases where the SCHEMA_VERSION is greater than the current db SCHEMA_VERSION
	    		 */
	    		boolean dbExist = checkDataBase();
	 	        if(!dbExist){
	 	        	
	 	        	/* Calls: DOES NOT call onUpgrade()
	 	        	 * Errors if db exists and there are db changes (New Columns/Tables), so use the below getWritableDatabase() instead	 	        	 */
	 	        	String myPath = DATABASE_PATH + DATABASE_NAME;
		        	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	 	        }
	 	        else
	 	        {
	 	        	/* Calls: onUpgrade()
	 	        	 * Errors if db does not exist, so use the above instead	 	        	 */
	 	        	myDataBase = this.getWritableDatabase(); 	  	        	
	 	        }
	    	}
			catch (Exception e) {
				//Log.e(TAG, "Error SQLiteFactoryAdapter openDataBase " + e.toString());
			}
	    }
	 
	    /**
	     * Close the database if exist
	     */
	    @Override
	    public synchronized void close() {
	        if (myDataBase != null)	        
	            myDataBase.close();
	        super.close();
	    }
	    
	    /**
	     * Call on creating data base for example for creating tables at run time
	     */
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    }
	 
	    /**
	     * can used for drop tables then call onCreate(db) function to create tables again - upgrade
	     */
	   
		@Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
	    	//TODO: Write DB Update logic.
	    	
	    	SQLiteDatabase upgradeDB = null;
	    	
	    	if (newVersion > oldVersion){	    	
	    		//Log.e(TAG, "NEWER VERSION DETECTED, DATABASE UPGRADE REQUIRED!");	    		
	    	
	    		InputStream inputStream = myContext.getResources().openRawResource(R.raw.dbscript_v2_0_1_0); 
	        	BufferedReader br2 = new BufferedReader(new InputStreamReader(inputStream), 1016688); //1016688 max 
	        	String buffer;
	        	try {
	      
	        		db.beginTransaction();
		        	
		        	//openDataBase();
					while ((buffer = br2.readLine()) != null) 
		        	{ 
		        			String[] execSql = buffer.split("\n");  
		        			execMultipleSQL(db, execSql);
		        			        			
		        	} 
					db.setTransactionSuccessful();		
					    	
					//Log.d(TAG, "onCreated sql: CREATED TABLES and INSERTED RECORDS");
	        	} catch (SQLException e) {
					//Log.e("Error creating tables and debug data", e.toString());
				} catch (IOException e) {
					//e.printStackTrace();
				} finally {
					db.endTransaction();
					//db.close();
				}
	    	
	    	}
	    	else
	    	{
	    		//Log.e(TAG, "NO DATABASE UPGRADE DETECTED"); 
	    	}
	    }
	 
	    // ----------------------- CRUD Functions ------------------------------
	 
	    /**
	     * This function used to select the records from DB.
	     * @param tableName
	     * @param tableColumns
	     * @param whereClase
	     * @param whereArgs
	     * @param groupBy
	     * @param having
	     * @param orderBy
	     * @return A Cursor object, which is positioned before the first entry.
	     */
	    public Cursor selectRecordsFromDB(String tableName, String[] tableColumns,
	            String whereClase, String whereArgs[], String groupBy,
	            String having, String orderBy) {
	        return myDataBase.query(tableName, tableColumns, whereClase, whereArgs,
	                groupBy, having, orderBy);
	    }
	 
	    /**
	     * This function used to update the Record in DB.
	     * @param tableName
	     * @param initialValues
	     * @param whereClause
	     * @param whereArgs
	     * @return 0 in case of failure otherwise return no of row(s) are updated
	     */
	    public int updateRecordsInDB(String tableName,
	            ContentValues initialValues, String whereClause, String whereArgs[]) {
	        return myDataBase.update(tableName, initialValues, whereClause, whereArgs);  
	    }
	    
	    /**
	     * This function used to insert the Record in DB.
	     * @param tableName
	     * @param initialValues
	     * @return 0 in case of failure otherwise return no of row(s) are updated
	     */
	    public void insertRecordsInDB(String tableName, ContentValues value) {
	        myDataBase.insert(tableName, null, value);
	    }
	 
	    /**
	     * This function used to delete the Record in DB.
	     * @param tableName
	     * @param whereClause
	     * @param whereArgs
	     * @return 0 in case of failure otherwise return no of row(s) are deleted.
	     */
	    public int deleteRecordInDB(String tableName, String whereClause,
	            String[] whereArgs) {
	        return myDataBase.delete(tableName, whereClause, whereArgs);
	    }
	 
	    // --------------------- Select Raw Query Functions ---------------------
	 
	    /**
	     * apply raw Query
	     * @param query
	     * @param selectionArgs
	     * @return Cursor
	     */
	    public Cursor selectRecordsFromDB(String query, String[] selectionArgs) {
	        
	        try
			{	
	        	if (myDataBase == null){
	        		//Log.e(TAG, "DATABASE DOES NOT EXIST!");
	        	}
	        	else
	        	{
	        	 return myDataBase.rawQuery(query, selectionArgs);
	        	}
			}
			catch (Exception e) {
				//Log.e(TAG, "Error " + e.toString());
			}
	        Cursor cur = null;;
			return cur;	        
	    }	 
	}
	
	
