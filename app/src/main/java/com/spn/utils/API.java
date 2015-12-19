package com.spn.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import com.spn.data.CompanyData;

import java.util.ArrayList;

/**
 * Created by developer on 12/4/15.
 */
public class API {

    public static String url="";


    public static String USER_ID = "ID";
    public static String PWD="PWD";
    public static String IMG="IMG";

    public static ArrayList<CompanyData> company = new ArrayList<CompanyData>();
    public static ArrayList<CompanyData> company2 = new ArrayList<CompanyData>();

    public static boolean NetworkStatus(Context mContext)
    {
        boolean status = false;
        try
        {
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
            {
                status = true;
            }
            else
            {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
