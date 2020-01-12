package com.salim3dd.mpsoundloc;

/**
 * Created by Salim3DD on 27/12/2016.
 */

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class SoundActivity extends AppCompatActivity {

    ArrayList<listitem> listitems = new ArrayList<>();

    int[] Photos = {R.drawable.bear, R.drawable.camel, R.drawable.cow, R.drawable.crow, R.drawable.dog, R.drawable.eagle, R.drawable.owl, R.drawable.raccoon, R.drawable.wolf, R.drawable.tiger};
    String[] Titles = {"دب", "جمل", "بقرة", "غراب", "كلب", "نسر", "بومة", "راكون", "ذئب", "نمر"};
    int[] MP3Sounds = {R.raw.bear, R.raw.camel, R.raw.cow, R.raw.crow, R.raw.dog, R.raw.eagle, R.raw.owl, R.raw.raccoon, R.raw.wolf, R.raw.tiger};

    ListView listView;
    MediaPlayer sound = new MediaPlayer();
    Button btn_play, btn_pause, btn_stop, btn_repeat, btn_playlist;
    TextView tvTitle, tvCurrentTime, tvTotalTime;
    // newwwwwww
    int currentSoundIndex = 0;           // to define the index of selected sound in playlist(ArrayList)
    int soundsCount = 0;
    int first_sound = 0;
    View list_view;
    private SeekBar seekBar;
    private Timer timer;
    private ArrayList<Integer> playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound);

        listView = (ListView) findViewById(R.id.listView2);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);


        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCurrentTime = (TextView) findViewById(R.id.tvCurrentTime);
        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);


        for (int i = 0; i < Photos.length; i++) {
            listitems.add(new listitem(Titles[i], Photos[i], MP3Sounds[i]));
        }

        ///                  additions
        btn_playlist = (Button) findViewById(R.id.btn_palylist);
        btn_repeat = (Button) findViewById(R.id.btn_repeat);
        playlist = new ArrayList();
        timer = new Timer();
        soundsCount = MP3Sounds.length;

        listAdapter listAdapter = new listAdapter(listitems);
        listView.setAdapter(listAdapter);
        sound = MediaPlayer.create(SoundActivity.this, MP3Sounds[0]);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    list_view = view;
                    view.setSelected(true);
                    sound.stop();
                    sound.reset();
                    sound = MediaPlayer.create(SoundActivity.this, listitems.get(i).sound);
                    tvTitle.setText(listitems.get(i).getTitle());
                    soundTime();
                    currentSoundIndex = 0;
                    first_sound = 0;
                    playlist.clear();
                    timer.cancel();
                    timer = new Timer();
                    for (int pos = i; pos < soundsCount; pos++) {
                        playlist.add(pos);
                    }
                } catch (Exception e) {
                }

            }

        });


        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sound.isPlaying()) {
                    start();
                }
                if (playlist.isEmpty()) {
                    Toast.makeText(SoundActivity.this, "please select sound", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sound.stop();
                    sound.reset();
                    timer.cancel();
                    timer.purge();
                    playlist.clear();
                    list_view.setSelected(false);

                } catch (Exception e) {
                }


            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sound.pause();
                    timer.cancel();

                    playlist.clear();
                    list_view.setSelected(false);

                } catch (Exception e) {
                }
            }
        });


        /**
         * when press repeat button will start sound palying repeat
         */
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sound.isPlaying()) {
                    sound.setLooping(true);      // only the addition for your app to looping about defined sound
                    start();                    // enclose your playing sound code to method to economy your code
                }
            }
        });


        // when press playlist button will looping on sound in playlist
        btn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!sound.isPlaying()) {
                        if (playlist.size() > 0) {
                            playNext();
                        } else {
                            Toast.makeText(SoundActivity.this, "select start item", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                }
            }

        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    if (b) sound.seekTo(i);
                    soundTime();
                } catch (Exception e) {
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    protected void playNext() {
        try {
            if (currentSoundIndex > 0) {
                first_sound = sound.getDuration() + 100;
            }
            timer.schedule(new TimerTask() {
                public void run() {
                    sound.reset();
                    sound = MediaPlayer.create(SoundActivity.this, listitems.get(playlist.get(currentSoundIndex)).sound);
                    soundTimeForNext();
                    currentSoundIndex++;
                    start();
                    if (playlist.size() > currentSoundIndex) {
                        playNext();

                    }
                }
            }, first_sound);
        } catch (Exception e) {
        }
    }


    private void start() {
        try {
            Thread updateSeekBar;
            updateSeekBar = new Thread() {
                @Override
                public void run() {
                    int SoundDuration = sound.getDuration();
                    int currentPostion = sound.getCurrentPosition();
                    seekBar.setMax(SoundDuration);
                    while (currentPostion < SoundDuration) {
                        try {
                            sleep(100);
                            currentPostion = sound.getCurrentPosition();
                            seekBar.setProgress(currentPostion);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        }
                    }
                }
            };
            sound.start();
            updateSeekBar.start();
        } catch (Exception e) {
        }
    }

    private void soundTimeForNext() {     // methods not start with capital letter
        try {
            seekBar.setMax(sound.getDuration());
            int tim = (seekBar.getMax() / 1000);
            int m = tim / 60;
            int s = tim % 60;


            int tim0 = (seekBar.getProgress() / 1000);
            int m0 = tim0 / 60;
            int s0 = tim0 % 60;
            tvTitle.setText(listitems.get(playlist.get(currentSoundIndex)).getTitle());
            tvTotalTime.setText(s + " : " + m);
            tvCurrentTime.setText(s0 + " : " + m0);
        } catch (Exception e) {

        }
    }

    private void soundTime() {     // methods not start with capital letter
        try {
            seekBar.setMax(sound.getDuration());
            int tim = (seekBar.getMax() / 1000);
            int m = tim / 60;
            int s = tim % 60;


            int tim0 = (seekBar.getProgress() / 1000);
            int m0 = tim0 / 60;
            int s0 = tim0 % 60;

            tvTotalTime.setText(s + " : " + m);
            tvCurrentTime.setText(s0 + " : " + m0);
        } catch (Exception e) {

        }
    }


    class listAdapter extends BaseAdapter {

        ArrayList<listitem> lis = new ArrayList<>();

        public listAdapter(ArrayList<listitem> lis) {
            this.lis = lis;
        }

        @Override
        public int getCount() {
            return lis.size();
        }

        @Override
        public Object getItem(int position) {
            return lis.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.row_itme, null);
            final ImageView img = (ImageView) view.findViewById(R.id.imageView);
            TextView title = (TextView) view.findViewById(R.id.textView_title);

            title.setText(lis.get(i).getTitle());
            Picasso.with(SoundActivity.this).load(lis.get(i).getImg()).into(img);

            return view;
        }
    }

}
