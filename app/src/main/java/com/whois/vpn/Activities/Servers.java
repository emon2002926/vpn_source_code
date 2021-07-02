package com.whois.vpn.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.whois.vpn.Fragments.FragmentFree;
import com.whois.vpn.R;
import com.google.android.material.tabs.TabLayout;
import com.whois.vpn.AdapterWrappers.TabAdapter;

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
        adapter.addFragment(new FragmentFree(), "Free");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        

////        show the available server in the list
//        for(int i=0; i < tabLayout.getTabCount(); i++) {
//            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
//            p.setMargins(20, 10, 20, 10);
//            tab.requestLayout();
//        }
    }
}
