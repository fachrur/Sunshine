package com.rfachrur.sunshine.forecast;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rfachrur.sunshine.R;
import com.rfachrur.sunshine.util.Utility;


class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE = 1;
    private boolean useTodayLayout = true;

    ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && useTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else if (viewType == VIEW_TYPE_FUTURE) {
            layoutId = R.layout.list_item_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.setTag(vh);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // UI simplification with binding
        ViewHolder vh = (ViewHolder)view.getTag();
        TextView dateTv = vh.dateView;
        TextView forecastTv = vh.descriptionView;
        TextView highTempTv = vh.highTempView;
        TextView lowTempTv = vh.lowTempView;
        ImageView iconIv = vh.iconView;

        dateTv.setText(getDay(context, cursor));
        forecastTv.setText(getWeather(context, cursor));
        highTempTv.setText(getTempHigh(context, cursor));
        lowTempTv.setText(getTempLow(context, cursor));

        int viewType = getItemViewType(cursor.getPosition());

        if (viewType == VIEW_TYPE_TODAY) {
            iconIv.setImageResource(Utility.getArtResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        } else if (viewType == VIEW_TYPE_FUTURE) {
            iconIv.setImageResource(Utility.getIconResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }
    }

    private String getTempHigh (Context context, Cursor c){
        return Utility.formatTemperature(context, c.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP), Utility.isMetric(context));
    }

    private String getTempLow (Context context, Cursor c) {
        return Utility.formatTemperature(context, c.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP), Utility.isMetric(context));
    }

    private String getWeather (Context context, Cursor c) {
        return c.getString(ForecastFragment.COL_WEATHER_DESC);
    }

    private String getDay (Context context, Cursor c) {
        return Utility.getFriendlyDayString(context, c.getLong(ForecastFragment.COL_WEATHER_DATE));
    }

    void setUseTodayLayout(boolean isUsed) {
        useTodayLayout = isUsed;
    }

    private static class ViewHolder {
        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

}
