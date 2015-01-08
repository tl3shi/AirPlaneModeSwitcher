package name.tanglei.airplaneswitcher.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import name.tanglei.airplaneswitcher.R;
import name.tanglei.airplaneswitcher.utils.OperationLogUtils;

public class OperationLogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_log);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        TextView logView = (TextView) this.findViewById(R.id.id_logText);
        String txt = OperationLogUtils.getOperationLogs();
        if(txt.length() == 0)
            txt = this.getString(R.string.operation_log_empty_tip);
        logView.setText(txt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_operation_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home: //click actionbar
                finish(); //act as back
                return true;
            case R.id.id_action_delete_oplog:
                OperationLogUtils.delLog();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
