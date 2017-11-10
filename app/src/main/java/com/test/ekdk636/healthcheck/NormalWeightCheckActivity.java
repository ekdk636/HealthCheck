package com.test.ekdk636.healthcheck;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
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

public class NormalWeightCheckActivity extends AppCompatActivity
{
    ListView mListView = null;
    ArrayList<HashMap<String,String>> mArrayList;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_weight_check);

        EditText etHeight = (EditText) findViewById(R.id.height);
        EditText etWeight = (EditText) findViewById(R.id.weight);

        TextView tvId = (TextView) findViewById(R.id.id);

        RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        Intent intent = getIntent();

        String height = intent.getExtras().getString("height");
        String weight = intent.getExtras().getString("weight");
        String sex = intent.getExtras().getString("sex");
        String id = intent.getExtras().getString("id");

        etHeight.setText(height);
        etWeight.setText(weight);

        tvId.setText(id);

        if("1".equals(sex)) rbMale.setChecked(true);
        else if("2".equals(sex)) rbFemale.setChecked(true);

        mListView = (ListView) findViewById(R.id.btnBodyInfoList);
        mArrayList = new ArrayList<>();

        BodyInfoData task = new BodyInfoData();
        task.execute("http://"+getString(R.string.server_url)+"/user/list");
    }

    private class BodyInfoData extends AsyncTask<String, Void, String>
    {
        ProgressDialog Dialog = new ProgressDialog(NormalWeightCheckActivity.this);

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
                    Toast.makeText(NormalWeightCheckActivity.this, "신제정보가 정상적으로 조회되었습니다.", Toast.LENGTH_SHORT).show();
                    mJsonString = s;
                    showListResult();
                }
                else
                {
                    Toast.makeText(NormalWeightCheckActivity.this, "신제정보가 조회 도중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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

                    mArrayList.add(hashMap);
                }

                ListAdapter adapter = new SimpleAdapter(
                        NormalWeightCheckActivity.this, mArrayList, R.layout.list_item,
                        new String[]{"id", "date", "name", "sex", "age", "height", "weight"},
                        new int[]{R.id.idText, R.id.dateText, R.id.nameText, R.id.sexText, R.id.ageText, R.id.heightText, R.id.weightText}
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
            EditText heightText = (EditText)findViewById(R.id.height);
            EditText weightText = (EditText)findViewById(R.id.weight);

            TextView idText = (TextView) findViewById(R.id.id);

            RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
            RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

            heightText.setText(mArrayList.get(position).get("height").toString());
            weightText.setText(mArrayList.get(position).get("weight").toString());
            idText.setText(mArrayList.get(position).get("id").toString());

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

    public void onConfirmClick(View view)
    {
        EditText height = (EditText) findViewById(R.id.height);
        EditText weight = (EditText) findViewById(R.id.weight);
        TextView id = (TextView) findViewById(R.id.id);

        RadioButton rbMale = (RadioButton) findViewById(R.id.rbMale);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.rbFemale);

        String sex = "";

        if(rbMale.isChecked()) sex = "1";
        else if(rbFemale.isChecked()) sex = "2";

        if(height.getText().toString().trim().length() == 0)
        {
            Toast.makeText(NormalWeightCheckActivity.this, "신장(cm)을 입력하셔야 표준체중 및 비만도 측정이 가능합니다.", Toast.LENGTH_SHORT).show();
            height.requestFocus();
            return;
        }

        if(weight.getText().toString().trim().length() == 0)
        {
            Toast.makeText(NormalWeightCheckActivity.this, "몸무게(kg)을 입력하셔야 표준체중 및 비만도 측정이 가능합니다.", Toast.LENGTH_SHORT).show();
            weight.requestFocus();
            return;
        }

        ConfirmClickEvent normalWeightObesty = new ConfirmClickEvent();
        normalWeightObesty.execute("http://"+getString(R.string.server_url)+"/user/body/obesty/"+id.getText().toString(),
                height.getText().toString(), weight.getText().toString(), sex);
    }

    class ConfirmClickEvent extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog = new ProgressDialog(NormalWeightCheckActivity.this);

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
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.result_modal, null);

                    Builder builder = new Builder(NormalWeightCheckActivity.this);

                    builder.setView(view);

                    final TextView modalContents = (TextView) view.findViewById(R.id.modalContents);
                    final Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
                    final Button btnSave = (Button) view.findViewById(R.id.btnSave);
                    final Button btnNorWeight = (Button) view.findViewById(R.id.btnNorWeight);
                    final Button btnObesty = (Button) view.findViewById(R.id.btnObesty);

                    String contMessage1 = "당신의 표준체중은 <font color=\"red\"><b>"+json.getString("avrWeight")+"kg</b></font> 입니다.<br>"+
                            "비만도는 <font color=\"red\"><b>"+json.getString("obesty")+"%</b></font>이며, 결과는 <font color=\"red\"><b>"+
                            json.getString("obestyChk")+"</b></font>입니다.<br>"+
                            "표준체중과 <font color=\"red\"><b>"+json.getString("minusWeight")+"kg</b></font> 차이가 있습니다.";
                    String contMessage2 = "표준체중: "+json.getString("avrWeight")+"kg";
                    String contMessage3 = "비만도: "+json.getString("obesty")+"%";

                    modalContents.setText(Html.fromHtml(contMessage1));
                    btnNorWeight.setText(Html.fromHtml(contMessage2));
                    btnObesty.setText(Html.fromHtml(contMessage3));

                    final AlertDialog dialog = builder.create();

                    final String avrWeight = json.getString("avrWeight");
                    final String obesty = json.getString("obesty");
                    final String obestyChk = json.getString("obestyChk");
                    final String id = json.getString("id");

                    btnConfirm.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.dismiss();
                        }
                    });

                    btnSave.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onSaveClick(v, avrWeight, obesty, obestyChk, id);
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                else
                {
                    Toast.makeText(NormalWeightCheckActivity.this, "표준체중 및 비만도 측정 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder output = new StringBuilder();

            try
            {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("height", params[1]);
                postDataParams.put("weight", params[2]);
                postDataParams.put("sex", params[3]);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if(conn != null)
                {
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("POST");
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
    }

    public void onSaveClick(View view, String avrWeight, String obesty, String obestyChk, String id)
    {
        SaveClickEvent normalWeightObesty = new SaveClickEvent();
        normalWeightObesty.execute("http://"+getString(R.string.server_url)+"/user/obesty/update/"+id,
                avrWeight, obesty, obestyChk);
    }

    class SaveClickEvent extends AsyncTask<String, String, String>
    {
        ProgressDialog dialog = new ProgressDialog(NormalWeightCheckActivity.this);

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
                    Toast.makeText(NormalWeightCheckActivity.this, "표준체중 및 비만도 결과가 정상적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(NormalWeightCheckActivity.this, "표준체중 및 비만도 결과 저장 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder output = new StringBuilder();

            try
            {
                URL url = new URL(params[0]);
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("avrWeight", params[1]);
                postDataParams.put("obesty", params[2]);
                postDataParams.put("obestyChk", params[3]);

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

