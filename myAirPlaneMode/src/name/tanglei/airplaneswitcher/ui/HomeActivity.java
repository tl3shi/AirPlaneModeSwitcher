package name.tanglei.airplaneswitcher.ui;

import java.util.ArrayList;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.Utils;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class HomeActivity extends SherlockListActivity
{
    private  ArrayAdapter<TaskEntity> taskAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home);
        
        ListView taskListView = this.getListView();
        
        List<TaskEntity> tasks = new ArrayList<TaskEntity>();
        /*
        tasks.add(new TaskEntity(true, 0x0000001F, "13:00",
                "午休开始", true));
        tasks.add(new TaskEntity(false, 0x0000001F, "14:30",
                "午休结束", true));
        tasks.add(new TaskEntity(true, 0x0000001F, "00:30",
                "睡觉开始", true));
        tasks.add(new TaskEntity(false, 0x0000001F, "07:30",
                "睡觉结束", true));
        tasks.add(new TaskEntity(true, 0x00000060, "14:00",
                "周末午休开始", true));
        */
        tasks.add(new TaskEntity(false, 0x00000060, "16:30",
                "周末午休结束", true));
        tasks.add(new TaskEntity(true, 0x00000060, "02:30",
                "周末睡觉开始", true));
        tasks.add(new TaskEntity(false, 0x00000060, "10:30",
                "周末睡觉结束", true));
        tasks.add(new TaskEntity(false, 0x00000000, "10:30",
                "单次睡觉", false));
        
        taskAdapter = new TaskListAdapter(this, tasks);
        this.setListAdapter(taskAdapter);
        //taskListView.setOnItemLongClickListener(longListClickedListener);
        //taskListView.setOnCreateContextMenuListener(menuListListener); //fail
    }
    
    OnItemLongClickListener longListClickedListener = new OnItemLongClickListener()
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                int position, long id)
        {
            Toast.makeText(HomeActivity.this, "tanglei" + "long click" + position, Toast.LENGTH_SHORT).show();
            return false;
        }
    };
    
    /*
    //task context menu
    ListView.OnCreateContextMenuListener menuListListener = new ListView.OnCreateContextMenuListener()
    {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                ContextMenuInfo menuInfo) 
        {
            Log.e("tanglei", "onCreateContextMenu");
            menu.setHeaderTitle(R.string.task_context_menu_title);
            menu.add(0, TASK_CONTEXT_MENU_INFO, 0, R.string.task_context_menu_info); 
            menu.add(0, TASK_CONTEXT_MENU_EDIT, 1, R.string.task_context_menu_edit); 
            menu.add(0, TASK_CONTEXT_MENU_DELETE, 2, R.string.task_context_menu_delete); 
        }
    };
    */
    
    static final int TASK_CONTEXT_MENU_INFO = Menu.FIRST;
    static final int TASK_CONTEXT_MENU_EDIT = TASK_CONTEXT_MENU_INFO + 1;
    static final int TASK_CONTEXT_MENU_DELETE = TASK_CONTEXT_MENU_EDIT + 1;
    
    /*
    public boolean onContextItemSelected(android.view.MenuItem item) 
    {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            case TASK_CONTEXT_MENU_INFO:
            case TASK_CONTEXT_MENU_EDIT:
                
                break;
            case TASK_CONTEXT_MENU_DELETE:
                break;
        }
        //return true; 
        return super.onContextItemSelected(item);
    }*/
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);  
        Utils.showToast(this, "tanglei" + "click" + position);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getSupportMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    //menu
    public boolean onOptionsItemSelected(MenuItem item) 
    {      
        switch(item.getItemId())
        {      
            case R.id.id_action_add:
                Intent intent = new Intent(this, TaskDetailActivity.class);
                this.startActivity(intent);
                break;      
            case R.id.id_action_settings:
                //Toast.makeText(this, "Settings TODO", Toast.LENGTH_SHORT).show();
                Utils.showToast(this, "Settings TODO");
        }      
        return super.onOptionsItemSelected(item);      
    }  


}
