package com.example.zmc_network;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private final String timesOfStart = "Start times";
    private final String url = "https://twc-android-bootcamp.github.io/fake-data/data/default.json";
    @BindView(R.id.get_information_button)
    Button getInformationBtn;
    @BindView(R.id.get_start_times)
    Button getStartTimes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        updateTimesStart(getTimesStart());
        initDatabase(url);
        getInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatus(url);
            }
        });

        getStartTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show(MainActivity.this, getTimesStart() + "");
            }
        });
    }

    public void showStatus(String url) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Wrapper wrapper = jsonToWrapper(result);
                    String firstName = findFirstPersonNameInWrapper(wrapper);
                    show(MainActivity.this, firstName);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Wrapper wrapper = getStatusFromDatabase();
                String firstName = findFirstPersonNameInWrapper(wrapper);
                show(MainActivity.this, firstName);
            }
        });
    }

    public static void show(Context context, String text) {
        Toast toast = null;
        try {
            if (toast != null) {
                toast.setText(text);
            } else {
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
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

    public int getTimesStart() {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(timesOfStart, Context.MODE_PRIVATE);
    }

    public void updateTimesStart(int timesNowStart) {
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(timesOfStart, timesNowStart + 1);
        editor.apply();
    }

    public Wrapper getStatusFromDatabase() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "person").build();
        return new Wrapper(db.personDao().getAll());
    }

    public void initDatabase(String url) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Wrapper wrapper = jsonToWrapper(result);
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "person").build();
                    for (Person person : wrapper.personList) {
                        db.personDao().insertAll(person);
                    }
                }
            }

        });
    }
}