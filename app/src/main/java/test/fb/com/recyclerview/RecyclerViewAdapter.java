package test.fb.com.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import test.fb.com.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private static final String LOG_TAG = RecyclerViewAdapter.class.getSimpleName();
    private List<Contact> mContacts;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        Button buttonMsg;
        Button buttonDel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cache widgets in the view
            text = (TextView) itemView.findViewById(R.id.text_contact_name);
            buttonMsg = (Button) itemView.findViewById(R.id.button_contact);
            buttonDel = (Button) itemView.findViewById(R.id.button_del);
            buttonDel.setOnClickListener((v) -> {
                int position = getAdapterPosition(); // Get real position (NOT binding position) when del button is clicked
                mContacts.remove(position);
                notifyItemRemoved(position);
                Log.i(LOG_TAG, "Removed contact at position " + position);
            });
        }
    }

    public RecyclerViewAdapter(List<Contact> contacts) {
        this.mContacts = contacts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view from parent's context whenever there is no ViewHolder in the pool.
        View view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.fragment_contact, parent, false); // We don't attach it right away, it's LayoutManager to decide when to attach/detach.
        return new MyViewHolder(view);
    }

    // Called when the ViewHolder is about to display on the screen
    // @param position: the position ONLY at the time of binding.
    //                  However it doesn't reflect the real position after binding as the list is subject to change (del/move/etc)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.text.setText(contact.mName);
        if (position % 2 == 0) {
            holder.buttonMsg.setText("MESSAGE");
            holder.buttonMsg.setEnabled(true);
        } else {
            holder.buttonMsg.setText("OFFLINE");
            holder.buttonMsg.setEnabled(false);
        }
        Log.i(LOG_TAG, "Bind " + contact.mName + " with VH at position " + position);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
