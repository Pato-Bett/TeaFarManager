package Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.teafarmanager.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database information
    private static final String DATABASE_NAME = "Emp_Details.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_EMPLOYEES = "Employees";
    private static final String TABLE_PLUCKING_RECORDS = "PluckingRecords";

    // Column names for Employees table
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_IDNUMBER = "idNumber";
    private static final String COLUMN_TOTAL_WEIGHT = "total_weight";
    private static final String COLUMN_TOTAL_OWED_AMOUNT = "total_owed_amount";
    private static final String COLUMN_TOTAL_AMOUNT_PAID = "total_amount_paid";
    private static final String COLUMN_BALANCE = "balance";

    // Column names for PluckingRecords table
    private static final String COLUMN_EMPLOYEE_ID = "employee_id";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_OWED_AMOUNT = "owedAmount";
    private static final String COLUMN_AMOUNT_PAID= "amountPaid";
    private static final String COLUMN_PLUCKING_DATE = "plucking_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        getReadableDatabase();
//        getWritableDatabase();
        createEmployeesTable(db);
        createPluckingRecordsTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    private void createEmployeesTable(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_EMPLOYEES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_IDNUMBER + " INTEGER,"
                + COLUMN_TOTAL_WEIGHT + " REAL,"
                + COLUMN_TOTAL_OWED_AMOUNT + " REAL,"
                + COLUMN_TOTAL_AMOUNT_PAID + " REAL,"
                + COLUMN_BALANCE + " REAL)";
        db.execSQL(createTableQuery);
    }

    private void createPluckingRecordsTable(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PLUCKING_RECORDS + " ("
                + COLUMN_EMPLOYEE_ID + " INTEGER,"
                + COLUMN_WEIGHT + " REAL,"
                + COLUMN_OWED_AMOUNT + " REAL,"
                + COLUMN_AMOUNT_PAID + " REAL,"
                + COLUMN_PLUCKING_DATE + " DATE,"
                + "FOREIGN KEY (" + COLUMN_EMPLOYEE_ID + ") REFERENCES " + TABLE_EMPLOYEES + "(" + COLUMN_ID + "))";
        db.execSQL(createTableQuery);
    }

    public void insertUser(String username, int idNumber, double totalWeight, double totalAmount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_IDNUMBER, idNumber);
        values.put(COLUMN_TOTAL_WEIGHT, totalWeight);
        values.put(COLUMN_TOTAL_OWED_AMOUNT, totalAmount);
        values.put(COLUMN_TOTAL_AMOUNT_PAID, totalAmount);
        db.insert(TABLE_EMPLOYEES, null, values);
        db.close();
    }

    public boolean checkLogin(String username, String idNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EMPLOYEES + " WHERE " +
                COLUMN_USERNAME + " = ? AND " +
                COLUMN_IDNUMBER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, idNumber});
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        return isValid;
    }

    public void insertPluckingRecord(int employeeId, double weight, double owedAmount, double amountPaid, String pluckingDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMPLOYEE_ID, employeeId);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_OWED_AMOUNT, owedAmount);
        values.put(COLUMN_AMOUNT_PAID, amountPaid);
        values.put(COLUMN_PLUCKING_DATE, pluckingDate);
        db.insert(TABLE_PLUCKING_RECORDS, null, values);
        db.close();
    }

    public void updateTotals() {
        SQLiteDatabase db = getWritableDatabase();

        String query = " SELECT " + COLUMN_EMPLOYEE_ID + ", SUM( " + COLUMN_WEIGHT + ") AS total_weight, SUM(" + COLUMN_OWED_AMOUNT + ") AS total_owed_amount, SUM(" + COLUMN_AMOUNT_PAID + ") AS total_amount_paid FROM " +  TABLE_PLUCKING_RECORDS + " GROUP BY " + COLUMN_EMPLOYEE_ID;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int employeeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMPLOYEE_ID));
                double totalWeight = cursor.getDouble(cursor.getColumnIndexOrThrow("total_weight"));
                double totalOwedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("total_owed_amount"));
                double totalAmountPaid = cursor.getDouble(cursor.getColumnIndexOrThrow("total_amount_paid"));
                double balance = totalOwedAmount - totalAmountPaid;

                ContentValues values = new ContentValues();
                values.put(COLUMN_TOTAL_WEIGHT, totalWeight);
                values.put(COLUMN_TOTAL_OWED_AMOUNT, totalOwedAmount);
                values.put(COLUMN_TOTAL_AMOUNT_PAID, totalAmountPaid);
                values.put(COLUMN_BALANCE, balance);

                db.update(TABLE_EMPLOYEES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(employeeId)});
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EMPLOYEES, null, null, null, null, null, null);

        int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
        int weightIndex = cursor.getColumnIndex(COLUMN_TOTAL_WEIGHT);
        int owedAmountIndex = cursor.getColumnIndex(COLUMN_TOTAL_OWED_AMOUNT);
        int paidAmountIndex = cursor.getColumnIndex(COLUMN_TOTAL_AMOUNT_PAID);
        int balanceIndex = cursor.getColumnIndex(COLUMN_BALANCE);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String username = (usernameIndex != -1) ? cursor.getString(usernameIndex) : "";
                double totalWeight = (weightIndex != -1) ? cursor.getDouble(weightIndex) : 0.0;
                double totalOwedAmount = (owedAmountIndex != -1) ? cursor.getDouble(owedAmountIndex) : 0.0;
                double totalPaidAmount = (paidAmountIndex != -1) ? cursor.getDouble(paidAmountIndex) : 0.0;
                double balance = (balanceIndex != -1) ? cursor.getDouble(balanceIndex) : 0.0;

                User user = new User(username, totalWeight, totalOwedAmount, totalPaidAmount, balance);
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }

    public int getUserIdByUsername(String username) {
        int employeeId = -1;
        SQLiteDatabase db = getReadableDatabase();

        if (db != null) {
            String selectQuery = "SELECT " + COLUMN_ID + " FROM " + TABLE_EMPLOYEES + " WHERE " + COLUMN_USERNAME + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{username});

            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(COLUMN_ID);
                if (columnIndex != -1) {
                    employeeId = cursor.getInt(columnIndex);
                }
            }

            cursor.close();
            db.close();
        }

        return employeeId;
    }

    public void updatePluckingRecord(int employeeId, double weight, double owedAmount,double amountPaid, String pluckingDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_OWED_AMOUNT, owedAmount);
        values.put(COLUMN_AMOUNT_PAID, amountPaid);
        String whereClause = COLUMN_EMPLOYEE_ID + " = ? AND " + COLUMN_PLUCKING_DATE + " = ?";
        String[] whereArgs = {String.valueOf(employeeId), pluckingDate};
        db.update(TABLE_PLUCKING_RECORDS, values, whereClause, whereArgs);
        db.close();
    }

    public double getWeightByDate(int employeeId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        double weight = 0.0;

        String query = "SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_PLUCKING_RECORDS +
                " WHERE " + COLUMN_EMPLOYEE_ID + " = ? AND " + COLUMN_PLUCKING_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(employeeId), date});

        if (cursor.moveToFirst()) {
            weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
        }

        cursor.close();
        db.close();

        return weight;
    }

    public double getOwedAmountByDate(int employeeId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        double owedAmount = 0.0;

        String query = "SELECT " + COLUMN_OWED_AMOUNT + " FROM " + TABLE_PLUCKING_RECORDS +
                " WHERE " + COLUMN_EMPLOYEE_ID + " = ? AND " + COLUMN_PLUCKING_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(employeeId), date});

        if (cursor.moveToFirst()) {
            owedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_OWED_AMOUNT));
        }

        cursor.close();
        db.close();

        return owedAmount;
    }
    public double getAmountPaidByDate(int employeeId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        double amountPaid = 0.0;

        String query = "SELECT " + COLUMN_AMOUNT_PAID + " FROM " + TABLE_PLUCKING_RECORDS +
                " WHERE " + COLUMN_EMPLOYEE_ID + " = ? AND " + COLUMN_PLUCKING_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(employeeId), date});

        if (cursor.moveToFirst()) {
            amountPaid = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT_PAID));
        }

        cursor.close();
        db.close();

        return amountPaid;
    }

    public boolean checkPluckingRecordExists(int employeeId, String pluckingDate) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_WEIGHT, COLUMN_OWED_AMOUNT};
        String selection = COLUMN_EMPLOYEE_ID + " = ? AND " + COLUMN_PLUCKING_DATE + " = ?";
        String[] selectionArgs = {String.valueOf(employeeId), pluckingDate};
        String groupBy = COLUMN_PLUCKING_DATE;
        String having = null;
        String orderBy = null;
        String limit = null;

        Cursor cursor = db.query(TABLE_PLUCKING_RECORDS, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        boolean recordExists = cursor.moveToFirst();
        cursor.close();
        return recordExists;
    }

}