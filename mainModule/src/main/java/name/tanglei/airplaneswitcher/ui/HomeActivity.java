package name.tanglei.airplaneswitcher.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.ReceivedAction;
import name.tanglei.airplaneswitcher.utils.AirplaneModeUtils;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;
import name.tanglei.airplaneswitcher.utils.Utils;
import name.tanglei.airplaneswitcher.dao.DatabaseHelper;
import name.tanglei.airplaneswitcher.entity.TaskEntity;


public class HomeActivity extends OrmLiteBaseActivity<DatabaseHelper>
{

    private final String TAG = HomeActivity.class.getName();

    private ArrayAdapter<TaskEntity> taskAdapter;
    private ListView taskListView;
    private List<TaskEntity> tasks =  new ArrayList<TaskEntity>();
    private boolean currentAirplaneOn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home);
        taskListView = (ListView) this.findViewById(R.id.task_list);
        taskAdapter = new TaskListAdapter(this, tasks, this.getHelper().getTaskDao());
        taskListView.setAdapter(taskAdapter);
        taskListView.setOnCreateContextMenuListener(menuListListener);
        taskListView.setOnItemClickListener(listClickedListener);
    }

    @Override
    protected void onResume()
    {
        this.queryAllTasks();
        super.onResume();
    }

    private OnItemClickListener listClickedListener = new OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            Intent intent = new Intent(HomeActivity.this, TaskDetailActivity.class);
            intent.putExtra("alarmTask", tasks.get(position));
            HomeActivity.this.startActivity(intent);
        }
    };


    //task context menu
    ListView.OnCreateContextMenuListener menuListListener = new ListView.OnCreateContextMenuListener()
    {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenuInfo menuInfo) {
            menu.setHeaderTitle(R.string.task_context_menu_title);
            menu.add(0, TASK_CONTEXT_MENU_INFO, 0, R.string.task_context_menu_info);
            menu.add(0, TASK_CONTEXT_MENU_EDIT, 1, R.string.task_context_menu_edit);
            menu.add(0, TASK_CONTEXT_MENU_DELETE, 2, R.string.task_context_menu_delete);
        }
    };


    static final int TASK_CONTEXT_MENU_INFO = Menu.FIRST;
    static final int TASK_CONTEXT_MENU_EDIT = TASK_CONTEXT_MENU_INFO + 1;
    static final int TASK_CONTEXT_MENU_DELETE = TASK_CONTEXT_MENU_EDIT + 1;


    public boolean onContextItemSelected(android.view.MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
            case TASK_CONTEXT_MENU_INFO:
            case TASK_CONTEXT_MENU_EDIT:
                Intent intent = new Intent(this, TaskDetailActivity.class);
                intent.putExtra("alarmTask", tasks.get(menuInfo.position));
                this.startActivity(intent);
                break;
            case TASK_CONTEXT_MENU_DELETE:
                deleteTask(menuInfo.position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        currentAirplaneOn = AirplaneModeUtils.isAirplaneModeOn(this);
        if(currentAirplaneOn)//menu.findItem(MENU_TOGGLE_RIGHTNOW).getTitle().equals(getString(R.string.menuSetOffRightnow)))
            menu.findItem(R.id.id_action_toggle).setTitle(R.string.menuSetOffRightnow);
        else
            menu.findItem(R.id.id_action_toggle).setTitle(R.string.menuSetOnRightnow);
        return true;
    }

    //menu
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_add:
                Intent intent = new Intent(this, TaskDetailActivity.class);
                this.startActivity(intent);
                break;
            case R.id.id_action_settings:
                //Toast.makeText(this, "Settings TODO", Toast.LENGTH_SHORT).show();
                Utils.showToast(this, "Settings TODO");
                break;
            case R.id.id_action_toggle:
                Intent i = new Intent(this, ReceivedAction.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(ReceivedAction.ACTION_TAG, !currentAirplaneOn);
                i.putExtra(ReceivedAction.USERACTION_TAG, true);
                this.startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void queryAllTasks()
    {
        try
        {
            List<TaskEntity> listIndb =  this.getHelper().getTaskDao().queryForAll();
            Log.i(TAG, "tasks.hash " + this.tasks.hashCode() + ", tasks count : " + tasks.size());
            String ids = "";
            for(int i = 0; i < listIndb.size(); i++)
                ids += listIndb.get(i).getId() + " ,";
            Log.i(TAG, "ids:  " + ids);
            this.tasks.clear();
            this.tasks.addAll(listIndb);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "query all tasks failed! " + e.toString());
            this.tasks.clear();
        }
        if(this.taskAdapter != null)
        {
            this.taskAdapter.notifyDataSetChanged();
            Log.i(TAG, "notified adapter!" );
        }
    }

    private void deleteTask(int position)
    {
        final int pos = position;
        String title = this.getString(R.string.home_task_delete_alert_title);
        String content = this.getString(R.string.home_task_delete_alert_content);
        Utils.showAlertDialog(this, title, content, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                TaskEntity task = tasks.get(pos);
                HomeActivity.this.getHelper().getTaskDao().delete(task);
                task.setActive(false); //delete alarm task
                TaskManagerUtils.addOrUpdateTask(HomeActivity.this.getApplicationContext(), task, false);
                HomeActivity.this.queryAllTasks();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }

}
