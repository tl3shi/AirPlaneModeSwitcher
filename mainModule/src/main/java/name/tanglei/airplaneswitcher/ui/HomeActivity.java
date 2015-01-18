package name.tanglei.airplaneswitcher.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.dao.DatabaseHelper;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.utils.AirplaneModeUtils;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;
import name.tanglei.airplaneswitcher.utils.Utils;


public class HomeActivity extends OrmLiteBaseActivity<DatabaseHelper>
{

    private final String TAG = HomeActivity.class.getName();

    private TaskListAdapter taskAdapter;
    private ListView taskListView;
    private List<TaskEntity> tasks =  new ArrayList<TaskEntity>();
    private boolean currentAirplaneOn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        UmengUpdateAgent.update(this);
        if(! Utils.getStoredPreferenceRooted(this) && android.os.Build.VERSION.SDK_INT >= 17)//no root
        {
            iCannotDoit();
            return;
        }
        this.setContentView(R.layout.home);
        taskListView = (ListView) this.findViewById(R.id.task_list);
        taskAdapter = new TaskListAdapter(this, tasks, this.getHelper().getTaskDao());
        taskListView.setAdapter(taskAdapter);
        taskListView.setOnCreateContextMenuListener(menuListListener);
        taskListView.setOnItemClickListener(listClickedListener);

        if(Utils.isFirstTime(this))
        {
            if(android.os.Build.VERSION.SDK_INT >= 17 && !Utils.isRooted())
            {
                Utils.setStoredPreferenceRooted(this, false);
                iCannotDoit();
                return;
            }
            Utils.setStoredPreferenceRooted(this, true);//rooted or SDK < 17
            this.queryAllTasks();
            if(this.tasks.size() == 0) //really first time, not update/reinstall(db remain)
            {
                showWelcomeDialog(getString(R.string.firstTimeTipTitle), getString(R.string.firstTimeTipContent));
                //add default 2 tasks
                DatabaseHelper.saveDefaultTasks(this);
            }

        }
    }

    @Override
    protected void onResume()
    {
        this.queryAllTasks();
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
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
            {
                Intent intent = new Intent(this, TaskDetailActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.id_action_help:
                showHelpDialog();
                break;
            case R.id.id_action_about:
                showAboutDialog();
                break;
            case R.id.id_action_toggle:
            {
                Intent i = new Intent(this, ReceivedAction.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(ReceivedAction.ACTION_TAG, !currentAirplaneOn);
                i.putExtra(ReceivedAction.USERACTION_TAG, true);
                this.startActivity(i);
                break;
            }
            case R.id.id_action_operationLog:
            {
                Intent intent  = new Intent(this, OperationLogActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.id_action_feedback:
            {
                FeedbackAgent agent = new FeedbackAgent(this);
                agent.setWelcomeInfo(getString(R.string.feedbackWelcome));
                agent.startFeedbackActivity();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void queryAllTasks()
    {
        try
        {
            List<TaskEntity> listIndb =  this.getHelper().getTaskDao().queryForAll();
            Log.i(TAG, "tasks.hash " + this.tasks.hashCode() + ", tasks count : " + tasks.size());
            //String ids = "";
            //for(int i = 0; i < listIndb.size(); i++)
            //    ids += listIndb.get(i).getId() + " @" + listIndb.get(i).hashCode() +" ,";
            //Log.i(TAG, "ids:  " + ids);
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


    public void iCannotDoit()
    {
        Log.i(TAG, "no root and sdk =" + android.os.Build.VERSION.SDK_INT);

        Utils.showAlertDialog(this, getString(R.string.noRootErrorTitle), getString(R.string.noRootError), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                HomeActivity.this.finish();
                System.exit(0);
            }
        });
    }

    public void showHelpDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getString(R.string.helpTitle));
        alertDialogBuilder.setIcon(R.drawable.ic_action_help);
        TextView txtView = new TextView(this);
        txtView.setTextSize(15f);
        Spanned text = Html.fromHtml(this.getString(R.string.helpContentHhml));
        txtView.setText(text);
        txtView.setClickable(true);
        txtView.setMovementMethod(LinkMovementMethod.getInstance());
        alertDialogBuilder.setView(txtView);

        alertDialogBuilder.setPositiveButton(
                this.getString(R.string.helpOkButton), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    public void showAboutDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(this.getString(R.string.aboutTitle));
        alertDialogBuilder.setIcon(R.drawable.ic_action_about);
        TextView txtView = new TextView(this);
        txtView.setTextSize(15f);
        Spanned text = Html.fromHtml(this.getString(R.string.aboutContentHtml));
        txtView.setText(text);
        txtView.setClickable(true);
        txtView.setMovementMethod(LinkMovementMethod.getInstance());
        alertDialogBuilder.setView(txtView);

        alertDialogBuilder.setPositiveButton(
                this.getString(R.string.aboutOkButton), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        UmengUpdateAgent.setUpdateOnlyWifi(false); //force update
                        UmengUpdateAgent.update(HomeActivity.this);
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    public void showWelcomeDialog(String title, String content) {
        Utils.showInfoDialog(this, title, content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }
}
