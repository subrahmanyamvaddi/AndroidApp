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
public class LoginFragment extends Fragment {

    Button login;
    EditText id,password;
    private HomeFragment.OnDbOpListener dbOpListener;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login = view.findViewById(R.id.button3);
        id = view.findViewById(R.id.username_Login);
        password = view.findViewById(R.id.password_login2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDBHelper dbHelper = new LoginDBHelper(getActivity());

                SQLiteDatabase database = dbHelper.getReadableDatabase();

                int ret = dbHelper.CheckRecordExists(id.getText().toString(),password.getText().toString(),database);
                switch (ret)
                {
                    case 0:
                        Toast.makeText(getActivity(),"User not found!",Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"User Logged in successfully",Toast.LENGTH_SHORT).show();
                        dbOpListener.dpOpPerformed(1);
                        break;
                    case 2:
                        Toast.makeText(getActivity(),"Incorrect username or password!",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        Activity activity = (Activity)context;
        try {
            dbOpListener = (HomeFragment.OnDbOpListener)activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement the interface method..");
        }
    }
}
