
package com.robotoatmeal.android;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

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
	static final String SEARCH = "search";
	static final String RESULTS = "results";
	static final String COUPON_DETAIL = "couponDetail";
	
	@ViewById
	EditText searchBar;
/*
    @RestService
    RestClient restClient;
*/

    @UiThread
    void doSomethingElseOnUiThread() {
    }

    @Background
    void doSomethingInBackground() {
        //restClient.main();
        doSomethingElseOnUiThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }
    
    @Click(R.id.searchButton) 
    void searchButtonClicked(View view) {
    	Intent intent = new Intent(this, SearchResultsActivity_.class);
    	String search = searchBar.getText().toString();
    	intent.putExtra(SEARCH, search);
    	/* TEST CODE */
    	InputStream is = getResources().openRawResource(R.raw.test);
    	StringBuffer fileContent = new StringBuffer("");

    	byte[] buffer = new byte[1024];

    	try {
			while (is.read(buffer) != -1) {
				fileContent.append(new String(buffer));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String results = fileContent.toString();
    	/* END TEST CODE*/
    	intent.putExtra(RESULTS, results);
    	startActivity(intent);
    }

}
