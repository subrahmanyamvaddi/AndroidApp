package com.example.subrahmanyamvaddi.detect;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button bnRegister, bnLogin;
    private OnDbOpListener dbOpListener;

    EditText Id,Name,Email,Password,ConfirmPassword;

    public HomeFragment() {
        // Required empty public constructor
    }

    public interface OnDbOpListener{
        public void dpOpPerformed(int method);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bnRegister = view.findViewById(R.id.create_button);

        bnLogin = view.findViewById(R.id.login_button);
        bnLogin.setOnClickListener(this);

        Id = view.findViewById(R.id.username_login2);
        Name = view.findViewById(R.id.name_TV);
        Email = view.findViewById(R.id.email_TV);
        Password = view.findViewById(R.id.password_login);
        ConfirmPassword = view.findViewById(R.id.confirmPassword_TV);

        bnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Password.getText().toString().equals(ConfirmPassword.getText().toString())) {

                    LoginDBHelper dbHelper = new LoginDBHelper(getActivity());

                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    dbHelper.addUser(Id.getText().toString(),Name.getText().toString(),Email.getText().toString(),Password.getText().toString(),database);
                    dbHelper.close();

                    Id.setText("");
                    Name.setText("");
                    Password.setText("");
                    Email.setText("");
                    ConfirmPassword.setText("");

                    Toast.makeText(getActivity(),"User saved successfully",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),"Error: Password and confirm password does not match!",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.login_button:
                dbOpListener.dpOpPerformed(0);
                break;
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity activity = (Activity)context;
        try {
            dbOpListener = (OnDbOpListener)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement the interface method..");
        }
    }
}
