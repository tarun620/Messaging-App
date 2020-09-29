package com.example.idene.whatsapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

//COMPLETED

public class Permission {

    public static boolean ValidPermission(String[] permissions, Activity activity, int requestCode){

        //// check if the user has the version above the Marshmallow because from this version the resource was created
        if (Build.VERSION.SDK_INT >= 23){

            List<String> listPermission = new ArrayList<>();

//           / * Cycle through past permissions, check one by one
//            if you already have permissions cleared * /
            for (String permission: permissions){//traverses all permissions passed by parameter traversed permissions to make validations
               Boolean hasPermission = ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED;//after verifying that permissions have been granted and verifying that permissions are the same as android saves
               if (!hasPermission) listPermission.add(permission);

            }
            //if the list is empty, it is not necessary to request permission
            if (listPermission.isEmpty()) return true;

            //convert the list to be passed as a parameter to requestPermissions because it only works with array and I only have a list of listPermission
            String[] newPermissions = new String[listPermission.size()];
            listPermission.toArray(newPermissions);

            //request permission
            ActivityCompat.requestPermissions(activity, newPermissions,requestCode);//code (requestCode) to control where permissions were requested from

        }

        return true;
    }
}
