package com.example.android.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sky on 19/07/2017.
 */

public class MusicManager {

    public List<Music> getMusic(ContentResolver contentResolver) {

        List<Music> returnedList = new LinkedList<>();

        final Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                String file = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                long durationInMs = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
                Music music = new Music(title, file, name, artist,durationInMs);
                returnedList.add(music);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return returnedList;
    }
}
