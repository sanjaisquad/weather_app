package com.example.weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVadapter extends RecyclerView.Adapter<WeatherRVadapter.ViewHolder> {

    private Context context;
    private ArrayList<weatherRVmodel> WeatherRVmodelArrayList;

    public WeatherRVadapter(Context context, ArrayList<weatherRVmodel> weatherRVmodelArrayList) {
        this.context = context;
        WeatherRVmodelArrayList = weatherRVmodelArrayList;

    }

    @NonNull
    @Override
    public WeatherRVadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVadapter.ViewHolder holder, int position) {
        weatherRVmodel model=WeatherRVmodelArrayList.get(position);
        holder.temperatureTV.setText(model.getTemperature()+"°C");
        Picasso.get().load("http://".concat(model.getIcon())).into(holder.conditionTV);
        holder.windTV.setText(model.getWindspeed()+"Km/h");
        SimpleDateFormat input=new SimpleDateFormat("YYYY-MM-DD hh:mm");
        SimpleDateFormat output= new SimpleDateFormat("hh:mm aa");
        try{
            Date t =input.parse(model.getTime());
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return WeatherRVmodelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV,temperatureTV,timeTV;
        private ImageView conditionTV;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV=itemView.findViewById(R.id.idTVTemperature);
            timeTV=itemView.findViewById(R.id.idTVTime);
            conditionTV=itemView.findViewById(R.id.idIVcondition);

        }

    }
}
