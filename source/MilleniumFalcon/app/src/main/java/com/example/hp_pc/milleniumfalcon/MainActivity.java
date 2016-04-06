package com.example.hp_pc.milleniumfalcon;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends ActionBarActivity {


    TextView textView;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null)
             {
                getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
             }
        GetRubyCommitsTask task = new GetRubyCommitsTask(this);
        task.execute(new String[] {"https://api.github.com/repos/rails/rails/commits"});
    }

    @Override
     public boolean onCreateOptionsMenu(Menu menu)
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

    @Override
     public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == R.id.action_settings)
                {
                return true;
                }
        return super.onOptionsItemSelected(item);
        }



    public class RubyCommitAdapter extends ArrayAdapter<JSONArray>
        {
            Context mContext;
            int mLayoutResourceId;
            JSONArray mData = null;

            public RubyCommitAdapter(Context context, int layoutResourceId, JSONArray data)
                {
                super(context, layoutResourceId);
                this.mLayoutResourceId = layoutResourceId;
                this.mContext = context;
                this.mData = data;
                }


        @Override
        public int getCount()
                {
                return mData.length();
                }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
                {
                LinearLayout row = (LinearLayout) convertView;
                CommitInfo info = null;
                if(row == null)
                    {
                        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                        row = (LinearLayout) inflater.inflate(mLayoutResourceId, parent, false);
                        info = new CommitInfo();
                        info.mAuthor = new TextView(mContext);
                        info.mAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        row.addView(info.mAuthor);
                        info.mCommitLL = new LinearLayout(mContext);
                        info.mCommitLL.setOrientation(LinearLayout.HORIZONTAL);
                        TextView cTitle1 = new TextView(mContext);
                        cTitle1.setText("Commit: ");
                        info.mCommitLL.addView(cTitle1);
                        info.mCommitNum = new TextView(mContext);
                        info.mCommitLL.addView(info.mCommitNum);
                        row.addView(info.mCommitLL);
                        info.mCommentLL = new LinearLayout(mContext);
                        info.mCommentLL.setOrientation(LinearLayout.HORIZONTAL);
                        TextView cTitle2 = new TextView(mContext);
                        cTitle2.setText("Comment: ");
                        info.mCommentLL.addView(cTitle2);
                        info.mComment = new TextView(mContext);
                        info.mCommentLL.addView(info.mComment);
                        row.addView(info.mCommentLL);
                        row.setTag(info);
                    }

                else
                    {
                        info = (CommitInfo)row.getTag();
                    }

                try
                    {

                        JSONObject jObj = mData.getJSONObject(position);
                        JSONObject commit = jObj.getJSONObject("commit");
                        info.mAuthor.setText(commit.getJSONObject("author").getString("name"));
                        info.mCommitNum.setText(jObj.getString("sha"));
                        info.mComment.setText(commit.getString("message"));
                    }
                catch (JSONException e1)
                    {
                        e1.printStackTrace();
                    }
                return row;
                }


        class CommitInfo
                   {
                        TextView mAuthor;
                        LinearLayout mCommitLL;
                        TextView mCommitNum;
                        LinearLayout mCommentLL;
                        TextView mComment;
                    }
        }


    private class GetRubyCommitsTask extends AsyncTask<String, Void, String>
                    {
                        Activity mActivity;
                        GetRubyCommitsTask(Activity activity)
                        {
                            mActivity = activity;
                        }


                        @Override
                        protected String doInBackground(String... urls)
                            {
                                String response = "";
                                for (String url : urls)
                                    {
                                        DefaultHttpClient client = new DefaultHttpClient();
                                        HttpGet httpGet = new HttpGet(url);
                                        try
                                            {
                                                HttpResponse execute = client.execute(httpGet);
                                                InputStream content = execute.getEntity().getContent();
                                                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                                                String s = "";
                                                while ((s = buffer.readLine()) != null)
                                                    {
                                                    response += s;
                                                    }
                                            }
                                        catch (Exception e)
                                            {
                                                e.printStackTrace();
                                            }
                                    }
                return response;
            }

            @Override
            protected void onPostExecute(String result)
                    {
                            JSONArray jArray = null;
                            try
                                {
                                    jArray = new JSONArray(result);
                                }
                            catch (JSONException e1)
                                {
                                    e1.printStackTrace();
                                }
                            RubyCommitAdapter adapter = new RubyCommitAdapter(mActivity,R.layout.listview_item_row, jArray);
                            View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
                            listView.addHeaderView(header);
                            listView.setAdapter(adapter);

                    }
                    }


    public class PlaceholderFragment extends Fragment
    {
        public PlaceholderFragment()
        {
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            textView = (TextView) rootView.findViewById(R.id.text_view);
            listView = (ListView)rootView.findViewById(R.id.list_view);
            return rootView;
        }
    }
}
