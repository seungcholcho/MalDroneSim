package com.dji.sdk.maldronesim;
import android.app.Application;
import android.content.Context;
import com.secneo.sdk.Helper;

import java.io.File;

//This class is that activate DJI SDK.
//reference : https://github.com/DJI-Mobile-SDK-Tutorials/Android-ImportAndActivateSDKInAndroidStudio
public class MApplication extends Application{
    @Override
    protected void attachBaseContext(Context paramContext){
        super.attachBaseContext(paramContext);
        Helper.install(MApplication.this);
    }
}
