package su.allabergen.zapiskz2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    public static final String RECOMMEND_URL = "http://zp.jgroup.kz/rest/v1/salon/getRecommended";
    private static final String POPULAR_URL = "http://zp.jgroup.kz/rest/v1/salon/getPopular";
    private static final String RECENT_URL = "http://zp.jgroup.kz/rest/v1/salon/getRecentlyAdded";
    private static final String BANNER_URL = "http://zp.jgroup.kz/rest/v1/getMobileAppBanners";
    private ScrollView scrollView;
    private ImageView headerImage;
    private TextView headerText;
    private RecyclerView recommendList;
    private RecyclerView popularList;
    private RecyclerView recentList;
    private LinearLayoutManager recommendLayoutManager;
    private LinearLayoutManager popularLayoutManager;
    private LinearLayoutManager recentLayoutManager;
    private ArrayList<Salon> recommendData = new ArrayList<>();
    private ArrayList<Salon> popularData = new ArrayList<>();
    private ArrayList<Salon> recentData = new ArrayList<>();
    private ArrayList<Banner> bannerData = new ArrayList<>();
    private SalonAdapter recommendAdapter;
    private SalonAdapter popularAdapter;
    private SalonAdapter recentAdapter;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        headerImage = (ImageView) view.findViewById(R.id.header_iv);
        headerText = (TextView) view.findViewById(R.id.banner_text);
        recommendList = (RecyclerView) view.findViewById(R.id.recommend_list);
        popularList = (RecyclerView) view.findViewById(R.id.popular_list);
        recentList = (RecyclerView) view.findViewById(R.id.recent_list);
        recommendLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        popularLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recentLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recommendList.setLayoutManager(recommendLayoutManager);
        popularList.setLayoutManager(popularLayoutManager);
        recentList.setLayoutManager(recentLayoutManager);

        recommendAdapter = new SalonAdapter(getActivity(), recommendData);
        popularAdapter = new SalonAdapter(getActivity(), popularData);
        recentAdapter = new SalonAdapter(getActivity(), recentData);
        recommendList.setAdapter(recommendAdapter);
        popularList.setAdapter(popularAdapter);
        recentList.setAdapter(recentAdapter);

        new RecommendAsyncTask().execute(RECOMMEND_URL, POPULAR_URL, RECENT_URL, BANNER_URL);

        return view;
    }

    private class RecommendAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Загружаются данные...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... urls) {

            ArrayList<String> results = new ArrayList<>();
            String resultRecommend = "";
            String resultPopular = "";
            String resultRecent = "";
            String resultBanner = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        resultRecommend += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(resultRecommend);
                    JSONArray jsonArray = jsonObject.getJSONArray("salons");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Salon recommend = new Salon();
                        recommend.id = jsonData.getInt("id");
                        recommend.name = jsonData.getString("name");
                        recommend.type = jsonData.getString("type");
                        recommend.pictureUrl = jsonData.getString("pictureUrl");
                        recommendData.add(recommend);
                        results.add(0, resultRecommend);
                    }
                } else {
                    resultRecommend = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
                urlConnection = null;
            }

            try {
                url = new URL(urls[1]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        resultPopular += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(resultPopular);
                    JSONArray jsonArray = jsonObject.getJSONArray("salons");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Salon popular = new Salon();
                        popular.id = jsonData.getInt("id");
                        popular.name = jsonData.getString("name");
                        popular.type = jsonData.getString("type");
                        popular.pictureUrl = jsonData.getString("pictureUrl");
                        popularData.add(popular);
                        results.add(1, resultPopular);
                    }
                } else {
                    resultPopular = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
                urlConnection = null;
            }

            try {
                url = new URL(urls[2]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        resultRecent += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(resultRecent);
                    JSONArray jsonArray = jsonObject.getJSONArray("salons");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Salon recent = new Salon();
                        recent.id = jsonData.getInt("id");
                        recent.name = jsonData.getString("name");
                        recent.type = jsonData.getString("type");
                        recent.pictureUrl = jsonData.getString("pictureUrl");
                        recentData.add(recent);
                        results.add(2, resultRecent);
                    }
                } else {
                    resultRecent = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
                urlConnection = null;
            }

            try {
                url = new URL(urls[3]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    int data = reader.read();
                    while (data != -1) {
                        char current = (char) data;
                        resultBanner += current;
                        data = reader.read();
                    }

                    JSONObject jsonObject = new JSONObject(resultBanner);
                    JSONArray jsonArray = jsonObject.getJSONArray("banners");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonData = jsonArray.getJSONObject(i);
                        Banner banner = new Banner();
                        banner.id = jsonData.getInt("salonId");
                        banner.text = jsonData.getString("text");
                        banner.pictureUrl = jsonData.getString("pictureUrl");
                        bannerData.add(banner);
                    }
                } else {
                    resultBanner = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                assert urlConnection != null;
                urlConnection.disconnect();
                urlConnection = null;
            }

            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            Log.e("ERRORR", "onPostExecute: " + s);
            if (s.isEmpty()) {
                Toast.makeText(getActivity(), "Данные не загружены", Toast.LENGTH_LONG).show();
            }

            recommendAdapter.notifyDataSetChanged();
            popularAdapter.notifyDataSetChanged();
            recentAdapter.notifyDataSetChanged();

            for (Banner banner : bannerData) {
                if (banner.text != null) {
                    headerText.setText(banner.text);
                    Glide.with(getActivity()).load("http://zp.jgroup.kz" + banner.pictureUrl)
                            .error(android.R.drawable.stat_notify_error)
                            .into(headerImage);
                }
            }

            progressDialog.dismiss();
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}
