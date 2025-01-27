package com.company.econatia;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username,fullname,email,password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this , LoginActivity.class);
                ContextCompat.startForegroundService(RegisterActivity.this , intent);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait..");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(RegisterActivity.this , "All fields are necessary" , Toast.LENGTH_SHORT).show();
                }
                else if(str_password.length() < 6){
                    Toast.makeText(RegisterActivity.this , "Password must have 6 characters" , Toast.LENGTH_SHORT).show();
                }
                else{

                    register(str_username , str_fullname , str_password , str_email);

                }
            }
        });
    }

    private void register(final String username , final String fullname , String password , String email){

        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Rewards").child(firebaseUser.getUid());

                            HashMap<String , Object> hashMap2 = new HashMap<>();

                            hashMap2.put("econs" , 0);
                            reference2.setValue(hashMap2);


                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String , Object> hashMap = new HashMap<>();
                            hashMap.put("id" , userId);
                            hashMap.put("username" , username.toLowerCase());
                            hashMap.put("fullname" , fullname);
                            hashMap.put("bio" ,"");
                            hashMap.put("imageurl" ,"https://firebasestorage.googleapis.com/v0/b/insta-7eb98.appspot.com/o/placeholder.png?alt=media&token=62b22c89-6dbf-472e-a940-ee14020c138f");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        ContextCompat.startForegroundService(RegisterActivity.this , intent);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this , "You cannot login with this email and password" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
