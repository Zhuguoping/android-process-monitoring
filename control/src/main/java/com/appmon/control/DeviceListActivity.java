package com.appmon.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appmon.control.persistence.ModelManager;
import com.appmon.control.presenters.DeviceListPresenter;
import com.appmon.control.presenters.IDeviceListPresenter;
import com.appmon.control.views.IDeviceListView;
import com.appmon.shared.entities.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity implements IDeviceListView {

    IDeviceListPresenter mPresenter;

    ListView mListView;
    TextView mNoDevicesText;

    List<DeviceInfo> mDeviceList;
    DeviceListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        mListView = (ListView) findViewById(R.id.deviceList);
        mNoDevicesText = (TextView) findViewById(R.id.noDevicesText);
        mPresenter = new DeviceListPresenter(ModelManager.getInstance()
                .getDeviceListModel());
        mPresenter.attachView(this);
        mDeviceList = new ArrayList<>();
        mListAdapter = new DeviceListAdapter();
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPresenter.selectDevice(position);
            }
        });
        // load cached list immediately
        mPresenter.requestDeviceList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsBtn) {
            Intent settingsActivity = new Intent(DeviceListActivity.this, SettingsActivity.class);
            startActivity(settingsActivity);
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.detachView();
    }

    @Override
    public void openAppList(String deviceId) {
        Intent appListActivity = new Intent(this, AppListActivity.class);
        appListActivity.putExtra("DeviceId", deviceId);
        startActivity(appListActivity);
    }

    @Override
    public void updateList(List<DeviceInfo> devices) {
        mDeviceList = devices;
        mListAdapter.notifyDataSetChanged();
        if (mDeviceList.size() == 0) {
            mListView.setVisibility(View.GONE);
            mNoDevicesText.setVisibility(View.VISIBLE);
        } else {
            mNoDevicesText.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSyncFailMessage() {
        Toast.makeText(this, R.string.text_device_list_sync_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Adapter for displaying devices list
     */
    class DeviceListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        DeviceListAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView text = (TextView) view;
            text.setText(mDeviceList.get(position).getName());
            return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return mDeviceList.size();
        }
    }

}
