package com2027.killaz.kalorie.gitfit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser;
    private DocumentReference userRef;
    private RecyclerView friendReqsRecycler;
    private List<String> friendReqsList;
    private FriendRequestsListAdapter friendReqsAdapter;
    private FriendRequestsListAdapter friendsAdapter;
    private RecyclerView friendsRecycler;
    private List<String> friendsList;
    private List<String> userList;
    private Button friendReqBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.friends_fragment, container, false);
    }

    /**
     * Initialise both recycler views for friends and friend requests, get the user current user and setup the front-end appropriately by fetching all list items from the database.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        friendReqsRecycler = getView().findViewById(R.id.friends_req_recycler_view);
        friendReqsRecycler.setHasFixedSize(true);
        friendReqsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecycler = getView().findViewById(R.id.friends_recycler_view);
        friendsRecycler.setHasFixedSize(true);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecycler.setVerticalScrollBarEnabled(true);
        friendReqsRecycler.setVerticalScrollBarEnabled(true);
        friendReqsList = new ArrayList<>();
        friendsList = new ArrayList<>();
        userList = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = db.collection("Users").document(currentUser.getDisplayName());
        friendReqBtn = getView().findViewById(R.id.send_friend_req);
        fetchFriendReqs();
        fetchFriends();
        fetchUsers();
        setupRecyclerActions();
        setupFriendReqs();
    }

    /**
     * Fetches the user's friend requests from the database and display their names in the appropriate recycler view
     */
    private void fetchFriendReqs() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    friendReqsList = (List<String>) documentSnapshot.get("friend_requests");
                    friendReqsAdapter = new FriendRequestsListAdapter(friendReqsList, getActivity());
                    friendReqsRecycler.setAdapter(friendReqsAdapter);
                    friendReqsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Fetches the user's friend names from the database and display their names in the appropriate recycler view.
     */
    private void fetchFriends() {
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    friendsList = (List<String>) documentSnapshot.get("friends");
                    friendsAdapter = new FriendRequestsListAdapter(friendsList, getActivity());
                    friendsRecycler.setAdapter(friendsAdapter);
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Setup recycler view actions for both friend requests and friends. Friend requests allow the acceptance/decline of requests and
     * Friends allow the deletion and viewing of information for that friend.
     * Both operate using swipe left/right implementations
     */
    private void setupRecyclerActions() {

        ItemTouchHelper.SimpleCallback callBackReqs = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.RIGHT) {
                    db.collection("Users").document(friendReqsList.get(position)).update("friends", FieldValue.arrayUnion(currentUser.getDisplayName()));
                    userRef.update("friends", FieldValue.arrayUnion(friendReqsList.get(position)));
                    userRef.update("friend_requests", FieldValue.arrayRemove(friendReqsList.get(position)));
                    friendReqsList.remove(position);
                    friendReqsAdapter.notifyItemRemoved(position);
                    friendReqsAdapter.notifyItemRangeChanged(position, friendReqsAdapter.getItemCount());
                    Toast.makeText(getContext(), "Friend Request Accepted", Toast.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.LEFT) {
                    userRef.update("friends", FieldValue.arrayUnion(friendReqsList.get(position)));
                    userRef.update("friend_requests", FieldValue.arrayRemove(friendReqsList.get(position)));
                    friendReqsList.remove(position);
                    friendReqsAdapter.notifyItemRemoved(position);
                    friendReqsAdapter.notifyItemRangeChanged(position, friendReqsAdapter.getItemCount());
                    Toast.makeText(getContext(), "Friend Request Declined", Toast.LENGTH_SHORT).show();
                }
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBackReqs);
        itemTouchHelper.attachToRecyclerView(friendReqsRecycler);

        ItemTouchHelper.SimpleCallback callBackFriends = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setTitle("Delete Friend");
                    builder1.setMessage("Are you sure you wish to delete " + friendsList.get(position) + " from your friends list?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(getContext(), "Removed " + friendsList.get(position) + " from friends", Toast.LENGTH_SHORT).show();
                                    userRef.update("friends", FieldValue.arrayRemove(friendsList.get(position)));
                                    db.collection("Users").document(friendsList.get(position)).update("friends", FieldValue.arrayRemove(currentUser.getDisplayName()));
                                    friendsList.remove(position);
                                    friendsAdapter.notifyItemRemoved(position);
                                    friendsAdapter.notifyItemRangeChanged(position, friendsAdapter.getItemCount());
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    friendsAdapter.notifyDataSetChanged();
                                    dialog.cancel();
                                }
                            });

                    AlertDialog friendAlert = builder1.create();
                    friendAlert.show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    showUserInfoPopup(friendsList.get(position));
                    friendsAdapter.notifyDataSetChanged();
                }
            }
        };


        ItemTouchHelper itemTouchHelperFriends = new ItemTouchHelper(callBackFriends);
        itemTouchHelperFriends.attachToRecyclerView(friendsRecycler);

    }

    /**
     * Displays friend information on a pop up dialog. Including name, challenges completed, points and steps taken.
     *
     * @param theFriend to show information about.
     */
    private void showUserInfoPopup(final String theFriend) {
        DocumentReference friendRef = db.collection("Users").document(theFriend);
        final Dialog infoDialog;
        TextView txtClose;
        final TextView friendName;
        final TextView challenges;
        final TextView points;
        final TextView steps;
        infoDialog = new Dialog(getActivity());
        infoDialog.setContentView(R.layout.friend_info_popup);
        txtClose = infoDialog.findViewById(R.id.popup_close);
        friendName = infoDialog.findViewById(R.id.popup_name);
        challenges = infoDialog.findViewById(R.id.popup_challenges);
        points = infoDialog.findViewById(R.id.popup_points);
        steps = infoDialog.findViewById(R.id.popup_steps);

        friendRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    friendName.setText(theFriend);
                    challenges.setText(String.valueOf(documentSnapshot.get("challenges_completed")));
                    points.setText(String.valueOf(documentSnapshot.get("points")));
                    steps.setText(String.valueOf(documentSnapshot.get("total_distance_covered")));
                }
            }
        });

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
            }
        });

        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoDialog.show();
    }

    /**
     * Initialise button listener for friend requests. Allowing user to send requests to other users.
     */
    private void setupFriendReqs() {
        friendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Send Friend Request");
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Enter name of user");
                builder.setView(input);
                builder.setPositiveButton("Send Request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addFriend(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    /**
     * Fetches all current users and adds them to a list. Friend requests check find if user exists from said list.
     */
    private void fetchUsers() {
        db.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    userList.add(document.getId());
                }
            }
        });
    }

    /**
     * Check if user trying to add as friend exists. If so, send request, if not, let user know.
     *
     * @param friendToAdd self-explanatory
     */
    private void addFriend(String friendToAdd) {
        if (friendToAdd.equals(currentUser.getDisplayName())) {
            Toast.makeText(getContext(), "Are you that lonely?", Toast.LENGTH_SHORT).show();
        } else if (userList.contains(friendToAdd)) {
            db.collection("Users").document(friendToAdd).update("friend_requests", FieldValue.arrayUnion(currentUser.getDisplayName()));
            Toast.makeText(getContext(), "Friend request sent to " + friendToAdd, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Can't add friend. This user does not exist", Toast.LENGTH_SHORT).show();
        }
    }

}
