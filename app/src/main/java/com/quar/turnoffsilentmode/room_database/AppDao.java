package com.quar.turnoffsilentmode.room_database;


import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

//todo
@Dao
public interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNumber(List<NumbersTable> numbersTable);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNumber(NumbersTable numbersTable);

    @Query("Select * from NumbersTable order by name ASC")
    DataSource.Factory<Integer, NumbersTable> getNumbers();

    @RawQuery
    List<NumbersTable> getNumbersFromDb(SupportSQLiteQuery supportSQLiteQuery);

    @RawQuery
    String isNumber(SupportSQLiteQuery supportSQLiteQuery);


    @RawQuery
    List<NumbersTable> getCheckedNumbers(SupportSQLiteQuery supportSQLiteQuery);

    @Query("Select * from NumbersTable")
    LiveData<List<NumbersTable>> getNumbersForCheck();

    @Delete
    void deleteNumber(NumbersTable numbersTable);
}
