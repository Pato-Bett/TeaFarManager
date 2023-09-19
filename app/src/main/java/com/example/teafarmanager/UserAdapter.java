package com.example.teafarmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends ArrayAdapter<com.example.teafarmanager.User> {

    private LayoutInflater inflater;

    public UserAdapter(Context context, List<com.example.teafarmanager.User> users) {
        super(context, 0, users);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_user, parent, false);

            holder = new ViewHolder();
            holder.usernameTextView = convertView.findViewById(R.id.usernameTextView);
            holder.weightTextView = convertView.findViewById(R.id.WeightTextView);
            holder.OwedAmountTextView = convertView.findViewById(R.id.OwedAmountTextView);
            holder.AmountPaidTextView = convertView.findViewById(R.id.AmountPaidTextView);
            holder.BalanceTextView = convertView.findViewById(R.id.BalanceTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        com.example.teafarmanager.User user = getItem(position);

        holder.usernameTextView.setText(user.getUsername());
        holder.OwedAmountTextView.setText(String.valueOf(user.getTotalOwedAmount()));
        holder.AmountPaidTextView.setText(String.valueOf(user.getTotalAmountPaid()));
        holder.weightTextView.setText(String.valueOf(user.getTotalWeight()));
        holder.BalanceTextView.setText(String.valueOf(user.getBalance()));

        return convertView;
    }

    private static class ViewHolder {
        TextView usernameTextView;
        TextView OwedAmountTextView;
        TextView AmountPaidTextView;
        TextView weightTextView;
        TextView BalanceTextView;
    }
}
