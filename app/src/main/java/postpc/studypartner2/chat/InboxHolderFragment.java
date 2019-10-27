package postpc.studypartner2.chat;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import postpc.studypartner2.R;
import postpc.studypartner2.profile.User;

/**
 * Holds the conversations and the requests tab.
 */
public class InboxHolderFragment extends Fragment {


    MyTabPagerAdapter myTabPagerAdapter;
    ViewPager viewPager;
    private static final String TAG = "InboxHolderFragment";

    public InboxHolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox_holder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myTabPagerAdapter = new MyTabPagerAdapter(getChildFragmentManager());
        viewPager = view.findViewById(R.id.tab_pager);
        viewPager.setAdapter(myTabPagerAdapter);

//         Display a tab for each Fragment displayed in ViewPager.
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // read bundle from notifications
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String isRequest = bundle.getString("isRequest", "");
            if (isRequest.equals("true")){
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
        } else {
            Log.d(TAG, "onCreateView: Got to InboxHolder without user info");
        }
    }

    public class MyTabPagerAdapter extends FragmentStatePagerAdapter{
        public MyTabPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new ConversationsFragment();
                case 1:
                    return new RequestsFragment();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "inbox";
                case 1:
                    return "requests";
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

}
