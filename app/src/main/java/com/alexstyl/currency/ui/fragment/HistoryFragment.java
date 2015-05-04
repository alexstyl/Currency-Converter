package com.alexstyl.currency.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.BaseFragment;
import com.alexstyl.currency.ui.dialog.SimpleMessageDialog;
import com.alexstyl.currency.util.DeLog;

import java.util.ArrayList;

/**
 * Created by alexstyl on 12/02/15.
 */
public class HistoryFragment extends BaseFragment {

    private static final String KEY_HISTORY = "key_history";
    private static final String TAG = "HistoryFragment";
    private ArrayList<String> mHistory;
    private ArrayAdapter<String> mAdapter;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);


        View headerView = inflater.inflate(R.layout.row_history_header, null, false);

        mListView.addHeaderView(headerView);

        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.row_history, mHistory);
        mListView.setAdapter(mAdapter);


        mListView.setEmptyView(view.findViewById(android.R.id.empty));


        return view;
    }

    /**
     * Adds an entry to be displayed in the History. If the item is added, the list is scrolled to the end of it
     *
     * @param entry The entry to be added
     */
    public void addEntry(String entry) {
        if (TextUtils.isEmpty(entry)) {
            // don't add 'nothing'
            return;
        }
        if (!mHistory.isEmpty() && entry.equals(mHistory.get(mHistory.size() - 1))) {
            DeLog.w(TAG, "Skipped history. Last entry is already " + entry);
            return;
        }
        mHistory.add(entry);
        mAdapter.notifyDataSetChanged();
        // scroll to the last item of the list when a new entry is added
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mHistory = savedInstanceState.getStringArrayList(KEY_HISTORY);
        } else {
            mHistory = new ArrayList<>();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_HISTORY, mHistory);

    }


}
