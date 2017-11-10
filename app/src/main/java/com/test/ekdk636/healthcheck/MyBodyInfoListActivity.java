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
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

                TextView height = (TextView)findViewById(R.id.height);
                TextView weight = (TextView)findViewById(R.id.weight);
                TextView tmpSex = (TextView)findViewById(R.id.sex);
                TextView id = (TextView) findViewById(R.id.id);

                if("남".equals(tmpSex.getText())) sex = "1";
                else if("여".equals(tmpSex.getText())) sex = "2";

                intent.putExtra("height", height.getText());
                intent.putExtra("weight", weight.getText());
                intent.putExtra("id", id.getText());
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

                TextView height = (TextView)findViewById(R.id.height);
                TextView weight = (TextView)findViewById(R.id.weight);
                TextView id = (TextView) findViewById(R.id.id);

                intent.putExtra("height", height.getText());
                intent.putExtra("weight", weight.getText());
                intent.putExtra("id", id.getText());

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

                TextView waist = (TextView)findViewById(R.id.waist);
                TextView hip = (TextView)findViewById(R.id.hip);
                TextView tmpSex = (TextView)findViewById(R.id.sex);
                TextView id = (TextView) findViewById(R.id.id);

                if("남".equals(tmpSex.getText())) sex = "1";
                else if("여".equals(tmpSex.getText())) sex = "2";

                intent.putExtra("waist", waist.getText());
                intent.putExtra("hip", hip.getText());
                intent.putExtra("id", id.getText());
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

                String sex = "";

                TextView age = (TextView) findViewById(R.id.age);
                TextView height = (TextView)findViewById(R.id.height);
                TextView weight = (TextView)findViewById(R.id.weight);
                TextView tmpSex = (TextView)findViewById(R.id.sex);
                TextView id = (TextView) findViewById(R.id.id);

                if("남".equals(tmpSex.getText())) sex = "1";
                else if("여".equals(tmpSex.getText())) sex = "2";

                intent.putExtra("age", age.getText());
                intent.putExtra("height", height.getText());
                intent.putExtra("weight", weight.getText());
                intent.putExtra("id", id.getText());
                intent.putExtra("sex", sex);

                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.btnBodyInfoList);
        mArrayList = new ArrayList<>();

        MyBodyInfoListActivity.BodyInfoData task = new MyBodyInfoListActivity.BodyInfoData();
        task.execute("http://"+getString(R.string.server_url)+"/user/list");
    }

    public void onReinquiryClick(View view)
    {
        mArrayList.clear();

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

                Log.d("mJsonString ======> ", mJsonString);
                for(int i=0; i<jArray.length(); i++)
                {
                    JSONObject item = jArray.getJSONObject(i);

                    String id = item.getString("id");
                    String date = item.getString("date");
                    String name = item.getString("name");
                    String tmpSex = item.getString("sex");
                    String age = item.getString("age");
                    String height = item.getString("height");
                    String weight = item.getString("weight");
                    String waist = item.getString("waist");
                    String hip = item.getString("hip");
                    String norWeight = item.getString("avrWeight");
                    String obesty = item.getString("obstObestyChk");
                    String bmi = item.getString("bmi");
                    String whr = item.getString("whr");
                    String fatMass = item.getString("fatMass");
                    String fatMassRate = item.getString("fatMassRate");
                    String fatLossRate = item.getString("fatLossRate");
                    String bascMtsm = item.getString("basicMetablsm");

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
                    hashMap.put("norweight", norWeight);
                    hashMap.put("obesty", obesty);
                    hashMap.put("bmi", bmi);
                    hashMap.put("whr", whr);
                    hashMap.put("fatmass", fatMass);
                    hashMap.put("fatmassrate", fatMassRate);
                    hashMap.put("fatlossrate", fatLossRate);
                    hashMap.put("bascmtsm", bascMtsm);

                    mArrayList.add(hashMap);
                }

                ListAdapter adapter = new SimpleAdapter(
                        MyBodyInfoListActivity.this, mArrayList, R.layout.list_item,
                        new String[]{"id", "date", "name", "sex", "age", "height", "weight", "waist", "hip",
                            "norweight", "obesty", "bmi", "whr", "fatmass", "fatmassrate", "fatlossrate", "bascmtsm"},
                        new int[]{R.id.idText, R.id.dateText, R.id.nameText, R.id.sexText, R.id.ageText, R.id.heightText, R.id.weightText,
                                R.id.waistText, R.id.hipText, R.id.norWeightText, R.id.obestyText, R.id.bmiText, R.id.whrText, R.id.fatMassText,
                                R.id.fatMassRateText, R.id.fatLossRateText, R.id.bascMtsmText}
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

            TextView sex = (TextView) findViewById(R.id.sex);
            TextView age = (TextView)findViewById(R.id.age);
            TextView height = (TextView)findViewById(R.id.height);
            TextView weight = (TextView)findViewById(R.id.weight);
            TextView waist = (TextView)findViewById(R.id.waist);
            TextView hip = (TextView)findViewById(R.id.hip);
            TextView idText = (TextView) findViewById(R.id.id);

            TextView txtNorWeight = (TextView)findViewById(R.id.txtNorWeight);
            TextView txtObesty = (TextView)findViewById(R.id.txtObesty);
            TextView txtBMI = (TextView) findViewById(R.id.txtBMI);
            TextView txtWHR = (TextView)findViewById(R.id.txtWHR);
            TextView txtFatMass = (TextView)findViewById(R.id.txtFatMass);
            TextView txtFatMassRate = (TextView)findViewById(R.id.txtFatMassRate);
            TextView txtFatLossRate = (TextView)findViewById(R.id.txtFatLossRate);
            TextView txtBascMtsm = (TextView)findViewById(R.id.txtBascMtsm);

            String fDate = mArrayList.get(position).get("date").toString();

            dateText.setText(fDate.substring(0,4)+"-"+fDate.substring(4,6)+"-"+fDate.substring(6,8));
            nameText.setText(mArrayList.get(position).get("name").toString());
            ageText.setText(mArrayList.get(position).get("age").toString()+" 세");
            heightText.setText(mArrayList.get(position).get("height").toString()+" cm");
            weightText.setText(mArrayList.get(position).get("weight").toString()+" kg");
            waistText.setText(mArrayList.get(position).get("waist").toString()+" inch");
            hipText.setText(mArrayList.get(position).get("hip").toString()+" inch");
            sexText.setText(mArrayList.get(position).get("sex").toString()+"자");

            age.setText(mArrayList.get(position).get("age").toString());
            height.setText(mArrayList.get(position).get("height").toString());
            weight.setText(mArrayList.get(position).get("weight").toString());
            waist.setText(mArrayList.get(position).get("waist").toString());
            hip.setText(mArrayList.get(position).get("hip").toString());
            sex.setText(mArrayList.get(position).get("sex").toString());
            idText.setText(mArrayList.get(position).get("id").toString());

            if("null".equals(mArrayList.get(position).get("norweight").toString())) txtNorWeight.setText("");
            else txtNorWeight.setText(mArrayList.get(position).get("norweight").toString() + " kg");

            if("null".equals(mArrayList.get(position).get("obesty").toString())) txtObesty.setText("");
            else txtObesty.setText(mArrayList.get(position).get("obesty").toString());

            if("null".equals(mArrayList.get(position).get("bmi").toString())) txtBMI.setText("");
            else txtBMI.setText(mArrayList.get(position).get("bmi").toString());

            if("null".equals(mArrayList.get(position).get("whr").toString())) txtWHR.setText("");
            else txtWHR.setText(mArrayList.get(position).get("whr").toString());

            if("null".equals(mArrayList.get(position).get("fatmass").toString())) txtFatMass.setText("");
            else txtFatMass.setText(mArrayList.get(position).get("fatmass").toString()+" kg");

            if("null".equals(mArrayList.get(position).get("fatmassrate").toString())) txtFatMassRate.setText("");
            else txtFatMassRate.setText(mArrayList.get(position).get("fatmassrate").toString()+" %");

            if("null".equals(mArrayList.get(position).get("fatlossrate").toString())) txtFatLossRate.setText("");
            else txtFatLossRate.setText(mArrayList.get(position).get("fatlossrate").toString()+" %");

            if("null".equals(mArrayList.get(position).get("bascmtsm").toString())) txtBascMtsm.setText("");
            else txtBascMtsm.setText(mArrayList.get(position).get("bascmtsm").toString()+" kcal");
        }
    };

    public void backPress(View view)
    {
        super.onBackPressed();
    }
}
