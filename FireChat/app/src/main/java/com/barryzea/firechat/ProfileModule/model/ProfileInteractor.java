package com.barryzea.firechat.ProfileModule.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public interface ProfileInteractor {

    void updateUsername(String username);
    void updateImage(Uri uri, String oldPhotoUrl);
}
