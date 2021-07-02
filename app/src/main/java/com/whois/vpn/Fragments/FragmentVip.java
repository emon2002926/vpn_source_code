package com.whois.vpn.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
//import com.toto.ex.R;
import com.efaso.admob_advanced_native_recyvlerview.AdmobNativeAdAdapter;
import com.whois.vpn.AdapterWrappers.FBNativeAdapter;
import com.whois.vpn.AdapterWrappers.ServerListAdapterVip;
import com.whois.vpn.Config;
import com.whois.vpn.R;

import java.util.ArrayList;

public class FragmentVip extends Fragment implements ServerListAdapterVip.RegionListAdapterInterface {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private ArrayList<Country> countryArrayList;
    private RegionChooserInterface regionChooserInterface;

    private RelativeLayout animationHolder;

    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        countryArrayList = new ArrayList<>();

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);

        mUnblockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getContext(), UnlockAllActivity.class));
            }
        });

        if (Config.vip_subscription || Config.all_subscription) {
            mPurchaseLayout.setVisibility(View.GONE);
        }

        adapter = new ServerListAdapterVip(countryArrayList, getActivity());
        if (getResources().getBoolean(R.bool.ads_switch) && getResources().getBoolean(R.bool.facebook_list_ads) && (!Config.ads_subscription || !Config.all_subscription)) {
            //facebook adapter
            FBNativeAdapter nativeAdapter = FBNativeAdapter.Builder.with(getString(R.string.facebook_placement_id), adapter)
                    .adItemInterval(4)
                    .build();
            recyclerView.setAdapter(nativeAdapter);
        } else if (getResources().getBoolean(R.bool.ads_switch) && getResources().getBoolean(R.bool.admob_list_ads) && (!Config.ads_subscription || !Config.all_subscription)) {
            //admob adapter here

            AdmobNativeAdAdapter admobNativeAdAdapter = AdmobNativeAdAdapter.Builder
                    .with(
                            getString(R.string.admob_native),
                            adapter,
                            "small"
                    )
                    .adItemIterval(12)
                    .build();
            recyclerView.setAdapter(admobNativeAdAdapter);
        } else {
            //simple adapter
            recyclerView.setAdapter(adapter);
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
                for (int i = 0; i < availableCountries.getCountries().size(); i++) {
                    if (availableCountries.getCountries().get(i).getServers() > 0) {
                        countryArrayList.add(availableCountries.getCountries().get(i));
                    }
                }
                adapter.notifyDataSetChanged();

                animationHolder.setVisibility(View.GONE);
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        } );
          /*  @Override
            public void success(List<Country> countries) {
                for (int i = 0; i < countries.size(); i++) {
                    if (countries.get(i).getServers() > 0) {
                        countryArrayList.add(countries.get(i));
                    }
                }
                adapter.notifyDataSetChanged();

                animationHolder.setVisibility(View.GONE);
            }

            @Override
            public void failure(HydraException e) {

            }
        });*/
    }

    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof RegionChooserInterface) {
            regionChooserInterface = (RegionChooserInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        regionChooserInterface = null;
    }

    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }
}