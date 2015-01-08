package name.tanglei.airplaneswitcher.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.dao.DatabaseHelper;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;
import name.tanglei.airplaneswitcher.utils.Utils;


public class TaskDetailActivity extends OrmLiteBaseActivity<DatabaseHelper>//FragmentActivity
{
    public static String TAG = TaskDetailActivity.class.getName();
    
    private TaskEntity editAlarm;//current edit alarm
    private TaskEntity oldAlarm; // the

    private TextView txtTimeView;
    private TextView txtTaskName;
    private RadioButton radioOn;
    private RadioButton radioOff;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        Log.i(TAG, "created");
        //this.setContentView(R.layout.task_detail);
        this.setContentView(R.layout.edit_alarm);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        oldAlarm = (TaskEntity) this.getIntent().getSerializableExtra("alarmTask");
        if(oldAlarm != null)
            this.editAlarm = new TaskEntity(oldAlarm);
        else
            this.editAlarm = Utils.createTask(this);
        
        int[] repeat_lines = new int[]{R.id.repeat_monday_line, R.id.repeat_tuesday_line, R.id.repeat_wendsday_line,
                R.id.repeat_thursday_line, R.id.repeat_friday_line, R.id.repeat_saturday_line, R.id.repeat_sunday_line};
        int[] repeat_txt = new int[]{R.id.repeat_monday, R.id.repeat_tuesday, R.id.repeat_wendsday,
                R.id.repeat_thursday, R.id.repeat_friday, R.id.repeat_saturday, R.id.repeat_sunday};
        for(int i = 0; i < 7; i++)
        {
            FrameLayout localFrameLayout = (FrameLayout)this.findViewById(repeat_lines[i]);
            this.setUpRepeat(localFrameLayout, (TextView)this.findViewById(repeat_txt[i]), i+1);
        }
        
        txtTimeView = (TextView) this.findViewById(R.id.edit_alarm_hour);
        txtTaskName = (TextView) this.findViewById(R.id.edit_alarm_label);

        TimeOnClickListener timeClickListener = new TimeOnClickListener();
        txtTimeView.setOnClickListener(timeClickListener);
        TaskNameOnClickListener taskNameOnClickListener = new TaskNameOnClickListener();
        txtTaskName.setOnClickListener(taskNameOnClickListener);

        radioOn = (RadioButton) this.findViewById(R.id.idRadioOn);
        radioOff = (RadioButton) this.findViewById(R.id.idRadioOff);

        if(this.editAlarm.isModeOn())
        {
            radioOn.setChecked(true);
            radioOff.setChecked(false);
        }else
        {
            radioOn.setChecked(false);
            radioOff.setChecked(true);
        }
        txtTimeView.setText(this.editAlarm.getTimeStr(this));
        txtTaskName.setText(this.editAlarm.getTitle());
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
            if (Utils.GetWeekdaysFromInt(TaskDetailActivity.this.editAlarm.getWeekdays(), paramInt))
            {
                paramFrameLayout.setBackgroundColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_normal));
                paramTextView.setTextColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_normal));
            }else
            {
                paramFrameLayout.setBackgroundColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_selected));
                paramTextView.setTextColor(TaskDetailActivity.this.getResources().getColor(R.color.husky_underline_selected));
            }
            TaskDetailActivity.this.editAlarm.toggleWeekday(paramInt);
            //Log.i(TAG, "weekday: " + TaskDetailActivity.this.editAlarm.toFormatString(TaskDetailActivity.this));
        }
    }

    class TimeOnClickListener implements  OnClickListener
    {
        @Override
        public void onClick(View v)
        {

            /*TimePickerDialog timeDialog = new TimePickerDialog(TaskDetailActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                    TaskDetailActivity.this.alarm.setTime(Utils.getCalendar(hourOfDay, minute));
                    TaskDetailActivity.this.updateView();
                }
            }, TaskDetailActivity.this.alarm.getHour(), TaskDetailActivity.this.alarm.getMinute(), true);
            timeDialog.show();*/
            final TimePicker timePicker = new TimePicker(TaskDetailActivity.this);
            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(TaskDetailActivity.this.editAlarm.getHour());
            timePicker.setCurrentMinute(TaskDetailActivity.this.editAlarm.getMinute());
            new AlertDialog.Builder(TaskDetailActivity.this).setTitle(TaskDetailActivity.this.getString(R.string.alertDialogTimeChoose_title)).setIcon(android.R.drawable.ic_dialog_info)
                    .setView(timePicker).setPositiveButton(TaskDetailActivity.this.getString(R.string.alertDialog_positive),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            TaskDetailActivity.this.editAlarm.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                            TaskDetailActivity.this.txtTimeView.setText(TaskDetailActivity.this.editAlarm.getTimeStr(TaskDetailActivity.this));

                        }
                    }
            ).setNegativeButton(TaskDetailActivity.this.getString(R.string.alertDialog_negative), null).show();
        }
    }

    class TaskNameOnClickListener implements  OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            final EditText input = new EditText(TaskDetailActivity.this);
            //input.focus select all
            new AlertDialog.Builder(TaskDetailActivity.this).setTitle(TaskDetailActivity.this.getString(R.string.alertDialogTaskInput_title)).setIcon(android.R.drawable.ic_dialog_info)
                    .setView(input).setPositiveButton(TaskDetailActivity.this.getString(R.string.alertDialog_positive),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            TaskDetailActivity.this.editAlarm.setTitle(input.getText().toString());
                            TaskDetailActivity.this.txtTaskName.setText(TaskDetailActivity.this.editAlarm.getTitle());
                        }
                    }
            ).setNegativeButton(TaskDetailActivity.this.getString(R.string.alertDialog_negative), null).show();
        }
    }

    private void setUpRepeat(FrameLayout paramFrameLayout, TextView paramTextView, int paramInt)
    {
      setTabSelected(paramFrameLayout, paramTextView, Utils.GetWeekdaysFromInt(this.editAlarm.getWeekdays(), paramInt));
      assert (paramFrameLayout.getParent() != null);
      RelativeLayout relativeLayout = (RelativeLayout) paramFrameLayout.getParent();
      relativeLayout.setOnClickListener(new RepeatOnClickListener(paramFrameLayout, paramTextView, paramInt));
    }
    
    public boolean onOptionsItemSelected(MenuItem paramMenuItem)
    {
      switch (paramMenuItem.getItemId())
      {
          case android.R.id.home: //click actionbar
              Log.i(TAG, "Task saved: " + this.editAlarm.toFormatString(this));
              this.saveOrUpdate();
              finish(); //act as back
              return true;
              //Intent intent = new Intent(this, HomeActivity.class);
              //this.startActivity(intent);
          case R.id.id_action_giveup:
              Log.i(TAG, "Task give up");
              finish();
          default:
            return true;
      
      }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveOrUpdate()
    {
        this.editAlarm.setModeOn(radioOn.isChecked());
        Log.i(TAG, "save or update id: " + this.editAlarm.getId());
        Log.i(TAG, "old alarm: " + this.oldAlarm);
        Log.i(TAG,  "new alarm: " + this.editAlarm);
        if(this.oldAlarm == null) //save
        {
            try {
                getHelper().getTaskDao().create(this.editAlarm);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "save new task failed ! " + e.toString());
            }
        }else //update
        {
            try {
                getHelper().getTaskDao().update(this.editAlarm);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "update new task failed ! " + e.toString());
            }
        }
        TaskManagerUtils.addOrUpdateTask(this.getApplicationContext(), this.editAlarm, true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}
