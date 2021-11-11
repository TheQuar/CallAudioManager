package com.quar.turnoffsilentmode.livedata;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.quar.turnoffsilentmode.room_database.NumbersTable;

import java.util.List;

public class NumberViewModel extends AndroidViewModel {

    private NumberRepository numberRepository;

    public NumberViewModel(@NonNull Application application) {
        super(application);
        numberRepository = new NumberRepository(application);
    }

    public LiveData<PagedList<NumbersTable>> getNumbers() {
        return numberRepository.getNumbers();
    }

    public String isNumber(String number) {
        return numberRepository.isNumber(number);
    }

    public LiveData<List<NumbersTable>> getNumbersForCheck() {
        return numberRepository.getNumbersForCheck();
    }

    public List<NumbersTable> getCheckedNumbers() {
        return numberRepository.getCheckedNumbers();
    }

    public List<NumbersTable> getNumbersFromDb() {
        return numberRepository.getNumbersFromDb();
    }

    public void updateNumber(NumbersTable numbersTable) {
        numberRepository.updateNumber(numbersTable);
    }

    public void insertNumber(List<NumbersTable> numbersTable) {
        numberRepository.insertNumber(numbersTable);
    }

    public void deleteNumber(NumbersTable numbersTable) {
        numberRepository.deleteNumber(numbersTable);
    }

}
