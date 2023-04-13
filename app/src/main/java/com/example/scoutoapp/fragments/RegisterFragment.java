package com.example.scoutoapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scoutoapp.DashboardActivity;
import com.example.scoutoapp.R;
import com.example.scoutoapp.db.CarsDB;

import org.w3c.dom.Text;

public class RegisterFragment extends Fragment {
    private EditText emailEdt, passwordEdt;
    private Button registerBtn;
    private TextView loginTV;
    private SharedPreferences pref;
    private CarsDB dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        emailEdt = view.findViewById(R.id.idRegisterEmailEdt);
        passwordEdt = view.findViewById(R.id.idRegisterPassEdt);
        registerBtn = view.findViewById(R.id.idRegisterBtn);
        loginTV = view.findViewById(R.id.idGoForLoginTV);

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    androidx.fragment.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frameLayout, new LoginFragment());
                    ft.addToBackStack("register");
                    ft.commit();
                }
            }
        });

        pref = requireActivity().getSharedPreferences("user_account", Context.MODE_PRIVATE);
        dbHelper = new CarsDB(getActivity().getApplicationContext());

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEdt.getText().length() > 0 && passwordEdt.getText().length() > 0) {
                    String email = emailEdt.getText().toString();
                    String password = passwordEdt.getText().toString();

                    if (!isEmailCorrect(email)) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please enter correct email like example@gmail.com", Toast.LENGTH_LONG).show();
                    } else if (!isPasswordCorrect(password)) {
                        Toast.makeText(getActivity().getApplicationContext(), "Password must contain at least 8 characters with at least one uppercase letter, one lowercase letter, and one number", Toast.LENGTH_LONG).show();
                    } else {

                        if (dbHelper.isUserAvailable(email, password) != -1) {
                            Toast.makeText(getActivity().getApplicationContext(), "Account already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            dbHelper.addUser(email, password);
                        }
                        emailEdt.getText().clear();
                        passwordEdt.getText().clear();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("user_email", email);
                        editor.putString("user_password", password);
                        editor.putInt("user_id", dbHelper.isUserAvailable(email, password));
                        editor.apply();
                        // Start home activity
                        startActivity(new Intent(getActivity(), DashboardActivity.class));
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please give email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean isEmailCorrect(String email) {
        if (email.length() < 10) return false;
        if (!email.contains("@")) return false;
        if (!email.contains(".com")) return false;
        return true;
    }

    private boolean isPasswordCorrect(String password) {
        char[] specialChars = {
                '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+',
                '=', '{', '}', '[', ']', '|', '\\', ':', ';', '<', '>', ',', '.', '?', '/'
        };

        if (password.length() < 8) return false;
        boolean containsSpecial = false;
        for (char i : specialChars) {
            if (password.contains(Character.toString(i))) {
                containsSpecial = true;
                break;
            }
        }
        if (!containsSpecial) {
            return false;
        }

        char[] numerals = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        boolean containsNumber = false;
        for (char i : numerals) {
            if (password.contains(Character.toString(i))) {
                containsNumber = true;
                break;
            }
        }

        if (!containsNumber) return false;

        return true;
    }
}




