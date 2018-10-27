package com.newware.wonderimage;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.newware.wonderimage.config.AppConfig;
import com.newware.wonderimage.utils.NotificationUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Start variable for firebase
    private final static int RC_SIGN_IN = 666;
    private final static String TAG = "firebase";
    private Uri personPic;
    private String userName, personPicString;
    private String userEmail;
    private String uid;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleClient;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userId;

    //ends

    private ProgressDialog progressDialog;
    private MaterialDialog materialDialog;
    RelativeLayout relativeLayout;
    SharedPreferences sharedPreferences;
    String query;
    View headerview;
    TextView tv_userName, tv_email;
    ImageView ivuserImg;

    private BroadcastReceiver mBroadcastReceiver;
    private int REQUEST_INVITE = 786;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            chngedUserData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AppConfig.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(AppConfig.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.content_main);
        handleIntent(getIntent());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerview = navigationView.getHeaderView(0);
        TextView tvgoole = headerview.findViewById(R.id.tv_google);

        tv_userName = headerview.findViewById(R.id.tvStudio);
        tv_email = headerview.findViewById(R.id.tvDeveloper);
        ivuserImg = headerview.findViewById(R.id.iv_userImg);
        tvgoole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                signIn();
            }
        });

        //notification system
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(AppConfig.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(AppConfig.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);

                    Log.e(TAG, "Firebase reg id: " + regId);
                }
            }
        };


        // to ckeck if user read EULA
        sharedPreferences = getSharedPreferences("eula", MODE_PRIVATE);


        if (!sharedPreferences.getString("eula", "").equals("read1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.eula, null);
            builder.setCancelable(false);
            builder.setView(dialogView);
            Button btnReadOk = dialogView.findViewById(R.id.btn_eula_ok);

            final AlertDialog thisWillShow = builder.create();

            btnReadOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    thisWillShow.dismiss();
                    editor.putString("eula", "read1");
                    editor.apply();
                }
            });
            thisWillShow.show();

        }

        //fire base start
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    chngedUserData();
                    //Toast.makeText(MainActivity.this, "Please Login ", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Snackbar.make(relativeLayout, "Loading Content", Snackbar.LENGTH_LONG).show();


        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        //getting client
        googleClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //check if account is logged in
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            userId = account.getId();
//            //signInButton.setEnabled(false);
//            //Toast.makeText(this, "Welcome " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
//
//        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if ((firebaseUser != null)) {
            userId = firebaseUser.getUid();
            chngedUserData();
            // Toast.makeText(getBaseContext(), "Welcome Back " + firebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();

        }


        //firebase ends

        showFragment(RandomFragment.class); //default fragment
    }

    // fire base methods Start
    private void signIn() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                userName = account.getDisplayName();
                userEmail = account.getEmail();
                personPic = account.getPhotoUrl();
                if (personPic != null) {
                    personPicString = personPic.toString();
                }
                //AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                handleSignInResult(task);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Login Failed " + e, Toast.LENGTH_SHORT).show();
                // ...
            }
        }

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(@NonNull GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                Snackbar snackbar = Snackbar.make(relativeLayout,
                                        "Hey, " + user.getDisplayName()
                                        , Snackbar.LENGTH_LONG)
                                        .setAction("Open My Collection", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(MainActivity.this, CurentUserActivity.class));
                                            }
                                        });
                                //editing view of snackbar

                                View snackbarEdited = snackbar.getView(); //getting view

                                snackbarEdited.setBackgroundColor(ContextCompat.getColor(getApplicationContext()
                                        , R.color.colorPrimary)); // setting background

                                TextView snackbarText = snackbarEdited.
                                        findViewById(android.support.design.R.id.snackbar_text);// getting text reference

                                snackbarText.setTextColor(ContextCompat.getColor(getApplicationContext()
                                        , R.color.colorAccent)); // setting text color

                                Button snackbarbtn = snackbarEdited
                                        .findViewById(android.support.design.R.id.snackbar_action);
                                snackbarbtn.setTextColor(ContextCompat.getColor(getApplicationContext()
                                        , R.color.warning)); // action btn color

                                snackbar.show();
                            }
                            if (user != null) {
                                personPic = user.getPhotoUrl();
                            }
                            if (user != null) {
                                userName = user.getDisplayName();
                            }
                            if (user != null) {
                                uid = user.getUid();
                            }
                            if (user != null) {
                                userEmail = user.getEmail();
                            }
                            sendDataToFirebase();


                            System.out.println(" nm " + userName + " id " + uid + " em " + userEmail);
                            chngedUserData();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.content_main), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void sendDataToFirebase() {


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("users").child(uid).child("userInfo");

        UserDataToSendToFireBase userInfo = new UserDataToSendToFireBase(userName, userEmail, personPicString);
        databaseReference.setValue(userInfo);

    }

    private void chngedUserData() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (firebaseUser != null) {
                        Glide.with(MainActivity.this)
                                .load(firebaseUser.getPhotoUrl())
                                .into(ivuserImg);
                    }
                    if (firebaseUser != null) {
                        tv_email.setText(firebaseUser.getEmail());
                    }
                    if (firebaseUser != null) {
                        tv_userName.setText(firebaseUser.getDisplayName());
                    }
                }
            });
        } else {
            tv_email.setText("developer@newware.com");
            tv_userName.setText("NewWare Apps");
            ivuserImg.setImageResource(R.drawable.ic_account_circle_black_24dp);

        }
    }


    private void signOutGoogle() {
        FirebaseAuth.getInstance().signOut();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Snackbar.make(relativeLayout, "Signed Out", Snackbar.LENGTH_LONG).show();
                    chngedUserData();
                }

            }
        };
    }
//firebase methods ends

    // drawer item click listener
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragment = null;

        switch (id) {
            case R.id.nav_ocean:
                fragment = OceanFragment.class;
                showFragment(fragment);

                break;
            case R.id.nav_flower:
                fragment = FlowerFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_ranfom:
                fragment = RandomFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_Landscape:
                fragment = LandscapesFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_Animals:
                fragment = AnimalsFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_Kitten:
                fragment = KittenFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_puppies:
                fragment = PuppiesFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_birds:
                fragment = BirdsFragment.class;
                showFragment(fragment);
                break;
            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, AboutMe.class));
                break;
            case R.id.nav_Currentuser: {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(MainActivity.this);
                    materialDialog.canceledOnTouchOutside(false)
                            .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                            .title("Sign In with Google")
                            .titleColor(getResources().getColor(R.color.warning))
                            .content("To explore your or other's collection, SignIn with your google account")
                            .contentColor(getResources().getColor(R.color.colorAccent))
                            .positiveText("Log me In")
                            .negativeText("Cancel")
                            .negativeColor(getResources().getColor(R.color.warning))
                            .positiveColor(getResources().getColor(R.color.colorAccent))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    signIn();
                                    chngedUserData();
                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();

                } else {
                    startActivity(new Intent(MainActivity.this, CurentUserActivity.class));
                }


            }
            break;
            case R.id.nav_otherUser:
                FirebaseAuth firebaseAuthAll = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser1 = firebaseAuthAll.getCurrentUser();
                if (firebaseUser1 == null) {
                    MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(MainActivity.this);
                    materialDialog.canceledOnTouchOutside(false)
                            .backgroundColor(getResources().getColor(R.color.colorPrimaryDark))
                            .title("Sign In with Google")
                            .titleColor(getResources().getColor(R.color.warning))
                            .content("TO explore your or other's collection, SignIn with your google account")
                            .contentColor(getResources().getColor(R.color.colorAccent))
                            .positiveText("Log me In")
                            .negativeText("Cancel")
                            .negativeColor(getResources().getColor(R.color.warning))
                            .positiveColor(getResources().getColor(R.color.colorAccent))
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    signIn();
                                    chngedUserData();
                                }
                            }).onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
                } else {
                    startActivity(new Intent(MainActivity.this, OtherUserCollectionActivity.class));
                }
                break;
            case R.id.nav_send: {
                onInviteClicked();
                break;
            }
            default:
                return false;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // show fragment method to open fragment on main activity
    private void showFragment(Class fragmentClass) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flContent_main, fragment)
                .commit();
    }


    //on clear chache


    // search view //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =
                (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        }

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if ((firebaseUser == null) && (account == null)) {
            menu.removeItem(R.id.menu_logout);
        }

        return true;
    }

    // showing search values
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY).trim();

            showFragment(SearchResultFragment.class);


        }
    }

    // to send data to fragment
    public String getMyData() {
        return query = query.replace(" ", "+");
    }

    // hide drawer if open on back button
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_clear): {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Clicking Yes will Clear Cache\nThis will re-load all image from Internet\nAnd restart app." +
                        "\nContinue ? ");
                builder.setTitle(Html.fromHtml("<font color='#D50000'>Clear Cache</font>"))
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Snackbar.make(relativeLayout, "Caches Cleared, Restarting app", Snackbar.LENGTH_SHORT).show();
                                deleteCache(MainActivity.this);
                                Intent i = getBaseContext().getPackageManager()
                                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                if (i != null) {
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    finish();
                                }
                                startActivity(i);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                AlertDialog dialog = builder.create();
                dialog.show();
                Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setTextColor(ContextCompat.getColor(this, R.color.warning));
                Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                break;
            }
            case R.id.menu_logout: {
                signOut();
                chngedUserData();
            }

            default:
                break;
        }

        return false;

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        signInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        Snackbar.make(relativeLayout, "Signed Out", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            Toast.makeText(context, "Error Clearing Cache - " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
    //for Current User Fragment


    public void setActionBarTitle(String title, String userName) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setSubtitle(userName);
    }


    private void onInviteClicked() {

        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
               // .setCallToActionText(getString(R.string.invitation_deep_link))
               // .setEmailSubject("Install this App")
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }


}

