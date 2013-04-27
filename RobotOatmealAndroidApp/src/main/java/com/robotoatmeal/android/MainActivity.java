
package com.robotoatmeal.android;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.rest.RestService;
import com.robotoatmeal.android.rest.RestClient;

@EActivity(R.layout.activity_main)
public class MainActivity
    extends Activity
{
	
	@ViewById
	EditText editText1;

    @RestService
    RestClient restClient;

    @AfterViews
    void afterViews() {
    }

    @UiThread
    void doSomethingElseOnUiThread() {
    }

    @Background
    void doSomethingInBackground() {
        restClient.main();
        doSomethingElseOnUiThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }
    
    @Click(R.id.button1) 
    void button1Clicked(View view) {
    	Intent intent = new Intent(this, SearchResultsActivity_.class);
    	intent.putExtra("query", editText1.getText().toString());
    	startActivity(intent);
    }

}
