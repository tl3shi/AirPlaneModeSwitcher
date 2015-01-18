package name.tanglei.airplaneswitcher.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.entity.TaskEntity;

/**
 * Created by TangLei on 14/12/27.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
    private static final String DATABASE_NAME = "tasklist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = DatabaseHelper.class.getName();
    private RuntimeExceptionDao<TaskEntity, Integer> taskDao;

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

    public RuntimeExceptionDao<TaskEntity, Integer> getTaskDao()
    {
        if(taskDao == null)
            taskDao = this.getRuntimeExceptionDao(TaskEntity.class);
        return taskDao;
    }

    private List<TaskEntity> getAllTasks()
    {
        return getTaskDao().queryForAll();
    }

    public static DatabaseHelper getHelper(Context context)
    {
        return OpenHelperManager.getHelper(context, DatabaseHelper.class);
    }

    public static List<TaskEntity> queryAllTasks(Context context)
    {
        RuntimeExceptionDao<TaskEntity, Integer> dao = getHelper(context).getTaskDao();
        try {
            return dao.queryForAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static TaskEntity query(Context context, int taskid)
    {
        RuntimeExceptionDao<TaskEntity, Integer> dao = getHelper(context).getTaskDao();
        try {
            return (TaskEntity)dao.queryForFirst(dao.queryBuilder().where().eq("_id", Integer.valueOf(taskid)).prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean disableTask(Context context, TaskEntity task)
    {
        RuntimeExceptionDao<TaskEntity, Integer> dao = getHelper(context).getTaskDao();
        task.setActive(false);
        return dao.update(task) == 1;
    }

    public static boolean saveDefaultTasks(Context context)
    {
        try
        {
            RuntimeExceptionDao<TaskEntity, Integer> dao = getHelper(context).getTaskDao();
            dao.create(new TaskEntity(true, TaskEntity.getRepeatWorkday(), 0, 00,
                    context.getResources().getString(R.string.task_default_sleep_name), true));
            dao.create(new TaskEntity(false, TaskEntity.getRepeatWorkday(), 6, 30,
                    context.getResources().getString(R.string.task_default_getup_name), true));
            return true;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
