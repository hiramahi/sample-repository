package com.example.shortmemoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private Spinner spTitle;
    private static final String DEFAULT_TITLE = "登録内容";
    private List<String> list = new ArrayList<String>(Arrays.asList(DEFAULT_TITLE));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences に登録されているデータを取得
        SharedPreferences sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        for (String key : map.keySet()) {
            list.add(key);
        }

        // Spinner 用のアダプタとしてインスタンス化
        // ArrayAdapter<>(どこに作るか, どの目的で使うか(今回は、spinnerとして), array/list(任意)) → コンストラクタが3つある
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        // Spinner の見せ方を指定
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // xml の Spinner と結合
        spTitle = findViewById(R.id.spTitle);
        spTitle.setAdapter(adapter);

        // Spinner 選択イベントリスナの初期化
        spTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // onCreate 時の発火を回避 + 初期値が選択された時は処理しない
                if (pos == 0) {
                    setEditText();
                    return;
                }

                // castする(AdapterView はスーパークラスである → Spinner 以外にも使われる)
                Spinner spinner = (Spinner) adapterView;
                // Spinner は String 以外の型のデータも入れることができる → 戻り値は Object 型なので、対応する型で cast する必要がある
                String title = (String) spinner.getSelectedItem();

                SharedPreferences sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE);
                String content = sharedPreferences.getString(title, "データがありません");

                setEditText(title, content);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // クリックリスナの初期化
        findViewById(R.id.btRegister).setOnClickListener(view -> { register(); });
        findViewById(R.id.btDelete).setOnClickListener(view -> { delete(); });
        findViewById(R.id.btDeleteAll).setOnClickListener(view -> { deleteAll(); });

        // デバッグ用
        // findViewById(R.id.btDebug).setOnClickListener(view -> {
        //     Log.d("SpinnerList", String.valueOf(list));
        // });
    }

    private void register() {
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etContent = findViewById(R.id.etContent);

        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        String toastText = "";
        if (!(title.equals("") || content.equals(""))) {
            // SharedPreferences に登録
            SharedPreferences sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(title, content);
            editor.commit();

            // Spinner に登録
            adapter.add(title);

            toastText = "登録しました";
        } else {
            toastText = "タイトルと内容を入力してください";
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        setEditText();
    }

    private void delete() {
        EditText etTitle = findViewById(R.id.etTitle);
        String title = etTitle.getText().toString();

        String toastText = "";
        if (!(title.equals("") || title.equals(DEFAULT_TITLE))) {
            // SharedPreferences から削除
            SharedPreferences sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE);

            if (sharedPreferences.getString(title, "DEFAULT_CONTENT") != "DEFAULT_CONTENT") {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(title);
                editor.commit();

                // Spinner から削除、表示リセット
                adapter.remove(title);
                spTitle.setSelection(0);

                toastText = "削除しました";
            } else {
                toastText = "タイトルは選択肢から選んでください";
            }

        } else {
            toastText = "タイトルを入力してください";
        }
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        setEditText();
    }

    private void deleteAll() {
        // SharedPreferences から全件削除
        SharedPreferences sharedPreferences = getSharedPreferences("memo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        // Spinner から全件削除
        adapter.clear();
        adapter.add(DEFAULT_TITLE);

        Toast.makeText(this, "全件削除しました", Toast.LENGTH_SHORT).show();
        setEditText();
    }

    private void setEditText() {
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etContent = findViewById(R.id.etContent);

        etTitle.setText("");
        etContent.setText("");
    }

    private void setEditText(String title, String content) {
        EditText etTitle = findViewById(R.id.etTitle);
        EditText etContent = findViewById(R.id.etContent);

        etTitle.setText(title);
        etContent.setText(content);
    }
}