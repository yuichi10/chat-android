package dev.yuichi.com.chat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dev.yuichi.com.chat.FirebaseModel.Room;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context mContext = null;
    private DatabaseReference mDatabase;
    private Firebase firebase;
    RoomListAdapter mAdapter;

    public RoomListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoomListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomListFragment newInstance(String param1, String param2) {
        RoomListFragment fragment = new RoomListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdapter = new RoomListAdapter(mContext);
        firebase = new Firebase(D.FirebaseURL);
        //ルームの情報をadapterに追加
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase.child(D.Users).child(user.getUid()).child(D.Rooms).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println(snapshot.getValue());
                    HashMap<String, String> value = (HashMap) snapshot.getValue();
                    System.out.println("value: " + value);
                    if (value != null) {
                        for (final String key : value.keySet()) {
                            mDatabase.child(D.Rooms).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    Room room = snapshot.getValue(Room.class);
                                    System.out.println(room.getName());
                                    RoomListInfo roomListInfo = new RoomListInfo(key, room.getGroup(), room.getName());
                                    mAdapter.addRoomListItem(roomListInfo);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                }
            });
            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
            System.out.println(u.getUid());
            DatabaseReference friendRoom = mDatabase.child(D.Users).child(UtilDB.getInstance().getOwnUserID()).child(D.Friends);
            friendRoom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, String> data = (HashMap) dataSnapshot.getValue();
                    if (data != null) {
                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            final String roomID = entry.getKey();
                            mDatabase.child(D.Users).child(entry.getKey()).child(D.Name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        RoomListInfo roomListInfo = new RoomListInfo(roomID, false, dataSnapshot.getValue().toString());
                                        mAdapter.addRoomListItem(roomListInfo);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        View roomList = inflater.inflate(R.layout.fragment_room_list, container, false);
        //アダプターをセット
        setListAdapter(mAdapter);

        // Inflate the layout for this fragment
        return roomList;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //if (!(activity instanceof RecyclerFragmentListener)) {
        //    throw new UnsupportedOperationException(
        //            "Listener is not Implementation.");
        //} else {
        //    mFragmentListener = (RecyclerFragmentListener) activity;
        //}
        mContext = context;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RoomListInfo item = (RoomListInfo) getListView().getItemAtPosition(position);
        Toast.makeText(mContext,
                item.getRoomID(), Toast.LENGTH_LONG
        ).show();
        Intent intent = new Intent();
        intent.putExtra(D.RoomID, item.getRoomID());
        intent.setClassName(D.packageRoot, D.packageRoot + ".ChatActivity");
        startActivity(intent);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("タブ切り替え");
        for (int i=0; i < mAdapter.getCount(); i++) {
            System.out.println(mAdapter.getRoomID(i));
        }
        /*if(_listview.getFooterViewsCount() == 0)
        {
            _listview.addFooterView(_footer);
        }*/
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
