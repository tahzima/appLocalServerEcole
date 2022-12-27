package com.example.appdb_ecole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class local extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText nom, prenom,id,cne, classe;
    Button btnAddData;
    Button btnviewAll;
    Button btnDelete;
    Button btnviewUpdate;

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
            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        myDb = new DatabaseHelper(this);

        id = findViewById(R.id.id);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        cne = findViewById(R.id.cne);
        classe = findViewById(R.id.classe);

        btnAddData = (Button)findViewById(R.id.button_add);
        btnviewAll = (Button)findViewById(R.id.button_viewAll);
        btnviewUpdate= (Button)findViewById(R.id.button_update);
        btnDelete= (Button)findViewById(R.id.button_delete);

        AddData();
        viewAll();
        UpdateData();
        DeleteData();
    }
    public void DeleteData() {
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(id.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(local.this,"Data Deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(local.this,"Data not Deleted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void UpdateData() {
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdate = myDb.updateData(
                                id.getText().toString(),
                                nom.getText().toString(),
                                prenom.getText().toString(),
                                cne.getText().toString(),
                                classe.getText().toString()
                                );
                        if(isUpdate == true)
                            Toast.makeText(local.this,"Data Update",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(local.this,"Data not Updated",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public  void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(
                                id.getText().toString(),
                                nom.getText().toString(),
                                prenom.getText().toString(),
                                cne.getText().toString(),
                                classe.getText().toString());
                        if(isInserted == true)
                            Toast.makeText(local.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(local.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void viewAll() {
        btnviewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("id :"+ res.getString(0)+"\n");
                            buffer.append("nom :"+ res.getString(1)+"\n");
                            buffer.append("prenom :"+ res.getString(2)+"\n");
                            buffer.append("cne :"+ res.getString(3)+"\n");
                            buffer.append("classe :"+ res.getString(4)+"\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


}
