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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.scoutoapp.DashboardActivity;
import com.example.scoutoapp.R;
import com.example.scoutoapp.db.CarsDB;

public class LoginFragment extends Fragment {

    private EditText emailEdt, passwordEdt;
    private Button loginBtn;
    private CarsDB dbHelper;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        emailEdt = view.findViewById(R.id.idLoginEmailEdt);
        passwordEdt = view.findViewById(R.id.idLoginPassEdt);
        loginBtn = view.findViewById(R.id.idLoginBtn);
        dbHelper = new CarsDB(getActivity().getApplicationContext());
        pref = requireActivity().getSharedPreferences("user_account", Context.MODE_PRIVATE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEdt.getText().length() > 0 && passwordEdt.getText().length() > 0) {
                    String email = emailEdt.getText().toString();
                    String password = passwordEdt.getText().toString();

                    if (!isEmailCorrect(email)) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please enter correct email like example@gmail.com",
                                Toast.LENGTH_LONG).show();

                    } else if (!isPasswordCorrect(password)) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Password must contain at least 8 characters with at least one uppercase letter, one lowercase letter, and one number",
                                Toast.LENGTH_LONG).show();
                    } else {

                        if (dbHelper.isUserAvailable(email, password) != -1) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Log In: Success",
                                    Toast.LENGTH_SHORT).show();

                            emailEdt.getText().clear();
                            passwordEdt.getText().clear();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("user_email", email);
                            editor.putString("user_password", password);
                            editor.putInt("user_id", dbHelper.isUserAvailable(email, password));
                            editor.apply();
                            getActivity().finish();
                            // starts home activity
                            startActivity(new Intent(getActivity(), DashboardActivity.class));
                        } else {
                            FragmentTransaction ft = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                            androidx.fragment.app.FragmentManager fm = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                            int countOfFragments = fm.getBackStackEntryCount();
                            for (int i = 0; i <= countOfFragments; i++) {
                                fm.popBackStack();
                            }
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "user_name or password is not available please register",
                                    Toast.LENGTH_SHORT).show();
                            ft.replace(R.id.frameLayout, new RegisterFragment());
                            ft.commit();
                        }
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please give email and password",
                            Toast.LENGTH_SHORT).show();
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
        for (char c : specialChars) {
            if (password.contains(String.valueOf(c))) {
                containsSpecial = true;
                break;
            }
        }
        if (!containsSpecial) {
            return false;
        }
        char[] numerals = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        boolean containsNumber = false;
        for (char c : numerals) {
            if (password.contains(String.valueOf(c))) {
                containsNumber = true;
                break;
            }
        }

        if (!containsNumber) return false;

        return true;
    }
}

