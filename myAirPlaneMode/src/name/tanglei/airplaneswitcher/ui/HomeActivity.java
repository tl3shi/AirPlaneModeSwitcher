package name.tanglei.airplaneswitcher.ui;

import java.util.ArrayList;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.Utils;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HomeActivity extends Activity
{
    private ArrayAdapter<TaskEntity> taskAdapter;
    private ListView taskListView ;
    private List<TaskEntity> tasks;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home);
        
        taskListView = (ListView) this.findViewById(R.id.task_list);
        
        tasks = new ArrayList<TaskEntity>();
        tasks.add(new TaskEntity(false, 0x00000060,  Utils.getCalendar(16, 30),
                "周末午休结束", true));
        tasks.add(new TaskEntity(true, 0x00000060, Utils.getCalendar(02, 30),
                "周末睡觉开始", true));
        tasks.add(new TaskEntity(false, 0x00000060, Utils.getCalendar(10, 30),
                "周末睡觉结束", true));
        tasks.add(new TaskEntity(false, 0x00000000, Utils.getCalendar(12, 30),
                "单次睡觉", false));
        
        taskAdapter = new TaskListAdapter(this, tasks);
        taskListView.setAdapter(taskAdapter);
        taskListView.setOnCreateContextMenuListener(menuListListener); 
        taskListView.setOnItemClickListener(listClickedListener);
    }
    
    private OnItemClickListener listClickedListener = new OnItemClickListener()
    {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
                int position, long id)
		{
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
                ContextMenuInfo menuInfo) 
        {
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
        switch(item.getItemId())
        {
            case TASK_CONTEXT_MENU_INFO:
            case TASK_CONTEXT_MENU_EDIT:
            	  Intent intent = new Intent(this, TaskDetailActivity.class);
            	  intent.putExtra("alarmTask", tasks.get(menuInfo.position));
                  this.startActivity(intent);
                break;
            case TASK_CONTEXT_MENU_DELETE:
            	Utils.showToast(this, "Delete TODO");
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
