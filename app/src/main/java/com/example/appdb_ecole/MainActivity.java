package com.example.appdb_ecole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Map<String, String> server_urls = new HashMap<String,String>();
    EditText txt_id, txt_nom, txt_prenom, txt_cne, txt_classe,txt_data;
    Button btn_add, btn_show, btn_update, btn_delete, btn_clear;
    RequestQueue requestQueue;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.local)
        {
            Intent intent = new Intent(getApplicationContext(),local.class);
            startActivity(intent);
        }
        if (id == R.id.server)
        {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        String server_addr = getIntent().getStringExtra("server");
        server_urls.put("insert", server_addr + "/insert.php");
        server_urls.put("show", server_addr + "/show.php");
        server_urls.put("update", server_addr + "/update.php");
        server_urls.put("delete", server_addr + "/delete.php");
        server_urls.put("list", server_addr + "/viewList.php");

        txt_id = findViewById(R.id.txt_id);
        txt_nom = findViewById(R.id.txt_nom);
        txt_prenom = findViewById(R.id.txt_prenom);
        txt_cne = findViewById(R.id.txt_cne);
        txt_classe = findViewById(R.id.txt_classe);
        txt_data = findViewById(R.id.txt_data);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        btn_show = findViewById(R.id.btn_show);
        btn_clear = findViewById(R.id.btn_clear);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("insert");
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_id.setText("");
                txt_prenom.setText("");
                txt_nom.setText("");
                txt_cne.setText("");
                txt_classe.setText("");
                List();
            }
        });

        List();

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("show");
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest("update");
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                sendRequest("delete");
            }
        });
    }

    public void sendRequest(String op) {
        if (txt_id.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Please submit a valid ID !", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (op) {
            case "insert":
                Add(txt_nom.getText().toString(), txt_prenom.getText().toString(), txt_cne.getText().toString(), txt_classe.getText().toString());
                break;
            case "update":
                Update(txt_id.getText().toString(), txt_nom.getText().toString(), txt_prenom.getText().toString(), txt_cne.getText().toString(), txt_classe.getText().toString());
                break;
            case "show":
                Show(txt_id.getText().toString());
                break;
            case "delete":
                Delete(txt_id.getText().toString());
                break;
        }
        List();
        ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void Add(String nom, String prenom, String cne, String classe) {
        String server_insert_url = server_urls.get("insert");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_insert_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                            txt_id.setText("");
                            txt_nom.setText("");
                            txt_prenom.setText("");
                            txt_cne.setText("");
                            txt_classe.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("cne", cne);
                params.put("classe", classe);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void Show(String id) {
        String server_show_url = server_urls.get("show") + "?id=" + txt_id.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_show_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "";
                            if (jsonObject.getInt("success") == 0) {
                                message = "The Request Resource Does not Exist !";
                                txt_id.setText("");
                                txt_nom.setText("");
                                txt_prenom.setText("");
                                txt_cne.setText("");
                                txt_classe.setText("");

                            } else {
                                message = "Operation Success";
                                txt_nom.setText(jsonObject.getJSONObject("order").getString("nom"));
                                txt_prenom.setText(jsonObject.getJSONObject("order").getString("prenom"));
                                txt_cne.setText(jsonObject.getJSONObject("order").getString("cne"));
                                txt_classe.setText(jsonObject.getJSONObject("order").getString("classe"));
                            }
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    public void Update(String id, String new_name, String new_prenom, String new_cne, String new_classe) {
        String server_update_url = server_urls.get("update");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_update_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "";
                            if (jsonObject.getInt("success") == 1) {
                                message = "Update Success !";
                                txt_id.setText("");
                                txt_nom.setText("");
                                txt_prenom.setText("");
                                txt_cne.setText("");
                                txt_classe.setText("");
                            } else
                                message = "Error Updating Resource ! ";
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("nom", new_name);
                params.put("prenom", new_prenom);
                params.put("cne", new_cne);
                params.put("classe", new_classe);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void Delete(String id) {
        String server_delete_url = server_urls.get("delete") + "?id=" + txt_id.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_delete_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = "";
                            if (jsonObject.getInt("success") == 1) {
                                message = "Operation Success";
                                txt_id.setText("");
                                txt_nom.setText("");
                                txt_prenom.setText("");
                                txt_cne.setText("");
                                txt_classe.setText("");

                            } else
                                message = "Error Deleting Resource";
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );

        requestQueue.add(stringRequest);
    }

    public void List() {
        String server_list_url = server_urls.get("list");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, server_list_url,
                new Response.Listener<String>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(String response) {
                        try {
                            txt_data.setText("");
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("success") == 1) {
                                StringBuilder orders = new StringBuilder();
                                orders.append("ID | NOM | PRENOM | CNE | CLASSE \n");
                                orders.append("----------------------------------------------- \n");
                                JSONArray liste = jsonObject.getJSONArray("orders");
                                for (int i = 0; i < liste.length(); i++) {
                                    int id = liste.getJSONObject(i).getInt("id");
                                    String nom = liste.getJSONObject(i).getString("nom");
                                    String prenom = liste.getJSONObject(i).getString("prenom");
                                    String cne = liste.getJSONObject(i).getString("cne");
                                    String classe = liste.getJSONObject(i).getString("classe");
                                    orders.append(String.format("%d | %s | %s | %s | %s \n", id, nom, prenom,cne,classe));
                                    orders.append("-------------------------------------------- \n");
                                }
                                txt_data.setText(orders);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "e" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "err" + error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }
        );
        requestQueue.add(stringRequest);
    }
}