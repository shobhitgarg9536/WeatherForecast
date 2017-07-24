package com.shobhit.weatherforecast;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ForecastWeeklyAdapter extends RecyclerView.Adapter<ForecastWeeklyAdapter.MyViewHolder> {

    private List<ForcastObject> forcastObjectList;
    private Context context;

    public ForecastWeeklyAdapter(List<ForcastObject> forcastObjects, Context context) {
        this.forcastObjectList = forcastObjects;
        this.context = context;
    }

    @Override
    public ForecastWeeklyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_weekly_view, parent, false);

        return new MyViewHolder(itemView);
    }

    //declare the values
    @Override
    public void onBindViewHolder(ForecastWeeklyAdapter.MyViewHolder holder, int position) {
        ForcastObject forcastObject = forcastObjectList.get(position);
        holder.tvWindSpeed.setText(context.getString(R.string.s_wind)+forcastObject.getWindSpeed());
        holder.tvTemp.setText(forcastObject.getTemperatue()+"°C");
        holder.tvCondition.setText(forcastObject.getWeatherDescription());
        holder.tvHumidity.setText(context.getString(R.string.s_humidity)+forcastObject.getHumidity());
        holder.tvDate.setText(forcastObject.getDate());
        holder.tvPressure.setText(context.getString(R.string.s_pressure)+forcastObject.getPressure());
        Picasso.with(context)
                .load(forcastObject.getIcon())
                .into(holder.ivWeatherIcon);

    }

    //get item count
    @Override
    public int getItemCount() {
        return forcastObjectList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTemp, tvWindSpeed, tvHumidity, tvPressure, tvCondition, tvDate;
        ImageView ivWeatherIcon;

        MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.textViewForecastDate);
            tvCondition = (TextView) view.findViewById(R.id.textViewForecastWeatherCondition);
            tvHumidity = (TextView) view.findViewById(R.id.textViewForecastHumidity);
            tvPressure = (TextView) view.findViewById(R.id.textViewForecastPressure);
            tvTemp = (TextView) view.findViewById(R.id.textViewForecastTemp);
            tvWindSpeed = (TextView) view.findViewById(R.id.textViewForecastWindSpeed);
            ivWeatherIcon = (ImageView) view.findViewById(R.id.imageViewForecastIcon);
        }
    }
}