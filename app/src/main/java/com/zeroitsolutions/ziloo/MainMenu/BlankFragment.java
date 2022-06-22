package com.zeroitsolutions.ziloo.MainMenu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zeroitsolutions.ziloo.MainMenu.RelateToFragmentOnBack.RootFragment;

public class BlankFragment extends RootFragment {

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText("");
        return textView;
    }

}
