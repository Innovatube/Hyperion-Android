package com.willowtreeapps.hyperion.sqlite.presentation.tables;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.willowtreeapps.hyperion.plugin.v1.HyperionIgnore;
import com.willowtreeapps.hyperion.sqlite.R;
import com.willowtreeapps.hyperion.sqlite.presentation.records.DbRecordViewerActivity;

import java.io.File;
import java.util.List;

import io.reactivex.functions.Consumer;

@HyperionIgnore
public class TablesListActivity extends AppCompatActivity implements TablesListAdapter.OnTableSelectedListener {

    private static final String ARGS_DB_NAME = "args_db_name";

    public static void startActivity(@NonNull Context context, @NonNull String databaseName) {
        final Intent intent = new Intent(context, TablesListActivity.class);
        intent.putExtra(ARGS_DB_NAME, databaseName);
        context.startActivity(intent);
    }

    private TableViewModel viewModel;
    private TablesListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hsql_database_list);
        setSupportActionBar((Toolbar) findViewById(R.id.hsql_toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.hsql_tables_list_heading);
        }
        String dbName = getIntent().getStringExtra(ARGS_DB_NAME);
        initViewModels(dbName);
        final RecyclerView list = findViewById(R.id.hsql_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new TablesListAdapter(dbName);
        this.adapter.setClickListener(this);
        list.setAdapter(adapter);
        loadTables();
    }

    private void loadTables() {
        viewModel.loadTables(new Consumer<List<TableItem>>() {
            @Override
            public void accept(List<TableItem> tableItems) throws Exception {
                adapter.setData(tableItems);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("loadTables", throwable.getLocalizedMessage());
            }
        });
    }

    private void initViewModels(String databaseName) {
        final File dbFile = getDatabasePath(databaseName);
        viewModel = ViewModelProviders.of(this).get(TableViewModel.class);
        viewModel.initDatabase(dbFile);
    }

    @Override
    public void onClick(String databaseName, String tableName) {
        DbRecordViewerActivity.startActivity(this, databaseName, tableName);
    }
}
