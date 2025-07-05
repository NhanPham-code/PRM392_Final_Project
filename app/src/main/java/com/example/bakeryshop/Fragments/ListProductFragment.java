package com.example.bakeryshop.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bakeryshop.Adapters.ProductAdapter;
import com.example.bakeryshop.R;
import com.example.bakeryshop.ViewModel.ProductListViewModel;
import com.example.bakeryshop.databinding.FragmentListProductBinding;

public class ListProductFragment extends Fragment {

    private FragmentListProductBinding binding;

    private ProductListViewModel productListViewModel;

    public ListProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        productListViewModel = new ProductListViewModel(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentListProductBinding.inflate(inflater, container, false);

        // Fetch products from ViewModel
        productListViewModel.fetchProducts();

        // Set up RecyclerView
        ProductAdapter productAdapter = new ProductAdapter();

        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerViewProducts.setAdapter(productAdapter);

        // Observe the products LiveData from ViewModel
        productListViewModel.products.observe(getViewLifecycleOwner(), products -> {
            // Update the RecyclerView adapter with the new product list
            productAdapter.setProductList(products);
        });

        // Observe loading state
        productListViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Observe error messages
        productListViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Filter products by category if needed
        binding.categoryAll.setOnClickListener(v -> {
            productListViewModel.fetchProducts(); // Fetch all products
        });

        binding.categoryBread.setOnClickListener(v -> {
            productListViewModel.fetchProductsByCategoryId(1); // Assuming 1 is the ID for bread category
        });
        binding.categoryCakeSlice.setOnClickListener(v -> {
            productListViewModel.fetchProductsByCategoryId(2); // Assuming 2 is the ID for cake category
        });
        binding.categorySavory.setOnClickListener(v -> {
            productListViewModel.fetchProductsByCategoryId(3); // Assuming 3 is the ID for pastry category
        });
        binding.categorySpecial.setOnClickListener(v -> {
            productListViewModel.fetchProductsByCategoryId(4); // Assuming 4 is the ID for pastry category
        });
        binding.categorySweet.setOnClickListener(v -> {
            productListViewModel.fetchProductsByCategoryId(5); // Fetch all products
        });

        // Search functionality
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.isEmpty()) {
                    // If the search query is empty, fetch all products
                    productListViewModel.fetchProducts();
                    return false;
                }
                // If the search query is not empty, search for products
                productListViewModel.searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    // If the search text is not empty, search for products
                    productListViewModel.searchProducts(newText);
                    return true;
                }
                // If the search text is empty, fetch all products
                productListViewModel.fetchProducts();
                return false;
            }
        });
        return binding.getRoot();
    }
}