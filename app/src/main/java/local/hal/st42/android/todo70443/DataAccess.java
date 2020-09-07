package local.hal.st42.android.todo70443;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class DataAccess {

    public static List<TaskBean> findAll(SQLiteDatabase db,int flag){
        List<TaskBean> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (flag == 1){
            String strFlag = String.valueOf(flag) ;
            sql = "SELECT * FROM tasks WHERE done = ? ORDER BY deadline DESC";
            cursor =db.rawQuery(sql,new String[]{strFlag});
        } else if(flag ==2){
            sql = "SELECT * FROM tasks ORDER BY deadline DESC";
            cursor =db.rawQuery(sql,null);
        }else{
            String strFlag = String.valueOf(flag) ;
            sql = "SELECT * FROM tasks WHERE done = ? ORDER BY deadline ASC";
            cursor =db.rawQuery(sql,new String[]{strFlag});
        }
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String deadline = cursor.getString(cursor.getColumnIndex("deadline"));
                int done = cursor.getInt(cursor.getColumnIndex("done"));
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                TaskBean bean = new TaskBean();
                bean.setName(name);
                bean.setId(id);
                bean.setDeadline(deadline);
                bean.setDone(done);
                bean.setNote(note);
                list.add(bean);
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    public static TaskBean findByPk(SQLiteDatabase db,long id){
        String sql = "SELECT * FROM tasks WHERE _id = ?";
        Cursor cursor = db.rawQuery(sql,new String[]{String.valueOf(id)});
        TaskBean bean = null;
        if(cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String deadline = cursor.getString(cursor.getColumnIndex("deadline"));
            int done = cursor.getInt(cursor.getColumnIndex("done"));
            String note = cursor.getString(cursor.getColumnIndex("note"));
            bean = new TaskBean();
            bean.setName(name);
            bean.setDeadline(deadline);
            bean.setDone(done);
            bean.setNote(note);

        }
        return bean;
    }
    public static long insert(SQLiteDatabase db,TaskBean taskBean){
        String sql = "INSERT INTO tasks(name,deadline,done,note)VALUES(?,?,?,?)";
        SQLiteStatement stmt =db.compileStatement(sql);
        stmt.bindString(1,taskBean.getName());
        stmt.bindString(2,taskBean.getDeadline());
        stmt.bindLong(3,taskBean.getDone());
        stmt.bindString(4,taskBean.getNote());
        return stmt.executeInsert();
    }

    public static int update(SQLiteDatabase db,TaskBean taskBean,long id){
        String sql = "UPDATE tasks SET name = ?, deadline = ?,done = ?, note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1,taskBean.getName());
        stmt.bindString(2,taskBean.getDeadline());
        stmt.bindLong(3,taskBean.getDone());
        stmt.bindString(4,taskBean.getNote());
        stmt.bindLong(5,id);
        return stmt.executeUpdateDelete();
    }

    public static int delete(SQLiteDatabase db,long id){
        String sql = "DELETE FROM tasks WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1,id);
        return stmt.executeUpdateDelete();
    }

    public static void changeDoneChecked(SQLiteDatabase db, long id, boolean isChecked) {
        String sql = "UPDATE tasks SET done = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        if(isChecked) {
            stmt.bindLong(1, 1);
        }else {
            stmt.bindLong(1, 0);
        }
        stmt.bindLong(2, id);
        stmt.executeUpdateDelete();
    }
}
