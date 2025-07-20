package com.example.bakeryshop.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.bakeryshop.Data.DTO.ReadOrderDTO;
import com.example.bakeryshop.R;
import com.example.bakeryshop.databinding.FragmentOrderHistoryBinding;

public class OrderHistoryDetailFragment extends Fragment {
    private FragmentOrderHistoryBinding binding;


    public OrderHistoryDetailFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        if (getArguments() != null) {
            ReadOrderDTO order = (ReadOrderDTO) getArguments().getSerializable("order");
            // Hiển thị ra view...
        }

        return view;
    }

}
