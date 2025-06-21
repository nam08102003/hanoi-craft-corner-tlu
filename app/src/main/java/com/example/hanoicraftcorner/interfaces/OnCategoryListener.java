package com.example.hanoicraftcorner.interfaces;


import com.example.hanoicraftcorner.model.Category;

public interface OnCategoryListener {
    void onEditClicked(Category category, String docId);
    void onDelete(Category category, String docId);
}

