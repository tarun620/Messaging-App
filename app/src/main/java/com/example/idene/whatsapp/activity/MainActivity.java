package com.example.idene.whatsapp.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.idene.whatsapp.R;
import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.fragment.ContactsFragment;
import com.example.idene.whatsapp.fragment.ConversationsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

//COMPLETED

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth authentication;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authentication = ConfigurationFirebase.getFirebaseAuthentication();

        //Configure a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Bulbul");
        setSupportActionBar(toolbar);//support for previous versions

        //Configure tabs
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversations", ConversationsFragment.class)
                        .add("Contacts", ContactsFragment.class)
                .create()
        );
        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPager);

        //search view setup
        searchView = findViewById(R.id.materialSearchPrincipal);

        //listener for search view
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {//when it is presented to the user

            }

            @Override
            public void onSearchViewClosed() {//when you close the search bar
                ConversationsFragment fragment = (ConversationsFragment) adapter.getPage(0);
                fragment.rechargeConversation();

            }
        });

        //listner for text box
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//this method is called when the user types the data and confirms
                //Log.d("event","onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//called at run time
                //Log.d("event","onQueryTextChange");

                //Check if you are searching for Conversations or Contacts from the tab that is active
                switch (viewPager.getCurrentItem()){//viewPager knows which fragment is being loaded, getCurrentItem () retrieves the selected item
                    case 0:
                        ConversationsFragment conversationsFragment = (ConversationsFragment) adapter.getPage(0);
                        if (newText != null && !newText.isEmpty()){
                            conversationsFragment.SearchConversations(newText.toLowerCase());//toLowerCase converts text to lowercase
                        }else{
                            conversationsFragment.rechargeConversation();
                        }
                        break;
                    case 1:
                        ContactsFragment contactsFragment = (ContactsFragment) adapter.getPage(1);
                        if (newText != null && !newText.isEmpty()){
                            contactsFragment.SearchContacts(newText.toLowerCase());
                        }else{
                            contactsFragment.rechargeContacts();
                        }
                        break;
                }



                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);

        //configure search button
        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }


    //retrieve selected menu items

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuExit:
                logoutUser();
                finish();
                break;
            case R.id.menuConfigurations:
                openConfigurations();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutUser(){

        try{
            authentication.signOut();
        }catch (Exception e){
            e.printStackTrace();

        }

    }

    public void openConfigurations(){
        Intent intent = new Intent(MainActivity.this, ConfigurationsActivity.class);
        startActivity(intent);
    }
}
