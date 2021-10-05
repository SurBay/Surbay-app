package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.adapter.SellerRecyclerViewAdapter;
import com.pumasi.surbay.adapter.StoreCategoryRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Coupon;
import com.pumasi.surbay.classfile.Store;
import com.pumasi.surbay.classfile.StoreCategory;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.SellingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MobileCouponFragment extends Fragment {

    private View view;
    private int pos = 0;
    private Context context;
    private SellerRecyclerViewAdapter sellerRecyclerViewAdapter;
    private StoreCategoryRecyclerViewAdapter storeCategoryRecyclerViewAdapter;
    private RecyclerView rv_coupon_seller_category;
    private RecyclerView rv_coupon_seller_supplier;
    private int type = 0;
    private ArrayList<Integer> clicked = new ArrayList<Integer>();
    private ArrayList<StoreCategory> boardStoreCategories = new ArrayList<StoreCategory>();
    private ArrayList<Store> boardStores = new ArrayList<Store>();
    private RelativeLayout loadingPanel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mobile_coupon, container, false);
        context = getActivity().getApplicationContext();
        loadingPanel  = view.findViewById(R.id.loadingPanel);

        rv_coupon_seller_supplier = view.findViewById(R.id.rv_coupon_seller_supplier);
        rv_coupon_seller_category = view.findViewById(R.id.rv_coupon_seller_category);
        sellerRecyclerViewAdapter = new SellerRecyclerViewAdapter(boardStores, context);
        rv_coupon_seller_supplier.setAdapter(sellerRecyclerViewAdapter);
        rv_coupon_seller_supplier.setLayoutManager(new GridLayoutManager(context, 3));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rv_coupon_seller_supplier.getLayoutParams();
        layoutParams.setMargins((int) (MainActivity.screen_width_px / 20.55), (int) (MainActivity.screen_width_px / 45.0657894737), (int) (MainActivity.screen_width_px / 20.55), (int) (MainActivity.screen_width_px / 45.0657894737));
        sellerRecyclerViewAdapter.setOnItemClickListener(new SellerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(context, SellingActivity.class);
                Store item = (Store) sellerRecyclerViewAdapter.getItem(position);
                intent.putExtra("store", item);
                intent.putExtra("position", position);
                Log.d("isClicked", "onItemClick: " + "true");
                startActivity(intent);
            }
        });

        storeCategoryRecyclerViewAdapter = new StoreCategoryRecyclerViewAdapter(boardStoreCategories, clicked, context);
        rv_coupon_seller_category.setAdapter(storeCategoryRecyclerViewAdapter);
        rv_coupon_seller_category.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
        storeCategoryRecyclerViewAdapter.setOnItemClickListener(new StoreCategoryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("category", "onItemClick: " + "clicked");
                clicked.set(type, 0);
                clicked.set(position, 1);
                boardStores.clear();
                sellerRecyclerViewAdapter.notifyDataSetChanged();
                storeCategoryRecyclerViewAdapter.notifyDataSetChanged();
                type = position;
                getCoupons();
                getStores(type);
            }
        });
        getCoupons();
        getStores(type);
        getCategories();
        return view;
    }


    public void getCategories() {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/store/getcategories";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONArray responseArray = new JSONArray(response.toString());
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject responseCategory = responseArray.getJSONObject(i);
                        int type = responseCategory.getInt("type");
                        String category = responseCategory.getString("category");
                        StoreCategory resultStoreCategory = new StoreCategory(type, category);
                        boardStoreCategories.add(resultStoreCategory);
                        clicked.add(0);
                    }
                    clicked.set(type, 1);
                    storeCategoryRecyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getStores(int type) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/store/getstores/?type=" + type;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONArray responseArray = new JSONArray(response.toString());
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject store = responseArray.getJSONObject(i);
                        String id = store.getString("_id");
                        boolean hide = store.getBoolean("hide");
                        ArrayList<String> img_urls = new ArrayList<String>();
                        try {
                            JSONArray ja = (JSONArray) store.get("img_urls");
                            for (int j = 0; j < ja.length(); j++) {
                                img_urls.add(ja.getString(j));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ArrayList<Coupon> coupons_list = new ArrayList<Coupon>();
                        try {
                            JSONArray ua = (JSONArray) store.get("coupons_list");
                            for (int u = 0; u < ua.length(); u++) {
                                JSONObject responseCoupon = (JSONObject) ua.get(u);
                                String id_ = responseCoupon.getString("_id");
                                boolean hide_ = responseCoupon.getBoolean("hide");
                                ArrayList<String> img_urls_ = new ArrayList<String>();
                                try {
                                    JSONArray uua = (JSONArray) responseCoupon.get("image_urls");
                                    for (int uu = 0; uu < uua.length(); uu++) {
                                        img_urls_.add(uua.getString(uu));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String store_ = responseCoupon.getString("store");
                                String menu_  = responseCoupon.getString("menu");
                                String content_ = responseCoupon.getString("content");
                                String author_ = responseCoupon.getString("author");
                                String category_ = responseCoupon.getString("category");
                                int cost_ = responseCoupon.getInt("cost");
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
                                Date date_ = null;
                                try {
                                    date_ = fm.parse(responseCoupon.getString("date"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Coupon resultCoupon = new Coupon(id_, hide_, img_urls_, store_, menu_, content_, author_, category_, cost_, date_);
                                coupons_list.add(resultCoupon);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String name = store.getString("name");
                        String category = store.getString("category");
                        Store resultStore = new Store(id, hide, img_urls, coupons_list, name, category);
                        boardStores.add(resultStore);
                    }
                    sellerRecyclerViewAdapter.notifyDataSetChanged();
                    setLoading(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error ->  {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getCoupons() {
        setLoading(true);
        boardStores.add(new Store("all", false, new ArrayList<>(Arrays.asList("https://ibb.co/q5n9Ckm")), null, "전체", "all"));
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/coupon";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    ArrayList<Coupon> allCoupons = new ArrayList<Coupon>();
                    JSONArray responseArray = new JSONArray(response.toString());
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject responseCoupon = (JSONObject) responseArray.get(i);
                        String id = responseCoupon.getString("_id");
                        boolean hide = responseCoupon.getBoolean("hide");
                        ArrayList<String> image_urls = new ArrayList<String>();
                        try {
                            JSONArray uua = (JSONArray) responseCoupon.get("image_urls");
                            for (int uu = 0; uu < uua.length(); uu++) {
                                image_urls.add(uua.getString(uu));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String store = responseCoupon.getString("store");
                        String menu = responseCoupon.getString("menu");
                        String content = responseCoupon.getString("content");
                        String author = responseCoupon.getString("author");
                        String category = responseCoupon.getString("category");
                        int cost = responseCoupon.getInt("cost");
                        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");

                        Date date = null;
                        try {
                            date = fm.parse(responseCoupon.getString("date"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Coupon newCoupon = new Coupon(id, hide, image_urls, store, menu, content, author, category, cost, date);
                        allCoupons.add(newCoupon);
                    }
                    boardStores.get(0).setCoupons_list(allCoupons);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setLoading(boolean show) {
        if (show) {
            rv_coupon_seller_supplier.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.VISIBLE);
        } else {
            rv_coupon_seller_supplier.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.GONE);
        }
    }
}