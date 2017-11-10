package com.test.ekdk636.healthcheck;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HealthBodyInfoActivity extends AppCompatActivity
{
    ListView mListView = null;
    ArrayList<HashMap<String,String>> mArrayList;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_body_info);

        mListView = (ListView) findViewById(R.id.btnBodyInfoList);
        mArrayList = new ArrayList<>();

        BodyInfoData task = new BodyInfoData();
        task.execute("http://"+getString(R.string.server_url)+"/user/list");
    }

    private class BodyInfoData extends AsyncTask<String, Void, String>
    {
        ProgressDialog Dialog = new ProgressDialog(HealthBodyInfoActivity.this);

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
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보가 정상적으로 조회되었습니다.", Toast.LENGTH_SHORT).show();
                    mJsonString = s;
                    showListResult();
                }
                else
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보가 조회 도중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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

                for(int i=0; i<jArray.length(); i++) {
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

                ListAdapter adapter = new SimpleAdapter (
                        HealthBodyInfoActivity.this, mArrayList, R.layout.list_item,
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
            EditText idText = (EditText)findViewById(R.id.id);
            EditText dateText = (EditText)findViewById(R.id.date);
            EditText nameText = (EditText)findViewById(R.id.name);
            EditText ageText = (EditText)findViewById(R.id.age);
            EditText heightText = (EditText)findViewById(R.id.height);
            EditText weightText = (EditText)findViewById(R.id.weight);
            EditText waistText = (EditText)findViewById(R.id.waist);
            EditText hipText = (EditText)findViewById(R.id.hip);

            RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
            RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

            idText.setText(mArrayList.get(position).get("id").toString());
            dateText.setText(mArrayList.get(position).get("date").toString());
            nameText.setText(mArrayList.get(position).get("name").toString());
            ageText.setText(mArrayList.get(position).get("age").toString());
            heightText.setText(mArrayList.get(position).get("height").toString());
            weightText.setText(mArrayList.get(position).get("weight").toString());
            waistText.setText(mArrayList.get(position).get("waist").toString());
            hipText.setText(mArrayList.get(position).get("hip").toString());

            String sex = mArrayList.get(position).get("sex").toString();

            if("남".equals(sex))
            {
                rbMale.setChecked(true);
                rbFemale.setChecked(false);
            }
            else if("여".equals(sex))
            {
                rbMale.setChecked(false);
                rbFemale.setChecked(true);
            }
        }
    };

    public void bodyInfoUpdate(View view)
    {
        EditText date = (EditText) findViewById(R.id.date);
        EditText name = (EditText) findViewById(R.id.name);
        EditText age = (EditText) findViewById(R.id.age);
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        EditText waist = (EditText) findViewById(R.id.waist);
        EditText hip = (EditText) findViewById(R.id.hip);

        RadioGroup rgSex = (RadioGroup) findViewById(R.id.rgSex);
        RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        String sex = "0";

        if(rbMale.isChecked()) sex = "1";
        else if(rbFemale.isChecked()) sex = "2";

        EditText idText = (EditText) findViewById(R.id.id);
        new BodyInfoUpdate().execute("http://" + getString(R.string.server_url) + "/user/update/" + idText.getText(),
                date.getText().toString(), name.getText().toString(), sex, age.getText().toString(),
                height.getText().toString(), weight.getText().toString(), waist.getText().toString(), hip.getText().toString());
    }

    class BodyInfoUpdate extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog = new ProgressDialog(HealthBodyInfoActivity.this);

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder output = new StringBuilder();

            try
            {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("date", params[1]);
                postDataParams.put("name", params[2]);
                postDataParams.put("sex", params[3]);
                postDataParams.put("age", params[4]);
                postDataParams.put("height", params[5]);
                postDataParams.put("weight", params[6]);
                postDataParams.put("waist", params[7]);
                postDataParams.put("hip", params[8]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null)
                {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("PUT");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    OutputStream os = conn.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();

                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;

                    while(true)
                    {
                        line = reader.readLine();

                        if(line == null) break;

                        output.append(line);
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return output.toString();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please Wait...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            dialog.dismiss();

            try
            {
                JSONObject json = new JSONObject(s);

                if(json.getBoolean("result") == true)
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보가 정상적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(HealthBodyInfoActivity.this, HealthBodyInfoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보 수정 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void bodyInfoDelete(View view)
    {
        EditText idText = (EditText) findViewById(R.id.id);
        new BodyInfoDelete().execute("http://" + getString(R.string.server_url) + "/user/delete/" + idText.getText());
    }

    class BodyInfoDelete extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog = new ProgressDialog(HealthBodyInfoActivity.this);

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder output = new StringBuilder();

            try
            {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null)
                {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("DELETE");
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();

                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;

                    while(true)
                    {
                        line = reader.readLine();

                        if(line == null) break;

                        output.append(line);
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return output.toString();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please Wait...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            dialog.dismiss();

            try
            {
                JSONObject json = new JSONObject(s);

                if(json.getBoolean("result") == true)
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보가 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(HealthBodyInfoActivity.this, HealthBodyInfoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보 삭제 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void bodyInfoAdd(View view)
    {
        EditText date = (EditText) findViewById(R.id.date);
        EditText name = (EditText) findViewById(R.id.name);
        EditText age = (EditText) findViewById(R.id.age);
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        EditText waist = (EditText) findViewById(R.id.waist);
        EditText hip = (EditText) findViewById(R.id.hip);

        RadioGroup rgSex = (RadioGroup) findViewById(R.id.rgSex);
        RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        String sex = "0";

        if(rbMale.isChecked()) sex = "1";
        else if(rbFemale.isChecked()) sex = "2";

        new BodyInfoAdd().execute("http://"+getString(R.string.server_url)+"/user/save",
                date.getText().toString(), name.getText().toString(), sex, age.getText().toString(),
                height.getText().toString(), weight.getText().toString(), waist.getText().toString(), hip.getText().toString());
    }

    class BodyInfoAdd extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog = new ProgressDialog(HealthBodyInfoActivity.this);

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder output = new StringBuilder();

            try
            {
                URL url = new URL(params[0]);

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("date", params[1]);
                postDataParams.put("name", params[2]);
                postDataParams.put("sex", params[3]);
                postDataParams.put("age", params[4]);
                postDataParams.put("height", params[5]);
                postDataParams.put("weight", params[6]);
                postDataParams.put("waist", params[7]);
                postDataParams.put("hip", params[8]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null)
                {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
                    writer.write(getPostDataString(postDataParams));
                    writer.flush();
                    writer.close();

                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;

                    while(true)
                    {
                        line = reader.readLine();

                        if(line == null) break;

                        output.append(line);
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            return output.toString();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            dialog.setMessage("Please Wait...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            dialog.dismiss();

            try
            {
                JSONObject json = new JSONObject(s);

                if(json.getBoolean("result") == true)
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보가 정상적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(HealthBodyInfoActivity.this, HealthBodyInfoActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(HealthBodyInfoActivity.this, "신제정보 저장 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext())
        {
            String key = itr.next();
            Object value = params.get(key);

            if(first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(key, "utf-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "utf-8"));
        }

        return result.toString();
    }

    public void backPress(View view)
    {
        super.onBackPressed();
    }
}