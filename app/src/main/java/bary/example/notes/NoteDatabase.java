package bary.example.notes;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database( entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase mInstance;
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance( Context context ){
        if( mInstance == null ){
            mInstance = Room.databaseBuilder( context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback( roomCallback )
                    .build();
        }

        return mInstance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate( @NonNull SupportSQLiteDatabase db ) {
            super.onCreate( db );
            new PopulateDbAsyncTask( mInstance ).execute(  );
        }

        @Override
        public void onOpen( @NonNull SupportSQLiteDatabase db ) {
            super.onOpen( db );
//            new PopulateDbAsyncTask( mInstance ).execute(  );
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao mNoteDao;

        private PopulateDbAsyncTask( NoteDatabase db ){
            mNoteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground( Void... voids ) {
            mNoteDao.insert( new Note( "Trash", "Throw the trash", 5 ) );
            mNoteDao.insert( new Note( "Halloween", "Buy paints for halloween and rent a costume!", 10 ) );
            mNoteDao.insert( new Note( "Tickets for train", "Buy tickets for train", 4 ) );
            mNoteDao.insert( new Note( "Presents", "Make list of presents for your dudes", 6 ) );


            return null;
        }
    }
}
