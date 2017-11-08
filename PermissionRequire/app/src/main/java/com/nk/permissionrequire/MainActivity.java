package com.nk.permissionrequire;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView videolist;
    int count;
    String thumbPath;
    Context context;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    DisplayMetrics metrics;
    MainActivity.VideoListAdapter videoListAdapter;
    private Cursor videoCursor = null;
    private int deviceHeight;
    private int deviceWidth;
    TextView no_video_txt;

    public static String convertBytes(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        metrics = new DisplayMetrics();
        getScreenSize();
        videolist = (ListView) findViewById(R.id.PhoneVideoList);
        no_video_txt = (TextView) findViewById(R.id.no_video_txt);
    }

    private void initialization() {
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION};
        videoCursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);

        if (videoCursor != null && videoCursor.getCount() > 0) {
            count = videoCursor.getCount();
            videoCursor.moveToFirst();
            no_video_txt.setVisibility(View.GONE);
        } else {
            count = 0;
        }
        if (count == 0) {
            no_video_txt.setVisibility(View.VISIBLE);
            videolist.setVisibility(View.GONE);
        }
        videoListAdapter = new MainActivity.VideoListAdapter(context);
        videolist.setAdapter(videoListAdapter);
        videoListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialization();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class VideoListAdapter extends BaseAdapter {
        ContentResolver contentResolver;
        private Context vContext;
        private int videoColumnIndex;
        ArrayList<String> arrayList;

        public VideoListAdapter(Context vContext) {
            this.vContext = vContext;
            this.arrayList = new ArrayList<>();
        }

        public int getCount() {
            Log.w("-------------", "All video videoCursor.getCount() : " + count);
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            metrics = new DisplayMetrics();

            convertView = LayoutInflater.from(vContext).inflate(R.layout.videolayout, parent, false);
            final LinearLayout video_ll = (LinearLayout) convertView.findViewById(R.id.video_ll);
            LinearLayout main_name_ll = (LinearLayout) convertView.findViewById(R.id.main_name_ll);
            TextView video_name = (TextView) convertView.findViewById(R.id.video_name);
            TextView video_path = (TextView) convertView.findViewById(R.id.video_path);
            TextView video_size = (TextView) convertView.findViewById(R.id.video_size);
            TextView video_date = (TextView) convertView.findViewById(R.id.video_date);
            TextView video_duration = (TextView) convertView.findViewById(R.id.video_duration);
            ImageView thumbImage = (ImageView) convertView.findViewById(R.id.img_video);
            final String str_video_name, str_video_path;
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(position);
            final String str_name = videoCursor.getString(videoColumnIndex);
            video_name.setText(videoCursor.getString(videoColumnIndex));
            str_video_name = videoCursor.getString(videoColumnIndex);
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            final String str_Data = videoCursor.getString(videoColumnIndex);
            video_path.setText(videoCursor.getString(videoColumnIndex));
            str_video_path = videoCursor.getString(videoColumnIndex);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            videoCursor.moveToPosition(position);
            final long videoz_size = Long.parseLong(videoCursor.getString(videoColumnIndex));
            final String str_size = convertBytes(Long.parseLong(videoCursor.getString(videoColumnIndex)));
            video_size.setText("" + convertBytes(Long.parseLong(videoCursor.getString(videoColumnIndex))) + "");

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
            videoCursor.moveToPosition(position);

            SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
            long ddd = Long.parseLong(videoCursor.getString(videoColumnIndex));
            String result = df.format(ddd);
            final String str_date = result;
            video_date.setText("" + result);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
            videoCursor.moveToPosition(position);
            int duration = Integer.parseInt(videoCursor.getString(videoColumnIndex));
            int timeInmillisec = (duration);
            long duration1 = timeInmillisec / 1000;
            long hours = duration1 / 3600;
            long minutes = (duration1 - hours * 3600) / 60;
            long seconds = duration1 - (hours * 3600 + minutes * 60);
            final String str_duration;
            if (hours == 00) {
                str_duration = "" + twoDigitString((int) minutes) + " : " + twoDigitString((int) seconds);
            } else {
                str_duration = "" + twoDigitString((int) hours) + " : " + twoDigitString((int) minutes) + " : " + twoDigitString((int) seconds);
            }

            video_duration.setText(str_duration);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            videoCursor.moveToPosition(position);

            int videoId = videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Cursor videoThumbnailCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);

            if (videoThumbnailCursor.moveToFirst()) {
                thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            }
            video_path.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_path.getTypeface(), (float) (deviceHeight * 2.2 / 100)));
            video_duration.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_duration.getTypeface(), deviceHeight * 3 / 100));
            video_size.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_size.getTypeface(), (float) (deviceHeight * 2.5 / 100)));
            video_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_date.getTypeface(), (float) (deviceHeight * 2.2 / 100)));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (deviceWidth / 5), deviceHeight / 11);
            thumbImage.setLayoutParams(params);
            RelativeLayout.LayoutParams params_duration = new RelativeLayout.LayoutParams((int) (deviceWidth / 8), deviceHeight / 25);

            // video_duration.setLayoutParams(params_duration);

            LinearLayout.LayoutParams params_main_ll = new LinearLayout.LayoutParams((int) (deviceWidth / 1.3), deviceHeight / 11);
            main_name_ll.setLayoutParams(params_main_ll);

            LinearLayout.LayoutParams params_ll = new LinearLayout.LayoutParams(deviceWidth / 1, deviceHeight / 11);
            video_ll.setLayoutParams(params_ll);
            contentResolver = getContentResolver();

            try {
                Glide.with(vContext)
                        .load(new File(videoCursor.getString(videoColumnIndex)))
                        .placeholder(R.drawable.no_thumbnail)
                        .error(R.drawable.no_thumbnail)
                        .into(thumbImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String get_video_data = str_video_name + ",>" + str_video_path;

            video_ll.setTag(get_video_data);

            video_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemData = (String) v.getTag();
                    Log.w("-------------", "Video Data on CLick to play : " + itemData);
                    startActivity(new Intent(vContext, PlayActivity.class).putExtra("video", itemData));
                }
            });
            return convertView;
        }

        private String twoDigitString(int number) {
            if (number == 0) {
                return "00";
            }
            if (number / 10 == 0) {
                return "0" + number;
            }
            return String.valueOf(number);
        }
    }
    public void getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        deviceHeight = metrics.heightPixels;
        deviceWidth = metrics.widthPixels;
    }
}