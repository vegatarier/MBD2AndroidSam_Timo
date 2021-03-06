package nl.samtimo.pubcrawl.authentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.samtimo.pubcrawl.MenuActivity;
import nl.samtimo.pubcrawl.Pub;
import nl.samtimo.pubcrawl.R;
import nl.samtimo.pubcrawl.Race;
import nl.samtimo.pubcrawl.Util;
import nl.samtimo.pubcrawl.request.Request;
import nl.samtimo.pubcrawl.request.RequestMethod;
import nl.samtimo.pubcrawl.request.RequestTask;
import nl.samtimo.pubcrawl.ui.ColorAppCompatActivity;
import nl.samtimo.pubcrawl.users.CurrentUser;

public class LoginActivity extends ColorAppCompatActivity {
    private TextView authenticationMessage;

    // UI references.
    private TextInputEditText mUsernameView;
    private TextInputEditText mPasswordView;

    public static CurrentUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authenticationMessage = (TextView)findViewById(R.id.tvAuthentication);

        mUsernameView = (TextInputEditText) findViewById(R.id.username);

        mPasswordView = (TextInputEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.sign_in_button);
        mUserSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        //TODO: remove this button
        Button simpleLoginButton = (Button) findViewById(R.id.simple_login_button);
        simpleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = new Request(RequestMethod.POST, "login", "username=hitchhiker&password=password", null);
                new RequestTask(LoginActivity.this).execute(request);
            }
        });

        Button mUserSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mUserSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart();

        authenticationMessage.setText(R.string.textview_authentication_placeholder);
        authenticationMessage.setTextColor(Color.BLACK);
    }

    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            authenticationMessage.setText(R.string.textview_authentication_failed);
            authenticationMessage.setTextColor(Color.RED);
        } else {
            authenticationMessage.setText(R.string.textview_authentication_passed);
            authenticationMessage.setTextColor(Color.BLACK);

            Request request = new Request(RequestMethod.POST, "login", "username="+username+"&password="+password, null);
            new RequestTask(this).execute(request);
        }
    }

    public void login(String result){
        try{
            JSONObject jsonUser = new JSONObject(result);
            if(jsonUser!=null){
                JSONArray jsonRaces = jsonUser.getJSONArray("race");
                ArrayList<Race> races = new ArrayList<>();
                for (int i=0; i<jsonRaces.length(); i++) {
                    JSONObject jsonRace = jsonRaces.getJSONObject(i);

                    JSONArray jsonPubs = jsonRace.getJSONArray("tagged");
                    ArrayList<Pub> pubs = new ArrayList<>();
                    for(int j=0;j<jsonPubs.length();j++) pubs.add(new Pub(jsonPubs.getString(j), null, true, null));

                    String id = Util.getJsonString(jsonRace, "id");
                    String name = Util.getJsonString(jsonRace, "name");
                    String startDate = Util.getJsonString(jsonRace, "startDate");
                    String endDate = Util.getJsonString(jsonRace, "endDate");
                    races.add(new Race(id, name, pubs, startDate, endDate));
                }
                user = new CurrentUser(jsonUser.getString("_id"), jsonUser.getString("username"), races);
                Intent intent = new Intent(this, MenuActivity.class);
                startActivity(intent);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void simpleSignIn(){

    }

    //TODO find more appropriate way
    /*public void updateUser(){

    }

    public void updateUserFinish(){

    }*/
}
