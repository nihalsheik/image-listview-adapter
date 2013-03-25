image-listview-adapter

----------------------


Listview Adapter for android


Example : 
---------

Setting List Adapter :
----------------------

    ListView topicListView = (ListView) findViewById(R.id.topicListView);
    topicListView.setAdapter(new TopicListAdapter(this, topicList));


Example List Adapter :
----------------------
<pre>
<code>

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.neemtec.android.library.ImageListAdapter;

import com.mobixx.floats.R;
import com.mobixx.floats.data.model.Topic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TopicListAdapter extends ImageListAdapter {

    private final static String TAG = "TopicListAdapter";

    private Context context;

    public TopicListAdapter(Context context, List<Object> listItems) {
        super(context, listItems);
        this.context = context;

        setResource(R.layout.topic_list_adapter);
        setCacheDir("/mnt/sdcard/cache");
        setCacheMethod(CacheMethod.DISK);
        setDefaultIconResource(R.drawable.ic_launcher);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(getResource(), null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.topicImage);
            holder.title = (TextView) convertView.findViewById(R.id.topicTitle);
            holder.channel = (TextView) convertView.findViewById(R.id.topicChannelName);
            holder.startTime = (TextView) convertView.findViewById(R.id.topicStartTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Topic topic = (Topic) listItems.get(position);

        loadImage(holder.icon, topic.getImageUrl(), position);
        holder.title.setText(topic.getTitle());
        holder.channel.setText(topic.getChannel());

        try {
            Date topicSTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(topic.getStartTime());
            DateFormat timeformat = new SimpleDateFormat("HH:mm");
            String sTime = timeformat.format(topicSTime);
            Date topicETime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(topic.getEndTime());
            String eTime = timeformat.format(topicETime);
            holder.startTime.setText(sTime + " - " + eTime);

        } catch (Exception e) {
            Log.e("ERROR : ", e.toString());
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView channel;
        TextView startTime;
    }
}
</code>
</pre>