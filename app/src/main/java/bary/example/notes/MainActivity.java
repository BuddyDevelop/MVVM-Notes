package bary.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_NOTE_REQUEST = 1;
    private static final int EDIT_NOTE_REQUEST = 2;
    public static final String ADD_NOTE = "bary.example.notes.ADD_NOTE";
    public static final String EDIT_NOTE = "bary.example.notes.EDIT_NOTE";

    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //show toolbar in activity
        Toolbar toolbar = findViewById( R.id.main_toolbar );
        setSupportActionBar( toolbar );

        //put recyclerview in activity
        RecyclerView recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setHasFixedSize( true );

        //floating button on click listener
        FloatingActionButton floatingActionButton = findViewById( R.id.btn_add_note );
        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent( MainActivity.this, AddEditNoteActivity.class );
                startActivityForResult( intent, ADD_NOTE_REQUEST );
            }
        } );

        //set adapter and put to recyclerview
        final NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter( noteAdapter );

        //ViewModel of notes which observes notes
        mNoteViewModel = ViewModelProviders.of( this ).get( NoteViewModel.class );
        mNoteViewModel.getAllNotes().observe( this, new Observer<List<Note>>() {
            @Override
            public void onChanged( List<Note> notes ) {
                //update RecyclerView
                noteAdapter.submitList( notes );
            }
        } );

        //actions on swiping or dragNdrop item
        new ItemTouchHelper( new ItemTouchHelper.SimpleCallback( 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove( @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target ) {
                return false;
            }

            @Override
            public void onSwiped( @NonNull RecyclerView.ViewHolder viewHolder, int direction ) {
                mNoteViewModel.delete( noteAdapter.getNote( viewHolder.getAdapterPosition() ) );
                Toast.makeText( MainActivity.this, "Note delete", Toast.LENGTH_SHORT ).show();
            }
        } ).attachToRecyclerView( recyclerView );

        //edit note on click item
        noteAdapter.setOnItemClickListener( new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick( Note note ) {
                Intent intent = new Intent( MainActivity.this, AddEditNoteActivity.class );
                intent.putExtra( AddEditNoteActivity.EXTRA_TITLE, note.getTitle() );
                intent.putExtra( AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription() );
                intent.putExtra( AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority() );
                intent.putExtra( AddEditNoteActivity.EXTRA_ID, note.getId() );

                startActivityForResult( intent, EDIT_NOTE_REQUEST );
            }
        } );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );

        //save btn clicked with proper data
        if ( requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK ) {
            String title = data.getStringExtra( AddEditNoteActivity.EXTRA_TITLE );
            String desc = data.getStringExtra( AddEditNoteActivity.EXTRA_DESCRIPTION );
            int priority = data.getIntExtra( AddEditNoteActivity.EXTRA_PRIORITY, 1 );

            Note note = new Note( title, desc, priority );
            mNoteViewModel.insert( note );
            Toast.makeText( this, "Note saved", Toast.LENGTH_SHORT ).show();
        } else if ( requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK ) {
            int id = data.getIntExtra( AddEditNoteActivity.EXTRA_ID, -1 );

            if ( id == -1 ) {
                Toast.makeText( this, "Note cannot be updated", Toast.LENGTH_SHORT ).show();
                return;
            }

            String title = data.getStringExtra( AddEditNoteActivity.EXTRA_TITLE );
            String desc = data.getStringExtra( AddEditNoteActivity.EXTRA_DESCRIPTION );
            int priority = data.getIntExtra( AddEditNoteActivity.EXTRA_PRIORITY, 1 );


            Note note = new Note( title, desc, priority );
            note.setId( id );
            mNoteViewModel.update( note );
            Toast.makeText( this, "Note updated", Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( this, "Note not saved", Toast.LENGTH_SHORT ).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.delete_all_notes: {
                mNoteViewModel.deleteAllNotes();
                Toast.makeText( this, "All notes deleted", Toast.LENGTH_SHORT ).show();
                return true;
            }
            default: {
                return super.onOptionsItemSelected( item );
            }
        }
    }
}
