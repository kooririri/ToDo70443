package local.hal.st42.android.todo70443;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "statusFile";
    private static final String STATUS_DONE = "完了タスク";
    private static final String STATUS_NOT_DONE = "未完了タスク";
    private static final String STATUS_ALL = "全タスク";
    private String _menuStatus;
    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private RecyclerView taskList;
    private DatabaseHelper _helper;
    private int _status;
    private TextView tvStatus;
    private List<TaskBean> taskData = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbarLayout);
        toolbarLayout.setTitle(getString(R.string.app_name));
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.LTGRAY);

        taskList = findViewById(R.id.lvTaskList);

        tvStatus = findViewById(R.id.tvStatus);
        _helper = new DatabaseHelper(getApplicationContext());
        String[] from = {"name","deadline","done"};
        int[] to = {R.id.tvNameRow,R.id.tvDeadlineRow,R.id.cbDoneCheck};

        LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
        taskList.setLayoutManager(layout);
        DividerItemDecoration decoration = new DividerItemDecoration(MainActivity.this,layout.getOrientation());
        taskList.addItemDecoration(decoration);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        SharedPreferences sharedPreferences =getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        _status = sharedPreferences.getInt("status",0);
        statusHandler();
        SQLiteDatabase db = _helper.getWritableDatabase();
        taskData = DataAccess.findAll(db,_status);
        LinearLayoutManager layout = new LinearLayoutManager(MainActivity.this);
        taskList.setLayoutManager(layout);

        TodoListAdapter adapter = new TodoListAdapter(taskData);

        adapter.notifyDataSetChanged();
        taskList.setAdapter(adapter);
    }

    private void statusHandler(){
        switch (_status){
            case 0:
                tvStatus.setText(STATUS_NOT_DONE);
                _menuStatus = STATUS_NOT_DONE;
                break;
            case 1:
                tvStatus.setText(STATUS_DONE);
                _menuStatus = STATUS_DONE;
                break;
            case 2:
                tvStatus.setText(STATUS_ALL);
                _menuStatus = STATUS_ALL;
                break;
        }
    }
    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuStatusOptionList =menu.findItem(R.id.menuStatus);
        switch (_menuStatus){
            case STATUS_ALL:
                menuStatusOptionList.setTitle(R.string.status_all);
                break;
            case STATUS_DONE:
                menuStatusOptionList.setTitle(R.string.status_done);
                break;
            case STATUS_NOT_DONE:
                menuStatusOptionList.setTitle(R.string.status_not_done);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()){
            case R.id.menuStatusNotDone:
                editor.putInt("status",0);
                _menuStatus = STATUS_NOT_DONE;
                invalidateOptionsMenu();
                break;
            case R.id.menuStatusDone:
                editor.putInt("status",1);
                _menuStatus = STATUS_DONE;
                invalidateOptionsMenu();
                break;
            case R.id.menuStatusALl:
                editor.putInt("status",2);
                _menuStatus = STATUS_ALL;
                invalidateOptionsMenu();
                break;
        }
        editor.commit();
        refreshData();
        return super.onOptionsItemSelected(item);
    }

    public void onFabResetClicked(View view){
        Intent intent = new Intent(getApplicationContext(),TaskEditActivity.class);
        intent.putExtra("mode",MODE_INSERT);
        startActivity(intent);
        invalidateOptionsMenu();
    }

    private class OnCheckBoxClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            CheckBox cbDoneCheck = (CheckBox) view;
            boolean isChecked = cbDoneCheck.isChecked();
            long id = (Long) cbDoneCheck.getTag();
            SQLiteDatabase db = _helper.getWritableDatabase();
            DataAccess.changeDoneChecked(db, id, isChecked);
            refreshData();
        }
    }

    private class TodoViewHolder extends RecyclerView.ViewHolder{
        public TextView _tvName;
        public TextView _tvDeadline;
        public CheckBox _cbDoneCheck;

        /**
         * コンストラクタ
         * @param itemView　リスト1行分の画面部品
         */
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            _tvName = itemView.findViewById(R.id.tvNameRow);
            _tvDeadline = itemView.findViewById(R.id.tvDeadlineRow);
            _cbDoneCheck = itemView.findViewById(R.id.cbDoneCheck);
        }
    }

    private class TodoListAdapter extends RecyclerView.Adapter<TodoViewHolder>{

        private List<TaskBean> _listData;

        public TodoListAdapter(List<TaskBean> listData){
            _listData = listData;
        }
        @NonNull
        @Override
        public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View row = inflater.inflate(R.layout.row,parent,false);
            final TodoViewHolder holder = new TodoViewHolder(row);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    long id = _listData.get(position).getId();
                    Intent intent = new Intent(getApplicationContext(),TaskEditActivity.class);
                    intent.putExtra("mode",MODE_EDIT);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
            TaskBean item = _listData.get(position);
            String taskName = item.getName();
            String deadline = item.getDeadline();
            int doneCheck = item.getDone();
            holder._tvName.setText(taskName);
            holder._tvDeadline.setText(deadline);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            LinearLayout row = (LinearLayout) holder._tvDeadline.getParent().getParent();
            int rColor = androidx.appcompat.R.drawable.abc_list_selector_holo_light;
            String strMonth = String.valueOf(month);
            String strDay = String.valueOf(day);
            if (month+1 < 10){
                strMonth = "0"+(month);
            }
            if (day < 10){
                strDay = "0"+day;
            }
            String today = year + "-" + strMonth + "-" + strDay;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date deadlineDate = null;
            Date todayDate = null;
            try {
                deadlineDate = sdf.parse(deadline);
                todayDate = sdf.parse(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(todayDate.getTime() == deadlineDate.getTime()){
                holder._tvDeadline.setText("期限：今日");
                rColor = R.color.colorPrimary;
            }else if(todayDate.getTime() > deadlineDate.getTime()){
                holder._tvDeadline.setText("期限："+deadline);
                rColor = androidx.appcompat.R.drawable.abc_list_selector_disabled_holo_dark;
            }else{
                holder._tvDeadline.setText("期限："+deadline);
                rColor = androidx.appcompat.R.drawable.abc_list_selector_holo_light;
            }
            row.setBackgroundResource(rColor);

            long id = item.getId();
            boolean checked = false;
            if(doneCheck == 1){
                checked = true;
            }
            holder._cbDoneCheck.setChecked(checked);
            holder._cbDoneCheck.setTag(id);
            holder._cbDoneCheck.setOnClickListener(new OnCheckBoxClickListener());
        }

        @Override
        public int getItemCount() {
            return _listData.size();
        }
    }


}