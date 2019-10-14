package postpc.studypartner2.chat;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import postpc.studypartner2.R;

/**
 * Holds the conversations and the requests tab.
 */
public class InboxHolderFragment extends Fragment {


    public InboxHolderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox_holder, container, false);
    }

}
