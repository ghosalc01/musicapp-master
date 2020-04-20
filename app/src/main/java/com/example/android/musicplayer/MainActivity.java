package com.example.android.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.R.drawable.ic_media_play;

public class MainActivity extends Activity {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP_VALUE = 5000;
    private static final int CALL = 872;
    static MediaPlayer mp = null;
    private final Handler handler = new Handler();
    private TextView selectedFile;
    private TextView selectedArtist;
    private RecyclerView recyclerView;
    private SeekBar seekbar;
    private MediaPlayer player;
    private ImageButton playButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private ImageButton repeat_oneButton;
    private ImageButton shuffleButton;
    private boolean isStarted = true;
    private int currentPosition;
    private boolean isMovingSeekBar = false;
    private MyRecyclerViewAdapter adapter;
    private final Runnable updatePositionRunnable = new Runnable() {
        public void run() {
            updatePosition();
        }
    };
    private List<Music> musicList;

    private boolean isShuffle = false;
    private View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.play:
                    if (player.isPlaying()) {
                        handler.removeCallbacks(updatePositionRunnable);
                        player.pause();
                        playButton.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        if (isStarted) {
                            player.start();
                            playButton.setImageResource(android.R.drawable.ic_media_pause);

                            updatePosition();
                        } else {
                            startPlay(currentPosition);
                        }
                    }

                    break;

                case R.id.prev:
                    startPlay(currentPosition - 1);


                    break;


                case R.id.next:
                    startPlay(currentPosition + 1);

                    break;


                case R.id.shuffle:
                    if (isShuffle) {
                        Collections.shuffle(musicList);
                        adapter.notifyDataSetChanged();
                        shuffleButton.setColorFilter(-16776961);
                    } else {
                        sortMusicList();
                        shuffleButton.setColorFilter(-16777216);
                    }

                    isShuffle = !isShuffle;
                    break;

                case R.id.repeat_one: {
                    player.setLooping(true);
                    repeat_oneButton.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                    repeat_oneButton.setColorFilter(-16776961);

                }

            }

        }
    };

    private void sortMusicList() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music o1, Music o2) {
                return o1.getArtists().compareTo(o2.getArtists());
            }
        });

        if (adapter != null) {
            adapter.notifyDataSetChanged();

        }
    }

    private MediaPlayer.OnCompletionListener onCompletion = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            stopPlay();
            startPlay(currentPosition + 1);
        }

    };
    private MediaPlayer.OnErrorListener onError = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            return false;
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isMovingSeekBar = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isMovingSeekBar) {
                player.seekTo(progress);

                Log.i("OnSeekBarChangeListener", "onProgressChanged");
            } else {
                TextView duration = findViewById(R.id.song_duration);
                duration.setText(String.valueOf(progress));
                long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(progress);
                long minss = totalSeconds / 60;
                long seconds = totalSeconds % 60;

                duration.setText(String.format(Locale.UK, "%02d:%02d", minss, seconds));
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);


        if (permissionCheck == PackageManager.PERMISSION_GRANTED) setupUI();
        else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CALL);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.getCurrentPosition();
    }


    private void setupUI() {
        selectedFile = findViewById(R.id.selected_file);
        selectedArtist = findViewById(R.id.artists);
        seekbar = findViewById(R.id.seekbar);
        prevButton = findViewById(R.id.prev);
        playButton = findViewById(R.id.play);
        nextButton = findViewById(R.id.next);
        repeat_oneButton = findViewById(R.id.repeat_one);
        shuffleButton = findViewById(R.id.shuffle);


        player = new MediaPlayer();

        player.setOnCompletionListener(onCompletion);

        player.setOnErrorListener(onError);

        seekbar.setOnSeekBarChangeListener(seekBarChanged);


        musicList = new MusicManager().getMusic(getContentResolver());
        sortMusicList();

        adapter = new MyRecyclerViewAdapter(musicList);
        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startPlay(position);
            }
        });
        recyclerView.setAdapter(adapter);

        prevButton.setOnClickListener(onButtonClick);
        playButton.setOnClickListener(onButtonClick);
        nextButton.setOnClickListener(onButtonClick);
        shuffleButton.setOnClickListener(onButtonClick);
        repeat_oneButton.setOnClickListener(onButtonClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeCallbacks(updatePositionRunnable);
        player.stop();
        player.reset();
        player.release();

        player = null;
    }

    private void startPlay(int position) {
        if (position < 0) {
            position = 0;
        }
        if (position >= musicList.size()) {
            position = musicList.size() - 1;
        }
        String file = musicList.get(position).getFile();
        String title = musicList.get(position).getTitle();
        String artists = musicList.get(position).getArtists();

        currentPosition = position;
        if (artists != null) {
            selectedArtist.setText(artists);
        }
        if (title != null) {
            selectedFile.setText(title);
        }


        seekbar.setProgress(0);
        updatePosition();

        player.stop();
        player.reset();

        try {
            player.setDataSource(file);
            player.prepare();
            player.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekbar.setMax(player.getDuration());
        playButton.setImageResource(android.R.drawable.ic_media_pause);


        isStarted = true;
    }

    private void stopPlay() {
        player.stop();
        player.reset();
        playButton.setImageResource(ic_media_play);
        handler.removeCallbacks(updatePositionRunnable);

        isStarted = false;
    }

    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);

        seekbar.setProgress(player.getCurrentPosition());

        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);

        if (adapter == null) {
            return;
        }

    }


}

