package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.regular.BuildConfig;
import org.telegram.messengerutils.XCrashHelper;

public class ApplicationLoaderImpl extends ApplicationLoader {
    @Override
    protected String onGetApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        XCrashHelper.init(base);
    }
}
