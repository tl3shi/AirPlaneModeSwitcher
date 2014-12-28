package name.tanglei.airplaneswitcher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import name.tanglei.airplaneswitcher.entity.TaskEntity;

/**
 * Created by TangLei on 14/12/27.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "tasklist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseHelper.class.getName();
    private Dao<TaskEntity, Integer> taskDao;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource)
    {
        try {
            TableUtils.createTable(connectionSource, TaskEntity.class);
        } catch (SQLException e) {
            Log.e(TAG, "unable create database");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int older, int newer)
    {
        try {
            TableUtils.dropTable(connectionSource, TaskEntity.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG,
                    "Unable to upgrade database from version " + older + " to new "
                            + newer, e);
        }

    }

    public Dao<TaskEntity, Integer> getTaskDao()
    {
        if(taskDao == null)
            try {
                taskDao = getDao(TaskEntity.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return taskDao;
    }

    private List<TaskEntity> getAllTasks()
    {
        try {
            return getTaskDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
