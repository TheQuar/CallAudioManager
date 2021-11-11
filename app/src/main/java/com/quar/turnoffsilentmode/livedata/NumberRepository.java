package com.quar.turnoffsilentmode.livedata;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.quar.turnoffsilentmode.room_database.AppDao;
import com.quar.turnoffsilentmode.room_database.AppDatabase;
import com.quar.turnoffsilentmode.room_database.NumbersTable;

import java.util.List;

public class NumberRepository extends AndroidViewModel {
    private AppDatabase appDatabase;
    private static final int PAGE_SIZE = 10;


    public String isNumber(String number) {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT phone_number FROM NumbersTable Where voice_mode=1 and phone_number = ?",
                new Object[]{number}
        );
        return appDatabase.appDao().isNumber(query);
    }

    public NumberRepository(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getInstance(application);
    }

    public LiveData<PagedList<NumbersTable>> getNumbers() {
        return new LivePagedListBuilder(appDatabase.appDao().getNumbers(), PAGE_SIZE).build();
    }

    public LiveData<List<NumbersTable>> getNumbersForCheck() {
        return appDatabase.appDao().getNumbersForCheck();
    }

    public List<NumbersTable> getCheckedNumbers() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT * FROM NumbersTable Where voice_mode=1 order by name ASC",
                new Object[]{}
        );
        return appDatabase.appDao().getCheckedNumbers(query);
    }

    public List<NumbersTable> getNumbersFromDb() {
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(
                "SELECT * FROM NumbersTable", new Object[]{}
        );
        return appDatabase.appDao().getNumbersFromDb(query);
    }

    public void insertNumber(List<NumbersTable> numbersTable) {
        new InsertNumberAsyncTask(appDatabase.appDao()).execute(numbersTable);
    }

    public void updateNumber(NumbersTable numbersTable) {
//        appDatabase.appDao().updateNumber(numbersTable);
        new UpdateNumber(appDatabase.appDao()).execute(numbersTable);
    }

    public void deleteNumber(NumbersTable numbersTable) {
        appDatabase.appDao().deleteNumber(numbersTable);
    }

    private static class InsertNumberAsyncTask extends AsyncTask<List<NumbersTable>, Void, Void> {

        private AppDao appDao;

        public InsertNumberAsyncTask(AppDao appDao) {
            this.appDao = appDao;
        }

        @Override
        protected Void doInBackground(List<NumbersTable>... lists) {
            appDao.insertNumber(lists[0]);
            return null;
        }
    }


    private static class UpdateNumber extends AsyncTask<NumbersTable, Void, Void> {

        private AppDao appDao;

        public UpdateNumber(AppDao appDao) {
            this.appDao = appDao;
        }

        @Override
        protected Void doInBackground(NumbersTable... lists) {
            appDao.updateNumber(lists[0]);
            return null;
        }
    }

}
