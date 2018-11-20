package com.example.mike.week5daily1employees.table_activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mike.week5daily1employees.R;
import com.example.mike.week5daily1employees.model.Employee;

import java.util.List;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {

    List<Employee> employees;

    public EmployeeAdapter( List<Employee> employees ){
        this.employees = employees;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.employee_adapter_item, viewGroup, false );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Employee e = employees.get(i);

        viewHolder.tvId.setText( e.getId( ) );
        viewHolder.tvFirstName.setText( e.getFirstName() );
        viewHolder.tvLastName.setText( e.getLastName() );
        viewHolder.tvSalary.setText( e.getSalary() );
        viewHolder.tvDaysOnJob.setText( e.getDaysEmplyed() );
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvId;
        TextView tvFirstName;
        TextView tvLastName;
        TextView tvSalary;
        TextView tvDaysOnJob;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById( R.id.tvId );
            tvFirstName = itemView.findViewById( R.id.tvFirstName );
            tvLastName = itemView.findViewById( R.id.tvLastName );
            tvSalary = itemView.findViewById( R.id.tvSalary );
            tvDaysOnJob = itemView.findViewById( R.id.tvDaysOnJob );
        }

    }
}
