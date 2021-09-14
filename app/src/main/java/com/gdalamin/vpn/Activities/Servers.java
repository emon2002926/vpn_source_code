package com.gdalamin.vpn.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.gdalamin.vpn.Fragments.FragmentFree;
import com.gdalamin.vpn.R;
import com.google.android.material.tabs.TabLayout;
import com.gdalamin.vpn.AdapterWrappers.TabAdapter;

public class Servers extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Premium Servers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        /*
         *         Vip Server will be shown in the "Vip Server" fragment...
         *         Free Server will be shown in the "Free Server" fragment...
         * */
//        adapter.addFragment(new FragmentVip(), "Vip Server");
        adapter.addFragment(new FragmentFree(), "Free Server");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
