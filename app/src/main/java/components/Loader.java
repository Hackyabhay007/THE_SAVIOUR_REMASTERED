package components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.hackydesk.thesaviour.R;


public class Loader {

    Activity activity;
    AlertDialog dialog;

    public Loader(Activity Myactivity) {
    activity = Myactivity;
    }
    @SuppressLint("InflateParams")
    public void startLoader()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater =activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.customdialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

   public void dismissLoader()
    {
        dialog.dismiss();
    }

    public  void startLoader(String Title,String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater =activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.customdialog,null));
        builder.setCancelable(false);
        builder.setTitle(Title).setMessage(Message);
        dialog = builder.create();
        dialog.show();
    }

    public  void dismissloader(String Title,String Message)
    {

        dialog.dismiss();
    }


}
