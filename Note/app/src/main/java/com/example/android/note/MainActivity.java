package com.example.android.note;

import android.Manifest;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import static com.example.android.note.R.layout.cdialog;

public class MainActivity extends AppCompatActivity {
    private EditText title ;
    private EditText content ;
    private SharedPreferences preferences;
    private boolean inter;
    private SharedPreferences.Editor editor;
    final int EXT_PERM =2;
    private boolean exter;
    public final String PERMKEY = "switches";
    private  Switch internal , external;
    public static final String prefkey = "Save Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title    =  (EditText) findViewById(R.id.title);
        content  =  (EditText) findViewById(R.id.content);
        preferences = getSharedPreferences(prefkey,MODE_PRIVATE);
        exter = preferences.getBoolean(getString(R.string.locationex),false);
        inter = preferences.getBoolean(getString(R.string.locationin),false);
         if(inter == exter)
         {
           inter=true;
          exter=false;
         }

    }
    public void clear(View view){
        content.setText("");
        title.setText("");
    }
public  void load(View view){
    String name = title.getText().toString()+".txt";

if(inter) {
    try {
        FileInputStream fis = openFileInput(name);

        Scanner scanner = new Scanner(fis);
        content.setText("");
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine() + "\n");
        }

        scanner.close();
        fis.close();
    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

    } finally {
        Toast.makeText(this, "File has been Loaded", Toast.LENGTH_LONG).show();
    }
}
if(exter){
    try {
        File file = getFile(name);
        FileInputStream fis = new FileInputStream(file);

        Scanner scanner = new Scanner(fis);
        content.setText("");
        while (scanner.hasNextLine()) {
            content.append(scanner.nextLine() + "\n");
        }

        scanner.close();
        fis.close();
    } catch (Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    } finally {
        Toast.makeText(this, "File has been Loaded", Toast.LENGTH_LONG).show();
    }
    }
}


public void save(View view){
    String name = title.getText().toString()+".txt";
    String text = content.getText().toString();
    FileOutputStream fos = null;
    try {
       if(inter)
        fos = openFileOutput(name,MODE_APPEND);
        if (exter)
            fos=new FileOutputStream(getFile(name));
        fos.write(text.getBytes());
        fos.close();
        Toast.makeText(this,"File has been Saved ",Toast.LENGTH_LONG).show();
    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
    }
    

}

public void delete(View view) {
    String name = title.getText().toString()+".txt";
    File file = null;
if(inter)
  file = new File(getFilesDir(),name);
    else if (exter)
        file = getFile(name);
    if (file.exists())
    {
            if(inter)
                deleteFile(name);
            if(exter )
                file.delete();
        if (!file.exists())
        {
          Toast.makeText(this,"File has been deleted",Toast.LENGTH_LONG).show();
        }
    }   else {
            Toast.makeText(this,"File was Not Found (FOF)",Toast.LENGTH_LONG).show();
    }


}
public boolean isExternalWritable(){
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuitems,menu);
        editor = getSharedPreferences(prefkey,MODE_PRIVATE).edit();
        return super.onCreateOptionsMenu(menu);

    }

private File getFile(String name){
    return new File(Environment.getExternalStorageDirectory(),name);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.location:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(cdialog);
                dialog.setTitle("Select Save Location");

                internal =(Switch) dialog.findViewById(R.id.in);
                external =(Switch) dialog.findViewById(R.id.ex);

                if(preferences.getString(PERMKEY,"").equals("denied"))
                {
                    internal.setVisibility(View.INVISIBLE);
                    external.setVisibility(View.INVISIBLE);
                }
                internal.setChecked(inter);
                external.setChecked(exter);
                Button saver = (Button) dialog.findViewById(R.id.settingssave);
                saver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void inter(View v){
        if(PackageManager.PERMISSION_GRANTED!=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE ))
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXT_PERM);
        }
        if(isExternalWritable())
        {
        editor.putBoolean(getString(R.string.locationin),!inter);
        editor.apply();
        inter = !inter;
        if(inter == exter)
            exter = !exter;
        external.setChecked(exter);
    }
        else{
            inter = true;
            exter = false;
            internal.setChecked(true);
            external.setChecked(false);
        }
    }
    public void exter(View v){

        if(PackageManager.PERMISSION_GRANTED!=ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXT_PERM);
        }
    if(isExternalWritable())
    {
        editor.putBoolean(getString(R.string.locationex),!exter);
        editor.apply();
        exter = !exter;
        if(inter == exter)
            inter = !inter;
        internal.setChecked(inter);

    }
    else{
        inter = true;
        exter = false;
        internal.setChecked(true);
        external.setChecked(false);
    }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXT_PERM: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {
                    external.setChecked(false);
                    internal.setChecked(true);
                    inter=true;
                    exter=false;
                    external.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(),"External Storage Permission Not Granted",Toast.LENGTH_LONG).show();
                    }
                });

                    editor.putString(PERMKEY,"denied");
                }
            }

        }
    }
}
