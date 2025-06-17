    package com.example.hanoicraftcorner;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.hanoicraftcorner.main.register.RegisterArtisan;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(AndroidJUnit4.class)
public class RegisterArtisanInstrumentedTest {
    @Test
    public void testRegisterArtisan_UploadThreeImagesAndRegister() {
        ActivityScenario.launch(RegisterArtisan.class);

        // Fill all required fields
        onView(withId(R.id.inpUsername)).perform(typeText("artisan123"), closeSoftKeyboard());
        onView(withId(R.id.inpStoreOrBrand)).perform(typeText("Handmade Store"), closeSoftKeyboard());
        onView(withId(R.id.inpEmail)).perform(typeText("artisan@example.com"), closeSoftKeyboard());
        onView(withId(R.id.inpPhone)).perform(typeText("0123456789"), closeSoftKeyboard());
        onView(withId(R.id.inpPassword)).perform(typeText("securePass123"), closeSoftKeyboard());
        onView(withId(R.id.inpIntroduce)).perform(typeText("I am an experienced artisan."), closeSoftKeyboard());

        // Prepare URI for drawable resource
        String imagePath = "/storage/emulated/0/Pictures/edit.png";
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            // Copy from res/drawable-hdpi/edit.png to /storage/emulated/0/Pictures/edit.png before running this test
            throw new AssertionError("Image file does not exist: " + imagePath);
        }
        Uri imageUri = Uri.fromFile(imageFile);

        // Simulate picking the same image 3 times
        for (int i = 0; i < 3; i++) {
            onView(withId(R.id.addPic)).perform(click());
            Intent resultData = new Intent();
            resultData.setData(imageUri);
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "am broadcast -a android.intent.action.MEDIA_SCANNER_SCAN_FILE -d file://" + imagePath
            );
            // You may need to mock the image picker intent result here depending on your implementation
        }

        // Click register
        onView(withId(R.id.registerButtonArtisan)).perform(click());

        // Assert registration success (update the matcher below to match your success UI)
        onView(withText(containsString("thành công")))
                .check(ViewAssertions.matches(isDisplayed()));
    }
}

