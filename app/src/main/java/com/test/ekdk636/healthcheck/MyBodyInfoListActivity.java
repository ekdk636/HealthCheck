package com.test.ekdk636.healthcheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MyBodyInfoListActivity extends AppCompatActivity
{
    ListView mListView = null;
    ArrayList<HashMap<String,String>> mArrayList;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_body_info_list);

        Button b = (Button)findViewById(R.id.btnNorWghtChk);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), NormalWeightCheckActivity.class);

                String sex = "";

                TextView height = (TextView)findViewById(R.id.heightText);
                TextView weight = (TextView)findViewById(R.id.weightText);
                TextView tmpSex = (TextView)findViewById(R.id.sexText);

                if("남".equals(tmpSex.getText())) sex = "1";
                else if("여".equals(tmpSex.getText())) sex = "2";

                intent.putExtra("height", height.getText());
                intent.putExtra("weight", weight.getText());
                intent.putExtra("sex", sex);

                startActivity(intent);
            }
        });

        Button b2 = (Button)findViewById(R.id.btnBMIChk);
        b2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyBmiCheckActivity.class);

                TextView height = (TextView)findViewById(R.id.heightText);
                TextView weight = (TextView)findViewById(R.id.weightText);

                intent.putExtra("height", height.getText());
                intent.putExtra("weight", weight.getText());

                startActivity(intent);
            }
        });

        Button b3 = (Button)findViewById(R.id.btnWHRChk);
        b3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyWhrCheckActivity.class);

                String sex = "";

                TextView waist = (TextView)findViewById(R.id.waistText);
                TextView hip = (TextView)findViewById(R.id.hipText);
                TextView tmpSex = (TextView)findViewById(R.id.sexText);

                if("남".equals(tmpSex.getText())) sex = "1";
                else if("여".equals(tmpSex.getText())) sex = "2";

                intent.putExtra("waist", waist.getText());
                intent.putExtra("hip", hip.getText());
                intent.putExtra("sex", sex);

                startActivity(intent);
            }
        });

        Button b4 = (Button)findViewById(R.id.btnFatPcntChk);
        b4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), BodyFatCheckActivity.class);
                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.btnBodyInfoList);
        mArrayList = new ArrayList<>();

        MyBodyInfoListActivity.BodyInfoData task = new MyBodyInfoListActivity.BodyInfoData();
        task.execute("http://"+getString(R.string.server_url)+"/user/list");
    }

    private class BodyInfoData extends AsyncTask<String, Void, String>
    {
        ProgressDialog Dialog = new ProgressDialog(MyBodyInfoListActivity.this);

        @Override
        protected String doInBackground(String... params)
        {
            String paramUrl = params[0];

            try
            {
                URL url = new URL(paramUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int resStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;

                if(resStatusCode == httpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufReader = new BufferedReader(inputStreamReader);

                StringBuilder output = new StringBuilder();
                String line;

                while((line = bufReader.readLine()) != null)
                {
                    output.append(line);
                }

                bufReader.close();
                return output.toString();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            Dialog.setMessage("Please Wait");
            Dialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Dialog.dismiss();

            try
            {
                if(s != null)
                {
                    Toast.makeText(MyBodyInfoListActivity.this, "신제정보가 정상적으로 조회되었습니다.", Toast.LENGTH_SHORT).show();
                    mJsonString = s;
                    showListResult();
                }
                else
                {
                    Toast.makeText(MyBodyInfoListActivity.this, "신제정보가 조회 도중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        private void showListResult()
        {
            try
            {
                JSONObject json = new JSONObject(mJsonString);
                JSONArray jArray = json.getJSONArray("result");
                Log.d("ssss ====>", mJsonString);
                for(int i=0; i<jArray.length(); i++) {
                    JSONObject item = jArray.getJSONObject(i);
                    Log.d("name ==> ",item.getString("name"));
                    String id = item.getString("id");
                    String date = item.getString("date");
                    String name = item.getString("name");
                    String tmpSex = item.getString("sex");
                    String age = item.getString("age");
                    String height = item.getString("height");
                    String weight = item.getString("weight");
                    String waist = item.getString("waist");
                    String hip = item.getString("hip");

                    String sex = "";

                    if("1".equals(tmpSex)) sex = "남";
                    else if("2".equals(tmpSex)) sex = "여";

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put("id", id);
                    hashMap.put("date", date);
                    hashMap.put("name", name);
                    hashMap.put("sex", sex);
                    hashMap.put("age", age);
                    hashMap.put("height", height);
                    hashMap.put("weight", weight);
                    hashMap.put("waist", waist);
                    hashMap.put("hip", hip);

                    mArrayList.add(hashMap);
                }

                ListAdapter adapter = new SimpleAdapter(
                        MyBodyInfoListActivity.this, mArrayList, R.layout.list_item,
                        new String[]{"id", "date", "name", "sex", "age", "height", "weight", "waist", "hip"},
                        new int[]{R.id.idText, R.id.dateText, R.id.nameText, R.id.sexText, R.id.ageText, R.id.heightText, R.id.weightText, R.id.waistText, R.id.hipText}
                );

                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(itemClickListenerBodyInfo);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private AdapterView.OnItemClickListener itemClickListenerBodyInfo = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            TextView dateText = (TextView)findViewById(R.id.txtDate);
            TextView nameText = (TextView)findViewById(R.id.txtName);
            TextView sexText = (TextView) findViewById(R.id.txtSex);
            TextView ageText = (TextView)findViewById(R.id.txtAge);
            TextView heightText = (TextView)findViewById(R.id.txtHeight);
            TextView weightText = (TextView)findViewById(R.id.txtWeight);
            TextView waistText = (TextView)findViewById(R.id.txtWaist);
            TextView hipText = (TextView)findViewById(R.id.txtHip);

            String fDate = mArrayList.get(position).get("date").toString();

            dateText.setText(fDate.substring(0,4)+"-"+fDate.substring(4,6)+"-"+fDate.substring(6,8));
            nameText.setText(mArrayList.get(position).get("name").toString());
            ageText.setText(mArrayList.get(position).get("age").toString()+" 세");
            heightText.setText(mArrayList.get(position).get("height").toString()+" cm");
            weightText.setText(mArrayList.get(position).get("weight").toString()+" kg");
            waistText.setText(mArrayList.get(position).get("waist").toString()+" inch");
            hipText.setText(mArrayList.get(position).get("hip").toString()+" inch");

            sexText.setText(mArrayList.get(position).get("sex").toString()+"자");
        }
    };
}
