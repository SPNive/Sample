package com.spn.wd;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spn.data.CompanyData;
import com.spn.utils.API;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private AutoCompleteTextView search;
    private Spinner filterspn;
    private ImageView iv;
    private ListView list;
    private CompanyListAdapter ac;

    private Button filter;

    private ArrayList<String> name = new ArrayList<String>();
    private ArrayAdapter<String> fil;
    private ArrayAdapter<String> searchfil;

    private Dialog mDialog;

    private String[] filterlist = {"Filter","Accounting","Advertising","Asset Management",
            "Customer Relations","Customer Service","Finances","Human Resources",
            "Legal Department","Media Relations","Payroll | Public Relations",
            "Quality Assurance","Sales and Marketing","Research and Development","Tech Support"};

    private ArrayList<String> deprt= new ArrayList<String>();

    private String url = "https://api.myjson.com/bins/2ggcs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        search = (AutoCompleteTextView) findViewById(R.id.searchEdt);
        filterspn = (Spinner) findViewById(R.id.filterSpn);
        iv = (ImageView) findViewById(R.id.loadImg);
        list = (ListView) findViewById(R.id.list);
        filter = (Button) findViewById(R.id.filterBtn);

        for(int i=0;i<filterlist.length;i++){
            deprt.add(filterlist[i]);
        }

        fil = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deprt);
        fil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterspn.setAdapter(fil);
        search.setThreshold(2);

        View view = View.inflate(this, R.layout.progress_bar, null);

        mDialog = new Dialog(this, R.style.NewDialog);

        mDialog.setContentView(view);
        mDialog.setCancelable(false);
        mDialog.show();

        LoadList();

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                filterspn.setSelection(0);

                if (API.company.size() != 0) {
                    API.company.clear();
                }
                for (int i = 0; i < API.company2.size(); i++) {

                    if (API.company2.get(i).getName().contains(parent.getItemAtPosition(position).toString())) {
                        API.company.add(API.company2.get(i));
                    }
                }

                ac = new CompanyListAdapter(MainActivity.this, API.company);
                list.setAdapter(ac);
                //list.notifyAll();
            }
        });

        filterspn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                search.setText("");

                if (position == 0) {

                } else {

                    if (API.company.size() != 0) {
                        API.company.clear();
                    }

                    for (int i = 0; i < API.company2.size(); i++) {

                        if(API.company2.get(i).getDept().equals("")) {

                        }
                        else{
                            if (API.company2.get(i).getDept().contains(parent.getItemAtPosition(position).toString())) {
                                API.company.add(API.company2.get(i));
                            }
                        }
                    }
                    ac = new CompanyListAdapter(MainActivity.this, API.company);
                    list.setAdapter(ac);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                search.setText("");
                filterspn.setSelection(0);

                LoadList();
                mDialog.show();
            }
        });
    }

    public void LoadList(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(API.company.size()!=0){
                    API.company.clear();
                }

                try{
                    JSONArray ja =new JSONArray(response);

                    for(int i=0;i<ja.length();i++){

                        CompanyData data = new CompanyData();

                        data.setId(ja.getJSONObject(i).getString("companyID"));
                        data.setName(ja.getJSONObject(i).getString("comapnyName"));
                        data.setDept(ja.getJSONObject(i).getString("companyDepartments"));
                        data.setOwner(ja.getJSONObject(i).getString("companyOwner"));
                        data.setDesc(ja.getJSONObject(i).getString("companyDescription"));
                        data.setDate(ja.getJSONObject(i).getString("companyStartDate"));

                        name.add(ja.getJSONObject(i).getString("comapnyName"));
                        API.company.add(data);
                        API.company2.add(data);
                    }

                    if(mDialog.isShowing())
                        mDialog.dismiss();

                    ac = new CompanyListAdapter(MainActivity.this, API.company);
                    searchfil = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, name);
                    searchfil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    list.setAdapter(ac);
                    search.setAdapter(searchfil);

                    Log.d("Company name : " , name.toString());

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error : ", error.toString());
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public class CompanyListAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<CompanyData> mList;
        String name;

        public CompanyListAdapter(Context context,
                               ArrayList<CompanyData> list) {
            this.mContext = context;
            this.mList = list;
            this.name=name;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) mContext
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                convertView = mInflater.inflate(R.layout.itemrow, null);
                holder = new ViewHolder();
                holder.id = (TextView) convertView.findViewById(R.id.idTxt);
                holder.name = (TextView) convertView.findViewById(R.id.nameTxt);
                holder.owner = (TextView) convertView.findViewById(R.id.ownerTxt);
                holder.depart = (TextView) convertView.findViewById(R.id.departTxt);
                holder.date = (TextView) convertView.findViewById(R.id.dateTxt);
                holder.desc = (TextView) convertView.findViewById(R.id.descTxt);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {

                Log.d("String : ", mList.get(position).getName() + " ");

                String name="<font color='#FAAC58'>Name : </font>" + mList.get(position).getName();
                String owner="<font color='#FAAC58'>Owner : </font>" + mList.get(position).getOwner();
                String depart="<font color='#FAAC58'>Department : </font>" + mList.get(position).getDept();
                String date="<font color='#FAAC58'>StartDate : </font>" + mList.get(position).getDate();
                String desc="<font color='#FAAC58'>Description : </font>" + mList.get(position).getDesc();

                holder.name.setText(Html.fromHtml(name));
                holder.owner.setText(Html.fromHtml(owner));
                holder.depart.setText(Html.fromHtml(depart));
                holder.date.setText(Html.fromHtml(date));
                holder.desc.setText(Html.fromHtml(desc));

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            return convertView;
        }

        private class ViewHolder {
            TextView id,name,owner,depart,date,desc;
        }
    }
}
