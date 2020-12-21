package com.example.wedetapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MainActivity extends AppCompatActivity {
MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation=findViewById(R.id.bottom_nav);
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_local_hotel_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_bookmark_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_location_on_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_notifications_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_baseline_account_circle_24));
        bottomNavigation.show(3,true);
bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
    @Override
    public Unit invoke(MeowBottomNavigation.Model model) {
        Fragment fragment=null;
        switch (model.getId()){
            case 1:
                fragment=new HotelsFragment();
                break;
            case 2:
                fragment=new Booking_Fragment();
                break;
            case 3:
                fragment=new MapsFragment();
                break;
            case 4:
                fragment=new NotificationFragment();
                break;
            case 5:
                fragment=new ProfileFragment();
                break;
            default:
        }
        FragmentTransaction ft= getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame,fragment);
        ft.commit();

        return null;
    }
});
bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
    @Override
    public Unit invoke(MeowBottomNavigation.Model model) {

        return null;
    }
});

    }

}