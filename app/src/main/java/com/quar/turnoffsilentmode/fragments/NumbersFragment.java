package com.quar.turnoffsilentmode.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quar.turnoffsilentmode.R;
import com.quar.turnoffsilentmode.adapter.NumbersAdapter;
import com.quar.turnoffsilentmode.livedata.NumberViewModel;
import com.quar.turnoffsilentmode.room_database.NumbersTable;


public class NumbersFragment extends Fragment implements NumbersAdapter.ItemClickListener {

    View root_view;
    NumberViewModel numberViewModel;
    NumbersAdapter numbersAdapter;

    public NumbersFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numberViewModel = ViewModelProviders.of(this).get(NumberViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_numbers, container, false);
        init();
        return root_view;
    }


    private void init() {
        RecyclerView recyclerView = root_view.findViewById(R.id.rv_numbers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        numbersAdapter = new NumbersAdapter(getContext());
        numbersAdapter.setClickListener(this);
        recyclerView.setAdapter(numbersAdapter);


        numberViewModel.getNumbers().observe(getViewLifecycleOwner(), numbersTables -> {
            if (numbersTables != null) {
                numbersAdapter.submitList(numbersTables);
            }
        });

    }


    @Override
    public void onItemClick(View view, NumbersTable numbersTable) {
        numberViewModel.updateNumber(numbersTable);
    }
}