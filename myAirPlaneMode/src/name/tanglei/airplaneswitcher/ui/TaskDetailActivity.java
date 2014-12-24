package name.tanglei.airplaneswitcher.ui;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.Utils;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;


public class TaskDetailActivity extends SherlockFragmentActivity
{
    public static String TAG = TaskDetailActivity.class.getName();
    
    private TaskEntity alarm;
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Log.i(TAG, "created");
        //this.setContentView(R.layout.task_detail);
        this.setContentView(R.layout.edit_alarm);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        alarm = alarm == null ? Utils.createTask(this) : alarm;
        
        int[] repeat_lines = new int[]{R.id.repeat_monday_line, R.id.repeat_tuesday_line, R.id.repeat_wendsday_line,
                R.id.repeat_thursday_line, R.id.repeat_friday_line, R.id.repeat_saturday_line, R.id.repeat_sunday_line};
        int[] repeat_txt = new int[]{R.id.repeat_monday, R.id.repeat_tuesday, R.id.repeat_wendsday,
                R.id.repeat_thursday, R.id.repeat_friday, R.id.repeat_saturday, R.id.repeat_sunday};
        for(int i = 0; i < 7; i++)
        {
            FrameLayout localFrameLayout = (FrameLayout)this.findViewById(repeat_lines[i]);
            this.setUpRepeat(localFrameLayout, (TextView)this.findViewById(repeat_txt[i]), i+1);
        }
        
        TextView txtTimeView = (TextView) this.findViewById(R.id.edit_alarm_hour);
        TextView txtTaskName = (TextView) this.findViewById(R.id.edit_alarm_label);
        
        txtTimeView.setText(this.alarm.getTime());
        txtTaskName.setText(this.alarm.getTitle());
        
        RadioButton radioOn = (RadioButton) this.findViewById(R.id.idRadioOn);
        RadioButton radioOff = (RadioButton) this.findViewById(R.id.idRadioOff);
        if(this.alarm.isModeOn())
        {
            radioOn.setChecked(true);
            radioOff.setChecked(false);
        }else
        {
            radioOn.setChecked(false);
            radioOff.setChecked(true);
        }
    }
    
    
    private void setTabSelected(View paramView, TextView paramTextView, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        paramView.setBackgroundColor(getResources().getColor(R.color.husky_underline_selected));
        paramTextView.setTextColor(getResources().getColor(R.color.husky_underline_selected));
        return;
      }
      paramView.setBackgroundColor(getResources().getColor(R.color.husky_underline_normal));
      paramTextView.setTextColor(getResources().getColor(R.color.husky_underline_normal));
    }
    
    class RepeatOnClickListener implements OnClickListener
    {
        private FrameLayout paramFrameLayout;
        private TextView paramTextView;
        private int paramInt;
        RepeatOnClickListener(FrameLayout paramFrameLayout, TextView paramTextView, int paramInt)
        {
            this.paramFrameLayout = paramFrameLayout;
            this.paramTextView = paramTextView;
            this.paramInt = paramInt;
        }
        @Override
        public void onClick(View v)
        {
            if (Utils.GetWeekdaysFromInt(TaskDetailActivity.this.alarm.getWeekdays(), paramInt))
            {
                paramFrameLayout.setBackgroundColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_normal));
                paramTextView.setTextColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_normal));
            }else
            {
                paramFrameLayout.setBackgroundColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_selected));
                paramTextView.setTextColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_selected));
            }
            TaskDetailActivity.this.alarm.toggleWeekday(this.paramInt);
        }
    }
    
    private void setUpRepeat(FrameLayout paramFrameLayout, TextView paramTextView, int paramInt)
    {
      setTabSelected(paramFrameLayout, paramTextView, Utils.GetWeekdaysFromInt(this.alarm.getWeekdays(), paramInt));
      assert (paramFrameLayout.getParent() != null);
      RelativeLayout relativeLayout = (RelativeLayout) paramFrameLayout.getParent();
      relativeLayout.setOnClickListener(new RepeatOnClickListener(paramFrameLayout, paramTextView, paramInt));
    }
    
    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
      switch (paramMenuItem.getItemId())
      {
          case android.R.id.home: //click actionbar
              finish(); //act as back
              return true;
              //Intent intent = new Intent(this, HomeActivity.class);
              //this.startActivity(intent);
          default:
            return true;
      
      }
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
    
}
