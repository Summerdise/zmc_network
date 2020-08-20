package com.example.zmc_network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private int times = 1;
    @BindView(R.id.get_information_button)
    Button getInformationBtn;
    @BindView(R.id.get_start_times)
    Button getStartTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        ButterKnife.bind(this);
        getInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        .get()
                        .url("https://twc-android-bootcamp.github.io/fake-data/data/default.json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try (Response response = client.newCall(request).execute()) {
                                    Wrapper wrapper = jsonToWrapper(response.body().string());
                                    Toast.makeText(MainActivity.this, findFirstPersonNameInWrapper(wrapper), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Toast.makeText(MainActivity.this, "fail to connect", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        getStartTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, times+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Wrapper jsonToWrapper(String jsonStatus) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonParser().parse(jsonStatus).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
        ArrayList<Person> dataList = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            Person person = gson.fromJson(element, new TypeToken<Person>() {
            }.getType());
            dataList.add(person);
        }
        return new Wrapper(dataList);
    }

    public String findFirstPersonNameInWrapper(Wrapper wrapper) {
        if (0 == wrapper.personList.size()) {
            return "no person exist";
        } else {
            return wrapper.personList.get(0).name;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        times++;
    }
}