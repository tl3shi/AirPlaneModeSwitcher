package name.tanglei.airplaneswitcher.ui;

import java.sql.SQLException;
import java.util.List;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.entity.TaskEntity;
import name.tanglei.airplaneswitcher.utils.TaskManagerUtils;
import name.tanglei.airplaneswitcher.utils.Utils;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class TaskListAdapter extends ArrayAdapter<TaskEntity>
{
    private final String  TAG = TaskListAdapter.class.getName();

    private RuntimeExceptionDao<TaskEntity, Integer> taskDao;

    public TaskListAdapter(Activity activity, List<TaskEntity> list, RuntimeExceptionDao<TaskEntity, Integer> taskDao) {
        super(activity, 0, list);
        this.taskDao = taskDao;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TaskViewHolder holder;
        final TaskEntity task = this.getItem(position);
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity) this.getContext())
                    .getLayoutInflater();
            //convertView = inflater.inflate(R.layout.task_item, null);
            convertView  = inflater.inflate(R.layout.row_alarm, null);
            holder = new TaskViewHolder();

            holder.timeView = (TextView) convertView
                    //        .findViewById(R.id.idTime);
                    .findViewById(R.id.alarm_line_alarm_hour);
            holder.modeOn = (TextView) convertView
                    //.findViewById(R.id.idModeOn);
                    .findViewById(R.id.alarm_line_alarm_wake_type);
            //TextView txRepeat = (TextView)rowView.findViewById(R.id.idRepeat);
            holder.txRepeat = (TextView) convertView.findViewById(R.id.alarm_line_alarm_weekdays);
            //TextView txTitle = (TextView)rowView.findViewById(R.id.idTaskDesc);
            holder.txTitle = (TextView) convertView.findViewById(R.id.alarm_line_alarm_name);
            //ToggleButton tgButton = (ToggleButton)rowView.findViewById(R.id.idTgButton);
            //tgButton.setChecked(task.isActive());
            holder.chk = (CheckBox) convertView.findViewById(R.id.alarm_line_alarm_is_active);
            //cannot set the onclicklistener here, for if the task is modified, the response is
            //always relate with the original one
            //holder.chk.setOnClickListener(new View.OnClickListener()
            convertView.setTag(holder);
        }else
        {
            holder = (TaskViewHolder) convertView.getTag();
        }

        holder.timeView.setText(task.getTimeStr(this.getContext()));
        String modeOnDesc = task.isModeOn() ? this.getContext().getString(R.string.txtTypeOpen)
                : this.getContext().getString(R.string.txtTypeClosed);
        holder.modeOn.setTextColor(task.isModeOn() ? this.getContext().getResources().getColor(R.color.GREEN)
                : this.getContext().getResources().getColor(R.color.RED));
        holder.modeOn.setText(modeOnDesc);
        String repeatDesc = task.getRepeatStr(this.getContext());
        holder.txRepeat.setText(repeatDesc);
        holder.txTitle.setText(task.getTitle());
        holder.chk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CheckBox c = (CheckBox) v;
                if(c.isChecked())
                {
                    task.setActive(true);
                }else
                    task.setActive(false);
                Log.i(TAG, "active " + task.getId() + " @ " + task.hashCode() + " : " + task.isActive());
                taskDao.update(task);
                TaskManagerUtils.addOrUpdateTask(getContext(), task, task.isActive());
            }
        });
//        holder.chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//                if(isChecked)
//                    task.setActive(true);
//                else
//                    task.setActive(false);
//                Log.i(TAG, "active " + task.getId() + " : " + task.isActive());
//                try {
//                    taskDao.update(task);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    Log.i(TAG, "update task failed, " + e.toString());
//                }
//            }
//        });
        //if use checkedChangeListener, setChecked should after addListener, more about: listview, view holder
        holder.chk.setChecked(task.isActive());
        return convertView;
    }

    static class TaskViewHolder
    {
        TextView timeView;
        TextView modeOn;
        TextView txRepeat;
        TextView txTitle;
        CheckBox chk;
    }


}
