package com.skype.demo.mmsdemo;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.skype.demo.mmsdemo.databinding.MainActivityBinding;
import com.skype.demo.mmsdemo.viewmodels.MainViewModel;

public class MainActivityActivity extends Activity {

    private MainViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        this.vm = new MainViewModel(this);
        binding.setVm(this.vm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.vm = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.vm.updateBindings();
    }
}
