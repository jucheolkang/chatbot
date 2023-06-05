package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Item> dataList = new ArrayList<>();

    EditText et;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    String format = "aa hh:mm";
    Calendar today = Calendar.getInstance();
    SimpleDateFormat type = new SimpleDateFormat(format);
    String tim = type.format(today.getTime());

    private final String BASEURL = "URL";
    private ApiService jsonPlaceHolderApi;
private String first = "글로벌 비즈니스학과에서 캡스톤 디자인으로 진행 중인 사업입니다. 많은 관심 부탁드립니다!\n" +
        "소형 폐가전 종류 / 소형 폐가전 처리 장소 / 소형 폐가전 분리배출 시 처리 방법에 관하여 문의해 주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et = findViewById(R.id.et);
        recyclerView = findViewById(R.id.RecyclerView);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        Gson gson = new GsonBuilder()
        .setLenient()
        .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        jsonPlaceHolderApi = retrofit.create(ApiService.class);

        dataList.add(new Item(first, "kpybot", tim, ViewType.LEFT_CHAT));
        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new MyAdapter(dataList));  // Adapter 등록
        et.setText("");
    }


    public void clickSend(View view) {
        String talk = String.valueOf(et.getText());


        dataList.add(new Item(talk, null, tim, ViewType.RIGHT_CHAT));

        recyclerView.setLayoutManager(manager); // LayoutManager 등록
        recyclerView.setAdapter(new MyAdapter(dataList));  // Adapter 등록
        et.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        createPost(talk);

    }

    private void createPost(String tk) {
        Post post = new Post(tk);

        Call<String> call = jsonPlaceHolderApi.createPost(post);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()) {
                    /*textViewResult.setText("code: " + response.code());*/
                    System.out.println("code: " + response.code());

                    return;
                }

                String postResponse = response.body();
                System.out.println("=============" + postResponse);
                String content = "";


                /*content = postResponse.getText();*/


                /*textViewResult.setText(content);*/
                dataList.add(new Item(postResponse, "kpybot", tim, ViewType.LEFT_CHAT));
                System.out.println(tim);
                recyclerView.setLayoutManager(manager); // LayoutManager 등록
                recyclerView.setAdapter(new MyAdapter(dataList));  // Adapter 등록
                et.setText("");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                /*textViewResult.setText(t.getMessage());*/

                System.out.println(t.getMessage());

            }
        });
    }

}
