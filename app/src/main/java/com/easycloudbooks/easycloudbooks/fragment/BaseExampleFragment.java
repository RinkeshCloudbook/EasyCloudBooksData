package com.easycloudbooks.easycloudbooks.fragment;

import android.content.Context;
import androidx.fragment.app.Fragment;

//import com.easycloudbooks.searchbar.FloatingSearchView;

/**
 * Created by ari on 8/16/16.
 */
public abstract class BaseExampleFragment extends Fragment {


    private BaseExampleFragmentCallbacks mCallbacks;

    public interface BaseExampleFragmentCallbacks{

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof BaseExampleFragmentCallbacks) {
            mCallbacks = (BaseExampleFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseExampleFragmentCallbacks");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    public abstract boolean onActivityBackPress();
}
