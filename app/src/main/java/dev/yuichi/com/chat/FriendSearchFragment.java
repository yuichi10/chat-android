package dev.yuichi.com.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dev.yuichi.com.chat.FirebaseModel.Room;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendSearchFragment extends Fragment implements View.OnClickListener, UtilDBInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context mContext = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;
    private EditText mEmailText;

    private String mFriendID = "";

    final private String UserIDKey = "user_id_key";
    final private String DoesFriendKey = "does_friend_key";

    public FriendSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendSearchFragment newInstance(String param1, String param2) {
        FriendSearchFragment fragment = new FriendSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_search, container, false);
        Button emailSearchButton = (Button)view.findViewById(R.id.email_search_button);
        emailSearchButton.setOnClickListener(this);
        mEmailText = (EditText) view.findViewById(R.id.email_search_edit_text);
        if (view.findViewById(R.id.email_search_edit_text) == null) {
            System.out.println("email_search_edit_text == null");
        }
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if (context instanceof OnFragmentInteractionListener) {
        //    mListener = (OnFragmentInteractionListener) context;
        //} else {
        //    throw new RuntimeException(context.toString()
        //            + " must implement OnFragmentInteractionListener");
        //}
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            final SpannableStringBuilder email = (SpannableStringBuilder)mEmailText.getText();
            if (email == null || email.toString().equals("")){
                return;
            }
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            switch (v.getId()) {
                case R.id.email_search_button:
                    UtilDB.getInstance().getUserIDFromEmail(email.toString(), UserIDKey, this);
                    break;
            }
        }
    }

    @Override
    public void afterGetUserIDFromEmail(String userID, String caseVal) {
        if (caseVal.equals(UserIDKey)) {
            System.out.println("ふれんどID: " + userID);
            if (userID != "") {
                mFriendID = userID;
                UtilDB.getInstance().doesAlreadyHasFriend(userID, DoesFriendKey, this);
            } else {
                System.out.println("そのフレンドはいません");
                Toast.makeText(mContext, "見つかりませんでした", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void afterDoesAlreadyHasFriend(Boolean isFriend, String caseVal) {
        if (caseVal.equals(DoesFriendKey)) {
            if (!isFriend) {
                //まだフレンドにいなかった場合新しくフレンドに登録
                String roomID = UtilDB.getInstance().setRoom();
                UtilDB.getInstance().setFriend(mFriendID, roomID);
                Toast.makeText(mContext, "新しいフレンズを追加: " + mFriendID, Toast.LENGTH_SHORT).show();
            } else {
                System.out.println("フレンドはもう登録されています");
                Toast.makeText(mContext, "すでに友達です", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void afterGetUserName(String userName, String caseVal) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
