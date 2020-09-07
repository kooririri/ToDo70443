package local.hal.st42.android.todo70443;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskEditActivity extends AppCompatActivity {

    private long _id = 0;
    private int _mode = MainActivity.MODE_INSERT;
    private DatabaseHelper _helper;
    private TaskBean tasks = null;
    private TextView tvDeadline = null;
    private Switch ifDone = null;
    private String strDeadline = "";
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int isDone = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _helper = new DatabaseHelper(getApplicationContext());
        tvDeadline = findViewById(R.id.tvDeadline);
        ifDone = findViewById(R.id.sIfDone);
        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode",MainActivity.MODE_INSERT);
        if(_mode == MainActivity.MODE_INSERT){
            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText(R.string.tv_title_insert);
            Calendar cal = Calendar.getInstance();
            selectedYear = cal.get(Calendar.YEAR);
            selectedMonth = cal.get(Calendar.MONTH);
            selectedDay = cal.get(Calendar.DAY_OF_MONTH);
            String strMonth = String.valueOf(selectedMonth);
            String strDay = String.valueOf(selectedDay);
            if (selectedMonth+1 < 10){
                strMonth = "0"+(selectedMonth+1);
            }
            if (selectedDay < 10){
                strDay = "0"+selectedDay;
            }

            strDeadline = selectedYear + "-" + strMonth + "-" + strDay;
            tvDeadline.setText(strDeadline);
        }else{
            _id = intent.getLongExtra("id",0);
            Log.e("pxl",_id+"");
            SQLiteDatabase db = _helper.getWritableDatabase();
            tasks = DataAccess.findByPk(db,_id);

            EditText etInputShopName = findViewById(R.id.etInputTaskName);
            etInputShopName.setText(tasks.getName());


            selectedYear = Integer.valueOf(tasks.getDeadline().split("-")[0]);
            selectedMonth = Integer.valueOf(tasks.getDeadline().split("-")[1])-1;
            selectedDay = Integer.valueOf(tasks.getDeadline().split("-")[2]);
            String strMonth = String.valueOf(selectedMonth);
            String strDay = String.valueOf(selectedDay);
            if (selectedMonth+1 < 10){
                strMonth = "0"+(selectedMonth+1);
            }
            if (selectedDay < 10){
                strDay = "0"+selectedDay;
            }
            strDeadline = selectedYear + "-" + strMonth + "-" + strDay;
            tvDeadline.setText(tasks.getDeadline());

            EditText etNote = findViewById(R.id.etNote);
            etNote.setText(tasks.getNote());

            isDone = tasks.getDone();
            if(tasks.getDone() == 1){
                ifDone.setChecked(true);
            }else{
                ifDone.setChecked(false);
            }

        }
        ifDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    isDone = 1;
                }else{
                    isDone = 0;
                }
            }
        });

        tvDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(TaskEditActivity.this,new DatePickerDialogDateSetListener(),selectedYear,selectedMonth,selectedDay);
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy(){
        _helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(_mode == MainActivity.MODE_INSERT){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.insert_menu,menu);
            return true;
        }else{
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_menu,menu);
            return true;
        }

    }
    private class DatePickerDialogDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            String strMonth = String.valueOf(selectedMonth);
            String strDay = String.valueOf(selectedDay);
            if (selectedMonth+1 < 10){
                strMonth = "0"+(selectedMonth+1);
            }
            if (selectedDay < 10){
                strDay = "0"+selectedDay;
            }
            strDeadline = selectedYear + "-" + strMonth + "-" + strDay;
            tvDeadline.setText(strDeadline);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.btnSave:
                EditText etInputTaskName= findViewById(R.id.etInputTaskName);
                String inputTaskName = etInputTaskName.getText().toString();
                if(inputTaskName.equals("")){
                    Toast.makeText(getApplicationContext(),R.string.msg_input_task_name,Toast.LENGTH_SHORT).show();
                }else{
                    EditText etInputNote = findViewById(R.id.etNote);
                    String inputNote = etInputNote.getText().toString();
                    TaskBean bean = new TaskBean();
                    bean.setName(inputTaskName);
                    bean.setDeadline(strDeadline);
                    bean.setDone(isDone);
                    bean.setNote(inputNote);

                    SQLiteDatabase db = _helper.getWritableDatabase();
                    if(_mode == MainActivity.MODE_INSERT){
                        DataAccess.insert(db,bean);
                    }else{
                        DataAccess.update(db,bean,_id);
                    }
                    finish();
                }
                break;

            case R.id.btnDelete:
                DeleteDialogFragment dialog = new DeleteDialogFragment();
                dialog.set_helper(_helper);
                dialog.set_id(_id);
                FragmentManager manager =getSupportFragmentManager();
                dialog.show(manager,"DeleteDialogFragment");

                break;

            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private static Date strToDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
