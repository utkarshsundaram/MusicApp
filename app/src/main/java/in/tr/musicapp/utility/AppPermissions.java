/*
 * Created by Mohamed Ibrahim N
 * Created on : 21/11/17 3:12 AM
 * File name : AppPermissions.java
 * Last modified by : Mohamed Ibrahim N
 * Last modified on : 18/7/16 6:05 PM
 * Project : MusicApp
 * Organization : FreeLancer trinhvanbien
 * Copyright (c) 2017. All rights reserved.
 */

package in.tr.musicapp.utility;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

public class AppPermissions {
  private Activity mActivity;

  @Deprecated public AppPermissions() {
  }

  public AppPermissions(Activity activity) {
    mActivity = activity;
  }

  public boolean hasPermission(String permission) {
    return ActivityCompat.checkSelfPermission(mActivity, permission)
        == PackageManager.PERMISSION_GRANTED;
  }

  public boolean hasPermission(String[] permissionsList) {
    for (String permission : permissionsList) {
      if (ActivityCompat.checkSelfPermission(mActivity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  public void requestPermission(String permission, int requestCode) {
    if (ActivityCompat.checkSelfPermission(mActivity, permission)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(mActivity, new String[] { permission }, requestCode);
    }
  }

  public void requestPermission(String[] permissionsList, int requestCode) {
    List<String> permissionNeeded = new ArrayList<>();
    for (String permission : permissionsList) {
      if (ActivityCompat.checkSelfPermission(mActivity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        permissionNeeded.add(permission);
      }
    }
    if (permissionNeeded.size() > 0) {
      ActivityCompat.requestPermissions(mActivity,
          permissionNeeded.toArray(new String[permissionNeeded.size()]), requestCode);
    }
  }

  @Deprecated public boolean hasPermission(Activity activity, String permission) {
    return ActivityCompat.checkSelfPermission(activity, permission)
        == PackageManager.PERMISSION_GRANTED;
  }

  @Deprecated public boolean hasPermission(Activity activity, String[] permissionsList) {
    for (String permission : permissionsList) {
      if (ActivityCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  @Deprecated public void requestPermission(Activity activity, String permission, int requestCode) {
    if (ActivityCompat.checkSelfPermission(activity, permission)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
    }
  }

  @Deprecated
  public void requestPermission(Activity activity, String[] permissionsList, int requestCode) {
    List<String> permissionNeeded = new ArrayList<>();
    for (String permission : permissionsList) {
      if (ActivityCompat.checkSelfPermission(activity, permission)
          != PackageManager.PERMISSION_GRANTED) {
        permissionNeeded.add(permission);
      }
    }
    if (permissionNeeded.size() > 0) {
      ActivityCompat.requestPermissions(activity,
          permissionNeeded.toArray(new String[permissionNeeded.size()]), requestCode);
    }
  }
}
