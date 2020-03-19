package com.cercledigital.listadetareas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = findViewById(R.id.listView);
        final TextAdapter adapter = new TextAdapter();

        readInfo();
        adapter.setData(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("¿Borrar tarea?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.remove(position);
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
            }
        });

        final Button newTaskButton = findViewById(R.id.newTaskButton);

        newTaskButton.setOnClickListener(new View.OnClickListener() {
        @Override
         public void onClick(View v) {
            final EditText taskInput = new EditText(MainActivity.this);
            taskInput.setSingleLine();
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Añade una nueva tarea")
                    .setMessage("¿Cual es tu nueva tarea?")
                    .setView(taskInput)
                    .setPositiveButton("Añadir tarea", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            list.add(taskInput.getText().toString());
                            adapter.setData(list);
                            saveInfo();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();
            dialog.show();
            }
           });

        final Button deleteAllTasksButton = findViewById(R.id.deleteAllTasksButton);
        deleteAllTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("¿Borrar todas las tareas?")
                        .setMessage("¿Realmente quieres borrar todas las tareas?")
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                list.clear();
                                adapter.setData(list);
                                saveInfo();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();


            }
        });
    }


    //Method to save information
    private void saveInfo(){
        try {
            File file = new File(this.getFilesDir(), "guardado");
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter((fOut)));
            for (int i = 0; i<list.size(); i++){
                bw.write(list.get(i));
                bw.newLine();
            }

            bw.close();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//Method to read info
    private void readInfo(){
        File file = new File(this.getFilesDir(), "guardado");
        if(!file.exists()){
            return;
        }
        try {
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while(line!=null){
                list.add(line);
                line = reader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class TextAdapter extends BaseAdapter {

        List<String> list = new ArrayList<>();

        void setData(List<String> mList) {
            list.clear();
            list.addAll(mList);
            notifyDataSetChanged();
        }

        //Returns the amount of views in the list
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item, parent, false);
            }

            final TextView textView = convertView.findViewById(R.id.task);
            //If task starts with important, it will be colored red. PD: It colours when another task is created after the important tag
            final String cellContent = textView.getText().toString();
            if (cellContent.startsWith("important")){
                textView.setBackgroundColor(Color.RED);
                textView.setTextColor(Color.BLACK);
            }
            textView.setText(list.get(position));
            return convertView;
        }
    }
}
