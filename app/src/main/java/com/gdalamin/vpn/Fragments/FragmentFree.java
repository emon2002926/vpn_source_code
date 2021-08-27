package com.gdalamin.vpn.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.gdalamin.vpn.R;
import com.google.android.gms.ads.InterstitialAd;
//import com.toto.ex.R;
import com.gdalamin.vpn.AdapterWrappers.ServerListAdapterFree;
import com.gdalamin.vpn.Config;

import java.util.ArrayList;
import java.util.List;

public class FragmentFree extends Fragment implements ServerListAdapterFree.RegionListAdapterInterface {
    private RecyclerView recyclerView;
    public ServerListAdapterFree adapter;
    private ArrayList<Country> countryArrayList;
//    private FragmentVip.RegionChooserInterface regionChooserInterface;
    int server;
    InterstitialAd mInterstitialAd;
    boolean isAds;
    private RelativeLayout animationHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        countryArrayList = new ArrayList<>();
        animationHolder = view.findViewById(R.id.animation_layout);

        adapter = new ServerListAdapterFree(getActivity());
        recyclerView.setAdapter(adapter);

        if (getResources().getBoolean(R.bool.ads_switch) && getResources().getBoolean(R.bool.facebook_list_ads) && (!Config.ads_subscription && !Config.all_subscription&& !Config.vip_subscription)) {

            isAds = true;
        } else if (getResources().getBoolean(R.bool.ads_switch) && getResources().getBoolean(R.bool.admob_list_ads) && (!Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription)) {

            isAds = true;
        } else {
            //simple adapter
            isAds = false;

        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadServers();
    }

    private void loadServers() {
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                List<Country> countries = availableCountries.getCountries();
                for (int i = 0; i < countries.size(); i++) {
                    if(isAds){
                        if (i % 2 == 0) {
                            countryArrayList.add(countries.get(i));
                        }else if(i%5 == 0){
                            countryArrayList.add(null);
                        }
                    }else {
                        countryArrayList.add(countries.get(i));

                    }

                }
                adapter.setData(countryArrayList);
//                adapter.notifyDataSetChanged();

                animationHolder.setVisibility(View.GONE);

            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    public void onCountrySelected(Country item) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

//        regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
//        if (ctx instanceof FragmentVip.RegionChooserInterface) {
//            regionChooserInterface = (FragmentVip.RegionChooserInterface) ctx;
//        }
    }
}
